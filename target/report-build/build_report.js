const fs = require('fs');
const path = require('path');
const zlib = require('zlib');
const root = path.resolve(__dirname, '../..');
const pages = [];
const xml = s => String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
const p = text => ({type:'p',text});
const h = text => ({type:'h',text});
const code = text => ({type:'code',text:text.trim()});
const table = (caption,headers,rows,widths) => ({type:'table',caption,headers,rows,widths});
const fig = (file,caption,width,height) => ({type:'figure',file,caption,width,height});
const note = text => ({type:'note',text});
const page = (title, ...blocks) => pages.push({title,blocks});
const schema = (name, purpose, cols, key, status, rule) => ({name,purpose,cols,key,status,rule});
const schemas = [
schema('KHACH_HANG','Lưu thông tin khách hàng và điểm tích lũy.',[
['MA_KH','NUMBER','PK; mã khách hàng'],['HO_TEN','VARCHAR2(100)','Họ tên khách hàng'],['SDT','VARCHAR2(15)','Số điện thoại'],['EMAIL','VARCHAR2(100)','Địa chỉ email'],['DIEM_TICH_LUY','NUMBER(10,0)','Điểm tích lũy, không âm']], 'MA_KH','Thiết kế; chưa có entity hoàn chỉnh.','Không bắt buộc tạo hồ sơ cho khách vãng lai; khi đó MA_KH trên hóa đơn có thể để trống.'),
schema('DAT_BAN','Ghi nhận yêu cầu đặt bàn của khách hàng.',[
['MA_DAT','NUMBER','PK; mã đặt bàn'],['MA_KH','NUMBER','FK → KHACH_HANG.MA_KH'],['MA_BAN','NUMBER','FK → BAN_AN.MA_BAN'],['NGAY_DAT','DATE','Ngày và giờ khách dự kiến đến'],['SO_NGUOI','NUMBER(3)','Số khách, lớn hơn 0'],['TIEN_COC','NUMBER(12,2)','Tiền đặt cọc, không âm'],['TRANG_THAI','VARCHAR2(20)','Trạng thái đặt bàn']], 'MA_DAT','Thiết kế; controller/service còn trống.','SO_NGUOI không vượt sức chứa bàn được chọn. Chưa có thời điểm kết thúc nên mô hình chưa đủ để tự kiểm tra trùng khoảng thời gian.'),
schema('BAN_AN','Lưu bàn phục vụ và trạng thái sử dụng.',[
['MA_BAN','NUMBER','PK; mã bàn'],['TEN_BAN','VARCHAR2(50)','Tên hoặc số hiệu bàn'],['SO_CHO','NUMBER(3)','Sức chứa, lớn hơn 0'],['KHU_VUC','VARCHAR2(50)','Khu vực bố trí'],['TRANG_THAI','VARCHAR2(20)','Trống / đặt trước / đang dùng']], 'MA_BAN','Thiết kế; chưa có entity hoàn chỉnh.','Bàn có thể phát sinh nhiều hóa đơn theo thời gian; tại một thời điểm chỉ có một hóa đơn đang phục vụ theo quy ước nghiệp vụ.'),
schema('HOA_DON','Lưu chứng từ bán hàng và thông tin thanh toán.',[
['MA_HD','NUMBER','PK; mã hóa đơn'],['MA_BAN','NUMBER','FK → BAN_AN.MA_BAN'],['MA_NV','NUMBER','FK → NHAN_VIEN.MA_NV'],['MA_KH','NUMBER','FK → KHACH_HANG.MA_KH; có thể NULL'],['NGAY_LAP','DATE','Ngày giờ lập hóa đơn'],['GIAM_GIA','NUMBER(12,2)','Số tiền giảm trực tiếp'],['TONG_TIEN','NUMBER(14,2)','Số tiền sau giảm giá'],['TRANG_THAI','VARCHAR2(20)','MO / DA_THANH_TOAN / HUY']], 'MA_HD','Thiết kế; chưa có nghiệp vụ hóa đơn.','GIAM_GIA được thống nhất là số tiền, không phải phần trăm. Tiền cọc của đặt bàn chưa được liên kết trực tiếp với hóa đơn trong 12 bảng.'),
schema('LOAI_MON','Phân nhóm các món ăn trong thực đơn.',[
['MA_LOAI','NUMBER','PK; SEQ_LOAI_MON cấp mã trong entity'],['TEN_LOAI','VARCHAR2(100)','Tên loại; entity quy định NOT NULL'],['MO_TA','VARCHAR2(255)','Mô tả loại món'],['TRANG_THAI','VARCHAR2(20)','Hoạt động / tạm dừng']], 'MA_LOAI','Đã có entity, repository và trang danh sách dùng dữ liệu mẫu.','soLuongMon là @Transient; không phải cột lưu trong Oracle và chưa được tính từ truy vấn cơ sở dữ liệu.'),
schema('MON_AN','Lưu thông tin món ăn, nhóm món và giá bán.',[
['MA_MON','NUMBER','PK; SEQ_MON_AN cấp mã trong entity'],['MA_LOAI','NUMBER','FK → LOAI_MON.MA_LOAI; NOT NULL'],['TEN_MON','VARCHAR2(150)','Tên món; entity quy định NOT NULL'],['DON_VI','VARCHAR2(20)','Đơn vị bán: kg, đĩa, con…'],['DON_GIA','NUMBER(12,2)','Giá bán; Java dùng BigDecimal'],['TRANG_THAI','VARCHAR2(20)','Đang phục vụ / tạm hết']], 'MA_MON','Đã có entity, repository và giao diện danh sách mẫu.','hinhAnh, moTa, soLuongDaBan đều @Transient. Không thể xem các giá trị này là dữ liệu đã được lưu xuống Oracle.'),
schema('CT_HOA_DON','Lưu từng món và giá bán tại thời điểm lập hóa đơn.',[
['MA_HD','NUMBER','PK thành phần; FK → HOA_DON.MA_HD'],['MA_MON','NUMBER','PK thành phần; FK → MON_AN.MA_MON'],['SO_LUONG','NUMBER(10,2)','Số lượng bán, lớn hơn 0'],['DON_GIA','NUMBER(12,2)','Giá tại thời điểm bán, không âm'],['THANH_TIEN','NUMBER(14,2)','SO_LUONG × DON_GIA']], '(MA_HD, MA_MON)','Thiết kế; file model/repository còn khung.','Một món chỉ xuất hiện một dòng trong một hóa đơn. Gọi thêm cùng món thì cộng dồn số lượng; không tách nhiều mức giá trong cùng hóa đơn.'),
schema('NHAN_VIEN','Lưu hồ sơ nhân viên tham gia hoạt động nhà hàng.',[
['MA_NV','NUMBER','PK; SEQ_NHAN_VIEN cấp mã trong entity'],['HO_TEN','VARCHAR2(100)','Họ tên; entity quy định NOT NULL'],['GIOI_TINH','VARCHAR2(10)','Giới tính'],['SDT','VARCHAR2(15)','Số điện thoại'],['CHUC_VU','VARCHAR2(50)','Chức vụ công việc'],['LUONG','NUMBER(12,2)','Mức lương; Java dùng BigDecimal'],['NGAY_VAO_LAM','DATE','Ngày vào làm; Java dùng LocalDate']], 'MA_NV','Đã có entity, repository và danh sách nhân viên mẫu.','email, caLamViec, trangThai là @Transient. CHUC_VU là dữ liệu nghiệp vụ, chưa phải vai trò đăng nhập hay Oracle role.'),
schema('NHA_CUNG_CAP','Lưu các đơn vị cung cấp hải sản và nguyên liệu.',[
['MA_NCC','NUMBER','PK; mã nhà cung cấp'],['TEN_NCC','VARCHAR2(150)','Tên nhà cung cấp'],['SDT','VARCHAR2(15)','Số điện thoại liên hệ'],['DIA_CHI','VARCHAR2(200)','Địa chỉ'],['EMAIL','VARCHAR2(100)','Email liên hệ']], 'MA_NCC','Thiết kế; chưa có entity hoàn chỉnh.','Một nhà cung cấp có nhiều phiếu nhập. Thông tin nhà cung cấp được quản lý tập trung, không lặp lại ở mọi phiếu nhập.'),
schema('PHIEU_NHAP','Lưu phần đầu chứng từ nhập nguyên liệu.',[
['MA_PN','NUMBER','PK; mã phiếu nhập'],['MA_NCC','NUMBER','FK → NHA_CUNG_CAP.MA_NCC'],['MA_NV','NUMBER','FK → NHAN_VIEN.MA_NV'],['NGAY_NHAP','DATE','Ngày giờ nhập hàng'],['TONG_TIEN','NUMBER(14,2)','Tổng giá trị chi tiết phiếu']], 'MA_PN','Thiết kế; các lớp chức năng đang để trống.','Mô hình chưa có cột trạng thái duyệt phiếu; trong phạm vi bài tập có thể quy ước phiếu đã lưu là phiếu được ghi nhận nhập.'),
schema('CT_PHIEU_NHAP','Lưu lượng và giá nhập của từng nguyên liệu.',[
['MA_PN','NUMBER','PK thành phần; FK → PHIEU_NHAP.MA_PN'],['MA_NL','NUMBER','PK thành phần; FK → NGUYEN_LIEU.MA_NL'],['SO_LUONG','NUMBER(10,2)','Số lượng nhập, lớn hơn 0'],['DON_GIA','NUMBER(12,2)','Đơn giá nhập, không âm'],['THANH_TIEN','NUMBER(14,2)','SO_LUONG × DON_GIA']], '(MA_PN, MA_NL)','Thiết kế; CTPhieuNhap mới là lớp rỗng.','Mỗi nguyên liệu xuất hiện một dòng trong một phiếu; không mô hình hóa nhiều lô có nhiều đơn giá trong cùng phiếu.'),
schema('NGUYEN_LIEU','Lưu danh mục nguyên liệu và số lượng tồn.',[
['MA_NL','NUMBER','PK; mã nguyên liệu'],['TEN_NL','VARCHAR2(100)','Tên nguyên liệu'],['DON_VI','VARCHAR2(20)','Đơn vị tồn: kg, lít, con…'],['SO_LUONG_TON','NUMBER(10,2)','Số lượng tồn, không âm'],['TRANG_THAI','VARCHAR2(20)','Trạng thái sử dụng']], 'MA_NL','Thiết kế; chưa có entity hoàn chỉnh.','Chưa có bảng định lượng MON_AN–NGUYEN_LIEU, phiếu xuất hoặc kiểm kê; chưa đủ dữ liệu tự trừ tồn khi bán món hay tính lợi nhuận chính xác.')
];

