USE qltl_luat_dan_su;

INSERT INTO VAI_TRO (VT_ten, VT_moTa) VALUES
('ADMIN', 'Quan tri he thong'),
('TRUONG_PHONG', 'Quan ly bo phan va duyet tai lieu'),
('NHAN_VIEN', 'Xu ly vu viec va tai lieu')
ON DUPLICATE KEY UPDATE VT_moTa = VALUES(VT_moTa);

INSERT INTO BO_PHAN (BP_ten, BP_moTa) VALUES
('Phong Dan su', 'Phu trach ho so dan su'),
('Phong Dat dai', 'Phu trach ho so dat dai'),
('Phong Hon nhan gia dinh', 'Phu trach ho so hon nhan gia dinh'),
('Phong Lao dong', 'Phu trach ho so lao dong')
ON DUPLICATE KEY UPDATE BP_moTa = VALUES(BP_moTa);

INSERT INTO TAI_KHOAN_NGUOI_DUNG (
  BP_id, VT_id, ND_hoTen, ND_taiKhoan, ND_matKhau, ND_email,
  ND_soLanSai, ND_trangThaiTK, ND_chuyenMon, ND_chungChi
)
SELECT NULL, vt.VT_id, 'Admin he thong', 'admin', '$2y$demo-admin',
       'admin@luatdan-su.local', 0, TRUE, 'Quan tri he thong', NULL
FROM VAI_TRO vt
WHERE vt.VT_ten = 'ADMIN'
ON DUPLICATE KEY UPDATE ND_hoTen = VALUES(ND_hoTen), VT_id = VALUES(VT_id);

INSERT INTO TAI_KHOAN_NGUOI_DUNG (
  BP_id, VT_id, ND_hoTen, ND_taiKhoan, ND_matKhau, ND_email,
  ND_soLanSai, ND_trangThaiTK, ND_chuyenMon, ND_chungChi
)
SELECT bp.BP_id, vt.VT_id, 'Nguyen Minh Truong', 'truongphong.dansu', '$2y$demo-manager',
       'truongphong.dansu@luatdan-su.local', 0, TRUE, 'Dan su', 'Chung chi hanh nghe luat su'
FROM BO_PHAN bp JOIN VAI_TRO vt
WHERE bp.BP_ten = 'Phong Dan su' AND vt.VT_ten = 'TRUONG_PHONG'
ON DUPLICATE KEY UPDATE ND_hoTen = VALUES(ND_hoTen), BP_id = VALUES(BP_id), VT_id = VALUES(VT_id);

INSERT INTO TAI_KHOAN_NGUOI_DUNG (
  BP_id, VT_id, ND_hoTen, ND_taiKhoan, ND_matKhau, ND_email,
  ND_soLanSai, ND_trangThaiTK, ND_chuyenMon, ND_chungChi
)
SELECT bp.BP_id, vt.VT_id, 'Tran Lan Anh', 'nhanvien.dansu', '$2y$demo-staff',
       'nhanvien.dansu@luatdan-su.local', 0, TRUE, 'Hop dong dan su', NULL
FROM BO_PHAN bp JOIN VAI_TRO vt
WHERE bp.BP_ten = 'Phong Dan su' AND vt.VT_ten = 'NHAN_VIEN'
ON DUPLICATE KEY UPDATE ND_hoTen = VALUES(ND_hoTen), BP_id = VALUES(BP_id), VT_id = VALUES(VT_id);

INSERT INTO KHACH_HANG (KH_ten, KH_loai, KH_CCCD_MST, KH_sdt, KH_diaChi, KH_email, KH_ngayTao) VALUES
('Cong ty TNHH Minh Phat', 'TO_CHUC', '0312345678', '0911000001', 'Quan 1, TP.HCM', 'contact@minhphat.local', NOW()),
('Le Thi Hanh', 'CA_NHAN', '079199900001', '0911000002', 'Thu Duc, TP.HCM', 'hanh.le@example.local', NOW()),
('Nguyen Van Binh', 'CA_NHAN', '079188800002', '0911000003', 'Binh Thanh, TP.HCM', 'binh.nguyen@example.local', NOW())
ON DUPLICATE KEY UPDATE KH_ten = VALUES(KH_ten), KH_sdt = VALUES(KH_sdt), KH_email = VALUES(KH_email);

INSERT INTO VU_VIEC (KH_id, ND_phuTrach_id, VV_ten, VV_loai, VV_trangThai, VV_ngayMo, VV_ngayDong, VV_ghiChu)
SELECT kh.KH_id, nd.ND_id, 'Tu van hop dong dan su', 'Dan su', 'DANG_XU_LY',
       '2026-06-01 09:00:00', NULL, 'Ra soat nghia vu va bien phap bao dam trong hop dong'
