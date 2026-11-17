"""Sửa mục lục ngay trên bản Word hiện tại, giữ nguyên nội dung và ảnh.

Chạy từ thư mục dự án: python BaoCao_PTTKHDoiTuong/nguon/sua_muc_luc.py
Các bản trước khi sửa được lưu tại target/report-build/toc-fix/backup.
"""
from copy import deepcopy
from io import BytesIO
from pathlib import Path
import re
import shutil
from zipfile import ZipFile

import fitz
from lxml import etree as ET
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas
from reportlab.lib.styles import ParagraphStyle
from reportlab.platypus import Paragraph
from xml.sax.saxutils import escape

ROOT = Path(__file__).resolve().parents[2]
FILES = [ROOT / 'BTL.docx', ROOT / 'BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx']
NS = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
W = '{' + NS['w'] + '}'


def elem(tag, **attrs):
    return ET.Element(W + tag, {W + key: str(value) for key, value in attrs.items()})


def child(parent, tag, **attrs):
    node = parent.find(W + tag)
    if node is None:
        node = elem(tag)
        parent.append(node)
    node.attrib.update({W + key: str(value) for key, value in attrs.items()})
    return node


def text(p):
    return ''.join(p.xpath('.//w:t/text()', namespaces=NS))


def normalized(value):
    return re.sub(r'\s+', '', value).casefold()


def run(parent, value=None, bold=False, size=21):
    r = ET.SubElement(parent, W + 'r')
    rp = ET.SubElement(r, W + 'rPr')
    child(rp, 'rFonts', ascii='Times New Roman', hAnsi='Times New Roman', eastAsia='Times New Roman', cs='Times New Roman')
    child(rp, 'sz', val=size)
    child(rp, 'szCs', val=size)
    if bold:
        child(rp, 'b')
    if value is not None:
        ET.SubElement(r, W + 't').text = value
    return r


def field(parent, instruction, cached='', size=21):
    run(parent, size=size).append(elem('fldChar', fldCharType='begin'))
    ins = ET.SubElement(run(parent, size=size), W + 'instrText')
    ins.set('{http://www.w3.org/XML/1998/namespace}space', 'preserve')
    ins.text = ' ' + instruction + ' '
    run(parent, size=size).append(elem('fldChar', fldCharType='separate'))
    run(parent, cached, size=size)
    run(parent, size=size).append(elem('fldChar', fldCharType='end'))


def format_entry(p, level, width, size, line, after):
    pp = child(p, 'pPr')
    pp.insert(0, elem('pStyle', val='TOC' + str(level)))
    child(pp, 'jc', val='left')
    child(pp, 'ind', left=0 if level == 1 else 180, firstLine=0, right=0)
    child(pp, 'spacing', before=0, after=after, line=line, lineRule='exact')
    child(pp, 'keepNext', val=0)
    child(pp, 'keepLines')
    child(pp, 'widowControl', val=0)
    tabs = child(pp, 'tabs')
    tabs.clear()
    tabs.append(elem('tab', val='right', leader='dot', pos=width))


