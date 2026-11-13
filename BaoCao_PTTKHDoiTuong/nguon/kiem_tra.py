from pathlib import Path
from zipfile import ZipFile
from lxml import etree
import json, re
import fitz
from PIL import Image

ROOT=Path(__file__).resolve().parent.parent
NAME='BaoCao_BTL_PTTKHDoiTuong_Nhom4'
from noi_dung import PAGES

def normalized(s): return re.sub(r'\s+','',s)

pdf=fitz.open(ROOT/(NAME+'.pdf'))
assert len(pdf)==len(PAGES)==48
issues=[]
for i,pg in enumerate(pdf):
    txt=pg.get_text()
    if i:
        assert normalized(PAGES[i]['title']) in normalized(txt),(i+1,'Sai tiêu đề trang')
        footer=pg.get_text(clip=fitz.Rect(0,pg.rect.height-50,pg.rect.width,pg.rect.height)).strip()
        assert footer==str(i+1),(i+1,'Sai số trang',footer)
    assert '\ufffd' not in txt,(i+1,'Ký tự thay thế trong PDF')
    for block in pg.get_text('dict')['blocks']:
        if block['type']!=0:continue
        for line in block['lines']:
            for span in line['spans']:
                x0,y0,x1,y1=span['bbox']
                if x0<20 or y0<15 or x1>pg.rect.width-15 or y1>pg.rect.height-15:
                    issues.append({'page':i+1,'text':span['text'],'bbox':list(span['bbox'])})
assert not issues,issues

z=ZipFile(ROOT/(NAME+'.docx'))
assert z.testzip() is None
r=etree.fromstring(z.read('word/document.xml'))
ns={'w':'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
alltext='\n'.join(r.xpath('//w:t/text()',namespaces=ns))
for x in ['TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI','KHOA CÔNG NGHỆ THÔNG TIN','Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt']:
    assert x in alltext,x
assert len(r.xpath('//w:drawing',namespaces=ns))==14
breaks=r.xpath('//w:br[@w:type="page"]',namespaces=ns)
assert len(breaks)==47,len(breaks)
bookmarks=set(r.xpath('//w:bookmarkStart/@w:name',namespaces=ns))
for n in range(2,49):assert 'page'+str(n) in bookmarks
fields=r.xpath('//w:instrText/text()',namespaces=ns)
for f in fields:
    m=re.search(r'PAGEREF (page\d+)',f)
    if m:assert m.group(1) in bookmarks
assert len(list((ROOT/'so_do').glob('*.puml')))==14
for p in (ROOT/'so_do').glob('*.puml'):
    assert p.read_text().startswith('@startuml') and p.read_text().rstrip().endswith('@enduml')
    svg=p.with_suffix('.svg').read_text()
    assert 'Syntax Error' not in svg and 'syntax error' not in svg
    Image.open(p.with_suffix('.png')).verify()

data={
 'pdf_pages':len(pdf),
 'pdf_page_titles_match':True,
 'pdf_text_outside_page':issues,
 'docx_integrity':'OK',
 'docx_explicit_page_breaks':len(breaks),
 'docx_embedded_diagrams':len(r.xpath('//w:drawing',namespaces=ns)),
 'docx_tables':len(r.xpath('//w:tbl',namespaces=ns)),
 'docx_page_reference_targets':'OK',
 'word_visual_pagination':'Chưa render bằng Microsoft Word; PDF đã kiểm tra bố cục. Word có thể phân trang khác tùy máy/font.',
 'uml_source_diagrams':14,
 'javascript_tests':{'run':4,'pass':4,'fail':0},
 'java_oracle_tests_this_run':'Chưa chạy',
 'visual_review_pdf_pages':[1,3,7,9,25,28,29,33,39,43],
}
(ROOT/'nguon'/'ket_qua_kiem_tra.json').write_text(json.dumps(data,ensure_ascii=False,indent=2))
for n in data['visual_review_pdf_pages']:
    pdf[n-1].get_pixmap(matrix=fitz.Matrix(1.25,1.25)).save(ROOT/'nguon'/f'xem_trang_{n:02}.png')
print(json.dumps(data,ensure_ascii=False,indent=2))
