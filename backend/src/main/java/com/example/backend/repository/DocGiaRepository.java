package com.example.backend.repository;

import com.example.backend.entity.DocGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocGiaRepository extends JpaRepository<DocGia, Integer> {

    Optional<DocGia> findByNguoiDungId(Integer nguoiDungId);

    @Query("SELECT dg FROM DocGia dg " +
           "JOIN FETCH dg.nguoiDung nd " +
           "ORDER BY dg.maTheDocGia")
    List<DocGia> findAllWithNguoiDung();

    // Lấy thẻ độc giả của nhiều người dùng trong 1 query (tránh N+1)
    List<DocGia> findAllByNguoiDungIdIn(List<Integer> nguoiDungIds);
}
