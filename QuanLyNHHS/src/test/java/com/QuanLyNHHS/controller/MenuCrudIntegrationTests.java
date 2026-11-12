package com.QuanLyNHHS.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.repository.*;
import com.QuanLyNHHS.service.*;
import com.QuanLyNHHS.support.MenuTestApplication;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Bộ kiểm thử CRUD, dữ liệu đầu vào và bảo mật bằng HTTP giả lập và database H2.
 */
@SpringBootTest(classes = MenuTestApplication.class)
@ActiveProfiles("test")
class MenuCrudIntegrationTests {
    // context: Spring WebApplicationContext của bộ kiểm thử.
    @Autowired WebApplicationContext context;
    // security: chuỗi bộ lọc Spring Security dùng trong kiểm thử.
    @Autowired FilterChainProxy security;
    // categories: thành phần LoaiMonService dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired LoaiMonService categories;
    // dishes: thành phần MonAnService dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired MonAnService dishes;
    // staff: thành phần NhanVienService dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired NhanVienService staff;
    // categoryRepository: thành phần LoaiMonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired LoaiMonRepository categoryRepository;
    // dishRepository: thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired MonAnRepository dishRepository;
    // staffRepository: thành phần NhanVienRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired NhanVienRepository staffRepository;
    // invoices: thành phần HoaDonRepository dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired HoaDonRepository invoices;
    // details: thành phần CTHoaDonRepository dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired CTHoaDonRepository details;
    // receipts: thành phần PhieuNhapRepository dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired PhieuNhapRepository receipts;
    // suppliers: thành phần NhaCungCapRepository cung cấp dữ liệu cho danh sách lựa chọn trên form.
    @Autowired NhaCungCapRepository suppliers;
    // accounts: thành phần TaiKhoanRepository dùng chuẩn bị, truy vấn và kiểm tra dữ liệu trong kiểm thử.
    @Autowired TaiKhoanRepository accounts;
    // passwordEncoder: thành phần băm và đối chiếu mật khẩu, không giải mã mật khẩu.
    @Autowired PasswordEncoder passwordEncoder;
    // mvc: MockMvc dùng gửi request giả lập đến controller mà không mở cổng HTTP.
    MockMvc mvc;
    // session: phiên HTTP giả lập của người dùng trong kiểm thử.
    MockHttpSession session;
    // csrf: token chống giả mạo request dùng khi kiểm thử các yêu cầu POST.
    CsrfToken csrf;