def update_pdf(path, headings, figures, source_pdf=None):
    """Đồng bộ riêng trang tra cứu; giữ nguyên các trang nội dung đã có."""
    pdf_path = path.with_suffix('.pdf')
    original_pdf = source_pdf or ROOT / 'target/report-build/toc-fix/backup' / pdf_path.name
    pdf = fitz.open(original_pdf)
    font_dir = Path('/System/Library/Fonts/Supplemental')
    for alias, filename in [('NHHS-TNR', 'Times New Roman.ttf'), ('NHHS-TNR-Bold', 'Times New Roman Bold.ttf')]:
        pdfmetrics.registerFont(TTFont(alias, str(font_dir / filename)))
    is_pttk = path.name != 'BTL.docx'
    checks = []
    jobs = [(2 if is_pttk else 1, headings, 10.5 if is_pttk else 11.5, 12.5 if is_pttk else 14.5, 0 if is_pttk else 3.5)]
    if figures:
        jobs.append((3, figures, 10.5, 12.5, 1))
    for page_index, entries, size, leading, after in jobs:
        page = pdf[page_index]
        title = 'DANH MỤC HÌNH VÀ THUẬT NGỮ' if page_index == 3 else 'MỤC LỤC'
        title_rect = page.search_for(title)[0]
        top = title_rect.y1 + 10
        if page_index == 3:
            bottom = page.search_for('Các thuật ngữ sử dụng')[0].y0 - 3
        else:
            bottom = 770
        left, right = 85.05, 538.58
        page.add_redact_annot(fitz.Rect(left - 1, top - 2, 545, bottom), fill=(1, 1, 1))
        page.apply_redactions(images=0, graphics=0)
        for link in page.get_links():
            if link['from'].intersects(fitz.Rect(left, top, 545, bottom)):
                page.delete_link(link)
        buffer = BytesIO()
        c = canvas.Canvas(buffer, pagesize=(page.rect.width, page.rect.height))
        y = top
        links = []
        for label, target, level, number in entries:
            font = 'NHHS-TNR-Bold' if level == 1 else 'NHHS-TNR'
            x = left + (9 if level == 2 and page_index != 3 else 0)
            paragraph = Paragraph(escape(label), ParagraphStyle('toc', fontName=font, fontSize=size, leading=leading))
            available = right - x - 24
            _, height = paragraph.wrap(available, 800)
            if y + height > bottom:
                raise RuntimeError(f'Mục lục {pdf_path.name} vượt khung: {y + height:.1f} > {bottom:.1f}')
            paragraph.drawOn(c, x, page.rect.height - y - height)
            baseline = page.rect.height - y - height + leading - size
            c.setFont(font, size)
            c.drawRightString(right, baseline, str(number))
            # Dấu dẫn bắt đầu sau dòng cuối của tiêu đề, chừa khoảng quanh số.
            last_line = paragraph.blPara.lines[-1]
            text_end = x + available - last_line[0]
            stop = right - pdfmetrics.stringWidth(str(number), font, size) - 5
            start = text_end + 5
            c.setFont('NHHS-TNR', size)
            dot_width = pdfmetrics.stringWidth('.', 'NHHS-TNR', size)
            if stop > start:
                c.drawString(start, baseline, '.' * int((stop - start) / dot_width))
            links.append((fitz.Rect(left, y, right, y + height), number))
            y += height + after
        c.save()
        overlay = fitz.open(stream=buffer.getvalue(), filetype='pdf')
        page.show_pdf_page(page.rect, overlay, 0)
        for rect, number in links:
            page.insert_link({'kind': fitz.LINK_GOTO, 'from': rect, 'page': number - 1, 'to': fitz.Point(0, 55)})
        checks.append({'page': page_index + 1, 'entries': len(entries), 'bottom': round(y, 1), 'links': len(links)})
    tmp = pdf_path.with_suffix('.fixing.pdf')
    pdf.save(tmp, garbage=4, deflate=True)
    pdf.close()
    tmp.replace(pdf_path)
    return checks


