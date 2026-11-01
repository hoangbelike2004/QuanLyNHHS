package com.QuanLyNHHS.controller;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.service.BanAnService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test cho DatBanController
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@WebMvcTest(
    value = DatBanController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@DisplayName("DatBanController - Unit Tests")
class DatBanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatBanService datBanService;

    @MockBean
    private BanAnService banAnService;

    @MockBean
    private KhachHangService khachHangService;

    // Dữ liệu mẫu
    private BanAn taoBanAn(Long id, String trangThai) {
        return new BanAn(id, "Bàn 0" + id, 4, "Tầng 1", trangThai);
    }

    private KhachHang taoKhachHang(Long id) {
        KhachHang kh = new KhachHang();
        kh.setMaKh(id);
        kh.setHoTen("Khách " + id);
        return kh;
    }

    private DatBan taoDatBan(Long id, String trangThai) {
        BanAn ban = taoBanAn(1L, "Đã đặt");
        KhachHang kh = taoKhachHang(1L);
        return new DatBan(id, kh, ban, LocalDate.now(), 2, new BigDecimal("200000"), trangThai);
    }

    // ===================== GET /dat-ban =====================

    @Test
    @DisplayName("GET /dat-ban - Trả về view list với danhSachDatBan")
    void list_TraVeViewList() throws Exception {
        when(datBanService.findAll()).thenReturn(Arrays.asList(taoDatBan(1L, "CHO_XAC_NHAN")));

        mockMvc.perform(get("/dat-ban"))
                .andExpect(status().isOk())
                .andExpect(view().name("dat-ban/list"))
                .andExpect(model().attributeExists("danhSachDatBan"));

        verify(datBanService).findAll();
    }

    @Test
    @DisplayName("GET /dat-ban?trangThai=DA_HUY - Gọi findByTrangThai")
    void list_VoiTrangThai_GoiFilter() throws Exception {
        when(datBanService.findByTrangThai("DA_HUY")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dat-ban").param("trangThai", "DA_HUY"))
                .andExpect(status().isOk())
                .andExpect(view().name("dat-ban/list"))
                .andExpect(model().attribute("trangThaiFilter", "DA_HUY"));

        verify(datBanService).findByTrangThai("DA_HUY");
    }

    // ===================== GET /dat-ban/them =====================

    @Test
    @DisplayName("GET /dat-ban/them - Gọi banAnService.findBanTrong() (chỉ bàn trống)")
    void showAddForm_ChiHienBanTrong() throws Exception {
        when(banAnService.findBanTrong()).thenReturn(Arrays.asList(taoBanAn(1L, "Trống")));
        when(khachHangService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dat-ban/them"))
                .andExpect(status().isOk())
                .andExpect(view().name("dat-ban/form"))
                .andExpect(model().attributeExists("danhSachBan"))
                .andExpect(model().attribute("isEdit", false));

        // Phải gọi findBanTrong, KHÔNG phải findAll
        verify(banAnService).findBanTrong();
        verify(banAnService, never()).findAll();
    }

    // ===================== POST /dat-ban/them =====================

    @Test
    @DisplayName("POST /dat-ban/them - Bàn trống → đặt thành công, redirect /dat-ban")
    void processAdd_BanTrong_DatThanhCong() throws Exception {
        BanAn banTrong = taoBanAn(1L, "Trống");
        when(banAnService.findById(1L)).thenReturn(Optional.of(banTrong));
        when(khachHangService.findById(1L)).thenReturn(Optional.of(taoKhachHang(1L)));
        when(datBanService.save(any(DatBan.class))).thenReturn(taoDatBan(1L, "CHO_XAC_NHAN"));

        mockMvc.perform(post("/dat-ban/them")
                        .param("maKh", "1")
                        .param("maBan", "1")
                        .param("soNguoi", "2")
                        .param("ngayDat", LocalDate.now().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(datBanService).save(any(DatBan.class));
        verify(banAnService).capNhatTrangThai(1L, "Đã đặt");
    }

    @Test
    @DisplayName("POST /dat-ban/them - Bàn đang được dùng → redirect /dat-ban/them với errorMessage")
    void processAdd_BanDangDung_RedirectVoiError() throws Exception {
        BanAn banDangDung = taoBanAn(2L, "Đã đặt");
        when(banAnService.findById(2L)).thenReturn(Optional.of(banDangDung));

        mockMvc.perform(post("/dat-ban/them")
                        .param("maKh", "1")
                        .param("maBan", "2")
                        .param("soNguoi", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban/them"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Không được save
        verify(datBanService, never()).save(any());
        verify(banAnService, never()).capNhatTrangThai(anyLong(), any());
    }

    @Test
    @DisplayName("POST /dat-ban/them - Bàn không tồn tại → redirect /dat-ban với errorMessage")
    void processAdd_BanKhongTonTai_RedirectVoiError() throws Exception {
        when(banAnService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/dat-ban/them")
                        .param("maKh", "1")
                        .param("maBan", "99")
                        .param("soNguoi", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(datBanService, never()).save(any());
    }

    // ===================== POST /dat-ban/xac-nhan/{maDat} =====================

    @Test
    @DisplayName("POST /dat-ban/xac-nhan/1 - Gọi datBanService.xacNhan(1)")
    void xacNhan_GoiService() throws Exception {
        doNothing().when(datBanService).xacNhan(1L);

        mockMvc.perform(post("/dat-ban/xac-nhan/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(datBanService).xacNhan(1L);
    }

    @Test
    @DisplayName("POST /dat-ban/xac-nhan/1 - Lỗi service → redirect với errorMessage")
    void xacNhan_LoiService() throws Exception {
        doThrow(new RuntimeException("Lỗi")).when(datBanService).xacNhan(1L);

        mockMvc.perform(post("/dat-ban/xac-nhan/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== POST /dat-ban/huy/{maDat} =====================

    @Test
    @DisplayName("POST /dat-ban/huy/1 - Gọi datBanService.huyDatBan(1)")
    void huy_GoiService() throws Exception {
        doNothing().when(datBanService).huyDatBan(1L);

        mockMvc.perform(post("/dat-ban/huy/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(datBanService).huyDatBan(1L);
    }

    @Test
    @DisplayName("POST /dat-ban/huy/1 - Lỗi service → redirect với errorMessage")
    void huy_LoiService() throws Exception {
        doThrow(new RuntimeException("Lỗi")).when(datBanService).huyDatBan(1L);

        mockMvc.perform(post("/dat-ban/huy/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ===================== POST /dat-ban/hoan-thanh/{maDat} =====================

    @Test
    @DisplayName("POST /dat-ban/hoan-thanh/1 - Gọi datBanService.hoanThanh(1)")
    void hoanThanh_GoiService() throws Exception {
        doNothing().when(datBanService).hoanThanh(1L);

        mockMvc.perform(post("/dat-ban/hoan-thanh/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(datBanService).hoanThanh(1L);
    }

    // ===================== POST /dat-ban/xoa/{maDat} =====================

    @Test
    @DisplayName("POST /dat-ban/xoa/1 - Đặt bàn DA_HUY → xóa thành công")
    void delete_DatBanDaHuy_XoaThanhCong() throws Exception {
        DatBan datBanDaHuy = taoDatBan(1L, "DA_HUY");
        when(datBanService.findById(1L)).thenReturn(Optional.of(datBanDaHuy));
        doNothing().when(datBanService).delete(1L);

        mockMvc.perform(post("/dat-ban/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(datBanService).delete(1L);
        // DA_HUY → không cần reset bàn
        verify(banAnService, never()).capNhatTrangThai(anyLong(), eq("Trống"));
    }

    @Test
    @DisplayName("POST /dat-ban/xoa/1 - Đặt bàn CHO_XAC_NHAN → reset bàn về Trống khi xóa")
    void delete_DatBanChoXacNhan_ResetBan() throws Exception {
        DatBan datBanChoXacNhan = taoDatBan(1L, "CHO_XAC_NHAN");
        when(datBanService.findById(1L)).thenReturn(Optional.of(datBanChoXacNhan));
        doNothing().when(datBanService).delete(1L);

        mockMvc.perform(post("/dat-ban/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"));

        verify(datBanService).delete(1L);
        // CHO_XAC_NHAN → phải reset bàn về Trống
        verify(banAnService).capNhatTrangThai(1L, "Trống");
    }

    @Test
    @DisplayName("POST /dat-ban/xoa/1 - Lỗi xóa → redirect với errorMessage")
    void delete_LoiXoa_RedirectVoiError() throws Exception {
        when(datBanService.findById(1L)).thenReturn(Optional.of(taoDatBan(1L, "DA_HUY")));
        doThrow(new RuntimeException("Lỗi DB")).when(datBanService).delete(1L);

        mockMvc.perform(post("/dat-ban/xoa/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dat-ban"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
