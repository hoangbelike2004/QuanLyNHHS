
package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.QuanLyNHHS.model.CTHoaDon;
import com.QuanLyNHHS.model.HoaDon;
import com.QuanLyNHHS.model.MonAn;
import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;
import com.QuanLyNHHS.repository.MonAnRepository;

/**
 * Xử lý nghiệp vụ hóa đơn và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
public class HoaDonService {

    // CHUA_THANH_TOAN: giá trị trạng thái Chua thanh toan lưu trong database.
    private static final String CHUA_THANH_TOAN = "Chua thanh toan";
    // DA_THANH_TOAN: giá trị trạng thái Da thanh toan dùng khi thanh toán và thống kê.
    private static final String DA_THANH_TOAN = "Da thanh toan";

    // hoaDonRepository: thành phần HoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final HoaDonRepository hoaDonRepository;
    // ctHoaDonRepository: thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final CTHoaDonRepository ctHoaDonRepository;
    // monAnRepository: thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final MonAnRepository monAnRepository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param hoaDonRepository thành phần HoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param ctHoaDonRepository thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param monAnRepository thành phần MonAnRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public HoaDonService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository,
            MonAnRepository monAnRepository) {

        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
        this.monAnRepository = monAnRepository;
    }

    // Lấy tất cả hóa đơn
    /**
     * Lấy các hóa đơn sắp theo ngày lập giảm dần.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonRepository.findAllByOrderByNgayLapDesc();
    }

    // Tìm hóa đơn theo trạng thái
    /**
     * Lấy các hóa đơn có trạng thái được yêu cầu.
     * @param trangThai trạng thái dùng để hiển thị, lọc hoặc kiểm tra điều kiện nghiệp vụ
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<HoaDon> timTheoTrangThai(String trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    // Tìm hóa đơn theo mã
    /**
     * Tìm hóa đơn theo mã; không tồn tại thì ném IllegalArgumentException.
     * @param maHd mã hóa đơn
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    public HoaDon timTheoMa(Long maHd) {
        return hoaDonRepository.findById(maHd)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Khong tim thay hoa don: " + maHd));
    }

    // Lấy chi tiết hóa đơn
    /**
     * Kiểm tra hóa đơn tồn tại trước khi lấy các dòng món ăn theo mã hóa đơn.
     * @param maHd mã hóa đơn
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<CTHoaDon> layChiTietHoaDon(Long maHd) {
        timTheoMa(maHd);
        return ctHoaDonRepository.findById_MaHd(maHd);
    }

    // Tạo hóa đơn và lưu chi tiết món ăn
    /**
     * Tạo hóa đơn chưa thanh toán, gộp các dòng trùng món, lưu chi tiết với đơn giá hiện tại rồi tính tổng tiền.
     * @param hoaDon hóa đơn đang xử lý
     * @param maMon danh sách mã món từ form; phần tử thứ i đi cùng soLuong[i]
     * @param soLuong danh sách số lượng từ form; phần tử thứ i đi cùng maMon[i]
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    @Transactional
    public HoaDon taoHoaDon(
            HoaDon hoaDon,
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        if (hoaDon.getMaBan() == null || hoaDon.getMaNv() == null) {
            throw new IllegalArgumentException(
                    "Ma ban va ma nhan vien khong duoc de trong");
        }

        // danhSachMon: Map mã món -> tổng số lượng; các dòng trùng món được cộng lại.
        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        hoaDon.setNgayLap(LocalDate.now());
        hoaDon.setTrangThai(CHUA_THANH_TOAN);
        hoaDon.setTongTien(BigDecimal.ZERO);

        if (hoaDon.getGiamGia() == null) {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        kiemTraGiamGia(hoaDon.getGiamGia());

        // hoaDonMoi: hóa đơn mới tạo hoặc dữ liệu hóa đơn mới nhận từ form.
        HoaDon hoaDonMoi = hoaDonRepository.save(hoaDon);

        luuChiTietHoaDon(hoaDonMoi.getMaHd(), danhSachMon);

        return tinhLaiTongTien(hoaDonMoi.getMaHd());
    }

    // Cập nhật thông tin hóa đơn (giữ phương thức cũ)
    /**
     * Chỉ cập nhật hóa đơn chưa thanh toán; kiểm tra thông tin, xử lý giảm giá và tính lại tổng tiền.
     * @param hoaDonMoi hóa đơn mới tạo hoặc dữ liệu hóa đơn mới nhận từ form
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    @Transactional
    public HoaDon capNhatHoaDon(HoaDon hoaDonMoi) {

        // hoaDon: hóa đơn đang xử lý.
        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        kiemTraChuaThanhToan(hoaDon);

        kiemTraThongTinHoaDon(hoaDonMoi);

        hoaDon.setMaBan(hoaDonMoi.getMaBan());
        hoaDon.setMaKh(hoaDonMoi.getMaKh());
        hoaDon.setMaNv(hoaDonMoi.getMaNv());

        if (hoaDonMoi.getGiamGia() != null) {
            kiemTraGiamGia(hoaDonMoi.getGiamGia());
            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        }

        hoaDonRepository.save(hoaDon);

        return tinhLaiTongTien(hoaDon.getMaHd());
    }

    // Cập nhật hóa đơn và danh sách món ăn
    /**
     * Chỉ sửa hóa đơn chưa thanh toán; kiểm tra và gộp món, cập nhật thông tin, thay toàn bộ chi tiết rồi tính lại tổng tiền.
     * @param hoaDonMoi hóa đơn mới tạo hoặc dữ liệu hóa đơn mới nhận từ form
     * @param maMon danh sách mã món từ form; phần tử thứ i đi cùng soLuong[i]
     * @param soLuong danh sách số lượng từ form; phần tử thứ i đi cùng maMon[i]
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    @Transactional
    public HoaDon capNhatHoaDon(
            HoaDon hoaDonMoi,
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        // hoaDon: hóa đơn đang xử lý.
        HoaDon hoaDon = timTheoMa(hoaDonMoi.getMaHd());

        kiemTraChuaThanhToan(hoaDon);

        kiemTraThongTinHoaDon(hoaDonMoi);

        // Kiểm tra danh sách món trước khi thay đổi dữ liệu
        // danhSachMon: Map mã món -> tổng số lượng; các dòng trùng món được cộng lại.
        Map<Long, BigDecimal> danhSachMon =
                kiemTraVaGopDanhSachMon(maMon, soLuong);

        // Cập nhật thông tin hóa đơn
        hoaDon.setMaBan(hoaDonMoi.getMaBan());
        hoaDon.setMaKh(hoaDonMoi.getMaKh());
        hoaDon.setMaNv(hoaDonMoi.getMaNv());

        if (hoaDonMoi.getGiamGia() != null) {
            kiemTraGiamGia(hoaDonMoi.getGiamGia());
            hoaDon.setGiamGia(hoaDonMoi.getGiamGia());
        } else {
            hoaDon.setGiamGia(BigDecimal.ZERO);
        }

        hoaDonRepository.save(hoaDon);

        // Xóa chi tiết món ăn cũ
        ctHoaDonRepository.deleteById_MaHd(hoaDon.getMaHd());

        // Lưu lại danh sách món ăn mới
        luuChiTietHoaDon(hoaDon.getMaHd(), danhSachMon);

        // Tính lại tổng tiền
        return tinhLaiTongTien(hoaDon.getMaHd());
    }

    // Xóa hóa đơn
    /**
     * Chỉ xóa hóa đơn chưa thanh toán; xóa chi tiết trước rồi xóa hóa đơn.
     * @param maHd mã hóa đơn
     */
    @Transactional
    public void xoaHoaDon(Long maHd) {

        // hoaDon: hóa đơn đang xử lý.
        HoaDon hoaDon = timTheoMa(maHd);

        kiemTraChuaThanhToan(hoaDon);

        ctHoaDonRepository.deleteById_MaHd(maHd);

        hoaDonRepository.delete(hoaDon);
    }

    // Tính lại tổng tiền
    /**
     * Cộng thành tiền chi tiết rồi trừ số tiền giảm giá; nếu kết quả âm thì đưa về 0 và lưu lại hóa đơn.
     * @param maHd mã hóa đơn
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    @Transactional
    public HoaDon tinhLaiTongTien(Long maHd) {

        // hoaDon: hóa đơn đang xử lý.
        HoaDon hoaDon = timTheoMa(maHd);

        // tongChiTiet: tổng thành tiền các dòng chi tiết hóa đơn.
        BigDecimal tongChiTiet =
                ctHoaDonRepository.tinhTongTienTheoHoaDon(maHd);

        if (tongChiTiet == null) {
            tongChiTiet = BigDecimal.ZERO;
        }

        // giamGia: số tiền giảm trực tiếp, không phải phần trăm.
        BigDecimal giamGia = hoaDon.getGiamGia() == null
                ? BigDecimal.ZERO
                : hoaDon.getGiamGia();

        // tongSauGiam: tổng tiền sau khi trừ giảm giá, được chặn tối thiểu bằng 0.
        BigDecimal tongSauGiam =
                tongChiTiet.subtract(giamGia);

        if (tongSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongSauGiam = BigDecimal.ZERO;
        }

        hoaDon.setTongTien(tongSauGiam);

        return hoaDonRepository.save(hoaDon);
    }

    // Thanh toán hóa đơn
    /**
     * Từ chối hóa đơn đã thanh toán hoặc chưa có món; tính lại tổng tiền rồi đổi trạng thái sang Da thanh toan.
     * @param maHd mã hóa đơn
     * @return kết quả kiểu {@code HoaDon} cho nơi gọi sử dụng
     */
    @Transactional
    public HoaDon thanhToan(Long maHd) {

        // hoaDon: hóa đơn đang xử lý.
        HoaDon hoaDon = timTheoMa(maHd);

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Hoa don da duoc thanh toan");
        }

        if (ctHoaDonRepository.countById_MaHd(maHd) == 0) {
            throw new IllegalStateException(
                    "Hoa don chua co mon an");
        }

        tinhLaiTongTien(maHd);

        hoaDon.setTrangThai(DA_THANH_TOAN);

        return hoaDonRepository.save(hoaDon);
    }

    // Tính doanh thu theo khoảng ngày
    /**
     * Kiểm tra khoảng ngày rồi cộng doanh thu hóa đơn đã thanh toán từ tuNgay đến hết denNgay, dùng denNgay + 1 làm cận trên.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    public BigDecimal tinhDoanhThu(
            LocalDate tuNgay,
            LocalDate denNgay) {

        kiemTraKhoangNgay(tuNgay, denNgay);

        return hoaDonRepository.tinhDoanhThu(
                DA_THANH_TOAN,
                tuNgay,
                denNgay.plusDays(1));
    }

    // Kiểm tra và gộp danh sách món ăn
    /**
     * Kiểm tra hai danh sách mã món/số lượng cùng kích thước, có dữ liệu và số lượng dương; cộng lượng của các món trùng mã vào Map.
     * @param maMon danh sách mã món từ form; phần tử thứ i đi cùng soLuong[i]
     * @param soLuong danh sách số lượng từ form; phần tử thứ i đi cùng maMon[i]
     * @return Map dùng tra cứu theo khóa; ý nghĩa key/value được mô tả trong hàm
     */
    private Map<Long, BigDecimal> kiemTraVaGopDanhSachMon(
            List<Long> maMon,
            List<BigDecimal> soLuong) {

        if (maMon == null || soLuong == null
                || maMon.isEmpty()
                || maMon.size() != soLuong.size()) {

            throw new IllegalArgumentException(
                    "Hoa don phai co it nhat mot mon an hop le");
        }

        // danhSachMon: Map mã món -> tổng số lượng; các dòng trùng món được cộng lại.
        Map<Long, BigDecimal> danhSachMon =
                new LinkedHashMap<>();

        for (/* i: chỉ số phần tử đang xét trong danh sách. */ int i = 0; i < maMon.size(); i++) {

            // idMon: mã món tại vị trí đang xét.
            Long idMon = maMon.get(i);
            // soLuongMon: số lượng của món đang xét.
            BigDecimal soLuongMon = soLuong.get(i);

            if (idMon == null || soLuongMon == null
                    || soLuongMon.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Ma mon va so luong phai hop le");
            }

            danhSachMon.merge(
                    idMon,
                    soLuongMon,
                    BigDecimal::add);
        }

        return danhSachMon;
    }

    // Lưu chi tiết món ăn vào CT_HOA_DON
    /**
     * Với mỗi mã món, lấy đơn giá hiện tại từ MON_AN, tính thành tiền và lưu một dòng CT_HOA_DON bằng khóa kép mã hóa đơn/mã món.
     * @param maHd mã hóa đơn
     * @param danhSachMon Map mã món -> tổng số lượng; các dòng trùng món được cộng lại
     */
    private void luuChiTietHoaDon(
            Long maHd,
            Map<Long, BigDecimal> danhSachMon) {

        for (/* entry: cặp mã món (key) và tổng số lượng món (value). */ Map.Entry<Long, BigDecimal> entry
                : danhSachMon.entrySet()) {

            // idMon: mã món tại vị trí đang xét.
            Long idMon = entry.getKey();
            // soLuongMon: số lượng của món đang xét.
            BigDecimal soLuongMon = entry.getValue();

            // monAn: món ăn đọc từ database để lấy đơn giá.
            MonAn monAn = monAnRepository.findById(idMon)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Khong tim thay mon an co ma: " + idMon));

            if (monAn.getDonGia() == null) {
                throw new IllegalArgumentException(
                        "Mon an chua co don gia: " + idMon);
            }

            // donGia: đơn giá của một đơn vị.
            BigDecimal donGia = monAn.getDonGia();
            // thanhTien: thành tiền của dòng chi tiết.
            BigDecimal thanhTien = donGia.multiply(soLuongMon);

            // chiTiet: dòng chi tiết hoặc danh sách chi tiết, tùy kiểu dữ liệu khai báo.
            CTHoaDon chiTiet = new CTHoaDon();

            chiTiet.setId(
                    new CTHoaDon.CTHoaDonId(maHd, idMon));

            chiTiet.setSoLuong(soLuongMon);
            chiTiet.setDonGia(donGia);
            chiTiet.setThanhTien(thanhTien);

            ctHoaDonRepository.save(chiTiet);
        }
    }

    // Kiểm tra hóa đơn chưa thanh toán
    /**
     * Ném IllegalStateException nếu hóa đơn đã thanh toán để chặn sửa hoặc xóa.
     * @param hoaDon hóa đơn đang xử lý
     */
    private void kiemTraChuaThanhToan(HoaDon hoaDon) {

        if (DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException(
                    "Khong the sua hoac xoa hoa don da thanh toan");
        }
    }

    // Kiểm tra thông tin hóa đơn
    /**
     * Kiểm tra hóa đơn đã có mã bàn và mã nhân viên; hàm này không tra sự tồn tại của hai mã trong database.
     * @param hoaDon hóa đơn đang xử lý
     */
    private void kiemTraThongTinHoaDon(HoaDon hoaDon) {

        if (hoaDon.getMaBan() == null || hoaDon.getMaNv() == null) {
            throw new IllegalArgumentException(
                    "Ma ban va ma nhan vien khong duoc de trong");
        }
    }

    // Kiểm tra giảm giá
    /**
     * Từ chối số tiền giảm giá null hoặc âm.
     * @param giamGia số tiền giảm trực tiếp, không phải phần trăm
     */
    private void kiemTraGiamGia(BigDecimal giamGia) {

        if (giamGia == null
                || giamGia.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Giam gia khong duoc am");
        }
    }

    // Kiểm tra khoảng ngày
    /**
     * Từ chối ngày bắt đầu/kết thúc bị thiếu hoặc ngày kết thúc trước ngày bắt đầu.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     */
    private void kiemTraKhoangNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException(
                    "Ngay bat dau va ngay ket thuc khong duoc trong");
        }

        if (denNgay.isBefore(tuNgay)) {
            throw new IllegalArgumentException(
                    "Ngay ket thuc phai sau ngay bat dau");
        }
    }
}
