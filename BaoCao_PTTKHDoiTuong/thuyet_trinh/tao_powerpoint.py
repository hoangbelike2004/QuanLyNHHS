"""Tạo PowerPoint 12 slide có thể chỉnh sửa và PDF xem trước từ cùng một bố cục."""
from pathlib import Path
from math import atan2, cos, sin, pi
import json, re
from zipfile import ZipFile
from xml.etree import ElementTree as ET

from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE, MSO_CONNECTOR
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR, MSO_AUTO_SIZE
from pptx.oxml.xmlchemy import OxmlElement
from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.colors import HexColor
import fitz

ROOT=Path(__file__).resolve().parent
NAME='ThuyetTrinh_BTL_PTTKHDoiTuong_Nhom4'
W,H=13.333333,7.5
NAVY='193B55'; INK='203342'; MUTED='5A6B77'; BORDER='C8D4DD'
PALE='F0F4F7'; TEAL='2C6C73'; WHITE='FFFFFF'
SLIDES=[]

def slide(title,source,notes):
    s={'title':title,'source':source,'notes':notes,'items':[]}
    SLIDES.append(s)
    if len(SLIDES)>1:
        rect(s,.58,.50,.10,.44,NAVY,line=None)
        text(s,title,.84,.44,11.8,.62,30,bold=True,color=NAVY)
    text(s,'NHÓM 4  |  PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG',.62,7.12,9,.18,10,color=MUTED)
    text(s,f'{len(SLIDES):02d} / 12',11.72,7.08,.98,.23,11,color=MUTED,align='right')
    line(s,.62,6.98,12.70,6.98,BORDER,width=.6)
    if source:text(s,source,.64,6.75,11.8,.18,10,color=MUTED)
    return s

def text(s,value,x,y,w,h,size=22,bold=False,color=INK,align='left'):
    s['items'].append({'kind':'text','text':value,'x':x,'y':y,'w':w,'h':h,'size':size,'bold':bold,'color':color,'align':align})
def rect(s,x,y,w,h,fill=PALE,line=BORDER,round=False):
    s['items'].append({'kind':'rect','x':x,'y':y,'w':w,'h':h,'fill':fill,'line':line,'round':round})
def ellipse(s,x,y,w,h,fill=WHITE,line=BORDER):
    s['items'].append({'kind':'ellipse','x':x,'y':y,'w':w,'h':h,'fill':fill,'line':line})
def line(s,x1,y1,x2,y2,color=MUTED,width=1.2,arrow=False,dashed=False):
    s['items'].append({'kind':'line','x':x1,'y':y1,'x2':x2,'y2':y2,'color':color,'width':width,'arrow':arrow,'dashed':dashed})
def bullet(s,value,x,y,w=5.3,size=23):
    ellipse(s,x,y+.13,.085,.085,TEAL,TEAL)
    text(s,value,x+.23,y,w-.23,1.0,size)
def card(s,x,y,w,h,title,body,ts=23,bs=21):
    rect(s,x,y,w,h,PALE,line=None,round=True)
    text(s,title,x+.25,y+.22,w-.5,.68,ts,bold=True,color=NAVY)
    text(s,body,x+.25,y+1.02,w-.5,h-1.18,bs)
def person(s,x,y,label):
    ellipse(s,x+.14,y,.24,.24,WHITE,NAVY)
    line(s,x+.26,y+.24,x+.26,y+.64,NAVY)
    line(s,x,y+.39,x+.52,y+.39,NAVY)
    line(s,x+.26,y+.64,x+.03,y+.91,NAVY)
    line(s,x+.26,y+.64,x+.49,y+.91,NAVY)
    text(s,label,x+.75,y+.25,2.2,.48,22,bold=True,color=NAVY)
def concept(s,x,y,title,attr):
    rect(s,x,y,2.50,1.35,WHITE,BORDER)
    rect(s,x,y,2.50,.79,PALE,line=None)
    text(s,title,x+.10,y+.08,2.30,.70,20,bold=True,color=NAVY,align='center')
    text(s,attr,x+.08,y+.94,2.34,.27,16,align='center')
def relation(s,x1,x2,y,left='1',right='0..*',composition=False):
    line(s,x1,y,x2,y,NAVY)
    if composition:
        s['items'].append({'kind':'diamond','x':x1,'y':y-.06,'w':.12,'h':.12,'fill':NAVY,'line':NAVY})
    text(s,left,x1-.02,y-.36,.4,.24,15,align='left')
    text(s,right,x2-.47,y-.36,.45,.24,15,align='right')

