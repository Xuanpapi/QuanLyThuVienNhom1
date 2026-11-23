package com.example.backend.controller;

import com.example.backend.dto.DuyetPhieuMuonRequest;
import com.example.backend.dto.HuyPhieuMuonRequest;
import com.example.backend.dto.LapPhieuMuonRequest;
import com.example.backend.dto.MuonOnlineRequest;
import com.example.backend.dto.PhieuMuonResponseDTO;
import com.example.backend.dto.TraSachRequest;
import com.example.backend.service.PhieuMuonService;
import com.example.backend.service.PhieuTraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phieu-muon")
public class PhieuMuonController {

    private final PhieuMuonService phieuMuonService;
    private final PhieuTraService phieuTraService;

    public PhieuMuonController(PhieuMuonService phieuMuonService, PhieuTraService phieuTraService) {
        this.phieuMuonService = phieuMuonService;
        this.phieuTraService = phieuTraService;
    }

    // API Xem lịch sử mượn sách cá nhân của sinh viên theo ID người dùng
    @GetMapping("/lich-su/{nguoiDungId}")
    public ResponseEntity<?> getLichSuMuon(@PathVariable Integer nguoiDungId) {
        try {
            List<PhieuMuonResponseDTO> lichSu = phieuMuonService.getLichSuMuonByNguoiDungId(nguoiDungId);
            return ResponseEntity.ok(lichSu);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // API Mượn sách online (độc giả tự gửi yêu cầu, chờ thủ thư duyệt)
    @PostMapping("/muon-online")
    public ResponseEntity<?> muonOnline(@Valid @RequestBody MuonOnlineRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuMuonService.muonOnline(request);
            return ResponseEntity.ok(Map.of("message", "Gửi yêu cầu mượn online thành công! Vui lòng chờ thủ thư duyệt.", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // US 3.1.2 - Admin xem toàn bộ phiếu mượn, lọc theo trạng thái / từ khóa
    @GetMapping
    public ResponseEntity<?> getDanhSachPhieuMuon(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String keyword) {
        try {
            return ResponseEntity.ok(phieuMuonService.getDanhSach(trangThai, keyword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // US 3.2.2 - Danh sách phiếu mượn quá hạn
    @GetMapping("/qua-han")
    public ResponseEntity<?> getDanhSachQuaHan(@RequestParam(required = false) String keyword) {
        try {
            return ResponseEntity.ok(phieuMuonService.getDanhSachQuaHan(keyword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Gửi email cảnh báo quá hạn cho một phiếu mượn
    @PostMapping("/{id}/gui-canh-bao")
    public ResponseEntity<?> guiCanhBao(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(phieuMuonService.guiCanhBaoMotPhieu(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Gửi email cảnh báo hàng loạt; body rỗng = gửi toàn bộ phiếu đang quá hạn
    @PostMapping("/gui-canh-bao")
    public ResponseEntity<?> guiCanhBaoHangLoat(@RequestBody(required = false) List<Integer> danhSachId) {
        try {
            return ResponseEntity.ok(phieuMuonService.guiCanhBaoHangLoat(danhSachId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChiTietPhieuMuon(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(phieuMuonService.getChiTiet(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // US 3.1.1 - Thủ thư lập phiếu mượn cho độc giả
    @PostMapping
    public ResponseEntity<?> lapPhieuMuon(@Valid @RequestBody LapPhieuMuonRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuMuonService.lapPhieuMuon(request);
            return ResponseEntity.ok(Map.of("message", "Lập phiếu mượn sách thành công!", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Duyệt phiếu mượn online
    @PostMapping("/{id}/duyet")
    public ResponseEntity<?> duyetPhieuMuon(
            @PathVariable Integer id,
            @Valid @RequestBody DuyetPhieuMuonRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuMuonService.duyetPhieuMuon(id, request);
            return ResponseEntity.ok(Map.of("message", "Duyệt phiếu mượn thành công!", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Từ chối phiếu mượn online
    @PostMapping("/{id}/tu-choi")
    public ResponseEntity<?> tuChoiPhieuMuon(
            @PathVariable Integer id,
            @Valid @RequestBody DuyetPhieuMuonRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuMuonService.tuChoiPhieuMuon(id, request);
            return ResponseEntity.ok(Map.of("message", "Đã từ chối phiếu mượn.", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Độc giả tự huỷ yêu cầu mượn online khi chưa được duyệt
    @PostMapping("/{id}/huy")
    public ResponseEntity<?> huyPhieuMuon(
            @PathVariable Integer id,
            @Valid @RequestBody HuyPhieuMuonRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuMuonService.huyPhieuMuon(id, request);
            return ResponseEntity.ok(Map.of("message", "Đã huỷ yêu cầu mượn sách.", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // US 3.2.1 - Ghi nhận trả sách (trả từng cuốn)
    @PostMapping("/{id}/tra")
    public ResponseEntity<?> traSach(
            @PathVariable Integer id,
            @Valid @RequestBody TraSachRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            PhieuMuonResponseDTO response = phieuTraService.traSach(id, request);
            return ResponseEntity.ok(Map.of("message", "Ghi nhận trả sách thành công!", "data", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> loiValidation(BindingResult bindingResult) {
        String message = bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "Dữ liệu không hợp lệ";
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
