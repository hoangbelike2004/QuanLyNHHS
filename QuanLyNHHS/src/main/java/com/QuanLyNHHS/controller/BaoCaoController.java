
package com.QuanLyNHHS.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.QuanLyNHHS.service.BaoCaoService;

@Controller
@RequestMapping("/bao-cao")
public class BaoCaoController {

    private final BaoCaoService baoCaoService;

    public BaoCaoController(BaoCaoService baoCaoService) {
        this.baoCaoService = baoCaoService;
    }

    @GetMapping
    public String hienThiBaoCao(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tuNgay,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate denNgay,

            Model model) {

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

        } catch (RuntimeException e) {
            model.addAttribute("loi", e.getMessage());
        }

        return "baocao/index";
    }
}
