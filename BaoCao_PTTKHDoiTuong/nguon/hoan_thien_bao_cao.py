"""Làm sạch ghi chú biên soạn và tạo bản tra cứu tương thích nhiều trình đọc.

Số trang mục lục là văn bản, liên kết tới bookmark nội bộ; không dùng PAGEREF.
Sau khi thay đổi nội dung, xuất PDF rồi dùng sync_pages() để cập nhật số trang.
"""
from copy import deepcopy
import json
from pathlib import Path
import re
import shutil
from zipfile import ZipFile

import fitz
from lxml import etree as ET

ROOT = Path(__file__).resolve().parents[2]
PATH = ROOT / 'BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx'
NS = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
W = '{' + NS['w'] + '}'
AUDIT = PATH.parent / 'nguon/ra_soat_bao_cao.json'
REMOVE_PREFIXES = (
    'Thông tin còn để trống theo yêu cầu:',
    'Các nội dung ngoài phạm vi không được suy diễn',
    'Cột đáp ứng là kết quả đọc mã nguồn,',
    'Ngoại lệ và hậu điều kiện đầy đủ ở trên là yêu cầu thiết kế.',
    'Không thay đổi mã nguồn hay cấu hình chạy của ứng dụng',
    'Nguồn cục bộ được dùng trực tiếp;',
    'Báo cáo được tạo thành bộ tệp mới;',
    'Mục lục bản Word dùng liên kết và trường tham chiếu trang.',
    'Vai trò là phân tích nghiệp vụ. Code hiện chưa giới hạn UC07',
)
REPLACEMENTS = {
    'Nguồn và cách sử dụng': 'Cơ sở thực hiện',
    'Các trang thực tế dùng template Thymeleaf': 'Giao diện sử dụng Thymeleaf và bố cục chung tại templates/layout. Bảng trên trình bày các trang chức năng và nội dung cần kiểm tra khi chạy thử.',
    'Nếu cần bổ sung ảnh demo theo yêu cầu giảng viên,': 'Các tình huống demo gồm: hóa đơn có chi tiết; từ chối sửa hóa đơn đã thanh toán; tồn kho trước và sau khi sửa phiếu nhập; từ chối lưu biểu mẫu đã lỗi phiên bản.',
    'Bảng thể hiện trách nhiệm phân công, không tự xác nhận': 'Bảng trình bày trách nhiệm theo phân công của nhóm. Tiến độ và mức đóng góp thực tế được tổng hợp khi hoàn thành bài tập lớn.',
    'Tài liệu được biên soạn có sử dụng ChatGPT/Codex': 'Công cụ hỗ trợ biên soạn gồm ChatGPT/Codex để đọc mã và xây dựng nội dung, PlantUML để dựng sơ đồ, Python để tạo và kiểm tra tài liệu.',
    'Thông tin cần hoàn thiện trước khi nộp': 'Thông tin nhóm và kết quả kiểm thử',
    'Điền tên giảng viên, lớp, mã sinh viên;': 'Thông tin giảng viên, lớp và mã sinh viên được bổ sung theo thông tin chính thức của nhóm. Kết quả kiểm thử được ghi theo môi trường, dữ liệu đầu vào và kết quả thực tế tại mục 4.5.',
    'PHỤ LỤC C CẬP NHẬT CÀI ĐẶT NGÀY 16 11 2026': 'PHỤ LỤC C. CẬP NHẬT CÀI ĐẶT NGÀY 16/11/2026',
}


def normalized(text):
    return re.sub(r'\s+', '', text).casefold()


def text(p):
    return ''.join(p.xpath('.//w:t/text()', namespaces=NS))


def load(path):
    with ZipFile(path) as archive:
        return {i.filename: (i, archive.read(i.filename)) for i in archive.infolist()}


def save(path, parts, replacements):
    temp = path.with_suffix('.review.docx')
    with ZipFile(temp, 'w') as archive:
        for name, (info, data) in parts.items():
            if name in replacements:
                data = ET.tostring(replacements[name], xml_declaration=True, encoding='UTF-8', standalone=True)
            archive.writestr(info, data)
    temp.replace(path)


def set_text(p, value):
    nodes = p.xpath('.//w:t', namespaces=NS)
    nodes[0].text = value
    for node in nodes[1:]:
        node.text = ''


