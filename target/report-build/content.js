module.exports = ({page,p,h,code,table,fig,note,schemas}) => {
page('TRANG BÌA', {type:'cover',lines:[
 {text:'TRƯỜNG: ........................................................................',before:100,after:160},
 {text:'KHOA / BỘ MÔN: ............................................................',after:500},
 {text:'BÁO CÁO BÀI TẬP LỚN',style:'CoverTitle',after:170},
 {text:'ĐỀ TÀI',style:'CoverSubtitle',after:120},
 {text:'XÂY DỰNG HỆ THỐNG QUẢN LÝ\nNHÀ HÀNG HẢI SẢN',style:'CoverTitle',after:250},
 {text:'Ứng dụng Java Spring Boot và Oracle Database',style:'CoverSubtitle',after:520},
 {text:'Môn học: ........................................................................',after:170},
 {text:'Giảng viên hướng dẫn: ...................................................',after:170},
 {text:'Lớp: ....................................    Nhóm: ...........................',after:200},
 {text:'DANH SÁCH THÀNH VIÊN',style:'CoverSubtitle',after:120}
 ],members:{headers:['STT','Họ và tên','Mã sinh viên','Nhiệm vụ'],rows:[['1','','',''],['2','','',''],['3','','',''],['4','','',''],['5','','','']],widths:[700,2900,2000,3471]}});

page('LỜI CẢM ƠN VÀ TÓM TẮT ĐỀ TÀI',
 h('Lời cảm ơn'),
 p('Nhóm thực hiện xin gửi lời cảm ơn đến giảng viên phụ trách học phần đã hướng dẫn kiến thức về phân tích hệ thống, lập trình Java và cơ sở dữ liệu. Các nội dung được học là cơ sở để nhóm vận dụng vào đề tài quản lý nhà hàng hải sản, từ xác định yêu cầu, thiết kế dữ liệu đến xây dựng giao diện và tổ chức mã nguồn.'),
 p('Trong quá trình thực hiện bài tập lớn, nhóm tập trung tìm hiểu cách xây dựng ứng dụng web bằng Java Spring Boot và kết nối Oracle Database. Do kinh nghiệm và thời gian còn hạn chế, sản phẩm hiện tại vẫn có các chức năng ở mức thiết kế hoặc mô phỏng giao diện. Nhóm mong nhận được góp ý để tiếp tục hoàn thiện kiến thức và sản phẩm.'),
 h('Tóm tắt'),
 p('Đề tài mô hình hóa hoạt động quản lý của một nhà hàng hải sản ở quy mô bài tập lớn. Cơ sở dữ liệu thiết kế gồm 12 bảng, bao quát khách hàng, đặt bàn, bàn ăn, loại món, món ăn, nhân viên, hóa đơn, chi tiết hóa đơn, nhà cung cấp, phiếu nhập, chi tiết phiếu nhập và nguyên liệu.'),
 p('Ứng dụng sử dụng Java, Spring Boot, Spring MVC, Thymeleaf, Spring Data JPA/Hibernate và Oracle JDBC. Mã nguồn hiện có ba entity và ba repository cho loại món, món ăn, nhân viên; các trang danh sách sử dụng dữ liệu mẫu do controller tạo. Script Oracle đã mô tả việc tạo tài khoản ứng dụng và các role phục vụ minh họa phân quyền.'),
 p('Báo cáo trình bày yêu cầu, mô hình dữ liệu, kiến trúc chương trình, cách cấu hình Oracle, phần mã nguồn đã xây dựng, kế hoạch kiểm thử và đánh giá giới hạn. Các đối tượng PL/SQL, nghiệp vụ hóa đơn và kho chưa có mã cài đặt được trình bày dưới dạng thiết kế minh họa, không được xem là kết quả chạy thực nghiệm.'),
 note('Phạm vi: một nhà hàng, một cơ sở dữ liệu, dữ liệu phục vụ học tập. Thông tin trường, môn học, giảng viên và thành viên nhóm được để trống theo yêu cầu.'));

page('MỤC LỤC', {type:'toc'});

page('DANH MỤC HÌNH, BẢNG VÀ TỪ VIẾT TẮT',
 h('Danh mục hình'),
 p('Hình 2.1. Kiến trúc ứng dụng và mức độ hoàn thiện các tầng (trang 8).\nHình 3.1. Nhóm tác nhân và chức năng theo thiết kế (trang 9).\nHình 3.2. Quy trình nghiệp vụ bán hàng theo thiết kế (trang 12).\nHình 4.1. Mô hình quan hệ 12 bảng vẽ lại theo sơ đồ đề tài (trang 13).'),
 h('Các nhóm bảng trong báo cáo'),
 p('Bảng 1.1: đối chiếu phạm vi triển khai; Bảng 2.1: công nghệ; Bảng 3.1–3.3: yêu cầu và ca sử dụng; Bảng 4.1: quan hệ; Bảng 4.2–4.13: từ điển dữ liệu; Bảng 4.14: vai trò; Bảng 5.1: đường dẫn giao diện; Bảng 6.1–6.3: đối chiếu và kiểm thử.'),
 h('Từ viết tắt'),
 table('', ['Ký hiệu','Diễn giải'],[
 ['BTL','Bài tập lớn'],['CSDL','Cơ sở dữ liệu'],['PK / FK','Primary Key / Foreign Key – khóa chính / khóa ngoại'],['ERD','Entity Relationship Diagram – sơ đồ thực thể và quan hệ'],['MVC','Model – View – Controller'],['JPA / ORM','Java Persistence API / ánh xạ đối tượng – quan hệ'],['JDBC','Java Database Connectivity'],['CRUD','Create, Read, Update, Delete – thêm, xem, sửa, xóa'],['PL/SQL','Ngôn ngữ lập trình thủ tục của Oracle'],['PDB','Pluggable Database – cơ sở dữ liệu thành phần'],['DDL / DML','Lệnh định nghĩa cấu trúc / thao tác dữ liệu']
 ],[1800,7271]));

page('CHƯƠNG 1. TỔNG QUAN ĐỀ TÀI',
 h('1.1. Lý do chọn đề tài'),
 p('Nhà hàng hải sản cần quản lý nhiều loại thông tin có liên quan: thực đơn thay đổi theo nguồn hàng, món bán theo nhiều đơn vị, bàn phục vụ, khách đặt trước và nguyên liệu nhập từ các nhà cung cấp. Nếu thông tin chỉ được ghi rời rạc bằng sổ hoặc bảng tính, việc tra cứu món ăn, theo dõi chứng từ và tổng hợp dữ liệu sẽ khó thống nhất.'),
 p('Đề tài được lựa chọn để vận dụng kiến thức lập trình hướng đối tượng và cơ sở dữ liệu quan hệ vào một tình huống cụ thể. Java Spring Boot hỗ trợ xây dựng ứng dụng web; Oracle được sử dụng để tổ chức dữ liệu, ràng buộc liên kết và tìm hiểu phân quyền. Quy mô triển khai phù hợp với học phần, tập trung vào mô hình dữ liệu và một số giao diện quản lý cơ bản.'),
 h('1.2. Mục tiêu'),
 p('Về phân tích, xác định tác nhân và nghiệp vụ của một nhà hàng hải sản; xây dựng sơ đồ dữ liệu 12 bảng và giải thích khóa, quan hệ, quy tắc xử lý. Về công nghệ, tổ chức chương trình Spring Boot theo các nhóm controller, model, repository, service; khai báo ánh xạ JPA và cấu hình kết nối Oracle qua JDBC.'),
 p('Về sản phẩm hiện có, xây dựng trang chủ cùng các trang loại món, món ăn và nhân viên; tạo dữ liệu mẫu để minh họa việc hiển thị bằng Thymeleaf; chuẩn bị entity, repository và script thiết lập Oracle. Về đánh giá, đối chiếu mã nguồn với thiết kế, xác định phần đã có và các bước cần hoàn thiện để dữ liệu giao diện được lưu thực sự.'),
 h('1.3. Đối tượng và phạm vi'),
 p('Đối tượng nghiên cứu là dữ liệu và quy trình cơ bản của một nhà hàng. Báo cáo không mở rộng thành nền tảng nhiều chi nhánh, thương mại điện tử, tích hợp giao hàng, hóa đơn điện tử hoặc thanh toán ngân hàng. Các tác nhân là vai trò nghiệp vụ được đề xuất cho bài tập lớn; chưa có cơ chế đăng nhập web hoàn chỉnh.'));

page('1.4. PHẠM VI THỰC HIỆN VÀ PHƯƠNG PHÁP',
 table('Bảng 1.1. Đối chiếu thiết kế và mã nguồn được cung cấp', ['Hạng mục','Mức độ hiện tại'],[
 ['CSDL 12 bảng','Có sơ đồ thiết kế; chưa có đầy đủ entity/DDL trong mã nguồn.'],
 ['Loại món, món ăn, nhân viên','Có entity JPA, repository, controller GET và template.'],
 ['Dữ liệu trang danh sách','Controller tạo ArrayList mẫu; chưa gọi repository.'],
 ['Thêm / sửa / xóa trên giao diện','Modal và thông báo JavaScript mô phỏng; chưa có lưu DML.'],
 ['Tầng Service','Các file đã tạo nhưng chưa có nội dung xử lý.'],
 ['Khách hàng, bàn, đặt bàn, hóa đơn, kho','Phần lớn file trống hoặc lớp khung; menu đang vô hiệu hóa.'],
 ['Oracle','Có cấu hình datasource, Hibernate và script tạo user/role.'],
 ['Đăng nhập, phân quyền web','SecurityConfig.java và login.html đang trống; chưa có Spring Security.'],
 ['View, procedure, function, trigger, package','Chưa tìm thấy mã tạo đối tượng; báo cáo chỉ đưa ví dụ thiết kế.']
 ],[2800,6271]),
 h('1.5. Phương pháp thực hiện'),
 p('Nhóm phân rã quy trình thành các nhóm quản lý danh mục, phục vụ khách, bán hàng và nhập kho; xây dựng mô hình quan hệ; lựa chọn kiểu dữ liệu Oracle; ánh xạ một phần mô hình sang Java. Tiếp theo, nhóm xây dựng controller và template để minh họa cách trình bày dữ liệu, đồng thời chuẩn bị tài khoản Oracle cho ứng dụng.'),
 p('Báo cáo được đối chiếu trực tiếp với pom.xml, application.properties, các entity, repository, controller, template, main.js, script oracle_setup_user.sql và lớp kiểm thử hiện có. Việc có khai báo trong mã nguồn không đồng nghĩa đã được chạy thành công; báo cáo không tự suy ra kết quả kết nối Oracle, kiểm thử hiệu năng hoặc kết quả DML.'),
 note('Ba mức được dùng xuyên suốt: “đã có mã” là đã tìm thấy nội dung triển khai; “mô phỏng” là dùng dữ liệu mẫu hoặc chỉ xử lý giao diện; “thiết kế/đề xuất” là nội dung chưa được cài đặt trong dự án.'));

page('CHƯƠNG 2. CƠ SỞ CÔNG NGHỆ',
 table('Bảng 2.1. Thành phần công nghệ theo cấu hình dự án', ['Thành phần','Ứng dụng trong đề tài'],[
 ['Java','pom.xml khai báo java.version=27; lớp model, controller và repository viết bằng Java.'],
 ['Spring Boot','Parent trong pom.xml là 4.1.1; khởi động ứng dụng và cấu hình các thành phần.'],
 ['Spring MVC','spring-boot-starter-webmvc; nhận HTTP request và trả tên template.'],
 ['Thymeleaf','spring-boot-starter-thymeleaf; lặp danh sách, hiển thị thuộc tính model, dùng fragment.'],
 ['Spring Data JPA / Hibernate','Khai báo entity, khóa, sequence và repository; Hibernate cấu hình sinh/cập nhật schema.'],
 ['Oracle JDBC','ojdbc17 ở runtime; driver oracle.jdbc.OracleDriver.'],
 ['Oracle Database','Thiết kế NUMBER, VARCHAR2, DATE; cấu hình đích kết nối FREEPDB1.'],
 ['Maven','Quản lý dependency, biên dịch, đóng gói và chạy ứng dụng.'],
 ['HTML, CSS, JavaScript','Bố cục giao diện, tìm kiếm/lọc dữ liệu đang hiển thị và modal.']
 ],[2700,6371]),
 h('2.1. Lựa chọn công nghệ trong phạm vi bài tập'),
 p('Ứng dụng dùng mô hình web kết xuất phía máy chủ. Controller đưa dữ liệu vào Model, Thymeleaf tạo HTML, trình duyệt nhận trang và chạy JavaScript tương tác. Các tầng được đặt dưới package gốc com.QuanLyNHHS để Spring có thể quét thành phần [1].'),
 p('Spring Data JPA cung cấp cơ chế repository cho tầng truy cập dữ liệu [2]. Trong mã nguồn, ba interface kế thừa JpaRepository đã được khai báo; chúng chỉ phát huy vai trò nghiệp vụ khi được gọi từ tầng xử lý. Oracle giữ dữ liệu quan hệ và thực thi các ràng buộc khi thao tác thật với cơ sở dữ liệu.'),
 note('Các số phiên bản trên được ghi theo pom.xml, không phải kết luận về khả năng tương thích đã kiểm thử. Phiên bản Oracle đang cài thực tế chưa được xác nhận; tên FREEPDB1 là giá trị mặc định trong cấu hình.'));

page('2.2. KIẾN TRÚC VÀ LUỒNG XỬ LÝ',
 fig('architecture.png','Hình 2.1. Kiến trúc ứng dụng và mức độ hoàn thiện các tầng.',6.10,3.81),
 h('2.2.1. Luồng đang có trong mã nguồn'),
 p('Với GET /mon-an, MonAnController gọi getSampleDishes() và getSampleCategories(), tính số món theo trạng thái, rồi thêm dữ liệu vào Model. Phương thức trả về monan/index; Thymeleaf tạo danh sách và trình duyệt thực hiện lọc/tìm kiếm. Luồng này chưa đọc dữ liệu qua MonAnRepository.'),
 h('2.2.2. Luồng đề xuất để hoàn thiện'),
 p('Controller nhận yêu cầu và dữ liệu biểu mẫu; Service kiểm tra quy tắc nghiệp vụ và xác định giao dịch; Repository truy cập JPA; Hibernate trao đổi với Oracle qua JDBC. Kết quả được đưa về Model hoặc chuyển hướng sau khi lưu. Các thao tác ghi nhiều bảng như lập hóa đơn cần được thực hiện trong cùng giao dịch.'),
 p('Đây là kiến trúc một ứng dụng thống nhất, phù hợp bài tập lớn. Không cần tách microservice hoặc bổ sung hệ thống phân tán cho quy mô đang xét.'));

page('CHƯƠNG 3. PHÂN TÍCH YÊU CẦU',
 fig('actors.png','Hình 3.1. Nhóm tác nhân và chức năng theo thiết kế.',6.10,4.0),
 h('3.1. Tác nhân'),
 p('ADMIN quản lý cấu hình và tài khoản; QUAN_LY phụ trách danh mục, nhân viên và theo dõi hoạt động; THU_NGAN phụ trách hóa đơn và thanh toán; PHUC_VU tiếp nhận bàn và gọi món; THU_KHO theo dõi nhập nguyên liệu; BAO_CAO chỉ tra cứu số liệu. Khách hàng là đối tượng được quản lý, chưa được thiết kế thành người dùng tự đăng nhập website.'),
 h('3.2. Quy tắc xác định quyền'),
 p('Vai trò trên là phân tích nghiệp vụ của đề tài. Oracle user/role điều khiển quyền của phiên kết nối cơ sở dữ liệu. Quyền của người dùng web cần cơ chế xác thực và phân quyền riêng; giao diện hiển thị tên quản trị viên mẫu không chứng minh đã có đăng nhập.'));

page('3.3. YÊU CẦU CHỨC NĂNG VÀ PHI CHỨC NĂNG',
 table('Bảng 3.1. Các yêu cầu chức năng', ['Mã','Yêu cầu','Hiện trạng'],[
 ['FR01','Xem và quản lý danh mục loại món.','Có danh sách mẫu; ghi dữ liệu chưa cài.'],
 ['FR02','Xem món theo loại, tìm tên/mã, đổi kiểu hiển thị.','Có mã JavaScript cho món; chưa kiểm thử trình duyệt.'],
 ['FR03','Xem nhân viên, tìm kiếm và lọc chức vụ.','Có danh sách mẫu và mã lọc.'],
 ['FR04','Quản lý khách hàng, bàn và đặt bàn.','Mức thiết kế.'],
 ['FR05','Lập hóa đơn, ghi món, tính tiền, thanh toán.','Mức thiết kế.'],
 ['FR06','Quản lý nhà cung cấp và nguyên liệu.','Mức thiết kế.'],
 ['FR07','Lập phiếu nhập, ghi chi tiết, cập nhật tồn.','Mức thiết kế.'],
 ['FR08','Tổng hợp doanh thu từ hóa đơn đã thanh toán.','Mức thiết kế.'],
 ['FR09','Phân quyền theo nhóm người sử dụng.','Có script Oracle một phần; web chưa cài.']
 ],[800,4250,4021]),
 h('3.4. Yêu cầu phi chức năng trong phạm vi BTL'),
 p('Giao diện sử dụng tiếng Việt, tên cột rõ ràng và bố cục thống nhất. Dữ liệu tiền dùng kiểu thập phân; các trường tên bắt buộc không để trống; mã quan hệ phải tồn tại. Khi thao tác ghi thất bại, ứng dụng cần hiển thị lý do và không để chứng từ lưu dở dang.'),
 p('Khả năng bảo trì được hỗ trợ bằng cách tách controller, model, repository và service. Cấu hình kết nối có thể thay đổi qua biến môi trường. Việc sao lưu có thể thực hiện bằng công cụ Oracle hoặc xuất dữ liệu phục vụ học tập; chưa có chức năng tự động sao lưu trong mã nguồn.'),
 p('Bài tập chưa đặt chỉ tiêu định lượng về số người đồng thời, thời gian phản hồi hoặc tính sẵn sàng. Vì chưa có đo đạc, báo cáo không đưa số liệu hiệu năng hoặc khẳng định đáp ứng vận hành thực tế.'));

page('3.5. ĐẶC TẢ CÁC CA SỬ DỤNG CHÍNH',
 table('Bảng 3.2. UC01 – Tra cứu thực đơn món ăn', ['Mục','Nội dung'],[
 ['Tác nhân / mục đích','Quản lý hoặc phục vụ; tra cứu món và giá bán.'],
 ['Tiền điều kiện','Ứng dụng đã khởi động, trang /mon-an truy cập được.'],
 ['Luồng chính','1. Mở thực đơn. 2. Xem danh sách. 3. Chọn loại hoặc nhập từ khóa. 4. Xem thẻ hoặc bảng.'],
 ['Dữ liệu đầu ra','Tên món, loại món, đơn vị, giá và trạng thái phục vụ.'],
 ['Ngoại lệ','Không có món khớp điều kiện; hiện chưa có thông báo riêng cho trường hợp rỗng.'],
 ['Hiện trạng','Controller và template có mã; nguồn dữ liệu là danh sách mẫu.']
 ],[2050,7021]),
 table('Bảng 3.3. UC02 – Thêm món ăn (thiết kế nghiệp vụ)', ['Mục','Nội dung'],[
 ['Tác nhân / mục đích','Quản lý; bổ sung món thuộc một loại món có sẵn.'],
 ['Tiền điều kiện','Có loại món hợp lệ; người thao tác có quyền ghi khi hoàn thiện.'],
 ['Luồng chính đề xuất','1. Nhập tên, loại, đơn vị, giá. 2. Kiểm tra dữ liệu. 3. Lưu qua Service/Repository. 4. Tải lại danh sách.'],
 ['Ngoại lệ','Tên trống, giá âm, loại không tồn tại hoặc lỗi cơ sở dữ liệu.'],
 ['Hậu điều kiện đề xuất','Có dòng MON_AN mới trong Oracle với mã do sequence cấp.'],
 ['Hiện trạng','Form đang chặn submit và chỉ hiện toast; chưa tạo bản ghi.']
 ],[2050,7021]),
 note('Các bước “đề xuất” là đặc tả để phát triển tiếp. Thông báo thành công ở frontend không được dùng làm bằng chứng lưu cơ sở dữ liệu.'));

page('3.6. QUY TRÌNH BÁN HÀNG VÀ NHẬP KHO',
 fig('workflow.png','Hình 3.2. Quy trình nghiệp vụ bán hàng theo thiết kế.',6.10,3.51),
 h('3.6.1. Ca sử dụng thanh toán (thiết kế)'),
 p('Thu ngân chọn hóa đơn đang mở; hệ thống lấy các dòng chi tiết, tính tổng trước giảm giá, kiểm tra tiền giảm và xác định tổng phải trả. Khi xác nhận, hóa đơn chuyển DA_THANH_TOAN và bàn trở về trạng thái phù hợp. Nếu dữ liệu không hợp lệ hoặc lưu thất bại, giao dịch cần hoàn tác. Mã nguồn hiện tại chưa triển khai luồng này.'),
 h('3.6.2. Ca sử dụng nhập nguyên liệu (thiết kế)'),
 p('Thủ kho chọn nhà cung cấp và nhân viên lập phiếu, nhập danh sách nguyên liệu kèm số lượng và đơn giá. Chương trình tạo PHIEU_NHAP, các dòng CT_PHIEU_NHAP, tính tổng giá trị và tăng tồn nguyên liệu trong một giao dịch. Cần tránh ghi nhận tồn hai lần khi người dùng gửi lại yêu cầu.'),
 h('3.6.3. Giới hạn của quy trình'),
 p('Tiền cọc đặt bàn chưa có quan hệ trực tiếp đến hóa đơn nên chưa tự động khấu trừ. Nhập kho có thể làm tăng tồn theo thiết kế; việc bán món chưa tự giảm nguyên liệu vì thiếu dữ liệu định lượng. Báo cáo không gọi phần chênh lệch doanh thu trừ tiền nhập là lợi nhuận thực tế.'));

page('CHƯƠNG 4. THIẾT KẾ CƠ SỞ DỮ LIỆU ORACLE',
 fig('erd.png','Hình 4.1. Mô hình quan hệ 12 bảng vẽ lại theo sơ đồ đề tài.',6.10,5.87),
 h('4.1. Các nhóm dữ liệu'),
 p('Nhóm phục vụ gồm KHACH_HANG, BAN_AN, DAT_BAN. Nhóm thực đơn gồm LOAI_MON, MON_AN. Nhóm bán hàng gồm HOA_DON, CT_HOA_DON cùng NHAN_VIEN. Nhóm nhập hàng gồm NHA_CUNG_CAP, PHIEU_NHAP, CT_PHIEU_NHAP, NGUYEN_LIEU.'),
 note('Sơ đồ thể hiện thiết kế toàn đề tài. Chỉ LOAI_MON, MON_AN, NHAN_VIEN đã có ánh xạ @Entity trong mã nguồn được kiểm tra.'));

page('4.2. QUAN HỆ VÀ CHUẨN HÓA DỮ LIỆU',
 table('Bảng 4.1. Các quan hệ một – nhiều theo khóa ngoại', ['Bảng cha','Bảng con','Khóa ngoại ở bảng con'],[
 ['KHACH_HANG','DAT_BAN','MA_KH'],['BAN_AN','DAT_BAN','MA_BAN'],['BAN_AN','HOA_DON','MA_BAN'],['NHAN_VIEN','HOA_DON','MA_NV'],['KHACH_HANG','HOA_DON','MA_KH'],['LOAI_MON','MON_AN','MA_LOAI'],['HOA_DON','CT_HOA_DON','MA_HD'],['MON_AN','CT_HOA_DON','MA_MON'],['NHA_CUNG_CAP','PHIEU_NHAP','MA_NCC'],['NHAN_VIEN','PHIEU_NHAP','MA_NV'],['PHIEU_NHAP','CT_PHIEU_NHAP','MA_PN'],['NGUYEN_LIEU','CT_PHIEU_NHAP','MA_NL']
 ],[2700,3000,3371]),
 h('4.2.1. Khóa và tính toàn vẹn tham chiếu'),
 p('Mỗi bảng danh mục hoặc chứng từ có một khóa chính. Hai bảng chi tiết dùng khóa chính ghép để không lặp món trong một hóa đơn hoặc nguyên liệu trong một phiếu. Quan hệ nhiều – nhiều giữa hóa đơn và món ăn được biểu diễn qua CT_HOA_DON; quan hệ tương tự cho phiếu nhập và nguyên liệu đi qua CT_PHIEU_NHAP.'),
 h('4.2.2. Chuẩn hóa ở mức thiết kế'),
 p('Mỗi ô chứa một giá trị, không lưu danh sách món trong một cột. Thông tin loại món, nhà cung cấp và nhân viên được tách ra khỏi các chứng từ để giảm lặp. Trong bảng chi tiết, số lượng và đơn giá giao dịch phụ thuộc toàn bộ khóa ghép. Đơn giá bán lịch sử được giữ ở chi tiết, không lấy lại giá thực đơn hiện hành khi xem hóa đơn cũ.'),
 p('THANH_TIEN và TONG_TIEN là dữ liệu tính toán được lưu dư có chủ đích để minh họa chứng từ. Chúng cần được đồng bộ hoặc tính bằng truy vấn; do tồn tại thuộc tính suy diễn, báo cáo không khẳng định toàn bộ mô hình đã chuẩn hóa hoàn toàn đến 3NF.'));

for(let i=0;i<schemas.length;i+=2){
 const a=schemas[i],b=schemas[i+1];
 page(`4.3.${i/2+1}. TỪ ĐIỂN DỮ LIỆU: ${a.name} / ${b.name}`,
  h(a.name+' – '+a.purpose),
  table(`Bảng 4.${i+2}. Cấu trúc ${a.name}`,['Cột','Kiểu Oracle','Ý nghĩa / khóa'],a.cols,[2450,2250,4371]),
  p('Khóa chính: '+a.key+'. '+a.status+' '+a.rule),
  h(b.name+' – '+b.purpose),
  table(`Bảng 4.${i+3}. Cấu trúc ${b.name}`,['Cột','Kiểu Oracle','Ý nghĩa / khóa'],b.cols,[2450,2250,4371]),
  p('Khóa chính: '+b.key+'. '+b.status+' '+b.rule),
  ...(i===0?[note('Kiểu dữ liệu theo sơ đồ. Các quy tắc không âm, sức chứa và thời gian là yêu cầu thiết kế; chưa mặc nhiên được thực thi nếu chưa có CHECK hoặc mã xử lý.')]:[]));
}

page('4.4. RÀNG BUỘC VÀ QUY TẮC TÍNH TOÁN',
 h('4.4.1. Miền giá trị'),
 p('Số lượng chi tiết phải lớn hơn 0. Đơn giá, mức lương, tiền cọc, giảm giá, tồn kho và điểm tích lũy không âm. SO_CHO và SO_NGUOI là số nguyên dương. Các trường tên bắt buộc không được bỏ trống. Giá trị trạng thái cần thống nhất giữa Java, giao diện và SQL để tránh cùng một trạng thái được viết nhiều cách.'),
 p('Oracle NUMBER(p,s) giới hạn độ chính xác và số chữ số thập phân. Khi tính thành tiền, cần kiểm tra giới hạn lưu trữ và làm tròn thống nhất hai chữ số. VARCHAR2 cần lưu ý ngữ nghĩa BYTE/CHAR khi chứa tiếng Việt; môi trường dùng Unicode và ngữ nghĩa ký tự phải được kiểm tra trước khi nhập chuỗi gần giới hạn.'),
 h('4.4.2. Công thức nghiệp vụ'),
 code(`Thành tiền dòng bán = ROUND(Số lượng × Đơn giá bán, 2)
Tổng trước giảm = SUM(Thành tiền các dòng hóa đơn)
0 ≤ Giảm giá ≤ Tổng trước giảm
Tổng hóa đơn = Tổng trước giảm − Giảm giá
Thành tiền nhập = ROUND(Số lượng nhập × Đơn giá nhập, 2)
Tổng phiếu nhập = SUM(Thành tiền các dòng phiếu nhập)
Tồn sau nhập = Tồn trước nhập + Số lượng nhập`),
 h('4.4.3. Giao dịch và cập nhật'),
 p('Khi hoàn thiện, việc tạo phần đầu và phần chi tiết chứng từ cần cùng giao dịch. Chỉ COMMIT khi toàn bộ thao tác thành công; nếu một dòng lỗi phải ROLLBACK. Tổng hóa đơn phải được kiểm tra lại tại máy chủ, không tin giá trị tính từ trình duyệt. Không sửa giá bán lịch sử chỉ vì giá thực đơn thay đổi.'),
 p('Khóa ngoại mặc định ngăn xóa bản ghi cha đang được tham chiếu. Với danh mục đã phát sinh chứng từ, có thể dùng trạng thái tạm dừng thay vì xóa. Nghiệp vụ giữ bàn, trùng giờ và cập nhật đồng thời chưa được cài đặt; cần bổ sung kiểm tra ở Service hoặc cơ sở dữ liệu khi triển khai.'));

page('4.5. USER, ROLE VÀ PRIVILEGE TRONG ORACLE',
 p('User là tài khoản kết nối và có thể sở hữu schema. Role là tập hợp quyền được cấp cho user hoặc role khác [4]. System privilege cho phép thực hiện loại thao tác như CREATE SESSION; object privilege cho phép thao tác trên đối tượng cụ thể như SELECT ON NHHS_APP.MON_AN.'),
 table('Bảng 4.14. Vai trò nghiệp vụ và mức quyền dự kiến', ['Role','Quyền nghiệp vụ dự kiến','Script hiện có'],[
 ['ROLE_ADMIN','Quản trị hệ thống học tập.','Có tạo và cấp ALL PRIVILEGES.'],
 ['ROLE_QUAN_LY','Quản lý nhân viên, bàn, thực đơn.','Có CRUD trên 3 bảng hiện có.'],
 ['ROLE_THU_NGAN','Tạo hóa đơn, ghi món, thanh toán.','Mới tạo role, cấp CREATE SESSION.'],
 ['ROLE_PHUC_VU','Xem thực đơn, bàn, tạo gọi món.','Hiện chỉ SELECT loại món và món.'],
 ['ROLE_THU_KHO','Quản lý nhập hàng và nguyên liệu.','Mới tạo role, cấp CREATE SESSION.'],
 ['ROLE_BAO_CAO','Chỉ đọc dữ liệu/tổng hợp.','SELECT trên 3 bảng hiện có.']
 ],[2300,3600,3171]),
 h('4.5.1. Tài khoản trong script'),
 p('oracle_setup_user.sql tạo NHHS_APP, sáu role và các tài khoản mẫu admin_user, quan_ly, phuc_vu, bao_cao. Script chưa tạo user mẫu riêng cho thu ngân và thủ kho. Các lệnh cấp quyền trên bảng phải chạy sau khi bảng tồn tại trong schema NHHS_APP.'),
 h('4.5.2. Phân biệt quyền Oracle và quyền web'),
 p('Datasource của Spring Boot đang dùng một tài khoản NHHS_APP. Vì các yêu cầu web dùng chung tài khoản kết nối, việc tạo quan_ly hoặc phuc_vu trong Oracle không tự tạo quyền cho người đang sử dụng trình duyệt. Cần hoàn thiện xác thực web và kiểm tra quyền trong ứng dụng nếu muốn phân vai người dùng.'),
 note('Script gốc có GRANT DBA TO NHHS_APP và quyền rất rộng cho ROLE_ADMIN. Báo cáo ghi nhận đúng hiện trạng môi trường học tập; ví dụ thiết lập ở trang sau dùng nhóm quyền cơ bản cho phần minh họa.'));

page('4.6. THIẾT LẬP ORACLE VÀ KIỂM TRA ĐỐI TƯỢNG',
 h('4.6.1. Trình tự thực hiện đề xuất'),
 p('Bước 1: kết nối bằng tài khoản quản trị và chọn đúng PDB. Bước 2: tạo NHHS_APP, cấp quyền tạo bảng, sequence và hạn ngạch. Bước 3: khởi động ứng dụng để Hibernate xử lý ba entity hiện có hoặc tạo schema bằng script đã rà soát. Bước 4: sau khi có bảng, chạy phần tạo role và cấp quyền đối tượng. Không chạy toàn bộ script cấp quyền bảng trước khi schema được tạo.'),
 code(`-- Minh họa; chạy bởi quản trị viên trong đúng PDB.
CREATE USER NHHS_APP IDENTIFIED BY "<MAT_KHAU_NHOM>";
GRANT CREATE SESSION, CREATE TABLE,
      CREATE SEQUENCE TO NHHS_APP;
ALTER USER NHHS_APP QUOTA 100M ON USERS;

-- Chạy sau khi NHHS_APP.MON_AN đã tồn tại.
CREATE ROLE ROLE_XEM_MON;
GRANT CREATE SESSION TO ROLE_XEM_MON;
GRANT SELECT ON NHHS_APP.MON_AN TO ROLE_XEM_MON;`),
 h('4.6.2. Truy vấn kiểm tra sau khi chạy'),
 code(`-- Kết nối NHHS_APP.
SELECT table_name FROM user_tables ORDER BY table_name;
SELECT sequence_name FROM user_sequences;
SELECT constraint_name, constraint_type, table_name
FROM user_constraints
WHERE table_name IN ('LOAI_MON','MON_AN','NHAN_VIEN');
SELECT object_name, object_type, status
FROM user_objects WHERE status <> 'VALID';`),
 p('Cần kiểm tra kết quả thực tế thay vì suy ra từ cấu hình ddl-auto. Hibernate chỉ xử lý các entity đã khai báo; việc có 12 tên file model không đồng nghĩa Oracle đã có đủ 12 bảng. Chưa có bản chụp hoặc kết quả truy vấn từ phiên Oracle trong lần đối chiếu báo cáo này.'),
 note('Các giá trị trong dấu <…> là chỗ điền, phải thay trước khi chạy. Script trong báo cáo là ví dụ hướng dẫn, không tự động được thực thi trên cơ sở dữ liệu.'));

page('4.7. VIEW VÀ FUNCTION PL/SQL – THIẾT KẾ MINH HỌA',
 p('Sơ đồ đề tài đề cập View, Procedure, Function, Trigger và Package. Mã nguồn hiện tại chưa có lệnh tạo các đối tượng này. Các ví dụ dưới đây minh họa cách áp dụng cho mô hình 12 bảng khi được hoàn thiện; cần tạo đầy đủ bảng trước và kiểm thử trên Oracle.'),
 h('4.7.1. View tổng hợp doanh thu theo ngày'),
 code(`CREATE OR REPLACE VIEW V_DOANH_THU_NGAY AS
SELECT TRUNC(NGAY_LAP) AS NGAY,
       COUNT(*) AS SO_HOA_DON,
       SUM(NVL(TONG_TIEN,0)) AS DOANH_THU
FROM HOA_DON
WHERE TRANG_THAI = 'DA_THANH_TOAN'
GROUP BY TRUNC(NGAY_LAP);`),
 p('View chỉ đưa hóa đơn đã thanh toán vào doanh thu. Hóa đơn mở hoặc hủy không được tính. NGAY_LAP được dùng làm ngày báo cáo do mô hình chưa có NGAY_THANH_TOAN; vì vậy view này phản ánh doanh thu theo ngày lập của các hóa đơn đã trả, chưa phải sổ thu tiền theo thời điểm thanh toán.'),
 h('4.7.2. Function tính tổng dòng hóa đơn'),
 code(`CREATE OR REPLACE FUNCTION FN_TONG_DONG_HD(
    P_MA_HD IN NUMBER) RETURN NUMBER IS
    V_TONG NUMBER;
BEGIN
    SELECT NVL(SUM(ROUND(SO_LUONG*DON_GIA,2)),0)
      INTO V_TONG
      FROM CT_HOA_DON WHERE MA_HD = P_MA_HD;
    RETURN V_TONG;
END;
/`),
 p('Function trả tổng trước giảm giá, bằng 0 nếu không có dòng chi tiết. Hàm này không tự xác nhận mã hóa đơn tồn tại; khi dùng trong nghiệp vụ cần kiểm tra phần đầu hóa đơn. Procedure phù hợp để thực hiện hành động, function phù hợp cho tính toán trả về giá trị [5].'));

page('4.8. PROCEDURE, TRIGGER VÀ PACKAGE – MINH HỌA',
 h('4.8.1. Procedure tính lại tổng hóa đơn'),
 code(`CREATE OR REPLACE PROCEDURE SP_TINH_TONG_HD(
    P_MA_HD IN NUMBER) IS
    V_GIAM NUMBER; V_TONG NUMBER; V_TT VARCHAR2(20);
BEGIN
    SELECT NVL(GIAM_GIA,0), TRANG_THAI
      INTO V_GIAM, V_TT FROM HOA_DON
      WHERE MA_HD=P_MA_HD FOR UPDATE;
    IF V_TT IS NULL OR V_TT <> 'MO' THEN
        RAISE_APPLICATION_ERROR(-20001,'Hoa don khong mo');
    END IF;
    V_TONG := FN_TONG_DONG_HD(P_MA_HD);
    IF V_GIAM < 0 OR V_GIAM > V_TONG THEN
        RAISE_APPLICATION_ERROR(-20002,'Giam gia sai');
    END IF;
    UPDATE HOA_DON SET TONG_TIEN=V_TONG-V_GIAM
      WHERE MA_HD=P_MA_HD;
END;
/`),
 p('Procedure không COMMIT bên trong; bên gọi quyết định giao dịch. NO_DATA_FOUND khi không tìm thấy hóa đơn sẽ được chuyển thành lỗi nghiệp vụ ở tầng gọi. Mọi thao tác thay đổi chi tiết cũng cần tuân theo cùng quy ước khóa hóa đơn để tránh đọc tổng trong lúc bị sửa đồng thời.'),
 h('4.8.2. Trigger tính thành tiền dòng bán'),
 code(`CREATE OR REPLACE TRIGGER TRG_CT_HD_THANH_TIEN
BEFORE INSERT OR UPDATE ON CT_HOA_DON
FOR EACH ROW
BEGIN
    IF :NEW.SO_LUONG IS NULL OR :NEW.SO_LUONG <= 0
       OR :NEW.DON_GIA IS NULL OR :NEW.DON_GIA < 0 THEN
        RAISE_APPLICATION_ERROR(-20003,'Du lieu sai');
    END IF;
    :NEW.THANH_TIEN := ROUND(:NEW.SO_LUONG*:NEW.DON_GIA,2);
END;
/`),
 p('Package được đề xuất để gom tính tổng và thao tác hóa đơn thành PKG_HOA_DON, gồm phần khai báo và phần thân. Chưa có package được cài đặt. Trigger trên chỉ tính dòng, không tự cập nhật tổng đầu hóa đơn; bên gọi cần gọi procedure sau khi hoàn tất thay đổi chi tiết.'));

page('CHƯƠNG 5. XÂY DỰNG ỨNG DỤNG JAVA SPRING BOOT',
 h('5.1. Tổ chức mã nguồn'),
 code(`QuanLyNHHS/
  pom.xml
  src/main/java/com/QuanLyNHHS/
    QuanLyNhhsApplication.java
    controller/   -- 4 controller có nội dung
    model/        -- 3 entity hoàn chỉnh
    repository/   -- 3 JpaRepository có nội dung
    service/      -- các file khung
    config/       -- SecurityConfig đang trống
  src/main/resources/
    application.properties
    oracle_setup_user.sql
    templates/    -- trang chủ, món, loại món, nhân viên
    static/css/style.css
    static/js/main.js`),
 h('5.2. Cấu hình kết nối và sinh schema'),
 code(`spring.datasource.url=\${DB_URL:jdbc:oracle:thin:@//localhost:1521/FREEPDB1}
spring.datasource.username=\${DB_USERNAME:NHHS_APP}
spring.datasource.password=\${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true`),
 p('URL, username, driver và cấu hình Hibernate trên được đối chiếu từ dự án. Dòng password được viết theo dạng biến môi trường để minh họa, không chép mật khẩu mặc định trong mã nguồn vào báo cáo. ddl-auto=update yêu cầu Hibernate cập nhật cấu trúc cho các entity đã có; đây không phải hệ thống quản lý phiên bản schema.'),
 h('5.3. Chạy ứng dụng trong môi trường học tập'),
 p('Chuẩn bị JDK theo cấu hình pom.xml, Maven và một Oracle PDB truy cập được. Tạo NHHS_APP, đặt DB_URL/DB_USERNAME/DB_PASSWORD, chuyển vào thư mục chứa pom.xml rồi chạy .\\mvnw.cmd spring-boot:run hoặc mvn spring-boot:run. Mở http://localhost:8080/ nếu cổng không bị đổi. Đây là hướng dẫn chạy; chưa phải biên bản khởi động thành công.'));

page('5.4. ÁNH XẠ ENTITY VÀ SEQUENCE',
 h('5.4.1. Ví dụ trích từ LoaiMon.java'),
 code(`@Entity
@Table(name = "LOAI_MON")
public class LoaiMon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "seq_loai_mon")
    @SequenceGenerator(name = "seq_loai_mon",
        sequenceName = "SEQ_LOAI_MON", allocationSize = 1)
    @Column(name = "MA_LOAI")
    private Long maLoai;

    @Column(name = "TEN_LOAI", length = 100, nullable = false)
    private String tenLoai;
    // Các thuộc tính, constructor, getter/setter được lược bớt.
}`),
 p('Tên bảng và cột được ánh xạ bằng annotation. @Id xác định khóa chính; @SequenceGenerator chỉ ra sequence Oracle cấp mã. allocationSize=1 là cấu hình trong mã hiện tại. Các getter/setter được viết tường minh; dependency Lombok đang bị comment trong pom.xml.'),
 h('5.4.2. Quan hệ món ăn – loại món'),
 code(`@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "MA_LOAI", nullable = false,
    foreignKey = @ForeignKey(name = "FK_MON_AN_LOAI_MON"))
private LoaiMon loaiMon;

@Column(name = "DON_GIA", precision = 12, scale = 2)
private BigDecimal donGia;`),
 p('MonAn giữ tham chiếu đến LoaiMon, ánh xạ về khóa ngoại MA_LOAI. EAGER là lựa chọn đang có trong mã nguồn, chưa có đánh giá chi phí truy vấn. NhanVien dùng BigDecimal cho lương và LocalDate cho ngày vào làm. Các thuộc tính @Transient chỉ phục vụ mô hình hiển thị, không tạo cột lưu trữ.'));

page('5.5. REPOSITORY, CONTROLLER VÀ THYMELEAF',
 h('5.5.1. Repository đã khai báo'),
 code(`public interface MonAnRepository
        extends JpaRepository<MonAn, Long> {
}
// LoaiMonRepository và NhanVienRepository có cấu trúc tương tự.`),
 p('Interface repository kế thừa khả năng thao tác entity từ Spring Data JPA. Tuy nhiên, controller hiện tại chưa inject hoặc gọi repository; chưa có triển khai Service để nối luồng giao diện với Oracle. Việc khai báo repository vì vậy chưa chứng minh các thao tác CRUD đã được sử dụng.'),
 h('5.5.2. Luồng danh sách hiện có'),
 code(`@GetMapping
public String index(Model model) {
    List<LoaiMon> categories = getSampleCategories();
    int totalCategories = categories.size();
    int totalDishesInCategories = categories.stream()
        .mapToInt(LoaiMon::getSoLuongMon).sum();
    model.addAttribute("categories", categories);
    model.addAttribute("totalCategories", totalCategories);
    model.addAttribute("totalDishesInCategories",
                       totalDishesInCategories);
    model.addAttribute("activeMenu", "loaimon");
    return "loaimon/index";
}`),
 p('Đoạn trích LoaiMonController thể hiện rõ nguồn danh sách mẫu. Template dùng th:each để lặp categories và th:text để đưa thuộc tính ra giao diện. Sidebar và navbar được dùng lại bằng fragment, giúp các trang có cùng bố cục [3].'),
 h('5.5.3. Hướng hoàn thiện tầng xử lý'),
 p('Bổ sung Service nhận repository qua constructor, dùng findAll cho truy vấn và save/delete cho thao tác đã kiểm tra hợp lệ. Controller cần endpoint ghi dữ liệu và xử lý lỗi; form cần tên trường và cơ chế bind phù hợp. Đây là bước tiếp theo của bài tập, không phải đoạn mã đã có trong dự án.'));

page('5.6. GIAO DIỆN VÀ TƯƠNG TÁC ĐÃ XÂY DỰNG',
 table('Bảng 5.1. Đường dẫn đã có controller và template', ['Đường dẫn','Template / nội dung'],[
 ['/','index.html; trang chủ liên kết các nhóm chức năng.'],['/hello','hello.html; nhận message từ HomeController.'],['/loai-mon','loaimon/index.html; 6 loại món mẫu.'],['/mon-an','monan/index.html; 8 món mẫu, 7 đang phục vụ và 1 tạm hết.'],['/nhan-vien','nhanvien/index.html; 8 nhân viên mẫu, 7 đang làm việc và 1 nghỉ phép.']
 ],[2200,6871]),
 h('5.6.1. Loại món và món ăn'),
 p('Trang loại món hiển thị mã, tên, mô tả, số món và trạng thái; có modal thêm/sửa. Tổng số món thuộc các loại được cộng từ số liệu mẫu của loại món, bằng 32, trong khi danh sách món mẫu chỉ có 8 phần tử. Hai số liệu này thuộc hai bộ dữ liệu minh họa độc lập, chưa phải tổng hợp từ cùng một cơ sở dữ liệu.'),
 p('Trang món ăn có thẻ món, bảng dữ liệu, bộ lọc theo loại và tìm kiếm tên/mã. main.js đăng ký các sự kiện tương ứng và chuyển đổi giữa hai kiểu hiển thị. Bộ lọc loại và từ khóa hiện xử lý độc lập; cần kiểm thử khi kết hợp cả hai điều kiện. Trang loại món có ô tìm kiếm nhưng chưa tìm thấy handler searchCategoryInput trong main.js.'),
 h('5.6.2. Nhân viên và biểu mẫu'),
 p('Trang nhân viên hiển thị hồ sơ mẫu, lọc chức vụ và tìm từ khóa trên dòng bảng. Modal thêm/sửa/xóa đang mô phỏng: mã JavaScript gọi preventDefault(), đóng modal và hiện toast. Các thao tác chưa gửi yêu cầu ghi đến máy chủ và không làm thay đổi dữ liệu Oracle.'),
 p('Sidebar vô hiệu hóa các nhóm bàn/đặt bàn, hóa đơn/doanh thu, kho/nguyên liệu bằng liên kết #. Tên người dùng trong sidebar là nội dung hiển thị mẫu. Báo cáo không đưa ảnh chụp chạy thật vì chưa có phiên ứng dụng được kiểm chứng; các mô tả trên dựa vào template và JavaScript.'));

page('CHƯƠNG 6. KIỂM THỬ VÀ ĐÁNH GIÁ',
 h('6.1. Phạm vi đánh giá đã thực hiện'),
 p('Đợt lập báo cáo thực hiện đối chiếu tĩnh các file nguồn và sơ đồ. Chưa chạy Maven test, chưa khởi động ứng dụng, chưa kết nối phiên Oracle và chưa thao tác bằng trình duyệt. Các nhận xét “đã xác nhận” ở đây chỉ áp dụng cho sự tồn tại hoặc luồng mã được đọc, không thay thế kiểm thử thực thi.'),
 table('Bảng 6.1. Kết quả đối chiếu trực tiếp mã nguồn', ['Mã','Nội dung kiểm tra','Kết quả đối chiếu'],[
 ['ST01','Entity ánh xạ Oracle','Có 3 lớp @Entity: LoaiMon, MonAn, NhanVien.'],
 ['ST02','Khóa và sequence','Có @Id, @SequenceGenerator; MonAn có FK đến LoaiMon.'],
 ['ST03','Repository','Có 3 interface extends JpaRepository.'],
 ['ST04','Nguồn dữ liệu danh sách','3 controller dùng getSample…(), chưa gọi repository.'],
 ['ST05','Ghi dữ liệu từ form','main.js chặn submit; chỉ đóng modal và hiển thị toast.'],
 ['ST06','Service và bảo mật','Service chưa có nội dung; SecurityConfig/login trống.'],
 ['ST07','PL/SQL','Không có script CREATE VIEW/PROCEDURE/FUNCTION/TRIGGER/PACKAGE.'],
 ['ST08','Kiểm thử tự động','Có contextLoads() với @SpringBootTest; chưa có kết quả chạy.']
 ],[800,2700,5571]),
 h('6.2. Ý nghĩa của kết quả'),
 p('Phần khung công nghệ, entity và giao diện đã được chuẩn bị. Điểm thiếu quan trọng là kết nối đầy đủ luồng Controller–Service–Repository và xử lý nghiệp vụ ghi dữ liệu. Cần kiểm thử runtime để xác nhận khả năng biên dịch, kết nối Oracle và hiển thị Thymeleaf trong môi trường thực tế của nhóm.'));

page('6.3. KẾ HOẠCH KIỂM THỬ CHỨC NĂNG',
 p('Các ca sau là kế hoạch nghiệm thu cho bài tập lớn. Trạng thái tất cả là “chưa thực thi trong lần lập báo cáo”; nhóm chỉ ghi đạt sau khi thao tác thật và có bằng chứng phù hợp.'),
 table('Bảng 6.2. Ca kiểm thử giao diện và nghiệp vụ', ['Mã','Thao tác / dữ liệu','Kết quả mong đợi'],[
 ['TC01','Mở /loai-mon.','Hiển thị 6 loại mẫu; tên và mô tả không lỗi tiếng Việt.'],
 ['TC02','Mở /mon-an.','Hiển thị 8 món; thống kê mẫu 7 đang phục vụ, 1 tạm hết.'],
 ['TC03','Chọn một loại món.','Chỉ thấy món thuộc loại được chọn.'],
 ['TC04','Tìm tên hoặc mã món.','Danh sách hiển thị các phần tử khớp từ khóa.'],
 ['TC05','Lọc loại rồi tìm từ khóa.','Ghi nhận hành vi kết hợp; sửa nếu hai điều kiện ghi đè nhau.'],
 ['TC06','Chuyển dạng thẻ / bảng.','Đổi kiểu hiển thị; không mất dữ liệu đang xem.'],
 ['TC07','Tìm kiếm / lọc nhân viên.','Dòng phù hợp xuất hiện; kiểm tra kết hợp bộ lọc.'],
 ['TC08','Gửi form thêm món hiện tại.','Chỉ toast mô phỏng; không ghi nhận là đã lưu Oracle.'],
 ['TC09','Thêm món hợp lệ sau khi nối backend.','Có bản ghi mới; tải lại trang vẫn tồn tại.'],
 ['TC10','Giá âm, tên trống, loại không tồn tại.','Máy chủ từ chối; không tạo bản ghi sai.'],
 ['TC11','Xóa loại đang có món.','Từ chối hoặc yêu cầu xử lý quan hệ; không tạo dữ liệu mồ côi.'],
 ['TC12','Hóa đơn 2 × 250.000, giảm 50.000.','Sau khi triển khai: tổng phải trả 450.000.'],
 ['TC13','Nhập một nguyên liệu 2,50 kg.','Sau khi triển khai: tồn tăng đúng 2,50, không tăng lặp.']
 ],[700,3920,4451]),
 note('Ví dụ TC12 dùng dữ liệu giả định để kiểm tra công thức, không phải giao dịch bán hàng đã phát sinh trong hệ thống.'));

page('6.4. KIỂM THỬ ORACLE VÀ ĐÁNH GIÁ GIỚI HẠN',
 table('Bảng 6.3. Kế hoạch kiểm tra cơ sở dữ liệu', ['Mã','Nội dung','Tiêu chí chấp nhận'],[
 ['DB01','Kết nối đúng PDB bằng NHHS_APP.','Đăng nhập được; schema và service name đúng.'],
 ['DB02','Khởi động Hibernate với 3 entity.','Bảng, sequence và FK cần thiết tồn tại; không có lỗi DDL.'],
 ['DB03','Thử INSERT món có mã loại sai.','Oracle từ chối theo FK.'],
 ['DB04','Thử số lượng âm khi có CHECK.','Bản ghi bị từ chối; dữ liệu cũ giữ nguyên.'],
 ['DB05','User BAO_CAO SELECT và UPDATE.','Đọc được đối tượng đã cấp; UPDATE bị từ chối.'],
 ['DB06','Gây lỗi giữa lúc lưu chứng từ.','Phần đầu, chi tiết và tồn kho được hoàn tác cùng nhau.'],
 ['DB07','Tạo view/function/trigger minh họa.','Đối tượng VALID; kết quả đúng bộ dữ liệu kiểm thử.']
 ],[800,3450,4821]),
 h('6.5. Hạn chế hiện tại'),
 p('Chưa có CRUD lưu dữ liệu từ giao diện; chưa có Service nghiệp vụ; chưa triển khai đầy đủ 12 entity và chứng từ. Các cột hiển thị @Transient sẽ không được lưu. Chưa có đăng nhập, phân quyền web, xuất báo cáo hoặc gọi PL/SQL từ Java. Một số bộ lọc và số thống kê cần được kiểm tra lại khi dữ liệu được lấy từ cùng một nguồn.'),
 p('Thiết kế chưa có định lượng nguyên liệu cho món, quản lý lô/hạn dùng, nhật ký xuất kho, thời điểm thanh toán hoặc liên kết cọc–hóa đơn. Vì vậy, hệ thống chưa đủ để tính tồn theo bán hàng và lợi nhuận. Đây là giới hạn cần nêu khi bảo vệ bài tập lớn, không mở rộng thành yêu cầu phải triển khai toàn bộ hệ thống vận hành.'));

page('KẾT LUẬN VÀ HƯỚNG HOÀN THIỆN',
 h('Kết luận'),
 p('Đề tài đã xây dựng mô hình quản lý nhà hàng hải sản ở quy mô bài tập lớn với thiết kế 12 bảng có liên kết khóa chính và khóa ngoại. Mô hình phân tách thông tin danh mục, chứng từ bán hàng và chứng từ nhập hàng, tạo cơ sở để luyện tập thiết kế dữ liệu quan hệ trên Oracle.'),
 p('Phần mã nguồn đã chuẩn bị ứng dụng Java Spring Boot với Spring MVC, Thymeleaf, Spring Data JPA và Oracle JDBC; hoàn thành ánh xạ entity/repository cho loại món, món ăn và nhân viên; có trang chủ và các trang danh sách sử dụng dữ liệu mẫu. Cấu hình datasource và script user/role thể hiện hướng tích hợp Oracle trong đề tài.'),
 p('Kết quả hiện tại phù hợp để trình bày kiến trúc, nguyên lý ánh xạ dữ liệu và giao diện minh họa. Báo cáo không kết luận đã hoàn thành nghiệp vụ đặt bàn, hóa đơn, nhập kho, phân quyền web hoặc PL/SQL. Việc ghi nhận đúng mức độ triển khai giúp xác định rõ nội dung nhóm đã làm và căn cứ cho phần phát triển tiếp theo.'),
 h('Hướng hoàn thiện trong phạm vi học phần'),
 p('Ưu tiên thứ nhất là nối ba màn hình hiện có với Service/Repository để đọc và ghi Oracle; thay toast mô phỏng bằng kết quả từ máy chủ, bổ sung kiểm tra dữ liệu và xác nhận bản ghi còn tồn tại sau khi tải lại trang. Đồng thời thống nhất các chỉ số trên giao diện theo dữ liệu thực.'),
 p('Ưu tiên tiếp theo là hoàn thiện các entity và quan hệ còn lại theo 12 bảng, xử lý giao dịch đầu–chi tiết chứng từ, sau đó xây dựng chức năng báo cáo đơn giản. Nếu học phần yêu cầu Oracle nâng cao, nhóm có thể triển khai và kiểm thử các ví dụ view, function, procedure, trigger và package đã nêu.'),
 p('Cuối cùng, hoàn thiện đăng nhập và kiểm tra quyền web, chạy các ca kiểm thử đã lập, bổ sung ảnh chụp giao diện và kết quả truy vấn thực tế. Thông tin phân công và kết quả đóng góp của từng thành viên cần được nhóm điền đúng thực tế trước khi nộp.'));

page('TÀI LIỆU THAM KHẢO',
 p('[1] Spring. “Structuring Your Code”, Spring Boot Reference. Tham khảo cách tổ chức package và lớp ứng dụng gốc. https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html (truy cập 24/10/2026).'),
 p('[2] Spring. “JPA”, Spring Data JPA Reference. Tham khảo cơ chế repository và truy cập dữ liệu JPA. https://docs.spring.io/spring-data/jpa/reference/jpa.html (truy cập 24/10/2026).'),
 p('[3] Thymeleaf. “Documentation”. Tham khảo template và tích hợp Spring. https://www.thymeleaf.org/documentation.html (truy cập 24/10/2026).'),
 p('[4] Oracle. “CREATE ROLE”, Oracle Database SQL Language Reference. Tham khảo user, role và việc tập hợp quyền. https://docs.oracle.com/en/database/oracle/oracle-database/26/sqlrf/CREATE-ROLE.html (truy cập 24/10/2026; trang tài liệu được Oracle chuyển hướng từ đường dẫn /23/).'),
 p('[5] Oracle. “PL/SQL Subprograms”, Oracle Database PL/SQL Language Reference. Tham khảo procedure và function. https://docs.oracle.com/en/database/oracle/oracle-database/26/lnpls/plsql-subprograms.html (truy cập 24/10/2026; trang tài liệu được Oracle chuyển hướng từ đường dẫn /23/).'),
 p('[6] Nhóm thực hiện. Mã nguồn QuanLyNHHS: pom.xml; src/main/resources/application.properties, oracle_setup_user.sql; các thư mục controller, model, repository, service, templates, static; lớp QuanLyNhhsApplicationTests.java. Nguồn đối chiếu trực tiếp của báo cáo.'),
 p('[7] Sơ đồ “Cơ sở dữ liệu quản lý nhà hàng hải sản – Quy mô báo cáo 12 bảng” do nhóm cung cấp. Nguồn thiết kế thực thể, thuộc tính và quan hệ; Hình 4.1 được vẽ lại từ nội dung sơ đồ này.'),
 note('Phiên bản công nghệ của dự án được lấy từ mã cấu hình. Các tài liệu tham khảo trực tuyến có thể hiển thị phiên bản khác; chúng được dùng cho khái niệm, không chứng minh dự án đã chạy với phiên bản đó.'));

// DDL minh họa toàn mô hình; có chủ đích độc lập với DDL Hibernate.
const ddlOrder=['LOAI_MON','NHAN_VIEN','KHACH_HANG','BAN_AN','NHA_CUNG_CAP','NGUYEN_LIEU','MON_AN','DAT_BAN','HOA_DON','PHIEU_NHAP','CT_HOA_DON','CT_PHIEU_NHAP'];
const mandatory={LOAI_MON:['TEN_LOAI'],NHAN_VIEN:['HO_TEN'],KHACH_HANG:['HO_TEN'],BAN_AN:['TEN_BAN','SO_CHO'],NHA_CUNG_CAP:['TEN_NCC'],NGUYEN_LIEU:['TEN_NL'],MON_AN:['MA_LOAI','TEN_MON','DON_GIA'],DAT_BAN:['MA_KH','MA_BAN','NGAY_DAT','SO_NGUOI'],HOA_DON:['MA_BAN','MA_NV','NGAY_LAP'],PHIEU_NHAP:['MA_NCC','MA_NV','NGAY_NHAP'],CT_HOA_DON:['SO_LUONG','DON_GIA'],CT_PHIEU_NHAP:['SO_LUONG','DON_GIA']};
const defaults={DIEM_TICH_LUY:'0',TIEN_COC:'0',GIAM_GIA:'0',SO_LUONG_TON:'0'};
const fk={MON_AN:{MA_LOAI:'LOAI_MON(MA_LOAI)'},DAT_BAN:{MA_KH:'KHACH_HANG(MA_KH)',MA_BAN:'BAN_AN(MA_BAN)'},HOA_DON:{MA_BAN:'BAN_AN(MA_BAN)',MA_NV:'NHAN_VIEN(MA_NV)',MA_KH:'KHACH_HANG(MA_KH)'},PHIEU_NHAP:{MA_NCC:'NHA_CUNG_CAP(MA_NCC)',MA_NV:'NHAN_VIEN(MA_NV)'},CT_HOA_DON:{MA_HD:'HOA_DON(MA_HD)',MA_MON:'MON_AN(MA_MON)'},CT_PHIEU_NHAP:{MA_PN:'PHIEU_NHAP(MA_PN)',MA_NL:'NGUYEN_LIEU(MA_NL)'}};
let ddl='-- THIET KE MINH HOA. Chua chay tren Oracle.\n-- Chi dung trong schema hoc tap trong; khong chay tren schema Hibernate da tao.\n-- VARCHAR2(... CHAR): gioi han theo ky tu.\n\n';
const scripts=[];
for(const name of ddlOrder){
 const s=schemas.find(x=>x.name===name),lines=s.cols.map(([col,type])=>`  ${col} ${type.replace(/VARCHAR2\((\d+)\)/,'VARCHAR2($1 CHAR)')}${defaults[col]!==undefined?' DEFAULT '+defaults[col]:''}${mandatory[name].includes(col)?' NOT NULL':''}`);
 lines.push(`  PRIMARY KEY (${s.key.replace(/[()]/g,'')})`);
 for(const [col,ref] of Object.entries(fk[name]||{}))lines.push(`  FOREIGN KEY (${col}) REFERENCES ${ref}`);
 for(const col of s.cols.map(x=>x[0])){
  if(['SO_LUONG','SO_CHO','SO_NGUOI'].includes(col))lines.push(`  CHECK (${col}>0)`);
  else if(['DON_GIA','LUONG','TIEN_COC','GIAM_GIA','SO_LUONG_TON','DIEM_TICH_LUY','THANH_TIEN','TONG_TIEN'].includes(col))lines.push(`  CHECK (${col}>=0)`);
 }
 const str=`CREATE TABLE ${name} (\n${lines.join(',\n')}\n);`;
 scripts.push(str);ddl+=str+'\n\n';
}
// 2 bảng mỗi trang để giữ DDL đọc được và không ép nhỏ cỡ chữ.
for(let i=0;i<scripts.length;i+=2){
 page(`PHỤ LỤC A.${i/2+1}. DDL THAM KHẢO: ${ddlOrder[i]} / ${ddlOrder[i+1]}`,
 note('DDL tự xây dựng theo thiết kế 12 bảng, chưa thực thi trên Oracle. Dùng schema học tập trống; không chạy chồng lên các bảng Hibernate đã tạo. Các CHECK/NOT NULL bổ sung là quy tắc đề xuất.'),
 code(scripts[i]),code(scripts[i+1]),
 ...(i===0?[p('Mã khóa chính của các bảng thiết kế được cấp tường minh trong DDL tham khảo này. Ba entity hiện có dùng SEQ_LOAI_MON, SEQ_MON_AN, SEQ_NHAN_VIEN; nếu tạo bảng thủ công cho ứng dụng cần tạo sequence tương ứng và đặt giá trị bắt đầu tránh trùng mã.')]:[]),
 ...(i===10?[p('Khóa chính ghép ngăn lặp cùng món/nguyên liệu trong chứng từ. CHECK không tự đồng bộ tổng đầu chứng từ. Khi dùng DDL này, cần bổ sung quy trình giao dịch và cách tính tổng đã trình bày tại mục 4.4, 4.7 và 4.8.')]:[]));
}
require('fs').writeFileSync(require('path').join(__dirname,'oracle_thiet_ke_tham_khao.sql'),ddl);
};
