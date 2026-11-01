package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.repository.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
    @PersistenceContext
    private EntityManager entityManager;

    public PhieuNhapService(PhieuNhapRepository repository, NhaCungCapRepository nhaCungCapRepository,
            NguyenLieuRepository nguyenLieuRepository, NhanVienNhapKhoRepository nhanVienRepository) {
        this.repository = repository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<PhieuNhap> findAll() { return repository.findAllByOrderByNgayNhapDescMaPnDesc(); }

    public PhieuNhap findById(Long id) {
        return repository.findDetails(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    public PhieuNhapForm getForm(Long id) {
        PhieuNhap p = findById(id);
        PhieuNhapForm f = new PhieuNhapForm();
        f.setMaNcc(p.getNhaCungCap().getMaNcc());
        f.setMaNv(p.getMaNv());
        f.setNgayNhap(p.getNgayNhap());
        f.setPhienBan(p.getPhienBan());
        for (CTPhieuNhap c : p.getChiTiet()) {
            DongNhapForm d = new DongNhapForm();
            d.setMaNl(c.getNguyenLieu().getMaNl());
            d.setSoLuong(c.getSoLuong());
            d.setDonGia(c.getDonGia());
            f.getChiTiet().add(d);
        }
        return f;
    }

    @Transactional
    public PhieuNhap save(Long id, @NotNull @Valid PhieuNhapForm form) {
        PhieuNhap p = id == null ? new PhieuNhap() : lock(id);
        if (id != null) {
            checkVersion(p, form.getPhienBan());
            // Child-only edits must also invalidate stale receipt forms.
            entityManager.lock(p, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }
        NhaCungCap supplier = nhaCungCapRepository.findForUpdate(form.getMaNcc())
            .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại."));
        if (!nhanVienRepository.existsById(form.getMaNv())) {
            throw new IllegalArgumentException("Nhân viên nhập kho không tồn tại.");
        }

        Map<Long, DongNhapForm> requested = new TreeMap<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (DongNhapForm d : form.getChiTiet()) {
            if (d == null) throw new IllegalArgumentException("Dòng nhập không hợp lệ.");
            if (requested.putIfAbsent(d.getMaNl(), d) != null) {
                throw new IllegalArgumentException("Mỗi nguyên liệu chỉ được xuất hiện một lần trong phiếu nhập.");
            }
            total = total.add(lineAmount(d));
        }
        if (total.compareTo(MAX_MONEY) > 0) throw new IllegalArgumentException("Tổng tiền vượt giới hạn NUMBER(14,2).");

        Map<Long, CTPhieuNhap> existing = new HashMap<>();
        for (CTPhieuNhap c : p.getChiTiet()) existing.put(c.getNguyenLieu().getMaNl(), c);
        Set<Long> ids = new TreeSet<>(existing.keySet());
        ids.addAll(requested.keySet());
        Map<Long, NguyenLieu> materials = lockMaterials(ids);
        for (Long materialId : ids) {
            NguyenLieu n = materials.get(materialId);
            DongNhapForm d = requested.get(materialId);
            CTPhieuNhap old = existing.get(materialId);
            BigDecimal before = old == null ? BigDecimal.ZERO : old.getSoLuong();
            BigDecimal after = d == null ? BigDecimal.ZERO : d.getSoLuong();
            if (d != null && !"HOAT_DONG".equals(n.getTrangThai()) && (old == null || after.compareTo(before) > 0)) {
                throw new IllegalArgumentException("Nguyên liệu " + n.getTenNl() + " đã ngừng sử dụng.");
            }
            adjustStock(n, after.subtract(before));
        }

        p.setNhaCungCap(supplier);
        p.setMaNv(form.getMaNv());
        p.setNgayNhap(form.getNgayNhap());
        p.setTongTien(total);
        // Keep existing composite keys in place instead of deleting and reinserting them.
        p.getChiTiet().removeIf(c -> !requested.containsKey(c.getNguyenLieu().getMaNl()));
        for (Map.Entry<Long, DongNhapForm> entry : requested.entrySet()) {
            CTPhieuNhap c = existing.get(entry.getKey());
            if (c == null) {
                c = new CTPhieuNhap();
                c.setPhieuNhap(p);
                c.setNguyenLieu(materials.get(entry.getKey()));
                p.getChiTiet().add(c);
            }
            c.setSoLuong(entry.getValue().getSoLuong());
            c.setDonGia(entry.getValue().getDonGia());
            c.setThanhTien(lineAmount(entry.getValue()));
        }
        return repository.saveAndFlush(p);
    }

    @Transactional
    public void delete(Long id, Long phienBan) {
        PhieuNhap p = lock(id);
        checkVersion(p, phienBan);
        Map<Long, BigDecimal> quantities = new TreeMap<>();
        for (CTPhieuNhap c : p.getChiTiet()) quantities.put(c.getNguyenLieu().getMaNl(), c.getSoLuong());
        Map<Long, NguyenLieu> materials = lockMaterials(quantities.keySet());
        quantities.forEach((materialId, quantity) -> adjustStock(materials.get(materialId), quantity.negate()));
        repository.delete(p);
        repository.flush();
    }

    private PhieuNhap lock(Long id) {
        return repository.findForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    private Map<Long, NguyenLieu> lockMaterials(Collection<Long> ids) {
        Map<Long, NguyenLieu> result = new TreeMap<>();
        // Every transaction acquires ingredient locks in the same order.
        for (Long id : new TreeSet<>(ids)) {
            NguyenLieu material = nguyenLieuRepository.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Nguyên liệu không tồn tại: " + id));
            // Details may have loaded this entity before its row lock was acquired.
            entityManager.refresh(material, LockModeType.PESSIMISTIC_WRITE);
            result.put(id, material);
        }
        return result;
    }

    private static void checkVersion(PhieuNhap p, Long expected) {
        if (expected == null || !expected.equals(p.getPhienBan())) {
            throw new IllegalArgumentException("Phiếu nhập đã thay đổi. Hãy tải lại trang trước khi sửa hoặc xóa.");
        }
    }

    private static BigDecimal lineAmount(DongNhapForm d) {
        BigDecimal amount = d.getSoLuong().multiply(d.getDonGia()).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(MAX_MONEY) > 0) throw new IllegalArgumentException("Thành tiền vượt giới hạn NUMBER(14,2).");
        return amount;
    }

    private static void adjustStock(NguyenLieu material, BigDecimal delta) {
        BigDecimal next = material.getSoLuongTon().add(delta);
        if (next.signum() < 0) {
            throw new IllegalArgumentException("Không đủ tồn kho của " + material.getTenNl() + " để sửa hoặc xóa phiếu nhập.");
        }
        if (next.compareTo(MAX_STOCK) > 0) {
            throw new IllegalArgumentException("Tồn kho của " + material.getTenNl() + " vượt giới hạn NUMBER(10,2).");
        }
        material.setSoLuongTon(next);
    }
}
