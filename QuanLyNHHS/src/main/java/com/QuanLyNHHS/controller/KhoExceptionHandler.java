package com.QuanLyNHHS.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(assignableTypes = {NhaCungCapController.class, NguyenLieuController.class, PhieuNhapController.class})
public class KhoExceptionHandler {
    @ExceptionHandler(InvalidPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidForm(Model model) {
        model.addAttribute("message", "Dữ liệu phiếu nhập không hợp lệ. Mỗi phiếu tối đa 100 dòng.");
        return "kho/error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage()); return "kho/error";
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConcurrencyFailureException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(Model model) {
        model.addAttribute("message", "Dữ liệu đang được sử dụng hoặc vừa thay đổi. Hãy tải lại trang và thử lại.");
        return "kho/error";
    }
}
