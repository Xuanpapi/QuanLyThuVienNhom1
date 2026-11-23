package com.example.backend.service;

import com.example.backend.common.TrangThaiPhieuMuon;
import com.example.backend.common.TrangThaiThanhToan;
import com.example.backend.dto.PhieuPhatResponseDTO;
import com.example.backend.dto.TaoPhieuPhatRequest;
import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.PhieuMuon;
import com.example.backend.entity.PhieuPhat;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PhieuMuonRepository;
import com.example.backend.repository.PhieuPhatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * US 3.2.3 - Ghi nhận phạt quá hạn / làm hỏng sách và quản lý thanh toán.
 */
@Service
public class PhieuPhatService {

    /** Giá trị tối đa cho cột NUMERIC(12,2). */
    private static final BigDecimal SO_TIEN_TOI_DA = new BigDecimal("9999999999.99");

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuPhatRepository phieuPhatRepository;
    private final PhieuMuonRepository phieuMuonRepository;
    private final EmailService emailService;

    public PhieuPhatService(PhieuPhatRepository phieuPhatRepository,
                            PhieuMuonRepository phieuMuonRepository,
                            EmailService emailService) {
        this.phieuPhatRepository = phieuPhatRepository;
        this.phieuMuonRepository = phieuMuonRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<PhieuPhatResponseDTO> getDanhSach(String trangThai, String keyword) {
        List<PhieuPhat> dsPhieu = phieuPhatRepository.findAllWithDetails();

        if (trangThai != null && !trangThai.isBlank()) {
            String trangThaiLoc = trangThai.trim();
            dsPhieu = dsPhieu.stream()
                    .filter(pp -> trangThaiLoc.equals(pp.getTrangThaiThanhToan()))
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase(Locale.ROOT);
            dsPhieu = dsPhieu.stream()
                    .filter(pp -> khopTuKhoa(pp, kw))
                    .collect(Collectors.toList());
        }

        return dsPhieu.stream().map(PhieuPhatResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PhieuPhatResponseDTO> getTheoPhieuMuon(Integer phieuMuonId) {
        return phieuPhatRepository.findByPhieuMuonIdWithDetails(phieuMuonId).stream()
                .map(PhieuPhatResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhieuPhatResponseDTO taoPhieuPhat(TaoPhieuPhatRequest request) {
        PhieuMuon phieuMuon = phieuMuonRepository.findById(request.getPhieuMuonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiếu mượn với ID: " + request.getPhieuMuonId()));

        if (!TrangThaiPhieuMuon.DANG_MUON.equals(phieuMuon.getTrangThai())
                && !TrangThaiPhieuMuon.DA_TRA.equals(phieuMuon.getTrangThai())) {
            throw new BadRequestException("Chỉ ghi phạt cho phiếu đang mượn hoặc đã trả sách");
        }

        if (phieuMuon.getDocGiaId() == null) {
            throw new BadRequestException("Phiếu mượn không có thông tin độc giả");
        }

        BigDecimal soTien = request.getSoTien();
        if (soTien == null || soTien.signum() <= 0) {
            throw new BadRequestException("Số tiền phạt phải lớn hơn 0");
        }
        if (soTien.compareTo(SO_TIEN_TOI_DA) > 0) {
            throw new BadRequestException("Số tiền phạt quá lớn");
        }

        String lyDo = request.getLyDo() == null ? "" : request.getLyDo().trim();
        if (lyDo.isEmpty()) {
            throw new BadRequestException("Vui lòng nhập lý do phạt");
        }

        PhieuPhat phieuPhat = new PhieuPhat();
        // doc_gia_id luôn lấy theo phiếu mượn ở server, không tin dữ liệu client gửi lên.
        phieuPhat.setPhieuMuonId(phieuMuon.getId());
        phieuPhat.setDocGiaId(phieuMuon.getDocGiaId());
        phieuPhat.setSoTien(soTien.setScale(2, RoundingMode.HALF_UP));
        phieuPhat.setLyDo(lyDo);
        phieuPhat.setTrangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN);
        phieuPhat.setNgayTao(LocalDateTime.now());

        PhieuPhat saved = phieuPhatRepository.save(phieuPhat);
        return new PhieuPhatResponseDTO(
                phieuPhatRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    public PhieuPhatResponseDTO thanhToan(Integer id) {
        PhieuPhat phieuPhat = phieuPhatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu phạt với ID: " + id));

        if (TrangThaiThanhToan.DA_THANH_TOAN.equals(phieuPhat.getTrangThaiThanhToan())) {
            throw new BadRequestException("Phiếu phạt này đã được thanh toán trước đó");
        }

        phieuPhat.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
        phieuPhat.setNgayThanhToan(LocalDateTime.now());

        PhieuPhat saved = phieuPhatRepository.save(phieuPhat);
        return new PhieuPhatResponseDTO(
                phieuPhatRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    /**
     * Gửi email thông báo phiếu phạt cho độc giả (thủ thư bấm tay).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> guiThongBao(Integer id) {
        PhieuPhat phieuPhat = phieuPhatRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiếu phạt với ID: " + id));

        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("phieuPhatId", id);

        DocGia docGia = phieuPhat.getDocGia();
        NguoiDung nguoiDung = docGia != null ? docGia.getNguoiDung() : null;
        String email = nguoiDung != null ? nguoiDung.getEmail() : null;
        ketQua.put("email", email);

        if (email == null || email.isBlank()) {
            ketQua.put("thanhCong", false);
            ketQua.put("message", "Độc giả chưa có email để gửi thông báo");
            return ketQua;
        }

        try {
            emailService.guiEmail(
                    email,
                    "[Thư viện] Thông báo phiếu phạt #" + phieuPhat.getId(),
                    taoNoiDungThongBaoPhat(phieuPhat, docGia, nguoiDung));
            ketQua.put("thanhCong", true);
            ketQua.put("message", "Đã gửi thông báo tới " + email);
        } catch (Exception e) {
            ketQua.put("thanhCong", false);
            ketQua.put("message", "Gửi email thất bại: " + e.getMessage());
        }
        return ketQua;
    }

    /**
     * Xoá phiếu phạt - chỉ cho phép khi phiếu đã thanh toán.
     */
    @Transactional
    public void xoaPhieuPhat(Integer id) {
        PhieuPhat phieuPhat = phieuPhatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiếu phạt với ID: " + id));

        if (!TrangThaiThanhToan.DA_THANH_TOAN.equals(phieuPhat.getTrangThaiThanhToan())) {
            throw new BadRequestException("Chỉ được xoá phiếu phạt đã thanh toán");
        }

        phieuPhatRepository.delete(phieuPhat);
    }

    private String taoNoiDungThongBaoPhat(PhieuPhat phieuPhat, DocGia docGia, NguoiDung nguoiDung) {
        StringBuilder noiDung = new StringBuilder();
        noiDung.append("Xin chào ")
                .append(nguoiDung != null ? nguoiDung.getHo_ten() : "bạn")
                .append(",\n\n");
        noiDung.append("Thư viện thông báo bạn có phiếu phạt #").append(phieuPhat.getId()).append(".\n\n");
        noiDung.append("- Phiếu mượn: #").append(phieuPhat.getPhieuMuonId()).append("\n");
        noiDung.append("- Mã thẻ độc giả: ")
                .append(docGia != null ? docGia.getMaTheDocGia() : "---").append("\n");
        noiDung.append("- Số tiền: ").append(dinhDangTien(phieuPhat.getSoTien())).append(" đ\n");
        noiDung.append("- Lý do: ").append(phieuPhat.getLyDo()).append("\n");
        noiDung.append("- Ngày tạo: ")
                .append(phieuPhat.getNgayTao() != null
                        ? phieuPhat.getNgayTao().format(DINH_DANG_NGAY_GIO)
                        : "---")
                .append("\n\n");

        if (TrangThaiThanhToan.DA_THANH_TOAN.equals(phieuPhat.getTrangThaiThanhToan())) {
            noiDung.append("Phiếu phạt này đã được thanh toán");
            if (phieuPhat.getNgayThanhToan() != null) {
                noiDung.append(" vào ").append(phieuPhat.getNgayThanhToan().format(DINH_DANG_NGAY_GIO));
            }
            noiDung.append(". Cảm ơn bạn đã hợp tác.\n\n");
        } else {
            noiDung.append("Vui lòng đến quầy thư viện để thanh toán tiền phạt trong thời gian sớm nhất.\n\n");
        }

        noiDung.append("Trân trọng,\nHệ thống Quản lý Thư viện");
        return noiDung.toString();
    }

    private String dinhDangTien(BigDecimal soTien) {
        if (soTien == null) {
            return "0";
        }
        return String.format("%,d", soTien.toBigInteger()).replace(',', '.');
    }

    private boolean khopTuKhoa(PhieuPhat phieuPhat, String keyword) {
        String hoTen = null;
        String email = null;
        String maThe = null;

        DocGia docGia = phieuPhat.getDocGia();
        if (docGia != null) {
            maThe = docGia.getMaTheDocGia();
            NguoiDung nguoiDung = docGia.getNguoiDung();
            if (nguoiDung != null) {
                hoTen = nguoiDung.getHo_ten();
                email = nguoiDung.getEmail();
            }
        }

        return chuaTuKhoa(maThe, keyword)
                || chuaTuKhoa(hoTen, keyword)
                || chuaTuKhoa(email, keyword)
                || chuaTuKhoa(phieuPhat.getLyDo(), keyword)
                || String.valueOf(phieuPhat.getId()).equals(keyword)
                || String.valueOf(phieuPhat.getPhieuMuonId()).equals(keyword);
    }

    private boolean chuaTuKhoa(String giaTri, String keyword) {
        return giaTri != null && giaTri.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
