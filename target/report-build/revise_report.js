const fs = require('fs');
const Module = require('module');
const path = require('path');
const filename = path.join(__dirname, 'build_report.js');
let source = fs.readFileSync(filename, 'utf8');
function replace(from, to) {
  if (!source.includes(from)) throw new Error('Missing builder fragment: ' + from);
  source = source.replace(from, to);
}
replace("require('./content.js')", "require('./content-two-chapters.js')");
replace("if(b.type==='h')return heading(b.text);", "if(b.type==='h')return heading(b.text,/^[A-Z_]+ –/.test(b.text)?'Subheading':/^[12]\\.\\d+\\.\\d+\\./.test(b.text)?'Heading3':'Heading2');");
replace("pg.title==='MỤC LỤC'?'TOCHeading':'Heading1'", "pg.title==='MỤC LỤC'?'TOCHeading':/^(CHƯƠNG |KẾT LUẬN$)/.test(pg.title)?'Heading1':/^2\\.\\d+\\.\\d+\\./.test(pg.title)?'Heading3':'Heading2'");
replace('TOC \\\\o "1-1"', 'TOC \\\\o "1-2"');
replace("sty('TableText',", "sty('Heading3','<w:jc w:val=\"left\"/><w:keepNext/><w:keepLines/><w:spacing w:before=\"100\" w:after=\"80\"/><w:outlineLvl w:val=\"2\"/>',font('Times New Roman',26,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+\nsty('TableText',");
replace("sty('TableText',", "sty('Subheading','<w:jc w:val=\"left\"/><w:keepNext/><w:keepLines/><w:spacing w:before=\"100\" w:after=\"80\"/>',font('Times New Roman',26,'163F59').replace('</w:rPr>','<w:b/></w:rPr>'))+\nsty('TableText',");
replace('2026-10-24T00:00:00Z', '2026-10-29T00:00:00Z');
const compiled = new Module(filename, module);
compiled.filename = filename;
compiled.paths = module.paths;
compiled._compile(source, filename);
