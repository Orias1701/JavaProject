-- Active: 1743462407381@@127.0.0.1@3306@ql_khachsan
DROP DATABASE IF EXISTS `ql_khachsan`

CREATE DATABASE IF NOT EXISTS `ql_khachsan`

USE `ql_khachsan`;

-- Nhân viên
DROP TABLE IF EXISTS `nhanvien`;
CREATE TABLE `nhanvien` (
  `MaNhanVien` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã nhân viên',
  `TenNhanVien` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Tên nhân viên',
  `Sdt` varchar(15) NOT NULL COMMENT 'Số điện thoại',
  `GioiTinh` enum('Nam','Nữ') NOT NULL COMMENT 'Giới tính',
  PRIMARY KEY (`MaNhanVien`),
  UNIQUE KEY `Sdt` (`Sdt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'NHÂN VIÊN';
LOCK TABLES `nhanvien` WRITE;
INSERT INTO `nhanvien` VALUES ('NV001','Nguyen Van X','0912345678','Nam'),('NV002','Tran Thi Y','0922345678','Nữ'),('NV003','Pham Van Z','0932345678','Nam'),('NV004','Le Thi M','0942345678','Nữ'),('NV005','Hoang Van N','0952345678','Nam');
UNLOCK TABLES;

-- Khách hàng
DROP TABLE IF EXISTS `khachhang`;
CREATE TABLE `khachhang` (
  `MaKhachHang` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã khách hàng',
  `TenKhachHang` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Tên khách hàng',
  `Sdt` varchar(15) NOT NULL COMMENT 'Số điện thoại',
  `TinhTrangKhach` enum('Đang ở','Đã rời đi','Hủy đặt phòng') NOT NULL COMMENT 'Tình trạng',
  PRIMARY KEY (`MaKhachHang`),
  UNIQUE KEY `Sdt` (`Sdt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'KHÁCH HÀNG';
LOCK TABLES `khachhang` WRITE;
INSERT INTO `khachhang` VALUES ('KH001','Nguyen Van A','0987654321','Đang ở'),('KH002','Le Thi B','0977654321','Đã rời đi'),('KH003','Tran Van C','0967654321','Hủy đặt phòng'),('KH004','Pham Thi D','0957654321','Đang ở'),('KH005','Hoang Van E','0947654321','Đã rời đi');
UNLOCK TABLES;

-- Loại Phòng
DROP TABLE IF EXISTS `loaiphong`;
CREATE TABLE `loaiphong` (
  `Maloai` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã loại phòng',
  `TenLoaiPhong` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Tên loại phòng',
  `GiaLoai` decimal(10,2) NOT NULL COMMENT 'Giá loại phòng',
  PRIMARY KEY (`Maloai`),
  CONSTRAINT `loaiphong_chk_1` CHECK ((`TenLoaiPhong` in (_utf8mb4'VIP',_utf8mb4'Đơn',_utf8mb4'Đôi')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'LOẠI PHÒNG';
LOCK TABLES `loaiphong` WRITE;
INSERT INTO `loaiphong` VALUES ('LP001','VIP',2000000.00),('LP002','Đơn',800000.00),('LP003','Đôi',1200000.00);
UNLOCK TABLES;

-- Phòng
DROP TABLE IF EXISTS `phong`;
CREATE TABLE `phong` (
  `MaPhong` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã phòng',
  `TenPhong` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Tên phòng',
  `MaLoai` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã loại phòng',
  `MoTa` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Mô tả phòng',
  `TinhTrangPhong` enum('Trống','Đang sử dụng','Đã đặt trước','Bảo trì') NOT NULL COMMENT 'Tình trạng',
  PRIMARY KEY (`MaPhong`),
  KEY `MaLoai` (`MaLoai`),
  CONSTRAINT `phong_ibfk_1` FOREIGN KEY (`MaLoai`) REFERENCES `loaiphong` (`Maloai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'QUẢN LÝ PHÒNG';
LOCK TABLES `phong` WRITE;
INSERT INTO `phong` VALUES ('P101','Phòng 101','LP001','Hướng biển','Trống'),('P102','Phòng 102','LP002','Có ban công','Đang sử dụng'),('P103','Phòng 103','LP003','Gần hồ bơi','Đã đặt trước'),('P104','Phòng 104','LP001','VIP view đẹp','Trống'),('P105','Phòng 105','LP002','Có cửa sổ','Bảo trì'),('P106','Phòng 106','LP003','Hướng núi','Trống'),('P107','Phòng 107','LP001','VIP rộng rãi','Đã đặt trước'),('P108','Phòng 108','LP002','Có bồn tắm','Đang sử dụng'),('P109','Phòng 109','LP003','Gần phòng gym','Trống'),('P110','Phòng 110','LP001','Siêu VIP','Trống');
UNLOCK TABLES;

-- Hóa đơn
DROP TABLE IF EXISTS `hoadon`;
CREATE TABLE `hoadon` (
  `MaHoaDon` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã hóa đơn',
  `MaNhanVien` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã nhân viên',
  `MaKhachHang` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã khách hàng',
  `Ngay` date NOT NULL COMMENT 'Ngày tạo',
  PRIMARY KEY (`MaHoaDon`),
  KEY `MaNhanVien` (`MaNhanVien`),
  KEY `MaKhachHang` (`MaKhachHang`),
  CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`MaNhanVien`) REFERENCES `nhanvien` (`MaNhanVien`),
  CONSTRAINT `hoadon_ibfk_2` FOREIGN KEY (`MaKhachHang`) REFERENCES `khachhang` (`MaKhachHang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'HÓA ĐƠN';
LOCK TABLES `hoadon` WRITE;
INSERT INTO `hoadon` VALUES ('HD001','NV001','KH001','2025-03-01'),('HD002','NV002','KH002','2025-03-02'),('HD003','NV003','KH003','2025-03-03');
UNLOCK TABLES;


--Phiếu đặt phòng
DROP TABLE IF EXISTS `phieudatphong`;
CREATE TABLE `phieudatphong` (
  `MaPhieuDatPhong` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã phiếu',
  `MaPhong` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã phòng',
  `MaNhanVien` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã nhân viên',
  `MaKhachHang` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã khách hàng',
  `MaHoaDon` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'Mã hóa đơn',
  `NgayNhanPhong` date NOT NULL COMMENT 'Ngày nhận',
  `NgayTraPhong` date NOT NULL COMMENT 'Ngày trả',
  PRIMARY KEY (`MaPhieuDatPhong`),
  KEY `MaPhong` (`MaPhong`),
  KEY `MaNhanVien` (`MaNhanVien`),
  KEY `MaKhachHang` (`MaKhachHang`),
  KEY `MaHoaDon` (`MaHoaDon`),
  CONSTRAINT `phieudatphong_ibfk_1` FOREIGN KEY (`MaPhong`) REFERENCES `phong` (`MaPhong`),
  CONSTRAINT `phieudatphong_ibfk_2` FOREIGN KEY (`MaNhanVien`) REFERENCES `nhanvien` (`MaNhanVien`),
  CONSTRAINT `phieudatphong_ibfk_3` FOREIGN KEY (`MaKhachHang`) REFERENCES `khachhang` (`MaKhachHang`),
  CONSTRAINT `phieudatphong_ibfk_4` FOREIGN KEY (`MaHoaDon`) REFERENCES `hoadon` (`MaHoaDon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'PHIẾU ĐẶT PHÒNG';
LOCK TABLES `phieudatphong` WRITE;
INSERT INTO `phieudatphong` VALUES ('PDP001','P101','NV001','KH001','HD001','2025-03-01','2025-03-05'),('PDP002','P102','NV002','KH002','HD002','2025-03-02','2025-03-06'),('PDP003','P103','NV003','KH003','HD003','2025-03-03','2025-03-07');
UNLOCK TABLES;

-- Dịch vụ
DROP TABLE IF EXISTS `dichvu`;
CREATE TABLE `dichvu` (
  `MaDichVu` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã dịch vụ',
  `LoaiDichVu` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Loại dịch vụ',
  `GiaDichVu` decimal(10,2) NOT NULL COMMENT 'Giá dịch vụ',
  PRIMARY KEY (`MaDichVu`),
  CONSTRAINT `dichvu_chk_1` CHECK ((`LoaiDichVu` in (_utf8mb4'Ăn uống',_utf8mb4'Giặt ủi',_utf8mb4'Spa',_utf8mb4'Thuê xe',_utf8mb4'Công viên',_utf8mb4'Massage',_utf8mb4'Bể bơi')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'DỊCH VỤ';
LOCK TABLES `dichvu` WRITE;
INSERT INTO `dichvu` VALUES ('DV001','Ăn uống',500000.00),('DV002','Giặt ủi',200000.00),('DV003','Spa',1000000.00),('DV004','Thuê xe',300000.00),('DV005','Công viên',150000.00),('DV006','Massage',800000.00),('DV007','Bể bơi',250000.00);
UNLOCK TABLES;

-- Phiếu dịch vụ
DROP TABLE IF EXISTS `phieusddv`;
CREATE TABLE `phieusddv` (
  `MaPhieuSDDV` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã phiếu',
  `MaKhachHang` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã khách hàng',
  `MaNhanVien` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã nhân viên',
  `NgaySDDV` date NOT NULL COMMENT 'Ngày sử dụng',
  PRIMARY KEY (`MaPhieuSDDV`),
  KEY `MaKhachHang` (`MaKhachHang`),
  KEY `MaNhanVien` (`MaNhanVien`),
  CONSTRAINT `phieusddv_ibfk_1` FOREIGN KEY (`MaKhachHang`) REFERENCES `khachhang` (`MaKhachHang`),
  CONSTRAINT `phieusddv_ibfk_2` FOREIGN KEY (`MaNhanVien`) REFERENCES `nhanvien` (`MaNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'PHIẾU DỊCH VỤ';
LOCK TABLES `phieusddv` WRITE;
INSERT INTO `phieusddv` VALUES ('PSDDV001','KH001','NV001','2025-03-01'),('PSDDV002','KH002','NV002','2025-03-02'),('PSDDV003','KH003','NV003','2025-03-03');
UNLOCK TABLES;

-- Chi tiết phiếu sử dụng dịch vụ
DROP TABLE IF EXISTS `chitietphieusddv`;
CREATE TABLE `chitietphieusddv` (
  `MaPhieuSDDV` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã phiếu',
  `MaDichVu` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'Mã dịch vụ',
  `SoLuongDichVu` int NOT NULL DEFAULT '1' COMMENT 'Số lượng',
  PRIMARY KEY (`MaPhieuSDDV`,`MaDichVu`),
  KEY `MaDichVu` (`MaDichVu`),
  CONSTRAINT `chitietphieusddv_ibfk_1` FOREIGN KEY (`MaPhieuSDDV`) REFERENCES `phieusddv` (`MaPhieuSDDV`),
  CONSTRAINT `chitietphieusddv_ibfk_2` FOREIGN KEY (`MaDichVu`) REFERENCES `dichvu` (`MaDichVu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'CHI TIẾT';
LOCK TABLES `chitietphieusddv` WRITE;
INSERT INTO `chitietphieusddv` VALUES ('PSDDV001','DV001',2),('PSDDV001','DV002',1),('PSDDV002','DV003',3),('PSDDV002','DV004',2),('PSDDV003','DV005',1),('PSDDV003','DV006',2);
UNLOCK TABLES;
