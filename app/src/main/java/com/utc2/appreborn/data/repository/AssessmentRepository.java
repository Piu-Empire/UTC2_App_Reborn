package com.utc2.appreborn.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssessmentRepository {

    private static volatile AssessmentRepository instance;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private AssessmentRepository() {}

    public static AssessmentRepository getInstance() {
        if (instance == null) {
            synchronized (AssessmentRepository.class) {
                if (instance == null) instance = new AssessmentRepository();
            }
        }
        return instance;
    }

    public LiveData<List<AssessmentCriteria>> getAssessmentCriteria(boolean isStudentTab) {
        MutableLiveData<List<AssessmentCriteria>> liveData = new MutableLiveData<>();
        mainHandler.postDelayed(() -> liveData.setValue(
                isStudentTab ? buildRlsvCriteria() : buildCvhtCriteria()), 50);
        return liveData;
    }

    public LiveData<List<AssessmentPeriod>> getAssessmentPeriods() {
        MutableLiveData<List<AssessmentPeriod>> liveData = new MutableLiveData<>();
        mainHandler.postDelayed(() -> liveData.setValue(buildMockPeriods()), 50);
        return liveData;
    }

    // ─── RLSV ────────────────────────────────────────────────────────────────

    private List<AssessmentCriteria> buildRlsvCriteria() {
        List<AssessmentCriteria> list = new ArrayList<>();
        int id = 0;

        // 1. Ý thức học tập (20đ)
        list.add(header(id++, "1. Đánh giá về ý thức tham gia học tập (Tối đa: 20 điểm)"));
        list.add(criteria(id++,
                "a. Có ý thức học tập tốt, có tinh thần vượt khó vươn lên",
                "Tất cả học phần điểm D trở lên; mỗi điểm F hoặc F+ trừ 1 điểm",
                5f, false, opts(0f, 1f, 2f, 3f, 4f, 5f)));
        list.add(criteria(id++,
                "b. Đạt được 1 trong các điều kiện:\n" +
                        "- Tham gia NCKH, hoàn thành nghiên cứu đúng tiến độ\n" +
                        "- Thành viên tích cực các câu lạc bộ học thuật\n" +
                        "- Thành viên đội tuyển tham dự cuộc thi học thuật\n" +
                        "- Có chứng chỉ nâng cao trình độ kiến thức\n" +
                        "(Cần nộp minh chứng – xác nhận CVHT & TT lớp)",
                "Có MC: 5đ | Không có MC: 0đ",
                5f, true, opts(0f, 5f)));
        list.add(criteria(id++, "c. Thực hiện quy chế thi và kiểm tra",
                "Chấp hành tốt: 5đ | Bị kỷ luật thi: 0đ",
                5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "d. Cộng điểm theo TBCHT",
                "1→<1.5: 1đ | 1.5→<2.0: 2đ | 2.0→<2.5: 3đ | 2.5→<3.2: 4đ | ≥3.2: 5đ",
                5f, false, opts(0f, 1f, 2f, 3f, 4f, 5f)));

        // 2. Nội quy quy chế (25đ)
        list.add(header(id++, "2. Đánh giá ý thức chấp hành nội quy, quy chế (Tối đa: 25 điểm)"));
        list.add(criteria(id++, "a. Tham gia bảo hiểm y tế", "Tham gia: 5đ | Không: 0đ",
                5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "b. Đóng học phí đúng quy định", "Đúng hạn: 5đ | Trễ hạn: 0đ",
                5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "c. Đánh giá Giảng viên", "Có: 2.5đ | Không: 0đ",
                2.5f, false, opts(0f, 2.5f)));
        list.add(criteria(id++, "d. Đánh giá Cố vấn học tập", "Có: 2.5đ | Không: 0đ",
                2.5f, false, opts(0f, 2.5f)));
        list.add(criteria(id++, "e. Tham gia tuần sinh hoạt công dân",
                "Tham gia: 2.5đ | Không: 0đ", 2.5f, false, opts(0f, 2.5f)));
        list.add(criteria(id++, "f. Bài thu hoạch tuần sinh hoạt công dân",
                "Thực hiện tốt: 2.5đ | Không: 0đ", 2.5f, false, opts(0f, 2.5f)));
        list.add(criteria(id++, "g. Tham gia họp lớp, chi đoàn đầy đủ",
                "Đầy đủ: 5đ | Vắng 1 buổi: 2đ | Vắng ≥2 buổi: 0đ",
                5f, false, opts(0f, 2f, 5f)));

        // 3. Hoạt động (20đ)
        list.add(header(id++, "3. Hoạt động chính trị - xã hội, văn hóa, thể thao (Tối đa: 20 điểm)"));
        list.add(criteria(id++, "a. Tham gia hội thảo, tọa đàm, cuộc thi",
                "Có: 6đ | Không: 0đ", 6f, false, opts(0f, 6f)));
        list.add(criteria(id++, "b. Hoạt động tình nguyện, công ích",
                "Có: 6đ | Không: 0đ", 6f, false, opts(0f, 6f)));
        list.add(criteria(id++, "c. Văn hóa, văn nghệ, thể thao",
                "Có: 6đ | Không: 0đ", 6f, false, opts(0f, 6f)));
        list.add(criteria(id++, "d. Tuyên truyền phòng chống tệ nạn",
                "Có: 2đ | Không: 0đ", 2f, false, opts(0f, 2f)));

        // 4. Ý thức công dân (25đ)
        list.add(header(id++, "4. Ý thức công dân và quan hệ cộng đồng (Tối đa: 25 điểm)"));
        list.add(criteria(id++, "a. Chấp hành pháp luật và quy định",
                "Tốt: 6đ | Không tốt: 0đ", 6f, false, opts(0f, 6f)));
        list.add(criteria(id++, "b. Có thành tích xã hội (Cần minh chứng)",
                "Có MC: 4đ | Không: 0đ", 4f, true, opts(0f, 4f)));
        list.add(criteria(id++, "c. Giúp đỡ người khác, bạn bè",
                "Có: 5đ | Không: 0đ", 5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "d. Cập nhật thông tin cá nhân đầy đủ",
                "Có: 5đ | Không: 0đ", 5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "e. Giữ gìn trật tự, môi trường, hình ảnh trường",
                "Có: 5đ | Không: 0đ", 5f, false, opts(0f, 5f)));

        // 5. Cán bộ lớp (10đ)
        list.add(header(id++, "5. Công tác cán bộ lớp, đoàn thể (Tối đa: 10 điểm)"));
        list.add(criteria(id++,
                "a. Tham gia tích cực Đoàn/Hội/CLB, đạt giải học thuật, được khen thưởng",
                "Có MC: 5đ | Không: 0đ", 5f, true, opts(0f, 5f)));
        list.add(criteria(id++, "b. Tham gia tổ chức hoạt động",
                "Có: 5đ | Không: 0đ", 5f, false, opts(0f, 5f)));

        // 6. Trừ điểm
        list.add(header(id++, "6. SINH VIÊN BỊ TRỪ ĐIỂM (Cấp lớp, Khoa/BM trừ điểm)"));
        list.add(deduction(id++, "Không tự đánh giá rèn luyện trên hệ thống",
                -10f, opts(0f, -10f)));
        list.add(deduction(id++, "Có thông báo vi phạm pháp luật",
                -10f, opts(0f, -10f)));

        // ── Footer RLSV: KQRL + ghi chú + quy định phân loại ───────────────
        list.add(new AssessmentCriteria(AssessmentCriteria.TYPE_FOOTER_RLSV));

        return list;
    }

    // ─── CVHT ────────────────────────────────────────────────────────────────

    private List<AssessmentCriteria> buildCvhtCriteria() {
        List<AssessmentCriteria> list = new ArrayList<>();
        int id = 100;

        list.add(header(id++,
                "1. Phần đánh giá theo tiêu chí\n" +
                        "(Rất tốt: 5đ | Tốt: 4đ | Khá: 3đ | Trung bình: 2đ | Yếu: 1đ)"));

        String[] titles = {
                "1. CVHT đã chuẩn bị tốt nội dung và chủ trì các buổi họp lớp theo kế hoạch của Khoa và Nhà trường.",
                "2. CVHT đã công khai về thời gian biểu, cách thức trao đổi, liên lạc với sinh viên.",
                "3. CVHT đã tạo điều kiện thuận lợi để sinh viên được tư vấn, trao đổi bằng các hình thức: gặp trực tiếp hoặc qua internet, điện thoại, email, fanpage…(ít nhất 2 tuần 1 lần).",
                "4. CVHT nắm chắc quy chế học tập và rèn luyện để tư vấn cho sinh viên.",
                "5. CVHT đã quan tâm theo dõi kết quả học tập để tư vấn cho sinh viên về đăng ký học, về xây dựng kế hoạch học tập và phương pháp học tập hiệu quả phù hợp với năng lực sinh viên.",
                "6. CVHT đã tư vấn cho sinh viên tham gia các hoạt động hỗ trợ học tập như: NCKH; tiếp cận doanh nghiệp; hoạt động thực tế…",
                "7. CVHT đã khuyến khích, động viên sinh viên tham gia các hoạt động xã hội, các hoạt động văn, thể, mỹ lành mạnh bổ ích.",
                "8. CVHT đã kịp thời thông báo, đôn đốc, nhắc nhở sinh viên tổ chức, thực hiện các kế hoạch của Khoa và Nhà trường.",
                "9. CVHT phổ biến, hướng dẫn và chủ trì họp đánh giá RLSV, đảm bảo đúng quy trình và tiến độ của Nhà trường.",
                "10. CVHT đã thường xuyên cập nhật thông tin sinh viên (địa chỉ, điện thoại, email).",
                "11. CVHT đã kịp thời giải quyết các vấn đề phát sinh theo yêu cầu của sinh viên hoặc lớp sinh viên.",
                "12. CVHT có thái độ ứng xử thân thiện, đúng mực với sinh viên.",
        };

        for (String title : titles) {
            list.add(new AssessmentCriteria(AssessmentCriteria.TYPE_CRITERIA, id++,
                    title, null, 5f, false, opts(1f, 2f, 3f, 4f, 5f)));
        }

        // ── Footer CVHT: Ý kiến sinh viên ────────────────────────────────────
        list.add(new AssessmentCriteria(AssessmentCriteria.TYPE_FOOTER_CVHT));

        return list;
    }

    // ─── Mock periods ─────────────────────────────────────────────────────────

    private List<AssessmentPeriod> buildMockPeriods() {
        return Arrays.asList(
                new AssessmentPeriod("HK1_2024_2025", "Học kỳ 1 – 2024-2025"),
                new AssessmentPeriod("HK2_2024_2025", "Học kỳ 2 – 2024-2025"),
                new AssessmentPeriod("HK1_2025_2026", "Học kỳ 1 – 2025-2026"),
                new AssessmentPeriod("HK2_2025_2026", "Học kỳ 2 – 2025-2026")
        );
    }

    // ─── Factory helpers ──────────────────────────────────────────────────────

    private AssessmentCriteria header(int id, String title) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_SECTION_HEADER, id, title, null, 0f, false, null);
    }

    private AssessmentCriteria criteria(int id, String title, String description,
                                        float max, boolean needsEvidence, List<Float> opts) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_CRITERIA, id, title, description, max, needsEvidence, opts);
    }

    private AssessmentCriteria deduction(int id, String title, float penalty, List<Float> opts) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_DEDUCTION, id, title, null, penalty, false, opts);
    }

    private List<Float> opts(Float... values) { return Arrays.asList(values); }
}