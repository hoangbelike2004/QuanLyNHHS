-- ====================================================================
-- HƯỚNG DẪN TẠO USER & PHÂN QUYỀN TRÊN ORACLE DATABASE FREE (23c / 23ai / XE)
-- Chạy script này bằng tài khoản SYS (as SYSDBA)
-- ====================================================================

-- BƯỚC 1: Chuyển vùng phiên làm việc sang Pluggable Database (PDB)
-- Lưu ý: Bản 23c Free mặc định là FREEPDB1, bản Oracle XE là XEPDB1
ALTER SESSION SET CONTAINER = FREEPDB1;
-- Nếu bạn dùng Oracle XE, hãy bỏ comment dòng dưới:
-- ALTER SESSION SET CONTAINER = XEPDB1;

-- BƯỚC 2: Tạo User ứng dụng cho dự án Spring Boot
-- Xóa user cũ nếu cần tạo lại sạch sẽ:
-- DROP USER NHHS_APP CASCADE;

CREATE USER NHHS_APP IDENTIFIED BY nhhs123;

-- BƯỚC 3: Cấp các quyền cơ bản để kết nối và tự động sinh bảng (DDL Auto)
GRANT CONNECT, RESOURCE TO NHHS_APP;

-- Cấp hạn ngạch lưu trữ không giới hạn trên Tablespace USERS (bắt buộc trong Oracle 12c/19c/23c)
ALTER USER NHHS_APP QUOTA UNLIMITED ON USERS;

-- Cấp các quyền tạo đối tượng cần thiết cho Hibernate và PL/SQL
GRANT CREATE TABLE TO NHHS_APP;
GRANT CREATE SEQUENCE TO NHHS_APP;
GRANT CREATE VIEW TO NHHS_APP;
GRANT CREATE PROCEDURE TO NHHS_APP;
GRANT CREATE TRIGGER TO NHHS_APP;
GRANT CREATE SYNONYM TO NHHS_APP;

-- (Tùy chọn môi trường học tập) Cấp toàn quyền DBA nếu muốn:
GRANT DBA TO NHHS_APP;

COMMIT;

-- Ứng dụng dùng một kết nối Oracle NHHS_APP. Bốn vai trò ADMIN,
-- QUAN_LY_KHO, KE_TOAN và THU_NGAN được lưu ở TAI_KHOAN.VAI_TRO
-- và kiểm tra bởi Spring Security, không phải Oracle CREATE ROLE.
-- Với cơ sở dữ liệu đã có tài khoản, chạy migrate_account_roles.sql.


sqlplus /nolog
CONNECT sys@//localhost:1521/FREEPDB1 AS SYSDBA
SELECT USERNAME FROM DBA_USERS WHERE USERNAME = 'NHHS_APP';
CREATE USER NHHS_APP IDENTIFIED BY nhhs123;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO NHHS_APP;
ALTER USER NHHS_APP QUOTA UNLIMITED ON USERS;
CONNECT NHHS_APP@//localhost:1521/FREEPDB1

INSERT INTO TAI_KHOAN (MA_TK, HO_TEN, TEN_DANG_NHAP, MAT_KHAU, VAI_TRO)
VALUES (
    SEQ_TAI_KHOAN.NEXTVAL,
    'Nguyễn Minh An',
    'nhhs_admin',
    '$2a$12$Uajz78eNAQW/4qUsKNe.ieq22HHanirmZaiD6WMkUV2yK3QPLOvse',
    'ADMIN'
);

COMMIT;

SELECT TEN_DANG_NHAP FROM TAI_KHOAN;
