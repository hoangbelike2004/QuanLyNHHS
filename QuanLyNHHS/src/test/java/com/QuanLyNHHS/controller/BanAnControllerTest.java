package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.service.BanAnService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test cho BanAnController
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@WebMvcTest(
    value = BanAnController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@DisplayName("BanAnController - Unit Tests")
class BanAnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BanAnService banAnService;

    // Dữ liệu mẫu
    private BanAn taoBanAn(Long id, String tenBan, String trangThai) {
        return new BanAn(id, tenBan, 4, "Tầng 1", trangThai);
    }

    // ===================== GET /ban-an =====================

    @Test
    @DisplayName("GET /ban-an - Trả về view list với danhSachBan")
    void list_TraVeViewList() throws Exception {
        when(banAnService.findAll()).thenReturn(Arrays.asList(
                taoBanAn(1L, "Bàn 01", "Trống"),
                taoBanAn(2L, "Bàn 02", "Đã đặt")
        ));

        mockMvc.perform(get("/ban-an"))
                .andExpect(status().isOk())
                .andExpect(view().name("ban-an/list"))
                .andExpect(model().attributeExists("danhSachBan"));

        verify(banAnService).findAll();
    }

    @Test
    @DisplayName("GET /ban-an?trangThai=Trống - Gọi findByTrangThai")
    void list_VoiTrangThai_GoiFilter() throws Exception {
        when(banAnService.findByTrangThai("Trống")).thenReturn(
                Arrays.asList(taoBanAn(1L, "Bàn 01", "Trống"))
        );

        mockMvc.perform(get("/ban-an").param("trangThai", "Trống"))
                .andExpect(status().isOk())
                .andExpect(view().name("ban-an/list"))
                .andExpect(model().attributeExists("trangThaiFilter"));

        verify(banAnService).findByTrangThai("Trống");
        verify(banAnService, never()).findAll();
    }

    // ===================== GET /ban-an/them =====================

    @Test
    @DisplayName("GET /ban-an/them - Trả về form thêm bàn mới")
    void showAddForm_TraVeFormThem() throws Exception {
        mockMvc.perform(get("/ban-an/them"))
                .andExpect(status().isOk())
                .andExpect(view().name("ban-an/form"))
                .andExpect(model().attributeExists("banAn"))
                .andExpect(model().attribute("isEdit", false));
    }

    // ===================== POST /ban-an/them =====================

    @Test
    @DisplayName("POST /ban-an/them - Thêm thành công → redirect /ban-an")
    void processAdd_ThemThanhCong() throws Exception {
        BanAn banMoi = taoBanAn(null, "Bàn 05", "Trống");
        when(banAnService.save(any(BanAn.class))).thenReturn(banMoi);

        mockMvc.perform(post("/ban-an/them")
                        .param("tenBan", "Bàn 05")
                        .param("soCho", "4")
                        .param("khuVuc", "Tầng 1")
                        .param("trangThai", "Trống"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"));

        verify(banAnService).save(any(BanAn.class));
    }

    @Test
    @DisplayName("POST /ban-an/them - Lỗi service → redirect với errorMessage")
    void processAdd_LoiService_RedirectVoiError() throws Exception {
        when(banAnService.save(any())).thenThrow(new RuntimeException("Lỗi DB"));

        mockMvc.perform(post("/ban-an/them")
                        .param("tenBan", "Bàn 05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== GET /ban-an/sua/{maBan} =====================

    @Test
    @DisplayName("GET /ban-an/sua/1 - Tìm thấy → view form sửa")
    void showEditForm_TimThay_TraVeFormSua() throws Exception {
        when(banAnService.findById(1L)).thenReturn(Optional.of(taoBanAn(1L, "Bàn 01", "Trống")));

        mockMvc.perform(get("/ban-an/sua/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ban-an/form"))
                .andExpect(model().attributeExists("banAn"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @DisplayName("GET /ban-an/sua/999 - Không tìm thấy → redirect với errorMessage")
    void showEditForm_KhongTimThay_Redirect() throws Exception {
        when(banAnService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ban-an/sua/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== POST /ban-an/sua/{maBan} =====================

    @Test
    @DisplayName("POST /ban-an/sua/1 - Cập nhật thành công → redirect /ban-an")
    void processEdit_CapNhatThanhCong() throws Exception {
        when(banAnService.save(any(BanAn.class))).thenReturn(taoBanAn(1L, "Bàn 01 Updated", "Trống"));

        mockMvc.perform(post("/ban-an/sua/1")
                        .param("tenBan", "Bàn 01 Updated")
                        .param("trangThai", "Trống"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"));

        verify(banAnService).save(any(BanAn.class));
    }

    @Test
    @DisplayName("POST /ban-an/sua/1 - Lỗi service → redirect với errorMessage")
    void processEdit_LoiService_RedirectVoiError() throws Exception {
        when(banAnService.save(any())).thenThrow(new RuntimeException("Lỗi DB"));

        mockMvc.perform(post("/ban-an/sua/1")
                        .param("tenBan", "Bàn 01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== POST /ban-an/xoa/{maBan} =====================

    @Test
    @DisplayName("POST /ban-an/xoa/1 - Xóa thành công → redirect /ban-an")
    void delete_XoaThanhCong() throws Exception {
        doNothing().when(banAnService).delete(1L);

        mockMvc.perform(post("/ban-an/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"));

        verify(banAnService).delete(1L);
    }

    @Test
    @DisplayName("POST /ban-an/xoa/1 - Lỗi xóa (FK) → redirect với errorMessage")
    void delete_XoaThatBai() throws Exception {
        doThrow(new RuntimeException("Ràng buộc FK")).when(banAnService).delete(1L);

        mockMvc.perform(post("/ban-an/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ban-an"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
