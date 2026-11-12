-- Du lieu kiem thu sat nghiep vu nha hang hai san (Oracle, UTF-8).
-- Ten nguoi, so dien thoai va email la du lieu gia lap; gia/mon/so luong la gia tri mau hop ly.
-- Chay SAU khi ung dung da tao bang va sequence, bang schema cua ung dung (NHHS_APP).
-- Chay bang Run Script (F5) trong SQL Developer. Chi chay mot lan (ten dang nhap la duy nhat).
-- Mat khau thu nghiem nam o README.md; chi dung bo tai khoan nay trong moi truong kiem thu.
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
SET DEFINE OFF
SET SERVEROUTPUT ON

DECLARE
    v_nv_quan_ly NHAN_VIEN.MA_NV%TYPE;
    v_nv_thu_ngan NHAN_VIEN.MA_NV%TYPE;
    v_loai_hai_san LOAI_MON.MA_LOAI%TYPE;
    v_loai_rau LOAI_MON.MA_LOAI%TYPE;
    v_loai_uong LOAI_MON.MA_LOAI%TYPE;
    v_tom MON_AN.MA_MON%TYPE;
    v_muc MON_AN.MA_MON%TYPE;
    v_ngheu MON_AN.MA_MON%TYPE;
    v_nuoc MON_AN.MA_MON%TYPE;
    v_ban_trong BAN_AN.MA_BAN%TYPE;
    v_ban_dat BAN_AN.MA_BAN%TYPE;
    v_ban_dang_dung BAN_AN.MA_BAN%TYPE;
    v_kh_1 KHACH_HANG.MA_KH%TYPE;
    v_kh_2 KHACH_HANG.MA_KH%TYPE;
    v_ncc_1 NHA_CUNG_CAP.MA_NCC%TYPE;
    v_ncc_2 NHA_CUNG_CAP.MA_NCC%TYPE;
    v_nl_tom NGUYEN_LIEU.MA_NL%TYPE;
    v_nl_muc NGUYEN_LIEU.MA_NL%TYPE;
    v_nl_ngheu NGUYEN_LIEU.MA_NL%TYPE;
    v_nl_rau NGUYEN_LIEU.MA_NL%TYPE;
    v_pn_1 PHIEU_NHAP.MA_PN%TYPE;
    v_pn_2 PHIEU_NHAP.MA_PN%TYPE;
    v_hd_1 HOA_DON.MA_HD%TYPE;
    v_hd_2 HOA_DON.MA_HD%TYPE;
