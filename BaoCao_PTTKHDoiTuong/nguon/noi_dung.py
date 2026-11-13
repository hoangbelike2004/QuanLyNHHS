"""Nội dung báo cáo: các trang, đoạn văn, bảng và hình dùng chung cho Word/PDF."""
PAGES = []
def page(title, *blocks, chapter=None):
    PAGES.append({'title': title, 'blocks': list(blocks), 'chapter': chapter})
def p(text): return {'kind': 'p', 'text': text}
def h(text): return {'kind': 'h', 'text': text}
def note(text): return {'kind': 'note', 'text': text}
def table(head, rows, widths=None): return {'kind': 'table', 'head': head, 'rows': rows, 'widths': widths}
def image(name, caption, maxh=16): return {'kind': 'image', 'name': name, 'caption': caption, 'maxh': maxh}
def code(text): return {'kind': 'code', 'text': text}

page('TRANG BÌA', {'kind':'cover'})

page('LỜI MỞ ĐẦU VÀ CĂN CỨ THỰC HIỆN',
 p('Nhà hàng hải sản tiếp nhận đặt bàn, phục vụ món ăn, lập hóa đơn và nhập nguyên liệu mỗi ngày. Khi dữ liệu bàn, khách hàng, thực đơn và chứng từ được ghi rời rạc, nhân viên khó đối chiếu trạng thái phục vụ, đơn giá và số lượng tồn. Đề tài “Hệ thống quản lý nhà hàng hải sản” được chọn để phân tích những quan hệ này và thiết kế một ứng dụng quản lý tập trung.'),
 p('Báo cáo vận dụng phân tích và thiết kế hướng đối tượng để nối kết mục tiêu người dùng với khái niệm nghiệp vụ, trách nhiệm lớp và tương tác phần mềm. Ba nghiệp vụ được phân tích sâu là lập hóa đơn, ghi nhận thanh toán và điều chỉnh phiếu nhập. Đặt bàn được xem xét thêm để làm rõ vòng đời trạng thái và những giới hạn của sản phẩm hiện tại.'),
 h('Nguồn và cách sử dụng'),
 table(['Nguồn','Vai trò trong báo cáo'],[
 ['Hướng dẫn thực hiện BTL – gửi SV.pdf','Căn cứ cấu trúc 4 chương; đặc tả UC, Conceptual Model, CRC, Sequence, DCD và lập luận thiết kế.'],
 ['PhanCong_Nhom4_QuanLyNhaHangHaiSan.xlsx','Căn cứ danh sách 4 thành viên và trách nhiệm module; không dùng mức tiến độ 0% để đánh giá code.'],
 ['Dự án QuanLyNHHS tại thời điểm 13/11/2026','Căn cứ mô tả cài đặt, tên lớp/phương thức, trạng thái và giới hạn đã nhận diện.'],
 ['Ảnh lược đồ 12 bảng do nhóm cung cấp','Định hướng dữ liệu nghiệp vụ; đối chiếu với entity để phản ánh các cột và bảng bổ sung.']],[.30,.70]),
 p('Các mô hình ở Chương 1–2 diễn đạt yêu cầu và ý nghĩa nghiệp vụ. Chương 3 chỉ rõ phần thiết kế phản ánh code và phần đề xuất cải thiện. Chương 4 phân biệt việc đọc mã nguồn, kiểm thử đã chạy và kịch bản chưa thực thi. Báo cáo không sử dụng số liệu khảo sát thực địa hoặc tự khẳng định kết quả vận hành trên Oracle.'),
 note('Thông tin còn để trống theo yêu cầu: giảng viên, lớp và mã sinh viên. Nhà hàng trong báo cáo là bối cảnh bài tập lớn, chưa gắn với một cơ sở đã khảo sát cụ thể.'))

page('MỤC LỤC', {'kind':'toc'})

page('DANH MỤC HÌNH VÀ THUẬT NGỮ',
 {'kind':'figures'},
 h('Các thuật ngữ sử dụng'),
 table(['Ký hiệu','Ý nghĩa'],[
 ['OOAD / UML','Phân tích và thiết kế hướng đối tượng / ngôn ngữ mô hình hóa thống nhất.'],
 ['Actor / UC','Vai trò tương tác với hệ thống / ca sử dụng phục vụ mục tiêu của vai trò.'],
 ['CRC / SSD / DCD','Lớp–trách nhiệm–cộng tác / biểu đồ tuần tự hệ thống / biểu đồ lớp thiết kế.'],
 ['BCE / DTO','Boundary–Control–Entity / đối tượng chuyển dữ liệu biểu mẫu.'],
 ['PK / FK / UQ','Khóa chính / khóa ngoại / ràng buộc duy nhất.'],
 ['NFR / JPA / CSRF','Yêu cầu phi chức năng / ánh xạ lưu trữ Java / giả mạo yêu cầu từ trang khác.']],[.27,.73]))

page('1.1. Bài toán, mục tiêu và bối cảnh',
 h('CHƯƠNG 1. BÀI TOÁN VÀ PHÂN TÍCH YÊU CẦU'),
 p('Một nhà hàng cần biết bàn nào còn trống, khách nào đã đặt bàn, món nào đang phục vụ và hóa đơn nào chưa được thanh toán. Khi nhập nguyên liệu, nhà hàng cần lưu nhà cung cấp, người lập phiếu, lượng nhập và giá nhập. Các giao dịch phải có thể tra cứu sau khi thực đơn hoặc thông tin danh mục thay đổi.'),
 h('Vấn đề cần giải quyết'),
 p('Nếu cùng một món được nhập nhiều lần vào hóa đơn, hệ thống phải xác định cộng dồn hay lưu các dòng riêng. Khi sửa một phiếu nhập đã làm tăng tồn kho, hệ thống phải biết lượng cũ để điều chỉnh phần chênh lệch. Khi hóa đơn đã thanh toán, việc sửa hoặc xóa tùy ý có thể làm sai báo cáo doanh thu. Những vấn đề này tạo ra quy tắc nghiệp vụ vượt ra ngoài thao tác thêm, sửa và xóa dữ liệu.'),
 h('Mục tiêu của đề tài'),
 table(['Mã','Mục tiêu','Cách đánh giá'],[
 ['MT01','Quản lý khách, bàn và lượt đặt nhất quán','Phân biệt trạng thái lượt đặt với trạng thái bàn; nêu điều kiện chuyển.'],
 ['MT02','Ghi nhận bán hàng và thanh toán chính xác','Có chi tiết với lượng, giá; tổng tiền xác định ở server; khóa sửa chứng từ đã thanh toán.'],
 ['MT03','Quản lý nhập hàng gắn với tồn kho','Lập/sửa/xóa phiếu làm thay đổi tồn theo cùng giao dịch; xử lý form cũ.'],
 ['MT04','Truy vết từ yêu cầu đến thiết kế','UC, CRC, Sequence, DCD và kiểm thử cùng tên gọi, điều kiện, trách nhiệm.']],[.13,.42,.45]),
 p('Sản phẩm tham chiếu là ứng dụng web nội bộ Java Spring Boot, Thymeleaf và Oracle. Việc có ứng dụng sẵn giúp kiểm tra tính khả thi của mô hình, nhưng không thay thế bước giải thích vì sao một lớp hoặc quan hệ xuất hiện trong thiết kế.'), chapter=1)

page('1.2. Phạm vi và các giả định',
 table(['Trong phạm vi','Ngoài phạm vi phiên bản này'],[
 ['Khách hàng, bàn ăn, đặt/xác nhận/hủy/hoàn thành lượt đặt.','Đặt bàn trực tuyến cho khách, sơ đồ ghép nhiều bàn, quản lý lịch theo từng khung giờ.'],
 ['Loại món, món ăn, đơn vị, giá, thông tin nhân viên.','Chấm công, tính lương tự động, điều phối bếp và tiến độ chế biến.'],
 ['Lập/sửa hóa đơn chưa thanh toán, giảm tiền trực tiếp, ghi nhận thanh toán.','Tách/gộp hóa đơn, thanh toán nhiều đợt, cổng ngân hàng, hoàn tiền, hóa đơn điện tử.'],
 ['Nhà cung cấp, nguyên liệu, nhập kho, điều chỉnh tồn theo chứng từ.','Xuất kho, kiểm kê, định lượng món–nguyên liệu, tự trừ tồn khi bán.'],
 ['Đăng nhập bằng tài khoản được cấp; xem báo cáo doanh thu.','Tự đăng ký tài khoản và phân quyền nghiệp vụ chi tiết đã hoàn thiện.']],[.5,.5]),
 h('Các giả định sử dụng xuyên suốt'),
 table(['Mã','Giả định và hệ quả'],[
 ['G01','Một cơ sở nhà hàng; tiền tệ VND. Lượng có thể là số thập phân vì hải sản có thể bán theo kg.'],
 ['G02','Một hóa đơn gắn một bàn và một người lập; khách có thể vãng lai nên thông tin khách trên hóa đơn là tùy chọn.'],
 ['G03','Giảm giá là số tiền trực tiếp. Tiền cọc của lượt đặt được lưu độc lập, chưa tự bù trừ vào hóa đơn.'],
 ['G04','Đặt bàn hiện lưu ngày và giữ bàn theo trạng thái chung; chưa suy ra được tính khả dụng theo giờ hoặc theo ngày tương lai.'],
 ['G05','Một phiếu nhập thuộc một nhà cung cấp; không lặp nguyên liệu trên cùng phiếu. Tồn đầu có thể được khai báo khi khởi tạo.'],
 ['G06','Phục vụ, thu ngân, thủ kho và quản lý là vai trò nghiệp vụ. Website hiện chỉ xác thực tài khoản với ROLE_USER chung.']],[.12,.88]),
 note('Các nội dung ngoài phạm vi không được suy diễn từ ví dụ gợi ý trong tài liệu học phần thành chức năng đã có.'))

page('1.3. Quy tắc nghiệp vụ',
 table(['Mã','Quy tắc','Mức đáp ứng khi đối chiếu code'],[
 ['BR01','Chỉ tiếp nhận lượt đặt mới khi bàn trống; số khách phải phù hợp sức chứa.','Có kiểm tra bàn trống tại Controller; kiểm tra số khách/sức chứa và đồng thời cần bổ sung.'],
 ['BR02','Hủy/hoàn thành đặt bàn trả bàn về trống; trạng thái kết thúc không được mở lại tùy ý.','Có cập nhật trạng thái; thiếu bảo vệ trạng thái nguồn.'],
 ['BR03','Hóa đơn phải có bàn, người lập và ít nhất một món; số lượng dương.','HoaDonService kiểm tra trường bắt buộc, danh sách và lượng; kiểm tra tồn tại mọi mã cha chưa đồng đều.'],
 ['BR04','Các dòng cùng món trên một hóa đơn được cộng dồn lượng.','Có gộp bằng Map trước khi lưu chi tiết.'],
 ['BR05','Đơn giá do server lấy từ món; thành tiền = lượng × giá; tổng = max(0, tổng dòng − giảm giá).','Có xử lý bằng BigDecimal; giảm giá không âm.'],
 ['BR06','Hóa đơn đã thanh toán không được sửa, xóa hoặc thanh toán lại.','Có kiểm tra theo trạng thái; chưa có khóa chống xử lý đồng thời.'],
 ['BR07','Phiếu nhập không lặp nguyên liệu; số lượng dương, giá không âm.','Có kiểm tra DTO và Service.'],
 ['BR08','Sửa phiếu điều chỉnh tồn theo lượng mới trừ lượng cũ; xóa phiếu hoàn lại lượng nhập.','Có giao dịch, khóa nguyên liệu và kiểm tra giới hạn tồn.'],
 ['BR09','Mọi thay đổi của một nghiệp vụ chứng từ phải thành công hoặc rollback cùng nhau.','Có ở hóa đơn/phiếu nhập; tiếp nhận đặt bàn đang tách lưu lượt đặt và cập nhật bàn.'],
 ['BR10','Form phiếu nhập cũ không được ghi đè phiên bản mới.','Có PHIEN_BAN, @Version và kiểm tra phiên bản.'],
 ['BR11','Không xóa danh mục đang được chứng từ tham chiếu.','Có kiểm tra tại các service danh mục; cần xác nhận đầy đủ FK trên Oracle.'],
 ['BR12','Doanh thu chỉ tính hóa đơn đã thanh toán trong khoảng ngày hợp lệ.','Có lọc doanh thu; bảng món bán chạy hiện tổng hợp toàn kỳ.']],[.12,.46,.42]),
 note('Cột đáp ứng là kết quả đọc mã nguồn, không phải xác nhận các luồng đã chạy thành công trên Oracle.'))

