package com.example.backend.controller;

import com.example.backend.entity.DanhMuc;
import com.example.backend.entity.NhaXuatBan;
import com.example.backend.entity.TacGia;
import com.example.backend.entity.TheLoai;
import com.example.backend.repository.DanhMucRepository;
import com.example.backend.repository.NhaXuatBanRepository;
import com.example.backend.repository.TacGiaRepository;
import com.example.backend.repository.TheLoaiRepository;
import com.example.backend.repository.VaiTroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final DanhMucRepository danhMucRepository;
    private final TacGiaRepository tacGiaRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final VaiTroRepository vaiTroRepository;

    public MetadataController(
            NhaXuatBanRepository nhaXuatBanRepository,
            DanhMucRepository danhMucRepository,
            TacGiaRepository tacGiaRepository,
            TheLoaiRepository theLoaiRepository,
            VaiTroRepository vaiTroRepository) {
        this.nhaXuatBanRepository = nhaXuatBanRepository;
        this.danhMucRepository = danhMucRepository;
        this.tacGiaRepository = tacGiaRepository;
        this.theLoaiRepository = theLoaiRepository;
        this.vaiTroRepository = vaiTroRepository;
    }

    @GetMapping("/nhaxuatban")
    public ResponseEntity<List<NhaXuatBan>> getAllNhaXuatBan() {
        return ResponseEntity.ok(nhaXuatBanRepository.findAll());
    }

    @GetMapping("/danhmuc")
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        return ResponseEntity.ok(danhMucRepository.findAll());
    }

    @GetMapping("/tacgia")
    public ResponseEntity<List<TacGia>> getAllTacGia() {
        return ResponseEntity.ok(tacGiaRepository.findAll());
    }

    @GetMapping("/theloai")
    public ResponseEntity<List<TheLoai>> getAllTheLoai() {
        return ResponseEntity.ok(theLoaiRepository.findAll());
    }

    @GetMapping("/vaitro")
    public ResponseEntity<?> getAllVaiTro() {
        return ResponseEntity.ok(vaiTroRepository.findAll());
    }
}