    /**
     * Dọn dữ liệu kiểm thử, tạo MockMvc có bộ lọc Security và chuẩn bị phiên đăng nhập cùng token CSRF.
     */
    @BeforeEach
    void setup() throws Exception {
        details.deleteAll();
        invoices.deleteAll();
        receipts.deleteAll();
        dishRepository.deleteAll();
        categoryRepository.deleteAll();
        staffRepository.deleteAll();
        suppliers.deleteAll();
        accounts.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        session = new MockHttpSession();
        // authentication: thông tin xác thực của request hiện tại; cần loại trừ người dùng ẩn danh.
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "nhanvien", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(authentication));
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = mvc.perform(get("/loai-mon/them").session(session)).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        csrf = (CsrfToken) result.getRequest().getAttribute(CsrfToken.class.getName());
        assertNotNull(csrf);
    }

    /**
     * Kiểm tra HTTP thêm/sửa/xóa loại món và việc escape HTML của dữ liệu người dùng.
     */
    @Test
    void categoryCreateEditDeleteAndEscapingWorkThroughHttp() throws Exception {
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/loai-mon/them", "tenLoai", "  <b>Hải sản</b>  ", "moTa", "Mô tả", "trangThai", "Hoạt động");
        assertEquals(302, result.getResponse().getStatus());
        // entity: entity LoaiMon lấy từ database hoặc tạo mới để lưu; khác với DTO chỉ mang dữ liệu của form.
        LoaiMon entity = categoryRepository.findAll().getFirst();
        assertEquals("<b>Hải sản</b>", entity.getTenLoai());
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = page("/loai-mon/sua/" + entity.getMaLoai());
        assertTrue(html.contains("&lt;b&gt;Hải sản&lt;/b&gt;"));
        assertTrue(html.contains("name=\"_csrf\""));
        assertFalse(html.contains("chưa hỗ trợ lưu"));
        assertEquals(302, submit("/loai-mon/sua/" + entity.getMaLoai(), "tenLoai", "Cá", "trangThai", "Tạm dừng").getResponse().getStatus());
        assertEquals("Cá", categories.getById(entity.getMaLoai()).getTenLoai());
        assertEquals(302, submit("/loai-mon/xoa/" + entity.getMaLoai()).getResponse().getStatus());
        assertFalse(categoryRepository.existsById(entity.getMaLoai()));
    }

    /**
     * Kiểm tra thêm/sửa/xóa món, điền sẵn form, chuyển loại và bỏ qua mã chính giả gửi từ form.
     */
    @Test
    void dishCreateEditDeleteAndFormPrefillWork() throws Exception {
        // category: loại món dùng trong tình huống kiểm thử.
        var category = category();
        assertEquals(302, submit("/mon-an/them", "maLoai", category.getMaLoai().toString(),
                "tenMon", "Tôm hấp", "donVi", "Kg", "donGia", "125000.50", "trangThai", "Đang phục vụ",
                "hinhAnh", "https://example.com/tom.jpg", "moTa", "Hấp sả").getResponse().getStatus());
        // dish: món ăn dùng trong tình huống kiểm thử.
        var dish = dishRepository.findAll().getFirst();
        assertEquals(new BigDecimal("125000.50"), dish.getDonGia());
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = page("/mon-an/sua/" + dish.getMaMon());
        assertTrue(html.contains("Tôm hấp"));
        assertTrue(html.contains("125000.50"));
        assertTrue(html.contains("selected=\"selected\""));
        // otherCategory: loại món khác dùng kiểm tra việc chuyển loại khi sửa món.
        var otherCategory = category();
        assertEquals(302, submit("/mon-an/sua/" + dish.getMaMon(), "maLoai", otherCategory.getMaLoai().toString(),
                "tenMon", "Tôm nướng", "donVi", "Đĩa", "donGia", "99000", "trangThai", "Tạm hết",
                "maMon", "999999").getResponse().getStatus());
        // saved: bản ghi đã được lưu để kiểm tra kết quả.
        var saved = dishes.getById(dish.getMaMon());
        assertEquals("Tôm nướng", saved.getTenMon());
        assertEquals(otherCategory.getMaLoai(), saved.getMaLoai());
        assertFalse(dishRepository.existsById(999999L));
        assertEquals(302, submit("/mon-an/xoa/" + dish.getMaMon()).getResponse().getStatus());
        assertEquals(0, dishRepository.count());
    }

    /**
     * Kiểm tra lưu đủ các trường hồ sơ nhân viên, sửa đúng mã và xóa được bản ghi chưa có chứng từ.
     */
    @Test
    void employeeCrudPreservesAllFieldsAndUsesRealId() throws Exception {
        assertEquals(302, submit("/nhan-vien/them", "hoTen", "Nguyễn An", "gioiTinh", "Nam", "sdt", "0901234567",
                "email", "an@example.com", "chucVu", "Đầu bếp", "caLamViec", "Ca sáng", "luong", "9000000.25",
                "ngayVaoLam", "2026-01-02", "trangThai", "Đang làm việc").getResponse().getStatus());
        // employee: nhân viên dùng trong tình huống kiểm thử.
        var employee = staffRepository.findAll().getFirst();
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = page("/nhan-vien/sua/" + employee.getMaNv());
        assertTrue(html.contains("2026-01-02"));
        assertTrue(html.contains("an@example.com"));
        assertEquals(302, submit("/nhan-vien/sua/" + employee.getMaNv(), "hoTen", "Nguyễn Bình", "gioiTinh", "Nữ",
                "sdt", "0907654321", "email", "binh@example.com", "chucVu", "Quản lý nhà hàng", "caLamViec", "Ca tối",
                "luong", "10000000", "ngayVaoLam", "2026-02-03", "trangThai", "Đã nghỉ việc").getResponse().getStatus());
        // updated: bản ghi đọc lại sau khi cập nhật.
        var updated = staff.getById(employee.getMaNv());
        assertEquals("Nguyễn Bình", updated.getHoTen());
        assertEquals("Nữ", updated.getGioiTinh());
        assertEquals(LocalDate.of(2026, 2, 3), updated.getNgayVaoLam());
        assertEquals("Đã nghỉ việc", updated.getTrangThai());
        assertEquals(302, submit("/nhan-vien/xoa/" + employee.getMaNv()).getResponse().getStatus());
        assertEquals(0, staffRepository.count());
    }

    /**
     * Kiểm tra form loại món sai hiển thị lỗi, giữ nội dung nhập và không ghi database.
     */
    @Test
    void invalidCategoryKeepsInputAndDoesNotSave() throws Exception {
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/loai-mon/them", "tenLoai", " ", "trangThai", "sai", "moTa", "Giữ lại nội dung");
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Nhập tên loại món."));
        assertTrue(result.getResponse().getContentAsString().contains("Giữ lại nội dung"));
        assertEquals(0, categoryRepository.count());
    }

    /**
     * Kiểm tra giá âm, vượt giới hạn, sai định dạng và URL ảnh không hợp lệ đều bị từ chối.
     */
    @Test
    void invalidDishPricePrecisionAndImageSchemeDoNotSave() throws Exception {
        // category: loại món dùng trong tình huống kiểm thử.
        var category = category();
        for (/* price: giá thử nghiệm dùng để kiểm tra dữ liệu không hợp lệ. */ String price : List.of("-1", "0.001", "10000000000", "khong-phai-so")) {
            // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
            var result = submit("/mon-an/them", "maLoai", category.getMaLoai().toString(), "tenMon", "Tôm",
                    "donVi", "Kg", "donGia", price, "trangThai", "Đang phục vụ");
            assertEquals(200, result.getResponse().getStatus());
            assertTrue(result.getResponse().getContentAsString().contains("aria-invalid=\"true\""));
        }
        assertEquals(200, submit("/mon-an/them", "maLoai", category.getMaLoai().toString(), "tenMon", "Tôm",
                "donVi", "Kg", "donGia", "100", "trangThai", "Đang phục vụ", "hinhAnh", "javascript:alert(1)").getResponse().getStatus());
        assertEquals(0, dishRepository.count());
    }

    /**
     * Kiểm tra không lưu món nếu loại món được chọn đã mất hoặc là mã giả.
     */
    @Test
    void deletedOrForgedCategoryIsReportedWithoutSavingDish() throws Exception {
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/mon-an/them", "maLoai", "999999", "tenMon", "Tôm", "donVi", "Kg",
                "donGia", "100", "trangThai", "Đang phục vụ");
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("không tồn tại"));
        assertEquals(0, dishRepository.count());
    }

    /**
     * Kiểm tra email, lương và ngày không hợp lệ giữ nguyên dữ liệu nhân viên trong database.
     */
    @Test
    void invalidEmployeeUpdateLeavesDatabaseUnchanged() throws Exception {
        // employee: nhân viên dùng trong tình huống kiểm thử.
        var employee = employee();
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/nhan-vien/sua/" + employee.getMaNv(), "hoTen", "Tên mới", "email", "email-sai",
                "chucVu", "Thu ngân", "luong", "-10", "ngayVaoLam", "2026-02-31", "trangThai", "Đang làm việc");
        assertEquals(200, result.getResponse().getStatus());
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("Email không hợp lệ."));
        assertTrue(html.contains("Lương không được âm."));
        assertTrue(html.contains("Ngày vào làm không hợp lệ."));
        assertEquals("Nhân viên", staff.getById(employee.getMaNv()).getHoTen());
    }

    /**
     * Kiểm tra không xóa loại đang có món ăn.
     */
    @Test
    void cannotDeleteCategoryWithDishes() throws Exception {
        // dish: món ăn dùng trong tình huống kiểm thử.
        var dish = dish();
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/loai-mon/xoa/" + dish.getMaLoai());
        assertEquals(302, result.getResponse().getStatus());
        assertTrue(result.getFlashMap().get("errorMessage").toString().contains("đang có món"));
        assertTrue(categoryRepository.existsById(dish.getMaLoai()));
    }

    /**
     * Kiểm tra không xóa món đã có trong hóa đơn kể cả hóa đơn chưa thanh toán.
     */
    @Test
    void cannotDeleteDishReferencedByAnyInvoice() throws Exception {
        // dish: món ăn dùng trong tình huống kiểm thử.
        var dish = dish();
        // invoice: hóa đơn dùng trong tình huống kiểm thử.
        HoaDon invoice = new HoaDon();
        invoice.setTrangThai("Chua thanh toan");
        invoices.saveAndFlush(invoice);
        // detail: dòng chi tiết đang được xử lý.
        CTHoaDon detail = new CTHoaDon();
        detail.setId(new CTHoaDon.CTHoaDonId(invoice.getMaHd(), dish.getMaMon()));
        detail.setSoLuong(BigDecimal.ONE);
        details.saveAndFlush(detail);
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = submit("/mon-an/xoa/" + dish.getMaMon());
        assertTrue(result.getFlashMap().containsKey("errorMessage"));
        assertTrue(dishRepository.existsById(dish.getMaMon()));
        assertEquals(1, details.count());
    }

    /**
     * Kiểm tra nhân viên đã lập hóa đơn hoặc phiếu nhập đều được bảo vệ khỏi thao tác xóa.
     */
    @Test
    void cannotDeleteEmployeeWithInvoiceOrReceipt() throws Exception {
        // employee: nhân viên dùng trong tình huống kiểm thử.
        var employee = employee();
        // invoice: hóa đơn dùng trong tình huống kiểm thử.
        HoaDon invoice = new HoaDon();
        invoice.setMaNv(employee.getMaNv());
        invoices.saveAndFlush(invoice);
        assertTrue(submit("/nhan-vien/xoa/" + employee.getMaNv()).getFlashMap().containsKey("errorMessage"));
        invoices.deleteAll();
        // supplier: nhà cung cấp được chọn hoặc đang chỉnh sửa.
        var supplier = new NhaCungCap();
        supplier.setTenNcc("Nhà cung cấp");
        suppliers.saveAndFlush(supplier);
        // receipt: phiếu nhập đang được đọc, tạo hoặc chỉnh sửa.
        var receipt = new PhieuNhap();
        receipt.setNhaCungCap(supplier);
        receipt.setMaNv(employee.getMaNv());
        receipt.setNgayNhap(LocalDate.of(2026, 1, 1));
        receipts.saveAndFlush(receipt);
        assertTrue(submit("/nhan-vien/xoa/" + employee.getMaNv()).getFlashMap().containsKey("errorMessage"));
        assertTrue(staffRepository.existsById(employee.getMaNv()));
    }

    /**
     * Kiểm tra mã đã mất trả 404 khi mở form, báo lỗi khi xóa và không bị thêm mới khi sửa.
     */
    @Test
    void missingRecordsReturn404AndUpdatesDoNotInsert() throws Exception {
        for (/* path: đường dẫn URL hoặc đường dẫn cần xử lý. */ String path : List.of("/mon-an", "/loai-mon", "/nhan-vien")) {
            assertEquals(404, mvc.perform(get(path + "/sua/999999").session(session)).andReturn().getResponse().getStatus());
            assertTrue(submit(path + "/xoa/999999").getFlashMap().containsKey("errorMessage"));
        }
        assertEquals(200, submit("/loai-mon/sua/999999", "tenLoai", "Không được thêm mới", "trangThai", "Hoạt động").getResponse().getStatus());
        assertEquals(0, categoryRepository.count());
    }

    /**
     * Kiểm tra bean service vẫn áp dụng Bean Validation khi được gọi ngoài controller.
     */
    @Test
    void serviceValidatesCallsFromOutsideController() {
        // form: đối tượng MonAnForm chứa dữ liệu nhập của biểu mẫu.
        MonAnForm form = new MonAnForm();
        form.setTenMon(" ");
        assertThrows(ConstraintViolationException.class, () -> dishes.save(null, form));
        assertEquals(0, dishRepository.count());
    }

    /**
     * Kiểm tra cần đăng nhập, POST cần CSRF và yêu cầu GET không thể xóa dữ liệu.
     */
    @Test
    void loginAndCsrfRemainRequiredAndGetCannotDelete() throws Exception {
        assertEquals(302, mvc.perform(get("/mon-an")).andReturn().getResponse().getStatus());
        assertEquals(403, mvc.perform(post("/loai-mon/them").session(session)
                .param("tenLoai", "Không có CSRF").param("trangThai", "Hoạt động")).andReturn().getResponse().getStatus());
        // category: loại món dùng trong tình huống kiểm thử.
        var category = category();
        assertEquals(405, mvc.perform(get("/loai-mon/xoa/" + category.getMaLoai()).session(session)).andReturn().getResponse().getStatus());
        assertTrue(categoryRepository.existsById(category.getMaLoai()));
    }

    /**
     * Kiểm tra trang đăng nhập hướng dẫn liên hệ quản trị viên và không còn đăng ký.
     */
    @Test
    void loginPageDirectsUsersToAdministratorWithoutRegistration() throws Exception {
        var result = mvc.perform(get("/login").param("registered", "true")).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String html = result.getResponse().getContentAsString();
        assertTrue(html.contains("tài khoản do quản trị viên cấp"));
        assertTrue(html.contains("liên hệ quản trị viên"));
        assertFalse(html.contains("/register"));
        assertFalse(html.contains("Đăng ký"));
        assertTrue(html.contains("name=\"_csrf\""));
        assertEquals("/", mvc.perform(get("/login").session(session)).andReturn().getResponse().getRedirectedUrl());
    }

    /**
     * Kiểm tra URL đăng ký cũ không thể tạo tài khoản, kể cả khi POST có CSRF hợp lệ.
     */
    @Test
    void registrationIsUnavailableForAnonymousAndAuthenticatedUsers() throws Exception {
        var loginPage = mvc.perform(get("/login")).andReturn();
        var anonymousSession = (MockHttpSession) loginPage.getRequest().getSession();
        var token = (CsrfToken) loginPage.getRequest().getAttribute(CsrfToken.class.getName());
        var anonymousGet = mvc.perform(get("/register").session(anonymousSession)).andReturn();
        assertEquals(302, anonymousGet.getResponse().getStatus());
        assertTrue(anonymousGet.getResponse().getRedirectedUrl().endsWith("/login"));
        var anonymousPost = mvc.perform(post("/register").session(anonymousSession)
                .param(token.getParameterName(), token.getToken()).param("hoTen", "Người dùng")
                .param("tenDangNhap", "nguoidung").param("matKhau", "matkhau123")
                .param("xacNhanMatKhau", "matkhau123")).andReturn();
        assertEquals(302, anonymousPost.getResponse().getStatus());
        assertTrue(anonymousPost.getResponse().getRedirectedUrl().endsWith("/login"));
        assertEquals(404, mvc.perform(get("/register").session(session)).andReturn().getResponse().getStatus());
        assertEquals(404, submit("/register", "hoTen", "Người dùng", "tenDangNhap", "nguoidung",
                "matKhau", "matkhau123", "xacNhanMatKhau", "matkhau123").getResponse().getStatus());
        assertEquals(0, accounts.count());
    }

    /**
     * Kiểm tra tài khoản được cấp với mật khẩu BCrypt vẫn đăng nhập và dùng phiên HTTP.
     */
    @Test
    void provisionedAccountCanLoginWithBcryptAndSession() throws Exception {
        var account = new TaiKhoan();
        account.setHoTen("Người dùng");
        account.setTenDangNhap("nguoidung");
        account.setMatKhau(passwordEncoder.encode("matkhau123"));
        accounts.saveAndFlush(account);

        var loginPage = mvc.perform(get("/login")).andReturn();
        var anonymousSession = (MockHttpSession) loginPage.getRequest().getSession();
        var token = (CsrfToken) loginPage.getRequest().getAttribute(CsrfToken.class.getName());
        var failedLogin = mvc.perform(post("/login").session(anonymousSession)
                .param(token.getParameterName(), token.getToken()).param("tenDangNhap", "nguoidung")
                .param("matKhau", "saimatkhau")).andReturn();
        assertEquals("/login?error", failedLogin.getResponse().getRedirectedUrl());
        assertNull(anonymousSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));

        var login = mvc.perform(post("/login").session(anonymousSession)
                .param(token.getParameterName(), token.getToken()).param("tenDangNhap", " NguoiDung ")
                .param("matKhau", "matkhau123")).andReturn();
        assertEquals(302, login.getResponse().getStatus());
        assertEquals("/", login.getResponse().getRedirectedUrl());
        assertNotNull(anonymousSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
        assertEquals(200, mvc.perform(get("/nhan-vien").session(anonymousSession)).andReturn().getResponse().getStatus());
    }

    /**
     * Tạo loại món hợp lệ làm dữ liệu đầu vào cho kiểm thử.
     * @return kết quả kiểu {@code LoaiMon} cho nơi gọi sử dụng
     */
    private LoaiMon category() {
        // form: đối tượng LoaiMonForm chứa dữ liệu nhập của biểu mẫu.
        LoaiMonForm form = new LoaiMonForm();
        form.setTenLoai("Hải sản");
        return categories.save(null, form);
    }
    /**
     * Tạo món hợp lệ cùng loại món để dùng trong kiểm thử.
     * @return kết quả kiểu {@code MonAn} cho nơi gọi sử dụng
     */
    private MonAn dish() {
        // form: đối tượng MonAnForm chứa dữ liệu nhập của biểu mẫu.
        MonAnForm form = new MonAnForm();
        form.setMaLoai(category().getMaLoai());
        form.setTenMon("Tôm hấp");
        form.setDonVi("Kg");
        form.setDonGia(new BigDecimal("100000"));
        return dishes.save(null, form);
    }
    /**
     * Tạo nhân viên hợp lệ để dùng trong kiểm thử.
     * @return kết quả kiểu {@code NhanVien} cho nơi gọi sử dụng
     */
    private NhanVien employee() {
        // form: đối tượng NhanVienForm chứa dữ liệu nhập của biểu mẫu.
        NhanVienForm form = new NhanVienForm();
        form.setHoTen("Nhân viên");
        form.setChucVu("Thu ngân");
        return staff.save(null, form);
    }
    /**
     * Gửi GET bằng phiên đã đăng nhập, kiểm tra HTTP 200 và trả nội dung HTML.
     * @param path đường dẫn URL hoặc đường dẫn cần xử lý
     * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
     */
    private String page(String path) throws Exception {
        // result: kết quả trả về từ thao tác kiểm thử để đọc status, HTML hoặc flash.
        var result = mvc.perform(get(path).session(session)).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        return result.getResponse().getContentAsString();
    }
    /**
     * Tạo POST có CSRF và các cặp tên/giá trị, trả kết quả để kiểm tra trạng thái, Model hoặc flash.
     * @param path đường dẫn URL hoặc đường dẫn cần xử lý
     * @param fields mảng xen kẽ tên trường và giá trị, ví dụ tenLoai, Hải sản
     * @return kết quả kiểu {@code MvcResult} cho nơi gọi sử dụng
     */
    private MvcResult submit(String path, String... fields) throws Exception {
        // request: request POST giả lập đang được điền tham số.
        MockHttpServletRequestBuilder request = post(path).session(session)
                .param(csrf.getParameterName(), csrf.getToken());
        for (/* index: chỉ số phần tử đang xét trong vòng lặp. */ int index = 0; index < fields.length; index += 2) request.param(fields[index], fields[index + 1]);
        return mvc.perform(request).andReturn();
    }
}
