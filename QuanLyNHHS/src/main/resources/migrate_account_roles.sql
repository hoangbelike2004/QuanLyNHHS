-- Run once as NHHS_APP after backing up TAI_KHOAN.
-- Existing manager accounts become admins; employee accounts become cashiers.
UPDATE TAI_KHOAN SET VAI_TRO = 'ADMIN'
WHERE VAI_TRO = 'QUAN_LY' OR VAI_TRO IS NULL;

UPDATE TAI_KHOAN SET VAI_TRO = 'THU_NGAN'
WHERE VAI_TRO = 'NHAN_VIEN';

COMMIT;
