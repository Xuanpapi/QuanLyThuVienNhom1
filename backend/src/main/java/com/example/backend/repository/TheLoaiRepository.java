package com.example.backend.repository;

import com.example.backend.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {
    Optional<TheLoai> findByTenTheLoai(String tenTheLoai);

    boolean existsByTenTheLoaiIgnoreCase(String tenTheLoai);

    boolean existsByTenTheLoaiIgnoreCaseAndIdNot(String tenTheLoai, Integer id);
}
