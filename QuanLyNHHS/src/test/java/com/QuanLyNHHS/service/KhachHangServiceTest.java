package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.repository.KhachHangRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho KhachHangService
 * Module: Khách hàng & Đặt bàn — Hoàng Việt Đức
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KhachHangService Tests")
class KhachHangServiceTest {

    @Mock
    private KhachHangRepository khachHangRepository;

    @InjectMocks
    private KhachHangService khachHangService;

    private KhachHang kh1, kh2;

    @BeforeEach
    void setUp() {
        kh1 = new KhachHang(1L, "Nguyễn Văn A", "0901234567", "a@email.com", 100);
        kh2 = new KhachHang(2L, "Trần Thị B", "0987654321", "b@email.com", 50);
    }

    // ===== findAll =====

    @Test
    @DisplayName("findAll: trả về danh sách tất cả khách hàng")
    void findAll_returnsList() {
        when(khachHangRepository.findAll()).thenReturn(List.of(kh1, kh2));

        List<KhachHang> result = khachHangService.findAll();

        assertThat(result).hasSize(2).contains(kh1, kh2);
        verify(khachHangRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll: trả về danh sách rỗng nếu không có khách hàng")
    void findAll_returnsEmptyList_whenNoData() {
        when(khachHangRepository.findAll()).thenReturn(List.of());

        List<KhachHang> result = khachHangService.findAll();

        assertThat(result).isEmpty();
    }

    // ===== findById =====

    @Test
    @DisplayName("findById: trả về Optional chứa khách hàng khi tìm thấy")
    void findById_returnsKhachHang_whenExists() {
        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(kh1));

        Optional<KhachHang> result = khachHangService.findById(1L);

        assertThat(result).isPresent().contains(kh1);
    }

    @Test
    @DisplayName("findById: trả về Optional rỗng khi không tìm thấy")
    void findById_returnsEmpty_whenNotExists() {
        when(khachHangRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<KhachHang> result = khachHangService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ===== save =====

    @Test
    @DisplayName("save: lưu khách hàng bình thường")
    void save_persistsKhachHang() {
        when(khachHangRepository.save(kh1)).thenReturn(kh1);

        KhachHang result = khachHangService.save(kh1);

        assertThat(result).isEqualTo(kh1);
        verify(khachHangRepository).save(kh1);
    }

    @Test
    @DisplayName("save: tự động set diemTichLuy = 0 khi null")
    void save_setsDiemTichLuyToZero_whenNull() {
        KhachHang khNull = new KhachHang(null, "Test", "0900000000", null, null);
        khNull.setDiemTichLuy(null);
        when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(inv -> inv.getArgument(0));

        KhachHang result = khachHangService.save(khNull);

        assertThat(result.getDiemTichLuy()).isEqualTo(0);
    }

    @Test
    @DisplayName("save: giữ nguyên diemTichLuy khi đã có giá trị")
    void save_keepsDiemTichLuy_whenNotNull() {
        kh1.setDiemTichLuy(200);
        when(khachHangRepository.save(kh1)).thenReturn(kh1);

        KhachHang result = khachHangService.save(kh1);

        assertThat(result.getDiemTichLuy()).isEqualTo(200);
    }

    // ===== delete =====

    @Test
    @DisplayName("delete: gọi deleteById trên repository")
    void delete_callsRepository() {
        doNothing().when(khachHangRepository).deleteById(1L);

        khachHangService.delete(1L);

        verify(khachHangRepository, times(1)).deleteById(1L);
    }

    // ===== search =====

    @Test
    @DisplayName("search: trả về tất cả khi keyword null")
    void search_returnsAll_whenKeywordNull() {
        when(khachHangRepository.findAll()).thenReturn(List.of(kh1, kh2));

        List<KhachHang> result = khachHangService.search(null);

        assertThat(result).hasSize(2);
        verify(khachHangRepository).findAll();
        verify(khachHangRepository, never()).findByHoTenContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("search: trả về tất cả khi keyword rỗng")
    void search_returnsAll_whenKeywordEmpty() {
        when(khachHangRepository.findAll()).thenReturn(List.of(kh1, kh2));

        List<KhachHang> result = khachHangService.search("   ");

        assertThat(result).hasSize(2);
        verify(khachHangRepository).findAll();
    }

    @Test
    @DisplayName("search: gọi tìm kiếm theo tên khi có keyword")
    void search_callsFindByHoTen_whenKeywordProvided() {
        when(khachHangRepository.findByHoTenContainingIgnoreCase("Nguyễn"))
                .thenReturn(List.of(kh1));

        List<KhachHang> result = khachHangService.search("Nguyễn");

        assertThat(result).hasSize(1).contains(kh1);
        verify(khachHangRepository).findByHoTenContainingIgnoreCase("Nguyễn");
        verify(khachHangRepository, never()).findAll();
    }

    @Test
    @DisplayName("search: trim keyword trước khi tìm kiếm")
    void search_trimsKeyword_beforeSearch() {
        when(khachHangRepository.findByHoTenContainingIgnoreCase("Trần"))
                .thenReturn(List.of(kh2));

        List<KhachHang> result = khachHangService.search("  Trần  ");

        assertThat(result).hasSize(1).contains(kh2);
        verify(khachHangRepository).findByHoTenContainingIgnoreCase("Trần");
    }

    @Test
    @DisplayName("search: tìm kiếm không phân biệt hoa thường")
    void search_isCaseInsensitive() {
        when(khachHangRepository.findByHoTenContainingIgnoreCase("nguyễn"))
                .thenReturn(List.of(kh1));

        List<KhachHang> result = khachHangService.search("nguyễn");

        assertThat(result).contains(kh1);
    }

    // ===== existsById =====

    @Test
    @DisplayName("existsById: trả về true khi tồn tại")
    void existsById_returnsTrue_whenExists() {
        when(khachHangRepository.existsById(1L)).thenReturn(true);

        assertThat(khachHangService.existsById(1L)).isTrue();
    }

    @Test
    @DisplayName("existsById: trả về false khi không tồn tại")
    void existsById_returnsFalse_whenNotExists() {
        when(khachHangRepository.existsById(99L)).thenReturn(false);

        assertThat(khachHangService.existsById(99L)).isFalse();
    }

    // ===== Model integrity =====

    @Test
    @DisplayName("KhachHang model: diemTichLuy mặc định là 0")
    void khachHang_defaultDiemTichLuy_isZero() {
        KhachHang kh = new KhachHang();
        assertThat(kh.getDiemTichLuy()).isEqualTo(0);
    }

    @Test
    @DisplayName("KhachHang model: setDiemTichLuy(null) giữ giá trị 0")
    void khachHang_setDiemTichLuyNull_keepsZero() {
        KhachHang kh = new KhachHang();
        kh.setDiemTichLuy(null);
        assertThat(kh.getDiemTichLuy()).isEqualTo(0);
    }
}
