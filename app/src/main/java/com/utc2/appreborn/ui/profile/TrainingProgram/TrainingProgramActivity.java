package com.utc2.appreborn.ui.profile.TrainingProgram;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast; // Thêm Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.adapter.SubjectAdapter;
import com.utc2.appreborn.ui.profile.model.Subject;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

import java.util.ArrayList;
import java.util.List;

public class TrainingProgramActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private List<Subject> fullList;
    private Chip chipSem1, chipSem2;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        try {
            initViews();
            setupNetworkMonitoring(); // Khởi tạo check mạng
            loadData();
            setupEvents();
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khởi tạo Activity: " + e.getMessage());
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerSubject);
        chipSem1 = findViewById(R.id.chipSem1);
        chipSem2 = findViewById(R.id.chipSem2);

        ImageButton btnBack = findViewById(R.id.btnBackProfile);
        SearchView searchView = findViewById(R.id.searchView);
        AutoCompleteTextView dropYear = findViewById(R.id.dropYear);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] years = {"Năm 1", "Năm 2", "Năm 3", "Năm 4"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, years);
        dropYear.setAdapter(dropdownAdapter);

        btnBack.setOnClickListener(v -> finish());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSubject(newText);
                return true;
            }
        });
    }

    // Thiết lập theo dõi mạng
    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                // Khi có mạng lại, bạn có thể gọi hàm loadData() từ Server ở đây
                Log.d("Network", "Đã có mạng");
            }

            @Override
            public void onNetworkLost() {
                // Thông báo cho người dùng biết dữ liệu có thể không mới nhất
                Toast.makeText(TrainingProgramActivity.this,
                        "Mất kết nối mạng. Bạn đang xem dữ liệu ngoại tuyến.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void loadData() {
        // Sau này khi dùng Web API, bạn sẽ check:
        // if (NetworkUtils.isNetworkAvailable(this)) { loadFromWeb(); } else { loadFromLocal(); }

        fullList = new ArrayList<>();
        try {
            fullList.add(new Subject("", "KỲ HỌC 1", "", "", 1, true));
            fullList.add(new Subject("BS0.001.2", "GIẢI TÍCH 1", "2", "7.80", 1, false));
            fullList.add(new Subject("BS0.101.3", "ĐẠI SỐ TUYẾN TÍNH", "3", "6.0", 1, false));
            fullList.add(new Subject("ANHA1.4", "TIẾNG ANH A1", "4", "Chưa có", 1, false));

            fullList.add(new Subject("", "KỲ HỌC 2", "", "", 2, true));
            fullList.add(new Subject("IT1.002.3", "LẬP TRÌNH C++", "3", "8.5", 2, false));
            fullList.add(new Subject("IT2.005.3", "CẤU TRÚC DỮ LIỆU", "3", "Chưa có", 2, false));
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi nạp dữ liệu: " + e.getMessage());
        } finally {
            adapter = new SubjectAdapter(fullList);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupEvents() {
        chipSem1.setOnClickListener(v -> scrollToSemester(1));
        chipSem2.setOnClickListener(v -> scrollToSemester(2));
    }

    private void scrollToSemester(int sem) {
        try {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
                for (int i = 0; i < fullList.size(); i++) {
                    Subject item = fullList.get(i);
                    if (item.isHeader() && item.getSemester() == sem) {
                        llm.scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khi cuộn trang: " + e.getMessage());
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
            Log.e("TrainingProgram", "Lỗi khi tìm kiếm: " + e.getMessage());
        } finally {
            if (adapter != null) {
                adapter.updateList(filteredList);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Quan trọng: Hủy đăng ký để tránh tốn pin và leak memory
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}