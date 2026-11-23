package com.example.backend.repository;

import com.example.backend.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {
    Optional<TacGia> findByTenTacGia(String tenTacGia);
}
