package com.utc2.appreborn.ui.dormitory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.dormitory.adapter.RoomAdapter;
import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;
import com.utc2.appreborn.ui.dormitory.lookup.adapter.OccupantAdapter;
import com.utc2.appreborn.ui.dormitory.lookup.model.LookupRepository;
import com.utc2.appreborn.ui.dormitory.lookup.model.RoomDetail;
import com.utc2.appreborn.ui.dormitory.model.DormitoryRegistration;
import com.utc2.appreborn.ui.dormitory.model.DormitoryRepository;
import com.utc2.appreborn.ui.dormitory.model.Room;

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
    private DormitoryRepository dormRepo;
    private LookupRepository    lookupRepo;

    // ── State Đăng ký ─────────────────────────────────────────────────────────
    private RoomAdapter   roomAdapter;
    private String        filterBuilding = "";
    private int           filterMaxPrice = 0;
    private Room.RoomType filterRoomType = null;
    private String        currentRegId   = null;

    // ── State Tra phòng ───────────────────────────────────────────────────────
    // null = chưa chọn; giá trị cụ thể = đã chọn
    private String selectedRoomId   = null;
    private String selectedBuilding = null;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory);

        dormRepo   = DormitoryRepository.getInstance();
        lookupRepo = LookupRepository.getInstance();

        bindViews();
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
        List<Room> rooms = dormRepo.getAllRooms();
        roomAdapter = new RoomAdapter(rooms, this::handleRegisterClick);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setNestedScrollingEnabled(false);
        rvRooms.setAdapter(roomAdapter);
    }

    /** [Chương 4] try-catch-finally xử lý đăng ký phòng. */
    private void handleRegisterClick(Room room) {
        try {
            DormitoryRegistration reg = dormRepo.registerRoom(room.getId(), 8);
            currentRegId = reg.getId();

            layoutSelected.setVisibility(View.VISIBLE);
            txtRoomInfo.setText(room.getName());
            if (txtRoomSubInfo != null) txtRoomSubInfo.setText(room.getDisplayInfo());
            txtTotal.setText("Tổng số tiền: "
                    + String.format("%,d", reg.getTotalPrice()) + "đ");

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

        } catch (DormitoryException e) {
            Log.w(TAG, "DormitoryException: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error khi đăng ký", e);
            Toast.makeText(this, "Lỗi không xác định, vui lòng thử lại.", Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "handleRegisterClick() done – roomId: " + room.getId());
        }
    }

    private void setupCancelButton() {
        if (btnCancel == null) return;
        btnCancel.setOnClickListener(v -> {
            try {
                if (currentRegId != null) {
                    dormRepo.cancelRegistration(currentRegId);
                    currentRegId = null;
                }
                layoutSelected.setVisibility(View.GONE);
                Toast.makeText(this, "Đã hủy đăng ký.", Toast.LENGTH_SHORT).show();

            } catch (DormitoryException e) {
                Log.w(TAG, "Cancel error: " + e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                Log.d(TAG, "handleCancelClick() done.");
            }
        });
    }

    private void setupFilterButtons() {
        btnToa.setOnClickListener(v ->
                showPopupMenu(btnToa,
                        new String[]{"Tất cả", "Tòa A", "Tòa B", "Tòa C"},
                        choice -> {
                            filterBuilding = choice.equals("Tất cả") ? "" : choice.replace("Tòa ", "");
                            btnToa.setText(choice + " ▾");
                            applyFilter();
                        }));

        btnGia.setOnClickListener(v ->
                showPopupMenu(btnGia,
                        new String[]{"Tất cả", "≤ 300,000đ", "≤ 500,000đ", "≤ 700,000đ"},
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
                        new String[]{"Tất cả", "Nam", "Nữ"},
                        choice -> {
                            if      (choice.equals("Nam")) filterRoomType = Room.RoomType.NAM;
                            else if (choice.equals("Nữ"))  filterRoomType = Room.RoomType.NU;
                            else                           filterRoomType = null;
                            btnLoai.setText(choice + " ▾");
                            applyFilter();
                        }));
    }

    private void applyFilter() {
        try {
            List<Room> filtered = dormRepo.filterRooms(filterBuilding, filterMaxPrice, filterRoomType);
            roomAdapter.updateData(filtered);
        } catch (DormitoryException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            Log.d(TAG, "applyFilter() done.");
        }
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String query = s.toString().trim().toLowerCase();
                    List<Room> all = dormRepo.getAllRooms();
                    List<Room> filtered = new ArrayList<>();
                    for (Room room : all) {
                        if (room.getName().toLowerCase().contains(query)) filtered.add(room);
                    }
                    roomAdapter.updateData(filtered);
                } catch (Exception e) {
                    Log.e(TAG, "Search error", e);
                }
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
            popup.getMenu().add(0, 0, 0, "-- Chọn phòng --");
            for (int i = 0; i < all.size(); i++) {
                String label = extractRoomNumber(all.get(i).getRoom().getName());
                popup.getMenu().add(0, i + 1, i + 1, label);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) {
                    selectedRoomId = null;
                    btnChonPhong.setText("Chọn phòng  ▾");
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
            popup.getMenu().add(0, 0, 0, "-- Chọn tòa --");
            popup.getMenu().add(0, 1, 1, "Tòa A");
            popup.getMenu().add(0, 2, 2, "Tòa B");
            popup.getMenu().add(0, 3, 3, "Tòa C");
            popup.setOnMenuItemClickListener(item -> {
                int idx = item.getItemId();
                if (idx == 0) {
                    selectedBuilding = null;
                    btnChonToa.setText("Chọn tòa  ▾");
                } else {
                    String[] buildings = {"A", "B", "C"};
                    selectedBuilding = buildings[idx - 1];
                    btnChonToa.setText("Tòa " + selectedBuilding + "  ▾");
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
            Toast.makeText(this,
                    "Vui lòng chọn phòng và tòa nhà trước khi tìm kiếm.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedRoomId == null) {
            Toast.makeText(this, "Vui lòng chọn phòng.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedBuilding == null) {
            Toast.makeText(this, "Vui lòng chọn tòa nhà.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Đã chọn đủ cả 2 → kiểm tra phòng có thuộc tòa đó không
            RoomDetail detail = lookupRepo.findById(selectedRoomId);
            String roomBuilding = detail.getRoom().getBuilding(); // "A", "B", "C"
            if (!roomBuilding.equalsIgnoreCase(selectedBuilding)) {
                Toast.makeText(this,
                        "Phòng đã chọn không thuộc Tòa " + selectedBuilding + ". Vui lòng kiểm tra lại.",
                        Toast.LENGTH_LONG).show();
                cardRoomInfo.setVisibility(View.GONE);
                cardOccupants.setVisibility(View.GONE);
                return;
            }
            // Khớp → hiển thị thông tin
            showRoomDetail(detail);

        } catch (DormitoryException e) {
            Log.w(TAG, "DormitoryException: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            cardRoomInfo.setVisibility(View.GONE);
            cardOccupants.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error khi tra phòng", e);
            Toast.makeText(this, "Lỗi không xác định, vui lòng thử lại.", Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "handleLookupSearch() done");
        }
    }

    private void showRoomDetail(RoomDetail detail) {
        Room room = detail.getRoom();

        cardRoomInfo.setVisibility(View.VISIBLE);
        tvRoomName.setText(room.getName());
        tvRoomStatus.setText("Trạng thái: " + detail.getStatusLabel());
        tvRoomType.setText("Loại phòng: " + room.getRoomType().getLabel());
        tvCapacity.setText("Sức chứa: " + room.getCapacity());
        tvPrice.setText("Giá: " + String.format("%,d", room.getPricePerMonth()) + "/tháng");

        int progress = (int) (detail.getOccupancyRatio() * 100);
        progressOccupancy.setProgress(progress);
        tvCurrentCount.setText("Hiện tại : " + detail.getCurrentOccupants());
        tvCapacityLabel.setText(detail.getCurrentOccupants() + " / " + room.getCapacity());

        // Dùng ic_status_check / ic_status_x có sẵn trong drawable
        if (room.isAvailable()) {
            ivStatusIcon.setImageResource(R.drawable.ic_status_check);
            tvStatusBadge.setText("Trạng thái: Còn chỗ");
            tvStatusBadge.setTextColor(getResources().getColor(R.color.green, null));
        } else {
            ivStatusIcon.setImageResource(R.drawable.ic_status_x);
            tvStatusBadge.setText("Trạng thái: Hết chỗ");
            tvStatusBadge.setTextColor(getResources().getColor(R.color.red, null));
        }

        cardOccupants.setVisibility(View.VISIBLE);
        OccupantAdapter adapter = new OccupantAdapter(detail.getOccupantList());
        rvOccupants.setLayoutManager(new LinearLayoutManager(this));
        rvOccupants.setNestedScrollingEnabled(false);
        rvOccupants.setAdapter(adapter);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
