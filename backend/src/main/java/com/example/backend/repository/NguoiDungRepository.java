package com.example.backend.repository;
import com.example.backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer>, JpaSpecificationExecutor<NguoiDung> {
    // Tìm người dùng theo mã sinh viên
    // Optional dùng để xử lý trường hợp không tìm thấy người dùng
    @Query("SELECT u FROM NguoiDung u WHERE u.ma_sv = :ma_sv")
    Optional<NguoiDung> findByMasv(@Param("ma_sv") String ma_sv);

    // Tìm người dùng theo email
    Optional<NguoiDung> findByEmail(String email);

    // Tìm người dùng theo mã sinh viên HOẶC email (phục vụ đăng nhập)
    @Query("SELECT u FROM NguoiDung u WHERE u.ma_sv = :taiKhoan OR u.email = :taiKhoan")
    Optional<NguoiDung> findByMaSvOrEmail(@Param("taiKhoan") String taiKhoan);

    // Kiểm tra mã sinh viên đã tồn tại trong database chưa
    // true  -> đã tồn tại
    // false -> chưa tồn tại
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM NguoiDung u WHERE u.ma_sv = :ma_sv")
    boolean existsByMasv(@Param("ma_sv") String ma_sv);

    // Kiểm tra email đã tồn tại trong database chưa
    // true  -> đã tồn tại
    // false -> chưa tồn tại
    boolean existsByEmail(String email);

}