page('1.4. Tác nhân và mục tiêu người dùng',
 table(['Actor','Mục tiêu','UC liên quan'],[
 ['Nhân viên nội bộ','Đăng nhập bằng tài khoản được cấp và kết thúc phiên khi hoàn thành công việc.','UC01; actor khái quát của các vai trò bên dưới.'],
 ['Phục vụ','Tiếp nhận thông tin khách, kiểm tra bàn và xử lý lượt đặt.','UC03, UC10.'],
 ['Thu ngân','Ghi các món sử dụng, điều chỉnh hóa đơn còn mở và ghi nhận thanh toán.','UC04, UC05.'],
 ['Thủ kho','Quản lý nhà cung cấp/nguyên liệu; nhập hàng và sửa chứng từ khi có sai sót.','UC06, UC09.'],
 ['Quản lý','Quản lý thực đơn, hồ sơ nhân viên và xem số liệu bán hàng.','UC02, UC07, UC08.']],[.2,.56,.24]),
 p('Khách hàng cung cấp nhu cầu đặt bàn cho nhân viên nhưng chưa trực tiếp thao tác website, nên không được vẽ thành actor trực tiếp của ứng dụng nội bộ này. Nhà cung cấp cũng là đối tượng lưu trữ thông tin, chưa có cổng giao tiếp riêng. Oracle là thành phần lưu trữ bên trong giải pháp, không phải actor nghiệp vụ.'),
 h('Cách đọc quan hệ actor và Use Case'),
 p('Sự khái quát hóa actor cho biết các vai trò nhân viên đều có thể thực hiện mục tiêu đăng nhập. Các đường nối còn lại diễn đạt trách nhiệm sử dụng theo nghiệp vụ. Chúng không phải bằng chứng rằng ứng dụng đã cấu hình quyền truy cập tương ứng.'),
 p('UC01 được mô tả riêng. Đăng nhập thành công là tiền điều kiện của nghiệp vụ, vì vậy không thêm quan hệ include từ mọi UC tới đăng nhập. Thanh toán cũng là mục tiêu xảy ra sau khi đã có hóa đơn; không bắt buộc mọi lần tạo hóa đơn đều thực hiện thanh toán ngay.'),
 note('Nếu hoàn thiện phân quyền, cần liên kết tài khoản với nhân viên/vai trò và bổ sung các quy tắc URL hoặc kiểm tra quyền ở phương thức. Thông tin chức vụ hiện chưa tạo ra quyền truy cập.'))

page('1.5. Biểu đồ Use Case tổng quát',
 image('01_use_case','Hình 1.1. Use Case của hệ thống nội bộ; vai trò theo nghiệp vụ.',18.4),
 p('Ranh giới hình chữ nhật là phạm vi ứng dụng. Mỗi UC biểu diễn một mục tiêu quản lý có kết quả quan sát được. Việc tách UC04 và UC05 giúp nêu rõ điều kiện khóa sửa hóa đơn sau thanh toán.'),
 note('Chưa bổ sung actor ngân hàng, khách đặt bàn trực tuyến hoặc hệ thống bếp vì chưa có tích hợp tương ứng trong phạm vi.'))

page('1.6. Danh sách ca sử dụng',
 table(['Mã','Tên và mục tiêu','Kết quả chính'],[
 ['UC01','Đăng nhập / đăng xuất','Xác thực tài khoản được cấp; tạo hoặc kết thúc phiên.'],
 ['UC02','Quản lý thực đơn','Loại món và món ăn được lưu hợp lệ; bảo vệ món/loại đã được tham chiếu.'],
 ['UC03','Tiếp nhận, xử lý đặt bàn','Lượt đặt được lưu; bàn được giữ hoặc trả trống khi xử lý.'],
 ['UC04','Lập, điều chỉnh hóa đơn','Chứng từ chưa thanh toán có các dòng món, giá và tổng tiền do server xác định.'],
 ['UC05','Ghi nhận thanh toán','Hóa đơn có món được đổi sang trạng thái đã thanh toán, không sửa/xóa qua service.'],
 ['UC06','Lập, điều chỉnh phiếu nhập','Phiếu và chi tiết được lưu; tồn nguyên liệu thay đổi đúng chênh lệch. Bao gồm xóa để hủy tác động phiếu.'],
 ['UC07','Xem báo cáo doanh thu','Hiển thị doanh thu theo ngày/tháng/khoảng ngày và thống kê bán hàng.'],
 ['UC08','Quản lý hồ sơ nhân viên','Hồ sơ, chức vụ, liên hệ, lương, ca và trạng thái được quản lý.'],
 ['UC09','Quản lý danh mục kho','Nhà cung cấp và nguyên liệu được tạo/cập nhật hợp lệ.'],
 ['UC10','Quản lý khách hàng, bàn ăn','Hồ sơ khách, bàn và lịch sử đặt được tra cứu/cập nhật.']],[.12,.39,.49]),
 h('Lựa chọn UC phân tích sâu'),
 p('UC03 có trạng thái và tài nguyên bàn dùng chung; UC04 có khóa ghép, giá lịch sử và tổng tiền; UC05 có điều kiện kết thúc chứng từ; UC06 có giao dịch nhiều bảng, chênh lệch tồn và xung đột phiên bản. Đây là các điểm quyết định tính đúng đắn của hệ thống. UC01 và UC07 được đặc tả để làm rõ bảo mật và cách hiểu số liệu báo cáo.'),
 p('UC02, UC08, UC09 và UC10 được trình bày ở mức mục tiêu/danh mục hỗ trợ, không cố tách mỗi nút thêm, sửa và xóa thành một UC độc lập.'))

page('1.7. Đặc tả UC01 và UC02',
 h('UC01 – Đăng nhập / đăng xuất'),
 table(['Thành phần','Đặc tả'],[
 ['Actor, mục tiêu','Nhân viên nội bộ; sử dụng ứng dụng bằng tài khoản được cấp.'],
 ['Tiền điều kiện','Có bản ghi TAI_KHOAN; mật khẩu lưu dưới dạng BCrypt; hệ thống truy cập được database.'],
 ['Luồng chính','1. Mở /login. 2. Nhập tên và mật khẩu. 3. Hệ thống chuẩn hóa tên, tải tài khoản và đối chiếu mật khẩu. 4. Tạo phiên xác thực, chuyển về trang chủ. 5. Khi đăng xuất, hệ thống hủy phiên.'],
 ['Thay thế / ngoại lệ','Sai thông tin: trở lại /login?error. Chưa đăng nhập mà mở nghiệp vụ: chuyển tới login. Đã đăng nhập mở login: chuyển trang chủ. Không có luồng tự đăng ký.'],
 ['Hậu điều kiện','Thành công: phiên được xác thực. Thất bại: không có phiên được cấp từ lần đăng nhập sai. Đăng xuất: phiên bị hủy.']],[.24,.76]),
 h('UC02 – Quản lý thực đơn'),
 table(['Thành phần','Đặc tả rút gọn'],[
 ['Actor / tiền điều kiện','Quản lý theo nghiệp vụ; đã đăng nhập.'],
 ['Luồng chính','Xem danh mục; thêm hoặc chọn bản ghi để sửa; nhập các trường; kiểm tra loại món, tên, giá, định dạng ảnh và trạng thái; lưu; hiển thị danh sách mới.'],
 ['Thay thế / ngoại lệ','Dữ liệu sai: trả form cùng lỗi. Loại bị xóa trước khi lưu món: yêu cầu chọn lại. Xóa loại đang có món hoặc món đã nằm trong hóa đơn: từ chối và gợi ý đổi trạng thái.'],
 ['Hậu điều kiện','Dữ liệu hợp lệ được lưu; chứng từ cũ không bị cập nhật chỉ vì thay đổi giá trong danh mục.']],[.24,.76]),
 note('Căn cứ cài đặt: SecurityConfig, TaiKhoanService, AuthController; LoaiMonService, MonAnService và các Form tương ứng.'))

page('1.8. Đặc tả UC03 – Tiếp nhận, xử lý đặt bàn',
 table(['Thành phần','Đặc tả'],[
 ['Actor / mục tiêu','Phục vụ; giữ một bàn cho nhu cầu của khách và theo dõi kết quả lượt đặt.'],
 ['Kích hoạt','Khách yêu cầu đặt bàn hoặc nhân viên xử lý một lượt đặt đã lưu.'],
 ['Tiền điều kiện','Nhân viên đã đăng nhập; có hồ sơ khách và bàn trong danh mục.'],
 ['Dữ liệu vào','Khách, bàn, ngày đặt, số người, tiền cọc nếu có.'],
 ['Luồng chính','1. Mở biểu mẫu, chọn khách và bàn. 2. Nhập ngày, số khách, tiền cọc. 3. Hệ thống kiểm tra dữ liệu và bàn còn trống. 4. Lưu lượt đặt ở CHO_XAC_NHAN, giữ bàn. 5. Nhân viên xác nhận: chuyển DA_XAC_NHAN. 6. Khi phục vụ xong, hoàn thành lượt đặt và trả bàn trống.'],
 ['Luồng thay thế','4a. Khách hủy trước khi hoàn thành: chuyển DA_HUY và trả bàn trống. 2a. Đổi thông tin: kiểm tra lại các ràng buộc khi lưu.'],
 ['Ngoại lệ','Bàn không tồn tại hoặc không trống: từ chối tiếp nhận. Số khách không dương/vượt sức chứa, tiền cọc âm, khách không tồn tại hoặc trạng thái kết thúc bị sửa lại: thiết kế yêu cầu từ chối. Hai nhân viên cùng giữ một bàn: thiết kế yêu cầu kiểm tra trong giao dịch có khóa.'],
 ['Hậu điều kiện','Thành công: lượt đặt và trạng thái bàn nhất quán. Thất bại: không giữ bàn hoặc lưu lượt đặt dở dang.']],[.23,.77]),
 h('Đối chiếu với cài đặt hiện có'),
 p('DatBanController kiểm tra trạng thái “Trống”, lưu lượt đặt rồi gọi BanAnService cập nhật bàn thành “Đã đặt”. Các thao tác xác nhận/hủy/hoàn thành có giao dịch trong DatBanService. Tuy nhiên luồng tiếp nhận chưa gộp hai bước ghi vào một giao dịch chung, chưa đủ kiểm tra sức chứa và chưa có khóa bảo vệ đặt đồng thời.'),
 note('Ngoại lệ và hậu điều kiện đầy đủ ở trên là yêu cầu thiết kế. Các điểm còn thiếu được giữ nguyên trong phần đánh giá, không mô tả thành tính năng đã hoàn thành.'))

