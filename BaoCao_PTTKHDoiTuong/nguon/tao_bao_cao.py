from pathlib import Path
import json, re, sys
from xml.sax.saxutils import escape
from io import BytesIO

from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from reportlab.pdfgen import canvas
from reportlab.lib import colors
from reportlab.lib.enums import TA_JUSTIFY, TA_LEFT, TA_CENTER
from reportlab.lib.styles import ParagraphStyle
from reportlab.platypus import Paragraph, Table, TableStyle, Image, Spacer
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.utils import ImageReader
from PIL import Image as PILImage

from noi_dung import PAGES

ROOT = Path(__file__).resolve().parent.parent
NAME = 'BaoCao_BTL_PTTKHDoiTuong_Nhom4'
FONT_DIR = Path('/System/Library/Fonts/Supplemental')
for alias, filename in [('TNR','Times New Roman.ttf'),('TNR-Bold','Times New Roman Bold.ttf'),('TNR-Italic','Times New Roman Italic.ttf'),('TNR-BoldItalic','Times New Roman Bold Italic.ttf'),('ArialU','Arial.ttf')]:
    pdfmetrics.registerFont(TTFont(alias, str(FONT_DIR / filename)))
pdfmetrics.registerFontFamily('TNR',normal='TNR',bold='TNR-Bold',italic='TNR-Italic',boldItalic='TNR-BoldItalic')
CM=28.3464567
PW,PH=21*CM,29.7*CM
LEFT,RIGHT,TOP,BOTTOM=3*CM,2*CM,2.1*CM,2*CM
WIDTH=PW-LEFT-RIGHT
HEIGHT=PH-TOP-BOTTOM
BLUE=colors.HexColor('#193B55')
GRAY=colors.HexColor('#526271')

FIGURES=[]
for n, pg in enumerate(PAGES,1):
    for b in pg['blocks']:
        if b['kind']=='image': FIGURES.append((b['caption'].split(';')[0],n))

TOC=[]
for n, pg in enumerate(PAGES,1):
    t=pg['title']
    if n in [5,18,24,38]:
        ch={5:'CHƯƠNG 1. BÀI TOÁN VÀ PHÂN TÍCH YÊU CẦU',18:'CHƯƠNG 2. MÔ HÌNH PHÂN TÍCH',24:'CHƯƠNG 3. THIẾT KẾ HỆ THỐNG',38:'CHƯƠNG 4. CÀI ĐẶT VÀ KIỂM THỬ'}[n]
        TOC.append((ch,n,True))
    # Các mục cấp một được gộp chọn lọc để mục lục gọn trong một trang.
    if n in [5,6,7,8,9,10,11,17,18,19,20,21,22,23,24,25,28,30,32,33,35,36,37,38,39,41,42,43,45,46,47,48]:
        label=t
        if n==11: label='1.7–1.12. Đặc tả các ca sử dụng tiêu biểu'
        if n==25: label='3.2–3.4. Sequence Diagram cho ba nghiệp vụ'
        if n==28: label='3.5–3.6. DCD của hóa đơn và nhập kho'
        if n==30: label='3.7–3.8. State Machine của hóa đơn và đặt bàn'
        if n==33: label='3.10–3.11. ERD và ánh xạ dữ liệu'
        if n==39: label='4.2–4.3. Minh chứng nghiệp vụ và bảo mật'
        if n==43: label='4.6–4.7. Kế hoạch kiểm thử luồng chính, ngoại lệ'
        TOC.append((label,n,False))

def sty(size=13, leading=None, align=TA_JUSTIFY, bold=False, color=None):
    return ParagraphStyle('x',fontName='TNR-Bold' if bold else 'TNR',fontSize=size,
        leading=leading or size*1.32,alignment=align,textColor=color or colors.black,
        spaceAfter=7,allowWidows=0,allowOrphans=0)

def para(text,size=13,leading=None,bold=False,align=TA_JUSTIFY,color=None):
    return Paragraph(escape(text).replace('\n','<br/>'),sty(size,leading,align,bold,color))

