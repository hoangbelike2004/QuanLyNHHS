package com.QuanLyNHHS.service;

import com.QuanLyNHHS.model.BanAn;
import com.QuanLyNHHS.repository.BanAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ cho Bàn ăn
 * Module: Khách hàng & Đặt bàn - Hoàng Việt Đức
 */
@Service
public class BanAnService {

    // banAnRepository: thành phần BanAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired
    private BanAnRepository banAnRepository;

    /**
     * Lấy toàn bộ bàn ăn.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<BanAn> findAll() { return banAnRepository.findAll(); }

    /**
     * Tìm bàn ăn theo khóa chính; trả Optional rỗng nếu không có bản ghi.
     * @param maBan mã bàn ăn
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    public Optional<BanAn> findById(Long maBan) { return banAnRepository.findById(maBan); }

    /**
     * Lưu đối tượng bàn ăn qua repository.
     * @param banAn đối tượng bàn ăn liên quan
     * @return kết quả kiểu {@code BanAn} cho nơi gọi sử dụng
     */
    public BanAn save(BanAn banAn) { return banAnRepository.save(banAn); }

    /**
     * Xóa bàn ăn theo mã qua repository.
     * @param maBan mã bàn ăn
     */
    public void delete(Long maBan) { banAnRepository.deleteById(maBan); }

    /**
     * Kiểm tra có bàn ăn mang mã đã cho trong database hay không.
     * @param maBan mã bàn ăn
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    public boolean existsById(Long maBan) { return banAnRepository.existsById(maBan); }

    /**
     * Lấy các bàn ăn khớp trạng thái; tham số null/trống thì lấy tất cả.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<BanAn> findByTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return banAnRepository.findAll();
        return banAnRepository.findByTrangThai(trangThai.trim());
    }

    /**
     * Lấy bàn theo khu vực; nếu tham số null hoặc trống thì lấy toàn bộ bàn.
     * @param khuVuc khu vực bố trí bàn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<BanAn> findByKhuVuc(String khuVuc) {
        if (khuVuc == null || khuVuc.trim().isEmpty()) return banAnRepository.findAll();
        return banAnRepository.findByKhuVuc(khuVuc.trim());
    }

    /**
     * Danh sách bàn trống dùng cho form đặt bàn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<BanAn> findBanTrong() {
        return banAnRepository.findByTrangThai("Trống");
    }

    /**
     * Tìm bàn theo mã và cập nhật trạng thái nếu bản ghi tồn tại; không tìm thấy thì không thay đổi dữ liệu.
     * @param maBan mã bàn ăn
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     */
    public void capNhatTrangThai(Long maBan, String trangThai) {
        // opt: Optional chứa bản ghi tìm được; có thể rỗng nếu mã không tồn tại.
        Optional<BanAn> opt = banAnRepository.findById(maBan);
        opt.ifPresent(/* ban: bàn ăn đang được xét. */ ban -> {
            ban.setTrangThai(trangThai);
            banAnRepository.save(ban);
        });
    }
}