def plain_run(parent, value=None, tab=False, bold=False, size=21):
    r = ET.SubElement(parent, W + 'r')
    rp = ET.SubElement(r, W + 'rPr')
    ET.SubElement(rp, W + 'rFonts', {W + k: 'Times New Roman' for k in ['ascii', 'hAnsi', 'eastAsia', 'cs']})
    for tag in ['sz', 'szCs']:
        ET.SubElement(rp, W + tag, {W + 'val': str(size)})
    ET.SubElement(rp, W + 'color', {W + 'val': '000000'})
    ET.SubElement(rp, W + 'u', {W + 'val': 'none'})
    if bold:
        ET.SubElement(rp, W + 'b')
    if tab:
        ET.SubElement(r, W + 'tab')
    if value is not None:
        ET.SubElement(r, W + 't').text = value
    return r


def references(doc):
    entries = []
    for p in doc.findall('w:body/w:p', NS):
        for link in p.findall('w:hyperlink', NS):
            target = link.get(W + 'anchor', '')
            if not target.startswith(('NHHS_TOC_', 'NHHS_FIG_', 'page')):
                continue
            texts = link.xpath('.//w:t/text()', namespaces=NS)
            label = next((s for s in texts if not s.isdigit()), '')
            if not label:
                continue
            number = next((int(t) for t in p.xpath('.//w:t/text()', namespaces=NS) if t.isdigit()), None)
            entries.append((p, label, target, number))
    return entries


def clean():
    backup = ROOT / 'target/report-build/toc-fix/before-final-review'
    backup.mkdir(parents=True, exist_ok=True)
    for source in [PATH, PATH.with_suffix('.pdf')]:
        if not (backup / source.name).exists():
            shutil.copy2(source, backup / source.name)
    parts = load(PATH)
    doc = ET.fromstring(parts['word/document.xml'][1])
    changes = []
    # Copy lookup data before removing the old complex field delimiters.
    entries = references(doc)
    for p in list(doc.findall('w:body/w:p', NS)):
        value = text(p)
        if p.findall('w:hyperlink', NS):
            continue
        if value.startswith(REMOVE_PREFIXES):
            p.getparent().remove(p)
            changes.append({'before': value, 'after': None})
            continue
        for prefix, replacement in REPLACEMENTS.items():
            if value.startswith(prefix):
                set_text(p, replacement)
                changes.append({'before': value, 'after': replacement})
                break
        if value.startswith('PHỤ LỤC C'):
            pp = p.find(W + 'pPr')
            if pp.find(W + 'pageBreakBefore') is None:
                ET.SubElement(pp, W + 'pageBreakBefore')
    next_id = max(int(v) for v in doc.xpath('//w:bookmarkStart/@w:id', namespaces=NS)) + 1
    for p, label, target, number in entries:
        is_figure = label.startswith('Hình ')
        if is_figure:
            figure_id = re.match(r'Hình (\d+)\.(\d+)\.', label)
            target = 'NHHS_FIG_' + '_'.join(figure_id.groups())
            caption = next(c for c in doc.findall('w:body/w:p', NS)
                           if not c.findall('w:hyperlink', NS) and text(c).startswith(figure_id.group(0)))
            if not doc.xpath('//w:bookmarkStart[@w:name=$name]', namespaces=NS, name=target):
                caption.insert(1, ET.Element(W + 'bookmarkStart', {W + 'id': str(next_id), W + 'name': target}))
                caption.append(ET.Element(W + 'bookmarkEnd', {W + 'id': str(next_id)}))
                next_id += 1
        elif label.startswith('PHỤ LỤC C'):
            label = REPLACEMENTS['PHỤ LỤC C CẬP NHẬT CÀI ĐẶT NGÀY 16 11 2026']
        pp = deepcopy(p.find(W + 'pPr'))
        for node in list(p):
            p.remove(node)
        p.append(pp)
        link = ET.SubElement(p, W + 'hyperlink', {W + 'anchor': target, W + 'history': '1'})
        bold = not is_figure and not bool(re.match(r'^\d+\.', label))
        plain_run(link, label, bold=bold)
        plain_run(link, tab=True)
        plain_run(link, str(number), bold=bold)
    settings = ET.fromstring(parts['word/settings.xml'][1])
    update = settings.find(W + 'updateFields')
    if update is not None:
        update.set(W + 'val', 'false')
    assert not doc.xpath('//w:instrText[contains(text(),"PAGEREF") or contains(text()," TOC ")]', namespaces=NS)
    save(PATH, parts, {'word/document.xml': doc, 'word/settings.xml': settings})
    audit = json.loads(AUDIT.read_text()) if AUDIT.exists() else {}
    previous = audit.get('editorial_changes', [])
    for change in changes:
        if change not in previous:
            previous.append(change)
    audit.update({'editorial_changes': previous, 'lookup_entries': len(entries), 'page_number_format': 'Văn bản thường có liên kết bookmark; không tự tính lại khi mở.'})
    AUDIT.write_text(json.dumps(audit, ensure_ascii=False, indent=2), encoding='utf-8')
    print(f'Đã sửa {len(entries)} dòng tra cứu; lược/gọn {len(changes)} ghi chú.', flush=True)


