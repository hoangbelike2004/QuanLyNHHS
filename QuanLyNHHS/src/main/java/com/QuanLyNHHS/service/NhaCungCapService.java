package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.NhaCungCapForm;
import com.QuanLyNHHS.model.NhaCungCap;
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
public class NhaCungCapService {
    private final NhaCungCapRepository repository;
    private final PhieuNhapRepository phieuNhapRepository;

    public NhaCungCapService(
            NhaCungCapRepository repository, PhieuNhapRepository phieuNhapRepository) {
        this.repository = repository;
        this.phieuNhapRepository = phieuNhapRepository;
    }

    public List<NhaCungCap> findAll() {
        return repository.findAll(Sort.by("tenNcc", "maNcc"));
    }

    public NhaCungCap findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp."));
    }

    @Transactional
    public NhaCungCap save(Long id, @Valid NhaCungCapForm form) {
        NhaCungCap supplier =
                id == null
                        ? new NhaCungCap()
                        : repository
                                .findForUpdate(id)
                                .orElseThrow(() ->
                                        new EntityNotFoundException(
                                                "Không tìm thấy nhà cung cấp."));
        supplier.setTenNcc(form.getTenNcc().trim());
        supplier.setSdt(trim(form.getSdt()));
        supplier.setDiaChi(trim(form.getDiaChi()));
        supplier.setEmail(trim(form.getEmail()));
        return repository.saveAndFlush(supplier);
    }

    @Transactional
    public void delete(Long id) {
        NhaCungCap supplier =
                repository
                        .findForUpdate(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Không tìm thấy nhà cung cấp."));
        if (phieuNhapRepository.existsByNhaCungCapMaNcc(id)) {
            throw new IllegalArgumentException("Nhà cung cấp đã có phiếu nhập, không thể xóa.");
        }
        repository.delete(supplier);
        repository.flush();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
