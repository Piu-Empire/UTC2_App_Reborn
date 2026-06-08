package com.utc2.appreborn.ui.assessment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.repository.AssessmentRepository;
import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;
import com.utc2.appreborn.network.dto.assessment.ExternalAssessmentResponse;
import com.utc2.appreborn.network.dto.assessment.StudentAssessmentResponse;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssessmentViewModel extends AndroidViewModel {

    public interface SaveCallback { void onResult(boolean success); }

    // ─── Deps ─────────────────────────────────────────────────────────────────
    private final AssessmentRepository repository;
    private final ExecutorService      executor = Executors.newSingleThreadExecutor();

    // ─── State ────────────────────────────────────────────────────────────────
    private final MutableLiveData<Boolean>  isStudentTab   = new MutableLiveData<>(true);
    private final MutableLiveData<Float>    totalScore     = new MutableLiveData<>(0f);
    private final MutableLiveData<String>   classification = new MutableLiveData<>("");
    private final MediatorLiveData<List<AssessmentCriteria>> criteria = new MediatorLiveData<>();
    private final LiveData<List<AssessmentPeriod>> periods;

    // Header RLSV
    private final MutableLiveData<String> studentName  = new MutableLiveData<>("");
    private final MutableLiveData<String> studentCode  = new MutableLiveData<>("");
    private final MutableLiveData<String> studentClass = new MutableLiveData<>("");

    // Header CVHT
    private final MutableLiveData<String> advisorName = new MutableLiveData<>("");

    private final MutableLiveData<AssessmentPeriod> selectedPeriod = new MutableLiveData<>();
    private LiveData<List<AssessmentCriteria>> currentSource;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repository = AssessmentRepository.getInstance(application);
        periods    = repository.getAssessmentPeriods(); // gọi API thật

        loadStudentInfo();
        switchTab(true);
    }

    // ─── Public LiveData ──────────────────────────────────────────────────────

    public LiveData<List<AssessmentCriteria>> getCriteria()       { return criteria; }
    public LiveData<List<AssessmentPeriod>>   getPeriods()        { return periods; }
    public LiveData<Float>                    getTotalScore()     { return totalScore; }
    public LiveData<String>                   getClassification() { return classification; }
    public LiveData<Boolean>                  getIsStudentTab()   { return isStudentTab; }

    public LiveData<String> getStudentName()  { return studentName; }
    public LiveData<String> getStudentCode()  { return studentCode; }
    public LiveData<String> getStudentClass() { return studentClass; }
    public LiveData<String> getAdvisorName()  { return advisorName; }

    // ─── Tab switch ───────────────────────────────────────────────────────────

    public void switchTab(boolean toStudentTab) {
        isStudentTab.setValue(toStudentTab);
        if (currentSource != null) criteria.removeSource(currentSource);
        currentSource = repository.getAssessmentCriteria(toStudentTab);
        criteria.addSource(currentSource, list -> {
            criteria.setValue(list);
            recalculate(list);
        });
    }

    // ─── Period ───────────────────────────────────────────────────────────────

    public void setSelectedPeriod(AssessmentPeriod period) {
        selectedPeriod.setValue(period);
    }

    // ─── Score ────────────────────────────────────────────────────────────────

    public void onScoreChanged(List<AssessmentCriteria> list) {
        recalculate(list);
    }

    // ─── Evidence ────────────────────────────────────────────────────────────

    public void addEvidenceUri(int criteriaId, String uri) {
        List<AssessmentCriteria> list = criteria.getValue();
        if (list == null) return;
        for (AssessmentCriteria c : list) {
            if (c.getId() == criteriaId) { c.addEvidenceUri(uri); break; }
        }
        criteria.setValue(list);
    }

    public void removeEvidenceUri(int criteriaId, int fileIndex) {
        List<AssessmentCriteria> list = criteria.getValue();
        if (list == null) return;
        for (AssessmentCriteria c : list) {
            if (c.getId() == criteriaId) { c.removeEvidenceAt(fileIndex); break; }
        }
        criteria.setValue(list);
    }

    // ─── Load dữ liệu khi chọn kỳ — gọi API thật ────────────────────────────

    /**
     * Gọi khi user bấm nút "Chọn" kỳ đánh giá.
     * Luồng: build criteria local → overlay điểm đã lưu (SV tab) → overlay điểm external → post lên UI.
     */
    public void loadForPeriod(AssessmentPeriod period, boolean toStudentTab) {
        selectedPeriod.setValue(period);
        isStudentTab.setValue(toStudentTab);
        if (currentSource != null) { criteria.removeSource(currentSource); currentSource = null; }

        List<AssessmentCriteria> list = repository.getCriteriaList(toStudentTab);

        if (!toStudentTab || period == null) {
            // Tab CVHT hoặc chưa có kỳ: hiển thị criteria local ngay
            criteria.setValue(list);
            recalculate(list);
            return;
        }

        // Tab RLSV: load điểm đã lưu → rồi load điểm external
        String periodId = period.getId();
        repository.getStudentAssessment(periodId,
                /* onLoaded (luôn gọi) */ loadSuccess ->
                        repository.getExternalAssessment(periodId,
                                /* onLoaded (luôn gọi) */ extSuccess -> {
                                    criteria.setValue(list);
                                    recalculate(list);
                                },
                                /* onData */ extData -> applyExternalData(list, extData)
                        ),
                /* onData */ svData -> applyStudentData(list, svData)
        );
    }

    // ─── Save RLSV — gọi API thật ────────────────────────────────────────────

    public void saveAssessment(List<AssessmentCriteria> criteriaList, SaveCallback callback) {
        AssessmentPeriod period = selectedPeriod.getValue();
        String periodId = period != null ? period.getId() : "";
        repository.saveStudentAssessment(criteriaList, periodId,
                success -> callback.onResult(success));
    }

    // ─── Submit CVHT — gọi API thật ──────────────────────────────────────────

    public void submitCvht(List<AssessmentCriteria> criteriaList,
                           String opinion,
                           SaveCallback callback) {
        AssessmentPeriod period = selectedPeriod.getValue();
        String periodId = period != null ? period.getId() : "";
        repository.saveAdvisorAssessment(criteriaList, periodId, opinion,
                success -> callback.onResult(success));
    }

    // ─── Private ─────────────────────────────────────────────────────────────

    private void recalculate(List<AssessmentCriteria> list) {
        if (list == null) return;
        float sum = 0f;
        for (AssessmentCriteria c : list) {
            int t = c.getViewType();
            if (t == AssessmentCriteria.TYPE_CRITERIA || t == AssessmentCriteria.TYPE_DEDUCTION)
                sum += c.getCurrentScore();
        }
        totalScore.setValue(sum);
        classification.setValue(classify(sum, Boolean.TRUE.equals(isStudentTab.getValue())));
    }

    private String classify(float score, boolean isRlsv) {
        if (isRlsv) {
            if (score >= 90) return "Xuất sắc";
            if (score >= 80) return "Tốt";
            if (score >= 65) return "Khá";
            if (score >= 50) return "Trung bình";
            if (score >= 35) return "Yếu";
            return "Kém";
        } else {
            float pct = (score / 60f) * 100f;
            if (pct >= 90) return "Xuất sắc";
            if (pct >= 80) return "Tốt";
            if (pct >= 65) return "Khá";
            if (pct >= 50) return "Trung bình";
            return "Yếu";
        }
    }

    private void applyStudentData(List<AssessmentCriteria> list, StudentAssessmentResponse data) {
        boolean hasData = data != null && data.items != null && !data.items.isEmpty();

        // Reset về max nếu chưa có data (mặc định 100đ), về 0 nếu có data server
        for (AssessmentCriteria c : list) {
            int t = c.getViewType();
            if (t == AssessmentCriteria.TYPE_CRITERIA) {
                c.setCurrentScore(hasData ? 0f : c.getMaxScore());
            } else if (t == AssessmentCriteria.TYPE_DEDUCTION) {
                c.setCurrentScore(0f); // điểm trừ luôn mặc định 0
            }
        }

        if (!hasData) return;

        for (StudentAssessmentResponse.Item item : data.items) {
            for (AssessmentCriteria c : list) {
                if (c.getId() == item.criteriaId) {
                    c.setCurrentScore(item.score);
                    break;
                }
            }
        }
    }

    private void applyExternalData(List<AssessmentCriteria> list, ExternalAssessmentResponse data) {
        if (data == null || data.items == null) return;
        for (ExternalAssessmentResponse.Item item : data.items) {
            for (AssessmentCriteria c : list) {
                if (c.getId() == item.criteriaId) {
                    c.setTapTheScore(item.tapTheScore);
                    c.setBoMonScore(item.boMonScore);
                    c.setKhoaScore(item.khoaScore);
                    c.setTruongScore(item.truongScore);
                    break;
                }
            }
        }
    }

    private void loadStudentInfo() {
        SessionManager session = SessionManager.getInstance(getApplication());

        String cachedName    = session.getFullName();
        String cachedCode    = session.getStudentCode();
        String cachedClass   = session.getClassName();
        String cachedAdvisor = session.getAdvisorName();

        studentName.setValue(!cachedName.isEmpty()    ? cachedName    : "Sinh viên");
        studentCode.setValue(!cachedCode.isEmpty()    ? cachedCode    : "—");
        studentClass.setValue(!cachedClass.isEmpty()  ? cachedClass   : "—");
        advisorName.setValue(!cachedAdvisor.isEmpty() ? cachedAdvisor : "—");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}