package com.utc2.appreborn.ui.profile.TrainingProgram;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.EnrollmentApiService;
import com.utc2.appreborn.network.dto.EnrollmentResponse;
import com.utc2.appreborn.ui.profile.adapter.SubjectAdapter;
import com.utc2.appreborn.ui.profile.model.Subject;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

public class TrainingProgramActivity extends AppCompatActivity {

    private RecyclerView    recyclerView;
    private SubjectAdapter  adapter;
    private List<Subject>   fullList = new ArrayList<>();
    private Chip            chipSem1, chipSem2;
    private ProgressBar     progressBar;
    private NetworkUtils    networkUtils;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        try {
            initViews();
            setupNetworkMonitoring();
            loadDataFromApi();
            setupEvents();
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khởi tạo Activity: " + e.getMessage());
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerSubject);
        chipSem1     = findViewById(R.id.chipSem1);
        chipSem2     = findViewById(R.id.chipSem2);
        progressBar  = findViewById(R.id.progressBar); // thêm ProgressBar vào layout nếu chưa có

        ImageButton            btnBack   = findViewById(R.id.btnBackProfile);
        SearchView             searchView = findViewById(R.id.searchView);
        AutoCompleteTextView   dropYear  = findViewById(R.id.dropYear);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] years = {"Năm 1", "Năm 2", "Năm 3", "Năm 4"};
        dropYear.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, years));

        btnBack.setOnClickListener(v -> finish());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSubject(newText);
                return true;
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Đã có mạng");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(TrainingProgramActivity.this,
                        "Mất kết nối mạng. Bạn đang xem dữ liệu ngoại tuyến.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    // ── API call ──────────────────────────────────────────────

    private void loadDataFromApi() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        String token = SessionManager.getInstance(this).getAuthToken();
        EnrollmentApiService enrollmentApi = ApiClient.getInstance(token)
                .create(EnrollmentApiService.class);

        enrollmentApi.getMyEnrollments()
                .enqueue(new Callback<ApiResponse<List<EnrollmentResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<EnrollmentResponse>>> call,
                                           Response<ApiResponse<List<EnrollmentResponse>>> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            bindEnrollments(response.body().getData());
                        } else {
                            Toast.makeText(TrainingProgramActivity.this,
                                    "Không tải được chương trình đào tạo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<EnrollmentResponse>>> call,
                                          Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(TrainingProgramActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Chuyển list EnrollmentResponse → list Subject (có header theo kỳ),
     * rồi gán vào adapter.
     */
    private void bindEnrollments(List<EnrollmentResponse> data) {
        fullList.clear();

        // Gom môn học theo kỳ (semesterNumber), giữ thứ tự tăng dần
        TreeMap<Integer, List<EnrollmentResponse>> bySem = new TreeMap<>();
        for (EnrollmentResponse e : data) {
            int sem = e.semesterNumber != null ? e.semesterNumber : 0;
            bySem.computeIfAbsent(sem, k -> new ArrayList<>()).add(e);
        }

        for (Map.Entry<Integer, List<EnrollmentResponse>> entry : bySem.entrySet()) {
            int semNum = entry.getKey();
            // Header row
            fullList.add(Subject.headerOf(semNum));
            // Môn học trong kỳ
            for (EnrollmentResponse e : entry.getValue()) {
                fullList.add(new Subject(
                        e.courseCode   != null ? e.courseCode   : "",
                        e.courseName   != null ? e.courseName   : "",
                        e.credits      != null ? e.credits      : 0,
                        e.totalScore   != null ? e.totalScore   : "N/A",
                        e.letterGrade  != null ? e.letterGrade  : "",
                        e.gradePoint   != null ? e.gradePoint   : 0.0,
                        Boolean.TRUE.equals(e.isPassed),
                        semNum,
                        false
                ));
            }
        }

        if (adapter == null) {
            adapter = new SubjectAdapter(fullList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(fullList);
        }
    }

    // ── Events ────────────────────────────────────────────────

    private void setupEvents() {
        chipSem1.setOnClickListener(v -> scrollToSemester(1));
        chipSem2.setOnClickListener(v -> scrollToSemester(2));
    }

    private void scrollToSemester(int sem) {
        try {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                for (int i = 0; i < fullList.size(); i++) {
                    Subject item = fullList.get(i);
                    if (item.isHeader() && item.getSemester() == sem) {
                        llm.scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TrainingProgram", getString(R.string.ScrollingError) + e.getMessage());
        }
    }

    private void searchSubject(String query) {
        List<Subject> filteredList = new ArrayList<>();
        try {
            String input = (query != null) ? query.toLowerCase().trim() : "";
            for (Subject item : fullList) {
                if (item.isHeader() || item.getName().toLowerCase().contains(input)) {
                    filteredList.add(item);
                }
            }
        } catch (Exception e) {
            Log.e("TrainingProgram", getString(R.string.FindingError) + e.getMessage());
        }
        if (adapter != null) adapter.updateList(filteredList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}