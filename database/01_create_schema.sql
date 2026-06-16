CREATE DATABASE IF NOT EXISTS qltl_luat_dan_su
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE qltl_luat_dan_su;

CREATE TABLE VAI_TRO (
  VT_id INT AUTO_INCREMENT PRIMARY KEY,
  VT_ten VARCHAR(100),
  VT_moTa TEXT,
  UNIQUE KEY uk_vt_ten (VT_ten)
) COMMENT='Vai tro nguoi dung';

CREATE TABLE BO_PHAN (
  BP_id INT AUTO_INCREMENT PRIMARY KEY,
  BP_ten VARCHAR(150),
  BP_moTa TEXT,
  UNIQUE KEY uk_bp_ten (BP_ten)
) COMMENT='Bo phan/phong ban';

CREATE TABLE TAI_KHOAN_NGUOI_DUNG (
  ND_id INT AUTO_INCREMENT PRIMARY KEY,
  BP_id INT,
  VT_id INT,
  ND_hoTen TEXT,
  ND_taiKhoan VARCHAR(100),
  ND_matKhau TEXT,
  ND_email VARCHAR(150),
  ND_soLanSai INT DEFAULT 0,
  ND_trangThaiTK BOOLEAN DEFAULT TRUE,
  ND_chuyenMon TEXT,
  ND_chungChi TEXT,
  UNIQUE KEY uk_nd_tai_khoan (ND_taiKhoan),
  UNIQUE KEY uk_nd_email (ND_email),
  KEY idx_nd_bp (BP_id),
  KEY idx_nd_vt (VT_id),
  CONSTRAINT fk_nd_bp FOREIGN KEY (BP_id) REFERENCES BO_PHAN(BP_id),
  CONSTRAINT fk_nd_vt FOREIGN KEY (VT_id) REFERENCES VAI_TRO(VT_id)
) COMMENT='Tai khoan nguoi dung';

CREATE TABLE KHACH_HANG (
  KH_id INT AUTO_INCREMENT PRIMARY KEY,
  KH_ten TEXT,
  KH_loai TEXT,
  KH_CCCD_MST VARCHAR(50),
  KH_sdt TEXT,
  KH_diaChi TEXT,
  KH_email TEXT,
  KH_ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_kh_cccd_mst (KH_CCCD_MST),
  KEY idx_kh_ngay_tao (KH_ngayTao)
) COMMENT='Thong tin khach hang';

CREATE TABLE VU_VIEC (
  VV_id INT AUTO_INCREMENT PRIMARY KEY,
  KH_id INT,
  ND_phuTrach_id INT,
  VV_ten VARCHAR(200),
  VV_loai TEXT,
  VV_trangThai TEXT,
  VV_ngayMo DATETIME,
  VV_ngayDong DATETIME,
  VV_ghiChu TEXT,
  UNIQUE KEY uk_vv_ten (VV_ten),
  KEY idx_vv_kh (KH_id),
  KEY idx_vv_phu_trach (ND_phuTrach_id),
  KEY idx_vv_trang_thai (VV_trangThai(50)),
  CONSTRAINT fk_vv_kh FOREIGN KEY (KH_id) REFERENCES KHACH_HANG(KH_id),
  CONSTRAINT fk_vv_phu_trach FOREIGN KEY (ND_phuTrach_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id)
) COMMENT='Vu viec cua khach hang';

CREATE TABLE DANH_MUC (
  DM_id INT AUTO_INCREMENT PRIMARY KEY,
  DM_ten VARCHAR(150),
  UNIQUE KEY uk_dm_ten (DM_ten)
) COMMENT='Danh muc phan loai tai lieu';

CREATE TABLE LOAI_TAI_LIEU_PHAP_LY (
  LTLPL_id INT AUTO_INCREMENT PRIMARY KEY,
  LTLPL_ten VARCHAR(150),
  LTLPL_moTa TEXT,
  UNIQUE KEY uk_ltlpl_ten (LTLPL_ten)
) COMMENT='Loai tai lieu phap ly';

