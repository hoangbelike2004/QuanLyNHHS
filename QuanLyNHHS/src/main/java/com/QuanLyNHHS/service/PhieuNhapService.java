package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.repository.*;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Validated
@Transactional(readOnly = true)
public class PhieuNhapService {
    private static final BigDecimal MAX_STOCK = new BigDecimal("99999999.99");
    private static final BigDecimal MAX_MONEY = new BigDecimal("999999999999.99");
    private final PhieuNhapRepository repository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final NhanVienNhapKhoRepository nhanVienRepository;
    private final EntityManager entityManager;

    public PhieuNhapService(
            PhieuNhapRepository repository,
            NhaCungCapRepository nhaCungCapRepository,
            NguyenLieuRepository nguyenLieuRepository,
            NhanVienNhapKhoRepository nhanVienRepository,
            EntityManager entityManager) {
        this.repository = repository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.entityManager = entityManager;
    }

    public List<PhieuNhap> findAll() {
        return repository.findAllByOrderByNgayNhapDescMaPnDesc();
    }

    public PhieuNhap findById(Long id) {
        return repository
                .findDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    public PhieuNhapForm getForm(Long id) {
        PhieuNhap receipt = findById(id);
        PhieuNhapForm form = new PhieuNhapForm();
        form.setMaNcc(receipt.getNhaCungCap().getMaNcc());
        form.setMaNv(receipt.getMaNv());
        form.setNgayNhap(receipt.getNgayNhap());
        form.setPhienBan(receipt.getPhienBan());
        for (CTPhieuNhap detail : receipt.getChiTiet()) {
            DongNhapForm line = new DongNhapForm();
            line.setMaNl(detail.getNguyenLieu().getMaNl());
            line.setSoLuong(detail.getSoLuong());
            line.setDonGia(detail.getDonGia());
            form.getChiTiet().add(line);
        }
        return form;
    }

    @Transactional
    public PhieuNhap save(Long id, @NotNull @Valid PhieuNhapForm form) {
        PhieuNhap receipt = id == null ? new PhieuNhap() : lock(id);
        if (id != null) {
            checkVersion(receipt, form.getPhienBan());
            // Child-only edits must also invalidate stale receipt forms.
            entityManager.lock(receipt, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }
        NhaCungCap supplier =
                nhaCungCapRepository
                        .findForUpdate(form.getMaNcc())
                        .orElseThrow(
                                () -> new IllegalArgumentException("Nhà cung cấp không tồn tại."));
        if (!nhanVienRepository.existsById(form.getMaNv())) {
            throw new IllegalArgumentException("Nhân viên nhập kho không tồn tại.");
        }

        Map<Long, DongNhapForm> requestedLines = new TreeMap<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (DongNhapForm line : form.getChiTiet()) {
            if (line == null) throw new IllegalArgumentException("Dòng nhập không hợp lệ.");
            if (requestedLines.putIfAbsent(line.getMaNl(), line) != null) {
                throw new IllegalArgumentException(
                        "Mỗi nguyên liệu chỉ được xuất hiện một lần trong phiếu nhập.");
            }
            total = total.add(lineAmount(line));
        }
        if (total.compareTo(MAX_MONEY) > 0)
            throw new IllegalArgumentException("Tổng tiền vượt giới hạn NUMBER(14,2).");

        Map<Long, CTPhieuNhap> existingDetails = new HashMap<>();
        for (CTPhieuNhap detail : receipt.getChiTiet())
            existingDetails.put(detail.getNguyenLieu().getMaNl(), detail);
        Set<Long> ids = new TreeSet<>(existingDetails.keySet());
        ids.addAll(requestedLines.keySet());
        Map<Long, NguyenLieu> ingredients = lockMaterials(ids);
        for (Long materialId : ids) {
            NguyenLieu ingredient = ingredients.get(materialId);
            DongNhapForm line = requestedLines.get(materialId);
            CTPhieuNhap previousDetail = existingDetails.get(materialId);
            BigDecimal before =
                    previousDetail == null ? BigDecimal.ZERO : previousDetail.getSoLuong();
            BigDecimal after = line == null ? BigDecimal.ZERO : line.getSoLuong();
            if (line != null
                    && !"HOAT_DONG".equals(ingredient.getTrangThai())
                    && (previousDetail == null || after.compareTo(before) > 0)) {
                throw new IllegalArgumentException(
                        "Nguyên liệu " + ingredient.getTenNl() + " đã ngừng sử dụng.");
            }
            adjustStock(ingredient, after.subtract(before));
        }

        receipt.setNhaCungCap(supplier);
        receipt.setMaNv(form.getMaNv());
        receipt.setNgayNhap(form.getNgayNhap());
        receipt.setTongTien(total);
        // Keep existing composite keys in place instead of deleting and reinserting them.
        receipt.getChiTiet()
                .removeIf(detail -> !requestedLines.containsKey(detail.getNguyenLieu().getMaNl()));
        for (Map.Entry<Long, DongNhapForm> entry : requestedLines.entrySet()) {
            CTPhieuNhap detail = existingDetails.get(entry.getKey());
            if (detail == null) {
                detail = new CTPhieuNhap();
                detail.setPhieuNhap(receipt);
                detail.setNguyenLieu(ingredients.get(entry.getKey()));
                receipt.getChiTiet().add(detail);
            }
            detail.setSoLuong(entry.getValue().getSoLuong());
            detail.setDonGia(entry.getValue().getDonGia());
            detail.setThanhTien(lineAmount(entry.getValue()));
        }
        return repository.saveAndFlush(receipt);
    }

    @Transactional
    public void delete(Long id, Long phienBan) {
        PhieuNhap receipt = lock(id);
        checkVersion(receipt, phienBan);
        Map<Long, BigDecimal> quantities = new TreeMap<>();
        for (CTPhieuNhap detail : receipt.getChiTiet())
            quantities.put(detail.getNguyenLieu().getMaNl(), detail.getSoLuong());
        Map<Long, NguyenLieu> ingredients = lockMaterials(quantities.keySet());
        quantities.forEach((materialId, quantity) ->
                adjustStock(ingredients.get(materialId), quantity.negate()));
        repository.delete(receipt);
        repository.flush();
    }

    private PhieuNhap lock(Long id) {
        return repository
                .findForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    private Map<Long, NguyenLieu> lockMaterials(Collection<Long> ids) {
        Map<Long, NguyenLieu> result = new TreeMap<>();
        // Every transaction acquires ingredient locks in the same order.
        for (Long id : new TreeSet<>(ids)) {
            NguyenLieu ingredient =
                    nguyenLieuRepository
                            .findForUpdate(id)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Nguyên liệu không tồn tại: " + id));
            // Details may have loaded this entity before its row lock was acquired.
            entityManager.refresh(ingredient, LockModeType.PESSIMISTIC_WRITE);
            result.put(id, ingredient);
        }
        return result;
    }

    private static void checkVersion(PhieuNhap receipt, Long expected) {
        if (expected == null || !expected.equals(receipt.getPhienBan())) {
            throw new IllegalArgumentException(
                    "Phiếu nhập đã thay đổi. Hãy tải lại trang trước khi sửa hoặc xóa.");
        }
    }

    private static BigDecimal lineAmount(DongNhapForm line) {
        BigDecimal amount =
                line.getSoLuong().multiply(line.getDonGia()).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(MAX_MONEY) > 0)
            throw new IllegalArgumentException("Thành tiền vượt giới hạn NUMBER(14,2).");
        return amount;
    }

    private static void adjustStock(NguyenLieu ingredient, BigDecimal delta) {
        BigDecimal next = ingredient.getSoLuongTon().add(delta);
        if (next.signum() < 0) {
            throw new IllegalArgumentException(
                    "Không đủ tồn kho của "
                            + ingredient.getTenNl()
                            + " để sửa hoặc xóa phiếu nhập.");
        }
        if (next.compareTo(MAX_STOCK) > 0) {
            throw new IllegalArgumentException(
                    "Tồn kho của " + ingredient.getTenNl() + " vượt giới hạn NUMBER(10,2).");
        }
        ingredient.setSoLuongTon(next);
    }
}
