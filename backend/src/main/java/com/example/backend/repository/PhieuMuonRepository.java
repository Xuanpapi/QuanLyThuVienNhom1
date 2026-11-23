package com.example.backend.repository;

import com.example.backend.entity.PhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, Integer> {

    @Query("SELECT DISTINCT pm FROM PhieuMuon pm " +
           "LEFT JOIN FETCH pm.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "LEFT JOIN FETCH pm.chiTietPhieuMuons ct " +
           "LEFT JOIN FETCH ct.cuonSach cs " +
           "LEFT JOIN FETCH cs.sach s " +
           "WHERE pm.docGiaId = :docGiaId " +
           "ORDER BY pm.ngayMuon DESC")
    List<PhieuMuon> findByDocGiaIdWithDetails(@Param("docGiaId") Integer docGiaId);

    @Query("SELECT DISTINCT pm FROM PhieuMuon pm " +
           "LEFT JOIN FETCH pm.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "LEFT JOIN FETCH pm.chiTietPhieuMuons ct " +
           "LEFT JOIN FETCH ct.cuonSach cs " +
           "LEFT JOIN FETCH cs.sach s " +
           "ORDER BY pm.ngayMuon DESC")
    List<PhieuMuon> findAllWithDetails();

    @Query("SELECT DISTINCT pm FROM PhieuMuon pm " +
           "LEFT JOIN FETCH pm.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "LEFT JOIN FETCH pm.chiTietPhieuMuons ct " +
           "LEFT JOIN FETCH ct.cuonSach cs " +
           "LEFT JOIN FETCH cs.sach s " +
           "WHERE pm.trangThai = :trangThai " +
           "ORDER BY pm.ngayMuon DESC")
    List<PhieuMuon> findByTrangThaiWithDetails(@Param("trangThai") String trangThai);

    @Query("SELECT DISTINCT pm FROM PhieuMuon pm " +
           "LEFT JOIN FETCH pm.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "LEFT JOIN FETCH pm.chiTietPhieuMuons ct " +
           "LEFT JOIN FETCH ct.cuonSach cs " +
           "LEFT JOIN FETCH cs.sach s " +
           "WHERE pm.id = :id")
    Optional<PhieuMuon> findByIdWithDetails(@Param("id") Integer id);

    // US 3.2.2 - Các phiếu đang mượn mà hạn trả đã qua (quá hạn)
    @Query("SELECT DISTINCT pm FROM PhieuMuon pm " +
           "LEFT JOIN FETCH pm.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "LEFT JOIN FETCH pm.chiTietPhieuMuons ct " +
           "LEFT JOIN FETCH ct.cuonSach cs " +
           "LEFT JOIN FETCH cs.sach s " +
           "WHERE pm.trangThai = :trangThai AND pm.hanTra < :homNay " +
           "ORDER BY pm.hanTra ASC")
    List<PhieuMuon> findQuaHan(@Param("trangThai") String trangThai, @Param("homNay") LocalDate homNay);
}
