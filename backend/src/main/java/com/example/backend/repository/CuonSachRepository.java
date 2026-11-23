package com.example.backend.repository;

import com.example.backend.entity.CuonSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CuonSachRepository extends JpaRepository<CuonSach, Integer> {

    /**
     * Lấy tất cả cuốn đang sẵn sàng của một đầu sách.
     * Trả về List vì một đầu sách có thể có nhiều cuốn cùng trạng thái CÓ_SẴN.
     */
    @Query("SELECT cs FROM CuonSach cs " +
           "WHERE cs.sachId = :sachId AND (cs.trangThai = 'CÓ_SẴN' OR cs.trangThai IS NULL) " +
           "ORDER BY cs.maCuonSach")
    List<CuonSach> findAvailableBySachId(@Param("sachId") Integer sachId);

    @Query("SELECT cs FROM CuonSach cs " +
           "JOIN FETCH cs.sach s " +
           "WHERE cs.sachId = :sachId " +
           "ORDER BY cs.maCuonSach")
    List<CuonSach> findBySachIdWithSach(@Param("sachId") Integer sachId);

    @Query("SELECT cs FROM CuonSach cs " +
           "JOIN FETCH cs.sach s " +
           "WHERE cs.sachId = :sachId AND (cs.trangThai = 'CÓ_SẴN' OR cs.trangThai IS NULL) " +
           "ORDER BY cs.maCuonSach")
    List<CuonSach> findAvailableCopiesBySachId(@Param("sachId") Integer sachId);
}
