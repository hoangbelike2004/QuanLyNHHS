"""Kiểm tra mục lục, tham chiếu, bản PDF và việc giữ nguyên nội dung."""
from pathlib import Path
from zipfile import ZipFile
import json
import re

import fitz
from lxml import etree as ET

ROOT = Path(__file__).resolve().parents[2]
NS = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
W = '{' + NS['w'] + '}'
BACKUP = ROOT / 'target/report-build/toc-fix/backup'


def normalized(value):
    return re.sub(r'\s+', '', value).casefold()


def check_final_pttk(path, doc, archive, original):
    from hoan_thien_bao_cao import references
    audit = json.loads((path.parent / 'nguon/ra_soat_bao_cao.json').read_text())
    before_path = ROOT / 'target/report-build/toc-fix/before-final-review' / path.name
    before = ET.fromstring(ZipFile(before_path).read('word/document.xml'))
    edits = {item['before']: item['after'] for item in audit['editorial_changes']}
    def report_blocks(root, apply_edits=False):
        result = []
        for node in root.find(W + 'body'):
            if node.findall('w:hyperlink', NS):
                continue
            value = ''.join(node.xpath('.//w:t/text()', namespaces=NS))
            if apply_edits and value in edits:
                value = edits[value]
            if value is not None:
                result.append(value)
        return result
    assert report_blocks(doc) == report_blocks(before, True), 'Thay đổi nội dung ngoài danh sách đã rà soát'
    assert not doc.xpath('//w:instrText[contains(text(),"PAGEREF") or contains(text()," TOC ")]', namespaces=NS)
    pdf = fitz.open(path.with_suffix('.pdf'))
    entries = references(doc)
    assert len(entries) == 63
    marks = {b.get(W + 'name'): b.getparent() for b in doc.xpath('//w:bookmarkStart', namespaces=NS)}
    for p, label, target, number in entries:
        assert number and 0 < number <= len(pdf), (label, number)
        title = ''.join(marks[target].xpath('.//w:t/text()', namespaces=NS))
        blocks = pdf[number - 1].get_text('blocks')
        assert any(normalized(''.join(b[4] for b in blocks[i:i + 4])).startswith(normalized(title))
                   for i in range(len(blocks))), (target, number, title)
    lookup_pages = [i for i, p in enumerate(pdf) if p.search_for('MỤC LỤC') or p.search_for('DANH MỤC HÌNH VÀ THUẬT NGỮ')]
    links = [link for n in lookup_pages for link in pdf[n].get_links() if link.get('kind') == fitz.LINK_GOTO]
    assert len(links) == 63, len(links)
    for (_, label, _, number), link in zip(entries, links):
        assert link['page'] + 1 == number, ('Liên kết khác số hiển thị', label, number, link['page'] + 1)
    all_text = '\n'.join(page.get_text() for page in pdf)
    for error in ['Không tìm thấy nội dung', 'Error! Reference', 'Error! Bookmark', 'Cột đáp ứng là kết quả đọc mã nguồn']:
        assert error not in all_text, error
    for page in pdf:
        assert '\ufffd' not in page.get_text()
    before_pdf = fitz.open(before_path.with_suffix('.pdf'))
    for n, page in enumerate(pdf):
        for clip in [fitz.Rect(0, 0, 595, 50), fitz.Rect(0, 780, 595, 842)]:
            assert page.get_text(clip=clip) == before_pdf[n].get_text(clip=clip), ('Header/footer thay đổi', n + 1)
    assert len(doc.xpath('//w:drawing', namespaces=NS)) == len(before.xpath('//w:drawing', namespaces=NS)) == 14
    assert len(doc.xpath('//w:tbl', namespaces=NS)) == len(before.xpath('//w:tbl', namespaces=NS))
    return {
        'file': str(path.relative_to(ROOT)), 'pdf_pages': len(pdf), 'verified_page_references': 63,
        'page_numbers': 'Văn bản thường, không còn TOC/PAGEREF để trình đọc tính lại thành 0.',
        'lookup_links_verified': True, 'embedded_media_preserved': True, 'embedded_diagrams': 14,
        'editorial_changes_verified': len(audit['editorial_changes']),
        'other_report_content_preserved': True, 'word_rendered': audit.get('render_engine', 'Chưa ghi nhận trình xuất PDF'),
        'pdf_visual_review_pages': [n + 1 for n in lookup_pages],
        'additional_pdf_visual_review_pages': [7, 41, 48, 49],
    }