page('1.9. Đặc tả UC04 – Lập, điều chỉnh hóa đơn',
 table(['Thành phần','Đặc tả'],[
 ['Actor / mục tiêu','Thu ngân; ghi các món khách sử dụng và xác định số tiền phải thanh toán.'],
 ['Tiền điều kiện','Đã đăng nhập; bàn, nhân viên, món tồn tại; hóa đơn được sửa phải chưa thanh toán.'],
 ['Dữ liệu vào','Mã bàn, người lập, khách nếu có; danh sách mã món và lượng; số tiền giảm giá.'],
 ['Luồng chính','1. Mở biểu mẫu lập hóa đơn. 2. Chọn bàn, người lập và các món. 3. Nhập lượng và giảm giá. 4. Hệ thống kiểm tra danh sách/giảm giá, gộp các dòng cùng món. 5. Tạo hóa đơn chưa thanh toán. 6. Với từng món, lấy giá hiện tại từ dữ liệu server và tạo chi tiết. 7. Tính tổng chi tiết, trừ giảm giá, chặn tối thiểu 0 và lưu. 8. Hiển thị chi tiết hóa đơn.'],
 ['Luồng sửa','Chọn hóa đơn chưa thanh toán; thay thông tin và danh sách món; kiểm tra; thay các dòng chi tiết; tính lại tổng trong cùng giao dịch.'],
 ['Ngoại lệ','Danh sách rỗng/khác kích thước, mã món rỗng, lượng không dương, giảm giá âm, món không tồn tại hoặc chưa có giá: từ chối và rollback. Hóa đơn đã thanh toán: từ chối sửa.'],
 ['Hậu điều kiện','Có đúng một chi tiết cho mỗi món sau gộp. Lượng, giá và tổng tiền hợp lệ. Nếu lỗi trong giao dịch, không còn phần đầu hoặc chi tiết được lưu riêng lẻ.']],[.23,.77]),
 p('Ví dụ: 1,5 kg tôm giá 320.000 đồng/kg và 2 đĩa nghêu giá 120.000 đồng/đĩa tạo tổng chi tiết 720.000 đồng. Giảm trực tiếp 20.000 đồng tạo tổng hóa đơn 700.000 đồng. Các giá trị là dữ liệu minh họa, không phải kết quả giao dịch đã chạy.'),
 note('Khi sửa hóa đơn chưa thanh toán, code đang thay toàn bộ chi tiết và lấy lại giá món hiện tại. Cần thống nhất chính sách có giữ giá cũ cho các món đã gọi hay không; chưa thể tuyên bố luôn bảo toàn giá cũ trong tình huống sửa.'))

page('1.10. Đặc tả UC05 – Ghi nhận thanh toán',
 table(['Thành phần','Đặc tả'],[
 ['Actor / mục tiêu','Thu ngân; xác nhận hóa đơn đã được thanh toán để kết thúc chứng từ bán hàng.'],
 ['Kích hoạt','Thu ngân chọn thao tác thanh toán trên hóa đơn.'],
 ['Tiền điều kiện','Đã đăng nhập; hóa đơn tồn tại, chưa thanh toán và có ít nhất một chi tiết. Thu ngân đã nhận/đối chiếu tiền bên ngoài ứng dụng.'],
 ['Luồng chính','1. Chọn hóa đơn. 2. Gửi yêu cầu thanh toán có mã hóa đơn. 3. Hệ thống tải hóa đơn, kiểm tra trạng thái. 4. Đếm chi tiết để bảo đảm có món. 5. Tính lại tổng tiền từ chi tiết và giảm giá. 6. Đổi trạng thái thành Da thanh toan và lưu. 7. Hiển thị kết quả.'],
 ['Ngoại lệ E1','Không tìm thấy hóa đơn: thông báo lỗi, không tạo bản ghi mới.'],
 ['Ngoại lệ E2','Hóa đơn đã thanh toán: từ chối yêu cầu; không thực hiện lại nghiệp vụ.'],
 ['Ngoại lệ E3','Không có món hoặc lưu thất bại: không đổi trạng thái; rollback khi lỗi trong giao dịch.'],
 ['Hậu điều kiện','Hóa đơn có trạng thái đã thanh toán; thao tác sửa/xóa qua HoaDonService bị từ chối; doanh thu có thể tổng hợp hóa đơn này.']],[.23,.77]),
 h('Giới hạn cần hiểu đúng'),
 p('Thanh toán ở đây là ghi nhận trạng thái trong ứng dụng, chưa có kết nối ngân hàng, thông tin số tiền khách đưa, tiền thừa hoặc biên nhận điện tử. Service hiện chưa gọi cập nhật trạng thái bàn. Vì vậy hậu điều kiện của UC05 hiện không bao gồm “bàn tự động trống”.'),
 p('Kiểm tra trạng thái trước khi lưu ngăn thao tác lặp tuần tự. Hai yêu cầu đồng thời vẫn có thể cùng đọc trạng thái cũ; thiết kế cần khóa hoặc kiểm soát phiên bản nếu muốn bảo đảm chỉ một yêu cầu hoàn tất. Việc có @Transactional chưa tự chứng minh đã giải quyết xung đột này.'))

page('1.11. Đặc tả UC06 – Lập, điều chỉnh phiếu nhập',
 table(['Thành phần','Đặc tả'],[
 ['Actor / mục tiêu','Thủ kho; ghi nhận lượng nguyên liệu nhập và điều chỉnh tồn chính xác khi sửa sai chứng từ.'],
 ['Tiền điều kiện','Đã đăng nhập; nhà cung cấp, người nhập và nguyên liệu tồn tại. Nếu sửa/xóa, phiếu tồn tại và form mang phiên bản hiện tại.'],
 ['Dữ liệu vào','Nhà cung cấp, nhân viên, ngày nhập; các dòng nguyên liệu–lượng–giá; phiên bản khi sửa.'],
 ['Luồng chính','1. Mở form và nhập thông tin. 2. Kiểm tra trường bắt buộc, lượng dương, giá không âm và không trùng nguyên liệu. 3. Khóa nhà cung cấp; nếu sửa thì khóa phiếu và kiểm tra phiên bản. 4. Khóa các nguyên liệu theo mã tăng dần. 5. Với mỗi nguyên liệu, lấy lượng cũ và lượng mới; tồn mới = tồn hiện tại + lượng mới − lượng cũ. 6. Kiểm tra tồn/giá trị tiền trong giới hạn; cập nhật phiếu, dòng và tổng tiền. 7. Lưu trong một giao dịch, hiển thị kết quả.'],
 ['Luồng xóa','Khóa phiếu và kiểm tra phiên bản; khóa nguyên liệu; trừ lại các lượng đã nhập; chỉ xóa nếu tồn sau điều chỉnh hợp lệ.'],
 ['Ngoại lệ','Form cũ; nhà cung cấp/nhân viên/nguyên liệu đã mất; dòng trùng; lượng/giá sai; nguyên liệu ngừng sử dụng mà bị nhập tăng; tồn âm/vượt ngưỡng hoặc tổng tiền quá lớn: từ chối toàn bộ giao dịch.'],
 ['Hậu điều kiện','Phiếu và chi tiết khớp tổng tiền; tồn đổi đúng chênh lệch. Thất bại: phiếu, chi tiết, tồn không bị cập nhật một phần.']],[.23,.77]),
 note('Tính tồn kho của luồng web thuộc PhieuNhapService. Procedure Oracle dùng cho quản trị SQL riêng; trigger hiện tính thành tiền và kiểm tra dòng, không cộng tồn thêm lần nữa.'))

page('1.12. Đặc tả UC07 và phạm vi các UC hỗ trợ',
 h('UC07 – Xem báo cáo doanh thu'),
 table(['Thành phần','Đặc tả'],[
 ['Actor / mục tiêu','Quản lý; xem số liệu doanh thu đã ghi nhận và thống kê món bán.'],
 ['Tiền điều kiện','Đã đăng nhập; có thể truy vấn hóa đơn và chi tiết.'],
 ['Luồng chính','1. Mở /bao-cao. 2. Chọn ngày bắt đầu/kết thúc; mặc định từ đầu tháng đến hôm nay. 3. Kiểm tra ngày. 4. Cộng TONG_TIEN của hóa đơn Da thanh toan trong khoảng. 5. Hiển thị doanh thu khoảng ngày, hôm nay, tháng này và các thống kê kèm theo.'],
 ['Ngoại lệ','Ngày kết thúc trước ngày bắt đầu: báo lỗi. Không có hóa đơn thỏa điều kiện: doanh thu bằng 0. Lỗi truy vấn: hiển thị lỗi, không thay đổi chứng từ.'],
 ['Hậu điều kiện','Chỉ đọc dữ liệu; không sửa hóa đơn hoặc trạng thái thanh toán.']],[.23,.77]),
 p('Khoảng ngày doanh thu dùng cận dưới bao gồm và cận trên không bao gồm ngày kết thúc cộng một ngày. Báo cáo hiện tính theo NGAY_LAP vì chưa có cột thời điểm thanh toán riêng. Bảng món bán chạy và tổng tiền theo hóa đơn đang lấy dữ liệu đã thanh toán toàn kỳ, không nhận khoảng ngày của bộ lọc doanh thu.'),
 h('Các UC hỗ trợ'),
 table(['UC','Luồng tiêu biểu và điều kiện quan trọng'],[
 ['UC08','Quản lý mở hồ sơ nhân viên, nhập/sửa liên hệ, chức vụ, lương, ca, trạng thái; kiểm tra dữ liệu rồi lưu. Từ chối xóa nhân viên đã có hóa đơn hoặc phiếu nhập.'],
 ['UC09','Thủ kho mở danh mục nhà cung cấp/nguyên liệu; nhập, sửa, xem hoặc xóa khi hợp lệ. Các tham chiếu chứng từ và ràng buộc tồn phải được bảo vệ.'],
 ['UC10','Phục vụ tìm khách, quản lý thông tin bàn/sức chứa và xem lịch sử đặt. Bản ghi được chứng từ/lượt đặt sử dụng cần được bảo vệ khi xóa.']],[.15,.85]),
 note('Vai trò là phân tích nghiệp vụ. Code hiện chưa giới hạn UC07 hoặc UC08 riêng cho quản lý.'))

page('1.13. Yêu cầu phi chức năng và tiêu chí chấp nhận',
 table(['Mã','Yêu cầu / tiêu chí đề xuất','Cách kiểm chứng'],[
 ['NFR01\nBảo mật','Trang nghiệp vụ yêu cầu đăng nhập; mật khẩu không lưu rõ; POST thay đổi dữ liệu yêu cầu CSRF hợp lệ.','Gửi yêu cầu chưa đăng nhập, sai mật khẩu và thiếu token; kiểm tra chỉ tồn tại chuỗi băm.'],
 ['NFR02\nToàn vẹn','Chứng từ nhiều bảng và tồn kho không bị cập nhật một phần khi lỗi.','Gây lỗi ở một dòng sau khi đã xử lý dòng trước, đối chiếu mọi bảng trước/sau.'],
 ['NFR03\nĐồng thời','Sửa phiếu bằng form cũ bị từ chối; đặt bàn/thanh toán cần có cơ chế chống hai lần xử lý xung đột.','Hai phiên cùng sửa một phiếu; mở rộng kịch bản đặt cùng bàn và thanh toán cùng hóa đơn.'],
 ['NFR04\nDễ sử dụng','Giao diện tiếng Việt; tiền/ngày thống nhất; lỗi chỉ rõ trường, giữ dữ liệu khi form hỗ trợ.','Thử dữ liệu sai và thao tác bàn phím trên các form; ghi nhận nơi đang redirect làm mất dữ liệu.'],
 ['NFR05\nHiệu năng','Mục tiêu tham khảo: 95% yêu cầu danh sách dưới 2 giây với 10 người dùng và 10.000 chứng từ trên máy thử được mô tả.','Đo bằng kịch bản tải sau khi có môi trường; chưa có số đo đáp ứng trong lần lập báo cáo.'],
 ['NFR06\nBảo trì','Tách Controller–Service–Repository; thay đổi quy tắc tiền hoặc tồn tập trung tại nghiệp vụ.','Truy vết UC04/UC06 tới phương thức; không tính tổng tin cậy chỉ từ JavaScript.'],
 ['NFR07\nTriển khai','Cấu hình Oracle qua biến môi trường; mô tả đúng phiên bản công cụ của dự án và dữ liệu kiểm thử.','Kiểm tra pom.xml và cấu hình; chạy trên JDK/phụ thuộc tương thích trước buổi bảo vệ.']],[.18,.47,.35]),
 p('Các ngưỡng hiệu năng là mục tiêu kiểm chứng do báo cáo đề xuất, không phải số liệu đo. Chưa có yêu cầu chính thức về tải hoặc khả dụng 24/7 nên không khẳng định hệ thống đạt mức vận hành thương mại.'),
 p('Đối với phạm vi bài tập, ưu tiên toàn vẹn chứng từ, xác thực và khả năng giải thích mô hình. Phân quyền chi tiết, xử lý đặt bàn đồng thời và thời điểm thanh toán là các cải thiện cần làm trước khi mở rộng sử dụng.'))

