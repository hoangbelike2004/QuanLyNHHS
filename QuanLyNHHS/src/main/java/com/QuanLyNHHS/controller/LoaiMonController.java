package com.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loai-mon")
public class LoaiMonController {
    @GetMapping
    public String index() {
        return "redirect:/mon-an#loai-mon";
    }
}
