const fs = require('fs');
const path = require('path');
const Module = require('module');
const base = fs.readFileSync(path.join(__dirname, 'build_report.js'), 'utf8');
const root = path.resolve(__dirname, '../..');
const backup = path.join(root, 'BTL.truoc-chinh-sua-2026-11-10.docx');
if (!fs.existsSync(backup)) fs.copyFileSync(path.join(root, 'BTL.docx'), backup);

function build(content, output, prefix) {
  let source = base;
  function replace(from, to) {
    if (!source.includes(from)) throw new Error('Missing fragment: ' + from);
    source = source.replace(from, to);
  }
  replace("require('./content.js')", `require('./${content}')`);
  replace("if(b.type==='h')return heading(b.text);", "if(b.type==='h')return heading(b.text,/^\\d+\\.\\d+\\.\\d+/.test(b.text)?'Heading3':/^[A-Z_]+ –/.test(b.text)?'Subheading':'Heading2');");
  replace("pg.title==='MỤC LỤC'?'TOCHeading':'Heading1'", "pg.title==='MỤC LỤC'?'TOCHeading':/^(CHƯƠNG |KẾT LUẬN|TÀI LIỆU THAM KHẢO)/.test(pg.title)?'Heading1':/^2\\.3\\.\\d+/.test(pg.title)?'Heading3':'Heading2'");
  // A live Word TOC updates when opened; cached entries are links without invented page numbers.
  replace('TOC \\\\o "1-1"', 'TOC \\\\o "1-2"');
  replace("${run(String(i+1))}", '');
  replace('w:line="300" w:lineRule="auto"', 'w:line="360" w:lineRule="auto"');
  replace("sty('Normal','<w:jc w:val=\"both\"/><w:widowControl/>'", "sty('Normal','<w:jc w:val=\"both\"/><w:widowControl/><w:ind w:firstLine=\"567\"/>'");
  replace("sty('TableText',", "sty('Heading3','<w:jc w:val=\"left\"/><w:keepNext/><w:keepLines/><w:spacing w:before=\"120\" w:after=\"90\"/><w:outlineLvl w:val=\"2\"/>',font('Times New Roman',26).replace('</w:rPr>','<w:b/></w:rPr>'))+\nsty('Subheading','<w:jc w:val=\"left\"/><w:keepNext/><w:spacing w:before=\"100\" w:after=\"70\"/>',font('Times New Roman',26).replace('</w:rPr>','<w:b/></w:rPr>'))+\nsty('TableText',");
  replace("font('Times New Roman',29,'163F59')", "font('Times New Roman',32,'000000')");
  replace("font('Times New Roman',27,'163F59')", "font('Times New Roman',28,'000000')");
  replace("font('Consolas',18,'183B50')", "font('Times New Roman',23,'000000')");
  replace('w:line="230" w:lineRule="exact"', 'w:line="270" w:lineRule="auto"');
  replace('<w:top w:w="65" w:type="dxa"/>', '<w:top w:w="45" w:type="dxa"/>');
  replace('<w:bottom w:w="65" w:type="dxa"/>', '<w:bottom w:w="45" w:type="dxa"/>');
  replace("font('Times New Roman',22))", "font('Times New Roman',23))");
  replace("const target=path.join(root,'BTL.docx');", `const target=path.join(root,${JSON.stringify(output)});`);
  replace("'BTL-noi-dung.md'", JSON.stringify(prefix + '-noi-dung.md'));
  replace("'report-structure.json'", JSON.stringify(prefix + '-structure.json'));
  replace("fs.writeFileSync(target,zip(packageFiles));", `
for(const [name, value] of Object.entries(packageFiles)) {
 if (!name.endsWith('.xml') || typeof value !== 'string') continue;
 packageFiles[name] = value
   .replace(/<w:color\\b[^>]*\\/>/g, '<w:color w:val="000000"/>')
   .replace(/<w:rFonts\\b[^>]*\\/>/g, '<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/>')
   .replace(/<w:shd\\b[^>]*\\/>/g, '<w:shd w:val="clear" w:fill="FFFFFF"/>')
   .replace(/(<w:(?:top|left|bottom|right|insideH|insideV)\\b[^>]*w:color=")[^"]*(")/g, (_,a,b)=>a+'000000'+b)
   .replace(/2026-10-24T00:00:00Z/g, '2026-11-10T00:00:00Z')
   .replace('Báo cáo theo sơ đồ 12 bảng và đối chiếu mã nguồn hiện có; phần thiết kế và phần cài đặt được phân biệt rõ.', 'Đối chiếu mã nguồn ngày 10/11/2026; 12 bảng nghiệp vụ và bảng tài khoản; kết quả kiểm thử H2 được tách khỏi xác nhận Oracle.');
}
packageFiles['word/styles.xml'] = packageFiles['word/styles.xml'].replace(/<w:style\\b[\\s\\S]*?<\\/w:style>/g, style => {
 if (style.includes('w:styleId="Normal"')) return style;
 return style.replace(/<w:ind\\b[^>]*\\/>/g, '').replace('<w:pPr>', '<w:pPr><w:ind w:firstLine="0"/>');
});
for (const part of ['word/header1.xml', 'word/footer1.xml']) {
 packageFiles[part] = packageFiles[part].replace('<w:pPr>', '<w:pPr><w:ind w:firstLine="0"/>');
}
if (${JSON.stringify(prefix)} === 'Hoang-on-tap') {
 packageFiles['word/header1.xml']=packageFiles['word/header1.xml'].replace('BÀI TẬP LỚN NHÓM • QUẢN LÝ NHÀ HÀNG HẢI SẢN','ÔN BẢO VỆ • NGUYỄN CAO HOÀNG');
}
fs.writeFileSync(target,zip(packageFiles));
fs.writeFileSync(path.join(__dirname, ${JSON.stringify(prefix + '-blocks.json')}), JSON.stringify(pages,null,2));`);
  const filename = path.join(__dirname, 'generated-' + prefix + '.js');
  const compiled = new Module(filename, module);
  compiled.filename = filename;
  compiled.paths = module.paths;
  compiled._compile(source, filename);
}
build('content-current.js', 'BTL.docx', 'BTL-da-chinh');
build('content-oral.js', 'ON_TAP_BAO_VE_HOANG.docx', 'Hoang-on-tap');
console.log('Original report preserved at: ' + backup);