page('2.1. Tìm lớp khái niệm từ yêu cầu',
 h('CHƯƠNG 2. MÔ HÌNH PHÂN TÍCH'),
 p('Mô hình khái niệm tập trung vào các đối tượng mà người làm nghiệp vụ cần nhớ lâu dài: khách hàng, bàn ăn, lượt đặt, món ăn, loại món, nhân viên, hóa đơn, dòng hóa đơn, nhà cung cấp, nguyên liệu, phiếu nhập và dòng phiếu nhập. Mỗi khái niệm có dữ liệu và quan hệ xuất phát từ mục tiêu người dùng.'),
 table(['Khái niệm ứng viên','Quyết định và căn cứ'],[
 ['Lượt đặt bàn','Giữ riêng vì cùng khách/bàn có thể xuất hiện ở nhiều lần đặt; mỗi lần có ngày, số người, tiền cọc và trạng thái.'],
 ['Dòng hóa đơn','Giữ riêng vì quan hệ món–hóa đơn có số lượng, đơn giá bán và thành tiền. Không thể chỉ nối trực tiếp món với hóa đơn.'],
 ['Dòng phiếu nhập','Giữ riêng vì cùng nguyên liệu có thể nhập nhiều lần với giá và lượng khác nhau.'],
 ['Thanh toán','Ở phiên bản này chỉ là chuyển trạng thái hóa đơn, chưa có trả nhiều đợt/phương thức hoặc hoàn tiền; chưa tạo lớp giao dịch thanh toán riêng.'],
 ['Báo cáo','Là kết quả tính toán từ chứng từ; không lưu như một thực thể độc lập trong mô hình khái niệm.'],
 ['Đăng nhập, Controller, Repository','Thuộc tương tác hoặc thiết kế phần mềm; không đưa vào mô hình miền nhà hàng. Tài khoản xuất hiện ở thiết kế kỹ thuật.'],
 ['Món–nguyên liệu','Chưa có định lượng trong phạm vi; không vẽ quan hệ suy diễn và không tuyên bố có trừ tồn khi bán.']],[.29,.71]),
 p('Không đặt các khóa kỹ thuật MA_HD, MA_MON hoặc annotation JPA vào mô hình khái niệm. Mã định danh, khóa ngoại và chiến lược sinh mã được bổ sung khi chuyển sang thiết kế lưu trữ. Tên và thuộc tính ở mô hình này dùng ngôn ngữ nghiệp vụ để người đọc hiểu ý nghĩa trước khi nhìn cấu trúc code.'), chapter=2)

page('2.2. Mô hình khái niệm bán hàng và đặt bàn',
 image('02_khai_niem_ban_hang','Hình 2.1. Các khái niệm đặt bàn và bán hàng; không phải ERD.',16.5),
 p('Một khách có thể có nhiều lượt đặt; mỗi lượt đặt chỉ một bàn. Một bàn có nhiều lượt đặt và hóa đơn qua thời gian. Đây là quan hệ lịch sử, không có nghĩa cùng một bàn được phục vụ nhiều hóa đơn đang mở một cách không kiểm soát.'),
 p('Hóa đơn có khách tùy chọn để hỗ trợ khách vãng lai; bàn và người lập là bắt buộc theo yêu cầu nghiệp vụ. Hóa đơn hợp lệ gồm ít nhất một dòng. Quan hệ hợp thành thể hiện dòng hóa đơn không có vòng đời nghiệp vụ độc lập với hóa đơn.'),
 p('Mỗi dòng tham chiếu một món và giữ đơn giá bán của chứng từ. Mỗi món thuộc đúng một loại; loại mới tạo có thể chưa có món. Mô hình không quy định rằng lớp Hóa đơn trong code nhất thiết phải chứa collection dòng; đó là quyết định ánh xạ ở thiết kế.'))

page('2.3. Mô hình khái niệm nhập kho',
 image('03_khai_niem_nhap_kho','Hình 2.2. Các khái niệm nhập hàng và tồn nguyên liệu.',14),
 p('Phiếu nhập ghi một lần tiếp nhận hàng từ một nhà cung cấp, do một nhân viên lập. Phiếu có ít nhất một dòng; một dòng thuộc đúng một phiếu và một nguyên liệu. Nhà cung cấp, nhân viên và nguyên liệu có thể tồn tại khi chưa có chứng từ.'),
 p('Hai thuộc tính cùng gọi “đơn giá” ở thực đơn và nhập kho có ý nghĩa khác nhau. Giá món là giá bán; giá trên dòng nhập là giá mua nguyên liệu của lần nhập. Không dùng giá nhập để tự suy ra giá bán vì chưa có công thức định lượng và chi phí trong phạm vi.'),
 h('Bất biến nghiệp vụ cần giữ'),
 table(['Đối tượng','Bất biến'],[
 ['Dòng phiếu nhập','Lượng > 0; đơn giá ≥ 0; thành tiền được xác định theo lượng và đơn giá.'],
 ['Phiếu nhập','Không lặp nguyên liệu; tổng tiền bằng tổng các thành tiền sau làm tròn.'],
 ['Nguyên liệu','Tồn không âm; khi sửa/xóa phiếu phải phản ánh đúng tác động chênh lệch.'],
 ['Quan hệ phiếu–dòng','Dòng không đứng riêng; xóa phiếu kéo theo xử lý các dòng cùng tác động tồn.']],[.3,.7]))

page('2.4. Bảng CRC và phân bổ trách nhiệm',
 p('CRC dưới đây ở mức phân tích: diễn đạt đối tượng có thông tin gì và cần cộng tác với ai. Khi cài đặt theo mô hình Service, một phần trách nhiệm được hiện thực ở lớp nghiệp vụ thay vì đưa toàn bộ hành vi vào entity.'),
 table(['Lớp khái niệm','Trách nhiệm','Cộng tác'],[
 ['Khách hàng','Giữ hồ sơ, liên hệ và lịch sử liên quan.','Lượt đặt, hóa đơn.'],
 ['Bàn ăn','Giữ sức chứa, khu vực, tình trạng sử dụng.','Lượt đặt, hóa đơn.'],
 ['Lượt đặt bàn','Ghi nhu cầu; biết trạng thái và kết quả đặt.','Khách hàng, bàn ăn.'],
 ['Loại món','Phân loại các món và trạng thái danh mục.','Món ăn.'],
 ['Món ăn','Cung cấp tên, đơn vị và giá hiện tại khi gọi món.','Loại món, dòng hóa đơn.'],
 ['Hóa đơn','Ghi chứng từ bán; tổng hợp số tiền và trạng thái thanh toán.','Dòng hóa đơn, bàn, nhân viên, khách tùy chọn.'],
 ['Dòng hóa đơn','Giữ lượng và giá của món trên chứng từ.','Hóa đơn, món ăn.'],
 ['Nhân viên','Cung cấp thông tin người lập và hồ sơ nhân sự.','Hóa đơn, phiếu nhập.'],
 ['Nhà cung cấp','Giữ thông tin đơn vị cung ứng.','Phiếu nhập.'],
 ['Phiếu nhập','Ghi lần nhập; tổng hợp giá trị và tác động nhập hàng.','Nhà cung cấp, nhân viên, dòng nhập.'],
 ['Dòng phiếu nhập','Giữ nguyên liệu, lượng, giá của lần nhập.','Phiếu nhập, nguyên liệu.'],
 ['Nguyên liệu','Giữ đơn vị và tồn; cung cấp dữ liệu kiểm tra điều chỉnh.','Dòng nhập.']],[.22,.48,.3]),
 h('Chuyển trách nhiệm sang thiết kế'),
 p('HoaDonService phối hợp dữ liệu chứng từ và chi tiết để tính tiền; PhieuNhapService điều phối chênh lệch tồn và giao dịch. Repository chỉ cung cấp thao tác truy vấn/lưu. Controller tiếp nhận sự kiện HTTP và đưa kết quả lên giao diện. Cách chia này làm rõ nơi cần thay đổi khi quy tắc nghiệp vụ đổi.'))

page('2.5. SSD cho UC04 – Lập hóa đơn',
 image('04_ssd_lap_hoa_don','Hình 2.3. SSD của sự kiện lập hóa đơn.',16),
 p('Sự kiện chính mang các thông tin người dùng có quyền nhập: bàn, người lập, khách tùy chọn, mã món, lượng và giảm giá. Giá bán không được coi là dữ liệu tin cậy do trình duyệt quyết định. Hệ thống trả chứng từ có mã, chi tiết và tổng tiền hoặc trả thông báo không hợp lệ.'),
 p('Trong SSD, hệ thống được xem như hộp đen. Controller, Service và Repository chưa xuất hiện. Biểu đồ tuần tự thiết kế ở Hình 3.2 sẽ phân rã đúng sự kiện này thành các thông điệp giữa các đối tượng phần mềm.'),
 note('Tên sự kiện trong SSD là ngôn ngữ phân tích. Phương thức triển khai tương ứng là HoaDonService.taoHoaDon(HoaDon, List<Long>, List<BigDecimal>).'))

page('2.6. Điểm mơ hồ và hợp đồng nghiệp vụ',
 table(['Điểm cần quyết định','Cách xử lý trong báo cáo / hệ quả'],[
 ['Giá món thay đổi khi có hóa đơn cũ','Thay giá thực đơn không tự cập nhật chi tiết đã lưu. Riêng sửa toàn bộ hóa đơn chưa thanh toán đang lấy lại giá mới; ghi rõ và cần thống nhất chính sách.'],
 ['Tiền cọc có trừ khi thanh toán?','Chưa có liên kết lượt đặt–hóa đơn hoặc thao tác bù trừ. Báo cáo không cộng/trừ tiền cọc trong công thức thanh toán.'],
 ['Đặt bàn theo ngày hay theo giờ?','Entity dùng LocalDate; trạng thái bàn là toàn cục. Không khẳng định có lịch chống trùng theo khung giờ.'],
 ['Doanh thu theo ngày nào?','Theo NGAY_LAP và trạng thái đã thanh toán. Chưa có NGAY_THANH_TOAN để thống kê dòng tiền đúng thời điểm thu.'],
 ['Có tách bảng thanh toán?','Chưa cần với một trạng thái ghi nhận. Nếu có nhiều đợt hoặc hoàn tiền thì cần lớp/bảng giao dịch riêng.'],
 ['Tồn kho sau bán món?','Chưa có định lượng; tồn hiện phản ánh khởi tạo và nhập/điều chỉnh phiếu, chưa phải tồn đầy đủ của vận hành thực tế.']],[.35,.65]),
 h('Hợp đồng thao tác lập hóa đơn'),
 p('Tiền điều kiện: người dùng có phiên; danh mục hợp lệ; danh sách món không rỗng. Hậu điều kiện thành công: tạo Hóa đơn ở trạng thái chưa thanh toán; tạo một dòng cho mỗi món sau gộp; gán giá lấy từ server; tổng tiền bằng max(0, tổng dòng − giảm). Nếu lỗi trong giao dịch thì không ghi một phần chứng từ.'),
 h('Hợp đồng thao tác sửa phiếu nhập'),
 p('Tiền điều kiện: phiếu tồn tại và phiên bản khớp. Hậu điều kiện: phần đầu/chi tiết phản ánh dữ liệu mới; mỗi tồn thay đổi đúng lượng mới − lượng cũ; phiên bản làm form cũ mất hiệu lực. Toàn bộ thay đổi phải cùng commit hoặc rollback.'))

