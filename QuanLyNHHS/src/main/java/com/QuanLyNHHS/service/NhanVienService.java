package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.NhanVienForm;
import com.QuanLyNHHS.model.NhanVien;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.NhanVienRepository;
import com.QuanLyNHHS.repository.PhieuNhapRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Xử lý nghiệp vụ nhân viên, được Controller gọi trước khi truy cập database.
 * {@code @Service}: Spring tạo và quản lý đối tượng service.
 * {@code @Validated}: kiểm tra @NotNull, @Valid trên tham số khi gọi qua bean của Spring.
 * {@code @Transactional(readOnly = true)}: mặc định dùng giao dịch đọc;
 * các hàm save/delete có @Transactional riêng để được phép ghi dữ liệu.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class NhanVienService {
    // Truy cập bảng NHAN_VIEN để đọc, lưu và xóa hồ sơ nhân viên.
    private final NhanVienRepository repository;
    // Kiểm tra nhân viên đã lập hóa đơn hay chưa trước khi cho phép xóa.
    private final HoaDonRepository hoaDonRepository;
    // Kiểm tra nhân viên đã lập phiếu nhập hay chưa trước khi cho phép xóa.
    private final PhieuNhapRepository phieuNhapRepository;

    /**
     * Spring truyền các repository vào constructor; this giữ chúng để các hàm dùng lại.
     * @param repository thành phần NhanVienRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param hoaDonRepository thành phần HoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param phieuNhapRepository thành phần PhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public NhanVienService(NhanVienRepository repository, HoaDonRepository hoaDonRepository,
                           PhieuNhapRepository phieuNhapRepository) {
        this.repository = repository;
        this.hoaDonRepository = hoaDonRepository;
        this.phieuNhapRepository = phieuNhapRepository;
    }
    /** @return danh sách toàn bộ nhân viên để Controller hiển thị và tính thống kê. */
    public List<NhanVien> findAll() { return repository.findAll(); }
    /**
     * Tìm một nhân viên theo khóa chính.
     * @param id mã nhân viên lấy từ URL hoặc từ lời gọi của service khác
     * @return đối tượng tương ứng trong database
     * @throws EntityNotFoundException nếu mã không tồn tại
     */
    public NhanVien getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên."));
    }
    /**
     * Thêm mới hoặc cập nhật nhân viên trong một giao dịch.
     * @param id null khi thêm; có giá trị khi sửa một bản ghi đã tồn tại
     * @param form dữ liệu đã được DTO kiểm tra, không chứa khóa chính để ghi đè tùy ý
     * @return đối tượng đã lưu; bản ghi mới được cấp mã bằng sequence của entity
     */
    @Transactional
    public NhanVien save(Long id, @NotNull @Valid NhanVienForm form) {
        // entity là đối tượng sẽ lưu: tạo mới khi id null, lấy bản ghi cũ khi sửa.
        NhanVien entity = id == null ? new NhanVien() : getById(id);
        entity.setHoTen(form.getHoTen());
        entity.setGioiTinh(form.getGioiTinh());
        entity.setSdt(form.getSdt());
        entity.setEmail(form.getEmail());
        entity.setChucVu(form.getChucVu());
        entity.setCaLamViec(form.getCaLamViec());
        entity.setLuong(form.getLuong());
        entity.setNgayVaoLam(form.getNgayVaoLam());
        entity.setTrangThai(form.getTrangThai());
        // saveAndFlush gửi thay đổi xuống database ngay; commit diễn ra khi giao dịch kết thúc.
        return repository.saveAndFlush(entity);
    }
    /**
     * Xóa nhân viên sau khi kiểm tra các dữ liệu đang tham chiếu đến nó.
     * @param id mã nhân viên cần xóa
     * @throws EntityNotFoundException nếu bản ghi đã mất
     * @throws IllegalArgumentException nếu dữ liệu liên quan không cho phép xóa
     */
    @Transactional
    public void delete(Long id) {
        // entity: bản ghi hiện có trong database để kiểm tra điều kiện trước khi xóa.
        NhanVien entity = getById(id);
        // Chỉ cần xuất hiện trong một trong hai loại chứng từ là phải giữ lại hồ sơ.
        if (hoaDonRepository.existsByMaNv(id) || phieuNhapRepository.existsByMaNv(id)) {
            throw new IllegalArgumentException("Nhân viên đã lập hóa đơn hoặc phiếu nhập, không thể xóa. Hãy chuyển sang trạng thái Đã nghỉ việc nếu cần.");
        }
        repository.delete(entity);
        // flush thực thi lệnh xóa để phát hiện lỗi ràng buộc ngay trong giao dịch này.
        repository.flush();
    }
}
