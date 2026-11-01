package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.service.DatBanService;
import com.QuanLyNHHS.service.KhachHangService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test cho KhachHangController
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@WebMvcTest(
    value = KhachHangController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@DisplayName("KhachHangController - Unit Tests")
class KhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KhachHangService khachHangService;

    @MockBean
    private DatBanService datBanService;

    // Dữ liệu mẫu
    private KhachHang taoKhachHang(Long id, String hoTen) {
        KhachHang kh = new KhachHang();
        kh.setMaKh(id);
        kh.setHoTen(hoTen);
        kh.setDiemTichLuy(0);
        return kh;
    }

    // ===================== GET /khach-hang =====================

    @Test
    @DisplayName("GET /khach-hang - Trả về view list với danh sách khách hàng")
    void list_TraVeViewList() throws Exception {
        when(khachHangService.findAll()).thenReturn(Arrays.asList(
                taoKhachHang(1L, "Nguyễn Văn A"),
                taoKhachHang(2L, "Trần Thị B")
        ));

        mockMvc.perform(get("/khach-hang"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/list"))
                .andExpect(model().attributeExists("danhSachKH"));

        verify(khachHangService).findAll();
    }

    @Test
    @DisplayName("GET /khach-hang?keyword=test - Gọi search() với keyword")
    void list_VoiKeyword_GoiSearch() throws Exception {
        when(khachHangService.search("Nguyễn")).thenReturn(
                Arrays.asList(taoKhachHang(1L, "Nguyễn Văn A"))
        );

        mockMvc.perform(get("/khach-hang").param("keyword", "Nguyễn"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/list"))
                .andExpect(model().attribute("keyword", "Nguyễn"));

        verify(khachHangService).search("Nguyễn");
        verify(khachHangService, never()).findAll();
    }

    // ===================== GET /khach-hang/them =====================

    @Test
    @DisplayName("GET /khach-hang/them - Trả về view form thêm mới")
    void showAddForm_TraVeFormThem() throws Exception {
        mockMvc.perform(get("/khach-hang/them"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/form"))
                .andExpect(model().attributeExists("khachHang"))
                .andExpect(model().attribute("isEdit", false));
    }

    // ===================== POST /khach-hang/them =====================

    @Test
    @DisplayName("POST /khach-hang/them - Thêm thành công → redirect /khach-hang")
    void processAdd_ThemThanhCong() throws Exception {
        KhachHang kh = taoKhachHang(null, "Lê Văn C");
        when(khachHangService.save(any(KhachHang.class))).thenReturn(kh);

        mockMvc.perform(post("/khach-hang/them")
                        .param("hoTen", "Lê Văn C")
                        .param("soDienThoai", "0901234567"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"));

        verify(khachHangService).save(any(KhachHang.class));
    }

    @Test
    @DisplayName("POST /khach-hang/them - Lỗi service → redirect với errorMessage")
    void processAdd_LoiService_RedirectVoiError() throws Exception {
        when(khachHangService.save(any())).thenThrow(new RuntimeException("Lỗi DB"));

        mockMvc.perform(post("/khach-hang/them")
                        .param("hoTen", "Lê Văn C"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== GET /khach-hang/sua/{maKh} =====================

    @Test
    @DisplayName("GET /khach-hang/sua/1 - Tìm thấy → view form sửa")
    void showEditForm_TimThay_TraVeFormSua() throws Exception {
        when(khachHangService.findById(1L)).thenReturn(Optional.of(taoKhachHang(1L, "Nguyễn Văn A")));

        mockMvc.perform(get("/khach-hang/sua/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/form"))
                .andExpect(model().attributeExists("khachHang"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @DisplayName("GET /khach-hang/sua/999 - Không tìm thấy → redirect với errorMessage")
    void showEditForm_KhongTimThay_Redirect() throws Exception {
        when(khachHangService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/khach-hang/sua/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== POST /khach-hang/sua/{maKh} =====================

    @Test
    @DisplayName("POST /khach-hang/sua/1 - Cập nhật thành công → redirect /khach-hang")
    void processEdit_CapNhatThanhCong() throws Exception {
        when(khachHangService.save(any(KhachHang.class))).thenReturn(taoKhachHang(1L, "Nguyễn Văn A Updated"));

        mockMvc.perform(post("/khach-hang/sua/1")
                        .param("hoTen", "Nguyễn Văn A Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"));

        verify(khachHangService).save(any(KhachHang.class));
    }

    // ===================== POST /khach-hang/xoa/{maKh} =====================

    @Test
    @DisplayName("POST /khach-hang/xoa/1 - Xóa thành công → redirect /khach-hang")
    void delete_XoaThanhCong() throws Exception {
        doNothing().when(khachHangService).delete(1L);

        mockMvc.perform(post("/khach-hang/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"));

        verify(khachHangService).delete(1L);
    }

    @Test
    @DisplayName("POST /khach-hang/xoa/1 - Xóa thất bại (có FK) → redirect với errorMessage")
    void delete_XoaThatBai() throws Exception {
        doThrow(new RuntimeException("Ràng buộc FK")).when(khachHangService).delete(1L);

        mockMvc.perform(post("/khach-hang/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== GET /khach-hang/{maKh}/lich-su-dat-ban =====================

    @Test
    @DisplayName("GET /khach-hang/1/lich-su-dat-ban - Tìm thấy → view lịch sử")
    void lichSuDatBan_TimThay_TraVeView() throws Exception {
        when(khachHangService.findById(1L)).thenReturn(Optional.of(taoKhachHang(1L, "Nguyễn Văn A")));
        when(datBanService.findByKhachHang(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/khach-hang/1/lich-su-dat-ban"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/lich-su-dat-ban"))
                .andExpect(model().attributeExists("khachHang"))
                .andExpect(model().attributeExists("danhSachDatBan"));
    }

    @Test
    @DisplayName("GET /khach-hang/999/lich-su-dat-ban - Không tìm thấy → redirect")
    void lichSuDatBan_KhongTimThay_Redirect() throws Exception {
        when(khachHangService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/khach-hang/999/lich-su-dat-ban"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
