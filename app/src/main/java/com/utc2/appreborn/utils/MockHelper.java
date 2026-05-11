package com.utc2.appreborn.utils;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.notification.NotificationItem;

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
    //  Student / User  — mapping TABLE USER_PROFILE + STUDENT_PROFILE
    // ═══════════════════════════════════════════════════════

    /** USER_PROFILE.full_name */
    public static String getMockFullName() {
        return "Nguyễn Minh Phúc";
    }

    /** STUDENT_PROFILE.student_code (MSSV) */
    public static String getMockStudentCode() {
        return "2251050001";
    }

    /** USER_PROFILE.phone_number */
    public static String getMockPhone() {
        return "0123 456 789";
    }

    /** USER_PROFILE.date_of_birth — "dd/MM/yyyy" */
    public static String getMockDateOfBirth() {
        return "01/01/2004";
    }

    /** USER_PROFILE.gender */
    public static String getMockGender() {
        return "Nam";
    }

    /** STUDENT_PROFILE.faculty */
    public static String getMockFaculty() {
        return "Công nghệ Thông tin";
    }

    /** STUDENT_PROFILE.major */
    public static String getMockMajor() {
        return "Công nghệ Thông tin";
    }

    /** STUDENT_PROFILE.academic_year */
    public static String getMockAcademicYear() {
        return "K65 (2024–2029)";
    }

    /** STUDENT_PROFILE.class_name */
    public static String getMockClassName() {
        return "CQ.65.CNTT";
    }

    /** STUDENT_PROFILE.status */
    public static String getMockStatus() {
        return "Đang học";
    }

    /** ADVISOR.full_name — lấy qua STUDENT_PROFILE.advisor_id */
    public static String getMockAdvisorName() {
        return "KS. Trần Quốc Khánh";
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
        // Tham số thứ 3 giờ là R.string.xxx thay vì "Chuỗi văn bản"
        list.add(new FeatureItem("hoc_phi",       R.drawable.ic_hoc_phi,       R.string.feature_hoc_phi));
        list.add(new FeatureItem("dich_vu_cong",  R.drawable.ic_dich_vu_cong,  R.string.feature_dich_vu_cong));
        list.add(new FeatureItem("danh_gia",      R.drawable.ic_danh_gia,      R.string.feature_danh_gia));
        list.add(new FeatureItem("ki_tuc_xa",     R.drawable.ic_ki_tuc_xa,     R.string.feature_ki_tuc_xa));
        list.add(new FeatureItem("ho_tro",        R.drawable.ic_ho_tro,        R.string.feature_ho_tro));
        list.add(new FeatureItem("danh_muc_khac", R.drawable.ic_danh_muc_khac, R.string.feature_danh_muc_khac));
        return list;
    }

    // ═══════════════════════════════════════════════════════
    //  Notification — mapping TABLE NOTIFICATION
    // ═══════════════════════════════════════════════════════

    /**
     * Mock data cho danh sách thông báo — mapping TABLE NOTIFICATION:
     *   senderName ↔ (nguồn gửi — thể hiện qua NOTIFICATION.type)
     *   subject    ↔ NOTIFICATION.title
     *   preview    ↔ NOTIFICATION.body  (rút gọn)
     *   timeLabel  ↔ NOTIFICATION.sent_at  (định dạng hiển thị)
     *   isRead     ↔ NOTIFICATION.is_read
     *
     * Dùng khi chưa đăng nhập Google hoặc chưa có API thật.
     */
    public static List<NotificationItem> getMockNotificationList() {
        List<NotificationItem> list = new ArrayList<>();

        list.add(new NotificationItem(
                "Phòng Đào Tạo",
                "Thông báo lịch thi học kỳ 2 năm học 2024–2025",
                "Sinh viên xem lịch thi chi tiết tại cổng thông tin UTC2. Phòng thi sẽ được công bố trước 3 ngày.",
                "10:30",
                false
        ));
        list.add(new NotificationItem(
                "Phòng Công tác SV",
                "Xét học bổng khuyến khích học tập HK2",
                "Sinh viên có điểm TB tích lũy ≥ 3.2 nộp hồ sơ trước ngày 15/5/2025.",
                "Hôm qua",
                true
        ));
        list.add(new NotificationItem(
                "Ban Giám hiệu",
                "Thông báo nghỉ lễ 30/4 và 1/5",
                "Nhà trường thông báo lịch nghỉ lễ theo quy định của Nhà nước.",
                "28/04",
                true
        ));
        list.add(new NotificationItem(
                "Phòng QLSV",
                "Nộp bản sao học bạ THPT cho sinh viên năm nhất",
                "Các bạn sinh viên K2024 chưa nộp học bạ THPT vui lòng đến Phòng QLSV trước 20/5.",
                "25/04",
                true
        ));
        list.add(new NotificationItem(
                "Thư viện UTC2",
                "Gia hạn thẻ thư viện học kỳ 2",
                "Thẻ thư viện học kỳ 1 sẽ hết hạn vào cuối tháng 4. Gia hạn tại quầy thư viện hoặc online.",
                "20/04",
                true
        ));

        return list;
    }

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