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
import com.utc2.appreborn.ui.dormitory.adapter.RoomAdapter;
import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;
import com.utc2.appreborn.ui.dormitory.lookup.adapter.OccupantAdapter;
import com.utc2.appreborn.ui.dormitory.lookup.model.DormitoryDbRepository;
import com.utc2.appreborn.ui.dormitory.lookup.model.LookupRepository;
import com.utc2.appreborn.ui.dormitory.lookup.model.RoomDetail;
import com.utc2.appreborn.ui.dormitory.model.Room;
import com.utc2.appreborn.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Kí túc xá – 1 Activity duy nhất chứa cả 2 trang.
 *
 * Tab bar (Đăng ký / Tra phòng) luôn cố định.
 * Khi bấm tab → chỉ đổi nội dung bên dưới (pageDangKy ↔ pageTraPhong),
 * header và tab bar KHÔNG thay đổi.
 *
 * [Chương 3 - OOP]  [Chương 4 - Ngoại lệ]  [Chương 5 - Collection]
 */
public class DormitoryActivity extends AppCompatActivity {

    private static final String TAG = "DormitoryActivity";

    // ── Tab & Pages ───────────────────────────────────────────────────────────
    private TextView    tabDangKy, tabTraPhong;
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
    private DormitoryDbRepository dormRepo;
    private LookupRepository    lookupRepo;

    // ── State Đăng ký ─────────────────────────────────────────────────────────
    private RoomAdapter   roomAdapter;
    private String        filterBuilding = "";
    private int           filterMaxPrice = 0;
    private Room.RoomType filterRoomType = null;
    private long          currentRegId   = -1;  // -1 = chưa có đăng ký