s=slide('Hệ thống quản lý nhà hàng hải sản','',
 'Kính chào thầy/cô và các bạn. Nhóm 4 trình bày đề tài Hệ thống quản lý nhà hàng hải sản trong học phần Phân tích và thiết kế hướng đối tượng. Bài trình bày tập trung vào cách chuyển yêu cầu nghiệp vụ thành mô hình và thiết kế, minh họa bằng hóa đơn, thanh toán và nhập kho. Thời lượng gợi ý cho toàn bài khoảng 8–10 phút.')
text(s,'TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI',.70,.58,12,.32,16,bold=True,color=NAVY)
text(s,'KHOA CÔNG NGHỆ THÔNG TIN',.70,1.03,11,.30,15,color=MUTED)
rect(s,.72,1.82,.12,2.10,TEAL,line=None)
text(s,'BÁO CÁO BÀI TẬP LỚN',1.06,1.83,11,.38,19,color=MUTED)
text(s,'HỆ THỐNG QUẢN LÝ\nNHÀ HÀNG HẢI SẢN',1.03,2.40,11.8,1.50,38,bold=True,color=NAVY)
text(s,'Phân tích và thiết kế hướng đối tượng',1.06,4.20,11.5,.44,25)
text(s,'NHÓM 4',1.06,5.20,2,.34,18,bold=True,color=TEAL)
text(s,'Hoàng Việt Đức  •  Nguyễn Cao Hoàng\nĐỗ Minh Chiến  •  Lý Tiến Đạt',1.06,5.73,11,.72,21)

s=slide('Bài toán và mục tiêu','Báo cáo: mục 1.1–1.2.',
 'Nhà hàng có nhiều thông tin liên quan: khách đặt bàn, món được gọi, hóa đơn cần thu và nguyên liệu nhập. Nếu ghi rời rạc, việc đối chiếu tiền hoặc lượng nhập dễ sai. Mục tiêu là quản lý tập trung và giữ các giao dịch nhất quán. Phạm vi là một nhà hàng, chưa mở rộng sang cổng ngân hàng, xuất kho hay điều phối bếp.')
text(s,'Vấn đề',.85,1.66,5.5,.46,25,bold=True,color=NAVY)
bullet(s,'Thông tin bàn, món và\nchứng từ còn rời rạc.',.88,2.42,5.3)
bullet(s,'Khó đối chiếu tiền bán\nvà lượng nguyên liệu nhập.',.88,3.86,5.3)
rect(s,6.70,1.58,5.83,3.95,PALE,line=None,round=True)
text(s,'Mục tiêu',7.02,1.85,5,.46,25,bold=True,color=NAVY)
bullet(s,'Quản lý tập trung.',7.05,2.65,4.9)
bullet(s,'Tính tiền chính xác.',7.05,3.48,4.9)
bullet(s,'Dữ liệu nhất quán.',7.05,4.31,4.9)
text(s,'Phạm vi: đặt bàn • bán hàng • nhập kho • báo cáo',.90,5.98,11.8,.43,23,color=TEAL)

s=slide('Ai sử dụng hệ thống?','Báo cáo: mục 1.4–1.6. Biểu đồ Use Case rút gọn.',
 'Bốn vai trò nghiệp vụ là phục vụ, thu ngân, thủ kho và quản lý. Mỗi ca sử dụng thể hiện mục tiêu của vai trò: đặt bàn, lập hóa đơn, thanh toán, nhập kho hoặc xem số liệu. Khách hàng chưa trực tiếp thao tác ứng dụng nên không vẽ thành actor ở đây. Các nghiệp vụ cần đăng nhập; website hiện vẫn dùng ROLE_USER chung, chưa giới hạn từng chức năng theo bốn vai trò.')
rect(s,4.20,1.54,8.45,4.78,WHITE,BORDER)
text(s,'HỆ THỐNG NỘI BỘ',4.45,1.65,7.9,.29,16,bold=True,color=MUTED,align='center')
for y,label in [(2.02,'Phục vụ'),(3.08,'Thu ngân'),(4.14,'Thủ kho'),(5.20,'Quản lý')]:person(s,.83,y,label)
def uc(x,y,w,label):
    ellipse(s,x,y,w,.75,PALE,BORDER)
    text(s,label,x+.12,y+.20,w-.24,.39,20,align='center')
