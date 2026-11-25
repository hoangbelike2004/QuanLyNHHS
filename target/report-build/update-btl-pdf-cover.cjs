const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const { PDFDocument, PDFName, rgb } = require('./pdf-tools/node_modules/pdf-lib');
const fontkit = require('./pdf-tools/node_modules/@pdf-lib/fontkit');

async function main() {
  const root = path.resolve(__dirname, '../..');
  const pdfPath = path.join(root, 'BTL.pdf');
  const document = await PDFDocument.load(fs.readFileSync(pdfPath), { updateMetadata: false });
  const bodyBefore = document.getPages().slice(1).map(page => page.node.toString());
  const originalObjects = new Map(document.context.enumerateIndirectObjects()
    .map(([ref, object]) => [ref.toString(), object.toString()]));
  document.registerFontkit(fontkit);
  const font = await document.embedFont(fs.readFileSync('C:/Windows/Fonts/timesbd.ttf'), { subset: true });
  const logo = await document.embedJpg(fs.readFileSync(path.join(__dirname, 'btl-cover-logo.jpeg')));
  const cover = document.getPage(0);
  const coverRef = cover.ref.toString();
  // Replace the complete cover content so the obsolete placeholders are removed.
  cover.node.set(PDFName.of('Contents'), document.context.obj([]));
  cover.node.set(PDFName.of('Resources'), document.context.obj({}));
  const { width, height } = cover.getSize();
  const black = rgb(0, 0, 0);
  function text(value, x, baselineFromTop, size) {
    cover.drawText(value, { x, y: height - baselineFromTop, size, font, color: black });
  }
  function center(value, baselineFromTop, size) {
    text(value, (width - font.widthOfTextAtSize(value, size)) / 2, baselineFromTop, size);
  }
  for (const inset of [0, 3]) {
    cover.drawRectangle({ x: 69.5 + inset, y: 52.5 + inset,
      width: 456 - 2 * inset, height: 745 - 2 * inset,
      borderWidth: 1.2, borderColor: black });
  }
  center('TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI', 71, 14);
  center('KHOA CÔNG NGHỆ THÔNG TIN', 94, 14);
  const dividerY = height - 112.5;
  for (const [left, right] of [[212, 286], [309, 383]]) {
    cover.drawLine({ start: { x: left, y: dividerY }, end: { x: right, y: dividerY }, thickness: 1.2, color: black });
  }
  const diamond = [[297.5, dividerY + 4], [301.5, dividerY], [297.5, dividerY - 4], [293.5, dividerY]];
  diamond.forEach((point, index) => {
    const next = diamond[(index + 1) % diamond.length];
    cover.drawLine({ start: { x: point[0], y: point[1] }, end: { x: next[0], y: next[1] }, thickness: 0.8, color: black });
  });
  cover.drawImage(logo, { x: (width - 126.4) / 2, y: height - 275.1, width: 126.4, height: 133.1 });
  center('BÁO CÁO BÀI TẬP LỚN', 325, 23);
  center('Môn học: Công nghệ Java', 358.5, 13);
  center('ĐỀ TÀI', 392, 14);
  center('HỆ THỐNG QUẢN LÝ', 426.5, 22);
  center('NHÀ HÀNG HẢI SẢN', 457.5, 22);
  text('Giảng viên hướng dẫn: Đào Thị Lệ Thủy', 107, 502, 14);
  text('Sinh viên thực hiện: NHÓM 4', 107, 529, 14);
  const members = [
    ['Hoàng Việt Đức', '5260046'],
    ['Nguyễn Cao Hoàng', '5260063'],
    ['Đỗ Minh Chiến', '5260020'],
    ['Lý Tiến Đạt', '5260038'],
  ];
  members.forEach(([name, id], index) => {
    text(name, 125, 552.5 + index * 19.5, 13);
    text('MSSV: ' + id, 294, 552.5 + index * 19.5, 13);
  });
  text('Lớp: LTCQ1CNTT', 107, 637, 14);
  text('Khóa: k30.1', 294, 637, 14);
  center('Hà Nội, năm 2026', 700, 13);
  const output = await document.save();
  const check = await PDFDocument.load(output, { updateMetadata: false });
  if (check.getPageCount() !== 27) throw new Error('Unexpected page count');
  check.getPages().slice(1).forEach((page, index) => {
    if (page.node.toString() !== bodyBefore[index]) throw new Error('Body page changed: ' + (index + 2));
  });
  for (const [reference, object] of check.context.enumerateIndirectObjects()) {
    const key = reference.toString();
    if (key !== coverRef && originalObjects.has(key) && object.toString() !== originalObjects.get(key)) {
      throw new Error('Unexpected modification to existing PDF object: ' + key);
    }
  }
  const work = path.join(__dirname, 'BTL-updated-cover.pdf');
  fs.writeFileSync(work, output);
  fs.copyFileSync(work, pdfPath);
  fs.writeFileSync(path.join(__dirname, 'btl-pdf-cover-validation.json'), JSON.stringify({
    pages: check.getPageCount(), unchangedBodyPages: 26,
    changedExistingObjects: [coverRef], class: 'LTCQ1CNTT', cohort: 'k30.1',
    sha256: crypto.createHash('sha256').update(output).digest('hex'),
  }, null, 2));
  console.log('Updated PDF cover; all 26 body pages and existing objects outside the cover are unchanged.');
}
main().catch(error => { console.error(error); process.exitCode = 1; });
