package com.QuanLyNHHS.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.QuanLyNHHS.model.LoaiMon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.model.NhanVien;
import com.QuanLyNHHS.service.MonAnService;
import com.QuanLyNHHS.service.LoaiMonService;
import com.QuanLyNHHS.service.NhanVienService;
import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.LoaiMonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;
import com.QuanLyNHHS.repository.NhanVienRepository;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Bộ kiểm thử hiển thị Thymeleaf với repository giả lập.
 */
class DatabasePagesTests {
    /**
     * Kiểm tra các trang hiển thị thông báo rỗng và không tự chèn dữ liệu mẫu khi repository không có bản ghi.
     */
    @Test
    void emptyTablesRenderEmptyMessagesWithoutSampleData() throws Exception {
        // staff: repository nhân viên giả lập dùng để kiểm tra giao diện.
        var staff = repository(NhanVienRepository.class, Map.of("findAll", List.of()));
        // dishes: danh sách món ăn.
        var dishes = repository(MonAnRepository.class,
                Map.of("findAll", List.of(), "demMonTheoLoai", List.of()));
        // categories: danh sách loại món.
        var categories = repository(LoaiMonRepository.class, Map.of("findAll", List.of()));
        // details: repository chi tiết hóa đơn giả lập dùng cung cấp lượng bán cho trang món ăn.
        var details = repository(CTHoaDonRepository.class, Map.of("tongSoLuongDaBanTheoMon", List.of()));
        // mvc: MockMvc dùng gửi request giả lập đến controller mà không mở cổng HTTP.
        MockMvc mvc = pages(new NhanVienController(new NhanVienService(staff, null, null)),
                new MonAnController(new MonAnService(dishes, categories, details), new LoaiMonService(categories, dishes)),
                new LoaiMonController(new LoaiMonService(categories, dishes)));

        // staffHtml: HTML trang nhân viên dùng để đối chiếu kết quả.
        String staffHtml = render(mvc, "/nhan-vien");
        // dishHtml: HTML trang món ăn dùng để đối chiếu kết quả.
        String dishHtml = render(mvc, "/mon-an");
        // categoryHtml: HTML trang loại món dùng để đối chiếu kết quả.
        String categoryHtml = render(mvc, "/loai-mon");
        assertTrue(staffHtml.contains("Chưa có nhân viên."));
        assertTrue(dishHtml.contains("Chưa có món ăn."));
        assertTrue(categoryHtml.contains("Chưa có loại món."));
        assertFalse(staffHtml.contains("Nguyễn Cao Hoàng"));
        assertFalse(dishHtml.contains("Tôm Hùm Alaska"));
        assertFalse(categoryHtml.contains("Tôm &amp; Cua ghẹ"));
    }

    /**
     * Kiểm tra nhân viên có các cột null vẫn hiển thị được và không sinh thông tin giả.
     */
    @Test
    void existingStaffWithNullableColumnsRendersWithoutInventedValues() throws Exception {
        // employee: nhân viên dùng trong tình huống kiểm thử.
        NhanVien employee = new NhanVien(91L, "Nhân viên từ Oracle", null, "0909000001", null, null, null);
        // staff: repository nhân viên giả lập dùng để kiểm tra giao diện.
        var staff = repository(NhanVienRepository.class, Map.of("findAll", List.of(employee)));
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = render(pages(new NhanVienController(new NhanVienService(staff, null, null))), "/nhan-vien");

        assertTrue(html.contains("Nhân viên từ Oracle"));
        assertTrue(html.contains("NV91"));
        assertTrue(html.contains("Chưa cập nhật"));
        assertFalse(html.contains("Chưa có nhân viên."));
        assertFalse(html.contains("hoang.nguyen@nhhs.vn"));
    }

    /**
     * Kiểm tra số món của từng loại và giá trị 0 cho loại chưa có món.
     */
    @Test
    void categoriesDisplayDatabaseCountsIncludingZeroForAnEmptyCategory() throws Exception {
        // first: bản ghi thứ nhất của dữ liệu kiểm thử.
        LoaiMon first = new LoaiMon(21L, "Loại có món", null, "Hoạt động");
        // second: bản ghi thứ hai của dữ liệu kiểm thử.
        LoaiMon second = new LoaiMon(22L, "Loại chưa có món", null, "Tạm dừng");
        // categories: danh sách loại món.
        var categories = repository(LoaiMonRepository.class, Map.of("findAll", List.of(first, second)));
        // dishes: danh sách món ăn.
        var dishes = repository(MonAnRepository.class,
                Map.of("demMonTheoLoai", List.<Object[]>of(new Object[]{21L, 3L})));
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = render(pages(new LoaiMonController(new LoaiMonService(categories, dishes))), "/loai-mon");

        assertTrue(html.contains("3 món"));
        assertTrue(html.contains("0 món"));
        assertTrue(html.contains("Loại chưa có món"));
        assertFalse(html.contains("Chưa có loại món."));
    }

