package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.DatBanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test cho DatBanService
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DatBanService - Unit Tests")
class DatBanServiceTest {

    @Mock
    private DatBanRepository datBanRepository;

    @Mock
    private BanAnRepository banAnRepository;

    @InjectMocks
    private DatBanService datBanService;

    // Dữ liệu dùng chung
    private BanAn banAnMau;
    private KhachHang khachHangMau;
    private DatBan datBanMau;

    @BeforeEach
    void setUp() {
        banAnMau = new BanAn(1L, "Bàn 01", 4, "Tầng 1", "Trống");
        khachHangMau = new KhachHang();
        khachHangMau.setMaKh(10L);
        khachHangMau.setHoTen("Nguyễn Văn A");

        datBanMau = new DatBan(
                1L, khachHangMau, banAnMau,
                LocalDate.now(), 2, new BigDecimal("200000"), "CHO_XAC_NHAN"
        );
    }

    // ===================== findAll =====================

    @Test
    @DisplayName("findAll - Trả về danh sách tất cả đặt bàn")
    void findAll_TraVeDanhSach() {
        List<DatBan> danhSach = Arrays.asList(datBanMau, new DatBan());
        when(datBanRepository.findAll()).thenReturn(danhSach);

        List<DatBan> result = datBanService.findAll();

        assertEquals(2, result.size());
        verify(datBanRepository).findAll();
    }

    // ===================== findById =====================

