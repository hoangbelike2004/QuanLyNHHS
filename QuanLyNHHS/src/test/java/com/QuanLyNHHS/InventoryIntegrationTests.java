package com.QuanLyNHHS;

import static org.junit.jupiter.api.Assertions.*;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.service.*;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class InventoryIntegrationTests {
    @LocalServerPort int port;
    @Autowired NhaCungCapService suppliers;
    @Autowired NguyenLieuService materials;
    @Autowired PhieuNhapService receipts;
    @Autowired JdbcTemplate jdbc;
    Long supplierId;
    Long shrimp;
    Long crab;

    @BeforeEach
    void setup() {
        jdbc.update("DELETE FROM CT_PHIEU_NHAP");
        jdbc.update("DELETE FROM PHIEU_NHAP");
        jdbc.update("DELETE FROM NGUYEN_LIEU");
        jdbc.update("DELETE FROM NHA_CUNG_CAP");
        NhaCungCapForm supplier = new NhaCungCapForm();
        supplier.setTenNcc("Hải sản Cần Giờ");
        supplier.setEmail("kho@example.com");
        supplierId = suppliers.save(null, supplier).getMaNcc();
        shrimp = material("Tôm sú");
        crab = material("Cua biển");
    }

    Long material(String name) {
        NguyenLieuForm form = new NguyenLieuForm();
        form.setTenNl(name);
        form.setDonVi("kg");
        return materials.save(null, form).getMaNl();
    }

    DongNhapForm line(Long id, String quantity, String price) {
        DongNhapForm d = new DongNhapForm();
        d.setMaNl(id);
        d.setSoLuong(new BigDecimal(quantity));
        d.setDonGia(new BigDecimal(price));
        return d;
    }

    PhieuNhapForm form(DongNhapForm... lines) {
        PhieuNhapForm f = new PhieuNhapForm();
        f.setMaNcc(supplierId);
        f.setMaNv(1L);
        f.setNgayNhap(LocalDate.now());
        f.setChiTiet(new ArrayList<>(List.of(lines)));
        return f;
    }

    void stock(Long id, String expected) {
        assertEquals(0, materials.findById(id).getSoLuongTon().compareTo(new BigDecimal(expected)));
    }

    @Test
    void createRoundsEachLineBeforeSummingAndUpdatesStock() {
        PhieuNhap p =
                receipts.save(
                        null, form(line(shrimp, "1.25", "10.01"), line(crab, "0.50", "20.01")));
        assertEquals(new BigDecimal("22.52"), p.getTongTien());
        assertEquals(2, receipts.findById(p.getMaPn()).getChiTiet().size());
        stock(shrimp, "1.25");
        stock(crab, "0.50");
    }

    @Test
    void editUsesDeltaAndAddsAndRemovesIngredients() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "10", "100")));
        PhieuNhapForm edit = receipts.getForm(p.getMaPn());
        edit.setChiTiet(
                new ArrayList<>(List.of(line(shrimp, "7.5", "120"), line(crab, "2", "50"))));
        receipts.save(p.getMaPn(), edit);
        stock(shrimp, "7.5");
        stock(crab, "2");
        assertEquals(new BigDecimal("1000.00"), receipts.findById(p.getMaPn()).getTongTien());
        edit = receipts.getForm(p.getMaPn());
        edit.setChiTiet(new ArrayList<>(List.of(line(crab, "3", "10"))));
        receipts.save(p.getMaPn(), edit);
        stock(shrimp, "0");
        stock(crab, "3");
        assertEquals(1, receipts.findById(p.getMaPn()).getChiTiet().size());
    }

    @Test
    void changingOnlyPriceDoesNotAddStockAgain() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "5", "100")));
        PhieuNhapForm edit = receipts.getForm(p.getMaPn());
        edit.getChiTiet().getFirst().setDonGia(new BigDecimal("200"));
        receipts.save(p.getMaPn(), edit);
        stock(shrimp, "5");
        assertEquals(new BigDecimal("1000.00"), receipts.findById(p.getMaPn()).getTongTien());
    }

    @Test
    void deleteReversesStockAndCascadesDetails() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "5", "100"), line(crab, "2", "20")));
        receipts.delete(p.getMaPn(), receipts.getForm(p.getMaPn()).getPhienBan());
        stock(shrimp, "0");
        stock(crab, "0");
        assertTrue(receipts.findAll().isEmpty());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM CT_PHIEU_NHAP", Integer.class));
    }

    @Test
    void insufficientStockRollsBackEntireEditAndDelete() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "5", "100"), line(crab, "2", "20")));
        jdbc.update("UPDATE NGUYEN_LIEU SET SO_LUONG_TON=0 WHERE MA_NL=?", crab);
        PhieuNhapForm edit = receipts.getForm(p.getMaPn());
        edit.getChiTiet().getFirst().setSoLuong(BigDecimal.ONE);
        edit.getChiTiet().get(1).setSoLuong(BigDecimal.ONE);
        assertThrows(IllegalArgumentException.class, () -> receipts.save(p.getMaPn(), edit));
        stock(shrimp, "5");
        stock(crab, "0");
        assertEquals(new BigDecimal("540.00"), receipts.findById(p.getMaPn()).getTongTien());
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.delete(p.getMaPn(), edit.getPhienBan()));
        stock(shrimp, "5");
        assertEquals(2, receipts.findById(p.getMaPn()).getChiTiet().size());
    }

    @Test
    void staleEditAndDeleteAreRejectedEvenForChildOnlyChange() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "1", "20"), line(crab, "1", "20")));
        PhieuNhapForm stale = receipts.getForm(p.getMaPn());
        PhieuNhapForm edit = receipts.getForm(p.getMaPn());
        edit.getChiTiet().getFirst().setDonGia(new BigDecimal("25"));
        edit.getChiTiet().get(1).setDonGia(new BigDecimal("15"));
        receipts.save(p.getMaPn(), edit);
        assertThrows(IllegalArgumentException.class, () -> receipts.save(p.getMaPn(), stale));
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.delete(p.getMaPn(), stale.getPhienBan()));
        stock(shrimp, "1");
        stock(crab, "1");
    }

    @Test
    void duplicateIngredientIsRejectedWithoutWrites() {
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.save(null, form(line(shrimp, "1", "20"), line(shrimp, "2", "20"))));
        stock(shrimp, "0");
        assertTrue(receipts.findAll().isEmpty());
    }

    @Test
    void validatesQuantityPriceScaleDateAndEmptyReceiptAtServiceBoundary() {
        for (DongNhapForm bad :
                List.of(
                        line(shrimp, "0", "1"),
                        line(shrimp, "-1", "1"),
                        line(shrimp, "1", "-1"),
                        line(shrimp, "1.001", "1"),
                        line(shrimp, "1", "0.001"))) {
            assertThrows(ConstraintViolationException.class, () -> receipts.save(null, form(bad)));
        }
        assertThrows(ConstraintViolationException.class, () -> receipts.save(null, form()));
        PhieuNhapForm future = form(line(shrimp, "1", "1"));
        future.setNgayNhap(LocalDate.now().plusDays(1));
        assertThrows(ConstraintViolationException.class, () -> receipts.save(null, future));
        stock(shrimp, "0");
    }

    @Test
    void missingSupplierEmployeeOrIngredientIsRejected() {
        PhieuNhapForm f = form(line(shrimp, "1", "10"));
        f.setMaNcc(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> receipts.save(null, f));
        f.setMaNcc(supplierId);
        f.setMaNv(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> receipts.save(null, f));
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.save(null, form(line(Long.MAX_VALUE, "1", "10"))));
        assertTrue(receipts.findAll().isEmpty());
    }

    @Test
    void moneyAndStockOverflowAreRejectedAtomically() {
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.save(null, form(line(shrimp, "99999999.99", "9999999999.99"))));
        receipts.save(null, form(line(shrimp, "99999999.99", "0")));
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.save(null, form(line(shrimp, "0.01", "0"))));
        stock(shrimp, "99999999.99");
        assertEquals(1, receipts.findAll().size());
    }

    @Test
    void totalOverflowIsRejectedWhenIndividualLinesFit() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        receipts.save(
                                null,
                                form(
                                        line(shrimp, "10000", "60000000"),
                                        line(crab, "10000", "60000000"))));
        stock(shrimp, "0");
        stock(crab, "0");
    }

    @Test
    void inactiveIngredientCannotReceiveMoreButCanBeReversed() {
        PhieuNhap p = receipts.save(null, form(line(shrimp, "2", "10")));
        NguyenLieuForm f = new NguyenLieuForm();
        f.setTenNl("Tôm sú");
        f.setDonVi("kg");
        f.setTrangThai("NGUNG_SU_DUNG");
        materials.save(shrimp, f);
        assertThrows(
                IllegalArgumentException.class,
                () -> receipts.save(null, form(line(shrimp, "1", "1"))));
        PhieuNhapForm edit = receipts.getForm(p.getMaPn());
        edit.getChiTiet().getFirst().setSoLuong(BigDecimal.ONE);
        receipts.save(p.getMaPn(), edit);
        stock(shrimp, "1");
        receipts.delete(p.getMaPn(), receipts.getForm(p.getMaPn()).getPhienBan());
        stock(shrimp, "0");
    }

    @Test
    void referencedCatalogRowsCannotBeDeletedOrHaveTheirUnitChanged() {
        receipts.save(null, form(line(shrimp, "1", "1")));
        assertThrows(IllegalArgumentException.class, () -> suppliers.delete(supplierId));
        assertThrows(IllegalArgumentException.class, () -> materials.delete(shrimp));
        NguyenLieuForm f = new NguyenLieuForm();
        f.setTenNl("Tôm sú");
        f.setDonVi("con");
        assertThrows(IllegalArgumentException.class, () -> materials.save(shrimp, f));
        materials.delete(crab);
        assertThrows(
                jakarta.persistence.EntityNotFoundException.class, () -> materials.findById(crab));
    }

    @Test
    void simultaneousReceiptsDoNotLoseStockUpdates() throws Exception {
        int count = 8;
        List<Long> supplierIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            NhaCungCapForm f = new NhaCungCapForm();
            f.setTenNcc("Nhà cung cấp " + i);
            supplierIds.add(suppliers.save(null, f).getMaNcc());
        }
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(count)) {
            List<Future<?>> tasks = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                final Long ncc = supplierIds.get(i);
                tasks.add(
                        executor.submit(() -> {
                            PhieuNhapForm f =
                                    form(line(shrimp, "1.25", "10"), line(crab, "0.5", "10"));
                            f.setMaNcc(ncc);
                            start.await();
                            receipts.save(null, f);
                            return null;
                        }));
            }
            start.countDown();
            for (Future<?> task : tasks) task.get(20, TimeUnit.SECONDS);
        }
        stock(shrimp, "10");
        stock(crab, "4");
        assertEquals(count, receipts.findAll().size());
    }

    HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    HttpResponse<String> post(String path, Map<String, String> fields) throws Exception {
        String body =
                fields.entrySet().stream()
                        .map(e ->
                                URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                                        + "="
                                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                        .collect(java.util.stream.Collectors.joining("&"));
        return HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .POST(HttpRequest.BodyPublishers.ofString(body))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    Map<String, String> receiptFields() {
        return new HashMap<>(
                Map.of(
                        "maNcc",
                        supplierId.toString(),
                        "maNv",
                        "1",
                        "ngayNhap",
                        LocalDate.now().toString(),
                        "chiTiet[0].maNl",
                        shrimp.toString(),
                        "chiTiet[0].soLuong",
                        "1.25",
                        "chiTiet[0].donGia",
                        "10.01"));
    }

    @Test
    void allInventoryPagesRenderWithRealThymeleaf() throws Exception {
        for (String path :
                List.of(
                        "/nha-cung-cap",
                        "/nha-cung-cap/them",
                        "/nha-cung-cap/" + supplierId + "/sua",
                        "/nguyen-lieu",
                        "/nguyen-lieu/them",
                        "/nguyen-lieu/" + shrimp + "/sua",
                        "/phieu-nhap",
                        "/phieu-nhap/them")) {
            HttpResponse<String> response = get(path);
            assertEquals(200, response.statusCode(), path + ": " + response.body());
            assertTrue(response.body().contains("<h1"), path);
        }
        assertEquals(302, get("/").statusCode());
        assertEquals(404, get("/phieu-nhap/99999999").statusCode());
        assertEquals(405, get("/nguyen-lieu/" + shrimp + "/xoa").statusCode());
    }

    @Test
    void receiptCanBeCreatedEditedAndDeletedThroughHttp() throws Exception {
        Map<String, String> fields = receiptFields();
        fields.put("tongTien", "0");
        fields.put("chiTiet[0].thanhTien", "0");
        HttpResponse<String> created = post("/phieu-nhap/them", fields);
        assertEquals(302, created.statusCode(), created.body());
        String detail =
                URI.create(created.headers().firstValue("Location").orElseThrow())
                        .getPath()
                        .split(";")[0];
        assertEquals(200, get(detail).statusCode());
        PhieuNhap p = receipts.findAll().getFirst();
        assertEquals(new BigDecimal("12.51"), p.getTongTien());
        stock(shrimp, "1.25");
        assertEquals(200, get(detail + "/sua").statusCode());
        fields.put("phienBan", receipts.getForm(p.getMaPn()).getPhienBan().toString());
        fields.put("chiTiet[0].soLuong", "2.5");
        assertEquals(302, post(detail + "/sua", fields).statusCode());
        stock(shrimp, "2.5");
        assertEquals(
                302,
                post(
                                detail + "/xoa",
                                Map.of(
                                        "phienBan",
                                        receipts.getForm(p.getMaPn()).getPhienBan().toString()))
                        .statusCode());
        stock(shrimp, "0");
        assertTrue(receipts.findAll().isEmpty());
    }

    @Test
    void invalidHttpFormKeepsUserInputAndDoesNotChangeStock() throws Exception {
        Map<String, String> fields = receiptFields();
        fields.put("chiTiet[0].soLuong", "-1");
        HttpResponse<String> response = post("/phieu-nhap/them", fields);
        assertEquals(200, response.statusCode(), response.body());
        assertTrue(response.body().contains("Số lượng phải lớn hơn 0."));
        assertTrue(response.body().contains("-1"));
        stock(shrimp, "0");
    }

    @Test
    void stockCannotBeOverwrittenThroughIngredientForm() throws Exception {
        receipts.save(null, form(line(shrimp, "2", "1")));
        HttpResponse<String> response =
                post(
                        "/nguyen-lieu/" + shrimp + "/sua",
                        Map.of(
                                "tenNl",
                                "Tôm sú mới",
                                "donVi",
                                "kg",
                                "trangThai",
                                "HOAT_DONG",
                                "soLuongTon",
                                "99999",
                                "maNl",
                                crab.toString()));
        assertEquals(302, response.statusCode(), response.body());
        stock(shrimp, "2");
        stock(crab, "0");
        assertEquals("Tôm sú mới", materials.findById(shrimp).getTenNl());
    }

    @Test
    void oversizedFormIndexIsRejectedWithoutWrites() throws Exception {
        Map<String, String> fields = receiptFields();
        fields.put("chiTiet[1000].maNl", shrimp.toString());
        assertEquals(400, post("/phieu-nhap/them", fields).statusCode());
        assertTrue(receipts.findAll().isEmpty());
        stock(shrimp, "0");
    }

    @Test
    void emptyHttpReceiptCanBeCorrectedWithoutReloading() throws Exception {
        HttpResponse<String> response =
                post(
                        "/phieu-nhap/them",
                        Map.of(
                                "maNcc",
                                supplierId.toString(),
                                "maNv",
                                "1",
                                "ngayNhap",
                                LocalDate.now().toString()));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Phiếu phải có từ 1 đến 100 dòng."));
        assertTrue(
                response.body().contains("data-line"),
                "The invalid form must retain an editable row for adding ingredients");
        assertTrue(response.body().contains("name=\"chiTiet[0].maNl\""));
        assertTrue(receipts.findAll().isEmpty());
    }

    @Test
    void unitCannotChangeWhileStockExistsWithoutReceiptDetails() {
        jdbc.update("UPDATE NGUYEN_LIEU SET SO_LUONG_TON=2 WHERE MA_NL=?", shrimp);
        NguyenLieuForm form = new NguyenLieuForm();
        form.setTenNl("Tôm sú");
        form.setDonVi("con");
        assertThrows(IllegalArgumentException.class, () -> materials.save(shrimp, form));
        stock(shrimp, "2");
        assertEquals("kg", materials.findById(shrimp).getDonVi());
        jdbc.update("UPDATE NGUYEN_LIEU SET SO_LUONG_TON=0 WHERE MA_NL=?", shrimp);
        materials.save(shrimp, form);
        assertEquals("con", materials.findById(shrimp).getDonVi());
    }
}