CREATE TABLE TAI_LIEU (
  TL_id INT AUTO_INCREMENT PRIMARY KEY,
  DM_id INT,
  LTLPL_id INT,
  VV_id INT,
  ND_nguoiTao_id INT,
  TL_ten TEXT,
  TL_duongDan TEXT,
  TL_dinhDang TEXT,
  TL_dungLuong BIGINT,
  TL_nguoiTao TEXT,
  TL_ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
  TL_ngayBanHanh DATE,
  TL_daXoa BOOLEAN DEFAULT FALSE,
  TL_baoMat TEXT,
  TL_ngayHetHan DATETIME,
  TL_soHieu VARCHAR(100),
  UNIQUE KEY uk_tl_so_hieu_ten (TL_soHieu, TL_ten(255)),
  KEY idx_tl_dm (DM_id),
  KEY idx_tl_ltlpl (LTLPL_id),
  KEY idx_tl_vv (VV_id),
  KEY idx_tl_nguoi_tao (ND_nguoiTao_id),
  KEY idx_tl_so_hieu (TL_soHieu),
  KEY idx_tl_ngay_ban_hanh (TL_ngayBanHanh),
  CONSTRAINT fk_tl_dm FOREIGN KEY (DM_id) REFERENCES DANH_MUC(DM_id),
  CONSTRAINT fk_tl_ltlpl FOREIGN KEY (LTLPL_id) REFERENCES LOAI_TAI_LIEU_PHAP_LY(LTLPL_id),
  CONSTRAINT fk_tl_vv FOREIGN KEY (VV_id) REFERENCES VU_VIEC(VV_id),
  CONSTRAINT fk_tl_nguoi_tao FOREIGN KEY (ND_nguoiTao_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id)
) COMMENT='Tai lieu noi bo va van ban phap ly';

CREATE TABLE PHIEN_BAN_TAI_LIEU (
  PBTL_id INT AUTO_INCREMENT PRIMARY KEY,
  TL_id INT,
  ND_update_id INT,
  PBTL_maPhienBan TEXT,
  PBTL_nguoiUpdate TEXT,
  PBTL_timeUpdate DATETIME,
  PBTL_ghiChu TEXT,
  KEY idx_pbtl_tl (TL_id),
  KEY idx_pbtl_nd (ND_update_id),
  CONSTRAINT fk_pbtl_tl FOREIGN KEY (TL_id) REFERENCES TAI_LIEU(TL_id),
  CONSTRAINT fk_pbtl_nd FOREIGN KEY (ND_update_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id)
) COMMENT='Phien ban tai lieu';

CREATE TABLE LICH_SU_HOAT_DONG (
  LS_id INT AUTO_INCREMENT PRIMARY KEY,
  ND_id INT,
  TL_id INT,
  LS_hoatDong TEXT,
  LS_thoiGianSua DATETIME,
  LS_diaChiIP TEXT,
  KEY idx_ls_nd (ND_id),
  KEY idx_ls_tl (TL_id),
  CONSTRAINT fk_ls_nd FOREIGN KEY (ND_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id),
  CONSTRAINT fk_ls_tl FOREIGN KEY (TL_id) REFERENCES TAI_LIEU(TL_id)
) COMMENT='Lich su hoat dong';

CREATE TABLE QUYEN_TRUY_CAP (
  QTC_id INT AUTO_INCREMENT PRIMARY KEY,
  TL_id INT,
  ND_id INT,
  QTC_loaiQuyen TEXT,
  QTC_thoiHan DATETIME,
  KEY idx_qtc_tl (TL_id),
  KEY idx_qtc_nd (ND_id),
  CONSTRAINT fk_qtc_tl FOREIGN KEY (TL_id) REFERENCES TAI_LIEU(TL_id),
  CONSTRAINT fk_qtc_nd FOREIGN KEY (ND_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id)
) COMMENT='Quyen truy cap tai lieu';

CREATE TABLE DUYET_TAI_LIEU (
  DTL_id INT AUTO_INCREMENT PRIMARY KEY,
  TL_id INT,
  ND_duyet_id INT,
  DTL_trangThai TEXT,
  DTL_ghiChu TEXT,
  DTL_timeDuyet DATETIME,
  KEY idx_dtl_tl (TL_id),
  KEY idx_dtl_nd (ND_duyet_id),
  CONSTRAINT fk_dtl_tl FOREIGN KEY (TL_id) REFERENCES TAI_LIEU(TL_id),
  CONSTRAINT fk_dtl_nd FOREIGN KEY (ND_duyet_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id)
) COMMENT='Trang thai duyet tai lieu';

CREATE TABLE THONG_BAO (
  TB_id INT AUTO_INCREMENT PRIMARY KEY,
  ND_id INT,
  TL_id INT,
  TB_tieuDe TEXT,
  TB_noiDung TEXT,
  TB_trangThaiDoc BOOLEAN DEFAULT FALSE,
  KEY idx_tb_nd (ND_id),
  KEY idx_tb_tl (TL_id),
  CONSTRAINT fk_tb_nd FOREIGN KEY (ND_id) REFERENCES TAI_KHOAN_NGUOI_DUNG(ND_id),
  CONSTRAINT fk_tb_tl FOREIGN KEY (TL_id) REFERENCES TAI_LIEU(TL_id)
) COMMENT='Thong bao cho nguoi dung';
