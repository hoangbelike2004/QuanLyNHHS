package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Điều hướng URL kho đến danh sách phiếu nhập.
 */
@Controller
public class KhoController {
    /**
     * Chuyển URL /kho sang danh sách phiếu nhập.
     * @return tên template hoặc đường dẫn chuyển hướng: redirect:/phieu-nhap
     */
    @GetMapping("/kho")
    public String index() {
        return "redirect:/phieu-nhap";
    }
}
