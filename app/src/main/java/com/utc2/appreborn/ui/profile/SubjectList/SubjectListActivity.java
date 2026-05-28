package com.utc2.appreborn.ui.profile.SubjectList;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SubjectListActivity extends AppCompatActivity {

    private RecyclerView         recyclerView;
    private SubjectAdapter       adapter;
    private AutoCompleteTextView dropYear;
    private List<EnrollmentResponse> rawData = new ArrayList<>();

    /**
     * fullList = data đang hiển thị (sau khi lọc năm + search).
     * Đây là list Subject gồm header + môn, được truyền vào adapter.
     */
    private List<Subject> fullList = new ArrayList<>();

    private ProgressBar progressBar;
    private NetworkUtils networkUtils;

    /** Năm học đang được chọn, null = hiển thị tất cả */
    private String selectedYear = null;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        try {
            initViews();
            setupNetworkMonitoring();
            loadDataFromApi();
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khởi tạo Activity: " + e.getMessage());
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerSubject);
        progressBar  = findViewById(R.id.progressBar);
        dropYear     = findViewById(R.id.dropYear);

        ImageButton btnBack    = findViewById(R.id.btnBackProfile);
        SearchView  searchView = findViewById(R.id.searchView);
        com.google.android.material.chip.ChipGroup chipGroup =
                findViewById(R.id.chipGroupSemester);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        // Dùng ChipGroup listener thay vì setOnClickListener từng chip
        // vì singleSelection=true, click chip đang checked sẽ không fire onClick
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipSem1)) scrollToSemester(1);
            else if (checkedIds.contains(R.id.chipSem2)) scrollToSemester(2);
        });

        // Search — áp lên fullList hiện tại
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
            @Override public void onNetworkAvailable() {
                Log.d("Network", "Đã có mạng");
            }
            @Override public void onNetworkLost() {
                Toast.makeText(SubjectListActivity.this,
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
                            rawData = response.body().getData();
                            setupYearDropdown(rawData);
                            applyFilters(null); // hiện tất cả ban đầu
                        } else {
                            Toast.makeText(SubjectListActivity.this,
                                    "Không tải được chương trình đào tạo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<EnrollmentResponse>>> call,
                                          Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(SubjectListActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Dropdown năm học (dynamic) ────────────────────────────

    /**
     * Lấy danh sách academicYear duy nhất từ data (đã sort tăng dần),
     * build dropdown với option "Tất cả" ở đầu.
     */
    private void setupYearDropdown(List<EnrollmentResponse> data) {
        // Dùng LinkedHashMap để giữ thứ tự insert (data đã sort từ query)
        LinkedHashMap<String, Boolean> seen = new LinkedHashMap<>();
        for (EnrollmentResponse e : data) {
            if (e.academicYear != null && !e.academicYear.isEmpty()) {
                seen.put(e.academicYear, true);
            }
        }

        List<String> yearOptions = new ArrayList<>();
        yearOptions.add("Tất cả");
        yearOptions.addAll(seen.keySet());

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, yearOptions);
        dropYear.setAdapter(yearAdapter);
        dropYear.setText("Tất cả", false); // hiển thị mặc định, không trigger filter

        dropYear.setOnItemClickListener((parent, view, position, id) -> {
            String picked = (String) parent.getItemAtPosition(position);
            selectedYear = "Tất cả".equals(picked) ? null : picked;
            applyFilters(null); // reset search khi đổi năm
        });
    }

    // ── Filter + build list ───────────────────────────────────

    /**
     * Lọc rawData theo selectedYear, rồi build Subject list (header + môn),
     * cuối cùng áp thêm search query nếu có.
     *
     * @param searchQuery null hoặc rỗng = không lọc search
     */
    private void applyFilters(String searchQuery) {
        fullList.clear();

        // 1. Lọc theo năm học
        List<EnrollmentResponse> filtered = new ArrayList<>();
        for (EnrollmentResponse e : rawData) {
            if (selectedYear == null || selectedYear.equals(e.academicYear)) {
                filtered.add(e);
            }
        }

        // 2. Gom theo (academicYear, semesterNumber) — TreeMap giữ thứ tự
        //    Key: "2025-2026|1", "2025-2026|2", ...
        TreeMap<String, List<EnrollmentResponse>> bySem = new TreeMap<>();
        for (EnrollmentResponse e : filtered) {
            String year = e.academicYear != null ? e.academicYear : "";
            int    sem  = e.semesterNumber != null ? e.semesterNumber : 0;
            String key  = year + "|" + sem;
            bySem.computeIfAbsent(key, k -> new ArrayList<>()).add(e);
        }

        // 3. Build Subject list với header
        for (Map.Entry<String, List<EnrollmentResponse>> entry : bySem.entrySet()) {
            String[] parts  = entry.getKey().split("\\|");
            String   year   = parts[0];
            int      semNum = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            // Header: "KỲ 1 – 2025-2026"
            String headerLabel = "KỲ " + semNum + " – " + year;
            fullList.add(Subject.headerOf(semNum, headerLabel));

            for (EnrollmentResponse e : entry.getValue()) {
                String scoreStr;
                if (e.totalScore != null) {
                    scoreStr = e.totalScore % 1 == 0
                            ? String.valueOf(e.totalScore.intValue())
                            : String.format("%.1f", e.totalScore);
                } else {
                    scoreStr = "N/A";
                }
                fullList.add(new Subject(
                        e.courseCode  != null ? e.courseCode  : "",
                        e.courseName  != null ? e.courseName  : "",
                        e.credits     != null ? e.credits     : 0,
                        scoreStr,
                        e.letterGrade != null ? e.letterGrade : "",
                        e.gradePoint  != null ? e.gradePoint  : 0.0,
                        Boolean.TRUE.equals(e.isPassed),
                        semNum,
                        false
                ));
            }
        }

        // 4. Áp search nếu có
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            searchSubject(searchQuery);
        } else {
            if (adapter == null) {
                adapter = new SubjectAdapter(fullList);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(fullList);
            }
        }
    }

    // ── Scroll + Search ───────────────────────────────────────

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
        String input = (query != null) ? query.toLowerCase().trim() : "";

        // Query rỗng → hiện lại toàn bộ theo filter năm hiện tại
        if (input.isEmpty()) {
            if (adapter == null) {
                adapter = new SubjectAdapter(fullList);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(fullList);
            }
            return;
        }

        List<Subject> result = new ArrayList<>();
        try {
            // pendingHeader giữ header + các môn khớp của kỳ đang xét
            // Chỉ flush vào result khi có ít nhất 1 môn khớp
            List<Subject> pendingHeader = new ArrayList<>();
            boolean hasMatch = false;

            for (Subject item : fullList) {
                if (item.isHeader()) {
                    // Flush kỳ trước
                    if (hasMatch) result.addAll(pendingHeader);
                    pendingHeader.clear();
                    hasMatch = false;
                    pendingHeader.add(item);
                } else {
                    if (item.getName().toLowerCase().contains(input)
                            || item.getCode().toLowerCase().contains(input)) {
                        hasMatch = true;
                        pendingHeader.add(item);
                    }
                }
            }
            // Flush kỳ cuối
            if (hasMatch) result.addAll(pendingHeader);

        } catch (Exception e) {
            Log.e("TrainingProgram", getString(R.string.FindingError) + e.getMessage());
        }

        if (adapter == null) {
            adapter = new SubjectAdapter(result);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}