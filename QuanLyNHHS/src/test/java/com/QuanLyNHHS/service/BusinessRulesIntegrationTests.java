package com.QuanLyNHHS.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.QuanLyNHHS.controller.*;
import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.repository.*;
import com.QuanLyNHHS.support.MenuTestApplication;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Kiểm thử tích hợp các quy tắc nghiệp vụ đặt bàn, hóa đơn, thanh toán, phiếu nhập và báo cáo trên H2 (MODE=Oracle).
 */
@SpringBootTest(classes = MenuTestApplication.class)
@Import({
    BanAnService.class,
    KhachHangService.class,
    DatBanService.class,
    HoaDonService.class,
    PhieuNhapService.class,
    BaoCaoService.class,
    NguyenLieuService.class,
    NhaCungCapService.class,
    NhanVienNhapKhoRepository.class,
    DatBanController.class,
    HoaDonController.class,
    PhieuNhapController.class,
    BaoCaoController.class,
    KhoExceptionHandler.class
})
@ActiveProfiles("test")
class BusinessRulesIntegrationTests {
    @Autowired WebApplicationContext context;
    @Autowired FilterChainProxy security;

    @Autowired BanAnService banAnService;
    @Autowired KhachHangService khachHangService;
    @Autowired DatBanService datBanService;
    @Autowired HoaDonService hoaDonService;
    @Autowired PhieuNhapService phieuNhapService;
    @Autowired BaoCaoService baoCaoService;
    @Autowired LoaiMonService loaiMonService;
    @Autowired MonAnService monAnService;
    @Autowired NhanVienService nhanVienService;

    @Autowired BanAnRepository banAnRepository;
    @Autowired KhachHangRepository khachHangRepository;
    @Autowired DatBanRepository datBanRepository;
    @Autowired HoaDonRepository hoaDonRepository;
    @Autowired CTHoaDonRepository ctHoaDonRepository;
    @Autowired MonAnRepository monAnRepository;
    @Autowired LoaiMonRepository loaiMonRepository;
    @Autowired NhanVienRepository nhanVienRepository;
    @Autowired PhieuNhapRepository phieuNhapRepository;
    @Autowired CTPhieuNhapRepository ctPhieuNhapRepository;
    @Autowired NguyenLieuRepository nguyenLieuRepository;
    @Autowired NhaCungCapRepository nhaCungCapRepository;
    @Autowired TaiKhoanRepository taiKhoanRepository;

    MockMvc mvc;
    MockHttpSession cashierSession;
    CsrfToken csrf;