page('3.1. Kiến trúc và hướng phụ thuộc',
 h('CHƯƠNG 3. THIẾT KẾ HỆ THỐNG'),
 image('05_kien_truc','Hình 3.1. Tổ chức lớp và luồng dữ liệu của ứng dụng.',13.5),
 p('Boundary là trang/form và lớp Controller tiếp nhận HTTP; Control là các Service xử lý nghiệp vụ; Entity biểu diễn dữ liệu miền được lưu. Repository và cấu hình bảo mật là thành phần hỗ trợ kỹ thuật, không phải lớp khái niệm nhà hàng.'),
 p('Controller gọi Service, Service gọi các interface Repository và thao tác entity. Spring cung cấp đối tượng thực thi Repository. Các template nhận dữ liệu qua Model; chúng không trực tiếp truy vấn database. JavaScript phục vụ tương tác tìm kiếm/lọc, không thay thế kiểm tra server.'),
 p('Phương án này phù hợp quy mô nhóm 4 người và cách tổ chức mã sẵn có. Hạn chế là entity chủ yếu chứa dữ liệu, còn quy tắc tập trung ở Service; khi nghiệp vụ phức tạp hơn cần xem xét tách tính toán hoặc hành vi miền để tránh Service quá lớn.'), chapter=3)

page('3.2. Sequence Diagram – Lập hóa đơn',
 image('06_sequence_lap_hoa_don','Hình 3.2. Tương tác thực hiện UC04; tên lớp theo code.',18.5),
 p('Việc lưu phần đầu, tạo dòng và tính lại tổng nằm trong taoHoaDon có giao dịch. Giá được đọc tại MonAnRepository. Nếu một món không tồn tại sau khi đã xử lý món trước, ngoại lệ làm rollback toàn bộ giao dịch.'),
 note('Biểu đồ lược các lời gọi getter/setter và lần tải lại hóa đơn bên trong tinhLaiTongTien; không lược các bước kiểm tra, tính tiền và ghi chứng từ.'))

page('3.3. Sequence Diagram – Ghi nhận thanh toán',
 image('07_sequence_thanh_toan','Hình 3.3. Tương tác thực hiện UC05 và các nhánh từ chối.',18.5),
 p('Hóa đơn là đối tượng được kết thúc ở UC05. Kiểm tra trạng thái và số dòng xảy ra trước khi đổi sang Da thanh toan. Không thêm đối tượng cổng thanh toán hoặc thông điệp giải phóng bàn vì code chưa có những bước này.'),
 note('Cần bổ sung cơ chế khóa/phiên bản nếu yêu cầu chống hai lần thanh toán đồng thời; đây là khác biệt giữa bảo vệ trạng thái tuần tự và bảo vệ xung đột đồng thời.'))

page('3.4. Sequence Diagram – Điều chỉnh phiếu nhập',
 image('08_sequence_sua_phieu_nhap','Hình 3.4. Tương tác sửa phiếu thuộc UC06.',18.5),
 p('Phiếu được khóa và đối chiếu PHIEN_BAN. Nguyên liệu được khóa theo cùng thứ tự mã tăng dần để giảm nguy cơ deadlock; refresh lấy tồn mới sau khi có khóa. Phần chênh lệch là cơ sở điều chỉnh, giúp tránh cộng lại toàn bộ lượng nhập cũ.'),
 note('Các Repository được gom thành một lifeline vai trò để biểu đồ dễ đọc. Tệp nguồn và ghi chú nêu các lớp thực tế; DCD ở Hình 3.6 làm rõ những phụ thuộc chính.'))

page('3.5. DCD của phân hệ hóa đơn',
 image('09_dcd_hoa_don','Hình 3.5. Lớp thiết kế tiêu biểu cho UC04 và UC05.',19),
 p('CTHoaDonId là đối tượng khóa ghép theo hai mã; equals/hashCode phải nhất quán. HoaDon đang lưu các mã liên quan bằng Long; CTHoaDon giữ khóa ghép thay cho tham chiếu đối tượng HoaDon/MonAn. Không vẽ collection chi tiết trong HoaDon vì entity hiện chưa có thuộc tính đó.'),
 note('DCD chọn các thuộc tính/phương thức liên quan đến UC; các getter/setter thông thường được lược. Các phương thức Repository cơ bản là thao tác kế thừa từ JpaRepository.'))

page('3.6. DCD của phân hệ nhập kho',
 image('10_dcd_kho','Hình 3.6. Lớp thiết kế tiêu biểu cho UC06.',19),
 p('PhieuNhapForm tách dữ liệu nhập khỏi entity, có phiên bản và danh sách dòng. PhieuNhap quản lý collection CTPhieuNhap với cascade và orphanRemoval. Dòng tham chiếu nguyên liệu và phiếu; PhieuNhap tham chiếu nhà cung cấp, còn mã nhân viên vẫn lưu Long.'),
 note('Bội số 1..* diễn đạt trạng thái chứng từ hợp lệ sau lưu. Một entity PhieuNhap mới khởi tạo có thể tạm có danh sách rỗng trước khi Service hoàn tất kiểm tra.'))

page('3.7. State Machine của hóa đơn',
 image('11_trang_thai_hoa_don','Hình 3.7. Vòng đời hóa đơn theo các thao tác Service.',12.5),
 table(['Sự kiện','Điều kiện / tác động'],[
 ['Tạo','Thông tin và danh sách hợp lệ; tạo trạng thái Chua thanh toan.'],
 ['Sửa','Chỉ khi chưa thanh toán; thay thông tin/chi tiết và tính lại tổng.'],
 ['Thanh toán','Chưa thanh toán và có chi tiết; tính lại tổng; chuyển Da thanh toan.'],
 ['Xóa','Chỉ khi chưa thanh toán; xóa dòng trước khi xóa phần đầu.'],
 ['Thao tác sau thanh toán','Service từ chối sửa/xóa/thanh toán lại. Chưa có luồng hoàn tiền.']],[.27,.73]),
 p('“Đã xóa” biểu thị kết quả chấm dứt vòng đời bản ghi, không phải một chuỗi trạng thái lưu trong bảng. Sau thanh toán, hóa đơn vẫn tồn tại để tra cứu và tổng hợp; không biểu diễn việc xóa vật lý ở trạng thái này.'),
 p('Nếu sau này cần hoàn tiền, không nên đơn giản đổi hóa đơn đã thanh toán về chưa thanh toán vì làm mất ý nghĩa lịch sử. Khi đó cần một giao dịch điều chỉnh và quy tắc báo cáo riêng.'))

page('3.8. State Machine đặt bàn và khoảng cách cài đặt',
 image('12_trang_thai_dat_ban','Hình 3.8. Vòng đời đặt bàn đề xuất; cần bổ sung guard vào Service.',13.5),
 p('Mô hình đề xuất giữ DA_HUY và HOAN_THANH là trạng thái kết thúc. Một lượt đặt chỉ hoàn thành sau khi đã xác nhận; thao tác hủy áp dụng trước khi kết thúc. Guard cần được kiểm tra tại Service thay vì chỉ ẩn nút trên giao diện.'),
 table(['Khía cạnh','Hiện có','Thiết kế cần bổ sung'],[
 ['Trạng thái nguồn','xacNhan/huyDatBan/hoanThanh gán trực tiếp.','Kiểm tra cặp trạng thái nguồn–sự kiện và từ chối bước không hợp lệ.'],
 ['Cập nhật bàn','Hủy/hoàn thành đặt bàn đưa bàn về Trống.','Kiểm tra các lượt đặt/các giao dịch còn hoạt động trước khi trả bàn.'],
 ['Tạo lượt đặt','Controller kiểm tra Trống rồi lưu và giữ bàn.','Chuyển sang một giao dịch Service và có cơ chế khóa chống giữ trùng.']],[.23,.37,.4]),
 note('Không mô tả sơ đồ này là máy trạng thái đã được cài đặt đầy đủ. Đây là đề xuất nhằm đáp ứng BR01–BR02 và khắc phục các chuyển trạng thái chưa được bảo vệ.'))

page('3.9. Giải thích quyết định thiết kế bằng GRASP/SOLID',
 table(['Nguyên tắc','Áp dụng cụ thể và hệ quả'],[
 ['Controller','HoaDonController nhận sự kiện lập/thanh toán, chuyển cho HoaDonService. HTTP và redirect nằm ở Controller; quy tắc tiền không rải vào template.'],
 ['Information Expert','Dữ liệu giá nằm ở MonAn; dữ liệu lượng/giá chứng từ ở CTHoaDon. Service tổng hợp qua Repository vì cách cài đặt hiện dùng entity dữ liệu và truy vấn tổng. Đây là sự phân bổ có chủ ý, không khẳng định entity đang có mọi hành vi.'],
 ['Creator','HoaDonService tạo phần đầu và dòng khi đã có danh sách hợp lệ; PhieuNhap quản lý vòng đời collection dòng. Lý do là chứng từ quyết định sự tồn tại và tính hợp lệ của các dòng.'],
 ['High Cohesion / SRP','MonAnService xử lý thực đơn; PhieuNhapService xử lý phiếu/tồn; BaoCaoService phục vụ tổng hợp. Thay đổi chính sách giảm giá tập trung ở hóa đơn thay vì ảnh hưởng CRUD nhân viên.'],
 ['Low Coupling / DIP','Service phụ thuộc interface Repository, không tự mở kết nối SQL cho từng thao tác. Repository có thể được cung cấp triển khai/test khác; entity và truy vấn JPA vẫn tạo phụ thuộc nền tảng cần thừa nhận.'],
 ['Tách dữ liệu đầu vào','DTO/Form tránh cho người dùng tùy ý ghi khóa hay tổng tiền ở các module đã dùng DTO. Module hóa đơn hiện bind Entity nên cần bổ sung DTO, xác thực trường và giảm phạm vi binding.']],[.26,.74]),
 h('Các phương án đã cân nhắc'),
 p('Đưa tính tiền vào Controller làm tăng phụ thuộc HTTP và khó tái sử dụng; tính ở trình duyệt có thể bị sửa dữ liệu gửi lên. Nhóm chọn Service cho quy tắc tiền. Với tồn kho, nếu cả Java và trigger cùng tăng tồn thì bị cộng hai lần; thiết kế hiện chỉ cho Service điều chỉnh tồn của luồng web.'),
 p('Chưa tạo Strategy giảm giá hoặc cây kế thừa các loại nhân viên vì phạm vi chưa có nhiều biến thể hành vi. Không thêm interface hoặc lớp con chỉ để liệt kê đủ SOLID; ưu tiên quyết định có tác động thực tế tới phạm vi này.'))

page('3.10. ERD bán hàng, khách và đặt bàn',
 image('13_erd_ban_hang','Hình 3.9. ERD logic bán hàng và đặt bàn; kiểu dữ liệu là thiết kế tham chiếu.',19),
 note('Đường liền: quan hệ có ánh xạ đối tượng JPA. Đường chấm và nhãn FK logic: quan hệ nghiệp vụ được lưu bằng mã, chưa tự chứng minh có FK vật lý. Phải kiểm tra DDL Oracle thực tế. Kiểu NUMBER tổng quát cho mã không quy định lại độ rộng số Hibernate sinh.'),
 p('Hình thể hiện 8 bảng nghiệp vụ. 4 bảng kho ở trang sau tạo đủ 12 bảng theo ảnh tham khảo. Các cột ảnh/mô tả món và hồ sơ nhân viên mở rộng được lấy từ entity hiện tại.'))

