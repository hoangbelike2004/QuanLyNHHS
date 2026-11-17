const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

const { FRONT_SECTIONS, CHAPTERS, BACK_SECTIONS } = require('./noi_dung_chuan');

const NGUON_DIR = __dirname;
const ROOT = path.resolve(NGUON_DIR, '..');
const REPO_ROOT = path.resolve(ROOT, '..');
const BACKUP_UNZIPPED = path.join(REPO_ROOT, 'target', 'report-build', 'backup-unzipped');
const BUILT_DOCX = path.join(REPO_ROOT, 'target', 'report-build', 'BaoCao_BTL_PTTKHDoiTuong_Nhom4.built.docx');

const W = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main';
const R = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships';

function xmlEscape(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function pngDimensions(buf) {
  // PNG IHDR starts at byte 8 (4 bytes len, 4 bytes 'IHDR', 4 bytes width BE, 4 bytes height BE)
  return {
    width: buf.readUInt32BE(16),
    height: buf.readUInt32BE(20)
  };
}

// Extract exact cover table & section break from backup docx, enforcing firstLine="0" and Times New Roman
function extractCoverParts() {
  const docXml = fs.readFileSync(path.join(BACKUP_UNZIPPED, 'word', 'document.xml'), 'utf8');
  const docStart = docXml.slice(0, docXml.indexOf('<w:body>') + 8);
  const tblStart = docXml.indexOf('<w:tbl>');
  const tblEnd = docXml.indexOf('</w:tbl>', tblStart) + 8;
  let coverTbl = docXml.slice(tblStart, tblEnd);

  // Remap cover logo relationship id from rId8 to rIdCoverLogo
  coverTbl = coverTbl.replace(/r:id="rId8"/g, 'r:id="rIdCoverLogo"');

  // Ensure every <w:pPr> inside cover table explicitly has w:firstLine="0" so Normal style's 1cm indent never shifts the cover
  coverTbl = coverTbl.replace(/<w:pPr>([\s\S]*?)<\/w:pPr>/g, (match, inner) => {
    let updated = inner;
    if (/<w:ind\b/.test(updated)) {
      updated = updated.replace(/<w:ind\b([^/>]*)\/>/g, (m, attrs) =>
        /w:firstLine=/.test(attrs) ? m : `<w:ind${attrs} w:firstLine="0"/>`
      );
    } else {
      updated += '<w:ind w:firstLine="0"/>';
    }
    return `<w:pPr>${updated}</w:pPr>`;
  });

  // Ensure every <w:rFonts> inside cover table specifies Times New Roman for ascii/hAnsi/eastAsia/cs
  coverTbl = coverTbl.replace(/<w:rFonts\b[^>]*\/>/g,
    '<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/>');

  const coverSectP = '<w:p><w:pPr><w:spacing w:after="0" w:line="20" w:lineRule="exact"/><w:ind w:firstLine="0"/><w:sectPr>' +
    '<w:headerReference w:type="default" r:id="rHeaderEmpty"/>' +
    '<w:footerReference w:type="default" r:id="rFooterEmpty"/>' +
    '<w:pgSz w:w="11906" w:h="16838"/>' +
    '<w:pgMar w:top="850" w:right="1417" w:bottom="850" w:left="1417" w:header="454" w:footer="454" w:gutter="0"/>' +
    '<w:cols w:space="720"/>' +
    '</w:sectPr></w:pPr></w:p>';

  return { docStart, coverTbl, coverSectP };
}

// Collect all figures and tables first so Danh mục hình & Danh mục bảng can reference their exact bookmarks
const allSections = [
  ...FRONT_SECTIONS,
  ...CHAPTERS.map(ch => ({ isChapter: true, ...ch })),
  ...BACK_SECTIONS
];

const collectedFigures = [];
const collectedTables = [];

function scanBlocks(blocks) {
  if (!blocks) return;
  for (const b of blocks) {
    if (b.kind === 'image') collectedFigures.push({ bookmark: b.bookmark, caption: b.caption });
    if (b.kind === 'table') collectedTables.push({ bookmark: b.id, caption: b.caption });
  }
}

for (const item of allSections) {
  if (item.isChapter) {
    for (const sec of item.sections) scanBlocks(sec.blocks);
  } else {
    scanBlocks(item.blocks);
  }
}

let nextBookmarkId = 1;
let nextImgId = 1;
const mediaEntries = [];

function makeRun(text, rPrXml = '') {
  return `<w:r>${rPrXml ? `<w:rPr>${rPrXml}</w:rPr>` : ''}<w:t xml:space="preserve">${xmlEscape(text)}</w:t></w:r>`;
}

function pageBreakPara() {
  return '<w:p><w:pPr><w:spacing w:before="0" w:after="0" w:line="20" w:lineRule="exact"/><w:ind w:firstLine="0"/></w:pPr><w:r><w:br w:type="page"/></w:r></w:p>';
}

function headingPara(text, styleId, bookmarkName) {
  const bId = nextBookmarkId++;
  return `<w:p><w:pPr><w:pStyle w:val="${styleId}"/></w:pPr>` +
    (bookmarkName ? `<w:bookmarkStart w:id="${bId}" w:name="${bookmarkName}"/>` : '') +
    makeRun(text) +
    (bookmarkName ? `<w:bookmarkEnd w:id="${bId}"/>` : '') +
    '</w:p>';
}

function renderNormalPara(text, noIndent = false) {
  if (noIndent) {
    return `<w:p><w:pPr><w:pStyle w:val="NormalNoIndent"/></w:pPr>${makeRun(text)}</w:p>`;
  }
  return `<w:p><w:pPr><w:pStyle w:val="Normal"/></w:pPr>${makeRun(text)}</w:p>`;
}

function renderCodeBlock(text) {
  const lines = String(text).split('\n');
  return lines.map((line, idx) => {
    const keep = idx < lines.length - 1 ? '<w:keepNext/>' : '';
    return `<w:p><w:pPr><w:pStyle w:val="Code"/>${keep}</w:pPr>${makeRun(line)}</w:p>`;
  }).join('') + '<w:p><w:pPr><w:spacing w:before="0" w:after="60" w:line="120" w:lineRule="auto"/><w:ind w:firstLine="0"/></w:pPr></w:p>';
}

function renderTableBlock(b) {
  const totalW = 9071;
  const colWidths = (b.widths || b.head.map(() => 1 / b.head.length)).map(w => Math.round(w * totalW));
  // Adjust last column width so sum matches exact 9071
  const sumExceptLast = colWidths.slice(0, -1).reduce((a, c) => a + c, 0);
  colWidths[colWidths.length - 1] = totalW - sumExceptLast;

  const captionXml = headingPara(b.caption, 'TableCaption', b.id);
  const allRows = [b.head, ...b.rows];

  const rowsXml = allRows.map((row, ri) => {
    const isHeader = ri === 0;
    const trPr = `<w:trPr><w:cantSplit/>${isHeader ? '<w:tblHeader/>' : ''}</w:trPr>`;
    const cellsXml = row.map((cellVal, ci) => {
      const cellLines = String(cellVal).split('\n');
      const parasXml = cellLines.map(line =>
        `<w:p><w:pPr><w:pStyle w:val="TableText"/></w:pPr>${makeRun(line, isHeader ? '<w:b/>' : '')}</w:p>`
      ).join('');
      return `<w:tc><w:tcPr><w:tcW w:w="${colWidths[ci]}" w:type="dxa"/><w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/><w:vAlign w:val="center"/></w:tcPr>${parasXml}</w:tc>`;
    }).join('');
    return `<w:tr>${trPr}${cellsXml}</w:tr>`;
  }).join('');

  const tblXml = `<w:tbl><w:tblPr><w:tblW w:w="${totalW}" w:type="dxa"/><w:tblLayout w:type="fixed"/>` +
    `<w:tblBorders>${['top', 'left', 'bottom', 'right', 'insideH', 'insideV'].map(s => `<w:${s} w:val="single" w:sz="4" w:space="0" w:color="000000"/>`).join('')}</w:tblBorders>` +
    `<w:tblCellMar><w:top w:w="55" w:type="dxa"/><w:left w:w="95" w:type="dxa"/><w:bottom w:w="55" w:type="dxa"/><w:right w:w="95" w:type="dxa"/></w:tblCellMar></w:tblPr>` +
    `<w:tblGrid>${colWidths.map(w => `<w:gridCol w:w="${w}"/>`).join('')}</w:tblGrid>${rowsXml}</w:tbl>`;

  return captionXml + tblXml + '<w:p><w:pPr><w:spacing w:before="0" w:after="60" w:line="120" w:lineRule="auto"/><w:ind w:firstLine="0"/></w:pPr></w:p>';
}

function renderImageBlock(b) {
  const imgPath = path.join(ROOT, 'so_do', `${b.name}.png`);
  const data = fs.readFileSync(imgPath);
  const { width, height } = pngDimensions(data);
  const n = nextImgId++;
  const rid = `rImgDiagram${n}`;
  const mediaName = `diagram_${n}_${b.name}.png`;
  mediaEntries.push({ rid, name: mediaName, data });

  // PlantUML dpi=180 -> 1 inch = 180px = 2.54cm
  const naturalWidthCm = (width / 180) * 2.54;
  const naturalHeightCm = (height / 180) * 2.54;
  const maxWidthCm = 15.6;
  const maxHeightCm = b.maxH || 15.5;
  const scale = Math.min(maxWidthCm / naturalWidthCm, maxHeightCm / naturalHeightCm, 1.0);
  const cx = Math.round(naturalWidthCm * scale * 360000);
  const cy = Math.round(naturalHeightCm * scale * 360000);

  const imgPara = `<w:p><w:pPr><w:keepNext/><w:jc w:val="center"/><w:ind w:firstLine="0"/><w:spacing w:before="80" w:after="40" w:line="240" w:lineRule="auto"/></w:pPr>` +
    `<w:r><w:drawing><wp:inline distT="0" distB="0" distL="0" distR="0">` +
    `<wp:extent cx="${cx}" cy="${cy}"/><wp:docPr id="${n}" name="${xmlEscape(b.caption)}" descr="${xmlEscape(b.caption)}"/>` +
    `<wp:cNvGraphicFramePr><a:graphicFrameLocks xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" noChangeAspect="1"/></wp:cNvGraphicFramePr>` +
    `<a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"><a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture">` +
    `<pic:pic xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:nvPicPr><pic:cNvPr id="${n}" name="${mediaName}"/><pic:cNvPicPr/></pic:nvPicPr>` +
    `<pic:blipFill><a:blip r:embed="${rid}"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill>` +
    `<pic:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="${cx}" cy="${cy}"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></pic:spPr>` +
    `</pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p>`;

  const captionPara = headingPara(b.caption, 'FigureCaption', b.bookmark);
  return imgPara + captionPara;
}

function renderBlocks(blocks) {
  if (!blocks) return '';
  let out = '';
  for (const b of blocks) {
    if (b.kind === 'p') out += renderNormalPara(b.text, Boolean(b.noIndent));
    else if (b.kind === 'h3') out += headingPara(b.text, 'Heading3');
    else if (b.kind === 'table') out += renderTableBlock(b);
    else if (b.kind === 'image') out += renderImageBlock(b);
    else if (b.kind === 'code') out += renderCodeBlock(b.text);
    else throw new Error(`Unknown block kind: ${b.kind}`);
  }
  return out;
}

function renderTocField() {
  // Build placeholder entries; Word COM ($toc.Update()) will replace everything between separate and end with real page numbers
  const placeholderRows = [];
  for (const item of allSections) {
    if (item.isToc) continue;
    placeholderRows.push({ label: item.title, style: 'TOC1', bold: true, anchor: item.id });
    if (item.isChapter) {
      for (const sec of item.sections) {
        placeholderRows.push({ label: sec.title, style: 'TOC2', bold: false, anchor: sec.id });
      }
    }
  }
  const rPr = '<w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="24"/><w:szCs w:val="24"/></w:rPr>';
  const head = `<w:p><w:pPr><w:pStyle w:val="TOC1"/></w:pPr><w:r>${rPr}<w:fldChar w:fldCharType="begin"/></w:r><w:r>${rPr}<w:instrText xml:space="preserve"> TOC \\o "1-2" \\h \\z \\u </w:instrText></w:r><w:r>${rPr}<w:fldChar w:fldCharType="separate"/></w:r></w:p>`;
  const entries = placeholderRows.map(row =>
    `<w:p><w:pPr><w:pStyle w:val="${row.style}"/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs></w:pPr>` +
    `<w:hyperlink w:anchor="${row.anchor}" w:history="1">` +
    makeRun(row.label, row.bold ? '<w:b/>' : '') +
    `<w:r><w:tab/></w:r>` +
    makeRun('...') +
    `</w:hyperlink></w:p>`
  ).join('');
  const tail = `<w:p><w:pPr><w:pStyle w:val="TOC1"/></w:pPr><w:r>${rPr}<w:fldChar w:fldCharType="end"/></w:r></w:p>`;
  return head + entries + tail;
}

function renderPageRefList(items) {
  const rPr = '<w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="24"/><w:szCs w:val="24"/></w:rPr>';
  return items.map(it =>
    `<w:p><w:pPr><w:pStyle w:val="ListEntry"/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs></w:pPr>` +
    `<w:hyperlink w:anchor="${it.bookmark}" w:history="1">` +
    makeRun(it.caption) +
    `<w:r>${rPr}<w:tab/></w:r>` +
    `<w:r>${rPr}<w:fldChar w:fldCharType="begin"/></w:r>` +
    `<w:r>${rPr}<w:instrText xml:space="preserve"> PAGEREF ${it.bookmark} \\h </w:instrText></w:r>` +
    `<w:r>${rPr}<w:fldChar w:fldCharType="separate"/></w:r>` +
    `<w:r>${rPr}<w:t>1</w:t></w:r>` +
    `<w:r>${rPr}<w:fldChar w:fldCharType="end"/></w:r>` +
    `</w:hyperlink></w:p>`
  ).join('');
}

function buildDocumentBody() {
  const { docStart, coverTbl, coverSectP } = extractCoverParts();
  let xml = coverTbl + coverSectP;

  for (const item of allSections) {
    if (item.pageBreakBefore && item.id !== 'sec_loi_mo_dau') {
      xml += pageBreakPara();
    }
    if (item.isToc) {
      xml += headingPara(item.title, 'TOCHeading', item.id);
      xml += renderTocField();
    } else if (item.isListOfFigures) {
      xml += headingPara(item.title, 'Heading1', item.id);
      xml += renderPageRefList(collectedFigures);
    } else if (item.isListOfTables) {
      xml += headingPara(item.title, 'Heading1', item.id);
      xml += renderPageRefList(collectedTables);
    } else if (item.isChapter) {
      xml += headingPara(item.title, 'Heading1', item.id);
      for (const sec of item.sections) {
        xml += headingPara(sec.title, 'Heading2', sec.id);
        xml += renderBlocks(sec.blocks);
      }
    } else {
      xml += headingPara(item.title, 'Heading1', item.id);
      xml += renderBlocks(item.blocks);
    }
  }

  const finalSectPr = '<w:sectPr>' +
    '<w:headerReference w:type="default" r:id="rHeaderBody"/>' +
    '<w:footerReference w:type="default" r:id="rFooterBody"/>' +
    '<w:pgSz w:w="11906" w:h="16838"/>' +
    '<w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1701" w:header="567" w:footer="567" w:gutter="0"/>' +
    '<w:pgNumType w:start="2"/>' +
    '<w:cols w:space="720"/>' +
    '</w:sectPr>';

  return `${docStart}${xml}${finalSectPr}</w:body></w:document>`;
}

function buildStylesXml() {
  const tnr = (sz, extra = '') =>
    `<w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="${sz}"/><w:szCs w:val="${sz}"/>${extra}</w:rPr>`;
  const consolas = (sz) =>
    `<w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas" w:eastAsia="Consolas" w:cs="Consolas"/><w:color w:val="000000"/><w:sz w:val="${sz}"/><w:szCs w:val="${sz}"/></w:rPr>`;
  const style = (id, name, pp, rp, base = 'Normal', extra = '') =>
    `<w:style w:type="paragraph" w:styleId="${id}"><w:name w:val="${name}"/>${base ? `<w:basedOn w:val="${base}"/>` : ''}${extra}<w:pPr>${pp}</w:pPr>${rp}</w:style>`;

  return `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="${W}">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/>
        <w:color w:val="000000"/>
        <w:sz w:val="26"/>
        <w:szCs w:val="26"/>
        <w:lang w:val="vi-VN" w:eastAsia="vi-VN"/>
      </w:rPr>
    </w:rPrDefault>
    <w:pPrDefault>
      <w:pPr><w:spacing w:after="100" w:line="360" w:lineRule="auto"/></w:pPr>
    </w:pPrDefault>
  </w:docDefaults>
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/><w:qFormat/>
    <w:pPr><w:ind w:firstLine="567"/><w:jc w:val="both"/><w:widowControl/></w:pPr>
  </w:style>
  <w:style w:type="character" w:default="1" w:styleId="DefaultParagraphFont">
    <w:name w:val="Default Paragraph Font"/><w:uiPriority w:val="1"/><w:semiHidden/><w:unhideWhenUsed/>
  </w:style>
  <w:style w:type="table" w:default="1" w:styleId="TableNormal">
    <w:name w:val="Normal Table"/><w:semiHidden/><w:unhideWhenUsed/>
    <w:tblPr><w:tblInd w:w="0" w:type="dxa"/></w:tblPr>
  </w:style>
  <w:style w:type="numbering" w:default="1" w:styleId="NoList">
    <w:name w:val="No List"/><w:semiHidden/><w:unhideWhenUsed/>
  </w:style>
  ${style('NormalNoIndent', 'NormalNoIndent', '<w:ind w:firstLine="0"/><w:jc w:val="both"/>', tnr(26))}
  ${style('Heading1', 'heading 1', '<w:keepNext/><w:keepLines/><w:spacing w:before="0" w:after="180"/><w:ind w:firstLine="0"/><w:jc w:val="left"/><w:outlineLvl w:val="0"/>', tnr(32, '<w:b/>'), 'Normal', '<w:qFormat/>')}
  ${style('Heading2', 'heading 2', '<w:keepNext/><w:keepLines/><w:spacing w:before="180" w:after="100"/><w:ind w:firstLine="0"/><w:jc w:val="left"/><w:outlineLvl w:val="1"/>', tnr(28, '<w:b/>'), 'Normal', '<w:qFormat/>')}
  ${style('Heading3', 'heading 3', '<w:keepNext/><w:keepLines/><w:spacing w:before="120" w:after="80"/><w:ind w:firstLine="0"/><w:jc w:val="left"/><w:outlineLvl w:val="2"/>', tnr(26, '<w:b/>'), 'Normal', '<w:qFormat/>')}
  ${style('TOCHeading', 'TOCHeading', '<w:keepNext/><w:keepLines/><w:spacing w:before="0" w:after="180"/><w:ind w:firstLine="0"/><w:jc w:val="left"/><w:outlineLvl w:val="9"/>', tnr(32, '<w:b/>'))}
  ${style('TOC1', 'toc 1', '<w:jc w:val="left"/><w:ind w:left="0" w:firstLine="0" w:right="0"/><w:spacing w:before="20" w:after="50" w:line="280" w:lineRule="auto"/><w:keepLines/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs>', tnr(24, '<w:b/>'))}
  ${style('TOC2', 'toc 2', '<w:jc w:val="left"/><w:ind w:left="340" w:firstLine="0" w:right="0"/><w:spacing w:before="0" w:after="40" w:line="270" w:lineRule="auto"/><w:keepLines/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs>', tnr(24))}
  ${style('ListEntry', 'ListEntry', '<w:jc w:val="left"/><w:ind w:left="0" w:firstLine="0" w:right="0"/><w:spacing w:before="0" w:after="45" w:line="270" w:lineRule="auto"/><w:keepLines/><w:tabs><w:tab w:val="right" w:leader="dot" w:pos="9071"/></w:tabs>', tnr(24))}
  ${style('TableCaption', 'TableCaption', '<w:keepNext/><w:spacing w:before="120" w:after="60" w:line="276" w:lineRule="auto"/><w:ind w:firstLine="0"/><w:jc w:val="left"/>', tnr(25, '<w:i/>'))}
  ${style('FigureCaption', 'FigureCaption', '<w:spacing w:before="60" w:after="140" w:line="276" w:lineRule="auto"/><w:ind w:firstLine="0"/><w:jc w:val="center"/>', tnr(25, '<w:i/>'))}
  ${style('TableText', 'TableText', '<w:spacing w:before="0" w:after="20" w:line="276" w:lineRule="auto"/><w:ind w:firstLine="0"/><w:jc w:val="left"/>', tnr(24))}
  ${style('Code', 'Code', '<w:keepLines/><w:pBdr><w:top w:val="single" w:sz="4" w:space="4" w:color="888888"/><w:left w:val="single" w:sz="4" w:space="6" w:color="888888"/><w:bottom w:val="single" w:sz="4" w:space="4" w:color="888888"/><w:right w:val="single" w:sz="4" w:space="6" w:color="888888"/><w:between w:val="single" w:sz="0" w:space="0" w:color="auto"/></w:pBdr><w:shd w:val="clear" w:color="auto" w:fill="F8F9FA"/><w:spacing w:before="0" w:after="0" w:line="260" w:lineRule="auto"/><w:ind w:left="140" w:right="140" w:firstLine="0"/><w:jc w:val="left"/>', consolas(20))}
  <w:style w:type="character" w:styleId="Hyperlink">
    <w:name w:val="Hyperlink"/><w:basedOn w:val="DefaultParagraphFont"/>
    <w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:u w:val="none"/></w:rPr>
  </w:style>
  <w:style w:type="character" w:styleId="FollowedHyperlink">
    <w:name w:val="FollowedHyperlink"/><w:basedOn w:val="DefaultParagraphFont"/>
    <w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:u w:val="none"/></w:rPr>
  </w:style>
</w:styles>`;
}

const crcTable = Array.from({ length: 256 }, (_, i) => {
  let c = i;
  for (let j = 0; j < 8; j++) c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
  return c >>> 0;
});

function crc32(data) {
  let c = 0xFFFFFFFF;
  for (const b of data) c = crcTable[(c ^ b) & 255] ^ (c >>> 8);
  return (c ^ 0xFFFFFFFF) >>> 0;
}

function buildZip(files) {
  let offset = 0;
  const locals = [];
  const central = [];
  for (const [name, content] of Object.entries(files)) {
    const n = Buffer.from(name, 'utf8');
    const data = Buffer.isBuffer(content) ? content : Buffer.from(content, 'utf8');
    const packed = zlib.deflateRawSync(data);
    const crc = crc32(data);
    const l = Buffer.alloc(30);
    l.writeUInt32LE(0x04034b50, 0);
    l.writeUInt16LE(20, 4);
    l.writeUInt16LE(0x800, 6);
    l.writeUInt16LE(8, 8);
    l.writeUInt32LE(crc, 14);
    l.writeUInt32LE(packed.length, 18);
    l.writeUInt32LE(data.length, 22);
    l.writeUInt16LE(n.length, 26);
    locals.push(l, n, packed);

    const c = Buffer.alloc(46);
    c.writeUInt32LE(0x02014b50, 0);
    c.writeUInt16LE(20, 4);
    c.writeUInt16LE(20, 6);
    c.writeUInt16LE(0x800, 8);
    c.writeUInt16LE(8, 10);
    c.writeUInt32LE(crc, 16);
    c.writeUInt32LE(packed.length, 20);
    c.writeUInt32LE(data.length, 24);
    c.writeUInt16LE(n.length, 28);
    c.writeUInt32LE(offset, 42);
    central.push(c, n);
    offset += l.length + n.length + packed.length;
  }
  const cd = Buffer.concat(central);
  const end = Buffer.alloc(22);
  end.writeUInt32LE(0x06054b50, 0);
  end.writeUInt16LE(Object.keys(files).length, 8);
  end.writeUInt16LE(Object.keys(files).length, 10);
  end.writeUInt32LE(cd.length, 12);
  end.writeUInt32LE(offset, 16);
  return Buffer.concat([...locals, cd, end]);
}

function buildMarkdownMirror() {
  const md = [];
  for (const item of allSections) {
    if (item.isToc || item.isListOfFigures || item.isListOfTables) {
      md.push(`# ${item.title}\n`);
      continue;
    }
    md.push(`# ${item.title}\n`);
    const dumpBlocks = (blocks) => {
      if (!blocks) return;
      for (const b of blocks) {
        if (b.kind === 'p') md.push(`${b.text}\n`);
        else if (b.kind === 'h3') md.push(`### ${b.text}\n`);
        else if (b.kind === 'code') md.push('```java\n' + b.text + '\n```\n');
        else if (b.kind === 'image') md.push(`![${b.caption}](../so_do/${b.name}.svg)\n`);
        else if (b.kind === 'table') {
          md.push(`*${b.caption}*\n`);
          md.push('| ' + b.head.join(' | ') + ' |');
          md.push('| ' + b.head.map(() => '---').join(' | ') + ' |');
          for (const r of b.rows) md.push('| ' + r.map(x => String(x).replace(/\n/g, ' ')).join(' | ') + ' |');
          md.push('');
        }
      }
    };
    if (item.isChapter) {
      for (const sec of item.sections) {
        md.push(`## ${sec.title}\n`);
        dumpBlocks(sec.blocks);
      }
    } else {
      dumpBlocks(item.blocks);
    }
  }
  return md.join('\n');
}

const documentXml = buildDocumentBody();
const stylesXml = buildStylesXml();
const coverLogoData = fs.readFileSync(path.join(BACKUP_UNZIPPED, 'word', 'media', 'image1.jpeg'));

const rels = [
  ['rStyles', 'styles', 'styles.xml'],
  ['rSettings', 'settings', 'settings.xml'],
  ['rHeaderEmpty', 'header', 'headerEmpty.xml'],
  ['rFooterEmpty', 'footer', 'footerEmpty.xml'],
  ['rHeaderBody', 'header', 'headerBody.xml'],
  ['rFooterBody', 'footer', 'footerBody.xml'],
  ['rIdCoverLogo', 'image', 'media/cover_logo.jpeg'],
  ...mediaEntries.map(m => [m.rid, 'image', `media/${m.name}`])
];

const emptyHeader = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:hdr xmlns:w="${W}"><w:p><w:pPr><w:ind w:firstLine="0"/></w:pPr></w:p></w:hdr>`;
const emptyFooter = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:ftr xmlns:w="${W}"><w:p><w:pPr><w:ind w:firstLine="0"/></w:pPr></w:p></w:ftr>`;
const bodyHeader = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:hdr xmlns:w="${W}"><w:p><w:pPr><w:pBdr><w:bottom w:val="single" w:sz="4" w:space="4" w:color="000000"/></w:pBdr><w:tabs><w:tab w:val="right" w:pos="9071"/></w:tabs><w:spacing w:after="70"/><w:ind w:firstLine="0"/><w:jc w:val="left"/></w:pPr>${makeRun('PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG', '<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="18"/>')}<w:r><w:tab/></w:r>${makeRun('NHÓM 4', '<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="18"/>')}</w:p></w:hdr>`;
const bodyFooter = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:ftr xmlns:w="${W}"><w:p><w:pPr><w:ind w:firstLine="0"/><w:jc w:val="right"/></w:pPr>${makeRun('Trang ', '<w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/>')}<w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/></w:rPr><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/></w:rPr><w:instrText xml:space="preserve"> PAGE </w:instrText></w:r><w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/></w:rPr><w:fldChar w:fldCharType="separate"/></w:r><w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/></w:rPr><w:t>2</w:t></w:r><w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman" w:cs="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="20"/></w:rPr><w:fldChar w:fldCharType="end"/></w:r></w:p></w:ftr>`;

const packageFiles = {
  '[Content_Types].xml': `<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Default Extension="png" ContentType="image/png"/><Default Extension="jpeg" ContentType="image/jpeg"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/><Override PartName="/word/settings.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml"/><Override PartName="/word/headerEmpty.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml"/><Override PartName="/word/footerEmpty.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"/><Override PartName="/word/headerBody.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml"/><Override PartName="/word/footerBody.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"/><Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/><Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/></Types>`,
  '_rels/.rels': `<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rDoc" Type="${R}/officeDocument" Target="word/document.xml"/><Relationship Id="rCore" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/><Relationship Id="rApp" Type="${R}/extended-properties" Target="docProps/app.xml"/></Relationships>`,
  'word/document.xml': documentXml,
  'word/styles.xml': stylesXml,
  'word/settings.xml': `<?xml version="1.0" encoding="UTF-8"?><w:settings xmlns:w="${W}"><w:zoom w:percent="100"/><w:defaultTabStop w:val="720"/><w:updateFields w:val="true"/><w:compat><w:compatSetting w:name="compatibilityMode" w:uri="http://schemas.microsoft.com/office/word" w:val="15"/></w:compat><w:themeFontLang w:val="vi-VN"/></w:settings>`,
  'word/_rels/document.xml.rels': `<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">${rels.map(([id, t, target]) => `<Relationship Id="${id}" Type="${R}/${t}" Target="${target}"/>`).join('')}</Relationships>`,
  'word/headerEmpty.xml': emptyHeader,
  'word/footerEmpty.xml': emptyFooter,
  'word/headerBody.xml': bodyHeader,
  'word/footerBody.xml': bodyFooter,
  'word/media/cover_logo.jpeg': coverLogoData,
  'docProps/core.xml': `<?xml version="1.0" encoding="UTF-8"?><cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><dc:title>Báo cáo BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4</dc:title><dc:subject>Hệ thống quản lý nhà hàng hải sản</dc:subject><dc:creator>Nhóm 4 – Khoa CNTT, Trường Đại học Giao thông Vận tải</dc:creator><dc:language>vi-VN</dc:language></cp:coreProperties>`,
  'docProps/app.xml': `<?xml version="1.0" encoding="UTF-8"?><Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"><Application>Microsoft Word</Application></Properties>`
};

for (const m of mediaEntries) {
  packageFiles[`word/media/${m.name}`] = m.data;
}

fs.mkdirSync(path.dirname(BUILT_DOCX), { recursive: true });
fs.writeFileSync(BUILT_DOCX, buildZip(packageFiles));
fs.writeFileSync(path.join(NGUON_DIR, 'BaoCao_noi_dung.md'), buildMarkdownMirror(), 'utf8');

console.log(JSON.stringify({
  output: BUILT_DOCX,
  figures: collectedFigures.length,
  tables: collectedTables.length,
  bytes: fs.statSync(BUILT_DOCX).size
}, null, 2));
