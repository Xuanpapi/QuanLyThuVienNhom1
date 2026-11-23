package com.example.backend.service;

import com.example.backend.entity.TheLoai;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SachRepository;
import com.example.backend.repository.TheLoaiRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * US 2.2.3 - Quản lý thể loại sách (thêm / sửa / xóa).
 */
@Service
public class TheLoaiService {

    private final TheLoaiRepository theLoaiRepository;
    private final SachRepository sachRepository;

    public TheLoaiService(TheLoaiRepository theLoaiRepository, SachRepository sachRepository) {
        this.theLoaiRepository = theLoaiRepository;
        this.sachRepository = sachRepository;
    }

    @Transactional(readOnly = true)
    public List<TheLoai> getAll() {
        return theLoaiRepository.findAll(Sort.by(Sort.Direction.ASC, "tenTheLoai"));
    }

    @Transactional
    public TheLoai create(String tenTheLoai) {
        String ten = chuanHoaTen(tenTheLoai);
        if (theLoaiRepository.existsByTenTheLoaiIgnoreCase(ten)) {
            throw new RuntimeException("Thể loại \"" + ten + "\" đã tồn tại");
        }
        return theLoaiRepository.save(new TheLoai(ten));
    }

    @Transactional
    public TheLoai update(Integer id, String tenTheLoai) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));

        String ten = chuanHoaTen(tenTheLoai);
        if (theLoaiRepository.existsByTenTheLoaiIgnoreCaseAndIdNot(ten, id)) {
            throw new RuntimeException("Thể loại \"" + ten + "\" đã tồn tại");
        }

        theLoai.setTenTheLoai(ten);
        return theLoaiRepository.save(theLoai);
    }

    @Transactional
    public void delete(Integer id) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));

        if (sachRepository.countSachDungTheLoai(id) > 0) {
            throw new RuntimeException("Không thể xóa: thể loại đang được dùng bởi một hoặc nhiều đầu sách");
        }
        theLoaiRepository.delete(theLoai);
    }

    private String chuanHoaTen(String tenTheLoai) {
        if (tenTheLoai == null || tenTheLoai.trim().isEmpty()) {
            throw new RuntimeException("Tên thể loại không được để trống");
        }
        String ten = tenTheLoai.trim();
        if (ten.length() > 100) {
            throw new RuntimeException("Tên thể loại không được vượt quá 100 ký tự");
        }
        return ten;
    }
}