    // ── State Tra phòng ───────────────────────────────────────────────────────
    // null = chưa chọn; giá trị cụ thể = đã chọn
    private String selectedRoomId   = null;
    private String selectedBuilding = null;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_dormitory);

        dormRepo = DormitoryDbRepository.getInstance(this);
        lookupRepo = LookupRepository.getInstance();

        bindViews();
        applyWindowInsets();
        setupTabs();
        setupBackButton();

        // Khởi tạo trang Đăng ký
        setupRecyclerView();
        setupFilterButtons();
        setupSearchBox();
        setupCancelButton();

        // Khởi tạo trang Tra phòng
        setupLookupDropdowns();
        setupSearchButton();

        // Hiển thị trang Đăng ký mặc định
        showPage(true);
    }

    // ── Tự động căn theo status bar của từng máy ──────────────────────────────
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

    // ── Ánh xạ tất cả views ───────────────────────────────────────────────────
    private void bindViews() {
        // Tab & pages
        tabDangKy    = findViewById(R.id.tabDangKy);
        tabTraPhong  = findViewById(R.id.tabTraPhong);
        pageDangKy   = findViewById(R.id.pageDangKy);
        pageTraPhong = findViewById(R.id.pageTraPhong);

        // Header
        btnBack = findViewById(R.id.btnBack);

        // Đăng ký
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

        // Tra phòng
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

    // ── Chuyển trang (tab) ────────────────────────────────────────────────────
    /**
     * showDangKy=true  → hiện pageDangKy,   ẩn pageTraPhong
     * showDangKy=false → hiện pageTraPhong, ẩn pageDangKy
     *
     * Tab bar và header KHÔNG thay đổi — chỉ nội dung bên dưới đổi.
     */
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

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG ĐĂNG KÝ
    // ══════════════════════════════════════════════════════════════════════════

    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(new ArrayList<>(), this::handleRegisterClick);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setNestedScrollingEnabled(false);
        rvRooms.setAdapter(roomAdapter);

        // Load phòng từ DB (async)
        dormRepo.getAllRooms(rooms -> {
            List<Room> roomList = new ArrayList<>();
            for (DormitoryRoomEntity e : rooms) roomList.add(entityToRoom(e));
            roomAdapter.updateData(roomList);
        });
    }

    /** [Chương 4] xử lý đăng ký phòng. */
    private void handleRegisterClick(Room room) {
        long userId = 1L;
        long roomId;
        try {
            roomId = Long.parseLong(room.getId());
        } catch (NumberFormatException e) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_id_invalid));
            return;
        }
        dormRepo.registerRoom(roomId, userId, 8, (reg, error) -> {
            if (error != null) {
                Log.w(TAG, "DormitoryException: " + error.getMessage());
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, error.getMessage());
                return;
            }
            currentRegId = reg.registrationId;
            layoutSelected.setVisibility(View.VISIBLE);
            txtRoomInfo.setText(room.getName());
            if (txtRoomSubInfo != null)
                txtRoomSubInfo.setText(getString(R.string.room_subinfo,
                        room.getCapacity(),
                        String.format("%,d", room.getPricePerMonth()),
                        room.getRoomType().getLabel()));
            txtTotal.setText(getString(R.string.dorm_tong_tien,
                    String.format("%,d", reg.totalPrice)));
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_register_success));
            Log.d(TAG, "handleRegisterClick() done – roomId: " + room.getId());
        });
    }

    private void setupCancelButton() {
        if (btnCancel == null) return;
        btnCancel.setOnClickListener(v -> {
            if (currentRegId < 0) return;
            dormRepo.cancelRegistration(currentRegId, error -> {
                if (error != null) {
                    Log.w(TAG, "Cancel error: " + error.getMessage());
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(this, error.getMessage());
                    return;
                }
                currentRegId = -1;
                layoutSelected.setVisibility(View.GONE);
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_cancel_success));
                Log.d(TAG, "handleCancelClick() done.");
            });
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
                            filterBuilding = choice.equals(getString(R.string.dorm_filter_all)) ? "" : choice.replace(getString(R.string.toa_nha) + " ", "").replace("Block ", "");
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

    private void applyFilter() {
        String roomTypeStr = filterRoomType == null ? "" : filterRoomType.name();
        dormRepo.filterRooms(filterBuilding, filterMaxPrice, roomTypeStr, rooms -> {
            List<Room> roomList = new ArrayList<>();
            for (DormitoryRoomEntity e : rooms) roomList.add(entityToRoom(e));
            roomAdapter.updateData(roomList);
            Log.d(TAG, "applyFilter() done.");
        });
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                dormRepo.getAllRooms(rooms -> {
                    List<Room> filtered = new ArrayList<>();
                    for (DormitoryRoomEntity e : rooms) {
                        if (e.roomName != null && e.roomName.toLowerCase().contains(query))
                            filtered.add(entityToRoom(e));
                    }
                    roomAdapter.updateData(filtered);
                });
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG TRA PHÒNG
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Dropdown chọn phòng: chỉ hiện TÊN PHÒNG (vd: "Phòng 201").
     * Dropdown chọn tòa:   chỉ hiện TÒA (vd: "Tòa A").
     * Hai state độc lập nhau.
     */
    private void setupLookupDropdowns() {
        // Dropdown Chọn phòng
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

        // Dropdown Chọn tòa nhà
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

    /**
     * Logic tìm kiếm:
     *  - Phải chọn ít nhất 1 tiêu chí.
     *  - Nếu chọn CẢ HAI: tìm phòng theo ID rồi kiểm tra tòa có khớp không.
     *    Nếu không khớp → báo lỗi, không hiển thị.
     *  - Nếu chỉ chọn phòng: tìm theo ID.
     *  - Nếu chỉ chọn tòa: lấy phòng đầu tiên của tòa đó.
     *
     * [Chương 4] try-catch-finally
     */
    private void handleLookupSearch() {
        // Validate: BẮT BUỘC phải chọn đủ cả 2
        if (selectedRoomId == null && selectedBuilding == null) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_select_both));
            return;
        }
        if (selectedRoomId == null) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_select_room));
            return;
        }
        if (selectedBuilding == null) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_select_building));
            return;
        }

        try {
            // Đã chọn đủ cả 2 → kiểm tra phòng có thuộc tòa đó không
            RoomDetail detail = lookupRepo.findById(selectedRoomId);
            String roomBuilding = detail.getRoom().getBuilding(); // "A", "B", "C"
            if (!roomBuilding.equalsIgnoreCase(selectedBuilding)) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_wrong_building, selectedBuilding));
                cardRoomInfo.setVisibility(View.GONE);
                cardOccupants.setVisibility(View.GONE);
                return;
            }
            // Khớp → hiển thị thông tin
            showRoomDetail(detail);

        } catch (DormitoryException e) {
            Log.w(TAG, "DormitoryException: " + e.getMessage());
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, e.getMessage());
            cardRoomInfo.setVisibility(View.GONE);
            cardOccupants.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error khi tra phòng", e);
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.dorm_unknown_error));

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

        // Dùng ic_status_check / ic_status_x có sẵn trong drawable
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Chuyển DormitoryRoomEntity (DB) sang Room (UI model). */
    private Room entityToRoom(DormitoryRoomEntity e) {
        boolean available = "AVAILABLE".equals(e.status);
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

    /** "Phòng 201 - Tòa A" → "Phòng 201" */
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