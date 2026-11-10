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

/**
 * Xử lý nghiệp vụ nguyên liệu và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class NguyenLieuService {
    // repository: thành phần NguyenLieuRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final NguyenLieuRepository repository;
    // chiTietRepository: thành phần CTPhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final CTPhieuNhapRepository chiTietRepository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần NguyenLieuRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param chiTietRepository thành phần CTPhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public NguyenLieuService(
            NguyenLieuRepository repository, CTPhieuNhapRepository chiTietRepository) {
        this.repository = repository;
        this.chiTietRepository = chiTietRepository;
    }

    /**
     * Lấy toàn bộ nguyên liệu theo thứ tự sắp xếp chỉ định trong Sort.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<NguyenLieu> findAll() {
        return repository.findAll(Sort.by("tenNl", "maNl"));
    }

    /**
     * Tìm nguyên liệu theo khóa chính; không tìm thấy thì ném EntityNotFoundException.
     * @param id khóa chính của nguyên liệu
     * @return kết quả kiểu {@code NguyenLieu} cho nơi gọi sử dụng
     */
    public NguyenLieu findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nguyên liệu."));
    }

    /**
     * Thêm/sửa nguyên liệu, khóa bản ghi khi sửa và chặn đổi đơn vị nếu còn tồn hoặc đã có phiếu; không cho form ghi đè tồn kho.
     * @param id khóa chính của nguyên liệu (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng NguyenLieuForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @return kết quả kiểu {@code NguyenLieu} cho nơi gọi sử dụng
     */
    @Transactional
    public NguyenLieu save(Long id, @Valid NguyenLieuForm form) {
        // ingredient: nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn.
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

    /**
     * Khóa nguyên liệu và chỉ xóa khi chưa phát sinh chi tiết nhập, đồng thời số lượng tồn bằng 0.
     * @param id khóa chính của nguyên liệu
     */
    @Transactional
    public void delete(Long id) {
        // ingredient: nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn.
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
