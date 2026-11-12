package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.LoaiMonForm;
import com.QuanLyNHHS.model.LoaiMon;
import com.QuanLyNHHS.repository.LoaiMonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Xử lý nghiệp vụ loại món, được Controller gọi trước khi truy cập database.
 * {@code @Service}: Spring tạo và quản lý đối tượng service.
 * {@code @Validated}: kiểm tra @NotNull, @Valid trên tham số khi gọi qua bean của Spring.
 * {@code @Transactional(readOnly = true)}: mặc định dùng giao dịch đọc;
 * các hàm save/delete có @Transactional riêng để được phép ghi dữ liệu.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class LoaiMonService {
    // Truy cập bảng LOAI_MON để đọc, lưu và xóa loại món.
    private final LoaiMonRepository repository;
    // Truy cập MON_AN để đếm món và kiểm tra loại món còn được sử dụng.
    private final MonAnRepository monAnRepository;

    /**
     * Spring truyền các repository vào constructor; this giữ chúng để các hàm dùng lại.
     * @param repository thành phần LoaiMonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param monAnRepository thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public LoaiMonService(LoaiMonRepository repository, MonAnRepository monAnRepository) {
        this.repository = repository;
        this.monAnRepository = monAnRepository;
    }
    /** @return danh sách toàn bộ loại món để Controller hiển thị và tính thống kê. */
    public List<LoaiMon> findAll() { return repository.findAll(); }
    /**
     * Tìm một loại món theo khóa chính.
     * @param id mã loại món lấy từ URL hoặc từ lời gọi của service khác
     * @return đối tượng tương ứng trong database
     * @throws EntityNotFoundException nếu mã không tồn tại
     */
    public LoaiMon getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại món."));
    }
    /**
     * Đổi kết quả truy vấn GROUP BY thành bảng tra cứu để giao diện lấy số món theo mã loại.
     * @return Map có key là mã loại món, value là số món thuộc loại đó
     */
    public Map<Long, Long> demMonTheoLoai() {
        // Mỗi row là Object[]: row[0] = mã loại (Long), row[1] = COUNT số món (Long).
        // Loại chưa có món không xuất hiện trong Map; giao diện hiển thị mặc định là 0.
        return monAnRepository.demMonTheoLoai().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    /**
     * Thêm mới hoặc cập nhật loại món trong một giao dịch.
     * @param id null khi thêm; có giá trị khi sửa một bản ghi đã tồn tại
     * @param form dữ liệu đã được DTO kiểm tra, không chứa khóa chính để ghi đè tùy ý
     * @return đối tượng đã lưu; bản ghi mới được cấp mã bằng sequence của entity
     */
    @Transactional
    public LoaiMon save(Long id, @NotNull @Valid LoaiMonForm form) {
        // entity là đối tượng sẽ lưu: tạo mới khi id null, lấy bản ghi cũ khi sửa.
        LoaiMon entity = id == null ? new LoaiMon() : getById(id);
        entity.setTenLoai(form.getTenLoai());
        entity.setMoTa(form.getMoTa());
        entity.setTrangThai(form.getTrangThai());
        // saveAndFlush gửi thay đổi xuống database ngay; commit diễn ra khi giao dịch kết thúc.
        return repository.saveAndFlush(entity);
    }
    /**
     * Xóa loại món sau khi kiểm tra các dữ liệu đang tham chiếu đến nó.
     * @param id mã loại món cần xóa
     * @throws EntityNotFoundException nếu bản ghi đã mất
     * @throws IllegalArgumentException nếu dữ liệu liên quan không cho phép xóa
     */
    @Transactional
    public void delete(Long id) {
        // entity: bản ghi hiện có trong database để kiểm tra điều kiện trước khi xóa.
        LoaiMon entity = getById(id);
        if (monAnRepository.existsByLoaiMon_MaLoai(id)) {
            throw new IllegalArgumentException("Loại món đang có món ăn. Hãy chuyển các món sang loại khác trước khi xóa.");
        }
        repository.delete(entity);
        // flush thực thi lệnh xóa để phát hiện lỗi ràng buộc ngay trong giao dịch này.
          repository.flush();
    }
}
