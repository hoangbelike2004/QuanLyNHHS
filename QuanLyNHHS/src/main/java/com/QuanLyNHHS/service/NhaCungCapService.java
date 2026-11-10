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

/**
 * Xử lý nghiệp vụ nhà cung cấp và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class NhaCungCapService {
    // repository: thành phần NhaCungCapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final NhaCungCapRepository repository;
    // phieuNhapRepository: thành phần PhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final PhieuNhapRepository phieuNhapRepository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần NhaCungCapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param phieuNhapRepository thành phần PhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public NhaCungCapService(
            NhaCungCapRepository repository, PhieuNhapRepository phieuNhapRepository) {
        this.repository = repository;
        this.phieuNhapRepository = phieuNhapRepository;
    }

    /**
     * Lấy toàn bộ nhà cung cấp theo thứ tự sắp xếp chỉ định trong Sort.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<NhaCungCap> findAll() {
        return repository.findAll(Sort.by("tenNcc", "maNcc"));
    }

    /**
     * Tìm nhà cung cấp theo khóa chính; không tìm thấy thì ném EntityNotFoundException.
     * @param id khóa chính của nhà cung cấp
     * @return kết quả kiểu {@code NhaCungCap} cho nơi gọi sử dụng
     */
    public NhaCungCap findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp."));
    }

    /**
     * Thêm/sửa nhà cung cấp, khóa bản ghi cũ khi sửa và chuẩn hóa khoảng trắng trước khi lưu.
     * @param id khóa chính của nhà cung cấp (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng NhaCungCapForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @return kết quả kiểu {@code NhaCungCap} cho nơi gọi sử dụng
     */
    @Transactional
    public NhaCungCap save(Long id, @Valid NhaCungCapForm form) {
        // supplier: nhà cung cấp được chọn hoặc đang chỉnh sửa.
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

    /**
     * Khóa nhà cung cấp và chặn xóa nếu đã có phiếu nhập tham chiếu.
     * @param id khóa chính của nhà cung cấp
     */
    @Transactional
    public void delete(Long id) {
        // supplier: nhà cung cấp được chọn hoặc đang chỉnh sửa.
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

    /**
     * Bỏ khoảng trắng đầu/cuối của chuỗi; giữ null nếu đầu vào null.
     * @param value chuỗi đầu vào cần bỏ khoảng trắng; có thể null
     * @return kết quả kiểu {@code String} cho nơi gọi sử dụng
     */
    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