def rewrite(path):
    backup = ROOT / 'target/report-build/toc-fix/backup'
    backup.mkdir(parents=True, exist_ok=True)
    for source in [path, path.with_suffix('.pdf')]:
        dest = backup / source.name
        if not dest.exists():
            shutil.copy2(source, dest)
    with ZipFile(path) as archive:
        parts = {info.filename: (info, archive.read(info.filename)) for info in archive.infolist()}
    doc = ET.fromstring(parts['word/document.xml'][1])
    if doc.xpath('//w:hyperlink[starts-with(@w:anchor,"NHHS_FIG_")]', namespaces=NS):
        raise RuntimeError('Báo cáo đã dùng số trang văn bản. Dùng hoan_thien_bao_cao.py và xuất PDF mới; không dựng lại từ bản sao lưu cũ.')
    body = doc.find(W + 'body')
    paragraphs = body.findall(W + 'p')
    start = next(p for p in paragraphs if text(p) == 'MỤC LỤC')
    body_start = next(p for p in paragraphs if text(p).startswith('CHƯƠNG 1.') and p.find('w:pPr/w:pStyle', NS) is not None and p.find('w:pPr/w:pStyle', NS).get(W + 'val').replace(' ', '').lower() == 'heading1')
    is_pttk = path.name != 'BTL.docx'
    end = next(p for p in paragraphs if text(p) == 'DANH MỤC HÌNH VÀ THUẬT NGỮ') if is_pttk else body_start
    # Keep the original page/section break following the TOC.
    old = list(body)[body.index(start) + 1:body.index(end)]
    boundary = next((p for p in reversed(old) if p.xpath('.//w:br[@w:type="page"] | ./w:pPr/w:sectPr', namespaces=NS)), None)
    for p in old:
        if p is not boundary:
            body.remove(p)
    width = 9071
    size, line, after = (21, 250, 0) if is_pttk else (23, 290, 70)
    styles = ET.fromstring(parts['word/styles.xml'][1])
    for level in [1, 2]:
        style = next((s for s in styles if s.get(W + 'styleId') == 'TOC' + str(level)), None)
        if style is None:
            style = elem('style', type='paragraph', styleId='TOC' + str(level))
            styles.append(style)
        style.clear()
        style.attrib.update({W + 'type': 'paragraph', W + 'styleId': 'TOC' + str(level)})
        style.append(elem('name', val='toc ' + str(level)))
        style.append(elem('basedOn', val='Normal'))
        holder = elem('p')
        format_entry(holder, level, width, size, line, after)
        pp = holder.find(W + 'pPr')
        pp.remove(pp.find(W + 'pStyle'))
        style.append(deepcopy(pp))
        holder = elem('p')
        run(holder, bold=level == 1, size=size)
        style.append(deepcopy(holder.find('w:r/w:rPr', NS)))
    # Scope Word's automatic TOC to the report body, excluding its own heading.
    for node in list(doc.xpath('//w:bookmarkStart[starts-with(@w:name,"NHHS_")]', namespaces=NS)):
        bid = node.get(W + 'id')
        node.getparent().remove(node)
        for last in doc.xpath('//w:bookmarkEnd[@w:id=$id]', namespaces=NS, id=bid):
            last.getparent().remove(last)
    next_id = max([int(x) for x in doc.xpath('//w:bookmarkStart/@w:id', namespaces=NS)] + [0]) + 1
    body_start.insert(1, elem('bookmarkStart', id=next_id, name='NHHS_ReportBody'))
    paragraphs[-1].append(elem('bookmarkEnd', id=next_id))
    next_id += 1
    pdf = fitz.open(backup / path.with_suffix('.pdf').name)
    pdf_text = [normalized(p.get_text()) for p in pdf]
    headings = []
    for p in paragraphs[paragraphs.index(body_start):]:
        st = p.find('w:pPr/w:pStyle', NS)
        sid = '' if st is None else st.get(W + 'val').replace(' ', '').lower()
        if sid not in ['heading1', 'heading2']:
            continue
        label = text(p)
        level = 1 if sid == 'heading1' else 2
        if label.startswith(('KẾT LUẬN', 'TÀI LIỆU THAM KHẢO', 'PHỤ LỤC')):
            level = 1
            if sid == 'heading2':
                st.set(W + 'val', body_start.find('w:pPr/w:pStyle', NS).get(W + 'val'))
        target = 'NHHS_TOC_' + str(len(headings) + 1)
        p.insert(1, elem('bookmarkStart', id=next_id, name=target))
        p.append(elem('bookmarkEnd', id=next_id))
        next_id += 1
        pages = []
        for n, pdf_page in enumerate(pdf):
            if n + 1 <= (4 if is_pttk else 2):
                continue
            blocks = pdf_page.get_text('blocks')
            if any(normalized(''.join(b[4] for b in blocks[i:i + 3])).startswith(normalized(label))
                   for i in range(len(blocks))):
                pages.append(n + 1)
        if not pages:
            pages = [n + 1 for n, t in enumerate(pdf_text)
                     if n + 1 > (4 if is_pttk else 2) and normalized(label) in t]
        if not pages:
            raise RuntimeError(f'Không tìm được trang nội dung: {label}')
        number = pages[0]
        headings.append((label, target, level, number))
    cursor = body.index(start) + 1
    for i, (label, target, level, number) in enumerate(headings):
        p = elem('p')
        format_entry(p, level, width, size, line, after)
        if i == 0:
            run(p, size=size).append(elem('fldChar', fldCharType='begin'))
            ins = ET.SubElement(run(p, size=size), W + 'instrText')
            ins.text = ' TOC \\o "1-2" \\h \\z \\b NHHS_ReportBody '
            run(p, size=size).append(elem('fldChar', fldCharType='separate'))
        link = ET.SubElement(p, W + 'hyperlink', {W + 'anchor': target, W + 'history': '1'})
        run(link, label, bold=level == 1, size=size)
        run(link, size=size).append(elem('tab'))
        field(link, 'PAGEREF ' + target + ' \\h', str(number), size)
        if i == len(headings) - 1:
            run(p, size=size).append(elem('fldChar', fldCharType='end'))
        body.insert(cursor, p)
        cursor += 1
    # Apply the same visible font, tab alignment and spacing to the figure list.
    figures = []
    if is_pttk:
        in_figures = False
        for p in body.findall(W + 'p'):
            if text(p) == 'DANH MỤC HÌNH VÀ THUẬT NGỮ':
                in_figures = True
            elif in_figures and not p.xpath('.//w:instrText[contains(text(),"PAGEREF")]', namespaces=NS):
                in_figures = False
            elif in_figures:
                instruction = p.xpath('.//w:instrText/text()', namespaces=NS)[0]
                target = instruction.split()[1]
                label = ''.join(p.xpath('.//w:hyperlink//w:t/text()', namespaces=NS))
                number = int(re.search(r'page(\d+)', target).group(1))
                figures.append((label, target, 2, number))
                pp = child(p, 'pPr')
                child(pp, 'ind', left=0, firstLine=0, right=0)
                child(pp, 'spacing', before=0, after=20, line=250, lineRule='exact')
                for r in p.xpath('.//w:r', namespaces=NS):
                    rp = child(r, 'rPr')
                    child(rp, 'rFonts', ascii='Times New Roman', hAnsi='Times New Roman', eastAsia='Times New Roman', cs='Times New Roman')
                    child(rp, 'sz', val=21)
                tabs = child(pp, 'tabs')
                tabs.clear()
                tabs.append(elem('tab', val='right', leader='dot', pos=width))
    settings = ET.fromstring(parts['word/settings.xml'][1])
    child(settings, 'updateFields', val='true')
    for node in settings.findall(W + 'showFieldCodes'):
        settings.remove(node)
    replacements = {'word/document.xml': doc, 'word/styles.xml': styles, 'word/settings.xml': settings}
    temp = path.with_suffix('.fixing.docx')
    with ZipFile(temp, 'w') as archive:
        for name, (info, data) in parts.items():
            if name in replacements:
                data = ET.tostring(replacements[name], xml_declaration=True, encoding='UTF-8', standalone=True)
            archive.writestr(info, data)
    temp.replace(path)
    checks = update_pdf(path, headings, figures)
    print(path.relative_to(ROOT), ':', len(headings), 'mục; giữ nguyên', len(doc.xpath('//w:drawing', namespaces=NS)), 'hình')
    return {'file': str(path.relative_to(ROOT)), 'toc_entries': len(headings), 'pdf_lookup_pages': checks}


if __name__ == '__main__':
    import json
    results = [rewrite(filename) for filename in FILES]
    output = ROOT / 'BaoCao_PTTKHDoiTuong/nguon/kiem_tra_muc_luc.json'
    output.write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding='utf-8')
