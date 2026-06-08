package com.utc2.appreborn.ui.dormitory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.data.local.entity.DormitoryRegistrationEntity;
import com.utc2.appreborn.data.local.entity.DormitoryRoomEntity;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.DormApiService;
import com.utc2.appreborn.network.dto.DormRegistrationResponse;
import com.utc2.appreborn.ui.dormitory.adapter.RoomAdapter;
import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;
import com.utc2.appreborn.ui.dormitory.lookup.adapter.OccupantAdapter;
import com.utc2.appreborn.ui.dormitory.lookup.model.DormitoryDbRepository;
import com.utc2.appreborn.ui.dormitory.lookup.model.LookupRepository;
import com.utc2.appreborn.ui.dormitory.lookup.model.RoomDetail;
import com.utc2.appreborn.ui.dormitory.model.Room;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình Ký túc xá.
 *
 * CHIẾN LƯỢC FIX (không phá chức năng cũ):
 *  - Hiển thị / tìm kiếm / lọc phòng: vẫn dùng DormitoryDbRepository (local) như cũ.
 *  - Đăng ký phòng (handleRegisterClick): GỌI API thật thay vì ghi local DB.
 *    → Server nhận request → tạo fee record → màn hình thanh toán hiện đúng số tiền.
 *  - Hủy đăng ký: gọi API DELETE.
 */
public class DormitoryActivity extends AppCompatActivity {

    private static final String TAG = "DormitoryActivity";
    private static final int    DEFAULT_MONTHS = 8;

    // ── Tab & Pages ───────────────────────────────────────────────────────────
    private TextView     tabDangKy, tabTraPhong;
    private LinearLayout pageDangKy;
    private LinearLayout pageTraPhong;

    // ── Views trang Đăng ký ───────────────────────────────────────────────────
    private RecyclerView  rvRooms;
    private EditText      searchBox;
    private LinearLayout  layoutSelected;
    private TextView      txtRoomInfo, txtRoomSubInfo, txtTotal;
    private TextView      btnToa, btnGia, btnLoai, btnBack;
    private TextView      btnCancel;

    // ── Views trang Tra phòng ─────────────────────────────────────────────────
    private TextView         btnChonPhong, btnChonToa, btnTimKiem;
    private MaterialCardView cardRoomInfo, cardOccupants;
    private TextView         tvRoomName, tvRoomStatus;
    private TextView         tvRoomType, tvCapacity, tvPrice;
    private TextView         tvCurrentCount, tvCapacityLabel;
    private ProgressBar      progressOccupancy;
    private TextView         tvStatusBadge;
    private ImageView        ivStatusIcon;
    private RecyclerView     rvOccupants;

    // ── Repositories ──────────────────────────────────────────────────────────
    // Giữ nguyên dormRepo cho hiển thị/tìm kiếm/lọc — không đụng vào
    private DormitoryDbRepository dormRepo;
    private LookupRepository      lookupRepo;

    // ── State Đăng ký ─────────────────────────────────────────────────────────
    private RoomAdapter   roomAdapter;
    private String        filterBuilding = "";
    private int           filterMaxPrice = 0;
    private Room.RoomType filterRoomType = null;
    private String        searchQuery    = "";
    private long          currentRegId   = -1;

