package com.example.backend.service;

import com.example.backend.entity.DanhMuc;
import com.example.backend.repository.DanhMucRepository;
import com.example.backend.repository.SachRepository;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * US 2.2.3 - Quản lý danh mục sách (thêm / sửa / xóa).
 */
@Service
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final SachRepository sachRepository;

    public DanhMucService(DanhMucRepository danhMucRepository, SachRepository sachRepository) {
        this.danhMucRepository = danhMucRepository;
        this.sachRepository = sachRepository;
    }

    @Transactional(readOnly = true)
    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll(Sort.by(Sort.Direction.ASC, "tenDanhMuc"));
    }

    @Transactional
    public DanhMuc create(String tenDanhMuc, String moTa) {
        String ten = chuanHoaTen(tenDanhMuc);
        if (danhMucRepository.existsByTenDanhMucIgnoreCase(ten)) {
            throw new RuntimeException("Danh mục \"" + ten + "\" đã tồn tại");
        }
        return danhMucRepository.save(new DanhMuc(ten, chuanHoaMoTa(moTa)));
    }

    @Transactional
    public DanhMuc update(Integer id, String tenDanhMuc, String moTa) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        String ten = chuanHoaTen(tenDanhMuc);
        if (danhMucRepository.existsByTenDanhMucIgnoreCaseAndIdNot(ten, id)) {
            throw new RuntimeException("Danh mục \"" + ten + "\" đã tồn tại");
        }

        danhMuc.setTenDanhMuc(ten);
        danhMuc.setMoTa(chuanHoaMoTa(moTa));
        return danhMucRepository.save(danhMuc);
    }

    @Transactional
    public void delete(Integer id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        if (sachRepository.existsByDanhMucId(id)) {
            throw new RuntimeException("Không thể xóa: danh mục đang được dùng bởi một hoặc nhiều đầu sách");
        }
        danhMucRepository.delete(danhMuc);
    }

    private String chuanHoaTen(String tenDanhMuc) {
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }
        String ten = tenDanhMuc.trim();
        if (ten.length() > 100) {
            throw new RuntimeException("Tên danh mục không được vượt quá 100 ký tự");
        }
        return ten;
    }

    private String chuanHoaMoTa(String moTa) {
        if (moTa == null || moTa.trim().isEmpty()) {
            return null;
        }
        String value = moTa.trim();
        if (value.length() > 255) {
            throw new RuntimeException("Mô tả danh mục không được vượt quá 255 ký tự");
        }
        return value;
    }
}