page('3.11. ERD nhập kho và bảng tài khoản bổ sung',
 image('14_erd_kho_tai_khoan','Hình 3.10. ERD kho, hồ sơ nhân viên và TAI_KHOAN.',17.5),
 p('NHAN_VIEN là bảng dùng chung, được lặp ở hai hình để dễ đọc; không phải hai bảng khác nhau. TAI_KHOAN là bảng kỹ thuật bổ sung, tạo tổng cộng 13 entity. Tên đăng nhập có ràng buộc duy nhất và độ dài 30 theo entity; không có cột vai trò hoặc khóa ngoại tới nhân viên.'),
 note('PHIEU_NHAP.MA_NV có FK trong script 01_inventory.sql nhưng chỉ là Long trong JPA. Nếu schema tạo bằng Hibernate thay vì script, cần xác nhận FK này. Các độ dài VARCHAR2 ở ERD là mức thiết kế; kiểm tra CHAR/BYTE semantics trong database khi triển khai.'))

page('3.12. Khóa, ánh xạ và ràng buộc dữ liệu',
 table(['Bảng','Khóa / quan hệ chính','Cách lưu và kiểm tra'],[
 ['KHACH_HANG','PK MA_KH','Identity; hồ sơ và điểm tích lũy.'],
 ['BAN_AN','PK MA_BAN','Identity; sức chứa, khu vực, trạng thái.'],
 ['DAT_BAN','PK MA_DAT; MA_KH, MA_BAN','Identity; ManyToOne tới khách và bàn.'],
 ['LOAI_MON','PK MA_LOAI','SEQ_LOAI_MON; bảo vệ xóa khi có món.'],
 ['MON_AN','PK MA_MON; MA_LOAI','SEQ_MON_AN; ManyToOne loại; giá BigDecimal.'],
 ['NHAN_VIEN','PK MA_NV','SEQ_NHAN_VIEN; bảo vệ xóa khi có chứng từ.'],
 ['HOA_DON','PK MA_HD; MA_BAN, MA_NV, MA_KH','Identity; mã cha Long; khách có thể trống.'],
 ['CT_HOA_DON','PK (MA_HD, MA_MON)','EmbeddedId; giá bán lưu trên chi tiết.'],
 ['NHA_CUNG_CAP','PK MA_NCC','SEQ_NHA_CUNG_CAP.'],
 ['NGUYEN_LIEU','PK MA_NL','SEQ_NGUYEN_LIEU; tồn NUMBER(10,2).'],
 ['PHIEU_NHAP','PK MA_PN; MA_NCC, MA_NV','SEQ_PHIEU_NHAP; có PHIEN_BAN; NCC là ManyToOne.'],
 ['CT_PHIEU_NHAP','PK (MA_PN, MA_NL)','EmbeddedId/MapsId; ManyToOne tới phiếu và nguyên liệu.'],
 ['TAI_KHOAN','PK MA_TK; UQ TEN_DANG_NHAP','SEQ_TAI_KHOAN; mật khẩu BCrypt.']],[.28,.31,.41]),
 p('Khóa ghép bảo đảm một món/nguyên liệu không lặp ở cùng chứng từ. Quan hệ nhiều–nhiều được chuyển thành bảng chi tiết vì có thuộc tính riêng. Controller, Service, DTO và báo cáo không trở thành bảng vì không đại diện dữ liệu bền vững độc lập.'),
 p('Đối với kho, script 01_inventory.sql có CHECK số lượng, giá, tồn và FK. Hibernate ddl-auto=update không thay thế việc kiểm tra đầy đủ CHECK, FK và quyền trên một schema đã tồn tại. Không chạy lại script tạo bảng kho trên schema đã có bảng nếu chưa lập kế hoạch chuyển đổi.'))

page('3.13. Thiết kế giao dịch và ví dụ tính toán',
 h('Tính tiền hóa đơn'),
 p('Thành tiền dòng i = số lượng i × đơn giá i. Tổng hóa đơn = max(0, Σ thành tiền dòng − giảm giá). Tiền dùng BigDecimal để biểu diễn thập phân. Tại kho, thành tiền được làm tròn 2 chữ số theo HALF_UP; cần kiểm tra khả năng lưu đúng precision/scale của các cột tiền.'),
 table(['Tình huống minh họa','Kết quả mong đợi'],[
 ['Tôm 1,5 × 320.000; nghêu 2 × 120.000; giảm 20.000','Tổng dòng 720.000; phải thu 700.000 đồng.'],
 ['Cùng món tôm nhập 0,5 kg và 1 kg','Gộp thành 1,5 kg trước khi tạo khóa (MA_HD, MA_MON).'],
 ['Tổng dòng 720.000; giảm 800.000','Code hiện cho tổng 0; cần thống nhất có chấp nhận mức giảm vượt tổng hay từ chối.'],
 ['Đổi giá món sau khi hóa đơn đã thanh toán','Chi tiết đã lưu không tự cập nhật theo giá danh mục mới.']],[.59,.41]),
 h('Điều chỉnh tồn theo chênh lệch'),
 table(['Bước minh họa','Tồn trước','Tác động','Tồn sau'],[
 ['Tồn đầu trước phiếu','10 kg','—','10 kg'],
 ['Lập phiếu nhập 5 kg','10 kg','+5 kg','15 kg'],
 ['Sửa lượng phiếu từ 5 thành 3 kg','15 kg','3 − 5 = −2 kg','13 kg'],
 ['Xóa phiếu đang ghi 3 kg','13 kg','−3 kg','10 kg']],[.48,.17,.19,.16]),
 p('Khóa phiếu/nguyên liệu ngăn cập nhật dựa trên dữ liệu vừa bị thay đổi. Phiên bản giúp phát hiện người dùng gửi form cũ. Hai cơ chế có vai trò bổ sung: một cơ chế bảo vệ lúc ghi, một cơ chế phát hiện nội dung mà người dùng đang xem đã lỗi thời.'),
 note('Các con số trên là ví dụ thiết kế để xây dựng test, chưa phải log chạy giao dịch trên database.'))

page('3.14. Ma trận truy vết và kiểm tra nhất quán',
 table(['Yêu cầu','Mô hình phân tích','Thiết kế / code','Kiểm chứng'],[
 ['UC03; BR01–02','Bàn, lượt đặt, khách; CRC bàn/lượt đặt.','State Hình 3.8; DatBanController/Service.','TC01–TC03; cần bổ sung guard và khóa.'],
 ['UC04; BR03–05','Hóa đơn, dòng, món; SSD Hình 2.3.','Sequence 3.2; DCD 3.5; taoHoaDon/capNhatHoaDon.','TC04–TC07; đối chiếu bảng đầu–dòng và công thức.'],
 ['UC05; BR06','Vòng đời hóa đơn.','Sequence 3.3; State 3.7; thanhToan.','TC08–TC10; kiểm tra lỗi và đồng thời.'],
 ['UC06; BR07–10','Phiếu, dòng nhập, nguyên liệu; CRC kho.','Sequence 3.4; DCD 3.6; PhieuNhapService.','TC11–TC16; tồn, rollback, form cũ.'],
 ['UC02/08; BR11','Món, loại, nhân viên với chứng từ.','Service danh mục và FK khi có.','Bộ Java có test bảo vệ xóa; cần chạy lại.'],
 ['UC07; BR12','Hóa đơn đã thanh toán là dữ liệu nguồn.','BaoCaoService và truy vấn tổng hợp.','TC17–TC18; phân biệt lọc doanh thu/toàn kỳ.'],
 ['UC01; NFR01','Actor nhân viên; tiền điều kiện phiên.','SecurityConfig, TaiKhoanService.','TC19–TC20; có mã test Java, chưa chạy lại.']],[.18,.25,.34,.23]),
 h('Các điểm đã đối chiếu giữa mô hình và code'),
 p('Tên UC04 và UC05 tương ứng hai mục tiêu và hai phương thức nghiệp vụ khác nhau; không gộp tạo đơn với thanh toán. Khóa chi tiết gồm hai mã đúng với quyết định gộp món. DCD kho có phiên bản phù hợp với nhánh form cũ ở Sequence. Mô hình đặt bàn được gắn nhãn đề xuất vì chưa có guard tương ứng đầy đủ trong code.'),
 p('Trước khi nộp, nhóm cần kiểm tra mọi thay đổi tên lớp, trạng thái hoặc chính sách giá có được cập nhật đồng thời trong UC, sơ đồ, mã nguồn và test case. Việc một sơ đồ đẹp không bù cho mâu thuẫn giữa tiền điều kiện và thông điệp thực thi.'))

page('4.1. Công nghệ và cấu trúc dự án',
 h('CHƯƠNG 4. CÀI ĐẶT VÀ KIỂM THỬ'),
 table(['Thành phần','Bằng chứng trong dự án'],[
 ['Java / Spring Boot','pom.xml đang khai báo java.version=27 và Spring Boot parent 4.1.1. Đây là giá trị cấu hình trong repo, không phải xác nhận môi trường hiện chạy được.'],
 ['Web / giao diện','Spring Web MVC, Thymeleaf; HTML/CSS/JavaScript trong src/main/resources.'],
 ['Nghiệp vụ / lưu trữ','Service giao dịch; Spring Data JPA; JDBC cho repository nhân viên dùng ở nhập kho.'],
 ['Database','Oracle JDBC ojdbc17; DB_URL, DB_USERNAME, DB_PASSWORD trong application.properties.'],
 ['Bảo mật / kiểm tra form','Spring Security; BCrypt độ khó 12; Jakarta Validation và DTO.'],
 ['Kiểm thử','JUnit/Spring test và H2 theo profile test; Node test cho bộ lọc JavaScript.']],[.27,.73]),
 code('QuanLyNHHS/\n  src/main/java/com/QuanLyNHHS/\n    controller/  service/  repository/\n    model/       dto/      config/\n  src/main/resources/\n    templates/   static/   application.properties\n  src/test/java/   src/test/js/\n  database/oracle/\n    01_inventory.sql   02_routines.sql\n    03_insert_data.sql 04_du_lieu_kiem_thu.sql'),
 p('Cấu hình hiện dùng ddl-auto=update và kết nối Oracle mặc định ở localhost, service FREEPDB1. Báo cáo không sao chép mật khẩu database hoặc thông tin bí mật vào nội dung. Khi chạy demo phải chuẩn bị service/schema đúng và bộ công cụ Java tương thích với pom.xml.'),
 note('Không thay đổi mã nguồn hay cấu hình chạy của ứng dụng trong quá trình tạo báo cáo này.'), chapter=4)

page('4.2. Minh chứng cài đặt UC04 và UC05',
 p('HoaDonController nhận POST /hoa-don/them và gọi taoHoaDon(hoaDon, maMon, soLuong). Service kiểm tra danh sách, gộp mã món, lưu hóa đơn chưa thanh toán rồi tạo các dòng với giá lấy từ MonAnRepository. Các đoạn dưới trích logic có trong code, lược chú thích và tên biến phụ để tập trung vào quyết định thiết kế.'),
 code('BigDecimal thanhTien = donGia.multiply(soLuongMon);\nCTHoaDon chiTiet = new CTHoaDon();\nchiTiet.setId(new CTHoaDon.CTHoaDonId(maHd, idMon));\nchiTiet.setSoLuong(soLuongMon);\nchiTiet.setDonGia(donGia);\nchiTiet.setThanhTien(thanhTien);\nctHoaDonRepository.save(chiTiet);'),
 p('Ở tinhLaiTongTien, dữ liệu tổng hợp của chi tiết được trừ giảm giá và chặn kết quả âm về 0. Trình duyệt không được quyết định giá trị phải thu cuối cùng. Phương thức thanh toán có giao dịch và các điều kiện từ chối quan trọng:'),
 code('@Transactional\npublic HoaDon thanhToan(Long maHd) {\n    HoaDon hoaDon = timTheoMa(maHd);\n    if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {\n        throw new IllegalStateException(\n            "Hoa don da duoc thanh toan");\n    }\n    if (ctHoaDonRepository.countById_MaHd(maHd) == 0) {\n        throw new IllegalStateException(\n            "Hoa don chua co mon an");\n    }\n    tinhLaiTongTien(maHd);\n    hoaDon.setTrangThai(DA_THANH_TOAN);\n    return hoaDonRepository.save(hoaDon);\n}'),
 note('Nguồn: service/HoaDonService.java. Lập trình thực tế có các bước tải dữ liệu lặp lại; Sequence lược bước kỹ thuật này nhưng giữ điều kiện và tác động chính.'))

