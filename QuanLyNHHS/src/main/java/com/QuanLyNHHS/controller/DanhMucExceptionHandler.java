package com.QuanLyNHHS.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Đổi lỗi không tìm thấy dữ liệu của ba module món/loại món/nhân viên thành trang HTTP 404.
 */
@ControllerAdvice(assignableTypes = {MonAnController.class, LoaiMonController.class, NhanVienController.class})
public class DanhMucExceptionHandler {
    /**
     * Đưa thông báo không tìm thấy bản ghi vào Model và trả trang lỗi với mã HTTP 404.
     * @param exception ngoại lệ chứa nguyên nhân và thông báo cần hiển thị
     * @param model nơi đặt các thuộc tính để Thymeleaf đọc và tạo HTML
     * @return tên template hoặc đường dẫn chuyển hướng: danhmuc/error
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(EntityNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "danhmuc/error";
    }
}
