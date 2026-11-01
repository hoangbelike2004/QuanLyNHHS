package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.repository.BanAnRepository;
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
 * Unit Test cho BanAnService
 * Module: Khách hàng & Đặt bàn — Hoàng Việt Đức
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BanAnService Tests")
class BanAnServiceTest {

    @Mock
    private BanAnRepository banAnRepository;

    @InjectMocks
    private BanAnService banAnService;

    private BanAn banTrong, banDaDat, banDangDung;

    @BeforeEach
    void setUp() {
        banTrong   = new BanAn(1L, "Bàn 01", 4, "Tầng 1", "Trống");
        banDaDat   = new BanAn(2L, "Bàn 02", 6, "Tầng 2", "Đã đặt");
        banDangDung = new BanAn(3L, "Bàn VIP", 8, "Phòng VIP", "Đang dùng");
    }

    // ===== findAll =====

    @Test
    @DisplayName("findAll: trả về tất cả bàn ăn")
    void findAll_returnsAll() {
        when(banAnRepository.findAll()).thenReturn(List.of(banTrong, banDaDat, banDangDung));

        List<BanAn> result = banAnService.findAll();

        assertThat(result).hasSize(3);
        verify(banAnRepository).findAll();
    }

    // ===== findBanTrong =====

    @Test
    @DisplayName("findBanTrong: chỉ trả về bàn có trạng thái 'Trống'")
    void findBanTrong_callsFindByTrangThaiTrong() {
        when(banAnRepository.findByTrangThai("Trống")).thenReturn(List.of(banTrong));

        List<BanAn> result = banAnService.findBanTrong();

        assertThat(result).hasSize(1).contains(banTrong);
        verify(banAnRepository).findByTrangThai("Trống");
        verify(banAnRepository, never()).findAll();
    }

    @Test
    @DisplayName("findBanTrong: trả về danh sách rỗng khi không có bàn trống")
    void findBanTrong_returnsEmpty_whenNoAvailableTable() {
        when(banAnRepository.findByTrangThai("Trống")).thenReturn(List.of());

        List<BanAn> result = banAnService.findBanTrong();

        assertThat(result).isEmpty();
    }

    // ===== findByTrangThai =====

    @Test
    @DisplayName("findByTrangThai: trả về tất cả khi trangThai null")
    void findByTrangThai_returnsAll_whenNull() {
        when(banAnRepository.findAll()).thenReturn(List.of(banTrong, banDaDat));

        List<BanAn> result = banAnService.findByTrangThai(null);

        assertThat(result).hasSize(2);
        verify(banAnRepository).findAll();
        verify(banAnRepository, never()).findByTrangThai(any());
    }

    @Test
    @DisplayName("findByTrangThai: trả về tất cả khi trangThai rỗng")
    void findByTrangThai_returnsAll_whenEmpty() {
        when(banAnRepository.findAll()).thenReturn(List.of(banTrong, banDaDat));

        List<BanAn> result = banAnService.findByTrangThai("  ");

        assertThat(result).hasSize(2);
        verify(banAnRepository).findAll();
    }

    @Test
    @DisplayName("findByTrangThai: lọc đúng trạng thái khi được cung cấp")
    void findByTrangThai_filtersCorrectly() {
        when(banAnRepository.findByTrangThai("Đã đặt")).thenReturn(List.of(banDaDat));

        List<BanAn> result = banAnService.findByTrangThai("Đã đặt");

        assertThat(result).hasSize(1).contains(banDaDat);
        verify(banAnRepository).findByTrangThai("Đã đặt");
    }

    @Test
    @DisplayName("findByTrangThai: trim giá trị trước khi lọc")
    void findByTrangThai_trimsValue() {
        when(banAnRepository.findByTrangThai("Trống")).thenReturn(List.of(banTrong));

        List<BanAn> result = banAnService.findByTrangThai(" Trống ");

        assertThat(result).contains(banTrong);
        verify(banAnRepository).findByTrangThai("Trống");
    }

    // ===== findByKhuVuc =====