uc(6.20,2.05,4.40,'Tiếp nhận đặt bàn')
uc(4.62,3.11,3.40,'Lập hóa đơn')
uc(8.70,3.11,3.40,'Thanh toán')
uc(5.10,4.17,6.60,'Lập, điều chỉnh phiếu nhập')
uc(4.62,5.23,3.40,'Quản lý thực đơn')
uc(8.70,5.23,3.40,'Xem báo cáo')
line(s,3.63,2.43,6.20,2.43)
line(s,3.63,3.49,4.62,3.49)
line(s,3.63,3.49,8.70,3.49) # chuyển đường thứ hai thành nhánh đi dưới oval đầu
# Tách tuyến liên kết để không xuyên qua use case lập hóa đơn.
s['items'].pop()
line(s,3.63,3.49,4.04,3.49)
line(s,4.04,3.49,4.04,3.98)
line(s,4.04,3.98,10.40,3.98)
line(s,10.40,3.98,10.40,3.86)
line(s,3.63,4.55,5.10,4.55)
line(s,3.63,5.61,4.62,5.61)
line(s,3.63,5.61,4.04,5.61)
line(s,4.04,5.61,4.04,6.12)
line(s,4.04,6.12,10.40,6.12)
line(s,10.40,6.12,10.40,5.98)
text(s,'Các nghiệp vụ yêu cầu đăng nhập.',.85,6.44,11.6,.27,18,color=MUTED)

s=slide('Mô hình khái niệm: các đối tượng chính','Báo cáo: mục 2.1–2.3. Chỉ trình bày các quan hệ cốt lõi.',
 'Đây là mô hình khái niệm, tập trung vào ý nghĩa nghiệp vụ, chưa đưa khóa kỹ thuật hay framework vào. Hóa đơn gồm các dòng món; phiếu nhập gồm các dòng nguyên liệu. Cần lớp chi tiết vì quan hệ có lượng và đơn giá riêng. Một chứng từ hợp lệ có ít nhất một dòng, thể hiện bằng bội số 1..*. Khách và nhân viên cùng các quan hệ còn lại có trong mô hình đầy đủ của báo cáo.')
text(s,'Bán hàng',.70,1.55,4,.32,20,bold=True,color=TEAL)
xs=[.65,3.82,6.99,10.16]
for x,title,attr in zip(xs,['Bàn ăn','Hóa đơn','Chi tiết\nhóa đơn','Món ăn'],['Tên • Số chỗ','Ngày • Tổng tiền','Lượng • Giá bán','Tên • Đơn vị']):concept(s,x,2.17,title,attr)
relation(s,3.15,3.82,2.85)
relation(s,6.32,6.99,2.85,'1','1..*',True)
relation(s,9.49,10.16,2.85,'0..*','1')
text(s,'Nhập kho',.70,4.06,4,.32,20,bold=True,color=TEAL)
for x,title,attr in zip(xs,['Nhà cung cấp','Phiếu nhập','Chi tiết\nphiếu nhập','Nguyên liệu'],['Tên • Liên hệ','Ngày • Tổng tiền','Lượng • Giá nhập','Tên • Tồn kho']):concept(s,x,4.68,title,attr)
relation(s,3.15,3.82,5.36)
relation(s,6.32,6.99,5.36,'1','1..*',True)
relation(s,9.49,10.16,5.36,'0..*','1')
text(s,'Lớp chi tiết giữ số lượng và đơn giá của từng chứng từ.',.70,6.34,12,.33,21,bold=True,color=NAVY)

s=slide('Kiến trúc và phân chia trách nhiệm','Báo cáo: mục 2.4 và 3.1.',
 'Ứng dụng tổ chức theo Controller, Service và Repository. Controller tiếp nhận yêu cầu; Service kiểm tra quy tắc và điều phối giao dịch; Repository đọc ghi dữ liệu qua các interface. Entity giữ dữ liệu ánh xạ xuống bảng. Cách chia này giúp thay quy tắc tiền hoặc tồn tại Service mà không phải sửa nhiều trang giao diện. Công nghệ hiện dùng là Spring Boot, Thymeleaf và Oracle.')
text(s,'Giao diện gửi yêu cầu → xử lý nghiệp vụ → lưu dữ liệu',.78,1.63,12,.44,23,color=MUTED)
nodes=[(.70,'Controller','Nhận yêu cầu'),(3.82,'Service','Xử lý nghiệp vụ'),(6.94,'Repository','Đọc / ghi dữ liệu'),(10.06,'Oracle','Lưu dữ liệu')]
for x,title,body in nodes:card(s,x,2.62,2.57,2.12,title,body,ts=22,bs=19)
for x in [3.27,6.39,9.51]:line(s,x,3.68,x+.52,3.68,NAVY,arrow=True)
text(s,'Entity: dữ liệu và ánh xạ bảng.',.85,5.35,11.8,.42,23)
text(s,'Quy tắc tiền và tồn kho được xử lý tại Service.',.85,6.08,11.8,.42,23,bold=True,color=TEAL)

