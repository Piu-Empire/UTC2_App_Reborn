package com.utc2.appreborn.utils;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * MockHelper
 * ──────────────────────────────────────────────────────────────
 * Centralized source of static mock / placeholder data.
 *
 * Rules:
 *  • Every method is static — no instantiation needed.
 *  • Used as an immediate skeleton while real data loads,
 *    AND as a fallback when the network / database fails.
 *  • Once real data sources are stable, swap call-sites
 *    one by one and delete the corresponding mock method.
 *
 * Package: com.utc2.appreborn.utils
 */
public final class MockHelper {

    private MockHelper() { /* utility class — no instances */ }

    // ═══════════════════════════════════════════════════════
    //  Student / User
    // ═══════════════════════════════════════════════════════

    /** Full name shown in the Home header and on the QR card. */
    public static String getMockFullName() {
        return "Nguyễn Văn An";
    }

    /**
     * Student code (MSSV) encoded in the QR bitmap.
     * Replace with real Firestore / SQLite read once DB is wired.
     */
    public static String getMockStudentCode() {
        return "SV2024789456";
    }

    // ═══════════════════════════════════════════════════════
    //  Feature Grid  (3 × 2)
    // ═══════════════════════════════════════════════════════

    /**
     * Returns the 6 {@link FeatureItem}s for the Home grid.
     *
     * Icon drawable names must exist under res/drawable/:
     *   ic_hoc_phi, ic_dich_vu_cong, ic_danh_gia,
     *   ic_ki_tuc_xa, ic_ho_tro, ic_danh_muc_khac
     */
    public static List<FeatureItem> getFeatureList() {
        List<FeatureItem> list = new ArrayList<>(6);
        list.add(new FeatureItem("hoc_phi",       R.drawable.ic_hoc_phi,       "Học phí"));
        list.add(new FeatureItem("dich_vu_cong",  R.drawable.ic_dich_vu_cong,  "Dịch vụ công"));
        list.add(new FeatureItem("danh_gia",      R.drawable.ic_danh_gia,      "Đánh giá"));
        list.add(new FeatureItem("ki_tuc_xa",     R.drawable.ic_ki_tuc_xa,     "Kí túc xá"));
        list.add(new FeatureItem("ho_tro",        R.drawable.ic_ho_tro,        "Hỗ trợ 24/7"));
        list.add(new FeatureItem("danh_muc_khac", R.drawable.ic_danh_muc_khac, "Danh mục khác"));
        return list;
    }

    // ═══════════════════════════════════════════════════════
    //  News Feed  (5 placeholder items)
    // ═══════════════════════════════════════════════════════

    /**
     * Returns 5 mock {@link NewsItem}s.
     *
     * Shown immediately in the RecyclerView before the Retrofit
     * response arrives, and kept if the API call fails.
     */
    public static List<NewsItem> getMockNewsList() {
        List<NewsItem> list = new ArrayList<>(5);

        list.add(new NewsItem(
                "mock_1",
                "Thông báo lịch nghỉ lễ 30/4 và 1/5 năm 2025",
                "20/04/2025",
                "Nhà trường thông báo lịch nghỉ lễ Giải phóng miền Nam và Quốc tế Lao động."
        ));
        list.add(new NewsItem(
                "mock_2",
                "Kết quả xét học bổng học kỳ 2 năm học 2024–2025",
                "18/04/2025",
                "Phòng Công tác Sinh viên thông báo danh sách sinh viên được xét học bổng."
        ));
        list.add(new NewsItem(
                "mock_3",
                "Thông báo đăng ký học phần học kỳ 3 năm 2025",
                "15/04/2025",
                "Sinh viên đăng ký học phần từ ngày 01/05 đến 10/05/2025."
        ));
        list.add(new NewsItem(
                "mock_4",
                "Hướng dẫn làm thẻ sinh viên kỳ mới",
                "10/04/2025",
                "Sinh viên năm nhất cần nộp ảnh 3×4 tại Phòng Đào tạo trước 30/04."
        ));
        list.add(new NewsItem(
                "mock_5",
                "Lịch thi kết thúc học phần học kỳ 2 năm học 2024–2025",
                "05/04/2025",
                "Phòng Đào tạo công bố lịch thi chính thức cho tất cả các học phần."
        ));

        return list;
    }
}