BEGIN
    -- TAI_KHOAN: khong co FK toi NHAN_VIEN trong thiet ke hien tai.
    INSERT INTO TAI_KHOAN (MA_TK, HO_TEN, TEN_DANG_NHAP, MAT_KHAU)
    VALUES (SEQ_TAI_KHOAN.NEXTVAL, 'Nguyễn Minh An', 'nhhs_admin',
            '$2a$12$Uajz78eNAQW/4qUsKNe.ieq22HHanirmZaiD6WMkUV2yK3QPLOvse');

    INSERT INTO NHAN_VIEN (MA_NV, HO_TEN, GIOI_TINH, SDT, CHUC_VU, LUONG,
                           NGAY_VAO_LAM, EMAIL, CA_LAM_VIEC, TRANG_THAI)
    VALUES (SEQ_NHAN_VIEN.NEXTVAL, 'Nguyễn Minh An', 'Nam', '0900000101',
            'Quản lý nhà hàng', 18000000, TRUNC(SYSDATE) - 365,
            'an.nhhs@example.com', 'Toàn thời gian', 'Đang làm việc')
    RETURNING MA_NV INTO v_nv_quan_ly;
    INSERT INTO NHAN_VIEN (MA_NV, HO_TEN, GIOI_TINH, SDT, CHUC_VU, LUONG,
                           NGAY_VAO_LAM, EMAIL, CA_LAM_VIEC, TRANG_THAI)
    VALUES (SEQ_NHAN_VIEN.NEXTVAL, 'Trần Thị Bình', 'Nữ', '0900000102',
            'Thu ngân', 9500000, TRUNC(SYSDATE) - 180,
            'binh.nhhs@example.com', 'Ca sáng (07:30 - 15:30)', 'Đang làm việc')
    RETURNING MA_NV INTO v_nv_thu_ngan;
    INSERT INTO NHAN_VIEN (MA_NV, HO_TEN, GIOI_TINH, SDT, CHUC_VU, LUONG,
                           NGAY_VAO_LAM, EMAIL, CA_LAM_VIEC, TRANG_THAI)
    VALUES (SEQ_NHAN_VIEN.NEXTVAL, 'Lê Hoàng Phúc', 'Nam', '0900000103',
            'Nhân viên phục vụ', 8000000, TRUNC(SYSDATE) - 90,
            'phuc.nhhs@example.com', 'Ca tối (15:00 - 23:00)', 'Đang làm việc');

    INSERT INTO LOAI_MON (MA_LOAI, TEN_LOAI, MO_TA, TRANG_THAI)
    VALUES (SEQ_LOAI_MON.NEXTVAL, 'Hải sản', 'Các món hải sản chế biến tại bếp', 'Hoạt động')
    RETURNING MA_LOAI INTO v_loai_hai_san;
    INSERT INTO LOAI_MON (MA_LOAI, TEN_LOAI, MO_TA, TRANG_THAI)
    VALUES (SEQ_LOAI_MON.NEXTVAL, 'Rau và món ăn kèm', 'Rau, món ăn kèm theo bữa', 'Hoạt động')
    RETURNING MA_LOAI INTO v_loai_rau;
    INSERT INTO LOAI_MON (MA_LOAI, TEN_LOAI, MO_TA, TRANG_THAI)
    VALUES (SEQ_LOAI_MON.NEXTVAL, 'Đồ uống', 'Nước uống đóng chai', 'Hoạt động')
    RETURNING MA_LOAI INTO v_loai_uong;

    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI, MO_TA)
    VALUES (SEQ_MON_AN.NEXTVAL, v_loai_hai_san, 'Tôm sú hấp bia', 'Đĩa', 329000,
            'Đang phục vụ', 'Tôm sú hấp bia, ăn kèm muối tiêu chanh')
    RETURNING MA_MON INTO v_tom;
    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI, MO_TA)
    VALUES (SEQ_MON_AN.NEXTVAL, v_loai_hai_san, 'Mực nướng muối ớt', 'Đĩa', 219000,
            'Đang phục vụ', 'Mực tươi nướng muối ớt')
    RETURNING MA_MON INTO v_muc;
    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI, MO_TA)
    VALUES (SEQ_MON_AN.NEXTVAL, v_loai_hai_san, 'Nghêu hấp sả', 'Thố', 129000,
            'Đang phục vụ', 'Nghêu hấp sả, nước dùng cay nhẹ')
    RETURNING MA_MON INTO v_ngheu;
    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI, MO_TA)
    VALUES (SEQ_MON_AN.NEXTVAL, v_loai_rau, 'Rau muống xào tỏi', 'Đĩa', 69000,
            'Đang phục vụ', 'Rau muống xào tỏi');
    INSERT INTO MON_AN (MA_MON, MA_LOAI, TEN_MON, DON_VI, DON_GIA, TRANG_THAI, MO_TA)
    VALUES (SEQ_MON_AN.NEXTVAL, v_loai_uong, 'Nước suối 500 ml', 'Chai', 15000,
            'Đang phục vụ', 'Nước uống đóng chai')
    RETURNING MA_MON INTO v_nuoc;

    -- Cac bang IDENTITY khong chen cot MA_...
    INSERT INTO BAN_AN (TEN_BAN, SO_CHO, KHU_VUC, TRANG_THAI)
    VALUES ('Bàn kiểm thử 01', 4, 'Tầng 1', 'Trống')
    RETURNING MA_BAN INTO v_ban_trong;
    INSERT INTO BAN_AN (TEN_BAN, SO_CHO, KHU_VUC, TRANG_THAI)
    VALUES ('Bàn kiểm thử 02', 6, 'Tầng 1', 'Đã đặt')
    RETURNING MA_BAN INTO v_ban_dat;
    INSERT INTO BAN_AN (TEN_BAN, SO_CHO, KHU_VUC, TRANG_THAI)
    VALUES ('Bàn kiểm thử 03', 4, 'Sân vườn', 'Đang dùng')
    RETURNING MA_BAN INTO v_ban_dang_dung;

    INSERT INTO KHACH_HANG (HO_TEN, SDT, EMAIL, DIEM_TICH_LUY)
    VALUES ('Phạm Thảo Vy', '0900000201', 'thao.vy@example.com', 120)
    RETURNING MA_KH INTO v_kh_1;
    INSERT INTO KHACH_HANG (HO_TEN, SDT, EMAIL, DIEM_TICH_LUY)
    VALUES ('Đỗ Quốc Huy', '0900000202', 'quoc.huy@example.com', 0)
    RETURNING MA_KH INTO v_kh_2;

    INSERT INTO DAT_BAN (MA_KH, MA_BAN, NGAY_DAT, SO_NGUOI, TIEN_COC, TRANG_THAI)
    VALUES (v_kh_1, v_ban_dat, TRUNC(SYSDATE) + 1, 4, 200000, 'DA_XAC_NHAN');
    INSERT INTO DAT_BAN (MA_KH, MA_BAN, NGAY_DAT, SO_NGUOI, TIEN_COC, TRANG_THAI)
    VALUES (v_kh_2, v_ban_trong, TRUNC(SYSDATE) - 1, 2, 0, 'HOAN_THANH');

    INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, SDT, DIA_CHI, EMAIL)
    VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Cơ sở hải sản An Bình', '0900000301',
            'Khu vực chợ Bình Điền, TP. Hồ Chí Minh', 'anbinh@example.com')
    RETURNING MA_NCC INTO v_ncc_1;
    INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, SDT, DIA_CHI, EMAIL)
    VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Cơ sở thực phẩm Minh Tâm', '0900000302',
            'Khu vực chợ đầu mối Thủ Đức, TP. Hồ Chí Minh', 'minhtam@example.com')
    RETURNING MA_NCC INTO v_ncc_2;

    INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI, SO_LUONG_TON, TRANG_THAI)
    VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Tôm sú tươi', 'Kg', 0, 'HOAT_DONG')
    RETURNING MA_NL INTO v_nl_tom;
    INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI, SO_LUONG_TON, TRANG_THAI)
    VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Mực ống tươi', 'Kg', 0, 'HOAT_DONG')
    RETURNING MA_NL INTO v_nl_muc;
    INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI, SO_LUONG_TON, TRANG_THAI)
    VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Nghêu sống', 'Kg', 0, 'HOAT_DONG')
    RETURNING MA_NL INTO v_nl_ngheu;
    INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI, SO_LUONG_TON, TRANG_THAI)
    VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Rau muống', 'Kg', 0, 'HOAT_DONG')
    RETURNING MA_NL INTO v_nl_rau;

    -- Phieu nhap truc tiep qua SQL: chi tiet va ton kho duoc ghi trong cung giao dich.
    -- Trigger TRG_CTPN_THANH_TIEN (neu da cai) chi tinh thanh tien, khong cong ton.
    INSERT INTO PHIEU_NHAP (MA_PN, MA_NCC, MA_NV, NGAY_NHAP, TONG_TIEN, PHIEN_BAN)
    VALUES (SEQ_PHIEU_NHAP.NEXTVAL, v_ncc_1, v_nv_quan_ly, TRUNC(SYSDATE) - 2, 2400000, 0)
    RETURNING MA_PN INTO v_pn_1;
    INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_pn_1, v_nl_tom, 8, 210000, 1680000);
    INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_pn_1, v_nl_muc, 4, 180000, 720000);

    INSERT INTO PHIEU_NHAP (MA_PN, MA_NCC, MA_NV, NGAY_NHAP, TONG_TIEN, PHIEN_BAN)
    VALUES (SEQ_PHIEU_NHAP.NEXTVAL, v_ncc_2, v_nv_quan_ly, TRUNC(SYSDATE) - 1, 845000, 0)
    RETURNING MA_PN INTO v_pn_2;
    INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_pn_2, v_nl_ngheu, 12, 60000, 720000);
    INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_pn_2, v_nl_rau, 5, 25000, 125000);

    UPDATE NGUYEN_LIEU SET SO_LUONG_TON = 8 WHERE MA_NL = v_nl_tom;
    UPDATE NGUYEN_LIEU SET SO_LUONG_TON = 4 WHERE MA_NL = v_nl_muc;
    UPDATE NGUYEN_LIEU SET SO_LUONG_TON = 12 WHERE MA_NL = v_nl_ngheu;
    UPDATE NGUYEN_LIEU SET SO_LUONG_TON = 5 WHERE MA_NL = v_nl_rau;

    -- Hoa don da thanh toan: 329000 + 219000 + 2*15000 - 28000 = 550000.
    INSERT INTO HOA_DON (MA_BAN, MA_NV, MA_KH, NGAY_LAP, GIAM_GIA, TONG_TIEN, TRANG_THAI)
    VALUES (v_ban_trong, v_nv_thu_ngan, v_kh_1, TRUNC(SYSDATE), 28000, 550000, 'Da thanh toan')
    RETURNING MA_HD INTO v_hd_1;
    INSERT INTO CT_HOA_DON (MA_HD, MA_MON, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_hd_1, v_tom, 1, 329000, 329000);
    INSERT INTO CT_HOA_DON (MA_HD, MA_MON, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_hd_1, v_muc, 1, 219000, 219000);
    INSERT INTO CT_HOA_DON (MA_HD, MA_MON, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_hd_1, v_nuoc, 2, 15000, 30000);

    -- Hoa don chua thanh toan de thu luong sua mon va thanh toan.
    INSERT INTO HOA_DON (MA_BAN, MA_NV, MA_KH, NGAY_LAP, GIAM_GIA, TONG_TIEN, TRANG_THAI)
    VALUES (v_ban_dang_dung, v_nv_thu_ngan, v_kh_2, TRUNC(SYSDATE), 0, 288000, 'Chua thanh toan')
    RETURNING MA_HD INTO v_hd_2;
    INSERT INTO CT_HOA_DON (MA_HD, MA_MON, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_hd_2, v_ngheu, 2, 129000, 258000);
    INSERT INTO CT_HOA_DON (MA_HD, MA_MON, SO_LUONG, DON_GIA, THANH_TIEN)
    VALUES (v_hd_2, v_nuoc, 2, 15000, 30000);

    DBMS_OUTPUT.PUT_LINE('Da them tai khoan nhhs_admin.');
    DBMS_OUTPUT.PUT_LINE('Ma phieu nhap: ' || v_pn_1 || ', ' || v_pn_2);
    DBMS_OUTPUT.PUT_LINE('Ma hoa don: ' || v_hd_1 || ' (da thanh toan), ' || v_hd_2 || ' (chua thanh toan)');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
