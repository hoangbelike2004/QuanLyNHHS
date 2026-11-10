package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.MonAnForm;
import com.QuanLyNHHS.model.LoaiMon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.LoaiMonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Xử lý nghiệp vụ món ăn, được Controller gọi trước khi truy cập database.
 * {@code @Service}: Spring tạo và quản lý đối tượng service.
 * {@code @Validated}: kiểm tra @NotNull, @Valid trên tham số khi gọi qua bean của Spring.
 * {@code @Transactional(readOnly = true)}: mặc định dùng giao dịch đọc;
 * các hàm save/delete có @Transactional riêng để được phép ghi dữ liệu.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class MonAnService {
    // Truy cập bảng MON_AN để đọc, lưu và xóa món ăn.
    private final MonAnRepository repository;
    // Tìm loại món được chọn và kiểm tra khóa ngoại MA_LOAI.
    // loaiMonRepository: thành phần LoaiMonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final LoaiMonRepository loaiMonRepository;
    // Đọc chi tiết hóa đơn để thống kê lượng bán và kiểm tra món đã được sử dụng.
    // ctHoaDonRepository: thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final CTHoaDonRepository ctHoaDonRepository;

    /**
     * Spring truyền các repository vào constructor; this giữ chúng để các hàm dùng lại.
     * @param repository thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param loaiMonRepository thành phần LoaiMonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param ctHoaDonRepository thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public MonAnService(MonAnRepository repository, LoaiMonRepository loaiMonRepository,
                        CTHoaDonRepository ctHoaDonRepository) {
        this.repository = repository;
        this.loaiMonRepository = loaiMonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
    }
    /** @return danh sách toàn bộ món ăn để Controller hiển thị và tính thống kê. */
    public List<MonAn> findAll() { return repository.findAll(); }
    /**
     * Tìm một món ăn theo khóa chính.
     * @param id mã món ăn lấy từ URL hoặc từ lời gọi của service khác
     * @return đối tượng tương ứng trong database
     * @throws EntityNotFoundException nếu mã không tồn tại
     */
    public MonAn getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy món ăn."));
    }
    /**
     * Tổng hợp lượng bán từ các hóa đơn đã thanh toán theo truy vấn của repository.
     * @return Map có key là mã món, value là tổng số lượng bán (có thể có phần thập phân)
     */
    public Map<Long, BigDecimal> tongSoLuongDaBan() {
        // row[0] là mã món (Long); row[1] là SUM số lượng (BigDecimal).
        return ctHoaDonRepository.tongSoLuongDaBanTheoMon().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (BigDecimal) row[1]));
    }
    /**
     * Thêm mới hoặc cập nhật món ăn trong một giao dịch.
     * @param id null khi thêm; có giá trị khi sửa một bản ghi đã tồn tại
     * @param form dữ liệu đã được DTO kiểm tra, không chứa khóa chính để ghi đè tùy ý
     * @return đối tượng đã lưu; bản ghi mới được cấp mã bằng sequence của entity
     */
    @Transactional
    public MonAn save(Long id, @NotNull @Valid MonAnForm form) {
        // entity là đối tượng sẽ lưu: tạo mới khi id null, lấy bản ghi cũ khi sửa.
        MonAn entity = id == null ? new MonAn() : getById(id);
        // loaiMon là loại được chọn trên form; phải lấy bản ghi thật để gắn quan hệ @ManyToOne.
        // loaiMon: loại món được gắn với món ăn.
        LoaiMon loaiMon = loaiMonRepository.findById(form.getMaLoai())
                .orElseThrow(() -> new IllegalArgumentException("Loại món đã bị xóa hoặc không tồn tại. Hãy chọn lại."));
        entity.setLoaiMon(loaiMon);
        entity.setTenMon(form.getTenMon());
        entity.setDonVi(form.getDonVi());
        entity.setDonGia(form.getDonGia());
        entity.setTrangThai(form.getTrangThai());
        entity.setHinhAnh(form.getHinhAnh());
        entity.setMoTa(form.getMoTa());
        // saveAndFlush gửi thay đổi xuống database ngay; commit diễn ra khi giao dịch kết thúc.
        return repository.saveAndFlush(entity);
    }
    /**
     * Xóa món ăn sau khi kiểm tra các dữ liệu đang tham chiếu đến nó.
     * @param id mã món ăn cần xóa
     * @throws EntityNotFoundException nếu bản ghi đã mất
     * @throws IllegalArgumentException nếu dữ liệu liên quan không cho phép xóa
     */
    @Transactional
    public void delete(Long id) {
        // entity: bản ghi hiện có trong database để kiểm tra điều kiện trước khi xóa.
        MonAn entity = getById(id);
        if (ctHoaDonRepository.existsById_MaMon(id)) {
            throw new IllegalArgumentException("Món đã có trong hóa đơn, không thể xóa. Bạn có thể đổi trạng thái thành Tạm hết.");
        }
        repository.delete(entity);
        // flush thực thi lệnh xóa để phát hiện lỗi ràng buộc ngay trong giao dịch này.
        repository.flush();
    }
}