    @Test
    @DisplayName("findById - Tìm thấy đặt bàn theo ID")
    void findById_TimThay() {
        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));

        Optional<DatBan> result = datBanService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getMaDat());
    }

    @Test
    @DisplayName("findById - Không tìm thấy trả về Optional.empty")
    void findById_KhongTimThay() {
        when(datBanRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DatBan> result = datBanService.findById(99L);

        assertFalse(result.isPresent());
    }

    // ===================== save =====================

    @Test
    @DisplayName("save - Lưu đặt bàn thành công")
    void save_LuuThanhCong() {
        when(datBanRepository.save(datBanMau)).thenReturn(datBanMau);

        DatBan result = datBanService.save(datBanMau);

        assertNotNull(result);
        verify(datBanRepository).save(datBanMau);
    }

    // ===================== delete =====================

    @Test
    @DisplayName("delete - Xóa đặt bàn theo ID")
    void delete_XoaTheoId() {
        datBanService.delete(1L);

        verify(datBanRepository).deleteById(1L);
    }

    // ===================== findByTrangThai =====================

    @Test
    @DisplayName("findByTrangThai - Null trả về findAll")
    void findByTrangThai_NullTraVeFindAll() {
        List<DatBan> danhSach = Arrays.asList(datBanMau);
        when(datBanRepository.findAll()).thenReturn(danhSach);

        List<DatBan> result = datBanService.findByTrangThai(null);

        verify(datBanRepository).findAll();
        verify(datBanRepository, never()).findByTrangThai(any());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findByTrangThai - Chuỗi rỗng trả về findAll")
    void findByTrangThai_ChuoiRongTraVeFindAll() {
        when(datBanRepository.findAll()).thenReturn(Collections.emptyList());

        datBanService.findByTrangThai("   ");

        verify(datBanRepository).findAll();
        verify(datBanRepository, never()).findByTrangThai(any());
    }

    @Test
    @DisplayName("findByTrangThai - Có trạng thái → gọi repository với trim")
    void findByTrangThai_CoTrangThai() {
        when(datBanRepository.findByTrangThai("DA_HUY")).thenReturn(Collections.emptyList());

        datBanService.findByTrangThai("  DA_HUY  ");

        verify(datBanRepository).findByTrangThai("DA_HUY");
    }

    // ===================== findByKhachHang =====================

    @Test
    @DisplayName("findByKhachHang - Gọi repository với maKh đúng")
    void findByKhachHang_GoiRepositoryDung() {
        when(datBanRepository.findByKhachHang_MaKh(10L)).thenReturn(Arrays.asList(datBanMau));

        List<DatBan> result = datBanService.findByKhachHang(10L);

        verify(datBanRepository).findByKhachHang_MaKh(10L);
        assertEquals(1, result.size());
    }

    // ===================== findByBanAn =====================

    @Test
    @DisplayName("findByBanAn - Gọi repository với maBan đúng")
    void findByBanAn_GoiRepositoryDung() {
        when(datBanRepository.findByBanAn_MaBan(1L)).thenReturn(Arrays.asList(datBanMau));

        List<DatBan> result = datBanService.findByBanAn(1L);

        verify(datBanRepository).findByBanAn_MaBan(1L);
        assertEquals(1, result.size());
    }

    // ===================== xacNhan =====================

    @Test
    @DisplayName("xacNhan - Đổi trạng thái sang DA_XAC_NHAN và BanAn sang Đã đặt")
    void xacNhan_DoiTrangThaiThanhCong() {
        banAnMau.setTrangThai("Trống");
        datBanMau.setTrangThai("CHO_XAC_NHAN");
        datBanMau.setBanAn(banAnMau);

        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(banAnMau));

        datBanService.xacNhan(1L);

        // Kiểm tra trạng thái đặt bàn được cập nhật
        ArgumentCaptor<DatBan> datBanCaptor = ArgumentCaptor.forClass(DatBan.class);
        verify(datBanRepository).save(datBanCaptor.capture());
        assertEquals("DA_XAC_NHAN", datBanCaptor.getValue().getTrangThai());

        // Kiểm tra trạng thái bàn ăn được cập nhật
        ArgumentCaptor<BanAn> banAnCaptor = ArgumentCaptor.forClass(BanAn.class);
        verify(banAnRepository).save(banAnCaptor.capture());
        assertEquals("Đã đặt", banAnCaptor.getValue().getTrangThai());
    }

    @Test
    @DisplayName("xacNhan - ID không tồn tại → không làm gì")
    void xacNhan_IdKhongTonTai() {
        when(datBanRepository.findById(99L)).thenReturn(Optional.empty());

        datBanService.xacNhan(99L);

        verify(datBanRepository, never()).save(any());
        verify(banAnRepository, never()).save(any());
    }

    @Test
    @DisplayName("xacNhan - BanAn là null → không gọi banAnRepository")
    void xacNhan_BanAnNull() {
        datBanMau.setBanAn(null);
        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));

        datBanService.xacNhan(1L);

        verify(datBanRepository).save(datBanMau);
        verify(banAnRepository, never()).save(any());
    }

    // ===================== huyDatBan =====================

    @Test
    @DisplayName("huyDatBan - Đổi trạng thái sang DA_HUY và BanAn sang Trống")
    void huyDatBan_DoiTrangThaiThanhCong() {
        banAnMau.setTrangThai("Đã đặt");
        datBanMau.setTrangThai("CHO_XAC_NHAN");

        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(banAnMau));

        datBanService.huyDatBan(1L);

        ArgumentCaptor<DatBan> datBanCaptor = ArgumentCaptor.forClass(DatBan.class);
        verify(datBanRepository).save(datBanCaptor.capture());
        assertEquals("DA_HUY", datBanCaptor.getValue().getTrangThai());

        ArgumentCaptor<BanAn> banAnCaptor = ArgumentCaptor.forClass(BanAn.class);
        verify(banAnRepository).save(banAnCaptor.capture());
        assertEquals("Trống", banAnCaptor.getValue().getTrangThai());
    }

    @Test
    @DisplayName("huyDatBan - ID không tồn tại → không làm gì")
    void huyDatBan_IdKhongTonTai() {
        when(datBanRepository.findById(99L)).thenReturn(Optional.empty());

        datBanService.huyDatBan(99L);

        verify(datBanRepository, never()).save(any());
        verify(banAnRepository, never()).save(any());
    }

    @Test
    @DisplayName("huyDatBan - BanAn là null → không gọi banAnRepository")
    void huyDatBan_BanAnNull() {
        datBanMau.setBanAn(null);
        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));

        datBanService.huyDatBan(1L);

        verify(datBanRepository).save(datBanMau);
        verify(banAnRepository, never()).save(any());
    }

    // ===================== hoanThanh =====================

    @Test
    @DisplayName("hoanThanh - Đổi trạng thái sang HOAN_THANH và BanAn sang Trống")
    void hoanThanh_DoiTrangThaiThanhCong() {
        banAnMau.setTrangThai("Đã đặt");
        datBanMau.setTrangThai("DA_XAC_NHAN");

        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(banAnMau));

        datBanService.hoanThanh(1L);

        ArgumentCaptor<DatBan> datBanCaptor = ArgumentCaptor.forClass(DatBan.class);
        verify(datBanRepository).save(datBanCaptor.capture());
        assertEquals("HOAN_THANH", datBanCaptor.getValue().getTrangThai());

        ArgumentCaptor<BanAn> banAnCaptor = ArgumentCaptor.forClass(BanAn.class);
        verify(banAnRepository).save(banAnCaptor.capture());
        assertEquals("Trống", banAnCaptor.getValue().getTrangThai());
    }

    @Test
    @DisplayName("hoanThanh - ID không tồn tại → không làm gì")
    void hoanThanh_IdKhongTonTai() {
        when(datBanRepository.findById(99L)).thenReturn(Optional.empty());

        datBanService.hoanThanh(99L);

        verify(datBanRepository, never()).save(any());
        verify(banAnRepository, never()).save(any());
    }

    @Test
    @DisplayName("hoanThanh - BanAn là null → không gọi banAnRepository")
    void hoanThanh_BanAnNull() {
        datBanMau.setBanAn(null);
        when(datBanRepository.findById(1L)).thenReturn(Optional.of(datBanMau));

        datBanService.hoanThanh(1L);

        verify(datBanRepository).save(datBanMau);
        verify(banAnRepository, never()).save(any());
    }
}
