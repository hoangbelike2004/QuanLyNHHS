
package com.QuanLyNHHS.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.QuanLyNHHS.service.BaoCaoService;

/**
 * Nhận yêu cầu HTTP của module báo cáo, gọi xử lý và đưa dữ liệu sang Thymeleaf hoặc chuyển hướng.
 */
@Controller
@RequestMapping("/bao-cao")
public class BaoCaoController {

    // baoCaoService: thành phần BaoCaoService xử lý nghiệp vụ trước khi đọc/ghi database.
    private final BaoCaoService baoCaoService;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param baoCaoService thành phần BaoCaoService xử lý nghiệp vụ trước khi đọc/ghi database
     */
    public BaoCaoController(BaoCaoService baoCaoService) {
        this.baoCaoService = baoCaoService;
    }

    /**
     * Mặc định chọn từ đầu tháng đến hôm nay nếu thiếu ngày; nạp doanh thu và các thống kê vào Model, lỗi được hiển thị trên trang.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: baocao/index
     */
    @GetMapping
    public String hienThiBaoCao(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tuNgay,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate denNgay,

            Model model) {

        // homNay: ngày hiện tại theo đồng hồ của máy chạy ứng dụng.
        LocalDate homNay = LocalDate.now();

        if (tuNgay == null) {
            tuNgay = homNay.withDayOfMonth(1);
        }

        if (denNgay == null) {
            denNgay = homNay;
        }

        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);

        try {
            model.addAttribute(
                    "doanhThu",
                    baoCaoService.doanhThuTheoNgay(tuNgay, denNgay));

            model.addAttribute(
                    "doanhThuHomNay",
                    baoCaoService.doanhThuHomNay());

            model.addAttribute(
                    "doanhThuThangNay",
                    baoCaoService.doanhThuThangNay());

            model.addAttribute(
                    "soHoaDonDaThanhToan",
                    baoCaoService.soHoaDonDaThanhToan());

            model.addAttribute(
                    "monBanChay",
                    baoCaoService.thongKeMonBanChay());

            model.addAttribute(
                    "tongTienTheoHoaDon",
                    baoCaoService.thongKeTongTienTheoHoaDon());

        } catch (/* e: ngoại lệ bị bắt để lấy thông báo lỗi. */ RuntimeException e) {
            model.addAttribute("loi", e.getMessage());
        }

        return "baocao/index";
    }
}