page('4.3. Minh chứng UC06 và xác thực tài khoản',
 h('Phiếu nhập và tồn kho'),
 p('PhieuNhapService.save nhận PhieuNhapForm đã kiểm tra @Valid. Với phiếu cũ, phương thức khóa phiếu, so phiên bản và tăng phiên bản khi cần. Service tạo tập nguyên liệu của cả chi tiết cũ và mới, khóa chúng theo thứ tự mã rồi áp dụng chênh lệch.'),
 code('BigDecimal before = previousDetail == null\n    ? BigDecimal.ZERO : previousDetail.getSoLuong();\nBigDecimal after = line == null\n    ? BigDecimal.ZERO : line.getSoLuong();\nadjustStock(ingredient, after.subtract(before));'),
 p('Cách làm này xử lý được dòng thêm mới, dòng đổi lượng và dòng bị bỏ. Không xóa rồi chèn lại mọi chi tiết cũ trong tất cả trường hợp; giữ khóa của dòng còn tồn tại và cập nhật giá trị. Khi xóa phiếu, service trừ lại lượng đã nhập trước khi xóa.'),
 h('Quyền ứng dụng và quyền Oracle'),
 table(['Tầng','Cài đặt / cách hiểu'],[
 ['Ứng dụng web','SecurityConfig cho phép /login và tài nguyên tĩnh; yêu cầu xác thực các URL còn lại. BCrypt kiểm tra mật khẩu; logout hủy phiên.'],
 ['Tài khoản','TaiKhoanService chuẩn hóa tên và trả UserDetails. Mọi tài khoản nhận ROLE_USER. Không có tự đăng ký ở AuthController.'],
 ['Oracle','oracle_setup_user.sql có cấu hình user/role minh họa. Role/privilege Oracle kiểm soát tài khoản database; không tự trở thành vai trò từng người dùng website.'],
 ['Routine kho','SP_CAP_NHAT_TON_KHO và SP_TINH_TONG_PHIEU_NHAP dùng cho quản trị SQL. Trigger TRG_CTPN_THANH_TIEN kiểm tra/tính tiền dòng, không tăng tồn.']],[.25,.75]),
 note('Sự có mặt của script chưa chứng minh script đã chạy trên database hiện dùng. Chưa có căn cứ để ghi “đã hoàn thiện phân quyền quản lý, thu ngân, thủ kho”.'))

page('4.4. Giao diện và điểm kiểm tra khi chạy demo',
 table(['UC / trang','Đường dẫn','Điểm cần quan sát'],[
 ['UC01 – đăng nhập','/login','Tên trường tenDangNhap, matKhau; sai thông tin báo lỗi; không có đăng ký tự do.'],
 ['UC02 – thực đơn','/loai-mon; /mon-an','Danh sách đọc dữ liệu; form thêm/sửa; lọc món theo loại/từ khóa; kiểm tra giá sai.'],
 ['UC03 – đặt bàn','/dat-ban','Chọn khách/bàn, ngày, số người, cọc; trạng thái giữ bàn, xác nhận/hủy/hoàn thành.'],
 ['UC04/05 – hóa đơn','/hoa-don; /hoa-don/chi-tiet/{maHd}','Món, lượng, giá và tổng; nút thanh toán; thử từ chối sửa chứng từ đã thanh toán.'],
 ['UC06 – phiếu nhập','/phieu-nhap','Form nhiều dòng; thêm/sửa/xóa; đối chiếu tồn và lỗi form cũ bằng hai phiên.'],
 ['UC07 – báo cáo','/bao-cao','Khoảng ngày hợp lệ; doanh thu đã thanh toán; phạm vi toàn kỳ của bảng món bán chạy.'],
 ['UC08 – nhân viên','/nhan-vien','Hồ sơ, chức vụ, ca, lương; bảo vệ xóa người đã có chứng từ.'],
 ['UC09 – kho','/nha-cung-cap; /nguyen-lieu','Thông tin nhà cung cấp, đơn vị, tồn và trạng thái.'],
 ['UC10 – khách/bàn','/khach-hang; /ban-an','Tìm kiếm khách, sức chứa/khu vực bàn, lịch sử đặt.']],[.25,.32,.43]),
 p('Các trang thực tế dùng template Thymeleaf và layout chung tại templates/layout. Bảng trên là bản đồ giao diện đối chiếu mã, không phải ảnh chụp ứng dụng đang chạy. Không sử dụng ảnh dựng để làm bằng chứng rằng nghiệp vụ đã được kiểm thử.'),
 p('Nếu cần bổ sung ảnh demo theo yêu cầu giảng viên, nên chụp sau khi chạy Oracle: một hóa đơn có chi tiết, lỗi sửa chứng từ đã thanh toán, tồn trước/sau khi sửa phiếu và kết quả từ chối form cũ. Ảnh phải đi kèm dữ liệu vào và giá trị đối chiếu; chỉ ảnh danh sách chưa chứng minh quy tắc nghiệp vụ.'))

page('4.5. Phạm vi và kết quả kiểm chứng thực tế',
 h('Đã thực hiện trong lần biên soạn ngày 13/11/2026'),
 p('Đã đọc tài liệu PDF, bảng phân công Excel, các entity, Service/Controller chính, cấu hình bảo mật, script kho và mã kiểm thử. Đã chạy bộ kiểm thử JavaScript bằng lệnh dưới tại thư mục gốc dự án:'),
 code('node --test QuanLyNHHS/src/test/js/menu-filters.test.cjs'),
 table(['Kiểm thử JavaScript','Kết quả thực tế'],[
 ['Tìm món kết hợp bộ lọc loại khi thay đổi từng điều kiện','Đạt.'],
 ['Tìm nhân viên kết hợp chức vụ đã chọn','Đạt.'],
 ['Tìm loại món theo tên/mã và xóa từ khóa','Đạt.'],
 ['Chuyển giao diện món dạng lưới/bảng và nút đang chọn','Đạt.']],[.8,.2]),
 p('Kết quả: 4 tests, 4 pass, 0 fail. Bộ test dùng mô phỏng DOM và JavaScript main.js; chỉ xác nhận tương tác lọc/hiển thị trong phạm vi đó. Log được lưu kèm thư mục nguồn báo cáo.'),
 h('Các bằng chứng hiện có nhưng chưa chạy lại'),
 p('MenuCrudIntegrationTests có 16 phương thức @Test; DatabasePagesTests có 4. Chúng bao gồm CRUD, validation, bảo vệ xóa, CSRF, tài khoản được cấp và từ chối đăng ký. Đây là bằng chứng về kịch bản test có trong repo, chưa phải kết quả chạy trong lần này.'),
 p('Thư mục target/surefire-reports ở gốc có báo cáo cũ cho QuanLyNhhsApplicationTests: 1 test, 0 lỗi. Không dùng kết quả cũ đó để kết luận mọi module hiện tại hoặc Oracle đã đạt. Bộ Java chưa được chạy lại: pom yêu cầu Java 27, trong khi Java tìm thấy từ Android Studio là bản 25.0.2. Không đổi cấu hình dự án để tạo kết quả kiểm thử khác môi trường.'),
 note('Chưa khởi động ứng dụng hoặc chạy test giao dịch trên Oracle. Những trường hợp ở hai trang tiếp theo là kế hoạch kiểm chứng, chưa có kết quả thực tế.'))

page('4.6. Test case đặt bàn, hóa đơn và thanh toán',
 table(['Mã / điều kiện và dữ liệu','Kết quả mong đợi','Thực tế lần này'],[
 ['TC01 – UC03: bàn Trống; khách hợp lệ; 4 người, bàn 6 chỗ.','Tạo CHO_XAC_NHAN và giữ bàn; dữ liệu bàn/lượt đặt nhất quán.','Chưa chạy.'],
 ['TC02 – UC03: chọn bàn đã được giữ.','Từ chối; không có lượt đặt mới.','Chưa chạy.'],
 ['TC03 – UC03: hai phiên cùng đặt bàn Trống.','Chỉ một lượt được giữ; phiên còn lại báo xung đột. Đây là tiêu chí cần bổ sung khóa.','Chưa chạy; code có khoảng trống.'],
 ['TC04 – UC04: tôm 1,5 × 320.000; nghêu 2 × 120.000; giảm 20.000.','Hai dòng, tổng 700.000; trạng thái chưa thanh toán.','Chưa chạy.'],
 ['TC05 – UC04: cùng món có hai lượng 0,5 và 1.','Một dòng khóa ghép; lượng 1,5; tính tiền đúng.','Chưa chạy.'],
 ['TC06 – UC04: lượng 0/âm; giảm âm; danh sách rỗng.','Từ chối; không lưu phần đầu/chi tiết.','Chưa chạy.'],
 ['TC07 – UC04: dòng đầu hợp lệ, món ở dòng sau không tồn tại.','Rollback mọi thay đổi của lần tạo. Đối chiếu cả HOA_DON và CT_HOA_DON.','Chưa chạy.'],
 ['TC08 – UC05: hóa đơn chưa thanh toán, có dòng.','Tính lại tổng, chuyển Da thanh toan; thử sửa/xóa sau đó bị từ chối.','Chưa chạy.'],
 ['TC09 – UC05: thanh toán lần hai hoặc hóa đơn không có dòng.','Báo lỗi; không thay đổi chứng từ.','Chưa chạy.'],
 ['TC10 – UC05: hai phiên thanh toán cùng hóa đơn.','Một lần kết thúc hợp lệ; yêu cầu sau bị từ chối hoặc trả kết quả nhất quán. Cần cơ chế đồng thời.','Chưa chạy; code có khoảng trống.']],[.48,.38,.14]),
 p('Sau mỗi test ghi, phải truy vấn đối chiếu phần đầu và chi tiết bằng mã chứng từ. Với test rollback, lưu số lượng bản ghi và giá trị trước khi gửi yêu cầu. Kiểm tra thông báo giao diện đơn thuần chưa đủ để kết luận không còn dữ liệu dở dang.'))

page('4.7. Test case kho, báo cáo và bảo mật',
 table(['Mã / điều kiện và dữ liệu','Kết quả mong đợi','Thực tế lần này'],[
 ['TC11 – UC06: tồn đầu 10 kg, lập phiếu 5 kg.','Tồn 15; tổng phiếu đúng; phiếu/dòng lưu cùng nhau.','Chưa chạy.'],
 ['TC12 – UC06: sửa phiếu 5 thành 3 kg.','Tồn từ 15 thành 13; không cộng thêm toàn bộ 3 kg.','Chưa chạy.'],
 ['TC13 – UC06: xóa phiếu đang ghi 3 kg, tồn 13.','Tồn còn 10; phiếu và dòng bị xóa.','Chưa chạy.'],
 ['TC14 – UC06: hai form cùng phiên bản; lưu form thứ nhất rồi form thứ hai.','Form thứ hai bị từ chối; không ghi đè phiếu/tồn mới.','Chưa chạy.'],
 ['TC15 – UC06: lặp nguyên liệu; lượng 0; giá âm; vượt độ chính xác.','Báo lỗi; không lưu và không đổi tồn.','Chưa chạy.'],
 ['TC16 – UC06: có một dòng gây tồn âm/vượt ngưỡng sau khi đã xử lý dòng khác.','Rollback toàn bộ, gồm tồn các dòng xử lý trước đó.','Chưa chạy.'],
 ['TC17 – UC07: hóa đơn đã/chưa thanh toán trong và ngoài khoảng ngày.','Chỉ cộng TONG_TIEN của hóa đơn đã thanh toán đúng khoảng.','Chưa chạy.'],
 ['TC18 – UC07: khoảng ngày ngược; dữ liệu rỗng; đổi bộ lọc ngày.','Ngày ngược báo lỗi; rỗng = 0. Món bán chạy hiện vẫn toàn kỳ, cần giải thích rõ.','Chưa chạy.'],
 ['TC19 – UC01: tài khoản cấp với BCrypt; đúng/sai mật khẩu.','Đúng: vào trang chủ; sai: không tạo phiên, báo lỗi chung.','Chưa chạy.'],
 ['TC20 – UC01: chưa đăng nhập; POST thiếu CSRF; thử URL đăng ký.','Nghiệp vụ yêu cầu đăng nhập; POST thiếu token bị từ chối; không tạo tài khoản qua đăng ký.','Chưa chạy.']],[.48,.38,.14]),
 p('Dữ liệu script 04_du_lieu_kiem_thu.sql có đủ 13 bảng, gồm một hóa đơn đã thanh toán và một hóa đơn chưa thanh toán. Chỉ dùng trên môi trường demo phù hợp; ứng dụng không tự chạy script. Không coi việc có script là kết quả thực thi các test trên.'))

