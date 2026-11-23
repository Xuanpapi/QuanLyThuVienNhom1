package com.example.backend.repository;

import com.example.backend.entity.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SachRepository extends JpaRepository<Sach, Integer> {
    
    @Query("SELECT DISTINCT s FROM Sach s " +
           "LEFT JOIN s.tacGias t " +
           "LEFT JOIN s.theLoais th " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.tenSach) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.tenTacGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(th.tenTheLoai) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.maSach) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Sach> searchBooks(@Param("keyword") String keyword);

    boolean existsByMaSach(String maSach);

    // Kiểm tra danh mục có đang được dùng bởi đầu sách nào không (chặn xóa)
    boolean existsByDanhMucId(Integer danhMucId);

    // Đếm số đầu sách đang dùng một thể loại (chặn xóa)
    @Query("SELECT COUNT(s) FROM Sach s JOIN s.theLoais tl WHERE tl.id = :theLoaiId")
    long countSachDungTheLoai(@Param("theLoaiId") Integer theLoaiId);
}
