package com.example.backend.service;

import com.example.backend.common.TrangThaiPhieuMuon;
import com.example.backend.common.TrangThaiThanhToan;
import com.example.backend.dto.PhieuMuonResponseDTO;
import com.example.backend.dto.ThongBaoResponseDTO;
import com.example.backend.entity.ChiTietPhieuTra;
import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.PhieuMuon;
import com.example.backend.entity.PhieuPhat;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietPhieuTraRepository;
import com.example.backend.repository.DocGiaRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.PhieuMuonRepository;
import com.example.backend.repository.PhieuPhatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tổng hợp cảnh báo cho người dùng: sách quá hạn, sắp đến hạn, phiếu phạt chưa thanh toán.
 */
@Service
public class ThongBaoService {

    /** Số ngày trước hạn để bắt đầu nhắc. */
    private static final int SO_NGAY_NHAC_TRUOC = 3;

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NguoiDungRepository nguoiDungRepository;
    private final DocGiaRepository docGiaRepository;
    private final PhieuMuonRepository phieuMuonRepository;
    private final PhieuPhatRepository phieuPhatRepository;
    private final ChiTietPhieuTraRepository chiTietPhieuTraRepository;

    public ThongBaoService(NguoiDungRepository nguoiDungRepository,
                           DocGiaRepository docGiaRepository,
                           PhieuMuonRepository phieuMuonRepository,
                           PhieuPhatRepository phieuPhatRepository,
                           ChiTietPhieuTraRepository chiTietPhieuTraRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.docGiaRepository = docGiaRepository;
        this.phieuMuonRepository = phieuMuonRepository;
        this.phieuPhatRepository = phieuPhatRepository;
        this.chiTietPhieuTraRepository = chiTietPhieuTraRepository;
    }

    @Transactional(readOnly = true)
    public List<ThongBaoResponseDTO> getThongBao(Integer nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với ID: " + nguoiDungId));

        if (laQuanTri(nguoiDung)) {
            return thongBaoChoThuThu();
        }
        return thongBaoChoDocGia(nguoiDung);
    }

    private List<ThongBaoResponseDTO> thongBaoChoThuThu() {
        List<ThongBaoResponseDTO> ds = new ArrayList<>();
        int soPhieuQuaHan = phieuMuonRepository
                .findQuaHan(TrangThaiPhieuMuon.DANG_MUON, LocalDate.now())
                .size();

        if (soPhieuQuaHan > 0) {
            ds.add(new ThongBaoResponseDTO(
                    "admin-qua-han",
                    "QUA_HAN_ADMIN",
                    "Có " + soPhieuQuaHan + " phiếu mượn quá hạn",
                    "Danh sách phiếu quá hạn đang chờ xử lý. Bấm Xem để mở tab Sách Quá Hạn.",
                    "CAO",
                    LocalDate.now(),
                    "QUA_HAN"));
        }
        return ds;
    }

