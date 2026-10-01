package com.example.backend.service;

import com.example.backend.dto.DangKyRequest;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.VaiTro;
import com.example.backend.repository.NguoiDungRepository;
import org.springframework.stereotype.Service;

@Service
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    public NguoiDungService(NguoiDungRepository nguoiDungRepository){
        this.nguoiDungRepository = nguoiDungRepository;
    }
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
        vaitro.setId(3);
        nguoiDung.setVai_tro_id(vaitro);
        nguoiDung.setTrang_thai(true);

        return nguoiDungRepository.save(nguoiDung);

    }

}
