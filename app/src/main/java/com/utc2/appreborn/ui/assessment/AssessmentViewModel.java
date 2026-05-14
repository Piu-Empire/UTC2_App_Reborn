package com.utc2.appreborn.ui.assessment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.repository.AssessmentRepository;
import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssessmentViewModel extends AndroidViewModel {

    public interface SaveCallback { void onResult(boolean success); }

    // ─── Deps ─────────────────────────────────────────────────────────────────
    private final AssessmentRepository repository;
    private final UserDao              userDao;
    private final AdvisorDao           advisorDao;
    private final ExecutorService      executor = Executors.newSingleThreadExecutor();

    // ─── State ────────────────────────────────────────────────────────────────
    private final MutableLiveData<Boolean>  isStudentTab   = new MutableLiveData<>(true);
    private final MutableLiveData<Float>    totalScore     = new MutableLiveData<>(0f);
    private final MutableLiveData<String>   classification = new MutableLiveData<>("");
    private final MediatorLiveData<List<AssessmentCriteria>> criteria = new MediatorLiveData<>();
    private final LiveData<List<AssessmentPeriod>> periods;

    // Header RLSV
    private final MutableLiveData<String>   studentName  = new MutableLiveData<>("");
    private final MutableLiveData<String>   studentCode  = new MutableLiveData<>("");
    private final MutableLiveData<String>   studentClass = new MutableLiveData<>("");

    // Header CVHT
    private final MutableLiveData<String>   advisorName  = new MutableLiveData<>("");

    private final MutableLiveData<AssessmentPeriod> selectedPeriod = new MutableLiveData<>();
    private LiveData<List<AssessmentCriteria>> currentSource;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repository = AssessmentRepository.getInstance();
        userDao    = AppDatabase.getInstance(application).userDao();
        advisorDao = AppDatabase.getInstance(application).advisorDao();
        periods    = repository.getAssessmentPeriods();

        loadStudentInfo(1L);
        switchTab(true);
    }

    // ─── Public LiveData ──────────────────────────────────────────────────────

    public LiveData<List<AssessmentCriteria>> getCriteria()       { return criteria; }
    public LiveData<List<AssessmentPeriod>>   getPeriods()        { return periods; }
    public LiveData<Float>                    getTotalScore()     { return totalScore; }
    public LiveData<String>                   getClassification() { return classification; }
    public LiveData<Boolean>                  getIsStudentTab()   { return isStudentTab; }

    // Header RLSV
    public LiveData<String> getStudentName()  { return studentName; }
    public LiveData<String> getStudentCode()  { return studentCode; }
    public LiveData<String> getStudentClass() { return studentClass; }

    // Header CVHT
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

    // ─── Save RLSV ───────────────────────────────────────────────────────────

    public void saveAssessment(List<AssessmentCriteria> criteriaList, SaveCallback callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(400); // giả lập DB write

                // TODO: Uncomment khi AssessmentDao sẵn sàng
                // AssessmentDao dao = AppDatabase.getInstance(getApplication()).assessmentDao();
                // dao.insertOrReplace(buildEntity(criteriaList));

                postResult(callback, true);
            } catch (Exception e) {
                postResult(callback, false);
            }
        });
    }

    // ─── Submit CVHT ─────────────────────────────────────────────────────────

    public void submitCvht(List<AssessmentCriteria> criteriaList,
                           String opinion,
                           SaveCallback callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(400); // giả lập DB write

                // TODO: Uncomment khi CvhtAssessmentDao sẵn sàng
                // CvhtAssessmentDao dao = AppDatabase.getInstance(getApplication()).cvhtAssessmentDao();
                // dao.insertOrReplace(buildCvhtEntity(criteriaList, opinion));

                postResult(callback, true);
            } catch (Exception e) {
                postResult(callback, false);
            }
        });
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

    private void loadStudentInfo(long userId) {
        executor.execute(() -> {
            try {
                StudentProfileEntity profile = userDao.getStudentProfileByUserId(userId);
                if (profile != null) {
                    studentName.postValue(profile.fullName != null ? profile.fullName : "Sinh viên");
                    studentCode.postValue(profile.studentCode != null ? profile.studentCode : "—");
                    studentClass.postValue(profile.className != null ? profile.className : "—");

                    if (profile.advisorId != null) {
                        AdvisorEntity advisor = advisorDao.getAdvisorById(profile.advisorId);
                        if (advisor != null) {
                            advisorName.postValue(advisor.fullName);
                            return;
                        }
                    }
                } else {
                    // Mock fallback
                    studentName.postValue("Nguyễn Văn B");
                    studentCode.postValue("2251060xxx");
                    studentClass.postValue("CQ.65.CNTT");
                }
            } catch (Exception e) {
                studentName.postValue("Nguyễn Văn B");
                studentCode.postValue("2251060xxx");
                studentClass.postValue("CQ.65.CNTT");
            }
            advisorName.postValue("ThS. Nguyễn Văn A");
        });
    }

    private void postResult(SaveCallback callback, boolean success) {
        if (callback == null) return;
        new android.os.Handler(android.os.Looper.getMainLooper())
                .post(() -> callback.onResult(success));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}