s=slide('UC04 — Lập hóa đơn','Báo cáo: mục 1.9 và 3.2. Số tiền là ví dụ minh họa.',
 'Thu ngân chọn bàn và món, nhập lượng và giảm giá. Hệ thống kiểm tra đầu vào, gộp các dòng trùng món rồi lấy giá từ dữ liệu máy chủ. Thành tiền bằng lượng nhân giá; tổng phải thu bằng tổng dòng trừ số tiền giảm và không nhỏ hơn 0. Phần đầu và chi tiết nằm trong cùng giao dịch. Ở ví dụ, tổng món 720 nghìn, giảm 20 nghìn, còn 700 nghìn đồng.')
steps=['Chọn bàn\nvà món','Kiểm tra,\ngộp món trùng','Lấy giá\ntừ hệ thống','Tính tổng\nvà lưu']
for i,(x,label) in enumerate(zip([.70,3.82,6.94,10.06],steps),1):
    text(s,f'0{i}',x,1.82,2.57,.42,22,bold=True,color=TEAL)
    rect(s,x,2.52,2.57,1.48,PALE,line=None,round=True)
    text(s,label,x+.14,2.78,2.29,1.0,22,bold=True,color=NAVY,align='center')
for x in [3.27,6.39,9.51]:line(s,x,3.26,x+.52,3.26,NAVY,arrow=True)
rect(s,.85,4.68,11.63,1.10,PALE,line=None,round=True)
text(s,'720.000 − 20.000 = 700.000 đồng',1.03,4.98,11.27,.51,31,bold=True,color=NAVY,align='center')
text(s,'Tổng món − giảm giá = số tiền phải thu',.85,6.02,11.63,.41,23,align='center')

s=slide('UC05 — Các lớp phối hợp khi thanh toán','Báo cáo: mục 3.3. Sequence Diagram rút gọn; lược nhánh lỗi và lần tải lặp.',
 'Biểu đồ thể hiện sự phối hợp của bốn đối tượng. Thu ngân gửi yêu cầu tới Controller; Controller gọi thanhToan của Service. Service tải hóa đơn, kiểm tra chưa thanh toán và có món, tính lại tổng rồi lưu trạng thái. Sau đó kết quả quay về Controller và hiển thị cho người dùng. Nếu đã thanh toán hoặc không có món thì Service từ chối. Biểu đồ đã lược nhánh lỗi, các lần đọc lặp và chi tiết từng Repository để dễ trình bày.')
centers=[1.65,4.67,7.88,11.43]
for x,label in zip(centers,['Thu ngân','HoaDon\nController','HoaDon\nService','Repository']):
    rect(s,x-.87,1.62,1.74,.83,PALE,BORDER)
    text(s,label,x-.81,1.78,1.62,.60,18,bold=True,color=NAVY,align='center')
    line(s,x,2.45,x,6.38,BORDER,width=1,dashed=True)
def msg(a,b,y,label,back=False):
    x1,x2=centers[a],centers[b]
    line(s,x1,y,x2,y,NAVY,arrow=True,dashed=back)
    text(s,label,min(x1,x2)+.04,y-.30,abs(x1-x2)-.08,.28,17,color=NAVY,align='center')
msg(0,1,2.98,'1. Yêu cầu thanh toán')
msg(1,2,3.52,'2. thanhToan(maHd)')
msg(2,3,4.06,'3. Đọc hóa đơn, chi tiết')
msg(3,2,4.60,'4. Trả dữ liệu',True)
msg(2,3,5.14,'5. Lưu tổng và trạng thái')
msg(2,1,5.68,'6. Trả kết quả',True)
msg(1,0,6.22,'7. Thông báo kết quả',True)
text(s,'Điều kiện: hóa đơn chưa thanh toán và đã có món.',.85,6.48,11.7,.28,18,bold=True,color=TEAL)

s=slide('UC06 — Điều chỉnh phiếu nhập và tồn kho','Báo cáo: mục 1.11, 3.4 và 3.13. Số lượng là ví dụ minh họa.',
 'Điểm quan trọng khi sửa phiếu là điều chỉnh theo chênh lệch. Tồn ban đầu 10 kg; nhập 5 kg thành 15 kg. Nếu sửa lượng trên phiếu từ 5 xuống 3 kg thì tồn là 15 cộng 3 trừ 5, bằng 13 kg. Service khóa dữ liệu, kiểm tra phiên bản form và ghi phiếu, chi tiết, tồn trong một giao dịch. Nhờ đó không cộng lại toàn bộ lượng mới hoặc ghi đè bằng form đã cũ.')
