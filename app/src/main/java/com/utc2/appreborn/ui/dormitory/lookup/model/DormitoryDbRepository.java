package com.utc2.appreborn.ui.dormitory.lookup.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.dao.DormitoryDao;
import com.utc2.appreborn.data.local.entity.DormitoryRegistrationEntity;
import com.utc2.appreborn.data.local.entity.DormitoryRoomEntity;
import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DormitoryDbRepository — Thay thế DormitoryRepository (RAM).
 *
 * Kết nối trực tiếp với AppDatabase (Room) để:
 *  - Lưu đăng ký KTX vào database → tồn tại sau khi thoát app
 *  - Đọc danh sách phòng từ database → đồng bộ với các trang khác
 *
 * Cách dùng (đồng bộ với pattern ScheduleRepository, StudentRepository):
 *  - Mọi thao tác DB chạy trên background thread (ExecutorService)
 *  - Kết quả trả về UI thread qua Callback
 *
 * Thay DormitoryRepository.getInstance() bằng DormitoryDbRepository.getInstance(context)
 * trong DormitoryActivity.
 */
public class DormitoryDbRepository {

    // ── Singleton ─────────────────────────────────────────────
    private static volatile DormitoryDbRepository instance;

    private final DormitoryDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private DormitoryDbRepository(Context context) {
        dao = AppDatabase.getInstance(context).dormitoryDao();
        // Nạp dữ liệu mẫu nếu DB trống (chỉ lần đầu)
        executor.execute(this::seedIfEmpty);
    }