    @BeforeEach
    void setup() throws Exception {
        ctPhieuNhapRepository.deleteAll();
        phieuNhapRepository.deleteAll();
        nguyenLieuRepository.deleteAll();
        nhaCungCapRepository.deleteAll();
        ctHoaDonRepository.deleteAll();
        hoaDonRepository.deleteAll();
        datBanRepository.deleteAll();
        banAnRepository.deleteAll();
        khachHangRepository.deleteAll();
        monAnRepository.deleteAll();
        loaiMonRepository.deleteAll();
        nhanVienRepository.deleteAll();
        taiKhoanRepository.deleteAll();

        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        cashierSession = sessionFor("thu_ngan", "ROLE_THU_NGAN");
        var res = mvc.perform(get("/dat-ban/them").session(cashierSession)).andReturn();
        assertEquals(200, res.getResponse().getStatus());
        csrf = (CsrfToken) res.getRequest().getAttribute(CsrfToken.class.getName());
        assertNotNull(csrf);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpSession sessionFor(String username, String role) {
        MockHttpSession s = new MockHttpSession();
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                username, "", List.of(new SimpleGrantedAuthority(role)));
        s.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(auth));
        return s;
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "user", "", List.of(new SimpleGrantedAuthority(role))));
    }

    private BanAn createTable(String status) {
        BanAn b = new BanAn();
        b.setTenBan("Bàn 01");
        b.setSoCho(6);
        b.setKhuVuc("Tầng 1");
        b.setTrangThai(status);
        return banAnService.save(b);
    }

    private KhachHang createCustomer() {
        KhachHang kh = new KhachHang();
        kh.setHoTen("Nguyễn Văn A");
        kh.setSdt("0909123456");
        return khachHangService.save(kh);
    }

    private NhanVien createStaff() {
        NhanVienForm form = new NhanVienForm();
        form.setHoTen("Trần Thu Ngân");
        form.setChucVu("Thu ngân");
        return nhanVienService.save(null, form);
    }

    private MonAn createDish(String name, String price) {
        LoaiMonForm catForm = new LoaiMonForm();
        catForm.setTenLoai("Hải sản " + name);
        LoaiMon cat = loaiMonService.save(null, catForm);
        MonAnForm form = new MonAnForm();
        form.setMaLoai(cat.getMaLoai());
        form.setTenMon(name);
        form.setDonVi("Kg");
        form.setDonGia(new BigDecimal(price));
        return monAnService.save(null, form);
    }

    private NhaCungCap createSupplier() {
        NhaCungCap ncc = new NhaCungCap();
        ncc.setTenNcc("Hải sản Biển Đông");
        ncc.setSdt("0912345678");
        return nhaCungCapRepository.save(ncc);
    }

    private NguyenLieu createMaterial(String name, String stock) {
        NguyenLieu nl = new NguyenLieu();
        nl.setTenNl(name);
        nl.setDonVi("Kg");
        nl.setSoLuongTon(new BigDecimal(stock));
        nl.setTrangThai("HOAT_DONG");
        return nguyenLieuRepository.save(nl);
    }

    @Test
    void tc01_datBanHopLeVaVongDoiXacNhanHoanThanh() throws Exception {
        KhachHang kh = createCustomer();
        BanAn ban = createTable("Trống");

        var res = mvc.perform(post("/dat-ban/them")
                .session(cashierSession)
                .param(csrf.getParameterName(), csrf.getToken())
                .param("maKh", kh.getMaKh().toString())
                .param("maBan", ban.getMaBan().toString())
                .param("ngayDat", LocalDate.now().toString())
                .param("soNguoi", "4")
                .param("tienCoc", "500000"))
                .andReturn();

        assertEquals(302, res.getResponse().getStatus());
        assertEquals("/dat-ban", res.getResponse().getRedirectedUrl());
        assertEquals(1, datBanRepository.count());
        DatBan datBan = datBanRepository.findAll().getFirst();
        assertEquals("CHO_XAC_NHAN", datBan.getTrangThai());
        assertEquals("Đã đặt", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());

        datBanService.xacNhan(datBan.getMaDat());
        assertEquals("DA_XAC_NHAN", datBanRepository.findById(datBan.getMaDat()).orElseThrow().getTrangThai());

        datBanService.hoanThanh(datBan.getMaDat());
        assertEquals("HOAN_THANH", datBanRepository.findById(datBan.getMaDat()).orElseThrow().getTrangThai());
        assertEquals("Trống", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());
    }

    @Test
    void tc02_tuChoiDatBanKhiBanKhongTrong() throws Exception {
        KhachHang kh = createCustomer();
        BanAn ban = createTable("Đã đặt");

        var res = mvc.perform(post("/dat-ban/them")
                .session(cashierSession)
                .param(csrf.getParameterName(), csrf.getToken())
                .param("maKh", kh.getMaKh().toString())
                .param("maBan", ban.getMaBan().toString())
                .param("ngayDat", LocalDate.now().toString())
                .param("soNguoi", "4"))
                .andReturn();

        assertEquals(302, res.getResponse().getStatus());
        assertEquals("/dat-ban/them", res.getResponse().getRedirectedUrl());
        assertTrue(res.getFlashMap().containsKey("errorMessage"));
        assertEquals(0, datBanRepository.count());
    }

    @Test
    void tc04_lapHoaDonTinhTongGiamGiaVaGiuBan() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn tom = createDish("Tôm sú", "320000");
        MonAn ngheu = createDish("Nghêu hấp", "120000");

        HoaDon hd = new HoaDon();
        hd.setMaBan(ban.getMaBan());
        hd.setMaNv(nv.getMaNv());
        hd.setGiamGia(new BigDecimal("20000"));

        HoaDon saved = hoaDonService.taoHoaDon(
                hd,
                List.of(tom.getMaMon(), ngheu.getMaMon()),
                List.of(new BigDecimal("1.5"), new BigDecimal("2")));

        assertEquals("Chua thanh toan", saved.getTrangThai());
        assertEquals(0, new BigDecimal("700000.00").compareTo(saved.getTongTien()));
        assertEquals(2, ctHoaDonRepository.findById_MaHd(saved.getMaHd()).size());
        assertEquals("Đang dùng", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());
    }

    @Test
    void tc05_gopCacDongCungMonTrongHoaDon() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn tom = createDish("Tôm hùm", "320000");

        HoaDon hd = new HoaDon();
        hd.setMaBan(ban.getMaBan());
        hd.setMaNv(nv.getMaNv());

        HoaDon saved = hoaDonService.taoHoaDon(
                hd,
                List.of(tom.getMaMon(), tom.getMaMon()),
                List.of(new BigDecimal("0.5"), new BigDecimal("1.0")));

        List<CTHoaDon> lines = ctHoaDonRepository.findById_MaHd(saved.getMaHd());
        assertEquals(1, lines.size());
        assertEquals(tom.getMaMon(), lines.getFirst().getMaMon());
        assertEquals(0, new BigDecimal("1.50").compareTo(lines.getFirst().getSoLuong()));
        assertEquals(0, new BigDecimal("480000.00").compareTo(saved.getTongTien()));
    }

    @Test
    void tc06_tuChoiDuLieuHoaDonSaiVaBaoVeMonDaXacNhan() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn tom = createDish("Tôm sú", "200000");

        HoaDon zeroQty = new HoaDon();
        zeroQty.setMaBan(ban.getMaBan());
        zeroQty.setMaNv(nv.getMaNv());
        assertThrows(IllegalArgumentException.class, () ->
                hoaDonService.taoHoaDon(zeroQty, List.of(tom.getMaMon()), List.of(BigDecimal.ZERO)));

        HoaDon negDiscount = new HoaDon();
        negDiscount.setMaBan(ban.getMaBan());
        negDiscount.setMaNv(nv.getMaNv());
        negDiscount.setGiamGia(new BigDecimal("-5000"));
        assertThrows(IllegalArgumentException.class, () ->
                hoaDonService.taoHoaDon(negDiscount, List.of(tom.getMaMon()), List.of(BigDecimal.ONE)));

        HoaDon emptyList = new HoaDon();
        emptyList.setMaBan(ban.getMaBan());
        emptyList.setMaNv(nv.getMaNv());
        assertThrows(IllegalArgumentException.class, () ->
                hoaDonService.taoHoaDon(emptyList, List.of(), List.of()));

        HoaDon valid = new HoaDon();
        valid.setMaBan(ban.getMaBan());
        valid.setMaNv(nv.getMaNv());
        HoaDon saved = hoaDonService.taoHoaDon(valid, List.of(tom.getMaMon()), List.of(new BigDecimal("2")));
        hoaDonService.xacNhanMon(saved.getMaHd());

        CTHoaDon confirmedLine = ctHoaDonRepository.findById_MaHd(saved.getMaHd()).getFirst();
        assertEquals(0, new BigDecimal("2.00").compareTo(confirmedLine.getSoLuongDaXacNhan()));

        HoaDon editForm = new HoaDon();
        editForm.setMaHd(saved.getMaHd());
        editForm.setMaNv(nv.getMaNv());

        authenticateAs("ROLE_THU_NGAN");
        assertThrows(IllegalStateException.class, () ->
                hoaDonService.capNhatHoaDon(editForm, List.of(tom.getMaMon()), List.of(BigDecimal.ONE)));

        authenticateAs("ROLE_ADMIN");
        HoaDon adminUpdated = hoaDonService.capNhatHoaDon(editForm, List.of(tom.getMaMon()), List.of(BigDecimal.ONE));
        assertEquals(0, new BigDecimal("200000.00").compareTo(adminUpdated.getTongTien()));
    }

    @Test
    void tc07_rollbackToanBoKhiMotMonKhongTonTai() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn tom = createDish("Tôm càng", "300000");

        HoaDon hd = new HoaDon();
        hd.setMaBan(ban.getMaBan());
        hd.setMaNv(nv.getMaNv());

        assertThrows(IllegalArgumentException.class, () ->
                hoaDonService.taoHoaDon(
                        hd,
                        List.of(tom.getMaMon(), 999999L),
                        List.of(BigDecimal.ONE, BigDecimal.ONE)));

        assertEquals(0, hoaDonRepository.count());
        assertEquals(0, ctHoaDonRepository.count());
        assertEquals("Trống", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());
    }

    @Test
    void tc08_thanhToanChuyenTrangThaiGiaiPhongBanVaKhoaSuaXoa() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn cua = createDish("Cua biển", "450000");

        HoaDon hd = new HoaDon();
        hd.setMaBan(ban.getMaBan());
        hd.setMaNv(nv.getMaNv());
        HoaDon saved = hoaDonService.taoHoaDon(hd, List.of(cua.getMaMon()), List.of(BigDecimal.ONE));
        assertEquals("Đang dùng", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());

        HoaDon paid = hoaDonService.thanhToan(saved.getMaHd());
        assertEquals("Da thanh toan", paid.getTrangThai());
        assertEquals("Trống", banAnRepository.findById(ban.getMaBan()).orElseThrow().getTrangThai());

        HoaDon patch = new HoaDon();
        patch.setMaHd(saved.getMaHd());
        patch.setMaNv(nv.getMaNv());
        assertThrows(IllegalStateException.class, () ->
                hoaDonService.capNhatHoaDon(patch, List.of(cua.getMaMon()), List.of(BigDecimal.ONE)));
        assertThrows(IllegalStateException.class, () ->
                hoaDonService.xoaHoaDon(saved.getMaHd()));
    }

    @Test
    void tc09_tuChoiThanhToanLanHaiHoacHoaDonRong() {
        BanAn ban = createTable("Trống");
        NhanVien nv = createStaff();
        MonAn muc = createDish("Mực ống", "250000");

        HoaDon hd = new HoaDon();
        hd.setMaBan(ban.getMaBan());
        hd.setMaNv(nv.getMaNv());
        HoaDon saved = hoaDonService.taoHoaDon(hd, List.of(muc.getMaMon()), List.of(BigDecimal.ONE));
        hoaDonService.thanhToan(saved.getMaHd());
        assertThrows(IllegalStateException.class, () -> hoaDonService.thanhToan(saved.getMaHd()));

        HoaDon empty = new HoaDon();
        empty.setMaBan(ban.getMaBan());
        empty.setMaNv(nv.getMaNv());
        empty.setTrangThai("Chua thanh toan");
        HoaDon emptySaved = hoaDonRepository.save(empty);
        assertThrows(IllegalStateException.class, () -> hoaDonService.thanhToan(emptySaved.getMaHd()));
    }

    private PhieuNhapForm receiptForm(Long maNcc, Long maNv, Long phienBan, Long maNl, String qty, String price) {
        PhieuNhapForm f = new PhieuNhapForm();
        f.setMaNcc(maNcc);
        f.setMaNv(maNv);
        f.setNgayNhap(LocalDate.now());
        f.setPhienBan(phienBan);
        DongNhapForm d = new DongNhapForm();
        d.setMaNl(maNl);
        d.setSoLuong(new BigDecimal(qty));
        d.setDonGia(new BigDecimal(price));
        f.getChiTiet().add(d);
        return f;
    }

    @Test
    void tc11_tc12_tc13_lapSuaXoaPhieuNhapDieuChinhTonChinhXac() {
        NhaCungCap ncc = createSupplier();
        NhanVien nv = createStaff();
        NguyenLieu tom = createMaterial("Tôm tươi", "10.00");

        PhieuNhap created = phieuNhapService.save(
                null,
                receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, tom.getMaNl(), "5.00", "200000"));
        assertEquals(0, new BigDecimal("15.00").compareTo(
                nguyenLieuRepository.findById(tom.getMaNl()).orElseThrow().getSoLuongTon()));
        assertEquals(0, new BigDecimal("1000000.00").compareTo(created.getTongTien()));

        PhieuNhapForm editForm = phieuNhapService.getForm(created.getMaPn());
        editForm.getChiTiet().getFirst().setSoLuong(new BigDecimal("3.00"));
        PhieuNhap updated = phieuNhapService.save(created.getMaPn(), editForm);
        assertEquals(0, new BigDecimal("13.00").compareTo(
                nguyenLieuRepository.findById(tom.getMaNl()).orElseThrow().getSoLuongTon()));
        assertEquals(0, new BigDecimal("600000.00").compareTo(updated.getTongTien()));

        phieuNhapService.delete(updated.getMaPn(), updated.getPhienBan());
        assertEquals(0, new BigDecimal("10.00").compareTo(
                nguyenLieuRepository.findById(tom.getMaNl()).orElseThrow().getSoLuongTon()));
        assertEquals(0, phieuNhapRepository.count());
        assertEquals(0, ctPhieuNhapRepository.count());
    }

    @Test
    void tc14_tuChoiFormPhieuNhapPhienBanCu() {
        NhaCungCap ncc = createSupplier();
        NhanVien nv = createStaff();
        NguyenLieu tom = createMaterial("Tôm hùm", "10.00");

        PhieuNhap created = phieuNhapService.save(
                null,
                receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, tom.getMaNl(), "5.00", "200000"));

        PhieuNhapForm form1 = phieuNhapService.getForm(created.getMaPn());
        PhieuNhapForm staleForm = phieuNhapService.getForm(created.getMaPn());

        form1.getChiTiet().getFirst().setSoLuong(new BigDecimal("6.00"));
        phieuNhapService.save(created.getMaPn(), form1);

        staleForm.getChiTiet().getFirst().setSoLuong(new BigDecimal("1.00"));
        assertThrows(IllegalArgumentException.class, () ->
                phieuNhapService.save(created.getMaPn(), staleForm));
        assertEquals(0, new BigDecimal("16.00").compareTo(
                nguyenLieuRepository.findById(tom.getMaNl()).orElseThrow().getSoLuongTon()));
    }

    @Test
    void tc15_tuChoiPhieuNhapTrungNguyenLieuHoacGiaLuongSai() {
        NhaCungCap ncc = createSupplier();
        NhanVien nv = createStaff();
        NguyenLieu tom = createMaterial("Tôm sú", "10.00");

        PhieuNhapForm dupForm = receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, tom.getMaNl(), "2.00", "100000");
        DongNhapForm line2 = new DongNhapForm();
        line2.setMaNl(tom.getMaNl());
        line2.setSoLuong(new BigDecimal("1.00"));
        line2.setDonGia(new BigDecimal("100000"));
        dupForm.getChiTiet().add(line2);
        assertThrows(IllegalArgumentException.class, () -> phieuNhapService.save(null, dupForm));

        PhieuNhapForm zeroQty = receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, tom.getMaNl(), "0.00", "100000");
        assertThrows(ConstraintViolationException.class, () -> phieuNhapService.save(null, zeroQty));

        PhieuNhapForm negPrice = receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, tom.getMaNl(), "2.00", "-1");
        assertThrows(ConstraintViolationException.class, () -> phieuNhapService.save(null, negPrice));

        assertEquals(0, phieuNhapRepository.count());
        assertEquals(0, new BigDecimal("10.00").compareTo(
                nguyenLieuRepository.findById(tom.getMaNl()).orElseThrow().getSoLuongTon()));
    }

    @Test
    void tc16_rollbackPhieuNhapKhiMotDongVuotNguongHoacTonAm() {
        NhaCungCap ncc = createSupplier();
        NhanVien nv = createStaff();
        NguyenLieu nl1 = createMaterial("Cua Cà Mau", "10.00");
        NguyenLieu nl2 = createMaterial("Ốc hương", "10.00");

        PhieuNhapForm overflowForm = receiptForm(ncc.getMaNcc(), nv.getMaNv(), null, nl1.getMaNl(), "5.00", "100000");
        DongNhapForm overflowLine = new DongNhapForm();
        overflowLine.setMaNl(nl2.getMaNl());
        overflowLine.setSoLuong(new BigDecimal("99999995.00"));
        overflowLine.setDonGia(new BigDecimal("1.00"));
        overflowForm.getChiTiet().add(overflowLine);

        assertThrows(IllegalArgumentException.class, () -> phieuNhapService.save(null, overflowForm));
        assertEquals(0, phieuNhapRepository.count());
        assertEquals(0, new BigDecimal("10.00").compareTo(
                nguyenLieuRepository.findById(nl1.getMaNl()).orElseThrow().getSoLuongTon()));
        assertEquals(0, new BigDecimal("10.00").compareTo(
                nguyenLieuRepository.findById(nl2.getMaNl()).orElseThrow().getSoLuongTon()));
    }

    @Test
    void tc17_tc18_baoCaoDoanhThuChiCongHoaDonDaThanhToanTrongKhoang() {
        HoaDon paidIn = new HoaDon();
        paidIn.setNgayLap(LocalDate.of(2026, 11, 10));
        paidIn.setTrangThai("Da thanh toan");
        paidIn.setTongTien(new BigDecimal("500000.00"));
        hoaDonRepository.save(paidIn);

        HoaDon unpaidIn = new HoaDon();
        unpaidIn.setNgayLap(LocalDate.of(2026, 11, 10));
        unpaidIn.setTrangThai("Chua thanh toan");
        unpaidIn.setTongTien(new BigDecimal("300000.00"));
        hoaDonRepository.save(unpaidIn);

        HoaDon paidOut = new HoaDon();
        paidOut.setNgayLap(LocalDate.of(2026, 11, 1));
        paidOut.setTrangThai("Da thanh toan");
        paidOut.setTongTien(new BigDecimal("200000.00"));
        hoaDonRepository.save(paidOut);

        assertEquals(0, new BigDecimal("500000.00").compareTo(
                baoCaoService.doanhThuTheoNgay(LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 15))));
        assertEquals(0, BigDecimal.ZERO.compareTo(
                baoCaoService.doanhThuTheoNgay(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31))));
        assertThrows(IllegalArgumentException.class, () ->
                baoCaoService.doanhThuTheoNgay(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 5)));
    }
}