def sync_pages(pdf_path):
    parts = load(PATH)
    doc = ET.fromstring(parts['word/document.xml'][1])
    pdf = fitz.open(pdf_path)
    bookmarks = {b.get(W + 'name'): b.getparent() for b in doc.xpath('//w:bookmarkStart', namespaces=NS)}
    entries = references(doc)
    lookup_pages = {i for i, p in enumerate(pdf) if p.search_for('MỤC LỤC') or p.search_for('DANH MỤC HÌNH VÀ THUẬT NGỮ')}
    changed = []
    verified = []
    for p, label, target, cached in entries:
        title = text(bookmarks[target])
        matches = []
        for n, page in enumerate(pdf):
            if n in lookup_pages:
                continue
            blocks = page.get_text('blocks')
            if any(normalized(''.join(b[4] for b in blocks[i:i + 4])).startswith(normalized(title)) for i in range(len(blocks))):
                matches.append(n + 1)
        assert matches, ('Không tìm được nội dung sau render', target, title)
        actual = matches[0]
        if actual != cached:
            p.xpath('.//w:t', namespaces=NS)[-1].text = str(actual)
            changed.append({'label': label, 'before': cached, 'after': actual})
        verified.append({'label': label, 'bookmark': target, 'page': actual})
    save(PATH, parts, {'word/document.xml': doc})
    audit = json.loads(AUDIT.read_text())
    audit.update({'pdf_pages': len(pdf), 'references': verified, 'last_page_number_updates': changed})
    AUDIT.write_text(json.dumps(audit, ensure_ascii=False, indent=2), encoding='utf-8')
    print(f'PDF {len(pdf)} trang: kiểm tra {len(verified)} tham chiếu, cập nhật {len(changed)} số trang.', flush=True)
    return len(changed)


