package com.example.backend.service;

import com.example.backend.common.TrangThaiCuonSach;
import com.example.backend.common.TrangThaiPhieuMuon;
import com.example.backend.dto.DuyetPhieuMuonRequest;
import com.example.backend.dto.HuyPhieuMuonRequest;
import com.example.backend.dto.LapPhieuMuonRequest;
import com.example.backend.dto.MuonOnlineRequest;
import com.example.backend.dto.PhieuMuonResponseDTO;
import com.example.backend.entity.ChiTietPhieuMuon;
import com.example.backend.entity.ChiTietPhieuTra;
import com.example.backend.entity.CuonSach;
import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.PhieuMuon;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietPhieuTraRepository;
import com.example.backend.repository.CuonSachRepository;
import com.example.backend.repository.DocGiaRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.PhieuMuonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PhieuMuonService {

    /** Số ngày mượn mặc định của một phiếu. */
    public static final int SO_NGAY_MUON_MAC_DINH = 14;

    /** Số ngày mượn tối đa khi độc giả tự chọn hạn trả online. */
    public static final int SO_NGAY_MUON_TOI_DA = 30;

    /** Định dạng ngày dùng trong nội dung email. */
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Mức phạt tham khảo mỗi ngày quá hạn (chỉ dùng để gợi ý trong email). */
    private static final long PHAT_MOI_NGAY = 5_000L;

    private final PhieuMuonRepository phieuMuonRepository;
    private final DocGiaRepository docGiaRepository;
    private final CuonSachRepository cuonSachRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ChiTietPhieuTraRepository chiTietPhieuTraRepository;
    private final EmailService emailService;

    public PhieuMuonService(PhieuMuonRepository phieuMuonRepository,
                            DocGiaRepository docGiaRepository,
                            CuonSachRepository cuonSachRepository,
                            NguoiDungRepository nguoiDungRepository,
                            ChiTietPhieuTraRepository chiTietPhieuTraRepository,
                            EmailService emailService) {
        this.phieuMuonRepository = phieuMuonRepository;
        this.docGiaRepository = docGiaRepository;
        this.cuonSachRepository = cuonSachRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.chiTietPhieuTraRepository = chiTietPhieuTraRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<PhieuMuonResponseDTO> getLichSuMuonByNguoiDungId(Integer nguoiDungId) {
        DocGia docGia = docGiaRepository.findByNguoiDungId(nguoiDungId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin độc giả cho người dùng ID: " + nguoiDungId));

        return toDTOs(phieuMuonRepository.findByDocGiaIdWithDetails(docGia.getId()));
    }

    @Transactional
    public PhieuMuonResponseDTO muonOnline(MuonOnlineRequest request) {
        DocGia docGia = docGiaRepository.findByNguoiDungId(request.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin độc giả cho người dùng ID: " + request.getNguoiDungId()));

        List<CuonSach> dsCuonSach = cuonSachRepository.findAvailableBySachId(request.getSachId());
        if (dsCuonSach.isEmpty()) {
            throw new BadRequestException("Đầu sách này hiện đã hết bản có sẵn để mượn online.");
        }
        CuonSach cuonSach = dsCuonSach.get(0);

        LocalDate hanTra = request.getHanTra() != null
                ? request.getHanTra()
                : LocalDate.now().plusDays(SO_NGAY_MUON_MAC_DINH);
        kiemTraHanTraOnline(hanTra);

        PhieuMuon phieuMuon = new PhieuMuon();
        phieuMuon.setDocGiaId(docGia.getId());
        phieuMuon.setDocGia(docGia);
        phieuMuon.setNguoiLapId(request.getNguoiDungId());
        phieuMuon.setNgayMuon(LocalDateTime.now());
        phieuMuon.setHanTra(hanTra);
        phieuMuon.setSoLanGiaHan(0);
        phieuMuon.setTrangThai(TrangThaiPhieuMuon.CHO_DUYET);

        // Phải lưu phiếu trước để có ID, sau đó mới gán phieu_muon_id cho chi tiết.
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);

        ChiTietPhieuMuon chiTiet = new ChiTietPhieuMuon();
        chiTiet.setPhieuMuonId(saved.getId());
        chiTiet.setPhieuMuon(saved);
        chiTiet.setCuonSachId(cuonSach.getId());
        chiTiet.setCuonSach(cuonSach);
        saved.getChiTietPhieuMuons().add(chiTiet);

        saved = phieuMuonRepository.save(saved);
        return new PhieuMuonResponseDTO(saved, List.of());
    }

    @Transactional(readOnly = true)
    public List<PhieuMuonResponseDTO> getDanhSach(String trangThai, String keyword) {        List<PhieuMuon> dsPhieu;
        if (trangThai == null || trangThai.isBlank()) {
            dsPhieu = phieuMuonRepository.findAllWithDetails();
        } else {
            dsPhieu = phieuMuonRepository.findByTrangThaiWithDetails(trangThai.trim());
        }

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase(Locale.ROOT);
            dsPhieu = dsPhieu.stream()
                    .filter(pm -> khopTuKhoa(pm, kw))
                    .collect(Collectors.toList());
        }

        return toDTOs(dsPhieu);
    }

    /**
     * US 3.2.2 - Danh sách phiếu mượn quá hạn: đang mượn và hạn trả < hôm nay.
     */
    @Transactional(readOnly = true)
    public List<PhieuMuonResponseDTO> getDanhSachQuaHan(String keyword) {
        List<PhieuMuon> dsPhieu = phieuMuonRepository.findQuaHan(
                TrangThaiPhieuMuon.DANG_MUON, LocalDate.now());

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase(Locale.ROOT);
            dsPhieu = dsPhieu.stream()
                    .filter(pm -> khopTuKhoa(pm, kw))
                    .collect(Collectors.toList());
        }

        return toDTOs(dsPhieu);
    }

    @Transactional(readOnly = true)
    public PhieuMuonResponseDTO getChiTiet(Integer id) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));
        return new PhieuMuonResponseDTO(phieuMuon, chiTietPhieuTraRepository.findByPhieuMuonId(id));
    }

    /**
     * US 3.1.1 - Thủ thư lập phiếu mượn cho độc giả.
     */
    @Transactional
    public PhieuMuonResponseDTO lapPhieuMuon(LapPhieuMuonRequest request) {
        DocGia docGia = docGiaRepository.findById(request.getDocGiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy độc giả với ID: " + request.getDocGiaId()));

        if (request.getHanTra() == null || request.getHanTra().isBefore(LocalDate.now())) {
            throw new BadRequestException("Hạn trả không được nhỏ hơn ngày hôm nay");
        }

        if (request.getNguoiLapId() == null || !nguoiDungRepository.existsById(request.getNguoiLapId())) {
            throw new ResourceNotFoundException("Không tìm thấy người lập phiếu với ID: " + request.getNguoiLapId());
        }

        List<Integer> dsId = request.getDanhSachCuonSachId();
        if (dsId == null || dsId.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một cuốn sách để mượn");
        }

        Set<Integer> dsIdKhongTrung = new LinkedHashSet<>(dsId);
        if (dsIdKhongTrung.size() != dsId.size()) {
            throw new BadRequestException("Danh sách cuốn sách bị trùng, vui lòng kiểm tra lại");
        }

        List<CuonSach> dsCuonSach = cuonSachRepository.findAllById(dsIdKhongTrung);
        if (dsCuonSach.size() != dsIdKhongTrung.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều cuốn sách không tồn tại trong hệ thống");
        }

        for (CuonSach cuonSach : dsCuonSach) {
            if (!TrangThaiCuonSach.laCoSan(cuonSach.getTrangThai())) {
                throw new BadRequestException("Cuốn sách " + cuonSach.getMaCuonSach()
                        + " hiện không sẵn sàng để mượn (trạng thái: " + cuonSach.getTrangThai() + ")");
            }
        }

        PhieuMuon phieuMuon = new PhieuMuon();
        phieuMuon.setDocGiaId(docGia.getId());
        phieuMuon.setDocGia(docGia);
        phieuMuon.setNguoiLapId(request.getNguoiLapId());
        phieuMuon.setNgayMuon(LocalDateTime.now());
        phieuMuon.setHanTra(request.getHanTra());
        phieuMuon.setSoLanGiaHan(0);
        phieuMuon.setTrangThai(TrangThaiPhieuMuon.DANG_MUON);

        // Lưu phiếu trước để có ID, tránh lỗi phieu_muon_id NULL ở bảng chi tiết.
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);

        for (CuonSach cuonSach : dsCuonSach) {
            ChiTietPhieuMuon chiTiet = new ChiTietPhieuMuon();
            chiTiet.setPhieuMuonId(saved.getId());
            chiTiet.setPhieuMuon(saved);
            chiTiet.setCuonSachId(cuonSach.getId());
            chiTiet.setCuonSach(cuonSach);
            saved.getChiTietPhieuMuons().add(chiTiet);
        }

        saved = phieuMuonRepository.save(saved);

        for (CuonSach cuonSach : dsCuonSach) {
            cuonSach.setTrangThai(TrangThaiCuonSach.DANG_MUON);
        }
        cuonSachRepository.saveAll(dsCuonSach);

        return new PhieuMuonResponseDTO(saved, List.of());
    }

    /**
     * Duyệt phiếu mượn online đang ở trạng thái CHO_DUYET.
     */
    @Transactional
    public PhieuMuonResponseDTO duyetPhieuMuon(Integer id, DuyetPhieuMuonRequest request) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));

        if (!TrangThaiPhieuMuon.CHO_DUYET.equals(phieuMuon.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể duyệt phiếu mượn đang ở trạng thái chờ duyệt");
        }

        if (request.getNguoiDuyetId() == null || !nguoiDungRepository.existsById(request.getNguoiDuyetId())) {
            throw new ResourceNotFoundException("Không tìm thấy người duyệt với ID: " + request.getNguoiDuyetId());
        }

        List<CuonSach> dsCapNhat = new ArrayList<>();
        for (ChiTietPhieuMuon chiTiet : phieuMuon.getChiTietPhieuMuons()) {
            CuonSach cuonSach = cuonSachRepository.findById(chiTiet.getCuonSachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuốn sách với ID: " + chiTiet.getCuonSachId()));

            if (!TrangThaiCuonSach.laCoSan(cuonSach.getTrangThai())) {
                Integer sachId = cuonSach.getSachId();
                List<CuonSach> dsCuonThayThe = cuonSachRepository.findAvailableBySachId(sachId);
                if (dsCuonThayThe.isEmpty()) {
                    throw new BadRequestException("Đầu sách " + sachId
                            + " đã hết bản sẵn sàng, hãy từ chối phiếu mượn này.");
                }
                CuonSach cuonThayThe = dsCuonThayThe.get(0);
                chiTiet.setCuonSachId(cuonThayThe.getId());
                chiTiet.setCuonSach(cuonThayThe);
                cuonSach = cuonThayThe;
            }

            cuonSach.setTrangThai(TrangThaiCuonSach.DANG_MUON);
            if (!dsCapNhat.contains(cuonSach)) {
                dsCapNhat.add(cuonSach);
            }
        }

        cuonSachRepository.saveAll(dsCapNhat);

        LocalDate homNay = LocalDate.now();
        phieuMuon.setNgayMuon(LocalDateTime.now());
        // Giữ hạn trả độc giả đã chọn nếu vẫn còn hợp lệ; nếu đã quá ngày thì đẩy về mặc định.
        if (phieuMuon.getHanTra() == null || phieuMuon.getHanTra().isBefore(homNay)) {
            phieuMuon.setHanTra(homNay.plusDays(SO_NGAY_MUON_MAC_DINH));
        }
        phieuMuon.setNguoiLapId(request.getNguoiDuyetId());
        phieuMuon.setTrangThai(TrangThaiPhieuMuon.DANG_MUON);

        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);
        return new PhieuMuonResponseDTO(saved, List.of());
    }

    /**
     * Từ chối phiếu mượn online. Cuốn sách không bị thay đổi trạng thái.
     */
    @Transactional
    public PhieuMuonResponseDTO tuChoiPhieuMuon(Integer id, DuyetPhieuMuonRequest request) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));

        if (!TrangThaiPhieuMuon.CHO_DUYET.equals(phieuMuon.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể từ chối phiếu mượn đang ở trạng thái chờ duyệt");
        }

        if (request.getNguoiDuyetId() == null || !nguoiDungRepository.existsById(request.getNguoiDuyetId())) {
            throw new ResourceNotFoundException("Không tìm thấy người duyệt với ID: " + request.getNguoiDuyetId());
        }

        phieuMuon.setNguoiLapId(request.getNguoiDuyetId());
        phieuMuon.setTrangThai(TrangThaiPhieuMuon.TU_CHOI);

        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);
        return new PhieuMuonResponseDTO(saved, List.of());
    }

    /**
     * Độc giả tự huỷ yêu cầu mượn online khi phiếu còn ở trạng thái CHO_DUYET.
     */
    @Transactional
    public PhieuMuonResponseDTO huyPhieuMuon(Integer id, HuyPhieuMuonRequest request) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));

        if (!TrangThaiPhieuMuon.CHO_DUYET.equals(phieuMuon.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể huỷ yêu cầu mượn đang ở trạng thái chờ duyệt");
        }

        if (request.getNguoiDungId() == null
                || phieuMuon.getDocGia() == null
                || !Objects.equals(phieuMuon.getDocGia().getNguoiDungId(), request.getNguoiDungId())) {
            throw new BadRequestException("Bạn chỉ có thể huỷ yêu cầu mượn của chính mình");
        }

        // Phiếu chờ duyệt chưa giữ cuốn sách nên không cần cập nhật trạng thái cuốn.
        phieuMuon.setTrangThai(TrangThaiPhieuMuon.DA_HUY);
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);
        return new PhieuMuonResponseDTO(saved, chiTietPhieuTraRepository.findByPhieuMuonId(id));
    }

    // ─────────────────────────────────────────────────────────────
    // Gửi email cảnh báo sách quá hạn (bổ trợ cho US 3.2.2 / 3.2.3)
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> guiCanhBaoMotPhieu(Integer phieuMuonId) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(phieuMuonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiếu mượn với ID: " + phieuMuonId));
        return guiCanhBao(phieuMuon);
    }

    /**
     * Gửi cảnh báo cho danh sách phiếu được chọn.
     * Nếu danh sách rỗng/null thì gửi cho toàn bộ phiếu đang quá hạn.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> guiCanhBaoHangLoat(List<Integer> dsId) {
        List<PhieuMuon> dsPhieu;
        if (dsId == null || dsId.isEmpty()) {
            dsPhieu = phieuMuonRepository.findQuaHan(TrangThaiPhieuMuon.DANG_MUON, LocalDate.now());
        } else {
            dsPhieu = new ArrayList<>();
            for (Integer id : new LinkedHashSet<>(dsId)) {
                phieuMuonRepository.findByIdWithDetails(id).ifPresent(dsPhieu::add);
            }
        }

        List<Map<String, Object>> chiTiet = new ArrayList<>();
        int thanhCong = 0;
        for (PhieuMuon phieuMuon : dsPhieu) {
            Map<String, Object> ketQua = guiCanhBao(phieuMuon);
            if (Boolean.TRUE.equals(ketQua.get("thanhCong"))) {
                thanhCong++;
            }
            chiTiet.add(ketQua);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tongSo", dsPhieu.size());
        response.put("thanhCong", thanhCong);
        response.put("thatBai", dsPhieu.size() - thanhCong);
        response.put("chiTiet", chiTiet);
        return response;
    }

    private Map<String, Object> guiCanhBao(PhieuMuon phieuMuon) {
        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("phieuMuonId", phieuMuon.getId());

        DocGia docGia = phieuMuon.getDocGia();
        NguoiDung nguoiDung = docGia != null ? docGia.getNguoiDung() : null;
        String email = nguoiDung != null ? nguoiDung.getEmail() : null;
        ketQua.put("email", email);

        if (email == null || email.isBlank()) {
            ketQua.put("thanhCong", false);
            ketQua.put("message", "Độc giả chưa có email");
            return ketQua;
        }

        if (!TrangThaiPhieuMuon.DANG_MUON.equals(phieuMuon.getTrangThai())
                || phieuMuon.getHanTra() == null
                || !phieuMuon.getHanTra().isBefore(LocalDate.now())) {
            ketQua.put("thanhCong", false);
            ketQua.put("message", "Phiếu này không ở trạng thái quá hạn");
            return ketQua;
        }

        int soNgayQuaHan = (int) ChronoUnit.DAYS.between(phieuMuon.getHanTra(), LocalDate.now());

        try {
            emailService.guiEmail(
                    email,
                    "[Thư viện] Cảnh báo sách quá hạn - Phiếu #" + phieuMuon.getId(),
                    taoNoiDungCanhBao(phieuMuon, soNgayQuaHan));
            ketQua.put("thanhCong", true);
            ketQua.put("message", "Đã gửi cảnh báo tới " + email);
        } catch (Exception e) {
            ketQua.put("thanhCong", false);
            ketQua.put("message", "Gửi email thất bại: " + e.getMessage());
        }
        return ketQua;
    }

    private String taoNoiDungCanhBao(PhieuMuon phieuMuon, int soNgayQuaHan) {
        DocGia docGia = phieuMuon.getDocGia();
        NguoiDung nguoiDung = docGia != null ? docGia.getNguoiDung() : null;

        StringBuilder noiDung = new StringBuilder();
        noiDung.append("Xin chào ")
                .append(nguoiDung != null ? nguoiDung.getHo_ten() : "bạn")
                .append(",\n\n");
        noiDung.append("Thư viện xin thông báo phiếu mượn #").append(phieuMuon.getId())
                .append(" của bạn đã QUÁ HẠN ").append(soNgayQuaHan).append(" ngày.\n\n");
        noiDung.append("- Mã thẻ độc giả: ")
                .append(docGia != null ? docGia.getMaTheDocGia() : "---").append("\n");
        noiDung.append("- Ngày mượn: ")
                .append(phieuMuon.getNgayMuon() != null
                        ? phieuMuon.getNgayMuon().toLocalDate().format(DINH_DANG_NGAY)
                        : "---")
                .append("\n");
        noiDung.append("- Hạn trả: ")
                .append(phieuMuon.getHanTra() != null
                        ? phieuMuon.getHanTra().format(DINH_DANG_NGAY)
                        : "---")
                .append("\n");

        noiDung.append("- Sách trong phiếu mượn:\n");
        if (phieuMuon.getChiTietPhieuMuons() != null && !phieuMuon.getChiTietPhieuMuons().isEmpty()) {
            for (ChiTietPhieuMuon chiTiet : phieuMuon.getChiTietPhieuMuons()) {
                String tenSach = chiTiet.getCuonSach() != null && chiTiet.getCuonSach().getSach() != null
                        ? chiTiet.getCuonSach().getSach().getTenSach()
                        : "---";
                String maCuon = chiTiet.getCuonSach() != null
                        ? chiTiet.getCuonSach().getMaCuonSach()
                        : "---";
                noiDung.append("   + ").append(tenSach)
                        .append(" (mã cuốn: ").append(maCuon).append(")\n");
            }
        } else {
            noiDung.append("   (không có dữ liệu)\n");
        }

        long tienPhatThamKhao = (long) soNgayQuaHan * PHAT_MOI_NGAY;
        noiDung.append("\n- Mức phạt tham khảo: ")
                .append(String.format("%,d", tienPhatThamKhao).replace(',', '.'))
                .append(" đ (").append(String.format("%,d", PHAT_MOI_NGAY).replace(',', '.'))
                .append(" đ/ngày quá hạn).\n\n");
        noiDung.append("Vui lòng mang sách đến thư viện để trả và thanh toán tiền phạt trong thời gian sớm nhất.\n\n");
        noiDung.append("Trân trọng,\nHệ thống Quản lý Thư viện");
        return noiDung.toString();
    }

    private void kiemTraHanTraOnline(LocalDate hanTra) {
        LocalDate homNay = LocalDate.now();
        if (hanTra.isBefore(homNay)) {
            throw new BadRequestException("Ngày trả không được nhỏ hơn ngày hôm nay");
        }
        if (hanTra.isAfter(homNay.plusDays(SO_NGAY_MUON_TOI_DA))) {
            throw new BadRequestException("Ngày trả không được vượt quá " + SO_NGAY_MUON_TOI_DA + " ngày kể từ hôm nay");
        }
    }

    private boolean khopTuKhoa(PhieuMuon phieuMuon, String keyword) {
        String maThe = phieuMuon.getDocGia() != null ? phieuMuon.getDocGia().getMaTheDocGia() : null;
        String hoTen = null;
        String email = null;
        if (phieuMuon.getDocGia() != null && phieuMuon.getDocGia().getNguoiDung() != null) {
            hoTen = phieuMuon.getDocGia().getNguoiDung().getHo_ten();
            email = phieuMuon.getDocGia().getNguoiDung().getEmail();
        }
        return chuaTuKhoa(maThe, keyword) || chuaTuKhoa(hoTen, keyword) || chuaTuKhoa(email, keyword)
                || String.valueOf(phieuMuon.getId()).equals(keyword);
    }

    private boolean chuaTuKhoa(String giaTri, String keyword) {
        return giaTri != null && giaTri.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private List<PhieuMuonResponseDTO> toDTOs(List<PhieuMuon> dsPhieu) {
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
}
