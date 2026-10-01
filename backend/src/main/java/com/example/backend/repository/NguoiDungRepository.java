package com.example.backend.repository;
import com.example.backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    // Tìm người dùng theo mã sinh viên
    // Optional dùng để xử lý trường hợp không tìm thấy người dùng
    Optional<NguoiDung> findByMasv(String ma_sv);
    // Tìm người dùng theo email
    Optional<NguoiDung> findByEmail(String email);

    // Kiểm tra mã sinh viên đã tồn tại trong database chưa
    // true  -> đã tồn tại
    // false -> chưa tồn tại
    boolean existsByMasv(String ma_sv);

    // Kiểm tra email đã tồn tại trong database chưa
    // true  -> đã tồn tại
    // false -> chưa tồn tại
    boolean existsByEmail(String email);

}