    public static DormitoryDbRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (DormitoryDbRepository.class) {
                if (instance == null) {
                    instance = new DormitoryDbRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────
    // SEED DATA — nạp dữ liệu mẫu lần đầu
    // ─────────────────────────────────────────────────────────
    private void seedIfEmpty() {
        if (dao.countRooms() > 0) return; // Đã có dữ liệu → bỏ qua

        List<DormitoryRoomEntity> rooms = new ArrayList<>();

        DormitoryRoomEntity r1 = new DormitoryRoomEntity();
        r1.roomId = 1;  r1.roomName = "Phòng 201 - Tòa A"; r1.building = "A";
        r1.capacity = 4; r1.currentOccupants = 2; r1.pricePerMonth = 300000;
        r1.roomType = "NAM"; r1.status = "AVAILABLE";
        rooms.add(r1);

        DormitoryRoomEntity r2 = new DormitoryRoomEntity();
        r2.roomId = 2;  r2.roomName = "Phòng 202 - Tòa A"; r2.building = "A";
        r2.capacity = 6; r2.currentOccupants = 6; r2.pricePerMonth = 450000;
        r2.roomType = "NU"; r2.status = "FULL";
        rooms.add(r2);

        DormitoryRoomEntity r3 = new DormitoryRoomEntity();
        r3.roomId = 3;  r3.roomName = "Phòng 203 - Tòa B"; r3.building = "B";
        r3.capacity = 5; r3.currentOccupants = 3; r3.pricePerMonth = 500000;
        r3.roomType = "NAM"; r3.status = "AVAILABLE";
        rooms.add(r3);

        DormitoryRoomEntity r4 = new DormitoryRoomEntity();
        r4.roomId = 4;  r4.roomName = "Phòng 204 - Tòa B"; r4.building = "B";
        r4.capacity = 3; r4.currentOccupants = 1; r4.pricePerMonth = 250000;
        r4.roomType = "NU"; r4.status = "AVAILABLE";
        rooms.add(r4);

        DormitoryRoomEntity r5 = new DormitoryRoomEntity();
        r5.roomId = 5;  r5.roomName = "Phòng 205 - Tòa C"; r5.building = "C";
        r5.capacity = 8; r5.currentOccupants = 8; r5.pricePerMonth = 700000;
        r5.roomType = "NAM"; r5.status = "FULL";
        rooms.add(r5);

        DormitoryRoomEntity r6 = new DormitoryRoomEntity();
        r6.roomId = 6;  r6.roomName = "Phòng 206 - Tòa C"; r6.building = "C";
        r6.capacity = 4; r6.currentOccupants = 2; r6.pricePerMonth = 350000;
        r6.roomType = "NU"; r6.status = "AVAILABLE";
        rooms.add(r6);

        dao.insertRooms(rooms);
    }

    // ─────────────────────────────────────────────────────────
    // CALLBACK INTERFACES
    // ─────────────────────────────────────────────────────────

    public interface RoomsCallback {
        void onResult(List<DormitoryRoomEntity> rooms);
    }

    public interface RoomCallback {
        void onResult(DormitoryRoomEntity room, DormitoryException error);
    }

    public interface RegistrationCallback {
        void onResult(DormitoryRegistrationEntity registration, DormitoryException error);
    }

    public interface RegistrationsCallback {
        void onResult(List<DormitoryRegistrationEntity> registrations);
    }

    public interface SimpleCallback {
        void onDone(DormitoryException error);
    }

    // ─────────────────────────────────────────────────────────
    // API PHÒNG
    // ─────────────────────────────────────────────────────────

    /** Lấy tất cả phòng (chạy background, trả về UI thread) */
    public void getAllRooms(RoomsCallback callback) {
        executor.execute(() -> {
            List<DormitoryRoomEntity> rooms = dao.getAllRoomsSync();
            mainHandler.post(() -> callback.onResult(rooms));
        });
    }

    /**
     * Lọc phòng theo tòa, giá tối đa, loại.
     * Truyền "" hoặc null để bỏ qua điều kiện; 0 để bỏ qua giá.
     */
    public void filterRooms(String building, int maxPrice, String roomType, RoomsCallback callback) {
        executor.execute(() -> {
            if (maxPrice < 0) {
                mainHandler.post(() -> callback.onResult(new ArrayList<>()));
                return;
            }
            String b = building == null ? "" : building;
            String t = roomType == null ? "" : roomType;
            List<DormitoryRoomEntity> rooms = dao.filterRooms(b, maxPrice, t);
            mainHandler.post(() -> callback.onResult(rooms));
        });
    }

    /** Tìm phòng theo ID */
    public void findRoomById(long roomId, RoomCallback callback) {
        executor.execute(() -> {
            DormitoryRoomEntity room = dao.getRoomById(roomId);
            mainHandler.post(() -> {
                if (room == null) {
                    callback.onResult(null, new DormitoryException("Không tìm thấy phòng: " + roomId));
                } else {
                    callback.onResult(room, null);
                }
            });
        });
    }

    // ─────────────────────────────────────────────────────────
    // API ĐĂNG KÝ
    // ─────────────────────────────────────────────────────────

    /**
     * Tạo đăng ký phòng mới.
     * Kiểm tra phòng còn chỗ trước khi lưu vào database.
     *
     * @param roomId   ID phòng cần đăng ký
     * @param userId   ID sinh viên đang đăng nhập (lấy từ SessionManager)
     * @param months   Số tháng đăng ký (1–12)
     */
    public void registerRoom(long roomId, long userId, int months, RegistrationCallback callback) {
        executor.execute(() -> {
            if (months <= 0 || months > 12) {
                mainHandler.post(() -> callback.onResult(null,
                        new DormitoryException("Số tháng đăng ký phải từ 1 đến 12.")));
                return;
            }

            DormitoryRoomEntity room = dao.getRoomById(roomId);
            if (room == null) {
                mainHandler.post(() -> callback.onResult(null,
                        new DormitoryException("Không tìm thấy phòng: " + roomId)));
                return;
            }

            if (!"AVAILABLE".equals(room.status)) {
                mainHandler.post(() -> callback.onResult(null,
                        new DormitoryException("Phòng " + room.roomName + " đã hết chỗ!")));
                return;
            }

            // Tạo entity đăng ký
            DormitoryRegistrationEntity reg = new DormitoryRegistrationEntity();
            reg.roomId = roomId;
            reg.userId = userId;
            reg.months = months;
            reg.totalPrice = room.pricePerMonth * months;
            reg.status = "CONFIRMED";
            reg.createdAt = System.currentTimeMillis();

            long newId = dao.insertRegistration(reg);
            reg.registrationId = newId;

            mainHandler.post(() -> callback.onResult(reg, null));
        });
    }

    /**
     * Hủy đăng ký theo registration_id.
     */
    public void cancelRegistration(long regId, SimpleCallback callback) {
        executor.execute(() -> {
            DormitoryRegistrationEntity reg = dao.getRegistrationById(regId);
            if (reg == null) {
                mainHandler.post(() -> callback.onDone(
                        new DormitoryException("Không tìm thấy đăng ký: " + regId)));
                return;
            }
            dao.updateRegistrationStatus(regId, "CANCELLED");
            mainHandler.post(() -> callback.onDone(null));
        });
    }

    /**
     * Lấy đăng ký đang hoạt động của user (chưa hủy).
     */
    public void getActiveRegistration(long userId, RegistrationCallback callback) {
        executor.execute(() -> {
            DormitoryRegistrationEntity reg = dao.getActiveRegistrationSync(userId);
            mainHandler.post(() -> callback.onResult(reg, null));
        });
    }

    /**
     * Lấy toàn bộ lịch sử đăng ký của user.
     */
    public void getAllRegistrations(long userId, RegistrationsCallback callback) {
        executor.execute(() -> {
            List<DormitoryRegistrationEntity> list = dao.getAllRegistrationsSync(userId);
            mainHandler.post(() -> callback.onResult(list));
        });
    }
}