package com.example.backend.common;

/**
 * Trạng thái của từng cuốn sách vật lý (cột "trang_thai" bảng CuonSach).
 */
public final class TrangThaiCuonSach {
    public static final String CO_SAN = "CÓ_SẴN";
    public static final String DANG_MUON = "ĐANG_MƯỢN";
    public static final String HONG = "HỎNG";
    public static final String MAT = "MẤT";

    private TrangThaiCuonSach() {
    }

    public static boolean laCoSan(String trangThai) {
        return trangThai == null || trangThai.isBlank() || CO_SAN.equalsIgnoreCase(trangThai.trim());
    }
}