def blocks_pdf(pg, scale=1):
    flow=[]
    blocks=pg['blocks']
    if pg.get('chapter'):
        flow += [para(blocks[0]['text'],15.5*scale,bold=True,align=TA_LEFT,color=BLUE),Spacer(1,10)]
        blocks=blocks[1:]
    if pg['title']!='TRANG BÌA':
        flow += [para(pg['title'],15.5*scale,bold=True,align=TA_LEFT,color=BLUE),Spacer(1,9)]
    for b in blocks:
        k=b['kind']
        if k in ['p','h','note']:
            size=(13 if k=='p' else 13 if k=='h' else 11.5)*scale
            flow += [para(b['text'],size,bold=k=='h',align=TA_LEFT if k=='h' else TA_JUSTIFY,color=GRAY if k=='note' else None),Spacer(1,5*scale)]
        elif k=='code':
            # Dùng font có tiếng Việt và font nhỏ vừa đủ để đoạn mã không tràn khung.
            txt=escape(b['text']).replace(' ','&#160;').replace('\n','<br/>')
            pp=Paragraph(txt,ParagraphStyle('code',fontName='ArialU',fontSize=9.5*scale,leading=13*scale))
            tt=Table([[pp]],colWidths=[WIDTH])
            tt.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,-1),colors.HexColor('#F3F5F7')),('BOX',(0,0),(-1,-1),.4,colors.HexColor('#D6DEE5')),('LEFTPADDING',(0,0),(-1,-1),9),('RIGHTPADDING',(0,0),(-1,-1),9),('TOPPADDING',(0,0),(-1,-1),8),('BOTTOMPADDING',(0,0),(-1,-1),8)]))
            flow += [tt,Spacer(1,8)]
        elif k=='table':
            n=len(b['head']); widths=b.get('widths') or [1/n]*n
            data=[[para(x,11.4*scale,bold=True,align=TA_LEFT,color=colors.white) for x in b['head']]]
            data += [[para(str(x),11.4*scale,align=TA_LEFT) for x in row] for row in b['rows']]
            tt=Table(data,colWidths=[x*WIDTH for x in widths],hAlign='LEFT')
            tt.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),BLUE),('ROWBACKGROUNDS',(0,1),(-1,-1),[colors.white,colors.HexColor('#F1F5F8')]),('GRID',(0,0),(-1,-1),.4,colors.HexColor('#CBD5DD')),('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),6),('RIGHTPADDING',(0,0),(-1,-1),6),('TOPPADDING',(0,0),(-1,-1),5*scale),('BOTTOMPADDING',(0,0),(-1,-1),5*scale)]))
            flow += [tt,Spacer(1,9*scale)]
        elif k=='image':
            path=ROOT/'so_do'/(b['name']+'.png')
            w,hh=PILImage.open(path).size
            factor=min(WIDTH/w,b['maxh']*CM/hh)
            im=Image(str(path),width=w*factor,height=hh*factor)
            im.hAlign='CENTER'
            flow += [im,Spacer(1,6),para(b['caption'],11*scale,align=TA_CENTER),Spacer(1,8)]
        elif k=='toc':
            rows=[]
            for label,num,bold in TOC:
                rows.append([para(label,10.7*scale,bold=bold,align=TA_LEFT,color=BLUE if bold else None),para(str(num),10.7*scale,align=TA_CENTER)])
            tt=Table(rows,colWidths=[WIDTH-25,25])
            tt.setStyle(TableStyle([('VALIGN',(0,0),(-1,-1),'TOP'),('BOTTOMPADDING',(0,0),(-1,-1),2),('TOPPADDING',(0,0),(-1,-1),2),('LEFTPADDING',(0,0),(-1,-1),0)]))
            flow += [tt]
        elif k=='figures':
            rows=[[para(re.sub(r';.*','',label),10.5*scale,align=TA_LEFT),para(str(num),10.5*scale,align=TA_CENTER)] for label,num in FIGURES]
            tt=Table(rows,colWidths=[WIDTH-25,25]);tt.setStyle(TableStyle([('BOTTOMPADDING',(0,0),(-1,-1),2),('TOPPADDING',(0,0),(-1,-1),2),('LEFTPADDING',(0,0),(-1,-1),0)]))
            flow += [tt,Spacer(1,10)]
    return flow

def measure(flow):
    return sum(x.wrap(WIDTH,HEIGHT)[1] for x in flow)

def draw_cover(c):
    c.setStrokeColor(BLUE); c.setLineWidth(1.1)
    c.rect(1.7*CM,1.5*CM,PW-3.4*CM,PH-3*CM)
    c.setLineWidth(.35);c.rect(1.85*CM,1.65*CM,PW-3.7*CM,PH-3.3*CM)
    def center(txt,y,size=14,font='TNR-Bold'):
        c.setFillColor(BLUE);c.setFont(font,size);c.drawCentredString(PW/2,y*CM,txt)
    center('TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI',26.6,15)
    center('KHOA CÔNG NGHỆ THÔNG TIN',25.6,14)
    c.setStrokeColor(BLUE);c.line(7.6*CM,25.15*CM,13.4*CM,25.15*CM)
    center('BÁO CÁO BÀI TẬP LỚN',22.3,23)
    center('PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG',20.9,14)
    center('ĐỀ TÀI',18.9,14)
    center('HỆ THỐNG QUẢN LÝ',17.65,22)
    center('NHÀ HÀNG HẢI SẢN',16.55,22)
    center('Nhóm 4',14.8,15)
    c.setFillColor(colors.black);c.setFont('TNR',13)
    c.drawString(3*CM,13.45*CM,'Giảng viên hướng dẫn: ........................................................')
    c.drawString(3*CM,12.55*CM,'Lớp: ....................................................................................')
    members=['Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt']
    tt=Table([[para('Họ và tên',12,bold=True,align=TA_LEFT),para('Mã sinh viên',12,bold=True,align=TA_LEFT)]]+[[para(x,12,align=TA_LEFT),para('................................',12,align=TA_LEFT)] for x in members],colWidths=[8*CM,7*CM])
    tt.setStyle(TableStyle([('LINEBELOW',(0,0),(-1,0),.6,BLUE),('TOPPADDING',(0,0),(-1,-1),7),('BOTTOMPADDING',(0,0),(-1,-1),7)]))
    w,hh=tt.wrap(15*CM,8*CM);tt.drawOn(c,3*CM,10.8*CM-hh)
    center('NĂM 2026',3.2,13)

def draw_pdf():
    path=ROOT/(NAME+'.pdf')
    c=canvas.Canvas(str(path),pagesize=(PW,PH),pageCompression=1)
    c.setTitle('Báo cáo BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4')
    c.setAuthor('Nhóm 4 – Khoa Công nghệ thông tin, Trường Đại học Giao thông Vận tải')
    result=[]
    for num,pg in enumerate(PAGES,1):
        c.bookmarkPage('page'+str(num))
        if num==1:
            draw_cover(c); result.append({'page':num,'title':pg['title'],'scale':1,'used_pt':0})
            c.showPage();continue
        c.addOutlineEntry(pg['title'],'page'+str(num),level=0)
        c.setFont('TNR',9);c.setFillColor(GRAY)
        c.drawString(LEFT,PH-1.25*CM,'PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG')
        c.drawRightString(PW-RIGHT,PH-1.25*CM,'NHÓM 4')
        c.setStrokeColor(colors.HexColor('#C4CED6'));c.setLineWidth(.4)
        c.line(LEFT,PH-1.5*CM,PW-RIGHT,PH-1.5*CM)
        c.setFont('TNR',10);c.drawCentredString(PW/2,1.1*CM,str(num))
        scale=1.0
        while True:
            flow=blocks_pdf(pg,scale);used=measure(flow)
            if used<=HEIGHT or scale<.84:break
            scale-=.02
        if used>HEIGHT: raise RuntimeError(f'Trang {num} bị tràn: {used}/{HEIGHT}')
        # Chừa thêm dung lượng Word để font/line breaking không sinh trang trắng.
        result.append({'page':num,'title':pg['title'],'scale':round(scale,3),'used_pt':round(used,1),'available_pt':round(HEIGHT,1)})
        y=PH-TOP
        for f in flow:
            w,hh=f.wrap(WIDTH,HEIGHT)
            xx=LEFT+(WIDTH-w)/2 if isinstance(f,Image) else LEFT
            f.drawOn(c,xx,y-hh);y-=hh
        c.showPage()
    c.save()
    return result

def set_run(run,size=13,bold=False,italic=False,color=None):
    run.font.name='Times New Roman';run.font.size=Pt(size)
    run.bold=bold;run.italic=italic
    if color:run.font.color.rgb=RGBColor.from_string(color)
    rpr=run._element.get_or_add_rPr();rf=rpr.find(qn('w:rFonts'))
    if rf is None:rf=OxmlElement('w:rFonts');rpr.insert(0,rf)
    for k in ['ascii','hAnsi','eastAsia','cs']:rf.set(qn('w:'+k),'Times New Roman')

def field(par,command,cached=''):
    run=par.add_run();start=OxmlElement('w:fldChar');start.set(qn('w:fldCharType'),'begin')
    ins=OxmlElement('w:instrText');ins.set(qn('xml:space'),'preserve');ins.text=' '+command+' '
    sep=OxmlElement('w:fldChar');sep.set(qn('w:fldCharType'),'separate')
    t=OxmlElement('w:t');t.text=cached
    end=OxmlElement('w:fldChar');end.set(qn('w:fldCharType'),'end')
    for x in [start,ins,sep,t,end]:run._r.append(x)
    return run

def add_bookmark(par,name,num):
    st=OxmlElement('w:bookmarkStart');st.set(qn('w:id'),str(num));st.set(qn('w:name'),name)
    en=OxmlElement('w:bookmarkEnd');en.set(qn('w:id'),str(num));par._p.insert(0,st);par._p.append(en)

def config_para(par,after=5,line=1.25,keep=False):
    pf=par.paragraph_format;pf.space_after=Pt(after);pf.space_before=Pt(0);pf.line_spacing=line
    pf.keep_with_next=keep;pf.widow_control=True

def add_doc_text(doc,text,size=13,bold=False,italic=False,color=None,align=WD_ALIGN_PARAGRAPH.JUSTIFY,style=None):
    par=doc.add_paragraph(style=style);par.alignment=align;config_para(par)
    set_run(par.add_run(text),size,bold,italic,color)
    return par

def doc_table(doc,b,scale):
    t=doc.add_table(rows=1,cols=len(b['head']));t.autofit=False
    n=len(b['head']);widths=b.get('widths') or [1/n]*n
    for col,w in zip(t.columns,widths):col.width=Cm(16*w)
    for i,txt in enumerate(b['head']):t.rows[0].cells[i].text=txt
    for row in b['rows']:
        cells=t.add_row().cells
        for i,txt in enumerate(row):cells[i].text=str(txt)
    for ri,row in enumerate(t.rows):
        trpr=row._tr.get_or_add_trPr();cant=OxmlElement('w:cantSplit');trpr.append(cant)
        if ri==0:
            rep=OxmlElement('w:tblHeader');trpr.append(rep)
        for ci,cell in enumerate(row.cells):
            cell.width=Cm(16*widths[ci])
            pr=cell._tc.get_or_add_tcPr();sh=OxmlElement('w:shd')
            sh.set(qn('w:fill'),'193B55' if ri==0 else 'F1F5F8' if ri%2==0 else 'FFFFFF');pr.append(sh)
            margins=OxmlElement('w:tcMar')
            for side,value in [('top','70'),('bottom','70'),('left','90'),('right','90')]:
                e=OxmlElement('w:'+side);e.set(qn('w:w'),value);e.set(qn('w:type'),'dxa');margins.append(e)
            pr.append(margins)
            for par in cell.paragraphs:
                par.alignment=WD_ALIGN_PARAGRAPH.LEFT;config_para(par,after=0,line=1.12)
                for run in par.runs:set_run(run,11.3*scale,bold=ri==0,color='FFFFFF' if ri==0 else None)
    props=t._tbl.tblPr;borders=OxmlElement('w:tblBorders')
    for side in ['top','left','bottom','right','insideH','insideV']:
        e=OxmlElement('w:'+side);e.set(qn('w:val'),'single');e.set(qn('w:sz'),'4');e.set(qn('w:color'),'CBD5DD');borders.append(e)
    props.append(borders)
    par=doc.add_paragraph();config_para(par,after=3,line=1);par.paragraph_format.space_before=Pt(0)
    par.paragraph_format.line_spacing=Pt(2);par.add_run().font.size=Pt(2)

def doc_cover(doc):
    def center(txt,size,bold=True,after=10):
        par=add_doc_text(doc,txt,size,bold,color='193B55',align=WD_ALIGN_PARAGRAPH.CENTER)
        par.paragraph_format.space_after=Pt(after);return par
    center('TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI',15,after=8)
    center('KHOA CÔNG NGHỆ THÔNG TIN',14,after=68)
    center('BÁO CÁO BÀI TẬP LỚN',23,after=12)
    center('PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG',14,after=34)
    center('ĐỀ TÀI',14,after=14)
    center('HỆ THỐNG QUẢN LÝ\nNHÀ HÀNG HẢI SẢN',22,after=25)
    center('Nhóm 4',15,after=20)
    add_doc_text(doc,'Giảng viên hướng dẫn: ........................................................',13,align=WD_ALIGN_PARAGRAPH.LEFT)
    add_doc_text(doc,'Lớp: ....................................................................................',13,align=WD_ALIGN_PARAGRAPH.LEFT)
    b=table_data=['Họ và tên','Mã sinh viên']
    doc_table(doc,{'head':b,'rows':[[x,'................................'] for x in ['Hoàng Việt Đức','Nguyễn Cao Hoàng','Đỗ Minh Chiến','Lý Tiến Đạt']],'widths':[.55,.45]},1)
    center('NĂM 2026',13,after=0)

def draw_docx(layout):
    doc=Document();sec=doc.sections[0]
    sec.page_width=Cm(21);sec.page_height=Cm(29.7)
    sec.left_margin=Cm(3);sec.right_margin=Cm(2);sec.top_margin=Cm(2.1);sec.bottom_margin=Cm(2)
    sec.header_distance=Cm(.8);sec.footer_distance=Cm(.8)
    sec.different_first_page_header_footer=True
    for name in ['Normal','Heading 1','Heading 2','Heading 3','Caption']:
        style=doc.styles[name];style.font.name='Times New Roman';style.font.size=Pt(13)
        style.paragraph_format.space_after=Pt(5);style.paragraph_format.line_spacing=1.25
    doc.styles['Heading 1'].font.size=Pt(15.5);doc.styles['Heading 1'].font.color.rgb=RGBColor.from_string('193B55')
    doc.styles['Heading 2'].font.size=Pt(13);doc.styles['Heading 2'].font.bold=True
    doc.styles['Heading 3'].font.size=Pt(13);doc.styles['Heading 3'].font.bold=True
    normal=doc.styles['Normal']._element.get_or_add_rPr();lang=OxmlElement('w:lang');lang.set(qn('w:val'),'vi-VN');normal.append(lang)
    header=sec.header.paragraphs[0];config_para(header,after=0,line=1)
    set_run(header.add_run('PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG                          NHÓM 4'),9,color='526271')
    footer=sec.footer.paragraphs[0];footer.alignment=WD_ALIGN_PARAGRAPH.CENTER
    set_run(field(footer,'PAGE','2'),10)
    # Tham chiếu cập nhật khi người dùng chỉnh Word; số trang trong PDF luôn cố định.
    update=OxmlElement('w:updateFields');update.set(qn('w:val'),'true');doc.settings._element.append(update)
    doc.core_properties.title='Báo cáo BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4'
    doc.core_properties.author='Nhóm 4 – Khoa Công nghệ thông tin, Trường Đại học Giao thông Vận tải'
    doc.core_properties.subject='Hệ thống quản lý nhà hàng hải sản'
    for num,pg in enumerate(PAGES,1):
        if num>1:doc.add_page_break()
        scale=layout[num-1]['scale']
        if num==1:doc_cover(doc);continue
        blocks=pg['blocks']
        if pg.get('chapter'):
            chapter=add_doc_text(doc,blocks[0]['text'],15.5*scale,True,color='193B55',align=WD_ALIGN_PARAGRAPH.LEFT,style='Heading 1')
            chapter.paragraph_format.keep_with_next=True
            blocks=blocks[1:]
        title=add_doc_text(doc,pg['title'],15.5*scale,True,color='193B55',align=WD_ALIGN_PARAGRAPH.LEFT,style='Heading 2' if re.match(r'^\d+\.',pg['title']) else 'Heading 1')
        title.paragraph_format.keep_with_next=True
        add_bookmark(title,'page'+str(num),num)
        for b in blocks:
            k=b['kind']
            if k in ['p','h','note']:
                size=(11.5 if k=='note' else 13)*scale
                par=add_doc_text(doc,b['text'],size,bold=k=='h',italic=k=='note',color='526271' if k=='note' else None,style='Heading 3' if k=='h' else None,align=WD_ALIGN_PARAGRAPH.LEFT if k=='h' else WD_ALIGN_PARAGRAPH.JUSTIFY)
                if k=='h':par.paragraph_format.keep_with_next=True
            elif k=='table':doc_table(doc,b,scale)
            elif k=='code':
                par=add_doc_text(doc,b['text'],9.5*scale,align=WD_ALIGN_PARAGRAPH.LEFT)
                par.paragraph_format.line_spacing=Pt(13*scale);par.paragraph_format.keep_together=True
                for run in par.runs:run.font.name='Arial'
                shade=OxmlElement('w:shd');shade.set(qn('w:fill'),'F3F5F7');par._p.get_or_add_pPr().append(shade)
            elif k=='image':
                path=ROOT/'so_do'/(b['name']+'.png');w,hh=PILImage.open(path).size
                factor=min(16/w,b['maxh']/hh)
                par=doc.add_paragraph();par.alignment=WD_ALIGN_PARAGRAPH.CENTER;config_para(par,after=3,line=1)
                par.add_run().add_picture(str(path),width=Cm(w*factor),height=Cm(hh*factor))
                par.paragraph_format.keep_with_next=True
                cap=add_doc_text(doc,b['caption'],11*scale,italic=True,align=WD_ALIGN_PARAGRAPH.CENTER,style='Caption')
            elif k in ['toc','figures']:
                entries=TOC if k=='toc' else [(label,nn,False) for label,nn in FIGURES]
                for label,nn,bold in entries:
                    par=doc.add_paragraph();config_para(par,after=2,line=1.02)
                    par.paragraph_format.tab_stops.add_tab_stop(Cm(15),WD_ALIGN_PARAGRAPH.RIGHT)
                    link=OxmlElement('w:hyperlink');link.set(qn('w:anchor'),'page'+str(nn))
                    rr=OxmlElement('w:r');rpr=OxmlElement('w:rPr')
                    fonts=OxmlElement('w:rFonts');fonts.set(qn('w:ascii'),'Times New Roman');fonts.set(qn('w:hAnsi'),'Times New Roman');rpr.append(fonts)
                    sz=OxmlElement('w:sz');sz.set(qn('w:val'),str(round((10.7 if k=='toc' else 10.5)*scale*2)));rpr.append(sz)
                    if bold:rpr.append(OxmlElement('w:b'))
                    rr.append(rpr);tx=OxmlElement('w:t');tx.text=label;rr.append(tx);link.append(rr);par._p.append(link)
                    set_run(par.add_run('\t'),10.7*scale)
                    set_run(field(par,f'PAGEREF page{nn} \\h',str(nn)),10.7*scale)
    path=ROOT/(NAME+'.docx');doc.save(path)
    return path

def write_markdown():
    lines=[]
    for num,pg in enumerate(PAGES,1):
        lines += [f"# {pg['title']}",f'<!-- Trang dự kiến {num} -->','']
        for b in pg['blocks']:
            k=b['kind']
            if k in ['p','h','note']:lines += [('## ' if k=='h' else '> ' if k=='note' else '')+b['text'],'']
            elif k=='table':
                lines += ['| '+' | '.join(b['head'])+' |','| '+' | '.join(['---']*len(b['head']))+' |']
                lines += ['| '+' | '.join(str(x).replace('\n','<br>') for x in row)+' |' for row in b['rows']];lines+=['']
            elif k=='code':lines += ['```java',b['text'],'```','']
            elif k=='image':lines += [f"![{b['caption']}](../so_do/{b['name']}.svg)",'']
    (ROOT/'nguon'/'BaoCao_noi_dung.md').write_text('\n'.join(lines),encoding='utf-8')

if __name__=='__main__':
    layout=draw_pdf();path=draw_docx(layout);write_markdown()
    (ROOT/'nguon'/'bo_cuc.json').write_text(json.dumps(layout,ensure_ascii=False,indent=2))
    print(json.dumps({'docx':str(path),'pages':len(PAGES),'min_scale':min(x['scale'] for x in layout),'dense_pages':[x['page'] for x in layout if x['scale']<1]},ensure_ascii=False))
