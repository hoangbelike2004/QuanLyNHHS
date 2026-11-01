package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KhoController {
    @GetMapping({"/", "/kho"})
    public String index() { return "redirect:/phieu-nhap"; }
}