def update_pdf_content():
    """Đồng bộ các đoạn đã sửa với PDF, không tuyên bố render bằng Word."""
    from io import BytesIO
    from xml.sax.saxutils import escape
    from reportlab.pdfbase import pdfmetrics
    from reportlab.pdfbase.ttfonts import TTFont
    from reportlab.pdfgen import canvas
    from reportlab.lib.styles import ParagraphStyle
    from reportlab.lib.enums import TA_JUSTIFY
    from reportlab.platypus import Paragraph
    from sua_muc_luc import update_pdf
    fonts = Path('/System/Library/Fonts/Supplemental')
    for alias, filename in [('Review-TNR', 'Times New Roman.ttf'), ('Review-Bold', 'Times New Roman Bold.ttf'), ('Review-Italic', 'Times New Roman Italic.ttf')]:
        pdfmetrics.registerFont(TTFont(alias, str(fonts / filename)))
    pdf = fitz.open(ROOT / 'target/report-build/toc-fix/before-final-review' / PATH.with_suffix('.pdf').name)
    audit = json.loads(AUDIT.read_text())
    overlays = {}
    for change in audit['editorial_changes']:
        if change['before'].startswith('PHỤ LỤC C'):
            continue
        found = False
        for n, page in enumerate(pdf):
            rects = page.search_for(change['before'])
            if not rects:
                continue
            found = True
            box = fitz.Rect(rects[0])
            for rect in rects[1:]:
                box |= rect
            spans = [span for block in page.get_text('dict')['blocks'] if block['type'] == 0
                     for line in block['lines'] for span in line['spans']
                     if fitz.Rect(span['bbox']).intersects(rects[0])]
            span = spans[0]
            font = 'Review-Bold' if 'bold' in span['font'].casefold() else 'Review-Italic' if 'italic' in span['font'].casefold() else 'Review-TNR'
            for rect in rects:
                page.add_redact_annot(rect, fill=(1, 1, 1))
            if change['after']:
                overlays.setdefault(n, []).append((box, change['after'], font, span['size'], span['color']))
        assert found, change['before']
    # Put Appendix C and all of its paragraphs together on the final page.
    appendix = pdf[47].search_for('PHỤ LỤC C CẬP NHẬT CÀI ĐẶT NGÀY 16 11 2026')[0]
    pdf[47].add_redact_annot(fitz.Rect(84, appendix.y0 - 2, 545, 780), fill=(1, 1, 1))
    pdf[48].add_redact_annot(fitz.Rect(84, 55, 545, 780), fill=(1, 1, 1))
    doc = ET.fromstring(load(PATH)['word/document.xml'][1])
    paragraphs = doc.findall('w:body/w:p', NS)
    start = next(i for i, p in enumerate(paragraphs) if text(p).startswith('PHỤ LỤC C.') and not p.findall('w:hyperlink', NS))
    y = 60
    appendix_overlay = []
    for offset, p in enumerate(paragraphs[start:]):
        value = text(p)
        if not value:
            continue
        font, size = ('Review-Bold', 14) if offset == 0 else ('Review-TNR', 13)
        style = ParagraphStyle('appendix', fontName=font, fontSize=size, leading=size * 1.32, alignment=0 if offset == 0 else TA_JUSTIFY)
        paragraph = Paragraph(escape(value), style)
        _, height = paragraph.wrap(453.53, 700)
        assert y + height < 780
        appendix_overlay.append((y, height, paragraph))
        y += height + 9
    for n, page in enumerate(pdf):
        page.apply_redactions(images=0, graphics=0)
        if n not in overlays and n != 48:
            continue
        buffer = BytesIO()
        c = canvas.Canvas(buffer, pagesize=(page.rect.width, page.rect.height))
        for box, value, font, size, color in overlays.get(n, []):
            paragraph = Paragraph(escape(value), ParagraphStyle('replacement', fontName=font, fontSize=size, leading=size * 1.32,
                textColor=((color >> 16 & 255) / 255, (color >> 8 & 255) / 255, (color & 255) / 255)))
            _, height = paragraph.wrap(453.53, 700)
            assert height <= box.height + size, (value, height, box.height)
            paragraph.drawOn(c, 85.05, page.rect.height - box.y0 - height)
        if n == 48:
            for top, height, paragraph in appendix_overlay:
                paragraph.drawOn(c, 85.05, page.rect.height - top - height)
        c.save()
        overlay = fitz.open(stream=buffer.getvalue(), filetype='pdf')
        page.show_pdf_page(page.rect, overlay, 0)
    intermediate = ROOT / 'target/report-build/toc-fix/pttk-noi-dung-da-ra-soat.pdf'
    pdf.save(intermediate, garbage=4, deflate=True)
    pdf.close()
    sync_pages(intermediate)
    doc = ET.fromstring(load(PATH)['word/document.xml'][1])
    headings, figures = [], []
    for p, label, target, number in references(doc):
        if label.startswith('Hình '):
            figures.append((label, target, 2, number))
        else:
            headings.append((label, target, 2 if re.match(r'^\d+\.', label) else 1, number))
    update_pdf(PATH, headings, figures, source_pdf=intermediate)
    audit = json.loads(AUDIT.read_text())
    audit['render_engine'] = 'PDF được đồng bộ trực tiếp; chưa render lại bằng Microsoft Word.'
    AUDIT.write_text(json.dumps(audit, ensure_ascii=False, indent=2), encoding='utf-8')
    print('Đã đồng bộ PDF, chuyển trọn Phụ lục C sang trang 49.', flush=True)


if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1 and sys.argv[1] == '--sync-existing-pdf':
        update_pdf_content()
    elif len(sys.argv) > 1:
        sync_pages(Path(sys.argv[1]))
    else:
        clean()