FROM KHACH_HANG kh JOIN TAI_KHOAN_NGUOI_DUNG nd
WHERE kh.KH_CCCD_MST = '0312345678' AND nd.ND_taiKhoan = 'nhanvien.dansu'
ON DUPLICATE KEY UPDATE VV_trangThai = VALUES(VV_trangThai), ND_phuTrach_id = VALUES(ND_phuTrach_id);

INSERT INTO VU_VIEC (KH_id, ND_phuTrach_id, VV_ten, VV_loai, VV_trangThai, VV_ngayMo, VV_ngayDong, VV_ghiChu)
SELECT kh.KH_id, nd.ND_id, 'Tu van tranh chap dat dai', 'Dat dai', 'MOI_TIEP_NHAN',
       '2026-06-05 10:30:00', NULL, 'Chuan bi tai lieu phap ly lien quan dang ky dat dai'
FROM KHACH_HANG kh JOIN TAI_KHOAN_NGUOI_DUNG nd
WHERE kh.KH_CCCD_MST = '079199900001' AND nd.ND_taiKhoan = 'truongphong.dansu'
ON DUPLICATE KEY UPDATE VV_trangThai = VALUES(VV_trangThai), ND_phuTrach_id = VALUES(ND_phuTrach_id);

UPDATE TAI_LIEU tl
JOIN VU_VIEC vv ON vv.VV_ten = 'Tu van hop dong dan su'
SET tl.VV_id = vv.VV_id
WHERE tl.TL_soHieu = '91/2015/QH13';

UPDATE TAI_LIEU tl
JOIN VU_VIEC vv ON vv.VV_ten = 'Tu van tranh chap dat dai'
SET tl.VV_id = vv.VV_id
WHERE tl.TL_soHieu IN ('31/2024/QH15', '102/2024/ND-CP', '102/2024/NĐ-CP');

INSERT INTO QUYEN_TRUY_CAP (TL_id, ND_id, QTC_loaiQuyen, QTC_thoiHan)
SELECT tl.TL_id, nd.ND_id, 'DOC_SUA', '2026-12-31 23:59:59'
FROM TAI_LIEU tl JOIN TAI_KHOAN_NGUOI_DUNG nd
WHERE tl.TL_soHieu = '91/2015/QH13' AND nd.ND_taiKhoan = 'nhanvien.dansu';

INSERT INTO DUYET_TAI_LIEU (TL_id, ND_duyet_id, DTL_trangThai, DTL_ghiChu, DTL_timeDuyet)
SELECT tl.TL_id, nd.ND_id, 'DA_DUYET', 'Tai lieu hop le cho ho so dan su', NOW()
FROM TAI_LIEU tl JOIN TAI_KHOAN_NGUOI_DUNG nd
WHERE tl.TL_soHieu = '91/2015/QH13' AND nd.ND_taiKhoan = 'truongphong.dansu';

INSERT INTO THONG_BAO (ND_id, TL_id, TB_tieuDe, TB_noiDung, TB_trangThaiDoc)
SELECT nd.ND_id, tl.TL_id, 'Tai lieu da duyet', 'Bo luat Dan su 2015 da duoc duyet cho vu viec.', FALSE
FROM TAI_KHOAN_NGUOI_DUNG nd JOIN TAI_LIEU tl
WHERE nd.ND_taiKhoan = 'nhanvien.dansu' AND tl.TL_soHieu = '91/2015/QH13';

INSERT INTO PHIEN_BAN_TAI_LIEU (TL_id, ND_update_id, PBTL_maPhienBan, PBTL_nguoiUpdate, PBTL_timeUpdate, PBTL_ghiChu)
SELECT tl.TL_id, nd.ND_id, 'v1.0', nd.ND_hoTen, NOW(), 'Phien ban dau tien tu du lieu crawl'
FROM TAI_LIEU tl JOIN TAI_KHOAN_NGUOI_DUNG nd
WHERE tl.TL_soHieu = '91/2015/QH13' AND nd.ND_taiKhoan = 'admin';

INSERT INTO LICH_SU_HOAT_DONG (ND_id, TL_id, LS_hoatDong, LS_thoiGianSua, LS_diaChiIP)
SELECT nd.ND_id, tl.TL_id, 'IMPORT_TAI_LIEU', NOW(), '127.0.0.1'
FROM TAI_KHOAN_NGUOI_DUNG nd JOIN TAI_LIEU tl
WHERE nd.ND_taiKhoan = 'admin' AND tl.TL_soHieu = '91/2015/QH13';
