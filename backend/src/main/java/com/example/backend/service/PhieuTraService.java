package com.example.backend.service;

import com.example.backend.common.TinhTrangSach;
import com.example.backend.common.TrangThaiCuonSach;
import com.example.backend.common.TrangThaiPhieuMuon;
import com.example.backend.dto.PhieuMuonResponseDTO;
import com.example.backend.dto.TraSachRequest;
import com.example.backend.entity.ChiTietPhieuMuon;
import com.example.backend.entity.ChiTietPhieuTra;
import com.example.backend.entity.CuonSach;
import com.example.backend.entity.PhieuMuon;
import com.example.backend.entity.PhieuTra;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietPhieuTraRepository;
import com.example.backend.repository.CuonSachRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.PhieuMuonRepository;
import com.example.backend.repository.PhieuTraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PhieuTraService {

    private final PhieuMuonRepository phieuMuonRepository;
    private final PhieuTraRepository phieuTraRepository;
    private final ChiTietPhieuTraRepository chiTietPhieuTraRepository;
    private final CuonSachRepository cuonSachRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public PhieuTraService(PhieuMuonRepository phieuMuonRepository,
                           PhieuTraRepository phieuTraRepository,
                           ChiTietPhieuTraRepository chiTietPhieuTraRepository,
                           CuonSachRepository cuonSachRepository,
                           NguoiDungRepository nguoiDungRepository) {
        this.phieuMuonRepository = phieuMuonRepository;
        this.phieuTraRepository = phieuTraRepository;
        this.chiTietPhieuTraRepository = chiTietPhieuTraRepository;
        this.cuonSachRepository = cuonSachRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    /**
     * US 3.2.1 - Ghi nhận trả sách (hỗ trợ trả từng cuốn trong phiếu).
     */
    @Transactional
    public PhieuMuonResponseDTO traSach(Integer phieuMuonId, TraSachRequest request) {
        PhieuMuon phieuMuon = phieuMuonRepository.findByIdWithDetails(phieuMuonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + phieuMuonId));

        if (!TrangThaiPhieuMuon.DANG_MUON.equals(phieuMuon.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể trả sách cho phiếu mượn đang ở trạng thái đang mượn");
        }

        if (request.getNguoiLapId() == null || !nguoiDungRepository.existsById(request.getNguoiLapId())) {
            throw new ResourceNotFoundException("Không tìm thấy người lập phiếu trả với ID: " + request.getNguoiLapId());
        }

        List<TraSachRequest.ChiTietTra> dsTra = request.getDanhSachTra();
        if (dsTra == null || dsTra.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một cuốn sách để trả");
        }

        Set<Integer> cuonTrongPhieu = phieuMuon.getChiTietPhieuMuons().stream()
                .map(ChiTietPhieuMuon::getCuonSachId)
                .collect(Collectors.toSet());

        Set<Integer> daTraIds = chiTietPhieuTraRepository.findByPhieuMuonId(phieuMuonId).stream()
                .map(ChiTietPhieuTra::getCuonSachId)
                .collect(Collectors.toSet());

        Set<Integer> daChon = new HashSet<>();
        for (TraSachRequest.ChiTietTra chiTiet : dsTra) {
            if (chiTiet == null || chiTiet.getCuonSachId() == null) {
                throw new BadRequestException("Thông tin cuốn sách trả không hợp lệ");
            }
            if (!daChon.add(chiTiet.getCuonSachId())) {
                throw new BadRequestException("Cuốn sách bị lặp lại trong phiếu trả");
            }
            if (!cuonTrongPhieu.contains(chiTiet.getCuonSachId())) {
                throw new BadRequestException("Cuốn sách ID " + chiTiet.getCuonSachId()
                        + " không thuộc phiếu mượn này");
            }
            if (daTraIds.contains(chiTiet.getCuonSachId())) {
                throw new BadRequestException("Cuốn sách ID " + chiTiet.getCuonSachId()
                        + " đã được trả trước đó");
            }

            String tinhTrang = TinhTrangSach.chuanHoa(chiTiet.getTinhTrangKhiTra());
            if (!TinhTrangSach.hopLe(tinhTrang)) {
                throw new BadRequestException("Tình trạng sách không hợp lệ. Chỉ chấp nhận: TỐT, CŨ, MỚI, HỎNG, MẤT");
            }
            chiTiet.setTinhTrangKhiTra(tinhTrang);
        }

        PhieuTra phieuTra = new PhieuTra();
        phieuTra.setPhieuMuonId(phieuMuonId);
        phieuTra.setNguoiLapId(request.getNguoiLapId());
        phieuTra.setNgayTra(LocalDateTime.now());
        PhieuTra savedPhieuTra = phieuTraRepository.save(phieuTra);

        List<ChiTietPhieuTra> dsChiTiet = new ArrayList<>();
        for (TraSachRequest.ChiTietTra chiTiet : dsTra) {
            CuonSach cuonSach = cuonSachRepository.findById(chiTiet.getCuonSachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuốn sách với ID: " + chiTiet.getCuonSachId()));

            ChiTietPhieuTra chiTietPhieuTra = new ChiTietPhieuTra();
            chiTietPhieuTra.setPhieuTraId(savedPhieuTra.getId());
            chiTietPhieuTra.setPhieuTra(savedPhieuTra);
            chiTietPhieuTra.setCuonSachId(cuonSach.getId());
            chiTietPhieuTra.setTinhTrangKhiTra(chiTiet.getTinhTrangKhiTra());
            dsChiTiet.add(chiTietPhieuTra);

            capNhatCuonSachSauKhiTra(cuonSach, chiTiet.getTinhTrangKhiTra());
        }
        chiTietPhieuTraRepository.saveAll(dsChiTiet);

        int tongSoCuon = phieuMuon.getChiTietPhieuMuons().size();
        if (daTraIds.size() + dsTra.size() >= tongSoCuon) {
            phieuMuon.setTrangThai(TrangThaiPhieuMuon.DA_TRA);
            phieuMuonRepository.save(phieuMuon);
        }

        PhieuMuon phieuMuonSauKhiTra = phieuMuonRepository.findByIdWithDetails(phieuMuonId).orElse(phieuMuon);
        List<ChiTietPhieuTra> tatCaDaTra = chiTietPhieuTraRepository.findByPhieuMuonId(phieuMuonId);
        return new PhieuMuonResponseDTO(phieuMuonSauKhiTra, tatCaDaTra);
    }

    private void capNhatCuonSachSauKhiTra(CuonSach cuonSach, String tinhTrang) {
        cuonSach.setTinhTrang(tinhTrang);
        if (TinhTrangSach.HONG.equals(tinhTrang)) {
            cuonSach.setTrangThai(TrangThaiCuonSach.HONG);
        } else if (TinhTrangSach.MAT.equals(tinhTrang)) {
            cuonSach.setTrangThai(TrangThaiCuonSach.MAT);
        } else {
            cuonSach.setTrangThai(TrangThaiCuonSach.CO_SAN);
        }
        cuonSachRepository.save(cuonSach);
    }
}
