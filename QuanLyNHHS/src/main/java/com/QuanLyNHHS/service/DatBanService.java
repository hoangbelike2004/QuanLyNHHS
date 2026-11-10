package com.QuanLyNHHS.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.DatBan;
import com.QuanLyNHHS.repository.BanAnRepository;
import com.QuanLyNHHS.repository.DatBanRepository;


/**
 * Xử lý nghiệp vụ đặt bàn và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
public class DatBanService {

    // datBanRepository: thành phần DatBanRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired
    private DatBanRepository datBanRepository;

    // banAnRepository: thành phần BanAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    @Autowired
    private BanAnRepository banAnRepository;

    /**
     * Lấy toàn bộ đặt bàn.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<DatBan> findAll() { return datBanRepository.findAll(); }

    /**
     * Tìm đặt bàn theo khóa chính; trả Optional rỗng nếu không có bản ghi.
     * @param maDat mã đặt bàn
     * @return Optional chứa bản ghi tìm được hoặc rỗng nếu không có
     */
    public Optional<DatBan> findById(Long maDat) { return datBanRepository.findById(maDat); }

    /**
     * Lưu đối tượng đặt bàn qua repository.
     * @param datBan thông tin đặt bàn đang xử lý
     * @return kết quả kiểu {@code DatBan} cho nơi gọi sử dụng
     */
    public DatBan save(DatBan datBan) { return datBanRepository.save(datBan); }

    /**
     * Xóa đặt bàn theo mã qua repository.
     * @param maDat mã đặt bàn
     */
    public void delete(Long maDat) { datBanRepository.deleteById(maDat); }

    /**
     * Kiểm tra có đặt bàn mang mã đã cho trong database hay không.
     * @param maDat mã đặt bàn
     * @return true nếu tìm thấy dữ liệu phù hợp, ngược lại false
     */
    public boolean existsById(Long maDat) { return datBanRepository.existsById(maDat); }

    /**
     * Lấy các đặt bàn khớp trạng thái; tham số null/trống thì lấy tất cả.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<DatBan> findByTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return datBanRepository.findAll();
        return datBanRepository.findByTrangThai(trangThai.trim());
    }

    /**
     * Lấy các lượt đặt bàn của một khách hàng.
     * @param maKh mã khách hàng
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<DatBan> findByKhachHang(Long maKh) {
        return datBanRepository.findByKhachHang_MaKh(maKh);
    }

    /**
     * Lấy các lượt đặt bàn gắn với một bàn ăn.
     * @param maBan mã bàn ăn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<DatBan> findByBanAn(Long maBan) {
        return datBanRepository.findByBanAn_MaBan(maBan);
    }

    /**
     * Xác nhận: DAT_BAN → DA_XAC_NHAN, BAN_AN → Đã đặt
     * @param maDat mã đặt bàn
     */
    @Transactional
    public void xacNhan(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(/* db: bản ghi đặt bàn đang xử lý. */ db -> {
            db.setTrangThai("DA_XAC_NHAN");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(/* ban: bàn ăn đang được xét. */ ban -> {
                    ban.setTrangThai("Đã đặt");
                    banAnRepository.save(ban);
                });
            }
        });
    }

    /**
     * Hủy: DAT_BAN → DA_HUY, BAN_AN → Trống
     * @param maDat mã đặt bàn
     */
    @Transactional
    public void huyDatBan(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(/* db: bản ghi đặt bàn đang xử lý. */ db -> {
            db.setTrangThai("DA_HUY");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(/* ban: bàn ăn đang được xét. */ ban -> {
                    ban.setTrangThai("Trống");
                    banAnRepository.save(ban);
                });
            }
        });
    }

    /**
     * Hoàn thành: DAT_BAN → HOAN_THANH, BAN_AN → Trống
     * @param maDat mã đặt bàn
     */
    @Transactional
    public void hoanThanh(Long maDat) {
        datBanRepository.findById(maDat).ifPresent(/* db: bản ghi đặt bàn đang xử lý. */ db -> {
            db.setTrangThai("HOAN_THANH");
            datBanRepository.save(db);
            if (db.getBanAn() != null) {
                banAnRepository.findById(db.getBanAn().getMaBan()).ifPresent(/* ban: bàn ăn đang được xét. */ ban -> {
                    ban.setTrangThai("Trống");
                    banAnRepository.save(ban);
                });
            }
        });
    }
}
