package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.NguyenLieuForm;
import com.QuanLyNHHS.model.NguyenLieu;
import com.QuanLyNHHS.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class NguyenLieuService {
    private final NguyenLieuRepository repository;
    private final CTPhieuNhapRepository chiTietRepository;
    public NguyenLieuService(NguyenLieuRepository repository, CTPhieuNhapRepository chiTietRepository) {
        this.repository = repository;
        this.chiTietRepository = chiTietRepository;
    }
    public List<NguyenLieu> findAll() { return repository.findAll(Sort.by("tenNl", "maNl")); }
    public NguyenLieu findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
    }
    @Transactional
    public NguyenLieu save(Long id, @Valid NguyenLieuForm form) {
        NguyenLieu n = id == null ? new NguyenLieu() : repository.findForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
        if (id != null && !n.getDonVi().equals(form.getDonVi().trim()) && chiTietRepository.existsByNguyenLieuMaNl(id)) {
            throw new IllegalArgumentException("Nguyên liệu đã có phiếu nhập, không thể đổi đơn vị tính.");
        }
        n.setTenNl(form.getTenNl().trim());
        n.setDonVi(form.getDonVi().trim());
        n.setTrangThai(form.getTrangThai());
        return repository.saveAndFlush(n);
    }
    @Transactional
    public void delete(Long id) {
        NguyenLieu n = repository.findForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
        if (chiTietRepository.existsByNguyenLieuMaNl(id) || n.getSoLuongTon().signum() != 0) {
            throw new IllegalArgumentException("Nguyên liệu đã phát sinh nhập hàng hoặc còn tồn kho. Hãy chuyển sang ngừng sử dụng.");
        }
        repository.delete(n);
        repository.flush();
    }
}
