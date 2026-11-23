package com.example.backend.service;

import com.example.backend.dto.DocGiaOptionDTO;
import com.example.backend.repository.DocGiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocGiaService {

    private final DocGiaRepository docGiaRepository;

    public DocGiaService(DocGiaRepository docGiaRepository) {
        this.docGiaRepository = docGiaRepository;
    }

    @Transactional(readOnly = true)
    public List<DocGiaOptionDTO> getAllOptions() {
        return docGiaRepository.findAllWithNguoiDung().stream()
                .map(DocGiaOptionDTO::new)
                .collect(Collectors.toList());
    }
}