    /**
     * Kiểm tra trang món giữ đúng dữ liệu database, lượng bán thập phân và không thêm ảnh mẫu.
     */
    @Test
    void dishesRenderDatabaseValuesAndFractionalSoldQuantityWithoutSampleImages() throws Exception {
        // category: loại món dùng trong tình huống kiểm thử.
        LoaiMon category = new LoaiMon(21L, "Hải sản từ Oracle", null, "Hoạt động");
        // first: bản ghi thứ nhất của dữ liệu kiểm thử.
        MonAn first = new MonAn(81L, category, "Món đã bán", "Kg", new BigDecimal("120000"), "Đang phục vụ");
        // second: bản ghi thứ hai của dữ liệu kiểm thử.
        MonAn second = new MonAn(82L, category, "Món chưa bán", "Đĩa", new BigDecimal("80000"), "Tạm hết");
        // dishes: danh sách món ăn.
        var dishes = repository(MonAnRepository.class, Map.of("findAll", List.of(first, second)));
        // categories: danh sách loại món.
        var categories = repository(LoaiMonRepository.class, Map.of("findAll", List.of(category)));
        // details: repository chi tiết hóa đơn giả lập dùng cung cấp lượng bán cho trang món ăn.
        var details = repository(CTHoaDonRepository.class, Map.of("tongSoLuongDaBanTheoMon",
                List.<Object[]>of(new Object[]{81L, new BigDecimal("1.50")})));
        // html: nội dung HTML trả về để kiểm tra kết quả hiển thị.
        String html = render(pages(new MonAnController(new MonAnService(dishes, categories, details),
                new LoaiMonService(categories, dishes))), "/mon-an");

        assertTrue(html.contains("Món đã bán"));
        assertTrue(html.contains("Món chưa bán"));
        assertTrue(html.contains(">1.50</td>"));
        assertTrue(html.contains(">0</td>"));
        assertFalse(html.contains("images.unsplash.com"));
        assertFalse(html.contains("Chưa có món ăn."));
    }

    /**
     * Tạo MockMvc độc lập với các controller và Thymeleaf để kiểm tra HTML mà không cần database đang chạy.
     * @param controllers các controller được đưa vào MockMvc độc lập
     * @return kết quả kiểu {@code MockMvc} cho nơi gọi sử dụng
     */
    private MockMvc pages(Object... controllers) {
        // resolver: bộ tìm template theo thư mục templates và đuôi .html.
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML");
        // engine: Thymeleaf engine xử lý template với dữ liệu của Model.
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        // views: bộ phân giải view dùng Thymeleaf và UTF-8.
        ThymeleafViewResolver views = new ThymeleafViewResolver();
        views.setTemplateEngine(engine);
        views.setCharacterEncoding("UTF-8");
        return MockMvcBuilders.standaloneSetup(controllers).setViewResolvers(views).build();
    }

    /**
     * Gửi GET đến controller, kiểm tra HTTP 200 và lấy chuỗi HTML để so sánh.
     * @param mvc MockMvc dùng gửi request giả lập đến controller mà không mở cổng HTTP
     * @param path đường dẫn URL hoặc đường dẫn cần xử lý
     * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
     */
    private String render(MockMvc mvc, String path) throws Exception {
        // response: phản hồi HTTP sau khi gọi controller.
        var response = mvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse();
        assertTrue(response.getStatus() == 200, "Page must render successfully: " + path);
        return response.getContentAsString();
    }

    // Repository responses stay isolated from a running Oracle instance.
    /**
     * Tạo proxy repository trả dữ liệu theo Map responses; phương thức chưa khai báo sẽ ném lỗi để phát hiện lời gọi ngoài dự kiến.
     * @param type kiểu interface repository cần tạo đối tượng giả
     * @param responses Map tên phương thức -> dữ liệu trả về đã định sẵn cho repository giả
     * @return kết quả kiểu {@code T} cho nơi gọi sử dụng
     */
    private <T> T repository(Class<T> type, Map<String, Object> responses) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (/* proxy: đối tượng proxy đại diện cho repository giả. */ proxy, /* method: phương thức đang được gọi trên repository giả. */ method, /* args: tham số dòng lệnh truyền khi chạy chương trình. */ args) -> {
                    if (responses.containsKey(method.getName())) return responses.get(method.getName());
                    throw new UnsupportedOperationException(method.getName());
                }));
    }
}