def check(relative):
    path = ROOT / relative
    original = ZipFile(BACKUP / path.name)
    archive = ZipFile(path)
    assert archive.testzip() is None
    doc = ET.fromstring(archive.read('word/document.xml'))
    old = ET.fromstring(original.read('word/document.xml'))
    paragraphs = doc.findall('w:body/w:p', NS)
    bookmarks = {node.get(W + 'name'): node for node in doc.xpath('//w:bookmarkStart', namespaces=NS)}
    ids = doc.xpath('//w:bookmarkStart/@w:id', namespaces=NS)
    assert len(ids) == len(set(ids)), 'Trùng ID bookmark'
    assert set(ids) == set(doc.xpath('//w:bookmarkEnd/@w:id', namespaces=NS)), 'Bookmark chưa đóng'
    for name in archive.namelist():
        if name.startswith('word/media/'):
            assert archive.read(name) == original.read(name), name
        if not name.startswith('word/') or not name.endswith('.xml'):
            continue
        root = ET.fromstring(archive.read(name))
        stack = []
        for node in root.iter():
            if node.tag == W + 'fldChar':
                kind = node.get(W + 'fldCharType')
                if kind == 'begin':
                    stack.append(False)
                elif kind == 'separate':
                    assert stack and not stack[-1], (name, 'Sai dấu separate')
                    stack[-1] = True
                elif kind == 'end':
                    assert stack, (name, 'Thừa dấu end')
                    stack.pop()
            if node.tag == W + 'hyperlink' and node.get(W + 'anchor'):
                assert node.get(W + 'anchor') in bookmarks, node.get(W + 'anchor')
        assert not stack, (name, 'Trường chưa đóng')
    if doc.xpath('//w:hyperlink[starts-with(@w:anchor,"NHHS_FIG_")]', namespaces=NS):
        return check_final_pttk(path, doc, archive, original)
    # The report body, including tables and the appended update, must keep its text.
    def body_text(root):
        body = root.find(W + 'body')
        start = next(i for i, p in enumerate(body) if p.find('w:pPr/w:pStyle', NS) is not None
                     and p.find('w:pPr/w:pStyle', NS).get(W + 'val').replace(' ', '').lower() == 'heading1'
                     and ''.join(p.xpath('.//w:t/text()', namespaces=NS)).startswith('CHƯƠNG 1.'))
        return [value for p in list(body)[start:] for value in p.xpath('.//w:t/text()', namespaces=NS)]
    assert body_text(doc) == body_text(old), 'Nội dung chính bị thay đổi'
    pdf = fitz.open(path.with_suffix('.pdf'))
    old_pdf = fitz.open(BACKUP / path.with_suffix('.pdf').name)
    assert len(pdf) == len(old_pdf)
    lookup_pages = [1] if path.name == 'BTL.docx' else [2, 3]
    for n in range(len(pdf)):
        if n not in lookup_pages:
            assert pdf[n].get_text() == old_pdf[n].get_text(), ('Trang nội dung bị thay đổi', n + 1)
    references = 0
    for p in paragraphs:
        instructions = p.xpath('.//w:instrText/text()', namespaces=NS)
        refs = [ins for ins in instructions if ins.strip().startswith('PAGEREF')]
        if not refs:
            continue
        assert len(refs) == 1
        target = refs[0].split()[1]
        mark = bookmarks[target]
        heading = mark.getparent()
        title = ''.join(heading.xpath('.//w:t/text()', namespaces=NS))
        # Figure references point to the heading on the same page as the picture.
        numbers = [int(t) for t in p.xpath('.//w:t/text()', namespaces=NS) if t.isdigit()]
        assert len(numbers) == 1, (target, numbers)
        number = numbers[0]
        assert number > max(lookup_pages) + 1, ('Tham chiếu trỏ về chính mục lục', target, number)
        blocks = [normalized(block[4]) for block in pdf[number - 1].get_text('blocks')]
        assert any(''.join(blocks[i:i + 3]).startswith(normalized(title)) for i in range(len(blocks))), (target, number, title)
        references += 1
    expected = 23 if path.name == 'BTL.docx' else 49 + 14
    assert references == expected, (references, expected)
    for n, count in zip(lookup_pages, [23] if path.name == 'BTL.docx' else [49, 14]):
        links = [link for link in pdf[n].get_links() if link.get('kind') == fitz.LINK_GOTO]
        assert len(links) == count, (n + 1, len(links), count)
        assert all(0 <= link['page'] < len(pdf) for link in links)
        assert not any(error in pdf[n].get_text() for error in ['Error!', 'PAGEREF', 'Bookmark not defined', '\ufffd'])
        # Ensure headers and footers survive the replacement of lookup content.
        for clip in [fitz.Rect(0, 0, 595, 48), fitz.Rect(0, 780, 595, 842)]:
            assert pdf[n].get_text(clip=clip) == old_pdf[n].get_text(clip=clip)
        for a, b in zip(links, links[1:]):
            assert a['from'].y1 <= b['from'].y0 + .1, 'Dòng mục lục chồng nhau'
    result = {
        'file': relative, 'pdf_pages': len(pdf), 'verified_page_references': references,
        'body_text_preserved': True, 'embedded_media_preserved': True,
        'lookup_links_verified': True, 'fields_and_bookmarks': 'OK',
        'unchanged_pdf_body_pages': len(pdf) - len(lookup_pages),
        'pdf_visual_review_pages': [n + 1 for n in lookup_pages],
        'word_pagination': 'Chưa render lại toàn bộ Word trên máy hiện tại; số trang tham chiếu đối chiếu PDF hiện có.',
    }
    return result


if __name__ == '__main__':
    results = [check(name) for name in ['BTL.docx', 'BaoCao_PTTKHDoiTuong/BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx']]
    output = ROOT / 'BaoCao_PTTKHDoiTuong/nguon/kiem_tra_muc_luc.json'
    output.write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding='utf-8')
    print(json.dumps(results, ensure_ascii=False, indent=2))