    // ── State Tra phòng ───────────────────────────────────────────────────────
    private String selectedRoomId   = null;
    private String selectedBuilding = null;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_dormitory);

        // Local repo — giữ nguyên như code gốc
        dormRepo   = DormitoryDbRepository.getInstance(this);
        lookupRepo = LookupRepository.getInstance();

        // dormApi được tạo lazy khi cần — xem getDormApi()

        bindViews();
        applyWindowInsets();
        setupTabs();
        setupBackButton();

        setupRecyclerView();
        setupFilterButtons();
        setupSearchBox();
        setupCancelButton();

        setupLookupDropdowns();
        setupSearchButton();

        showPage(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG ĐĂNG KÝ — hiển thị/lọc/tìm kiếm giữ nguyên như code gốc
    // ══════════════════════════════════════════════════════════════════════════

    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(new ArrayList<>(), this::handleRegisterClick);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setNestedScrollingEnabled(false);
        rvRooms.setAdapter(roomAdapter);

        // Load phòng từ API backend (roomId đúng với server)
        loadRoomsFromApi();
    }

    private void loadRoomsFromApi() {
        getDormApi().getRooms().enqueue(new retrofit2.Callback<ApiResponse<java.util.List<com.utc2.appreborn.network.dto.DormRoomResponse>>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<java.util.List<com.utc2.appreborn.network.dto.DormRoomResponse>>> call,
                                   retrofit2.Response<ApiResponse<java.util.List<com.utc2.appreborn.network.dto.DormRoomResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    java.util.List<Room> roomList = new ArrayList<>();
                    for (com.utc2.appreborn.network.dto.DormRoomResponse r : response.body().getData()) {
                        roomList.add(apiRoomToRoom(r));
                    }
                    runOnUiThread(() -> {
                        allRooms.clear();
                        allRooms.addAll(roomList);
                        roomAdapter.updateData(roomList);
                    });
                } else {
                    // Fallback: dùng local DB nếu API lỗi
                    Log.w(TAG, "API getRooms failed, fallback local DB");
                    loadRoomsFromLocal();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<java.util.List<com.utc2.appreborn.network.dto.DormRoomResponse>>> call, Throwable t) {
                Log.w(TAG, "API getRooms network error, fallback local: " + t.getMessage());
                loadRoomsFromLocal();
            }
        });
    }

    private void loadRoomsFromLocal() {
        dormRepo.getAllRooms(rooms -> {
            List<Room> roomList = new ArrayList<>();
            for (DormitoryRoomEntity e : rooms) roomList.add(entityToRoom(e));
            roomAdapter.updateData(roomList);
        });
    }

    /**
     * FIX CHÍNH: Thay vì gọi dormRepo.registerRoom() (ghi local DB),
     * giờ gọi POST /api/v1/dormitory/register để server tạo fee record.
     */
    private void handleRegisterClick(Room room) {
        long roomId;
        try {
            roomId = Long.parseLong(room.getId());
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.dorm_id_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("roomId", roomId);
        body.put("months", DEFAULT_MONTHS);

        getDormApi().register(body).enqueue(new Callback<ApiResponse<DormRegistrationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DormRegistrationResponse>> call,
                                   Response<ApiResponse<DormRegistrationResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    DormRegistrationResponse reg = response.body().getData();
                    currentRegId = reg.dormRegId != null ? reg.dormRegId : -1;

                    layoutSelected.setVisibility(View.VISIBLE);
                    txtRoomInfo.setText(room.getName());
                    if (txtRoomSubInfo != null)
                        txtRoomSubInfo.setText(getString(R.string.room_subinfo,
                                room.getCapacity(),
                                String.format("%,d", (long) room.getPricePerMonth()),
                                room.getRoomType().getLabel()));

                    double totalFee = reg.getTotalFeeAsDouble();
                    txtTotal.setText(getString(R.string.dorm_tong_tien,
                            String.format("%,.0f", totalFee)));

                    Toast.makeText(DormitoryActivity.this,
                            getString(R.string.dorm_register_success), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Registered via API, dormRegId=" + reg.dormRegId + " fee=" + totalFee);

                } else {
                    int code = response.code();
                    String msg = null;
                    try {
                        if (response.errorBody() != null) {
                            // Parse JSON errorBody để lấy field "message"
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            msg = json.optString("message", null);
                        }
                    } catch (Exception ignored) {}
                    Log.w(TAG, "API register failed HTTP " + code + " msg=" + msg);
                    String display = msg != null ? msg : "Đăng ký thất bại (lỗi " + code + ")";
                    Toast.makeText(DormitoryActivity.this, display, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DormRegistrationResponse>> call, Throwable t) {
                Log.w(TAG, "API register network error: " + t.getMessage());
                Toast.makeText(DormitoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Fallback: ghi local DB như code gốc khi API không khả dụng */
    private void registerRoomLocal(Room room) {
        long userId = SessionManager.getInstance(this).getUserId();
        if (userId < 0) userId = 1L; // guest fallback
        long roomId = Long.parseLong(room.getId());

        dormRepo.registerRoom(roomId, userId, DEFAULT_MONTHS, (reg, error) -> {
            if (error != null) {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            currentRegId = reg.registrationId;
            layoutSelected.setVisibility(View.VISIBLE);
            txtRoomInfo.setText(room.getName());
            if (txtRoomSubInfo != null)
                txtRoomSubInfo.setText(getString(R.string.room_subinfo,
                        room.getCapacity(),
                        String.format("%,d", (long) room.getPricePerMonth()),
                        room.getRoomType().getLabel()));
            txtTotal.setText(getString(R.string.dorm_tong_tien,
                    String.format("%,d", reg.totalPrice)));
            Toast.makeText(this, getString(R.string.dorm_register_success), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCancelButton() {
        if (btnCancel == null) return;
        btnCancel.setOnClickListener(v -> {
            if (currentRegId < 0) return;

            // Thử API trước, fallback local nếu lỗi
            getDormApi().cancel(currentRegId).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call,
                                       Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        currentRegId = -1;
                        layoutSelected.setVisibility(View.GONE);
                        Toast.makeText(DormitoryActivity.this,
                                getString(R.string.dorm_cancel_success), Toast.LENGTH_SHORT).show();
                    } else {
                        cancelLocal();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    cancelLocal();
                }
            });
        });
    }

    private void cancelLocal() {
        dormRepo.cancelRegistration(currentRegId, error -> {
            if (error != null) {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            currentRegId = -1;
            layoutSelected.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.dorm_cancel_success), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFilterButtons() {
        btnToa.setOnClickListener(v ->
                showPopupMenu(btnToa,
                        new String[]{getString(R.string.dorm_filter_all),
                                getString(R.string.dorm_filter_toa_a),
                                getString(R.string.dorm_filter_toa_b),
                                getString(R.string.dorm_filter_toa_c)},
                        choice -> {
                            filterBuilding = choice.equals(getString(R.string.dorm_filter_all)) ? "" :
                                    choice.replaceAll("(?i)^(Tòa|Block)\\s+", "").trim();
                            btnToa.setText(choice + " ▾");
                            applyFilter();
                        }));

        btnGia.setOnClickListener(v ->
                showPopupMenu(btnGia,
                        new String[]{getString(R.string.dorm_filter_all), "≤ 300,000đ", "≤ 500,000đ", "≤ 700,000đ"},
                        choice -> {
                            if      (choice.contains("300")) filterMaxPrice = 300000;
                            else if (choice.contains("500")) filterMaxPrice = 500000;
                            else if (choice.contains("700")) filterMaxPrice = 700000;
                            else                             filterMaxPrice = 0;
                            btnGia.setText(choice + " ▾");
                            applyFilter();
                        }));

        btnLoai.setOnClickListener(v ->
                showPopupMenu(btnLoai,
                        new String[]{getString(R.string.dorm_filter_all), "Nam", "Nữ"},
                        choice -> {
                            if      (choice.equals("Nam")) filterRoomType = Room.RoomType.NAM;
                            else if (choice.equals("Nữ"))  filterRoomType = Room.RoomType.NU;
                            else                           filterRoomType = null;
                            btnLoai.setText(choice + " ▾");
                            applyFilter();
                        }));
    }

    // allRooms giữ toàn bộ list gốc từ API để filter không mất data
    private final List<Room> allRooms = new ArrayList<>();

    private void applyFilter() {
        List<Room> filtered = new ArrayList<>();
        String q = searchQuery.toLowerCase();
        for (Room room : allRooms) {
            boolean okBuilding = filterBuilding.isEmpty()
                    || room.getBuilding().equalsIgnoreCase(filterBuilding);
            boolean okPrice    = filterMaxPrice == 0
                    || room.getPricePerMonth() <= filterMaxPrice;
            boolean okType     = filterRoomType == null
                    || room.getRoomType() == filterRoomType;
            boolean okSearch   = q.isEmpty()
                    || room.getName().toLowerCase().contains(q);
            if (okBuilding && okPrice && okType && okSearch) filtered.add(room);
        }
        roomAdapter.updateData(filtered);
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                applyFilter();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG TRA PHÒNG — giữ nguyên hoàn toàn như code gốc
    // ══════════════════════════════════════════════════════════════════════════

    private void setupLookupDropdowns() {
        btnChonPhong.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            List<RoomDetail> all = lookupRepo.getAllRoomDetails();
            all.sort((a, b) -> a.getRoom().getId().compareTo(b.getRoom().getId()));
            popup.getMenu().add(0, 0, 0, getString(R.string.dorm_dropdown_chon_phong));
            for (int i = 0; i < all.size(); i++) {
                String label = extractRoomNumber(all.get(i).getRoom().getName());
                popup.getMenu().add(0, i + 1, i + 1, label);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) {
                    selectedRoomId = null;
                    btnChonPhong.setText(getString(R.string.chon_phong));
                } else {
                    RoomDetail chosen = all.get(item.getItemId() - 1);
                    selectedRoomId = chosen.getRoom().getId();
                    btnChonPhong.setText(extractRoomNumber(chosen.getRoom().getName()) + "  ▾");
                }
                return true;
            });
            popup.show();
        });

        btnChonToa.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add(0, 0, 0, getString(R.string.dorm_dropdown_chon_toa));
            popup.getMenu().add(0, 1, 1, getString(R.string.dorm_filter_toa_a));
            popup.getMenu().add(0, 2, 2, getString(R.string.dorm_filter_toa_b));
            popup.getMenu().add(0, 3, 3, getString(R.string.dorm_filter_toa_c));
            popup.setOnMenuItemClickListener(item -> {
                int idx = item.getItemId();
                if (idx == 0) {
                    selectedBuilding = null;
                    btnChonToa.setText(getString(R.string.chon_toa));
                } else {
                    String[] buildings = {"A", "B", "C"};
                    selectedBuilding = buildings[idx - 1];
                    btnChonToa.setText(getString(R.string.dorm_toa_prefix, selectedBuilding));
                }
                return true;
            });
            popup.show();
        });
    }

    private void setupSearchButton() {
        btnTimKiem.setOnClickListener(v -> handleLookupSearch());
    }

    private void handleLookupSearch() {
        if (selectedRoomId == null && selectedBuilding == null) {
            Toast.makeText(this, getString(R.string.dorm_select_both), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedRoomId == null) {
            Toast.makeText(this, getString(R.string.dorm_select_room), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedBuilding == null) {
            Toast.makeText(this, getString(R.string.dorm_select_building), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            RoomDetail detail = lookupRepo.findById(selectedRoomId);
            String roomBuilding = detail.getRoom().getBuilding();
            if (!roomBuilding.equalsIgnoreCase(selectedBuilding)) {
                Toast.makeText(this,
                        getString(R.string.dorm_wrong_building, selectedBuilding),
                        Toast.LENGTH_LONG).show();
                cardRoomInfo.setVisibility(View.GONE);
                cardOccupants.setVisibility(View.GONE);
                return;
            }
            showRoomDetail(detail);
        } catch (DormitoryException e) {
            Log.w(TAG, "DormitoryException: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            cardRoomInfo.setVisibility(View.GONE);
            cardOccupants.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error khi tra phòng", e);
            Toast.makeText(this, getString(R.string.dorm_unknown_error), Toast.LENGTH_SHORT).show();
        } finally {
            Log.d(TAG, "handleLookupSearch() done");
        }
    }

    private void showRoomDetail(RoomDetail detail) {
        Room room = detail.getRoom();
        cardRoomInfo.setVisibility(View.VISIBLE);
        tvRoomName.setText(room.getName());
        tvRoomStatus.setText(getString(R.string.dorm_trang_thai, detail.getStatusLabel()));
        tvRoomType.setText(getString(R.string.dorm_loai_phong, room.getRoomType().getLabel()));
        tvCapacity.setText(getString(R.string.dorm_suc_chua, room.getCapacity()));
        tvPrice.setText(getString(R.string.dorm_gia, String.format("%,d", room.getPricePerMonth())));
        int progress = (int) (detail.getOccupancyRatio() * 100);
        progressOccupancy.setProgress(progress);
        tvCurrentCount.setText(getString(R.string.dorm_hien_tai, detail.getCurrentOccupants()));
        tvCapacityLabel.setText(detail.getCurrentOccupants() + " / " + room.getCapacity());
        if (room.isAvailable()) {
            ivStatusIcon.setImageResource(R.drawable.ic_status_check);
            tvStatusBadge.setText(getString(R.string.trang_thai_con_cho));
            tvStatusBadge.setTextColor(getResources().getColor(R.color.green, null));
        } else {
            ivStatusIcon.setImageResource(R.drawable.ic_status_x);
            tvStatusBadge.setText(getString(R.string.trang_thai_het_cho));
            tvStatusBadge.setTextColor(getResources().getColor(R.color.red, null));
        }
        cardOccupants.setVisibility(View.VISIBLE);
        OccupantAdapter adapter = new OccupantAdapter(detail.getOccupantList());
        rvOccupants.setLayoutManager(new LinearLayoutManager(this));
        rvOccupants.setNestedScrollingEnabled(false);
        rvOccupants.setAdapter(adapter);
    }

    // ── Boilerplate giữ nguyên ────────────────────────────────────────────────

    private void applyWindowInsets() {
        View statusBarSpacer = findViewById(R.id.statusBarSpacer);
        View scrollView = findViewById(R.id.scrollView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ViewGroup.LayoutParams lp = statusBarSpacer.getLayoutParams();
            lp.height = statusH;
            statusBarSpacer.setLayoutParams(lp);
            int navH = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            scrollView.setPadding(0, 0, 0, navH);
            return insets;
        });
    }

    private void bindViews() {
        tabDangKy    = findViewById(R.id.tabDangKy);
        tabTraPhong  = findViewById(R.id.tabTraPhong);
        pageDangKy   = findViewById(R.id.pageDangKy);
        pageTraPhong = findViewById(R.id.pageTraPhong);
        btnBack      = findViewById(R.id.btnBack);
        rvRooms        = findViewById(R.id.rvRooms);
        searchBox      = findViewById(R.id.searchBox);
        layoutSelected = findViewById(R.id.layoutSelected);
        txtRoomInfo    = findViewById(R.id.txtRoomInfo);
        txtRoomSubInfo = findViewById(R.id.txtRoomSubInfo);
        txtTotal       = findViewById(R.id.txtTotal);
        btnToa         = findViewById(R.id.btnToa);
        btnGia         = findViewById(R.id.btnGia);
        btnLoai        = findViewById(R.id.btnLoai);
        btnCancel      = findViewById(R.id.btnCancel);
        btnChonPhong      = findViewById(R.id.btnChonPhong);
        btnChonToa        = findViewById(R.id.btnChonToa);
        btnTimKiem        = findViewById(R.id.btnTimKiem);
        cardRoomInfo      = findViewById(R.id.cardRoomInfo);
        cardOccupants     = findViewById(R.id.cardOccupants);
        tvRoomName        = findViewById(R.id.tvRoomName);
        tvRoomStatus      = findViewById(R.id.tvRoomStatus);
        tvRoomType        = findViewById(R.id.tvRoomType);
        tvCapacity        = findViewById(R.id.tvCapacity);
        tvPrice           = findViewById(R.id.tvPrice);
        tvCurrentCount    = findViewById(R.id.tvCurrentCount);
        tvCapacityLabel   = findViewById(R.id.tvCapacityLabel);
        progressOccupancy = findViewById(R.id.progressOccupancy);
        tvStatusBadge     = findViewById(R.id.tvStatusBadge);
        ivStatusIcon      = findViewById(R.id.ivStatusIcon);
        rvOccupants       = findViewById(R.id.rvOccupants);
    }

    private void showPage(boolean showDangKy) {
        if (showDangKy) {
            pageDangKy.setVisibility(View.VISIBLE);
            pageTraPhong.setVisibility(View.GONE);
            tabDangKy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabDangKy.setTextColor(android.graphics.Color.WHITE);
            tabTraPhong.setBackground(null);
            tabTraPhong.setTextColor(android.graphics.Color.BLACK);
        } else {
            pageDangKy.setVisibility(View.GONE);
            pageTraPhong.setVisibility(View.VISIBLE);
            tabTraPhong.setBackgroundResource(R.drawable.bg_tab_selected);
            tabTraPhong.setTextColor(android.graphics.Color.WHITE);
            tabDangKy.setBackground(null);
            tabDangKy.setTextColor(android.graphics.Color.BLACK);
        }
    }

    private void setupTabs() {
        tabDangKy.setOnClickListener(v -> showPage(true));
        tabTraPhong.setOnClickListener(v -> showPage(false));
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    /** Tạo DormApiService dùng ApiClient singleton (BASE_URL tập trung) */
    private DormApiService getDormApi() {
        String token = SessionManager.getInstance(this).getAuthToken();
        Log.d(TAG, ">>> TOKEN = " + token);
        return ApiClient.getInstance(token).create(DormApiService.class);
    }

    /** Convert API response → Room model (roomId là Long từ server) */
    private Room apiRoomToRoom(com.utc2.appreborn.network.dto.DormRoomResponse r) {
        boolean available = Boolean.TRUE.equals(r.available) || "còn chỗ".equals(r.status);
        Room.RoomType type = "NU".equals(r.roomType) ? Room.RoomType.NU : Room.RoomType.NAM;
        String name = r.roomCode + " - Tòa " + r.building;
        int price = r.pricePerMonth != null ? r.pricePerMonth.intValue() : 0;
        int cap   = r.capacity   != null ? r.capacity   : 0;
        // roomId từ server — đây là giá trị đúng để gửi khi đăng ký
        return new Room(
                String.valueOf(r.roomId),
                name,
                r.building,
                cap,
                price,
                available,
                type
        );
    }

    private Room entityToRoom(DormitoryRoomEntity e) {
        boolean available = "AVAILABLE".equals(e.status) || "còn chỗ".equals(e.status);
        Room.RoomType type = "NU".equals(e.roomType) ? Room.RoomType.NU : Room.RoomType.NAM;
        return new Room(
                String.valueOf(e.roomId),
                e.roomName,
                e.building,
                e.capacity,
                e.pricePerMonth,
                available,
                type
        );
    }

    private String extractRoomNumber(String fullName) {
        if (fullName == null) return "";
        int dashIndex = fullName.indexOf(" - ");
        return dashIndex > 0 ? fullName.substring(0, dashIndex).trim() : fullName;
    }

    private interface MenuCallback { void onSelected(String choice); }

    private void showPopupMenu(TextView anchor, String[] options, MenuCallback cb) {
        PopupMenu menu = new PopupMenu(this, anchor);
        for (String opt : options) menu.getMenu().add(opt);
        menu.setOnMenuItemClickListener(item -> {
            cb.onSelected(item.getTitle().toString());
            return true;
        });
        menu.show();
    }
}
