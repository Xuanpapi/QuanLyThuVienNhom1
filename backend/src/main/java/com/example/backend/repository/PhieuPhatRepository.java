package com.example.backend.repository;

import com.example.backend.entity.PhieuPhat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuPhatRepository extends JpaRepository<PhieuPhat, Integer> {

    @Query("SELECT pp FROM PhieuPhat pp " +
           "LEFT JOIN FETCH pp.phieuMuon pm " +
           "LEFT JOIN FETCH pp.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "ORDER BY pp.ngayTao DESC, pp.id DESC")
    List<PhieuPhat> findAllWithDetails();

    @Query("SELECT pp FROM PhieuPhat pp " +
           "LEFT JOIN FETCH pp.phieuMuon pm " +
           "LEFT JOIN FETCH pp.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "WHERE pp.phieuMuonId = :phieuMuonId " +
           "ORDER BY pp.ngayTao DESC, pp.id DESC")
    List<PhieuPhat> findByPhieuMuonIdWithDetails(@Param("phieuMuonId") Integer phieuMuonId);

    @Query("SELECT pp FROM PhieuPhat pp " +
           "LEFT JOIN FETCH pp.phieuMuon pm " +
           "LEFT JOIN FETCH pp.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "WHERE pp.id = :id")
    Optional<PhieuPhat> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT pp FROM PhieuPhat pp " +
           "LEFT JOIN FETCH pp.phieuMuon pm " +
           "LEFT JOIN FETCH pp.docGia dg " +
           "LEFT JOIN FETCH dg.nguoiDung nd " +
           "WHERE dg.nguoiDungId = :nguoiDungId " +
           "ORDER BY pp.ngayTao DESC, pp.id DESC")
    List<PhieuPhat> findByNguoiDungIdWithDetails(@Param("nguoiDungId") Integer nguoiDungId);
}