for x,title,formula,desc in [(.75,'Tồn ban đầu','10 kg','Chưa nhập hàng'),(4.92,'Nhập 5 kg','15 kg','10 + 5'),(9.09,'Sửa 5 → 3 kg','13 kg','15 + 3 − 5')]:
    rect(s,x,1.84,3.50,2.66,PALE,line=None,round=True)
    text(s,title,x+.16,2.10,3.18,.40,23,bold=True,color=NAVY,align='center')
    text(s,formula,x+.16,2.88,3.18,.63,36,bold=True,color=TEAL,align='center')
    text(s,desc,x+.16,3.86,3.18,.36,21,align='center')
text(s,'Tồn mới = tồn hiện tại + lượng mới − lượng cũ',.75,5.03,11.84,.47,26,bold=True,color=NAVY,align='center')
text(s,'Kiểm tra phiên bản • Khóa dữ liệu • Cùng giao dịch',.75,6.00,11.84,.42,23,align='center')

s=slide('Vòng đời hóa đơn','Báo cáo: mục 3.7. State Machine rút gọn theo HoaDonService.',
 'Hóa đơn được tạo ở trạng thái chưa thanh toán. Khi còn mở, thu ngân có thể sửa hoặc xóa theo điều kiện nghiệp vụ. Khi đã có món và được tính lại tổng, thao tác thanh toán chuyển sang trạng thái đã thanh toán. Từ đây Service từ chối sửa, xóa và thanh toán lại. Đây là kiểm tra theo trạng thái; việc chống hai yêu cầu đồng thời vẫn cần bổ sung khóa hoặc kiểm soát phiên bản.')
rect(s,1.02,2.20,4.00,1.42,PALE,NAVY,round=True)
rect(s,8.30,2.20,4.00,1.42,PALE,NAVY,round=True)
text(s,'Chưa thanh toán',1.18,2.70,3.68,.46,26,bold=True,color=NAVY,align='center')
text(s,'Đã thanh toán',8.46,2.70,3.68,.46,26,bold=True,color=NAVY,align='center')
line(s,5.02,2.93,8.30,2.93,NAVY,width=1.6,arrow=True)
text(s,'Có món, tính lại tổng',5.10,2.25,3.10,.38,19,align='center')
text(s,'thanhToan()',5.15,3.16,3.0,.32,20,bold=True,color=TEAL,align='center')
text(s,'Cho phép sửa / xóa',1.02,4.22,4.0,.47,24,align='center')
text(s,'Từ chối sửa / xóa',8.30,4.22,4.0,.47,24,align='center')
text(s,'Thanh toán lại bị từ chối.',.95,5.77,11.7,.48,26,bold=True,color=TEAL,align='center')

s=slide('DCD rút gọn: mỗi lớp có trách nhiệm rõ','Báo cáo: mục 3.5 và 3.9. Chỉ hiện các thao tác liên quan đến hóa đơn.',
 'DCD làm rõ phương thức nằm ở lớp nào. Controller nhận thao tác từ giao diện. HoaDonService chứa các phương thức tạo, thanh toán và tính lại tổng tiền. Repository cung cấp thao tác tìm và lưu, trong đó các phương thức cơ bản được kế thừa từ JpaRepository. Quyết định này thể hiện Controller, High Cohesion và SRP: các quy tắc tiền tập trung tại Service, không rải vào giao diện hoặc lớp lưu trữ.')
classes=[(.65,3.48,'HoaDonController',['+ thanhToan(...)']),(4.58,4.10,'HoaDonService',['+ taoHoaDon(...)','+ thanhToan(...)','+ tinhLaiTongTien(...)']),(9.13,3.56,'HoaDonRepository',['+ findById(...)','+ save(...)'])]
for x,w,title,methods in classes:
    rect(s,x,2.14,w,2.57,WHITE,BORDER)
    rect(s,x,2.14,w,.80,PALE,line=None)
    text(s,title,x+.10,2.39,w-.20,.42,20,bold=True,color=NAVY,align='center')
    line(s,x,2.94,x+w,2.94,BORDER)
    text(s,'\n'.join(methods),x+.18,3.25,w-.36,1.30,19)
