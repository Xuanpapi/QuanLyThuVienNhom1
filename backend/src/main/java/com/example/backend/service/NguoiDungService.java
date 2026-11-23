package com.example.backend.service;

import com.example.backend.dto.DangKyRequest;
import com.example.backend.dto.DangNhapRequest;
import com.example.backend.dto.NguoiDungAdminResponseDTO;
import com.example.backend.dto.NguoiDungProfileDTO;
import com.example.backend.dto.PageResponseDTO;
import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.VaiTro;
import com.example.backend.repository.DocGiaRepository;
import com.example.backend.repository.NguoiDungRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final DocGiaRepository docGiaRepository;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository, DocGiaRepository docGiaRepository){
        this.nguoiDungRepository = nguoiDungRepository;
        this.docGiaRepository = docGiaRepository;
    }

    @Transactional
    public NguoiDung dangky(DangKyRequest request){
        if(nguoiDungRepository.existsByMasv(request.getMa_sv())){
            throw new RuntimeException("Mã sinh viên đã tồn tại");
        }
        if(nguoiDungRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email đã tồn tại");
        }

        NguoiDung nguoiDung = new NguoiDung(
                request.getMa_sv(),
                request.getEmail(),
                request.getMat_khau(),
                request.getHo_ten(),
                request.getLop(),
                request.getSdt(),
                request.getKhoa()
        );
        VaiTro vaitro = new VaiTro();
        vaitro.setId(2);
        nguoiDung.setVai_tro_id(vaitro);
        nguoiDung.setTrang_thai(true);

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);

        // Tự động cấp thẻ độc giả với mã duy nhất (DG + mã sinh viên)
        DocGia docGia = new DocGia();
        docGia.setNguoiDungId(savedUser.getId());
        docGia.setMaTheDocGia("DG" + request.getMa_sv());
        docGia.setNgayCap(LocalDate.now());
        docGia.setNgayHetHan(LocalDate.now().plusYears(4));

        docGiaRepository.save(docGia);

        return savedUser;
    }

    public NguoiDung dangNhap(DangNhapRequest request) {
        if (request.getTai_khoan() == null || request.getTai_khoan().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mã sinh viên hoặc email");
        }
        if (request.getMat_khau() == null || request.getMat_khau().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mật khẩu");
        }

        // Tìm kiếm theo Mã sinh viên hoặc Email
        NguoiDung nguoiDung = nguoiDungRepository.findByMaSvOrEmail(request.getTai_khoan().trim())
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không chính xác"));

        // Kiểm tra mật khẩu
        if (!nguoiDung.getMat_khau().equals(request.getMat_khau())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        }

        // Kiểm tra trạng thái hoạt động của tài khoản
        if (Boolean.FALSE.equals(nguoiDung.getTrang_thai())) {
            throw new RuntimeException("Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên");
        }

        // Ẩn mật khẩu trước khi trả về để đảm bảo an toàn
        nguoiDung.setMat_khau(null);
        return nguoiDung;
    }

    public NguoiDungProfileDTO getProfile(Integer nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        DocGia docGia = docGiaRepository.findByNguoiDungId(nguoiDungId).orElse(null);
        return new NguoiDungProfileDTO(nguoiDung, docGia);
    }

    // ─────────────────────────────────────────────────────────────
    // QUẢN LÝ NGƯỜI DÙNG (US 1.2.1 & 1.2.2)
    // ─────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách người dùng có phân trang, tìm kiếm và lọc theo vai trò / trạng thái.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<NguoiDungAdminResponseDTO> getDanhSachNguoiDung(
            String keyword, String vaiTro, Boolean trangThai, int page, int size) {

        int trangHienTai = Math.max(page, 0);
        int soDong = Math.min(Math.max(size, 1), 100);

        String tuKhoa = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim().toLowerCase();
        String vaiTroLoc = (vaiTro == null || vaiTro.trim().isEmpty()) ? null : vaiTro.trim().toUpperCase();

        Specification<NguoiDung> spec = (root, query, cb) -> {
            List<Predicate> dieuKien = new ArrayList<>();

            if (tuKhoa != null) {
                String pattern = "%" + tuKhoa + "%";
                dieuKien.add(cb.or(
                        cb.like(cb.lower(root.get("ho_ten")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("ma_sv")), pattern),
                        cb.like(cb.lower(root.get("so_dien_thoai")), pattern)
                ));
            }
            if (vaiTroLoc != null) {
                dieuKien.add(cb.equal(cb.upper(root.get("vai_tro_id").get("tenVaiTro")), vaiTroLoc));
            }
            if (trangThai != null) {
                dieuKien.add(cb.equal(root.get("trang_thai"), trangThai));
            }

            return cb.and(dieuKien.toArray(new Predicate[0]));
        };

        Page<NguoiDung> ketQua = nguoiDungRepository.findAll(
                spec,
                PageRequest.of(trangHienTai, soDong, Sort.by(Sort.Direction.ASC, "id"))
        );

        List<NguoiDung> danhSach = ketQua.getContent();
        List<Integer> ids = danhSach.stream().map(NguoiDung::getId).toList();

        Map<Integer, DocGia> theDocGiaTheoNguoiDung = ids.isEmpty()
                ? Map.of()
                : docGiaRepository.findAllByNguoiDungIdIn(ids).stream()
                        .collect(Collectors.toMap(DocGia::getNguoiDungId, Function.identity(), (a, b) -> a));

        List<NguoiDungAdminResponseDTO> noiDung = danhSach.stream()
                .map(nd -> new NguoiDungAdminResponseDTO(nd, theDocGiaTheoNguoiDung.get(nd.getId())))
                .toList();

        return new PageResponseDTO<>(
                noiDung,
                ketQua.getNumber(),
                ketQua.getSize(),
                ketQua.getTotalElements(),
                ketQua.getTotalPages(),
                ketQua.isLast()
        );
    }

    /**
     * Khóa / mở khóa tài khoản. Không cho thủ thư tự khóa chính mình.
     */
    @Transactional
    public NguoiDungAdminResponseDTO capNhatTrangThai(
            Integer nguoiDungId, Boolean trangThaiMoi, Integer nguoiThucHienId) {

        if (trangThaiMoi == null) {
            throw new RuntimeException("Trạng thái không được để trống");
        }

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (Boolean.FALSE.equals(trangThaiMoi)
                && nguoiThucHienId != null
                && nguoiDungId.equals(nguoiThucHienId)) {
            throw new RuntimeException("Không thể khóa tài khoản của chính mình");
        }

        nguoiDung.setTrang_thai(trangThaiMoi);
        NguoiDung daLuu = nguoiDungRepository.save(nguoiDung);

        DocGia docGia = docGiaRepository.findByNguoiDungId(daLuu.getId()).orElse(null);
        return new NguoiDungAdminResponseDTO(daLuu, docGia);
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI MẬT KHẨU
    // ─────────────────────────────────────────────────────────────

    /**
     * Đổi mật khẩu: yêu cầu nhập đúng mật khẩu cũ, mật khẩu mới và xác nhận khớp nhau.
     */
    @Transactional
    public void doiMatKhau(Integer nguoiDungId, String matKhauCu, String matKhauMoi, String xacNhanMatKhauMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!nguoiDung.getMat_khau().equals(matKhauCu)) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        if (matKhauMoi.equals(matKhauCu)) {
            throw new RuntimeException("Mật khẩu mới phải khác mật khẩu cũ");
        }

        nguoiDung.setMat_khau(matKhauMoi);
        nguoiDungRepository.save(nguoiDung);
    }

    // ─────────────────────────────────────────────────────────────
    // QUÊN MẬT KHẨU (đặt lại mật khẩu qua OTP email)
    // ─────────────────────────────────────────────────────────────

    /**
     * Tìm người dùng theo email, ném lỗi thân thiện nếu không tồn tại.
     */
    public NguoiDung getNguoiDungByEmail(String email) {
        String emailChuan = email == null ? "" : email.trim();
        if (emailChuan.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập email");
        }
        return nguoiDungRepository.findByEmail(emailChuan)
                .orElseThrow(() -> new RuntimeException("Email chưa được đăng ký trong hệ thống"));
    }

    /**
     * Đặt lại mật khẩu sau khi OTP đã được xác nhận ở controller.
     * Không yêu cầu mật khẩu cũ (dùng cho luồng quên mật khẩu).
     */
    @Transactional
    public void datLaiMatKhau(String email, String matKhauMoi) {
        NguoiDung nguoiDung = getNguoiDungByEmail(email);
        nguoiDung.setMat_khau(matKhauMoi);
        nguoiDungRepository.save(nguoiDung);
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI EMAIL (2 bước: gửi OTP → xác nhận OTP + email mới)
    // ─────────────────────────────────────────────────────────────

    /**
     * Bước 1: Lấy email hiện tại để OtpService gửi OTP đến.
     */
    public String getEmailHienTai(Integer nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return nguoiDung.getEmail();
    }

    /**
     * Kiểm tra email mới trước khi xác thực OTP (tránh làm mất OTP khi email không hợp lệ).
     */
    public void kiemTraEmailMoi(Integer nguoiDungId, String emailMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String moi = emailMoi == null ? "" : emailMoi.trim();
        if (nguoiDung.getEmail().equalsIgnoreCase(moi)) {
            throw new RuntimeException("Email mới phải khác email hiện tại");
        }
        if (nguoiDungRepository.existsByEmail(moi)) {
            throw new RuntimeException("Email này đã được sử dụng bởi tài khoản khác");
        }
    }

    /**
     * Bước 2: Sau khi OTP đã được xác nhận ở controller, cập nhật email mới.
     */
    @Transactional
    public void capNhatEmail(Integer nguoiDungId, String emailMoi) {
        kiemTraEmailMoi(nguoiDungId, emailMoi);

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        nguoiDung.setEmail(emailMoi.trim());
        nguoiDungRepository.save(nguoiDung);
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI SỐ ĐIỆN THOẠI (2 bước: gửi OTP → xác nhận OTP + SĐT mới)
    // ─────────────────────────────────────────────────────────────

    /**
     * Kiểm tra số điện thoại mới trước khi xác thực OTP.
     */
    public void kiemTraSdtMoi(Integer nguoiDungId, String sdtMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (sdtMoi != null && sdtMoi.equals(nguoiDung.getSo_dien_thoai())) {
            throw new RuntimeException("Số điện thoại mới phải khác số điện thoại hiện tại");
        }
    }

    /**
     * Bước 2: Sau khi OTP đã được xác nhận ở controller, cập nhật số điện thoại mới.
     */
    @Transactional
    public void capNhatSdt(Integer nguoiDungId, String sdtMoi) {
        kiemTraSdtMoi(nguoiDungId, sdtMoi);

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        nguoiDung.setSo_dien_thoai(sdtMoi);
        nguoiDungRepository.save(nguoiDung);
    }
}
