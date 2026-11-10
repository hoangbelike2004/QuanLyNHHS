
package com.QuanLyNHHS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.QuanLyNHHS.repository.CTHoaDonRepository;
import com.QuanLyNHHS.repository.HoaDonRepository;

/**
 * Xử lý nghiệp vụ báo cáo và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
public class BaoCaoService {

    // DA_THANH_TOAN: giá trị trạng thái Da thanh toan dùng khi thanh toán và thống kê.
    private static final String DA_THANH_TOAN = "Da thanh toan";

    // hoaDonRepository: thành phần HoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final HoaDonRepository hoaDonRepository;
    // ctHoaDonRepository: thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final CTHoaDonRepository ctHoaDonRepository;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param hoaDonRepository thành phần HoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param ctHoaDonRepository thành phần CTHoaDonRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     */
    public BaoCaoService(
            HoaDonRepository hoaDonRepository,
            CTHoaDonRepository ctHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.ctHoaDonRepository = ctHoaDonRepository;
    }

    /**
     * Cộng doanh thu hóa đơn đã thanh toán từ tuNgay đến hết denNgay; truy vấn dùng denNgay cộng 1 ngày làm cận trên không bao gồm.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    public BigDecimal doanhThuTheoNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        kiemTraNgay(tuNgay, denNgay);

        // doanhThu: tổng doanh thu lấy từ hóa đơn đã thanh toán.
        BigDecimal doanhThu = hoaDonRepository.tinhDoanhThu(
                DA_THANH_TOAN,
                tuNgay,
                denNgay.plusDays(1));

        return doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }

    /**
     * Gọi thống kê doanh thu với ngày bắt đầu và kết thúc đều là hôm nay.
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    public BigDecimal doanhThuHomNay() {
        // homNay: ngày hiện tại theo đồng hồ của máy chạy ứng dụng.
        LocalDate homNay = LocalDate.now();
        return doanhThuTheoNgay(homNay, homNay);
    }

    /**
     * Tính doanh thu từ đầu tháng hiện tại đến hết hôm nay.
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    public BigDecimal doanhThuThangNay() {
        // homNay: ngày hiện tại theo đồng hồ của máy chạy ứng dụng.
        LocalDate homNay = LocalDate.now();
        // dauThang: ngày đầu tháng hiện tại.
        LocalDate dauThang = homNay.withDayOfMonth(1);

        return doanhThuTheoNgay(dauThang, homNay);
    }

    /**
     * Đếm số hóa đơn đang có trạng thái Da thanh toan.
     * @return kết quả kiểu {@code long} cho nơi gọi sử dụng
     */
    public long soHoaDonDaThanhToan() {
        return hoaDonRepository.countByTrangThai(DA_THANH_TOAN);
    }

    /**
     * Thống kê món theo trạng thái hóa đơn, sắp tổng lượng bán giảm dần; mỗi dòng gồm tên món, tổng số lượng, tổng thành tiền.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<Object[]> thongKeMonBanChay() {
        return ctHoaDonRepository.thongKeMonBanChay(DA_THANH_TOAN);
    }

    /**
     * Gom nhóm chi tiết theo hóa đơn; mỗi dòng gồm mã hóa đơn và tổng thành tiền chi tiết, chưa trừ giảm giá ở hóa đơn.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<Object[]> thongKeTongTienTheoHoaDon() {
        return ctHoaDonRepository.thongKeTongTienTheoHoaDon(DA_THANH_TOAN);
    }

    /**
     * Từ chối khoảng thống kê thiếu ngày hoặc có ngày kết thúc trước ngày bắt đầu.
     * @param tuNgay ngày bắt đầu khoảng thống kê
     * @param denNgay ngày kết thúc khoảng thống kê
     */
    private void kiemTraNgay(
            LocalDate tuNgay,
            LocalDate denNgay) {

        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException(
                    "Vui long chon ngay bat dau va ngay ket thuc");
        }

        if (denNgay.isBefore(tuNgay)) {
            throw new IllegalArgumentException(
                    "Ngay ket thuc khong duoc truoc ngay bat dau");
        }
    }
}
