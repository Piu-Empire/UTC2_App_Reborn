package com.utc2.appreborn.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.ScheduleWithCourse;
import com.utc2.appreborn.data.local.entity.SemesterEntity;

import java.util.List;

@Dao
public interface ScheduleDao {

    // Lấy toàn bộ lịch kèm tên môn — dùng cho hiển thị UI
    @Transaction
    @Query("SELECT * FROM schedule")
    List<ScheduleWithCourse> getAllScheduleWithCourse();

    // Lấy lịch theo học kỳ
    @Transaction
    @Query("SELECT * FROM schedule WHERE semesterId = :semesterId")
    List<ScheduleWithCourse> getScheduleBySemester(long semesterId);

    // Lấy lịch theo thứ — dùng cho màn hình tuần
    @Transaction
    @Query("SELECT * FROM schedule WHERE day_of_week = :day AND semesterId = :semesterId")
    List<ScheduleWithCourse> getScheduleByDay(int day, long semesterId);

    // Xóa toàn bộ rồi insert mới (khi server trả về data mới)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSchedules(List<ScheduleEntity> schedules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourses(List<CourseEntity> courses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSemesters(List<SemesterEntity> semesters);

    @Query("DELETE FROM schedule")
    void deleteAllSchedules();
}