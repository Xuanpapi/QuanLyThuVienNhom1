package com.example.backend.repository;

import com.example.backend.entity.ChiTietPhieuTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ChiTietPhieuTraRepository extends JpaRepository<ChiTietPhieuTra, Integer> {

    @Query("SELECT ct FROM ChiTietPhieuTra ct " +
           "JOIN FETCH ct.phieuTra pt " +
           "WHERE pt.phieuMuonId = :phieuMuonId")
    List<ChiTietPhieuTra> findByPhieuMuonId(@Param("phieuMuonId") Integer phieuMuonId);

    @Query("SELECT ct FROM ChiTietPhieuTra ct " +
           "JOIN FETCH ct.phieuTra pt " +
           "WHERE pt.phieuMuonId IN :phieuMuonIds")
    List<ChiTietPhieuTra> findByPhieuMuonIds(@Param("phieuMuonIds") Collection<Integer> phieuMuonIds);
}
