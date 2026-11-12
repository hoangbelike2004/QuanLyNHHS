package com.QuanLyNHHS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.model.KhachHang;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.model.LoaiMon;
import com.QuanLyNHHS.controller.NhanVienController;
import com.QuanLyNHHS.controller.MonAnController;
import com.QuanLyNHHS.controller.HoaDonController;
import com.QuanLyNHHS.repository.*;
import com.QuanLyNHHS.service.DatBanService;
import com.QuanLyNHHS.service.BanAnService;
import com.QuanLyNHHS.service.HoaDonService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class ReservationInvoiceFlowTests {
    @Test
    void nhanVienXacNhanDuocLuuVaKhongTheXacNhanLai() {
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        NhanVienRepository nhanVienRepo = mock(NhanVienRepository.class);
        BanAnRepository banRepo = mock(BanAnRepository.class);
        DatBanService service = new DatBanService(datBanRepo, banRepo,
                mock(KhachHangRepository.class), nhanVienRepo, mock(HoaDonRepository.class));
        DatBan db = new DatBan();
        db.setTrangThai("CHO_XAC_NHAN");
        BanAn ban = new BanAn();
        ban.setMaBan(2L);
        ban.setTrangThai("Đã đặt");
        db.setBanAn(ban);
        when(datBanRepo.findForUpdate(1L)).thenReturn(Optional.of(db));
        when(banRepo.findForUpdate(2L)).thenReturn(Optional.of(ban));
        when(nhanVienRepo.existsById(5L)).thenReturn(true);

        service.xacNhan(1L, 5L);

        assertEquals("DA_XAC_NHAN", db.getTrangThai());
        assertEquals(5L, db.getMaNv());
        assertThrows(IllegalStateException.class, () -> service.xacNhan(1L, 6L));
        assertEquals(5L, db.getMaNv());
    }

    @Test
    void hoaDonLayDungBanKhachNhanVienTuDatBanVaChanHoaDonTrung() {
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        BanAnRepository banRepo = mock(BanAnRepository.class);
        HoaDonRepository hoaDonRepo = mock(HoaDonRepository.class);
        CTHoaDonRepository chiTietRepo = mock(CTHoaDonRepository.class);
        MonAnRepository monRepo = mock(MonAnRepository.class);
        HoaDonService service = new HoaDonService(hoaDonRepo, chiTietRepo, monRepo, datBanRepo, banRepo);

        BanAn ban = new BanAn();
        ban.setMaBan(2L);
        KhachHang khach = new KhachHang();
        khach.setMaKh(3L);
        DatBan db = new DatBan();
        db.setMaDat(1L);
        db.setTrangThai("DA_XAC_NHAN");
        db.setMaNv(5L);
        db.setBanAn(ban);
        db.setKhachHang(khach);
        when(datBanRepo.findForUpdate(1L)).thenReturn(Optional.of(db));
        when(banRepo.findForUpdate(2L)).thenReturn(Optional.of(ban));
        ban.setTrangThai("Đã đặt");
        HoaDon invoice = new HoaDon();
        when(hoaDonRepo.save(any(HoaDon.class))).thenAnswer(call -> {
            HoaDon hd = call.getArgument(0);
            hd.setMaHd(9L);
            return hd;
        });
        when(hoaDonRepo.findById(9L)).thenAnswer(call -> Optional.of(invoice));
        MonAn mon = new MonAn();
        mon.setDonGia(BigDecimal.TEN);
        mon.setTrangThai("Đang phục vụ");
        when(monRepo.findById(4L)).thenReturn(Optional.of(mon));
        when(chiTietRepo.tinhTongTienTheoHoaDon(9L)).thenReturn(BigDecimal.TEN);

        invoice.setMaDat(1L);
        invoice.setMaBan(99L);
        invoice.setMaNv(99L);
        invoice.setMaKh(99L);
        HoaDon result = service.taoHoaDon(invoice, List.of(4L), List.of(BigDecimal.ONE));

        assertEquals(2L, result.getMaBan());
        assertEquals(3L, result.getMaKh());
        assertEquals(5L, result.getMaNv());
        assertEquals("Đang dùng", ban.getTrangThai());
        when(hoaDonRepo.existsByMaDat(1L)).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.taoHoaDon(newInvoice(1L), List.of(4L), List.of(BigDecimal.ONE)));
    }

    @Test
    void thanhToanHoanThanhDatBanVaTraBanTrong() {
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        BanAnRepository banRepo = mock(BanAnRepository.class);
        HoaDonRepository hoaDonRepo = mock(HoaDonRepository.class);
        CTHoaDonRepository chiTietRepo = mock(CTHoaDonRepository.class);
        HoaDonService service = new HoaDonService(hoaDonRepo, chiTietRepo,
                mock(MonAnRepository.class), datBanRepo, banRepo);
        HoaDon hd = newInvoice(1L);
        hd.setMaHd(9L);
        hd.setMaBan(2L);
        hd.setTrangThai("Chua thanh toan");
        DatBan db = new DatBan();
        db.setTrangThai("DA_XAC_NHAN");
        BanAn ban = new BanAn();
        ban.setTrangThai("Đang dùng");
        when(hoaDonRepo.findById(9L)).thenReturn(Optional.of(hd));
        when(chiTietRepo.countById_MaHd(9L)).thenReturn(1L);
        when(chiTietRepo.tinhTongTienTheoHoaDon(9L)).thenReturn(BigDecimal.TEN);
        when(hoaDonRepo.save(hd)).thenReturn(hd);
        when(datBanRepo.findForUpdate(1L)).thenReturn(Optional.of(db));
        when(banRepo.findForUpdate(2L)).thenReturn(Optional.of(ban));

        service.thanhToan(9L);

        assertEquals("Da thanh toan", hd.getTrangThai());
        assertEquals("HOAN_THANH", db.getTrangThai());
        assertEquals("Trống", ban.getTrangThai());
        assertEquals(BigDecimal.TEN, hd.getTongTien());
    }

    @Test
    void khongTheHuyDatBanKhiDaCoHoaDon() {
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        BanAnRepository banRepo = mock(BanAnRepository.class);
        HoaDonRepository hoaDonRepo = mock(HoaDonRepository.class);
        DatBanService service = new DatBanService(datBanRepo, banRepo,
                mock(KhachHangRepository.class), mock(NhanVienRepository.class), hoaDonRepo);
        DatBan db = new DatBan();
        db.setTrangThai("DA_XAC_NHAN");
        when(datBanRepo.findForUpdate(1L)).thenReturn(Optional.of(db));
        when(hoaDonRepo.findByMaDat(1L)).thenReturn(Optional.of(new HoaDon()));

        assertThrows(IllegalStateException.class, () -> service.huyDatBan(1L));

        assertEquals("DA_XAC_NHAN", db.getTrangThai());
        verifyNoInteractions(banRepo);
    }

    @Test
    void suaHoaDonKhongTheDoiNhanVienPhuTrach() {
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        HoaDonRepository hoaDonRepo = mock(HoaDonRepository.class);
        CTHoaDonRepository chiTietRepo = mock(CTHoaDonRepository.class);
        HoaDonService service = new HoaDonService(hoaDonRepo, chiTietRepo,
                mock(MonAnRepository.class), datBanRepo, mock(BanAnRepository.class));
        HoaDon hd = newInvoice(1L);
        hd.setMaHd(9L);
        hd.setMaBan(2L);
        hd.setMaNv(5L);
        hd.setTrangThai("Chua thanh toan");
        DatBan db = new DatBan();
        db.setTrangThai("DA_XAC_NHAN");
        db.setMaNv(5L);
        BanAn ban = new BanAn();
        ban.setMaBan(2L);
        db.setBanAn(ban);
        KhachHang khach = new KhachHang();
        khach.setMaKh(3L);
        db.setKhachHang(khach);
        when(hoaDonRepo.findById(9L)).thenReturn(Optional.of(hd));
        when(hoaDonRepo.save(hd)).thenReturn(hd);
        when(datBanRepo.findForUpdate(1L)).thenReturn(Optional.of(db));
        HoaDon form = new HoaDon();
        form.setMaHd(9L);
        form.setMaNv(99L);
        form.setMaBan(99L);
        form.setMaKh(99L);

        service.capNhatHoaDon(form);

        assertEquals(5L, hd.getMaNv());
        assertEquals(2L, hd.getMaBan());
        assertEquals(3L, hd.getMaKh());
    }

    @Test
    void suaSoLuongGiuDonGiaDaGoiVaKhongXoaThemLaiDongCu() {
        HoaDonRepository hoaDonRepo = mock(HoaDonRepository.class);
        CTHoaDonRepository chiTietRepo = mock(CTHoaDonRepository.class);
        MonAnRepository monRepo = mock(MonAnRepository.class);
        HoaDonService service = new HoaDonService(hoaDonRepo, chiTietRepo, monRepo,
                mock(DatBanRepository.class), mock(BanAnRepository.class));
        HoaDon hd = new HoaDon();
        hd.setMaHd(9L);
        hd.setTrangThai("Chua thanh toan");
        CTHoaDon ct = new CTHoaDon();
        ct.setId(new CTHoaDon.CTHoaDonId(9L, 4L));
        ct.setDonGia(BigDecimal.TEN);
        MonAn mon = new MonAn();
        mon.setDonGia(BigDecimal.valueOf(20));
        mon.setTrangThai("Tạm hết");
        when(hoaDonRepo.findById(9L)).thenReturn(Optional.of(hd));
        when(hoaDonRepo.save(hd)).thenReturn(hd);
        when(chiTietRepo.findById_MaHd(9L)).thenReturn(List.of(ct));
        when(chiTietRepo.tinhTongTienTheoHoaDon(9L)).thenReturn(BigDecimal.valueOf(30));
        when(monRepo.findById(4L)).thenReturn(Optional.of(mon));
        HoaDon form = new HoaDon();
        form.setMaHd(9L);

        service.capNhatHoaDon(form, List.of(4L), List.of(BigDecimal.valueOf(3)));

        assertEquals(BigDecimal.TEN, ct.getDonGia());
        assertEquals(BigDecimal.valueOf(30), ct.getThanhTien());
        verify(chiTietRepo, never()).delete(ct);
    }

    @Test
    void themNhanVienLuuVaoBangThat() {
        NhanVienRepository repo = mock(NhanVienRepository.class);
        NhanVienController controller = new NhanVienController(repo);
        RedirectAttributes redirect = mock(RedirectAttributes.class);

        assertEquals("redirect:/nhan-vien", controller.them("Nguyễn An", "0901234567",
                "Nhân viên phục vụ", BigDecimal.valueOf(8000000), LocalDate.of(2026, 11, 11), redirect));

        verify(repo).save(argThat(nv -> "Nguyễn An".equals(nv.getHoTen())
                && "Nhân viên phục vụ".equals(nv.getChucVu())));
    }

    @Test
    void themMonAnLuuVaoBangThatDeCoTheGoiMon() {
        MonAnRepository monRepo = mock(MonAnRepository.class);
        LoaiMonRepository loaiRepo = mock(LoaiMonRepository.class);
        MonAnController controller = new MonAnController(monRepo, loaiRepo);
        RedirectAttributes redirect = mock(RedirectAttributes.class);
        LoaiMon loai = new LoaiMon();
        loai.setTrangThai("Hoạt động");
        when(loaiRepo.findById(2L)).thenReturn(Optional.of(loai));

        assertEquals("redirect:/mon-an", controller.themMon(2L, "Cá hấp", "Đĩa",
                BigDecimal.valueOf(150000), redirect));

        verify(monRepo).save(argThat(mon -> mon.getLoaiMon() == loai
                && "Cá hấp".equals(mon.getTenMon())
                && "Đang phục vụ".equals(mon.getTrangThai())));
    }

    @Test
    void khongTheSuaTrangThaiBanDangDuocDat() {
        BanAnRepository banRepo = mock(BanAnRepository.class);
        DatBanRepository datBanRepo = mock(DatBanRepository.class);
        BanAnService service = new BanAnService();
        ReflectionTestUtils.setField(service, "banAnRepository", banRepo);
        ReflectionTestUtils.setField(service, "datBanRepository", datBanRepo);
        BanAn ban = new BanAn(2L, "Bàn 2", 4, "Tầng 1", "Đã đặt");
        BanAn sua = new BanAn(2L, "Bàn 2", 4, "Tầng 1", "Trống");
        when(banRepo.findForUpdate(2L)).thenReturn(Optional.of(ban));
        when(datBanRepo.existsByBanAn_MaBanAndTrangThaiIn(eq(2L), anyList())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.save(sua));

        assertEquals("Đã đặt", ban.getTrangThai());
    }

    @Test
    void hoaDonHienThiTienConThuSauKhiTruCoc() {
        HoaDonService hoaDonService = mock(HoaDonService.class);
        DatBanService datBanService = mock(DatBanService.class);
        HoaDonController controller = new HoaDonController(hoaDonService,
                mock(MonAnRepository.class), datBanService);
        Model model = mock(Model.class);
        HoaDon hd = newInvoice(1L);
        hd.setTongTien(BigDecimal.valueOf(100));
        DatBan db = new DatBan();
        db.setTienCoc(BigDecimal.valueOf(30));
        when(hoaDonService.timTheoMa(9L)).thenReturn(hd);
        when(datBanService.findById(1L)).thenReturn(Optional.of(db));

        assertEquals("hoadon/chi-tiet", controller.chiTiet(9L, model));

        verify(model).addAttribute("conPhaiThu", BigDecimal.valueOf(70));
        verify(model).addAttribute("tienCoc", BigDecimal.valueOf(30));
        assertEquals(BigDecimal.valueOf(100), hd.getTongTien());
    }

    private HoaDon newInvoice(Long maDat) {
        HoaDon hd = new HoaDon();
        hd.setMaDat(maDat);
        return hd;
    }
}
