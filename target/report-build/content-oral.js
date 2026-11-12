const fs = require('fs');
const path = require('path');
module.exports=({page,p,h,code,table})=>{
 const input=fs.readFileSync(path.resolve(__dirname,'../../QuanLyNHHS/ON_TAP_BAO_VE_HOANG.md'),'utf8');
 const clean=s=>s.replace(/\[([^\]]+)\]\([^)]+\)/g,'$1').replace(/\*\*([^*]+)\*\*/g,'$1').replace(/`([^`]+)`/g,'$1');
 const sections=input.split(/^## /m);
 page('TRANG BÌA',{type:'cover',lines:[
  {text:'TÀI LIỆU ÔN BẢO VỆ',style:'CoverTitle',before:1000,after:250},
  {text:'NGUYỄN CAO HOÀNG',style:'CoverSubtitle',after:250},
  {text:'THỰC ĐƠN – NHÂN VIÊN – ĐĂNG NHẬP / BẢO MẬT',after:300},
  {text:'Dự án quản lý nhà hàng hải sản • Nhóm 4',after:200},
  {text:'Đối chiếu bảng phân công và mã nguồn ngày 10/11/2026',after:400},
  {text:'Học theo luồng xử lý; tự chỉ được vị trí trong code.',after:160},
  {text:'Ưu tiên: xem danh sách → thêm/sửa → chặn xóa → đăng nhập.',after:200}
 ],members:{headers:['Phạm vi','Nội dung'],rows:[['Tài liệu này','Hướng dẫn học, câu hỏi và demo cho Hoàng.'],['Báo cáo nhóm','Xem BTL.docx để trình bày nội dung chính thức.']],widths:[2200,6871]}});
 for(const section of sections.slice(1)) {
  const lines=section.trim().split(/\r?\n/);
  const title=lines.shift();
  const blocks=[];
  let paragraph=[], codeLines=null;
  function flush(){ if(paragraph.length){blocks.push(p(clean(paragraph.join(' '))));paragraph=[];} }
  for(let i=0;i<lines.length;i++){
   const line=lines[i];
   if(line.startsWith('```')) { if(codeLines!==null){blocks.push(code(codeLines.join('\n')));codeLines=null;}else{flush();codeLines=[];} continue; }
   if(codeLines!==null){codeLines.push(line);continue;}
   if(line.startsWith('### ')){flush();blocks.push(h(clean(line.slice(4))));continue;}
   if(line.startsWith('|')){
    flush();const rows=[];
    while(i<lines.length&&lines[i].startsWith('|')){
     const row=lines[i].replace(/^\||\|$/g,'').split('|').map(x=>clean(x.trim()));
     if(!row.every(x=>/^[-: ]+$/.test(x)))rows.push(row);
     i++;
    }i--;blocks.push(table('',rows[0],rows.slice(1)));continue;
   }
   if(!line.trim()){flush();continue;}
   if(/^\d+\. /.test(line)){flush();blocks.push(p(clean(line)));continue;}
   paragraph.push(line);
  }
  flush();
  // Long sections flow naturally; no invented fixed page numbers.
  page(clean(title),...blocks);
 }
};