    @Test
    @DisplayName("findByKhuVuc: trả về tất cả khi khuVuc null")
    void findByKhuVuc_returnsAll_whenNull() {
        when(banAnRepository.findAll()).thenReturn(List.of(banTrong, banDaDat, banDangDung));

        List<BanAn> result = banAnService.findByKhuVuc(null);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("findByKhuVuc: lọc đúng khu vực")
    void findByKhuVuc_filtersCorrectly() {
        when(banAnRepository.findByKhuVuc("Tầng 1")).thenReturn(List.of(banTrong));

        List<BanAn> result = banAnService.findByKhuVuc("Tầng 1");

        assertThat(result).hasSize(1).contains(banTrong);
    }

    // ===== capNhatTrangThai =====

    @Test
    @DisplayName("capNhatTrangThai: cập nhật trạng thái khi tìm thấy bàn")
    void capNhatTrangThai_updatesBan_whenFound() {
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(banTrong));
        when(banAnRepository.save(any(BanAn.class))).thenAnswer(inv -> inv.getArgument(0));

        banAnService.capNhatTrangThai(1L, "Đã đặt");

        assertThat(banTrong.getTrangThai()).isEqualTo("Đã đặt");
        verify(banAnRepository).save(banTrong);
    }

    @Test
    @DisplayName("capNhatTrangThai: không làm gì khi không tìm thấy bàn")
    void capNhatTrangThai_doesNothing_whenNotFound() {
        when(banAnRepository.findById(99L)).thenReturn(Optional.empty());

        banAnService.capNhatTrangThai(99L, "Trống");

        verify(banAnRepository, never()).save(any());
    }

    @Test
    @DisplayName("capNhatTrangThai: có thể đặt trạng thái bất kỳ")
    void capNhatTrangThai_setsAnyStatus() {
        when(banAnRepository.findById(2L)).thenReturn(Optional.of(banDaDat));
        when(banAnRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        banAnService.capNhatTrangThai(2L, "Trống");

        assertThat(banDaDat.getTrangThai()).isEqualTo("Trống");
    }

    // ===== findById =====

    @Test
    @DisplayName("findById: trả về Optional chứa bàn ăn khi tìm thấy")
    void findById_returnsBanAn_whenExists() {
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(banTrong));

        Optional<BanAn> result = banAnService.findById(1L);

        assertThat(result).isPresent().contains(banTrong);
    }

    @Test
    @DisplayName("findById: trả về Optional rỗng khi không tìm thấy")
    void findById_returnsEmpty_whenNotExists() {
        when(banAnRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<BanAn> result = banAnService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ===== save / delete =====

    @Test
    @DisplayName("save: gọi repository.save và trả về kết quả")
    void save_callsRepositoryAndReturns() {
        when(banAnRepository.save(banTrong)).thenReturn(banTrong);

        BanAn result = banAnService.save(banTrong);

        assertThat(result).isEqualTo(banTrong);
        verify(banAnRepository).save(banTrong);
    }

    @Test
    @DisplayName("delete: gọi deleteById trên repository")
    void delete_callsRepository() {
        doNothing().when(banAnRepository).deleteById(1L);

        banAnService.delete(1L);

        verify(banAnRepository).deleteById(1L);
    }

    // ===== BanAn model integrity =====

    @Test
    @DisplayName("BanAn model: các getter/setter hoạt động đúng")
    void banAnModel_gettersSettersWork() {
        BanAn ban = new BanAn();
        ban.setMaBan(10L);
        ban.setTenBan("Bàn Test");
        ban.setSoCho(6);
        ban.setKhuVuc("Sân vườn");
        ban.setTrangThai("Trống");

        assertThat(ban.getMaBan()).isEqualTo(10L);
        assertThat(ban.getTenBan()).isEqualTo("Bàn Test");
        assertThat(ban.getSoCho()).isEqualTo(6);
        assertThat(ban.getKhuVuc()).isEqualTo("Sân vườn");
        assertThat(ban.getTrangThai()).isEqualTo("Trống");
    }
}
