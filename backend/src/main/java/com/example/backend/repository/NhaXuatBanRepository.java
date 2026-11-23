package com.example.backend.repository;

import com.example.backend.entity.NhaXuatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Integer> {
    Optional<NhaXuatBan> findByTenNhaXuatBan(String tenNhaXuatBan);
}
