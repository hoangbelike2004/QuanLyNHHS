package com.QuanLyNHHS.controller;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Xử lý tập trung lỗi form, bản ghi mất và xung đột dữ liệu cho các controller kho.
 */
@ControllerAdvice(
        assignableTypes = {
            NhaCungCapController.class,
            NguyenLieuController.class,
            PhieuNhapController.class
        })
public class KhoExceptionHandler {
    /**
     * Hiển thị trang lỗi HTTP 400 khi cấu trúc dữ liệu form kho không hợp lệ hoặc vượt giới hạn số dòng.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/error
     */
    @ExceptionHandler(InvalidPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidForm(Model model) {
        model.addAttribute(
                "message", "Dữ liệu phiếu nhập không hợp lệ. Mỗi phiếu tối đa 100 dòng.");
        return "kho/error";
    }

    /**
     * Đưa thông báo không tìm thấy bản ghi vào Model và trả trang lỗi với mã HTTP 404.
     * @param e ngoại lệ bị bắt để lấy thông báo lỗi
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/error
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "kho/error";
    }

    /**
     * Hiển thị trang lỗi HTTP 409 khi vi phạm ràng buộc dữ liệu hoặc có xung đột cập nhật.
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: kho/error
     */
    @ExceptionHandler({DataIntegrityViolationException.class, ConcurrencyFailureException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(Model model) {
        model.addAttribute(
                "message",
                "Dữ liệu đang được sử dụng hoặc vừa thay đổi. Hãy tải lại trang và thử lại.");
        return "kho/error";
    }
}
