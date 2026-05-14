package com.utc2.appreborn.data.repository;

import com.utc2.appreborn.model.ScheduleItem;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository {

    // Trả về danh sách mock data.
    // Sau này nối API thật, bạn chỉ cần đổi logic bên trong hàm này thành gọi Retrofit là xong!
    public static List<ScheduleItem> getMockScheduleData() {
        List<ScheduleItem> list = new ArrayList<>();

        list.add(new ScheduleItem("QLY17.2", "Kỹ năng mềm", "LÝ THUYẾT", "ThS. Nguyễn Văn A", 0, 1, 3, 0, "07:30", "10:00", "20/01/2026", "25/05/2026", "A101", "Tòa A"));
        return list;
    }
}