line(s,4.13,3.50,4.58,3.50,NAVY,arrow=True)
line(s,8.68,3.50,9.13,3.50,NAVY,arrow=True)
text(s,'Tách tiếp nhận, nghiệp vụ và lưu trữ.',.85,5.36,11.7,.47,26,bold=True,color=TEAL)
text(s,'Quy tắc tiền tập trung ở Service; Repository chỉ đọc / ghi.',.85,6.08,11.7,.42,22)

s=slide('Kết quả kiểm chứng và giới hạn','Báo cáo: mục 4.5–4.8. Phân biệt đọc mã nguồn với kiểm thử thực thi.',
 'Nhóm đã có bộ mô hình đối chiếu với mã nguồn. Trong lần lập báo cáo, 4 kiểm thử JavaScript về tìm kiếm và bộ lọc đã chạy đạt; chưa chạy lại bộ Java và Oracle. Vì vậy không dùng 4 kiểm thử này để kết luận giao dịch chứng từ đã được kiểm thử đầy đủ. Ba giới hạn cần nói rõ là ROLE_USER dùng chung, thanh toán chưa tự giải phóng bàn và bán món chưa tự trừ nguyên liệu. Các điểm này được đưa vào hướng hoàn thiện.')
text(s,'Đã kiểm chứng',.85,1.64,5.6,.46,25,bold=True,color=NAVY)
bullet(s,'Đối chiếu mô hình\nvới mã nguồn.',.88,2.44,5.4,22)
bullet(s,'4/4 kiểm thử JavaScript đạt.',.88,3.75,5.4,22)
bullet(s,'Java / Oracle: chưa chạy lại.',.88,4.85,5.4,22)
rect(s,6.70,1.57,5.85,4.82,PALE,line=None,round=True)
text(s,'Giới hạn hiện tại',7.03,1.87,5.1,.46,25,bold=True,color=NAVY)
bullet(s,'Phân quyền còn dùng\nROLE_USER chung.',7.05,2.68,4.95,21)
bullet(s,'Thanh toán chưa tự\ngiải phóng bàn.',7.05,3.88,4.95,21)
bullet(s,'Chưa trừ nguyên liệu\nkhi bán món.',7.05,5.08,4.95,21)

s=slide('Kết luận và hướng hoàn thiện','Nguồn: báo cáo BTL Nhóm 4, hướng dẫn học phần, bảng phân công và mã nguồn QuanLyNHHS.',
 'Đề tài nối kết yêu cầu đặt bàn, bán hàng và nhập kho thành các mô hình nhất quán. Bài học chính là xác định đúng đối tượng, phân chia trách nhiệm và bảo vệ tính toàn vẹn của giao dịch. Hướng tiếp theo là hoàn thiện phân quyền, xử lý xung đột và chạy đầy đủ các test Java/Oracle. Nhóm xin cảm ơn thầy/cô và các bạn đã lắng nghe.')
text(s,'Nối kết đặt bàn, hóa đơn và nhập kho\ntrong một hệ thống quản lý.',.88,1.67,11.7,1.12,28,bold=True,color=NAVY)
bullet(s,'Yêu cầu rõ → mô hình thống nhất.',.90,3.26,11.8,24)
bullet(s,'Trách nhiệm nghiệp vụ tập trung tại Service.',.90,4.13,11.8,24)
bullet(s,'Hoàn thiện phân quyền và kiểm thử Java / Oracle.',.90,5.00,11.8,24)
text(s,'Cảm ơn thầy/cô và các bạn!',.90,6.12,11.6,.47,26,bold=True,color=TEAL)

assert len(SLIDES)==12

def rgb(hexstr):return RGBColor.from_string(hexstr)

