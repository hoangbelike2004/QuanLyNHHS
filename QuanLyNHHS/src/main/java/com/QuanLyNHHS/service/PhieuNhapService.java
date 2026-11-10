package com.QuanLyNHHS.service;

import com.QuanLyNHHS.dto.*;
import com.QuanLyNHHS.model.*;
import com.QuanLyNHHS.repository.*;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Xử lý nghiệp vụ phiếu nhập và phối hợp các repository trước khi trả kết quả cho controller.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class PhieuNhapService {
    // MAX_STOCK: giới hạn tồn kho phù hợp cột NUMBER(10,2).
    private static final BigDecimal MAX_STOCK = new BigDecimal("99999999.99");
    // MAX_MONEY: giới hạn tổng tiền/thành tiền phù hợp cột NUMBER(14,2).
    private static final BigDecimal MAX_MONEY = new BigDecimal("999999999999.99");
    // repository: thành phần PhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final PhieuNhapRepository repository;
    // nhaCungCapRepository: thành phần NhaCungCapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final NhaCungCapRepository nhaCungCapRepository;
    // nguyenLieuRepository: thành phần NguyenLieuRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai.
    private final NguyenLieuRepository nguyenLieuRepository;
    // nhanVienRepository: thành phần NhanVienNhapKhoRepository chỉ đọc và kiểm tra mã nhân viên bằng JDBC.
    private final NhanVienNhapKhoRepository nhanVienRepository;
    // entityManager: thành phần JPA quản lý entity, khóa bản ghi, nạp lại dữ liệu và giao dịch.
    private final EntityManager entityManager;

    /**
     * Nhận các thành phần phụ thuộc qua constructor và gán vào thuộc tính this để các hàm dùng lại.
     * @param repository thành phần PhieuNhapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param nhaCungCapRepository thành phần NhaCungCapRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param nguyenLieuRepository thành phần NguyenLieuRepository dùng đọc/ghi và truy vấn dữ liệu; Spring cung cấp đối tượng triển khai
     * @param nhanVienRepository thành phần NhanVienNhapKhoRepository chỉ đọc và kiểm tra mã nhân viên bằng JDBC
     * @param entityManager thành phần JPA quản lý entity, khóa bản ghi, nạp lại dữ liệu và giao dịch
     */
    public PhieuNhapService(
            PhieuNhapRepository repository,
            NhaCungCapRepository nhaCungCapRepository,
            NguyenLieuRepository nguyenLieuRepository,
            NhanVienNhapKhoRepository nhanVienRepository,
            EntityManager entityManager) {
        this.repository = repository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.entityManager = entityManager;
    }

    /**
     * Lấy toàn bộ phiếu nhập.
     * @return danh sách kết quả, có thể rỗng khi không có dữ liệu phù hợp
     */
    public List<PhieuNhap> findAll() {
        return repository.findAllByOrderByNgayNhapDescMaPnDesc();
    }

    /**
     * Tìm phiếu nhập theo khóa chính; không tìm thấy thì ném EntityNotFoundException.
     * @param id khóa chính của phiếu nhập
     * @return kết quả kiểu {@code PhieuNhap} cho nơi gọi sử dụng
     */
    public PhieuNhap findById(Long id) {
        return repository
                .findDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    /**
     * Đổi phiếu nhập và các chi tiết thành DTO điền form sửa, kèm phiên bản tại thời điểm đọc.
     * @param id khóa chính của phiếu nhập
     * @return DTO PhieuNhapForm gồm thông tin phiếu, phiên bản và các dòng DongNhapForm
     */
    public PhieuNhapForm getForm(Long id) {
        // receipt: phiếu nhập đang được đọc, tạo hoặc chỉnh sửa.
        PhieuNhap receipt = findById(id);
        // form: đối tượng PhieuNhapForm chứa dữ liệu nhập của biểu mẫu.
        PhieuNhapForm form = new PhieuNhapForm();
        form.setMaNcc(receipt.getNhaCungCap().getMaNcc());
        form.setMaNv(receipt.getMaNv());
        form.setNgayNhap(receipt.getNgayNhap());
        form.setPhienBan(receipt.getPhienBan());
        for (/* detail: dòng chi tiết đang được xử lý. */ CTPhieuNhap detail : receipt.getChiTiet()) {
            // line: dữ liệu một dòng nhập gồm nguyên liệu, số lượng và đơn giá.
            DongNhapForm line = new DongNhapForm();
            line.setMaNl(detail.getNguyenLieu().getMaNl());
            line.setSoLuong(detail.getSoLuong());
            line.setDonGia(detail.getDonGia());
            form.getChiTiet().add(line);
        }
        return form;
    }

    /**
     * Lập/sửa phiếu nhập trong một giao dịch: kiểm tra phiên bản và danh mục, khóa nguyên liệu, điều chỉnh tồn theo lượng mới trừ cũ, tính tổng rồi lưu chi tiết.
     * @param id khóa chính của phiếu nhập (null để thêm mới, có giá trị để sửa)
     * @param form đối tượng PhieuNhapForm chứa dữ liệu nhập của biểu mẫu; được kiểm tra ràng buộc bằng @Valid
     * @return kết quả kiểu {@code PhieuNhap} cho nơi gọi sử dụng
     */
    @Transactional
    public PhieuNhap save(Long id, @NotNull @Valid PhieuNhapForm form) {
        // receipt: phiếu nhập đang được đọc, tạo hoặc chỉnh sửa.
        PhieuNhap receipt = id == null ? new PhieuNhap() : lock(id);
        if (id != null) {
            checkVersion(receipt, form.getPhienBan());
            // Child-only edits must also invalidate stale receipt forms.
            entityManager.lock(receipt, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }
        // supplier: nhà cung cấp được chọn hoặc đang chỉnh sửa.
        NhaCungCap supplier =
                nhaCungCapRepository
                        .findForUpdate(form.getMaNcc())
                        .orElseThrow(
                                () -> new IllegalArgumentException("Nhà cung cấp không tồn tại."));
        if (!nhanVienRepository.existsById(form.getMaNv())) {
            throw new IllegalArgumentException("Nhân viên nhập kho không tồn tại.");
        }

        // requestedLines: Map mã nguyên liệu -> dòng nhập người dùng yêu cầu, sắp theo mã và không trùng nguyên liệu.
        Map<Long, DongNhapForm> requestedLines = new TreeMap<>();
        // total: tổng thành tiền của phiếu nhập, cộng từ các dòng đã làm tròn.
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (/* line: dữ liệu một dòng nhập gồm nguyên liệu, số lượng và đơn giá. */ DongNhapForm line : form.getChiTiet()) {
            if (line == null) throw new IllegalArgumentException("Dòng nhập không hợp lệ.");
            if (requestedLines.putIfAbsent(line.getMaNl(), line) != null) {
                throw new IllegalArgumentException(
                        "Mỗi nguyên liệu chỉ được xuất hiện một lần trong phiếu nhập.");
            }
            total = total.add(lineAmount(line));
        }
        if (total.compareTo(MAX_MONEY) > 0)
            throw new IllegalArgumentException("Tổng tiền vượt giới hạn NUMBER(14,2).");

        // existingDetails: Map mã nguyên liệu -> dòng chi tiết đã có trong phiếu cũ.
        Map<Long, CTPhieuNhap> existingDetails = new HashMap<>();
        for (/* detail: dòng chi tiết đang được xử lý. */ CTPhieuNhap detail : receipt.getChiTiet())
            existingDetails.put(detail.getNguyenLieu().getMaNl(), detail);
        // ids: tập mã nguyên liệu thuộc cả phiếu cũ và yêu cầu mới, được sắp để khóa nhất quán.
        Set<Long> ids = new TreeSet<>(existingDetails.keySet());
        ids.addAll(requestedLines.keySet());
        // ingredients: Map mã nguyên liệu -> entity nguyên liệu đã được khóa.
        Map<Long, NguyenLieu> ingredients = lockMaterials(ids);
        for (/* materialId: mã nguyên liệu đang được xử lý. */ Long materialId : ids) {
            // ingredient: nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn.
            NguyenLieu ingredient = ingredients.get(materialId);
            // line: dữ liệu một dòng nhập gồm nguyên liệu, số lượng và đơn giá.
            DongNhapForm line = requestedLines.get(materialId);
            // previousDetail: dòng chi tiết trước khi sửa; null nếu đây là nguyên liệu mới của phiếu.
            CTPhieuNhap previousDetail = existingDetails.get(materialId);
            // before: số lượng nhập trước khi sửa; bằng 0 nếu chưa có dòng cũ.
            BigDecimal before =
                    previousDetail == null ? BigDecimal.ZERO : previousDetail.getSoLuong();
            // after: số lượng nhập sau khi sửa; bằng 0 nếu dòng đã bị loại bỏ.
            BigDecimal after = line == null ? BigDecimal.ZERO : line.getSoLuong();
            if (line != null
                    && !"HOAT_DONG".equals(ingredient.getTrangThai())
                    && (previousDetail == null || after.compareTo(before) > 0)) {
                throw new IllegalArgumentException(
                        "Nguyên liệu " + ingredient.getTenNl() + " đã ngừng sử dụng.");
            }
            adjustStock(ingredient, after.subtract(before));
        }

        receipt.setNhaCungCap(supplier);
        receipt.setMaNv(form.getMaNv());
        receipt.setNgayNhap(form.getNgayNhap());
        receipt.setTongTien(total);
        // Keep existing composite keys in place instead of deleting and reinserting them.
        receipt.getChiTiet()
                .removeIf(/* detail: dòng chi tiết đang được xử lý. */ detail -> !requestedLines.containsKey(detail.getNguyenLieu().getMaNl()));
        for (/* entry: cặp mã nguyên liệu (key) và DongNhapForm được yêu cầu (value). */ Map.Entry<Long, DongNhapForm> entry : requestedLines.entrySet()) {
            // detail: dòng chi tiết đang được xử lý.
            CTPhieuNhap detail = existingDetails.get(entry.getKey());
            if (detail == null) {
                detail = new CTPhieuNhap();
                detail.setPhieuNhap(receipt);
                detail.setNguyenLieu(ingredients.get(entry.getKey()));
                receipt.getChiTiet().add(detail);
            }
            detail.setSoLuong(entry.getValue().getSoLuong());
            detail.setDonGia(entry.getValue().getDonGia());
            detail.setThanhTien(lineAmount(entry.getValue()));
        }
        return repository.saveAndFlush(receipt);
    }

    /**
     * Khóa và kiểm tra phiên bản phiếu, trừ lại tồn kho theo các dòng đã nhập rồi xóa phiếu; lỗi sẽ rollback giao dịch.
     * @param id khóa chính của phiếu nhập
     * @param phienBan số phiên bản để phát hiện form cũ khi sửa hoặc xóa phiếu nhập
     */
    @Transactional
    public void delete(Long id, Long phienBan) {
        // receipt: phiếu nhập đang được đọc, tạo hoặc chỉnh sửa.
        PhieuNhap receipt = lock(id);
        checkVersion(receipt, phienBan);
        // quantities: Map mã nguyên liệu -> số lượng đã nhập cần trừ lại khi xóa phiếu.
        Map<Long, BigDecimal> quantities = new TreeMap<>();
        for (/* detail: dòng chi tiết đang được xử lý. */ CTPhieuNhap detail : receipt.getChiTiet())
            quantities.put(detail.getNguyenLieu().getMaNl(), detail.getSoLuong());
        // ingredients: Map mã nguyên liệu -> entity nguyên liệu đã được khóa.
        Map<Long, NguyenLieu> ingredients = lockMaterials(quantities.keySet());
        quantities.forEach((/* materialId: mã nguyên liệu đang được xử lý. */ materialId, /* quantity: số lượng nhập của nguyên liệu đang xét. */ quantity) ->
                adjustStock(ingredients.get(materialId), quantity.negate()));
        repository.delete(receipt);
        repository.flush();
    }

    /**
     * Tìm phiếu nhập theo mã đồng thời lấy khóa ghi; không tìm thấy thì ném EntityNotFoundException.
     * @param id khóa chính của phiếu nhập
     * @return kết quả kiểu {@code PhieuNhap} cho nơi gọi sử dụng
     */
    private PhieuNhap lock(Long id) {
        return repository
                .findForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu nhập."));
    }

    /**
     * Khóa các nguyên liệu theo thứ tự mã tăng dần để giảm nguy cơ deadlock; refresh sau khi khóa để lấy tồn kho mới nhất.
     * @param ids tập mã nguyên liệu thuộc cả phiếu cũ và yêu cầu mới, được sắp để khóa nhất quán
     * @return Map dùng tra cứu theo khóa; ý nghĩa key/value được mô tả trong hàm
     */
    private Map<Long, NguyenLieu> lockMaterials(Collection<Long> ids) {
        // result: Map kết quả ánh xạ mã nguyên liệu sang entity đã khóa.
        Map<Long, NguyenLieu> result = new TreeMap<>();
        // Every transaction acquires ingredient locks in the same order.
        for (/* id: khóa chính của phiếu nhập. */ Long id : new TreeSet<>(ids)) {
            // ingredient: nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn.
            NguyenLieu ingredient =
                    nguyenLieuRepository
                            .findForUpdate(id)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Nguyên liệu không tồn tại: " + id));
            // Details may have loaded this entity before its row lock was acquired.
            entityManager.refresh(ingredient, LockModeType.PESSIMISTIC_WRITE);
            result.put(id, ingredient);
        }
        return result;
    }

    /**
     * So phiên bản trên form với phiên bản phiếu hiện tại; không khớp hoặc thiếu phiên bản thì yêu cầu tải lại trang.
     * @param receipt phiếu nhập đang được đọc, tạo hoặc chỉnh sửa
     * @param expected phiên bản phiếu người dùng đã thấy khi mở form
     */
    private static void checkVersion(PhieuNhap receipt, Long expected) {
        if (expected == null || !expected.equals(receipt.getPhienBan())) {
            throw new IllegalArgumentException(
                    "Phiếu nhập đã thay đổi. Hãy tải lại trang trước khi sửa hoặc xóa.");
        }
    }

    /**
     * Tính số lượng nhân đơn giá, làm tròn 2 chữ số theo HALF_UP và từ chối thành tiền vượt NUMBER(14,2).
     * @param line dữ liệu một dòng nhập gồm nguyên liệu, số lượng và đơn giá
     * @return giá trị tiền hoặc số lượng tính được bằng BigDecimal
     */
    private static BigDecimal lineAmount(DongNhapForm line) {
        // amount: thành tiền một dòng: số lượng nhân đơn giá, làm tròn 2 chữ số HALF_UP.
        BigDecimal amount =
                line.getSoLuong().multiply(line.getDonGia()).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(MAX_MONEY) > 0)
            throw new IllegalArgumentException("Thành tiền vượt giới hạn NUMBER(14,2).");
        return amount;
    }

    /**
     * Tính tồn kho mới bằng tồn hiện tại cộng delta; từ chối tồn âm hoặc vượt NUMBER(10,2), sau đó gán vào entity nguyên liệu.
     * @param ingredient nguyên liệu đang được đọc, khóa hoặc điều chỉnh tồn
     * @param delta chênh lệch tăng/giảm tồn kho, bằng số lượng mới trừ số lượng cũ
     */
    private static void adjustStock(NguyenLieu ingredient, BigDecimal delta) {
        // next: tồn kho dự kiến sau khi cộng chênh lệch delta.
        BigDecimal next = ingredient.getSoLuongTon().add(delta);
        if (next.signum() < 0) {
            throw new IllegalArgumentException(
                    "Không đủ tồn kho của "
                            + ingredient.getTenNl()
                            + " để sửa hoặc xóa phiếu nhập.");
        }
        if (next.compareTo(MAX_STOCK) > 0) {
            throw new IllegalArgumentException(
                    "Tồn kho của " + ingredient.getTenNl() + " vượt giới hạn NUMBER(10,2).");
        }
        ingredient.setSoLuongTon(next);
    }
}
