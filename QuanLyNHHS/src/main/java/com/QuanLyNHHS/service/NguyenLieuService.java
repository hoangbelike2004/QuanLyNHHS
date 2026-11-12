package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.NguyenLieuForm;
import com.QuanLyNHHS.model.NguyenLieu;
import com.QuanLyNHHS.repository.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class NguyenLieuService {
    private final NguyenLieuRepository repository;
    private final CTPhieuNhapRepository chiTietRepository;

    public NguyenLieuService(
            NguyenLieuRepository repository, CTPhieuNhapRepository chiTietRepository) {
        this.repository = repository;
        this.chiTietRepository = chiTietRepository;
    }

    public List<NguyenLieu> findAll() {
        return repository.findAll(Sort.by("tenNl", "maNl"));
    }

    public NguyenLieu findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
    }

    @Transactional
    public NguyenLieu save(Long id, @Valid NguyenLieuForm form) {
        NguyenLieu ingredient =
                id == null
                        ? new NguyenLieu()
                        : repository
                                .findForUpdate(id)
                                .orElseThrow(() ->
                                        new EntityNotFoundException("Không tìm thấy nguyên liệu."));
        if (id != null
                && !ingredient.getDonVi().equals(form.getDonVi().trim())
                && (ingredient.getSoLuongTon().signum() != 0
                        || chiTietRepository.existsByNguyenLieuMaNl(id))) {
            throw new IllegalArgumentException(
                    "Nguyên liệu còn tồn kho hoặc đã có phiếu nhập, không thể đổi đơn vị tính.");
        }
        ingredient.setTenNl(form.getTenNl().trim());
        ingredient.setDonVi(form.getDonVi().trim());
        ingredient.setTrangThai(form.getTrangThai());
        return repository.saveAndFlush(ingredient);
    }

    @Transactional
    public void delete(Long id) {
        NguyenLieu ingredient =
                repository
                        .findForUpdate(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
        if (chiTietRepository.existsByNguyenLieuMaNl(id)
                || ingredient.getSoLuongTon().signum() != 0) {
            throw new IllegalArgumentException(
                    "Nguyên liệu đã phát sinh nhập hàng hoặc còn tồn kho. Hãy chuyển sang ngừng sử"
                        + " dụng.");
        }
        repository.delete(ingredient);
        repository.flush();
    }
}