COMMIT;

-- Doi chieu so ban ghi va so tien sau khi chay.
SELECT 'TAI_KHOAN' BANG, COUNT(*) SO_DONG FROM TAI_KHOAN
UNION ALL SELECT 'NHAN_VIEN', COUNT(*) FROM NHAN_VIEN
UNION ALL SELECT 'LOAI_MON', COUNT(*) FROM LOAI_MON
UNION ALL SELECT 'MON_AN', COUNT(*) FROM MON_AN
UNION ALL SELECT 'BAN_AN', COUNT(*) FROM BAN_AN
UNION ALL SELECT 'KHACH_HANG', COUNT(*) FROM KHACH_HANG
UNION ALL SELECT 'DAT_BAN', COUNT(*) FROM DAT_BAN
UNION ALL SELECT 'NHA_CUNG_CAP', COUNT(*) FROM NHA_CUNG_CAP
UNION ALL SELECT 'NGUYEN_LIEU', COUNT(*) FROM NGUYEN_LIEU
UNION ALL SELECT 'PHIEU_NHAP', COUNT(*) FROM PHIEU_NHAP
UNION ALL SELECT 'CT_PHIEU_NHAP', COUNT(*) FROM CT_PHIEU_NHAP
UNION ALL SELECT 'HOA_DON', COUNT(*) FROM HOA_DON
UNION ALL SELECT 'CT_HOA_DON', COUNT(*) FROM CT_HOA_DON;

SELECT h.MA_HD, h.TRANG_THAI, h.GIAM_GIA, h.TONG_TIEN,
       SUM(c.THANH_TIEN) AS TONG_CHI_TIET
FROM HOA_DON h JOIN CT_HOA_DON c ON c.MA_HD = h.MA_HD
GROUP BY h.MA_HD, h.TRANG_THAI, h.GIAM_GIA, h.TONG_TIEN
ORDER BY h.MA_HD DESC FETCH FIRST 10 ROWS ONLY;
