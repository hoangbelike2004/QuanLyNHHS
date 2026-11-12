module.exports = ({page,p,h,code,table,note,schemas}) => {
page('TRANG BÌA', {type:'cover',lines:[
 {text:'TRƯỜNG: ........................................................................',before:100,after:130},
 {text:'KHOA / BỘ MÔN: ............................................................',after:420},
 {text:'BÁO CÁO BÀI TẬP LỚN',style:'CoverTitle',after:180},
 {text:'ĐỀ TÀI',style:'CoverSubtitle',after:120},
 {text:'XÂY DỰNG HỆ THỐNG QUẢN LÝ\nNHÀ HÀNG HẢI SẢN',style:'CoverTitle',after:220},
 {text:'Ứng dụng Java Spring Boot, Thymeleaf và Oracle Database',after:360},
 {text:'Môn học: ........................................................................',after:150},
 {text:'Giảng viên hướng dẫn: ...................................................',after:150},
 {text:'Lớp: ....................................    Nhóm: 4',after:180},
 {text:'DANH SÁCH THÀNH VIÊN',style:'CoverSubtitle',after:100}
 ],members:{headers:['STT','Họ và tên','Mã sinh viên','Phần việc'],rows:[
 ['1','Hoàng Việt Đức','','Khách hàng, bàn ăn, đặt bàn'],
 ['2','Nguyễn Cao Hoàng','','Thực đơn, nhân viên, bảo mật'],
 ['3','Đỗ Minh Chiến','','Hóa đơn, bán hàng, báo cáo'],
 ['4','Lý Tiến Đạt','','Kho, nhập hàng']
 ],widths:[800,2650,1500,4121]}});
page('MỤC LỤC', {type:'toc'});
page('CHƯƠNG 1. KHẢO SÁT BÀI TOÁN VÀ PHÂN TÍCH YÊU CẦU',
 h('1.1. Mô tả bài toán'),
 p('Hoạt động nhà hàng hải sản cần quản lý thực đơn, hồ sơ nhân viên, khách hàng, bàn ăn, đặt bàn, hóa đơn và nguyên liệu nhập kho. Các thông tin có quan hệ với nhau: món thuộc một loại, hóa đơn có nhiều món, nhân viên là người lập chứng từ và phiếu nhập gồm các nguyên liệu từ nhà cung cấp. Việc ghi chép rời rạc gây khó khăn khi tra cứu và đối chiếu lịch sử.'),
 p('Đề tài xây dựng một ứng dụng web quản lý tập trung, cung cấp giao diện tiếng Việt và lưu trữ bằng cơ sở dữ liệu quan hệ. Người dùng có thể quản lý danh mục, lập chứng từ và xem số liệu tổng hợp từ các giao dịch đã ghi nhận.'),
 h('1.2. Mục tiêu và phạm vi'),
 p('Hệ thống phục vụ một nhà hàng trong phạm vi bài tập lớn. Báo cáo được xây dựng từ bảng phân công của nhóm và đối chiếu mã nguồn QuanLyNHHS ngày 10/11/2026; không sử dụng số liệu khảo sát thực địa hoặc kết quả vận hành thương mại.'),
 p('Mục tiêu gồm phân tích nghiệp vụ, thiết kế dữ liệu, xây dựng ứng dụng Java Spring Boot theo các tầng Controller – Service – Repository, hiển thị bằng Thymeleaf và kết nối Oracle. Thiết kế ban đầu gồm 12 bảng nghiệp vụ; mã hiện tại bổ sung TAI_KHOAN để đăng ký và đăng nhập.'),
 p('Mã nguồn hiện có xử lý CRUD danh mục, khách hàng, đặt bàn, hóa đơn và nhập kho. Báo cáo mô tả chức năng đã tìm thấy trong code, đồng thời nêu riêng các điểm chưa hoàn thiện và phạm vi kiểm thử. Sự tồn tại của chức năng trong code không thay thế kết quả chạy thực tế trên Oracle.'));
page('1.3. Đối tượng quản lý và người sử dụng',
 table('Bảng 1.1. Đối tượng quản lý',['Nhóm','Nội dung'],[
 ['Thực đơn','Loại món; món ăn; đơn vị, đơn giá, ảnh, mô tả và trạng thái phục vụ.'],
 ['Nhân viên','Hồ sơ, liên hệ, chức vụ, ca làm, lương, ngày vào làm và trạng thái.'],
 ['Khách và bàn','Khách hàng, bàn ăn, sức chứa, lượt đặt, ngày đặt và tiền cọc.'],
 ['Bán hàng','Hóa đơn, món đã gọi, số lượng, đơn giá tại lúc ghi chứng từ, giảm giá và thanh toán.'],
 ['Kho','Nhà cung cấp, nguyên liệu, tồn kho, phiếu nhập và chi tiết nhập.'],
 ['Tài khoản','Thông tin đăng ký, tên đăng nhập và mật khẩu đã băm.']
 ],[2000,7071]),
 h('1.3.1. Nhóm người sử dụng theo nghiệp vụ'),
 p('Quản lý theo dõi danh mục và nhân viên; phục vụ tra cứu món và tiếp nhận đặt bàn; thu ngân lập, thanh toán hóa đơn; thủ kho quản lý nguyên liệu và phiếu nhập. Đây là cách phân tích nhiệm vụ nghiệp vụ.'),
 p('Trong cấu hình website hiện tại, các trang nghiệp vụ yêu cầu đăng nhập và tài khoản được cấp ROLE_USER chung. Chưa có quy tắc cho phép hoặc từ chối chức năng theo từng vai trò quản lý, thu ngân, phục vụ, thủ kho. Chức vụ trong hồ sơ nhân viên chưa liên kết với quyền tài khoản.'));
page('1.4. Yêu cầu chức năng và dữ liệu vào/ra',
 table('Bảng 1.2. Các yêu cầu chức năng',['Mã','Chức năng','Dữ liệu vào và kết quả'],[
 ['CN01','Loại món','Nhập tên, mô tả, trạng thái; xem danh sách, đếm món, thêm/sửa/xóa khi hợp lệ.'],
 ['CN02','Món ăn','Nhập loại, tên, đơn vị, giá, ảnh, mô tả, trạng thái; tra cứu, lọc và quản lý thực đơn.'],
 ['CN03','Nhân viên','Nhập hồ sơ, chức vụ, ca làm, lương; xem và cập nhật danh sách nhân viên.'],
 ['CN04','Khách hàng, bàn, đặt bàn','Nhập khách, bàn và lượt đặt; quản lý hồ sơ, xác nhận/hủy/hoàn thành đặt bàn.'],
 ['CN05','Hóa đơn','Chọn món và lượng; lưu chi tiết, tính tổng, giảm giá, thanh toán.'],
 ['CN06','Kho và nhập hàng','Chọn nhà cung cấp, nguyên liệu, lượng, giá; lập phiếu và điều chỉnh tồn.'],
 ['CN07','Báo cáo','Nhận khoảng ngày; tổng hợp doanh thu đã thanh toán và món bán chạy.'],
 ['CN08','Tài khoản','Đăng ký, đăng nhập, đăng xuất; bảo vệ truy cập trang nghiệp vụ.']
 ],[650,2200,6221]),
 h('1.4.1. Yêu cầu hợp lệ dữ liệu'),
 p('Các trường bắt buộc phải có giá trị. Giá và lương nếu nhập không âm; số lượng giao dịch dương; danh mục tham chiếu phải tồn tại. Loại món đang có món, món đã có hóa đơn và nhân viên đã lập chứng từ phải được bảo vệ khi xóa.'),
 h('1.4.2. Yêu cầu giao diện'),
 p('Thống nhất bố cục điều hướng, tên trường và cách định dạng tiền. Biểu mẫu cần giữ lại dữ liệu nhập khi lỗi, hiển thị lý do và chuyển về danh sách khi lưu thành công.'));
page('1.5. Phân công thực hiện và tiêu chí đánh giá',
 table('Bảng 1.3. Phân công theo bảng tính của nhóm',['Thành viên','Phạm vi phụ trách','Bảng nghiệp vụ chính'],[
 ['Hoàng Việt Đức','Khách hàng, bàn ăn, đặt bàn','KHACH_HANG; BAN_AN; DAT_BAN'],
 ['Nguyễn Cao Hoàng','Thực đơn, nhân viên, Security','LOAI_MON; MON_AN; NHAN_VIEN'],
 ['Đỗ Minh Chiến','Bán hàng, hóa đơn, báo cáo','HOA_DON; CT_HOA_DON'],
 ['Lý Tiến Đạt','Kho, nhập hàng','NHA_CUNG_CAP; NGUYEN_LIEU; PHIEU_NHAP; CT_PHIEU_NHAP']
 ],[2200,3100,3771]),
 p('Nguồn phân công: PhanCong_Nhom4_QuanLyNhaHangHaiSan.xlsx, sheet Tổng quan, các dòng 8–11. Phần Hoàng có thêm giao diện login, Spring Security, phân quyền URL theo yêu cầu và role/privilege Oracle. Bảng TAI_KHOAN xuất hiện trong mã hiện tại để lưu tài khoản đăng nhập.'),
 p('Các ô tiến độ trong bảng tính còn ghi Chưa bắt đầu. Báo cáo sử dụng bảng này để xác định trách nhiệm, không dùng phần trăm tiến độ đó để kết luận chức năng chưa được cài đặt. Việc phân công cũng không chứng minh quyền tác giả của từng dòng code sau khi tích hợp.'),
 h('1.5.1. Tiêu chí đánh giá'),
 p('Một chức năng CRUD cần có đường dẫn xử lý, kiểm tra dữ liệu, thao tác lưu trữ và phản hồi trên giao diện. Nghiệp vụ nhiều bảng cần có giao dịch. Kiểm thử phải chỉ rõ môi trường, trường hợp đã chạy và kết quả; tránh gộp kiểm thử H2 với xác nhận trên Oracle thật.'));
page('CHƯƠNG 2. THIẾT KẾ CƠ SỞ DỮ LIỆU VÀ XÂY DỰNG CHƯƠNG TRÌNH',
 h('2.1. Kiến trúc và tổ chức mã nguồn'),
 table('Bảng 2.1. Trách nhiệm của các thành phần',['Thành phần','Vai trò trong chương trình'],[
 ['Entity / Model','Ánh xạ lớp và thuộc tính Java vào bảng, cột, khóa và quan hệ.'],
 ['DTO / Form','Nhận các trường được phép nhập và khai báo điều kiện kiểm tra dữ liệu.'],
 ['Controller','Nhận GET/POST, kiểm tra lỗi form, gọi Service, đưa dữ liệu vào Model và chọn template.'],
 ['Service','Xử lý quy tắc nghiệp vụ và giao dịch trước khi đọc/ghi database.'],
 ['Repository','Truy cập dữ liệu qua Spring Data JPA; bổ sung truy vấn tổng hợp khi cần.'],
 ['Thymeleaf và JavaScript','Thymeleaf tạo HTML tại server; JavaScript xử lý tìm kiếm, lọc và tương tác sau khi trang tải.'],
 ['Spring Security','Xử lý đăng nhập, phiên người dùng, bảo vệ URL và CSRF.']
 ],[2200,6871]),
 h('2.1.1. Luồng yêu cầu xem dữ liệu'),
 p('Trình duyệt gửi GET → Controller gọi Service → Repository đọc dữ liệu qua JPA/Hibernate → Controller đặt kết quả vào Model → Thymeleaf tạo HTML → trình duyệt hiển thị.'),
 h('2.1.2. Luồng ghi dữ liệu'),
 p('Biểu mẫu gửi POST → kiểm tra đăng nhập và CSRF → Controller nhận DTO và kiểm tra @Valid → Service kiểm tra nghiệp vụ trong giao dịch → Repository ghi dữ liệu → chuyển hướng khi thành công hoặc trả form kèm lỗi.'));
page('2.2. Lược đồ dữ liệu và quan hệ',
 p('Hệ thống có 12 bảng nghiệp vụ theo phân công và 1 bảng tài khoản bổ sung. Mười bảng danh mục/phần đầu chứng từ dùng khóa chính đơn. Hai bảng chi tiết dùng khóa ghép để phân biệt từng món trong hóa đơn và từng nguyên liệu trong phiếu nhập.'),
 table('Bảng 2.2. Các liên kết dữ liệu chính',['Bảng con / cột','Bảng được tham chiếu','Cách thể hiện cần lưu ý'],[
 ['DAT_BAN.MA_KH, MA_BAN','KHACH_HANG; BAN_AN','Ánh xạ quan hệ ManyToOne trong entity.'],
 ['MON_AN.MA_LOAI','LOAI_MON.MA_LOAI','ManyToOne; mã loại bắt buộc.'],
 ['HOA_DON.MA_BAN, MA_NV, MA_KH','BAN_AN; NHAN_VIEN; KHACH_HANG','Mã hiện tại lưu bằng thuộc tính Long.'],
 ['CT_HOA_DON.MA_HD, MA_MON','HOA_DON; MON_AN','Khóa ghép trong EmbeddedId.'],
 ['PHIEU_NHAP.MA_NCC, MA_NV','NHA_CUNG_CAP; NHAN_VIEN','Nhà cung cấp ánh xạ quan hệ; mã nhân viên lưu Long.'],
 ['CT_PHIEU_NHAP.MA_PN, MA_NL','PHIEU_NHAP; NGUYEN_LIEU','EmbeddedId kết hợp MapsId và ManyToOne.'],
 ['TAI_KHOAN','Chưa liên kết NHAN_VIEN','Lưu tài khoản riêng; tên đăng nhập có ràng buộc duy nhất.']
 ],[2900,2400,3771]),
 p('Quan hệ nghiệp vụ và ràng buộc vật lý cần được phân biệt. Một thuộc tính Long chứa mã không tự tạo khóa ngoại bằng JPA. Cần kiểm tra DDL thực tế trên Oracle để xác nhận đầy đủ khóa ngoại, CHECK và quyền truy cập.'),
 p('Giá bán được lưu tại chi tiết hóa đơn để giữ lịch sử khi danh mục đổi giá. Ở phần món ăn, việc cập nhật giá không tự sửa các dòng hóa đơn cũ.'));

// Retain the original report's six pairs of business tables, update them to current entities.
for (const s of schemas) s.status='Đã có entity và mã xử lý trong dự án; cấu trúc Oracle thực tế cần được đối chiếu khi triển khai.';
const byName=Object.fromEntries(schemas.map(s=>[s.name,s]));
byName.DAT_BAN.cols.find(c=>c[0]==='NGAY_DAT')[2]='Ngày đặt; entity hiện dùng LocalDate';
byName.DAT_BAN.rule='Đã có luồng đặt, xác nhận, hủy và hoàn thành. Hiện lưu ngày, chưa mô hình hóa khung giờ bắt đầu/kết thúc.';
byName.BAN_AN.rule='Có truy vấn bàn trống và cập nhật trạng thái khi xử lý đặt bàn. Liên kết thanh toán với việc giải phóng bàn cần hoàn thiện.';
byName.HOA_DON.cols.find(c=>c[0]==='NGAY_LAP')[2]='Ngày lập; Java dùng LocalDate';
byName.HOA_DON.cols.find(c=>c[0]==='TRANG_THAI')[2]='Chua thanh toan / Da thanh toan';
byName.HOA_DON.rule='Giảm giá là số tiền trực tiếp. Tổng sau giảm được chặn tối thiểu bằng 0. Khách vãng lai có thể không gắn mã khách.';
byName.LOAI_MON.rule='Số món được đếm bằng truy vấn GROUP BY; không lưu số lượng món thành cột riêng.';
byName.MON_AN.cols.push(['HINH_ANH','VARCHAR2(1000)','Đường dẫn ảnh được lưu'],['MO_TA','VARCHAR2(1000)','Mô tả món được lưu']);
byName.MON_AN.rule='Ảnh và mô tả hiện là cột lưu trữ. Lượng đã bán được tổng hợp từ chi tiết hóa đơn đã thanh toán; không phải cột cố định của món.';
byName.CT_HOA_DON.rule='Khóa ghép (MA_HD, MA_MON). Luồng tạo hóa đơn gộp các dòng trùng món bằng cách cộng số lượng.';
byName.NHAN_VIEN.cols.push(['EMAIL','VARCHAR2(100)','Email liên hệ'],['CA_LAM_VIEC','VARCHAR2(100)','Thông tin ca làm'],['TRANG_THAI','VARCHAR2(30)','Đang làm việc / Nghỉ phép / Đã nghỉ việc']);
byName.NHAN_VIEN.rule='Email, ca làm và trạng thái hiện được lưu. Chức vụ là dữ liệu hồ sơ; không tự cấp quyền đăng nhập.';
byName.PHIEU_NHAP.cols.find(c=>c[0]==='NGAY_NHAP')[2]='Ngày nhập; Java dùng LocalDate';
byName.PHIEU_NHAP.cols.push(['PHIEN_BAN','NUMBER','Thuộc tính Version để kiểm soát cập nhật']);
byName.PHIEU_NHAP.rule='Có giao dịch lưu/sửa/xóa phiếu, khóa dữ liệu và kiểm tra phiên bản. Tổng tiền được tính lại từ chi tiết.';
byName.CT_PHIEU_NHAP.rule='Khóa ghép (MA_PN, MA_NL). Service từ chối nhập trùng nguyên liệu trong một phiếu.';
byName.NGUYEN_LIEU.rule='Tồn được điều chỉnh ở PhieuNhapService khi lập/sửa/xóa phiếu. Chưa có định lượng món – nguyên liệu để tự trừ tồn khi bán.';
const groups=['Khách hàng và đặt bàn','Bàn ăn và hóa đơn','Loại món và món ăn','Chi tiết hóa đơn và nhân viên','Nhà cung cấp và phiếu nhập','Chi tiết phiếu nhập và nguyên liệu'];
for(let i=0;i<schemas.length;i+=2){
 const blocks=[];
 if(i===0) blocks.push(p('Cấu trúc bảng được mô tả theo entity và thiết kế nghiệp vụ; các ràng buộc vật lý cần được xác nhận trên Oracle.'),h('2.3.1. Khách hàng và đặt bàn'));
 for(let j=i;j<i+2;j++){
  const s=schemas[j];
  blocks.push(h(s.name+' – '+s.purpose),table(`Bảng 2.${j+3}. Cấu trúc ${s.name}`,['Cột','Kiểu Oracle','Ý nghĩa'],s.cols,[2350,2050,4671]),p('Khóa chính: '+s.key+'. '+s.rule));
 }
 page(i===0?'2.3. Mô tả các bảng dữ liệu':`2.3.${i/2+1}. ${groups[i/2]}`,...blocks);
}
page('2.3.7. Bảng tài khoản bổ sung',
 p('Bảng TAI_KHOAN được dùng trong chức năng đăng ký và đăng nhập. Bảng này bổ sung cho thiết kế 12 bảng nghiệp vụ ban đầu và không thay thế bảng NHAN_VIEN.'),
 table('Bảng 2.15. Cấu trúc TAI_KHOAN',['Cột','Kiểu Oracle','Ý nghĩa / ràng buộc'],[
 ['MA_TK','NUMBER','Khóa chính; cấp bằng SEQ_TAI_KHOAN.'],
 ['HO_TEN','VARCHAR2(100)','Họ tên; bắt buộc.'],
 ['TEN_DANG_NHAP','VARCHAR2(30)','Tên đăng nhập; bắt buộc và duy nhất.'],
 ['MAT_KHAU','VARCHAR2(100)','Lưu chuỗi băm BCrypt; bắt buộc.']
 ],[2400,2100,4571]),
 h('2.3.7.1. Đặc điểm tài khoản hiện tại'),
 p('TaiKhoanService chuẩn hóa tên đăng nhập, kiểm tra trùng tên, kiểm tra xác nhận mật khẩu và băm trước khi lưu. Khi xác thực, service chuyển bản ghi thành UserDetails cho Spring Security.'),
 p('Entity chưa có cột vai trò và chưa có quan hệ với NHAN_VIEN. Lớp NguoiDung hiện cấp ROLE_USER cho mọi tài khoản. Vì vậy không thể dùng việc thay đổi chức vụ nhân viên để thay đổi quyền website.'),
 h('2.3.7.2. Quyền Oracle và quyền website'),
 p('NHHS_APP là user dùng cho kết nối Oracle. Các role và GRANT trong oracle_setup_user.sql thuộc lớp quyền cơ sở dữ liệu. Tài khoản trong TAI_KHOAN và quy tắc trong SecurityConfig thuộc lớp đăng nhập website. Script Oracle không tự tạo quyền truy cập URL và chưa được thực thi trong lần rà soát này.'));
page('2.4. Quy trình nghiệp vụ và quy tắc toàn vẹn',
 table('Bảng 2.16. Quy tắc tính toán trong mã hiện tại',['Nội dung','Cách xử lý'],[
 ['Thành tiền','Số lượng × đơn giá; kiểu tiền dùng BigDecimal.'],
 ['Tổng hóa đơn','Tổng chi tiết − số tiền giảm; nếu âm thì đưa về 0.'],
 ['Thanh toán','Từ chối hóa đơn đã thanh toán hoặc chưa có món; tính lại tổng và đổi trạng thái.'],
 ['Tổng phiếu nhập','Cộng thành tiền các dòng nhập; kiểm tra giới hạn giá trị.'],
 ['Tồn khi sửa phiếu','Tồn mới = tồn hiện tại + lượng mới − lượng cũ.'],
 ['Tồn khi xóa phiếu','Trừ lại lượng đã nhập; từ chối thao tác nếu làm tồn không hợp lệ.'],
 ['Doanh thu','Chỉ cộng hóa đơn Da thanh toan trong khoảng ngày.']
 ],[2300,6771]),
 h('2.4.1. Bảo vệ dữ liệu danh mục'),
 p('LoaiMonService chặn xóa loại còn món. MonAnService chặn xóa món đã xuất hiện trong chi tiết hóa đơn. NhanVienService chặn xóa nhân viên đã lập hóa đơn hoặc phiếu nhập. Các trường hợp đã được dùng có thể đổi trạng thái thay vì xóa lịch sử.'),
 h('2.4.2. Giao dịch'),
 p('@Transactional tổ chức các thao tác liên quan trong một giao dịch. Khi xảy ra lỗi phù hợp, thay đổi được rollback. saveAndFlush gửi thay đổi đang chờ xuống database để phát hiện lỗi sớm; flush không đồng nghĩa commit.'),
 p('Các kiểm tra ở Service cung cấp thông báo dễ hiểu. Ràng buộc database vẫn cần thiết để bảo vệ dữ liệu khi phát sinh truy cập đồng thời.'));
page('2.5. Xây dựng chức năng loại món và món ăn',
 h('2.5.1. Quản lý loại món'),
 p('URL /loai-mon hiển thị danh sách, tổng loại và số món theo loại. Người dùng thêm, sửa tên, mô tả và trạng thái Hoạt động/Tạm dừng. LoaiMonForm kiểm tra tên bắt buộc, tối đa 100 ký tự và mô tả tối đa 255 ký tự.'),
 p('LoaiMonService.save(id, form) tạo entity mới khi id là null hoặc lấy bản ghi hiện có khi sửa. delete(id) kiểm tra còn món tham chiếu trước khi xóa. MonAnRepository.demMonTheoLoai() dùng COUNT và GROUP BY để tổng hợp số món.'),
 h('2.5.2. Quản lý món ăn'),
 p('URL /mon-an hiển thị thực đơn dạng thẻ hoặc bảng. Thông tin gồm loại, tên, đơn vị, đơn giá, trạng thái, URL ảnh và mô tả. Có thống kê tổng món, đang phục vụ, tạm hết và số lượng bán từ các hóa đơn đã thanh toán.'),
 p('MonAnForm yêu cầu loại, tên, đơn vị, giá và trạng thái hợp lệ. Giá không âm, tối đa 10 chữ số nguyên và 2 chữ số thập phân. URL ảnh nếu nhập phải dùng HTTP hoặc HTTPS. Hệ thống lưu đường dẫn ảnh, chưa cung cấp chức năng tải file ảnh lên.'),
 p('MonAnService tìm loại trong database trước khi gán vào món. Mã không tồn tại khi sửa gây lỗi thay vì tạo mới. Món đã có hóa đơn bị chặn xóa; sửa giá danh mục không tự cập nhật giá đã lưu ở chi tiết hóa đơn cũ.'),
 h('2.5.3. Giao diện'),
 p('templates/monan/index.html duyệt dishes bằng th:each. main.js kết hợp từ khóa với bộ lọc loại và áp dụng cho cả thẻ lẫn bảng. Tìm kiếm lọc các phần tử đã tải trên trang; mỗi lần gõ không tự truy vấn lại Oracle.'));
page('2.6. Xây dựng chức năng quản lý nhân viên',
 p('URL /nhan-vien cung cấp danh sách và biểu mẫu quản lý hồ sơ. Các trường gồm họ tên, giới tính, điện thoại, email, chức vụ, ca làm, lương, ngày vào làm và trạng thái. Trạng thái hiện gồm Đang làm việc, Nghỉ phép, Đã nghỉ việc.'),
 table('Bảng 2.17. Kiểm tra dữ liệu nhân viên',['Trường','Quy tắc ở DTO'],[
 ['Họ tên','Bắt buộc; tối đa 100 ký tự.'],
 ['Chức vụ','Bắt buộc; tối đa 50 ký tự.'],
 ['Email','Nếu nhập phải đúng định dạng; tối đa 100 ký tự.'],
 ['Điện thoại','Tối đa 15 ký tự; kiểm tra nhóm ký tự được phép.'],
 ['Lương','Có thể bỏ trống; nếu nhập không âm, tối đa 10 chữ số nguyên và 2 số thập phân.'],
 ['Trạng thái','Bắt buộc; thuộc ba giá trị được quy định.']
 ],[2100,6971]),
 p('NhanVienController nhận DTO và chuyển lỗi về biểu mẫu. NhanVienService lưu đủ thông tin đã nhập. Khi xóa, service kiểm tra đồng thời hóa đơn và phiếu nhập để giữ lại thông tin người lập chứng từ.'),
 p('Trang có thống kê tổng nhân viên, số đang làm việc, số nghỉ phép; tìm kiếm kết hợp lọc chức vụ bằng JavaScript. Hồ sơ có thông tin lương nhưng chưa phải hệ thống chấm công hoặc tính lương tự động.'),
 p('Chức vụ ở NHAN_VIEN mô tả công việc. Quyền truy cập website được xử lý riêng bởi Spring Security, hiện chưa được suy ra từ trường này.'));
page('2.7. Xây dựng chức năng đăng nhập và bảo mật',
 h('2.7.1. Đăng ký và lưu mật khẩu'),
 p('AuthController nhận form tại /register. TaiKhoanService kiểm tra tên đăng nhập trùng, mật khẩu xác nhận và độ dài phù hợp; gọi PasswordEncoder để băm BCrypt trước khi lưu. Database chứa chuỗi băm, không dùng cơ chế giải mã để lấy lại mật khẩu gốc.'),
 h('2.7.2. Đăng nhập và phiên làm việc'),
 p('GET /login hiển thị form. Khi gửi thông tin, Spring Security xử lý xác thực với tên tham số tenDangNhap và matKhau. TaiKhoanService.loadUserByUsername() tìm tài khoản. Đăng nhập thành công chuyển về /; sai chuyển /login?error. Đăng xuất hủy phiên và xóa cookie JSESSIONID.'),
 h('2.7.3. Bảo vệ URL và CSRF'),
 code(`.requestMatchers("/login", "/register", "/css/**",
                 "/js/**", "/images/**", "/favicon.ico").permitAll()
.anyRequest().authenticated()`),
 p('Đoạn trích rút gọn từ SecurityConfig cho phép truy cập các trang công khai và yêu cầu đăng nhập với các URL còn lại. CSRF được giữ theo mặc định của Spring Security; các form thay đổi dữ liệu dùng POST. Token giúp chống việc trang khác giả mạo yêu cầu thay đổi dữ liệu dưới phiên người dùng.'),
 h('2.7.4. Giới hạn phân quyền'),
 p('Mã hiện cấp ROLE_USER chung, chưa có hasRole/hasAuthority để chia quyền CRUD theo vai trò nghiệp vụ. Script role Oracle là một phần riêng ở tầng database; việc có script không chứng minh đã chạy thành công hoặc đã có phân quyền chức năng trên website.'));
page('2.8. Các module tích hợp trong hệ thống',
 table('Bảng 2.18. Cách triển khai các module liên quan',['Module','Nội dung đã có trong mã'],[
 ['Khách hàng và bàn ăn','Có entity, repository, service, controller, trang danh sách và biểu mẫu; tra cứu khách và bàn trống.'],
 ['Đặt bàn','Kiểm tra bàn Trống khi tạo; lưu lượt đặt; xác nhận, hủy và hoàn thành có cập nhật trạng thái bàn.'],
 ['Hóa đơn','Tạo/sửa hóa đơn chưa thanh toán, gộp món trùng, lưu đơn giá, tính tổng và giảm giá, xem chi tiết và thanh toán.'],
 ['Báo cáo','Tính doanh thu theo khoảng ngày, hôm nay, tháng hiện tại; tổng hợp món bán chạy và tổng chi tiết hóa đơn.'],
 ['Nhập kho','CRUD nhà cung cấp/nguyên liệu; lập/sửa/xóa phiếu; điều chỉnh tồn theo chênh lệch; kiểm tra phiên bản.']
 ],[2200,6871]),
 p('Đây là phạm vi tích hợp của cả nhóm. Phần của Hoàng sử dụng dữ liệu hóa đơn để thống kê lượng bán và bảo vệ xóa món, sử dụng hóa đơn/phiếu nhập để bảo vệ hồ sơ nhân viên. Nghiệp vụ hóa đơn thuộc phần Chiến; nghiệp vụ kho thuộc phần Đạt.'),
 h('2.8.1. Những điểm cần tiếp tục hoàn thiện'),
 p('HoaDonService.thanhToan() hiện đổi trạng thái hóa đơn, chưa trực tiếp giải phóng bàn. Đặt bàn dùng LocalDate nên chưa có khung giờ và kiểm tra trùng khoảng thời gian. Mô hình chưa có định lượng món – nguyên liệu để tự trừ kho khi bán; tiền cọc chưa tự liên kết khấu trừ vào hóa đơn.'),
 p('Trong mã Java hiện tại, cập nhật tồn diễn ra tại PhieuNhapService. Không mô tả chức năng này là một trigger hoặc procedure Oracle đã được triển khai nếu chưa có bằng chứng kiểm tra tương ứng.'));
page('2.9. Luồng hiển thị danh sách và Thymeleaf',
 h('2.9.1. Controller truyền dữ liệu'),
 code(`@GetMapping
public String index(Model model) {
    var dishes = service.findAll();
    model.addAttribute("dishes", dishes);
    // Lược bớt các thuộc tính thống kê và dữ liệu form.
    return "monan/index";
}`),
 p('Đoạn trích rút gọn từ MonAnController. service.findAll() gọi repository.findAll(). Controller đặt danh sách vào Model dưới tên dishes; return chọn template templates/monan/index.html, không phải một lệnh truy vấn database.'),
 h('2.9.2. Template duyệt danh sách'),
 code(`<div class="dish-card" th:each="dish : \${dishes}">
    <!-- Hiển thị tên, giá và các thông tin của dish -->
</div>`),
 p('th:each là vòng lặp Thymeleaf. dishes là tên thuộc tính đã truyền, dish là biến tạm đại diện từng món. Với mỗi phần tử, Thymeleaf tạo một thẻ div. Quá trình này chạy tại server trước khi HTML được gửi đến trình duyệt.'),
 h('2.9.3. Danh sách chọn món của hóa đơn'),
 p('HoaDonController truyền danhSachMonAn bằng monAnRepository.findAll(Sort.by("tenMon").ascending()). Trong hoadon/form.html, select tạo ô chọn, còn option có th:each tạo lựa chọn cho mỗi món. th:value lấy mã món; th:text tạo tên và giá hiển thị. danhSachMonAn là biến dữ liệu, không phải tên hàm.'));
page('2.10. Luồng thêm, sửa, xóa và đoạn mã tiêu biểu',
 h('2.10.1. Ví dụ lưu loại món'),
 code(`@Transactional
public LoaiMon save(Long id, @NotNull @Valid LoaiMonForm form) {
    LoaiMon entity = id == null ? new LoaiMon() : getById(id);
    entity.setTenLoai(form.getTenLoai());
    entity.setMoTa(form.getMoTa());
    entity.setTrangThai(form.getTrangThai());
    return repository.saveAndFlush(entity);
}`),
 p('Đoạn trích từ LoaiMonService. Thêm mới truyền id = null; sửa truyền mã lấy từ URL. DTO chứa dữ liệu nhập; entity biểu diễn bản ghi sẽ lưu. getById() từ chối mã không tồn tại, tránh biến yêu cầu sửa thành thao tác thêm mới.'),
 h('2.10.2. Đường dẫn CRUD của phần Hoàng'),
 table('Bảng 2.19. Thay module bằng loai-mon, mon-an hoặc nhan-vien',['Phương thức','URL','Ý nghĩa'],[
 ['GET','/{module}','Hiển thị danh sách.'],
 ['GET','/{module}/them','Mở form mới.'],
 ['POST','/{module}/them','Kiểm tra và lưu mới.'],
 ['GET','/{module}/sua/{id}','Mở form điền sẵn dữ liệu.'],
 ['POST','/{module}/sua/{id}','Cập nhật bản ghi theo mã URL.'],
 ['POST','/{module}/xoa/{id}','Xóa sau khi kiểm tra quan hệ.']
 ],[1500,3300,4271]),
 p('Sau khi lưu thành công, Controller dùng redirect về danh sách và flash message. Khi lỗi, trả lại form cùng BindingResult. Các trường được nhận còn được giới hạn bằng WebDataBinder.setAllowedFields().'));
page('2.11. Truy vấn tổng hợp và xử lý giao diện',
 h('2.11.1. Đếm món theo loại'),
 code(`SELECT m.loaiMon.maLoai, COUNT(m)
FROM MonAn m
GROUP BY m.loaiMon.maLoai`),
 p('Đây là JPQL trong MonAnRepository: dùng tên entity và thuộc tính Java. Kết quả mỗi dòng gồm mã loại và số món. LoaiMonService đổi danh sách kết quả thành Map để template tra theo mã loại; loại chưa có món hiển thị mặc định bằng 0.'),
 h('2.11.2. Tổng lượng bán theo món'),
 code(`SELECT c.id.maMon, SUM(c.soLuong)
FROM CTHoaDon c, HoaDon h
WHERE c.id.maHd = h.maHd AND h.trangThai = 'Da thanh toan'
GROUP BY c.id.maMon`),
 p('Truy vấn trong CTHoaDonRepository nối chi tiết với hóa đơn theo mã, chỉ giữ hóa đơn đã thanh toán rồi cộng số lượng theo món. Số món trong một loại, tổng số món danh mục và tổng lượng bán là ba đại lượng khác nhau.'),
 h('2.11.3. Tìm kiếm và lọc'),
 p('main.js đọc từ khóa, loại món/chức vụ và các thuộc tính dữ liệu trên phần tử HTML. Điều kiện tìm kiếm và lọc được kết hợp khi xác định phần tử cần hiện. Nút đổi thẻ/bảng chỉ thay đổi cách trình bày, không tạo hoặc xóa món trong database.'),
 p('Với quy mô dữ liệu lớn, cần bổ sung phân trang và tìm kiếm tại server để tránh tải toàn bộ danh sách vào trình duyệt.'));
page('2.12. Kiểm thử và kết quả đối chiếu',
 p('Ngày 10/11/2026 đã chạy lại MenuCrudIntegrationTests và menu-filters.test.cjs. Bộ Java sử dụng profile test, MockMvc và H2 trong bộ nhớ với chế độ tương thích Oracle; không dùng database Oracle cấu hình cho ứng dụng thật.'),
 table('Bảng 2.20. Kết quả kiểm thử đã thực hiện',['Bộ kiểm thử','Phạm vi','Kết quả'],[
 ['Java: MenuCrudIntegrationTests','CRUD; form sửa; kiểm tra dữ liệu; danh mục bị xóa; bảo vệ xóa; mã không tồn tại; đăng nhập, BCrypt và CSRF.','14 chạy; 0 lỗi; 0 thất bại.'],
 ['JavaScript: menu-filters.test.cjs','Kết hợp từ khóa/bộ lọc món; lọc nhân viên; tra cứu loại; chuyển dạng thẻ/bảng.','4 chạy; 0 thất bại.'],
 ['Oracle và giao diện trình duyệt','Chưa chạy kiểm chứng đầy đủ trong lần chỉnh báo cáo.','Cần kiểm tra trên máy demo.']
 ],[2400,4500,2171]),
 h('2.12.1. Lệnh kiểm tra có thể chạy lại'),
 code(`mvn -Dtest=MenuCrudIntegrationTests test
node --test src/test/js/menu-filters.test.cjs`),
 p('Chạy trong thư mục QuanLyNHHS có pom.xml. Ở lần rà soát này Maven chạy offline với kho dependency cục bộ target/maven-repository. Kết quả Java được lưu tại target/surefire-reports; log chạy tại target/report-build-hoang-tests.log.'),
 h('2.12.2. Phạm vi kết luận'),
 p('Các kết quả trên xác nhận những trường hợp đã chạy của phần thực đơn, nhân viên và bảo mật trên môi trường test. Chưa dùng chúng để kết luận toàn bộ các module tích hợp hoặc các đặc điểm riêng của Oracle đều đã đạt.'));
page('2.13. Triển khai, giới hạn và hướng phát triển',
 h('2.13.1. Chuẩn bị chạy chương trình'),
 p('Cấu hình kết nối Oracle được đặt trong application.properties, mặc định dùng localhost:1521/FREEPDB1 nếu không có DB_URL. User kết nối cần đủ quyền trên schema và sequence. Báo cáo không chép mật khẩu cấu hình vào nội dung.'),
 p('Chạy QuanLyNhhsApplication và chờ thông báo Started QuanLyNhhsApplication. Ứng dụng chỉ tự mở trình duyệt sau ApplicationReadyEvent. Nếu không có server.port thì cổng mặc định là 8080; khi cổng bị chiếm có thể chọn cổng trống trong cấu hình rồi mở đúng địa chỉ localhost tương ứng.'),
 h('2.13.2. Giới hạn hiện tại'),
 p('Phân quyền URL theo vai trò nghiệp vụ chưa được cài đặt. Bảng tài khoản chưa liên kết hồ sơ nhân viên. Thanh toán chưa trực tiếp giải phóng bàn. Đặt bàn chưa có khung giờ; chưa tự khấu trừ tiền cọc; bán món chưa tự trừ kho theo định lượng. Ảnh món dùng URL; phần nhân viên chưa có chấm công và tính lương tự động.'),
 p('Một số mã tham chiếu được lưu bằng Long nên cần kiểm tra DDL để bổ sung khóa ngoại phù hợp. Script role/privilege và việc cập nhật schema bằng Hibernate cần được kiểm chứng riêng trên Oracle; không mặc định coi script đã chạy chỉ vì có trong repository.'),
 h('2.13.3. Hướng phát triển'),
 p('Bổ sung vai trò tài khoản và quy tắc URL; thống nhất trạng thái giữa bàn, đặt bàn và hóa đơn; kiểm tra toàn vẹn trong giao dịch; thêm phân trang, tìm kiếm server; mở rộng mô hình định lượng, xuất kho và kiểm kê. Hoàn thiện kiểm thử tích hợp Oracle và các kịch bản sử dụng thực tế trước khi triển khai.'));
page('KẾT LUẬN',
 p('Đề tài tổ chức một hệ thống quản lý nhà hàng hải sản bằng Java Spring Boot, Thymeleaf và Oracle. Mã nguồn hiện có các nhóm quản lý danh mục, nhân viên, khách hàng và đặt bàn, hóa đơn, báo cáo, nhập kho và tài khoản. Thiết kế được mô tả bằng 12 bảng nghiệp vụ cùng bảng TAI_KHOAN bổ sung.'),
 p('Phần của Nguyễn Cao Hoàng bao gồm loại món, món ăn, nhân viên và đăng nhập/bảo mật. Ba module danh mục có luồng Controller – DTO – Service – Repository, kiểm tra dữ liệu và bảo vệ bản ghi đã được chứng từ sử dụng. Chức năng tài khoản dùng BCrypt và Spring Security; phân quyền chi tiết theo vai trò vẫn là nội dung cần hoàn thiện.'),
 p('Trong lần đối chiếu ngày 10/11/2026, 14 kiểm thử Java và 4 kiểm thử JavaScript của phần Hoàng đều đạt. Phạm vi đó chưa thay thế việc chạy toàn bộ hệ thống trên Oracle. Các hạn chế về liên kết nghiệp vụ, phân quyền và tồn kho cần tiếp tục được xử lý và kiểm tra trong giai đoạn hoàn thiện.'),
 h('TÀI LIỆU THAM KHẢO'),
 p('[1] Bảng phân công nhóm: PhanCong_Nhom4_QuanLyNhaHangHaiSan.xlsx, các sheet Tổng quan, Phân công và Checklist.'),
 p('[2] Mã nguồn dự án QuanLyNHHS trong workspace, đối chiếu ngày 10/11/2026; các thư mục model, dto, controller, service, repository, config, templates và static/js.'),
 p('[3] Các file kiểm thử MenuCrudIntegrationTests.java, MenuTestApplication.java, application-test.properties và menu-filters.test.cjs; kết quả chạy ngày 10/11/2026.'),
 p('[4] Tài liệu nội bộ HOANG_CRUD.md, GIAI_THICH_JAVA.md, BAO_CAO_HOANG.md và oracle_setup_user.sql.'));
};