def render_pptx():
    prs=Presentation();prs.slide_width=Inches(W);prs.slide_height=Inches(H)
    prs.core_properties.title='Hệ thống quản lý nhà hàng hải sản – Nhóm 4'
    prs.core_properties.author='Nhóm 4 – Khoa Công nghệ thông tin – Trường Đại học Giao thông Vận tải'
    prs.core_properties.subject='Bài tập lớn Phân tích và thiết kế hướng đối tượng'
    for data in SLIDES:
        sl=prs.slides.add_slide(prs.slide_layouts[6])
        sl.background.fill.solid();sl.background.fill.fore_color.rgb=rgb(WHITE)
        for a in data['items']:
            kind=a['kind']
            if kind=='text':
                sh=sl.shapes.add_textbox(Inches(a['x']),Inches(a['y']),Inches(a['w']),Inches(a['h']))
                tf=sh.text_frame;tf.clear();tf.word_wrap=False;tf.auto_size=MSO_AUTO_SIZE.NONE
                tf.margin_left=tf.margin_right=tf.margin_top=tf.margin_bottom=0
                tf.vertical_anchor=MSO_ANCHOR.TOP
                for i,value in enumerate(a['text'].split('\n')):
                    p=tf.paragraphs[0] if i==0 else tf.add_paragraph()
                    p.alignment={'left':PP_ALIGN.LEFT,'center':PP_ALIGN.CENTER,'right':PP_ALIGN.RIGHT}[a['align']]
                    p.space_before=p.space_after=Pt(0)
                    p.line_spacing=Pt(a['size']*1.20)
                    r=p.add_run();r.text=value;r.font.name='Arial';r.font.size=Pt(a['size']);r.font.bold=a['bold'];r.font.color.rgb=rgb(a['color'])
                    rpr=r._r.get_or_add_rPr();rpr.set('lang','vi-VN')
                    for tag in ['ea','cs']:
                        f=OxmlElement('a:'+tag);f.set('typeface','Arial');rpr.append(f)
            elif kind in ['rect','ellipse','diamond']:
                st=MSO_SHAPE.DIAMOND if kind=='diamond' else MSO_SHAPE.OVAL if kind=='ellipse' else MSO_SHAPE.ROUNDED_RECTANGLE if a.get('round') else MSO_SHAPE.RECTANGLE
                sh=sl.shapes.add_shape(st,Inches(a['x']),Inches(a['y']),Inches(a['w']),Inches(a['h']))
                sh.fill.solid();sh.fill.fore_color.rgb=rgb(a['fill'])
                if a.get('line'):
                    sh.line.color.rgb=rgb(a['line']);sh.line.width=Pt(.9)
                else:sh.line.fill.background()
                if a.get('round'):
                    try:sh.adjustments[0]=.10
                    except Exception:pass
            elif kind=='line':
                sh=sl.shapes.add_connector(MSO_CONNECTOR.STRAIGHT,Inches(a['x']),Inches(a['y']),Inches(a['x2']),Inches(a['y2']))
                sh.line.color.rgb=rgb(a['color']);sh.line.width=Pt(a['width'])
                ln=sh._element.spPr.find('{http://schemas.openxmlformats.org/drawingml/2006/main}ln')
                if a['dashed']:
                    dash=OxmlElement('a:prstDash');dash.set('val','dash');ln.append(dash)
                if a['arrow']:
                    end=OxmlElement('a:tailEnd');end.set('type','triangle');end.set('w','sm');end.set('len','sm');ln.append(end)
        sl.notes_slide.notes_text_frame.text=data['notes']
    dest=ROOT/(NAME+'.pptx');prs.save(dest)
    return dest

FONT_DIR=Path('/System/Library/Fonts/Supplemental')
pdfmetrics.registerFont(TTFont('AR',str(FONT_DIR/'Arial.ttf')))
pdfmetrics.registerFont(TTFont('AR-B',str(FONT_DIR/'Arial Bold.ttf')))

def render_pdf():
    c=canvas.Canvas(str(ROOT/(NAME+'.pdf')),pagesize=(W*72,H*72))
    c.setTitle('Thuyết trình BTL Phân tích và thiết kế hướng đối tượng – Nhóm 4')
    c.setAuthor('Nhóm 4')
    for i,data in enumerate(SLIDES,1):
        c.bookmarkPage('slide'+str(i));c.addOutlineEntry(data['title'],'slide'+str(i),level=0)
        for a in data['items']:
            kind=a['kind']
            if kind=='text':
                font='AR-B' if a['bold'] else 'AR';size=a['size'];c.setFont(font,size);c.setFillColor(HexColor('#'+a['color']))
                # Align PDF baselines with native PowerPoint text boxes.
                for k,value in enumerate(a['text'].split('\n')):
                    xx=a['x']*72;yy=H*72-a['y']*72-size*.95-k*size*1.20
                    if a['align']=='center':c.drawCentredString(xx+a['w']*36,yy,value)
                    elif a['align']=='right':c.drawRightString(xx+a['w']*72,yy,value)
                    else:c.drawString(xx,yy,value)
            elif kind in ['rect','ellipse','diamond']:
                x,y,w,h=a['x']*72,(H-a['y']-a['h'])*72,a['w']*72,a['h']*72
                c.setFillColor(HexColor('#'+a['fill']))
                if a.get('line'):c.setStrokeColor(HexColor('#'+a['line']));c.setLineWidth(.9)
                stroke=int(bool(a.get('line')))
                if kind=='diamond':
                    p=c.beginPath();p.moveTo(x,y+h/2);p.lineTo(x+w/2,y+h);p.lineTo(x+w,y+h/2);p.lineTo(x+w/2,y);p.close();c.drawPath(p,fill=1,stroke=stroke)
                elif kind=='ellipse':c.ellipse(x,y,x+w,y+h,fill=1,stroke=stroke)
                elif a.get('round'):c.roundRect(x,y,w,h,8,fill=1,stroke=stroke)
                else:c.rect(x,y,w,h,fill=1,stroke=stroke)
            elif kind=='line':
                x1,y1,x2,y2=a['x']*72,(H-a['y'])*72,a['x2']*72,(H-a['y2'])*72
                c.setStrokeColor(HexColor('#'+a['color']));c.setLineWidth(a['width'])
                c.setDash([4,3] if a['dashed'] else []);c.line(x1,y1,x2,y2);c.setDash([])
                if a['arrow']:
                    angle=atan2(y2-y1,x2-x1);l=7
                    q=c.beginPath();q.moveTo(x2,y2)
                    q.lineTo(x2-l*cos(angle-pi/7),y2-l*sin(angle-pi/7))
                    q.lineTo(x2-l*cos(angle+pi/7),y2-l*sin(angle+pi/7));q.close()
                    c.setFillColor(HexColor('#'+a['color']));c.drawPath(q,fill=1,stroke=0)
        c.showPage()
    c.save()

