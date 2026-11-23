package com.example.backend.service;

import com.example.backend.dto.CuonSachOptionDTO;
import com.example.backend.dto.SachResponseDTO;
import com.example.backend.dto.SachSearchResponse;
import com.example.backend.entity.CuonSach;
import com.example.backend.entity.DanhMuc;
import com.example.backend.entity.NhaXuatBan;
import com.example.backend.entity.Sach;
import com.example.backend.entity.TacGia;
import com.example.backend.entity.TheLoai;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SachService {
    private final SachRepository sachRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final DanhMucRepository danhMucRepository;
    private final TacGiaRepository tacGiaRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final CuonSachRepository cuonSachRepository;

    public SachService(
            SachRepository sachRepository,
            NhaXuatBanRepository nhaXuatBanRepository,
            DanhMucRepository danhMucRepository,
            TacGiaRepository tacGiaRepository,
            TheLoaiRepository theLoaiRepository,
            CuonSachRepository cuonSachRepository) {
        this.sachRepository = sachRepository;
        this.nhaXuatBanRepository = nhaXuatBanRepository;
        this.danhMucRepository = danhMucRepository;
        this.tacGiaRepository = tacGiaRepository;
        this.theLoaiRepository = theLoaiRepository;
        this.cuonSachRepository = cuonSachRepository;
    }

    public List<SachSearchResponse> getAllBooks() {
        return sachRepository.findAll().stream()
                .map(SachSearchResponse::new)
                .collect(Collectors.toList());
    }

    public List<SachSearchResponse> searchBooks(String keyword) {
        return sachRepository.searchBooks(keyword).stream()
                .map(SachSearchResponse::new)
                .collect(Collectors.toList());
    }

    public SachResponseDTO getBookById(Integer id) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        return new SachResponseDTO(sach);
    }

    /**
     * Lấy danh sách cuốn sách vật lý của một đầu sách.
     *
     * @param chiLayKhaDung true: chỉ lấy các cuốn đang CÓ_SẴN (dùng cho form lập phiếu mượn)
     */
    @Transactional(readOnly = true)
    public List<CuonSachOptionDTO> getCuonSachBySachId(Integer sachId, boolean chiLayKhaDung) {
        if (!sachRepository.existsById(sachId)) {
            throw new ResourceNotFoundException("Không tìm thấy sách với ID: " + sachId);
        }

        List<CuonSach> dsCuonSach = chiLayKhaDung
                ? cuonSachRepository.findAvailableCopiesBySachId(sachId)
                : cuonSachRepository.findBySachIdWithSach(sachId);

        return dsCuonSach.stream()
                .map(CuonSachOptionDTO::new)
                .collect(Collectors.toList());
    }

    public SachResponseDTO createBook(Sach sach) {
        if (sach.getMaSach() != null && !sach.getMaSach().isEmpty() && sachRepository.existsByMaSach(sach.getMaSach())) {
            throw new RuntimeException("Mã sách đã tồn tại trong hệ thống");
        }

        sach.setNhaXuatBanId(resolveNhaXuatBanId(sach));
        sach.setDanhMucId(resolveDanhMucId(sach));
        sach.setTacGias(resolveTacGias(sach.getTacGias()));
        sach.setTheLoais(resolveTheLoais(sach.getTheLoais()));

        Sach saved = sachRepository.save(sach);
        return new SachResponseDTO(saved);
    }

    /**
     * US 2.2.2 - Cập nhật thông tin đầu sách.
     * Mã sách được giữ nguyên để không phá vỡ liên kết với các cuốn sách vật lý.
     */
    @Transactional
    public SachResponseDTO updateBook(Integer id, Sach sachDetails) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));

        if (sachDetails.getTenSach() == null || sachDetails.getTenSach().trim().isEmpty()) {
            throw new BadRequestException("Tên sách không được để trống");
        }

        sach.setTenSach(sachDetails.getTenSach().trim());
        sach.setMoTa(sachDetails.getMoTa());
        sach.setNamXuatBan(sachDetails.getNamXuatBan());

        Integer nhaXuatBanId = resolveNhaXuatBanId(sachDetails);
        if (nhaXuatBanId != null) {
            sach.setNhaXuatBanId(nhaXuatBanId);
        }

        Integer danhMucId = resolveDanhMucId(sachDetails);
        if (danhMucId != null) {
            sach.setDanhMucId(danhMucId);
        }

        if (sachDetails.getTacGias() != null && !sachDetails.getTacGias().isEmpty()) {
            sach.setTacGias(resolveTacGias(sachDetails.getTacGias()));
        }
        if (sachDetails.getTheLoais() != null && !sachDetails.getTheLoais().isEmpty()) {
            sach.setTheLoais(resolveTheLoais(sachDetails.getTheLoais()));
        }

        Sach updated = sachRepository.save(sach);
        return new SachResponseDTO(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: chuẩn hóa NXB / Danh mục / Tác giả / Thể loại
    // ─────────────────────────────────────────────────────────────

    /** Chọn NXB theo ID có sẵn, hoặc tạo mới theo tên gửi lên. */
    private Integer resolveNhaXuatBanId(Sach sach) {
        Integer nhaXuatBanId = sach.getNhaXuatBanId();

        if (nhaXuatBanId == null && sach.getNhaXuatBan() != null) {
            if (sach.getNhaXuatBan().getId() != null) {
                nhaXuatBanId = sach.getNhaXuatBan().getId();
            } else {
                String tenNxb = sach.getNhaXuatBan().getTenNhaXuatBan();
                if (tenNxb != null && !tenNxb.trim().isEmpty()) {
                    nhaXuatBanId = nhaXuatBanRepository.findByTenNhaXuatBan(tenNxb.trim())
                            .orElseGet(() -> nhaXuatBanRepository.save(new NhaXuatBan(tenNxb.trim(), null, null)))
                            .getId();
                }
            }
        }

        if (nhaXuatBanId != null) {
            final Integer idNhaXuatBan = nhaXuatBanId;
            nhaXuatBanRepository.findById(idNhaXuatBan)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà xuất bản với ID: " + idNhaXuatBan));
        }
        return nhaXuatBanId;
    }

    /** Chọn Danh mục theo ID có sẵn, hoặc tạo mới theo tên gửi lên. */
    private Integer resolveDanhMucId(Sach sach) {
        Integer danhMucId = sach.getDanhMucId();

        if (danhMucId == null && sach.getDanhMuc() != null) {
            if (sach.getDanhMuc().getId() != null) {
                danhMucId = sach.getDanhMuc().getId();
            } else {
                String tenDm = sach.getDanhMuc().getTenDanhMuc();
                if (tenDm != null && !tenDm.trim().isEmpty()) {
                    danhMucId = danhMucRepository.findByTenDanhMuc(tenDm.trim())
                            .orElseGet(() -> danhMucRepository.save(new DanhMuc(tenDm.trim())))
                            .getId();
                }
            }
        }

        if (danhMucId != null) {
            final Integer idDanhMuc = danhMucId;
            danhMucRepository.findById(idDanhMuc)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + idDanhMuc));
        }
        return danhMucId;
    }

    /** Chọn tác giả theo ID có sẵn, hoặc tạo mới theo tên gửi lên. */
    private Set<TacGia> resolveTacGias(Set<TacGia> danhSach) {
        Set<TacGia> ketQua = new HashSet<>();
        if (danhSach == null) {
            return ketQua;
        }
        for (TacGia tg : danhSach) {
            if (tg.getId() != null) {
                TacGia existingTg = tacGiaRepository.findById(tg.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + tg.getId()));
                ketQua.add(existingTg);
            } else if (tg.getTenTacGia() != null && !tg.getTenTacGia().trim().isEmpty()) {
                String tenTg = tg.getTenTacGia().trim();
                TacGia resolvedTg = tacGiaRepository.findByTenTacGia(tenTg)
                        .orElseGet(() -> tacGiaRepository.save(new TacGia(tenTg, null)));
                ketQua.add(resolvedTg);
            }
        }
        return ketQua;
    }

    /** Chọn thể loại theo ID có sẵn, hoặc tạo mới theo tên gửi lên. */
    private Set<TheLoai> resolveTheLoais(Set<TheLoai> danhSach) {
        Set<TheLoai> ketQua = new HashSet<>();
        if (danhSach == null) {
            return ketQua;
        }
        for (TheLoai tl : danhSach) {
            if (tl.getId() != null) {
                TheLoai existingTl = theLoaiRepository.findById(tl.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + tl.getId()));
                ketQua.add(existingTl);
            } else if (tl.getTenTheLoai() != null && !tl.getTenTheLoai().trim().isEmpty()) {
                String tenTl = tl.getTenTheLoai().trim();
                TheLoai resolvedTl = theLoaiRepository.findByTenTheLoai(tenTl)
                        .orElseGet(() -> theLoaiRepository.save(new TheLoai(tenTl)));
                ketQua.add(resolvedTl);
            }
        }
        return ketQua;
    }

    public void deleteBook(Integer id) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        sachRepository.delete(sach);
    }
}
