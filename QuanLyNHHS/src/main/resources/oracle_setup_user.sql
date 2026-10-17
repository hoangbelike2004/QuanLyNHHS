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

-- ====================================================================
-- PHÂN QUYỀN THEO VAI TRÒ (USER, ROLE, PRIVILEGE) THEO ĐÚNG ẢNH 2
-- ====================================================================

-- 1. Tạo các Role theo thiết kế hệ thống
CREATE ROLE ROLE_ADMIN;
CREATE ROLE ROLE_QUAN_LY;
CREATE ROLE ROLE_THU_NGAN;
CREATE ROLE ROLE_PHUC_VU;
CREATE ROLE ROLE_THU_KHO;
CREATE ROLE ROLE_BAO_CAO;

-- 2. Gán quyền cơ bản đăng nhập cho các Role
GRANT CREATE SESSION TO ROLE_ADMIN;
GRANT CREATE SESSION TO ROLE_QUAN_LY;
GRANT CREATE SESSION TO ROLE_THU_NGAN;
GRANT CREATE SESSION TO ROLE_PHUC_VU;
GRANT CREATE SESSION TO ROLE_THU_KHO;
GRANT CREATE SESSION TO ROLE_BAO_CAO;

-- 3. Gán quyền thao tác dữ liệu trên các bảng (LOAI_MON, MON_AN, NHAN_VIEN)
-- ADMIN: Toàn quyền
GRANT ALL PRIVILEGES TO ROLE_ADMIN;

-- QUAN_LY: Quản lý nhân viên, bàn ăn, thực đơn món ăn, thống kê
GRANT SELECT, INSERT, UPDATE, DELETE ON NHHS_APP.LOAI_MON TO ROLE_QUAN_LY;
GRANT SELECT, INSERT, UPDATE, DELETE ON NHHS_APP.MON_AN TO ROLE_QUAN_LY;
GRANT SELECT, INSERT, UPDATE, DELETE ON NHHS_APP.NHAN_VIEN TO ROLE_QUAN_LY;

-- PHUC_VU: Xem thực đơn món ăn để phục vụ khách
GRANT SELECT ON NHHS_APP.LOAI_MON TO ROLE_PHUC_VU;
GRANT SELECT ON NHHS_APP.MON_AN TO ROLE_PHUC_VU;

-- BAO_CAO: Chỉ xem dữ liệu, không được thêm/sửa/xóa
GRANT SELECT ON NHHS_APP.LOAI_MON TO ROLE_BAO_CAO;
GRANT SELECT ON NHHS_APP.MON_AN TO ROLE_BAO_CAO;
GRANT SELECT ON NHHS_APP.NHAN_VIEN TO ROLE_BAO_CAO;

-- 4. Tạo các tài khoản người dùng mẫu và gán Role (Ví dụ theo Ảnh 2)
CREATE USER admin_user IDENTIFIED BY admin123;
GRANT ROLE_ADMIN TO admin_user;

CREATE USER quan_ly IDENTIFIED BY 123456;
GRANT ROLE_QUAN_LY TO quan_ly;

CREATE USER phuc_vu IDENTIFIED BY 123456;
GRANT ROLE_PHUC_VU TO phuc_vu;

CREATE USER bao_cao IDENTIFIED BY 123456;
GRANT ROLE_BAO_CAO TO bao_cao;

COMMIT;