require('./content.js')({page,p,h,code,table,fig,note,schemas});

const W='http://schemas.openxmlformats.org/wordprocessingml/2006/main';
const R='http://schemas.openxmlformats.org/officeDocument/2006/relationships';
let bookId=0, imgId=0;
const media=[];
function run(text, props='') {return `<w:r>${props?`<w:rPr>${props}</w:rPr>`:''}<w:t xml:space="preserve">${xml(text).replace(/\n/g,'</w:t><w:br/><w:t xml:space="preserve">')}</w:t></w:r>`;}
function para(text,style='Normal',extra='') { return `<w:p><w:pPr><w:pStyle w:val="${style}"/>${extra}</w:pPr>${run(text)}</w:p>`; }
function heading(text, style='Heading2', bookmark) {
 const id=++bookId;
 return `<w:p><w:pPr><w:pStyle w:val="${style}"/></w:pPr>${bookmark?`<w:bookmarkStart w:id="${id}" w:name="${bookmark}"/>`:''}${run(text)}${bookmark?`<w:bookmarkEnd w:id="${id}"/>`:''}</w:p>`;
}
function renderTable(b) {
 const widths=b.widths||b.headers.map(()=>Math.floor(9071/b.headers.length));
 const all=[b.headers,...b.rows];
 return (b.caption?para(b.caption,'Caption'):'')+`<w:tbl><w:tblPr><w:tblW w:w="9071" w:type="dxa"/><w:tblLayout w:type="fixed"/><w:tblBorders>${['top','left','bottom','right','insideH','insideV'].map(x=>`<w:${x} w:val="single" w:sz="4" w:color="C4D2DC"/>`).join('')}</w:tblBorders><w:tblCellMar><w:top w:w="65" w:type="dxa"/><w:left w:w="95" w:type="dxa"/><w:bottom w:w="65" w:type="dxa"/><w:right w:w="95" w:type="dxa"/></w:tblCellMar></w:tblPr><w:tblGrid>${widths.map(w=>`<w:gridCol w:w="${w}"/>`).join('')}</w:tblGrid>`+all.map((row,i)=>`<w:tr><w:trPr><w:cantSplit/>${i===0?'<w:tblHeader/>':''}</w:trPr>${row.map((cell,j)=>`<w:tc><w:tcPr><w:tcW w:w="${widths[j]}" w:type="dxa"/>${i===0?'<w:shd w:fill="163F59"/>':i%2===0?'<w:shd w:fill="F0F5F8"/>':''}<w:vAlign w:val="center"/></w:tcPr><w:p><w:pPr><w:pStyle w:val="TableText"/></w:pPr>${run(cell,i===0?'<w:b/><w:color w:val="FFFFFF"/>':'')}</w:p></w:tc>`).join('')}</w:tr>`).join('')+'</w:tbl>'+para('','TableText');
}
function renderFigure(b) {
 const n=++imgId, rid='rImg'+n;
 media.push({rid,name:`image${n}.png`,data:fs.readFileSync(path.join(__dirname,b.file))});
 const cx=Math.round(b.width*914400),cy=Math.round(b.height*914400);
 return `<w:p><w:pPr><w:jc w:val="center"/><w:spacing w:after="100"/></w:pPr><w:r><w:drawing><wp:inline distT="0" distB="0" distL="0" distR="0"><wp:extent cx="${cx}" cy="${cy}"/><wp:docPr id="${n}" name="${xml(b.caption)}" descr="${xml(b.caption)}"/><wp:cNvGraphicFramePr><a:graphicFrameLocks noChangeAspect="1"/></wp:cNvGraphicFramePr><a:graphic><a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:pic><pic:nvPicPr><pic:cNvPr id="${n}" name="image${n}.png"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip r:embed="${rid}"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="${cx}" cy="${cy}"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></pic:spPr></pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p>`+para(b.caption,'Caption','<w:jc w:val="center"/>');
}
function renderBlock(b) {
 if(b.type==='p')return para(b.text);
 if(b.type==='h')return heading(b.text);
 if(b.type==='note')return para(b.text,'Note');
 if(b.type==='table')return renderTable(b);
 if(b.type==='figure')return renderFigure(b);
 if(b.type==='code')return b.text.split('\n').map(l=>para(l,'Code')).join('')+para('','TableText');
 if(b.type==='cover')return b.lines.map(l=>para(l.text,l.style||'CoverText',`<w:jc w:val="center"/><w:spacing w:before="${l.before||0}" w:after="${l.after||160}"/>`)).join('')+renderTable(b.members);
 if(b.type==='toc') {
  const targets=pages.map((x,i)=>({x,i})).filter(({x})=>!['TRANG BÌA','MỤC LỤC'].includes(x.title));
  return `<w:p><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText xml:space="preserve"> TOC \\o "1-1" \\h \\z \\u </w:instrText></w:r><w:r><w:fldChar w:fldCharType="separate"/></w:r></w:p>`+targets.map(({x,i})=>`<w:p><w:pPr><w:pStyle w:val="TOC1"/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs></w:pPr><w:hyperlink w:anchor="page_${i+1}">${run(x.title)}<w:r><w:tab/></w:r>${run(String(i+1))}</w:hyperlink></w:p>`).join('')+'<w:p><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>';
 }
 throw Error('Unknown block '+b.type);
}
const body=pages.map((pg,i)=>(i?'<w:p><w:pPr><w:spacing w:before="0" w:after="0" w:line="20" w:lineRule="exact"/></w:pPr><w:r><w:br w:type="page"/></w:r></w:p>':'')+(i===0?'':heading(pg.title,pg.title==='MỤC LỤC'?'TOCHeading':'Heading1',`page_${i+1}`))+pg.blocks.map(renderBlock).join('')).join('');
const doc=`<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="${W}" xmlns:r="${R}" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"><w:body>${body}<w:sectPr><w:headerReference w:type="default" r:id="rHeader"/><w:footerReference w:type="default" r:id="rFooter"/><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1701" w:header="567" w:footer="567" w:gutter="0"/><w:titlePg/><w:cols w:space="720"/></w:sectPr></w:body></w:document>`;
const font=(name,size,color='222222')=>`<w:rPr><w:rFonts w:ascii="${name}" w:hAnsi="${name}" w:eastAsia="${name}" w:cs="${name}"/><w:color w:val="${color}"/><w:sz w:val="${size}"/><w:szCs w:val="${size}"/></w:rPr>`;
const sty=(id,pp,rp,base='Normal')=>`<w:style w:type="paragraph" w:styleId="${id}"><w:name w:val="${id==='TOC1'?'toc 1':id}"/>${id==='Normal'?'':`<w:basedOn w:val="${base}"/>`}<w:pPr>${pp}</w:pPr>${rp}</w:style>`;
const styles=`<?xml version="1.0" encoding="UTF-8"?><w:styles xmlns:w="${W}"><w:docDefaults><w:rPrDefault>${font('Times New Roman',26)}</w:rPrDefault><w:pPrDefault><w:pPr><w:spacing w:after="100" w:line="300" w:lineRule="auto"/></w:pPr></w:pPrDefault></w:docDefaults>`+
sty('Normal','<w:jc w:val="both"/><w:widowControl/>',font('Times New Roman',26))+
sty('Heading1','<w:jc w:val="left"/><w:keepNext/><w:keepLines/><w:spacing w:before="0" w:after="180"/><w:outlineLvl w:val="0"/>',font('Times New Roman',29,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+
sty('Heading2','<w:jc w:val="left"/><w:keepNext/><w:keepLines/><w:spacing w:before="140" w:after="90"/><w:outlineLvl w:val="1"/>',font('Times New Roman',27,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+
sty('TableText','<w:spacing w:after="20" w:line="240" w:lineRule="auto"/><w:jc w:val="left"/>',font('Times New Roman',22))+
sty('Caption','<w:keepNext/><w:spacing w:before="90" w:after="70"/>',font('Times New Roman',23,'3B5364').replace('</w:rPr>','<w:i/></w:rPr>'))+
sty('Note','<w:spacing w:before="90" w:after="120"/><w:shd w:fill="EDF4F8"/><w:ind w:left="120" w:right="120"/>',font('Times New Roman',24,'284A60'))+
sty('Code','<w:keepLines/><w:spacing w:after="0" w:line="230" w:lineRule="exact"/><w:shd w:fill="F1F4F6"/><w:ind w:left="100" w:right="50"/><w:jc w:val="left"/>',font('Consolas',18,'183B50'))+
sty('TOC1','<w:spacing w:after="10" w:line="240" w:lineRule="exact"/>',font('Times New Roman',22))+
sty('TOCHeading','<w:keepNext/><w:spacing w:before="0" w:after="180"/><w:outlineLvl w:val="9"/>',font('Times New Roman',29,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+
sty('CoverText','<w:spacing w:line="300"/>',font('Times New Roman',27))+
sty('CoverTitle','<w:spacing w:line="340"/>',font('Times New Roman',38,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+
sty('CoverSubtitle','<w:spacing w:line="300"/>',font('Times New Roman',31,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+'</w:styles>';
const rels=[['rStyles','styles','styles.xml'],['rSettings','settings','settings.xml'],['rHeader','header','header1.xml'],['rFooter','footer','footer1.xml'],...media.map(x=>[x.rid,'image','media/'+x.name])];
const packageFiles={
'[Content_Types].xml':`<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Default Extension="png" ContentType="image/png"/>${[['document','document.main'],['styles','styles'],['settings','settings'],['header1','header'],['footer1','footer']].map(([n,t])=>`<Override PartName="/word/${n}.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.${t}+xml"/>`).join('')}<Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/><Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/></Types>`,
'_rels/.rels':`<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rDoc" Type="${R}/officeDocument" Target="word/document.xml"/><Relationship Id="rCore" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/><Relationship Id="rApp" Type="${R}/extended-properties" Target="docProps/app.xml"/></Relationships>`,
'word/document.xml':doc,
'word/styles.xml':styles,
'word/settings.xml':`<?xml version="1.0" encoding="UTF-8"?><w:settings xmlns:w="${W}"><w:zoom w:percent="100"/><w:defaultTabStop w:val="720"/><w:updateFields w:val="true"/><w:compat><w:compatSetting w:name="compatibilityMode" w:uri="http://schemas.microsoft.com/office/word" w:val="15"/></w:compat><w:themeFontLang w:val="vi-VN"/></w:settings>`,
'word/_rels/document.xml.rels':`<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">${rels.map(([id,t,target])=>`<Relationship Id="${id}" Type="${R}/${t}" Target="${target}"/>`).join('')}</Relationships>`,
'word/header1.xml':`<?xml version="1.0" encoding="UTF-8"?><w:hdr xmlns:w="${W}"><w:p><w:pPr><w:pBdr><w:bottom w:val="single" w:sz="5" w:color="B5C7D4"/></w:pBdr><w:spacing w:after="70"/></w:pPr>${run('BÀI TẬP LỚN NHÓM • QUẢN LÝ NHÀ HÀNG HẢI SẢN','<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/><w:color w:val="587183"/><w:sz w:val="18"/>')}</w:p></w:hdr>`,
'word/footer1.xml':`<?xml version="1.0" encoding="UTF-8"?><w:ftr xmlns:w="${W}"><w:p><w:pPr><w:jc w:val="right"/></w:pPr>${run('Trang ','<w:sz w:val="20"/><w:color w:val="587183"/>')}<w:fldSimple w:instr="PAGE"><w:r><w:rPr><w:sz w:val="20"/></w:rPr><w:t>1</w:t></w:r></w:fldSimple></w:p></w:ftr>`,
'docProps/core.xml':`<?xml version="1.0" encoding="UTF-8"?><cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><dc:title>Báo cáo bài tập lớn nhóm - Quản lý nhà hàng hải sản</dc:title><dc:subject>Java Spring Boot và Oracle Database</dc:subject><dc:creator>Nhóm thực hiện</dc:creator><dc:description>Báo cáo theo sơ đồ 12 bảng và đối chiếu mã nguồn hiện có; phần thiết kế và phần cài đặt được phân biệt rõ.</dc:description><dc:language>vi-VN</dc:language><dcterms:created xsi:type="dcterms:W3CDTF">2026-10-24T00:00:00Z</dcterms:created></cp:coreProperties>`,
'docProps/app.xml':`<?xml version="1.0" encoding="UTF-8"?><Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"><Application>BTL report builder</Application><Company></Company></Properties>`
};
for(const m of media)packageFiles['word/media/'+m.name]=m.data;
const crcTable=Array.from({length:256},(_,i)=>{let c=i;for(let j=0;j<8;j++)c=(c&1)?0xEDB88320^(c>>>1):(c>>>1);return c>>>0;});
function crc32(data){let c=0xFFFFFFFF;for(const b of data)c=crcTable[(c^b)&255]^(c>>>8);return (c^0xFFFFFFFF)>>>0;}
function zip(files){let offset=0;const locals=[],central=[];for(const [name,content] of Object.entries(files)){const n=Buffer.from(name);const data=Buffer.isBuffer(content)?content:Buffer.from(content,'utf8');const packed=zlib.deflateRawSync(data);const crc=crc32(data);const l=Buffer.alloc(30);l.writeUInt32LE(0x04034b50,0);l.writeUInt16LE(20,4);l.writeUInt16LE(0x800,6);l.writeUInt16LE(8,8);l.writeUInt32LE(crc,14);l.writeUInt32LE(packed.length,18);l.writeUInt32LE(data.length,22);l.writeUInt16LE(n.length,26);locals.push(l,n,packed);const c=Buffer.alloc(46);c.writeUInt32LE(0x02014b50,0);c.writeUInt16LE(20,4);c.writeUInt16LE(20,6);c.writeUInt16LE(0x800,8);c.writeUInt16LE(8,10);c.writeUInt32LE(crc,16);c.writeUInt32LE(packed.length,20);c.writeUInt32LE(data.length,24);c.writeUInt16LE(n.length,28);c.writeUInt32LE(offset,42);central.push(c,n);offset+=l.length+n.length+packed.length;}const cd=Buffer.concat(central),end=Buffer.alloc(22);end.writeUInt32LE(0x06054b50,0);end.writeUInt16LE(Object.keys(files).length,8);end.writeUInt16LE(Object.keys(files).length,10);end.writeUInt32LE(cd.length,12);end.writeUInt32LE(offset,16);return Buffer.concat([...locals,cd,end]);}
const target=path.join(root,'BTL.docx');
const backup=path.join(root,'BTL.ban-goc.docx');
if(!fs.existsSync(backup))fs.copyFileSync(target,backup);
fs.writeFileSync(target,zip(packageFiles));
const manuscript=pages.map((pg,i)=>`# ${pg.title}\n\n`+pg.blocks.map(b=>b.text||(b.type==='table'?[b.caption,b.headers.join(' | '),...b.rows.map(r=>r.join(' | '))].join('\n'):b.type==='figure'?b.caption:'')).join('\n\n')).join('\n\n---\n\n');
fs.writeFileSync(path.join(__dirname,'BTL-noi-dung.md'),manuscript);
fs.writeFileSync(path.join(__dirname,'report-structure.json'),JSON.stringify({plannedPages:pages.length,figures:media.length,pages:pages.map((pg,i)=>({page:i+1,title:pg.title,characters:JSON.stringify(pg.blocks).length})),packageParts:Object.keys(packageFiles)},null,2));
console.log(JSON.stringify({output:target,backup,pages:pages.length,figures:media.length,bytes:fs.statSync(target).size}));
