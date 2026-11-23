package com.example.backend.common;

import java.util.Locale;
import java.util.Set;

/**
 * Tình trạng sách khi trả (cột "tinh_trang_khi_tra" bảng ChiTietPhieuTra).
 */
public final class TinhTrangSach {
    public static final String TOT = "TỐT";
    public static final String CU = "CŨ";
    public static final String MOI = "MỚI";
    public static final String HONG = "HỎNG";
    public static final String MAT = "MẤT";

    private static final Set<String> HOP_LE = Set.of(TOT, CU, MOI, HONG, MAT);

    private TinhTrangSach() {
    }

    public static String chuanHoa(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean hopLe(String value) {
        return value != null && HOP_LE.contains(value);
    }

    /**
     * Tình trạng sau khi trả có còn cho độc giả mượn tiếp hay không.
     */
    public static boolean conChoMuon(String value) {
        return TOT.equals(value) || CU.equals(value) || MOI.equals(value);
    }
}