    private List<ThongBaoResponseDTO> thongBaoChoDocGia(NguoiDung nguoiDung) {
        Optional<DocGia> docGiaOpt = docGiaRepository.findByNguoiDungId(nguoiDung.getId());
        if (docGiaOpt.isEmpty()) {
            return List.of();
        }

        DocGia docGia = docGiaOpt.get();
        List<ThongBaoResponseDTO> ds = new ArrayList<>();
        LocalDate homNay = LocalDate.now();
        LocalDate nguongSapHan = homNay.plusDays(SO_NGAY_NHAC_TRUOC);

        List<PhieuMuonResponseDTO> dsPhieu = toPhieuMuonDTOs(
                phieuMuonRepository.findByDocGiaIdWithDetails(docGia.getId()));

        for (PhieuMuonResponseDTO phieu : dsPhieu) {
            if (!TrangThaiPhieuMuon.DANG_MUON.equals(phieu.getTrangThai()) || phieu.getHanTra() == null) {
                continue;
            }

            List<String> tenSach = phieu.getDanhSachSach().stream()
                    .filter(s -> !s.isDaTra() && s.getTenSach() != null && !s.getTenSach().isBlank())
                    .map(PhieuMuonResponseDTO.ChiTietSachDTO::getTenSach)
                    .collect(Collectors.toList());
            String sachStr = tenSach.isEmpty() ? "Không có sách chưa trả" : String.join(", ", tenSach);

            if (phieu.getHanTra().isBefore(homNay)) {
                int soNgayQuaHan = (int) ChronoUnit.DAYS.between(phieu.getHanTra(), homNay);
                ds.add(new ThongBaoResponseDTO(
                        "qua-han-" + phieu.getPhieuMuonId(),
                        "QUA_HAN",
                        "Sách quá hạn " + soNgayQuaHan + " ngày",
                        "Phiếu mượn #" + phieu.getPhieuMuonId()
                                + " • Hạn trả " + phieu.getHanTra().format(DINH_DANG_NGAY)
                                + "\nSách: " + sachStr,
                        "CAO",
                        phieu.getHanTra(),
                        "LICH_SU"));
            } else if (!phieu.getHanTra().isAfter(nguongSapHan)) {
                long soNgayConLai = ChronoUnit.DAYS.between(homNay, phieu.getHanTra());
                ds.add(new ThongBaoResponseDTO(
                        "sap-han-" + phieu.getPhieuMuonId(),
                        "SAP_HAN",
                        soNgayConLai == 0
                                ? "Sách đến hạn hôm nay"
                                : "Sách sắp đến hạn (" + soNgayConLai + " ngày)",
                        "Phiếu mượn #" + phieu.getPhieuMuonId()
                                + " • Hạn trả " + phieu.getHanTra().format(DINH_DANG_NGAY)
                                + "\nSách: " + sachStr,
                        "TRUNG_BINH",
                        phieu.getHanTra(),
                        "LICH_SU"));
            }
        }

        for (PhieuPhat phieuPhat : phieuPhatRepository.findByNguoiDungIdWithDetails(nguoiDung.getId())) {
            if (!TrangThaiThanhToan.CHUA_THANH_TOAN.equals(phieuPhat.getTrangThaiThanhToan())) {
                continue;
            }
            ds.add(new ThongBaoResponseDTO(
                    "phieu-phat-" + phieuPhat.getId(),
                    "PHIEU_PHAT",
                    "Phiếu phạt chưa thanh toán",
                    "Phiếu phạt #" + phieuPhat.getId()
                            + " • " + dinhDangTien(phieuPhat.getSoTien()) + " đ"
                            + "\nLý do: " + phieuPhat.getLyDo(),
                    "CAO",
                    phieuPhat.getNgayTao() != null ? phieuPhat.getNgayTao().toLocalDate() : null,
                    null));
        }

        ds.sort(Comparator
                .comparingInt((ThongBaoResponseDTO tb) -> "CAO".equals(tb.getMucDo()) ? 0 : 1)
                .thenComparing(ThongBaoResponseDTO::getNgay,
                        Comparator.nullsLast(Comparator.reverseOrder())));

        return ds;
    }

    private List<PhieuMuonResponseDTO> toPhieuMuonDTOs(List<PhieuMuon> dsPhieu) {
        if (dsPhieu.isEmpty()) {
            return List.of();
        }

        List<Integer> dsId = dsPhieu.stream().map(PhieuMuon::getId).collect(Collectors.toList());
        Map<Integer, List<ChiTietPhieuTra>> daTraTheoPhieu = chiTietPhieuTraRepository.findByPhieuMuonIds(dsId).stream()
                .filter(ct -> ct.getPhieuTra() != null)
                .collect(Collectors.groupingBy(ct -> ct.getPhieuTra().getPhieuMuonId()));

        return dsPhieu.stream()
                .map(pm -> new PhieuMuonResponseDTO(pm, daTraTheoPhieu.getOrDefault(pm.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private boolean laQuanTri(NguoiDung nguoiDung) {
        return nguoiDung.getVai_tro_id() != null
                && "QUAN_TRI".equalsIgnoreCase(nguoiDung.getVai_tro_id().getTenVaiTro());
    }

    private String dinhDangTien(BigDecimal soTien) {
        if (soTien == null) {
            return "0";
        }
        return String.format("%,d", soTien.toBigInteger()).replace(',', '.');
    }
}