page('4.8. Giới hạn sản phẩm và hướng hoàn thiện',
 table(['Ưu tiên','Vấn đề đã nhận diện','Hướng xử lý / tiêu chí hoàn thành'],[
 ['Cao','Vai trò nghiệp vụ chưa thành quyền website.','Liên kết tài khoản–nhân viên/vai trò; cấu hình quyền theo chức năng; test từng vai trò được phép và bị từ chối.'],
 ['Cao','Giữ bàn chưa có giao dịch chung/khóa; chuyển trạng thái còn tự do.','Đưa tiếp nhận vào Service; khóa tài nguyên; kiểm tra guard theo Hình 3.8, sức chứa và dữ liệu khách.'],
 ['Cao','Thanh toán chưa bảo vệ xung đột và chưa xử lý liên kết bàn.','Bổ sung khóa/phiên bản; xác định quy tắc giải phóng bàn theo các lượt phục vụ còn mở.'],
 ['Cao','Hóa đơn bind entity và kiểm tra danh mục tham chiếu chưa đồng đều.','Dùng DTO; tải/kiểm tra mọi mã cha; giữ lỗi form; có FK phù hợp trên schema.'],
 ['Trung bình','Chính sách giá khi sửa hóa đơn chưa rõ.','Chọn giữ giá từng dòng hoặc lấy giá mới có xác nhận; cập nhật UC, Service và test cùng lúc.'],
 ['Trung bình','Doanh thu theo ngày lập; món bán chạy không theo bộ lọc ngày.','Thêm thời điểm thanh toán nếu cần; thống nhất tên nhãn và phạm vi từng bảng thống kê.'],
 ['Trung bình','Tiền cọc chưa liên kết hóa đơn; lịch bàn chỉ có ngày.','Bổ sung quan hệ/quy tắc bù cọc nếu mở rộng; thiết kế khoảng giờ thay cho chỉ trạng thái toàn cục.'],
 ['Sau phạm vi','Tồn chưa phản ánh tiêu hao khi bán.','Mở rộng định lượng món–nguyên liệu, xuất kho/kiểm kê; không suy ra tồn vận hành chỉ từ nhập.']],[.15,.37,.48]),
 p('Ưu tiên sửa các vấn đề ảnh hưởng tính đúng đắn trước khi thêm chức năng. Với mỗi cải thiện, cần cập nhật ít nhất một quy tắc nghiệp vụ, một kịch bản tương tác và một test ngoại lệ để giữ tính nhất quán của bộ mô hình.'))

page('KẾT LUẬN VÀ TÀI LIỆU THAM KHẢO',
 p('Đề tài đã xác định phạm vi hệ thống nhà hàng hải sản nội bộ, các vai trò sử dụng và các quy tắc trọng tâm về chứng từ, trạng thái và tồn kho. Từ đó xây dựng Use Case, mô hình khái niệm, CRC, SSD, ba Sequence Diagram, DCD và State Machine; mô hình dữ liệu được đối chiếu với 12 bảng nghiệp vụ và bảng tài khoản bổ sung trong mã nguồn.'),
 p('Thiết kế chọn phân tầng Controller–Service–Repository, đặt nghiệp vụ tiền và tồn ở Service, giữ dữ liệu chứng từ trong chi tiết và dùng khóa/phiên bản cho cập nhật nhập kho. Các quyết định được giải thích theo trách nhiệm, dữ liệu sẵn có và vòng đời đối tượng. Báo cáo cũng chỉ rõ những khác biệt giữa yêu cầu đầy đủ và cài đặt hiện tại, đặc biệt ở phân quyền, đặt bàn và xử lý thanh toán đồng thời.'),
 p('Trong lần lập báo cáo, bốn kiểm thử JavaScript đã chạy đều đạt. Các kịch bản Java/Oracle quan trọng được liệt kê để nhóm chạy và ghi kết quả trước khi bảo vệ. Mức đáp ứng chức năng trong mã nguồn và kết quả kiểm thử thực tế được phân biệt để tránh kết luận vượt bằng chứng.'),
 h('Tài liệu tham khảo và nguồn đối chiếu'),
 p('[1] Hướng dẫn thực hiện BTL – gửi SV.pdf, học phần Phân tích và thiết kế hướng đối tượng; tài liệu do người học cung cấp. Trọng tâm: mục 3–7, cấu trúc báo cáo ở mục 5 và gợi ý đề tài nhà hàng ở mục 8.10.'),
 p('[2] PhanCong_Nhom4_QuanLyNhaHangHaiSan.xlsx; các sheet “Tổng quan”, “Phân công”, “Checklist”; dùng làm căn cứ phân công module và danh sách thành viên.'),
 p('[3] Mã nguồn dự án QuanLyNHHS trong thư mục làm việc, đối chiếu ngày 13/11/2026: model, service, repository, controller, dto, config và templates.'),
 p('[4] QuanLyNHHS/database/oracle: 01_inventory.sql, 02_routines.sql, 04_du_lieu_kiem_thu.sql; cùng src/main/resources/oracle_setup_user.sql và pom.xml.'),
 p('[5] Bộ test src/test/java/com/QuanLyNHHS/controller và src/test/js/menu-filters.test.cjs; log chạy JavaScript kèm thư mục nguồn báo cáo.'),
 note('Nguồn cục bộ được dùng trực tiếp; không gán tên tác giả, số liệu khảo sát hoặc kết quả chạy cho tài liệu chưa xác minh.'))

page('PHỤ LỤC A. PHÂN CÔNG VÀ CHUẨN BỊ BẢO VỆ',
 table(['Thành viên','Phân công theo bảng Excel','Mô hình cần nắm để bảo vệ'],[
 ['Hoàng Việt Đức','Khách hàng, bàn ăn, đặt bàn; KHACH_HANG, BAN_AN, DAT_BAN.','UC03/10; mô hình khái niệm bán hàng; trạng thái đặt bàn; hạn chế ngày/giờ và giữ bàn đồng thời.'],
 ['Nguyễn Cao Hoàng','Loại món, món, nhân viên, đăng nhập/bảo mật; LOAI_MON, MON_AN, NHAN_VIEN.','UC01/02/08; quan hệ loại–món; validation; phân biệt ROLE_USER với vai trò nghiệp vụ và role Oracle.'],
 ['Đỗ Minh Chiến','Bán hàng, hóa đơn, báo cáo; HOA_DON, CT_HOA_DON.','UC04/05/07; SSD; Sequence hóa đơn; DCD; công thức và phạm vi thống kê.'],
 ['Lý Tiến Đạt','Nhà cung cấp, nguyên liệu, nhập hàng; NHA_CUNG_CAP, NGUYEN_LIEU, PHIEU_NHAP, CT_PHIEU_NHAP.','UC06/09; Sequence/DCD kho; chênh lệch tồn; khóa, phiên bản và trách nhiệm procedure/trigger.']],[.22,.4,.38]),
 p('Bảng thể hiện trách nhiệm phân công, không tự xác nhận tỷ lệ đóng góp hoặc quyền tác giả từng dòng code sau tích hợp. Các ô tiến độ trong file Excel vẫn là trạng thái kế hoạch; nhóm cần cập nhật đóng góp thực tế nếu giảng viên yêu cầu.'),
 h('Câu hỏi tự kiểm tra trước buổi bảo vệ'),
 p('1. Vì sao dòng hóa đơn là lớp riêng? Vì quan hệ món–hóa đơn có lượng và giá tại lúc ghi nhận. 2. Vì sao sửa phiếu dùng lượng mới trừ cũ? Để loại tác động cũ trước khi ghi tác động mới. 3. Vì sao có giao dịch vẫn cần khóa/phiên bản? Giao dịch không tự phát hiện mọi trường hợp người dùng ghi dựa trên dữ liệu cũ.'),
 p('4. Actor và quyền trong code khác nhau thế nào? Actor mô tả vai trò sử dụng; quyền là quy tắc được thực thi ở ứng dụng/database. 5. Vì sao không tự trừ tồn khi bán? Chưa có định lượng món–nguyên liệu. 6. Khi thay đổi chính sách giá, những mô hình nào phải sửa? UC04, trách nhiệm liên quan, Sequence, Service và test tương ứng.'))

page('PHỤ LỤC B. TỆP NGUỒN VÀ THÔNG TIN CẦN BỔ SUNG',
 h('Các tệp bàn giao'),
 table(['Tệp / thư mục','Nội dung và cách dùng'],[
 ['BaoCao_BTL_PTTKHDoiTuong_Nhom4.docx','Bản Word có thể chỉnh sửa, A4, Times New Roman, tiêu đề phân cấp và số trang.'],
 ['BaoCao_BTL_PTTKHDoiTuong_Nhom4.pdf','Bản PDF trình bày cố định để xem/in; nếu sửa Word cần xuất lại PDF tương ứng.'],
 ['so_do/*.puml','14 mô hình nguồn PlantUML; có thể chỉnh sửa bằng công cụ hỗ trợ PlantUML.'],
 ['so_do/*.svg; *.png','Hình vector để phóng to và ảnh đã chèn vào báo cáo.'],
 ['nguon/noi_dung.py; tao_bao_cao.py; tao_so_do.py','Nguồn nội dung và công cụ tái tạo tài liệu/sơ đồ.'],
 ['nguon/ket_qua_kiem_tra.json; kiem_thu_js.txt','Kết quả kiểm tra cấu trúc tài liệu và log test JavaScript đã chạy.']],[.43,.57]),
 h('Thông tin cần hoàn thiện trước khi nộp'),
 p('Điền tên giảng viên, lớp, mã sinh viên; xác nhận tên nhóm và các giả định nghiệp vụ. Đọc lại mô hình, thống nhất chính sách giá khi sửa đơn và các giới hạn đã ghi. Chạy các test Java/Oracle, ghi điều kiện môi trường, dữ liệu vào, kết quả thực tế; chỉ đổi trạng thái test sau khi có bằng chứng.'),
 p('Mục lục bản Word dùng liên kết và trường tham chiếu trang. Sau khi chỉnh sửa nội dung, chọn toàn bộ rồi cập nhật trường để số trang mục lục phản ánh bản mới; trên macOS có thể cần dùng tổ hợp phím Fn + F9 hoặc lệnh Update Field của Word.'),
 h('Ghi nhận hỗ trợ công cụ'),
 p('Tài liệu được biên soạn có sử dụng ChatGPT/Codex để hỗ trợ đọc nguồn, xây dựng nội dung và mô hình; PlantUML để dựng sơ đồ; Python để tạo Word/PDF và kiểm tra cấu trúc. Nhóm cần tự rà soát, chạy thử, điều chỉnh và có khả năng giải thích sản phẩm theo quy định học phần. Không ghi nhận rằng các thành viên đã xác nhận hoặc chạy test khi chưa có dữ liệu thực tế.'),
 note('Báo cáo được tạo thành bộ tệp mới; các tài liệu BTL.docx và BTL.pdf cũ được giữ nguyên.'))

assert len(PAGES) == 48, len(PAGES)