def validate(pptx_path):
    errs=[]
    for i,data in enumerate(SLIDES,1):
        for a in data['items']:
            if a['kind']=='text':
                font='AR-B' if a['bold'] else 'AR'
                for value in a['text'].split('\n'):
                    need=pdfmetrics.stringWidth(value,font,a['size'])
                    if need>a['w']*72+1:errs.append(f'Slide {i}: chữ vượt chiều ngang: {value}')
                need_h=len(a['text'].split('\n'))*a['size']*1.20
                # Một dòng được đặt theo font em box, không theo toàn khoảng cách dòng.
                if len(a['text'].split('\n'))>1 and need_h>a['h']*72+2:
                    errs.append(f'Slide {i}: khung thấp: {a["text"]}')
            if a['kind']!='line':
                if a['x']<0 or a['y']<0 or a['x']+a['w']>W+.01 or a['y']+a['h']>H+.01:errs.append(f'Slide {i}: phần tử ngoài khung')
    z=ZipFile(pptx_path);assert z.testzip() is None
    ns={'p':'http://schemas.openxmlformats.org/presentationml/2006/main'}
    xmls=[n for n in z.namelist() if re.fullmatch(r'ppt/slides/slide\d+.xml',n)]
    assert len(xmls)==12
    for n in xmls:
        tree=ET.fromstring(z.read(n))
        assert not tree.findall('.//p:transition',ns)
        assert not tree.findall('.//p:timing',ns)
    assert len([n for n in z.namelist() if re.fullmatch(r'ppt/notesSlides/notesSlide\d+.xml',n)])==12
    pdf=fitz.open(ROOT/(NAME+'.pdf'));assert len(pdf)==12
    report={'slides':12,'aspect_ratio':'16:9','animations':0,'transitions':0,'speaker_notes':12,'native_editable_shapes':True,'layout_issues':errs,'preview_pdf':'12 trang từ cùng bố cục nguồn; chưa render bằng Microsoft PowerPoint.'}
    (ROOT/'kiem_tra_slide.json').write_text(json.dumps(report,ensure_ascii=False,indent=2))
    if errs:raise RuntimeError('\n'.join(errs))
    out=ROOT/'xem_truoc';out.mkdir(exist_ok=True)
    for i,pg in enumerate(pdf,1):pg.get_pixmap(matrix=fitz.Matrix(1.35,1.35)).save(out/f'slide_{i:02}.png')
    return report

if __name__=='__main__':
    pptx_path=render_pptx();render_pdf()
    (ROOT/'noi_dung_slide.json').write_text(json.dumps(SLIDES,ensure_ascii=False,indent=2))
    lines=['# Gợi ý thuyết trình – Nhóm 4','', '12 slide, thời lượng gợi ý khoảng 8–10 phút. Lời dẫn cũng có trong phần Notes của PowerPoint.','']
    for i,d in enumerate(SLIDES,1):lines += [f'## Slide {i}. {d["title"]}','',d['notes'],'']
    (ROOT/'Goi_y_thuyet_trinh.md').write_text('\n'.join(lines))
    print(json.dumps(validate(pptx_path),ensure_ascii=False,indent=2))
