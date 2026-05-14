package com.utc2.appreborn.data.local;

import android.content.Context;

import com.utc2.appreborn.data.local.dao.ScheduleDao;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.ScheduleWithCourse;
import com.utc2.appreborn.data.local.entity.SemesterEntity;

import java.util.List;

public class ScheduleLocalDataSource {

    private final ScheduleDao scheduleDao;

    public ScheduleLocalDataSource(Context context) {
        this.scheduleDao = AppDatabase.getInstance(context).scheduleDao();
    }

    // Đọc — gọi trên background thread
    public List<ScheduleWithCourse> getAll() {
        return scheduleDao.getAllScheduleWithCourse();
    }

    public List<ScheduleWithCourse> getBySemester(long semesterId) {
        return scheduleDao.getScheduleBySemester(semesterId);
    }

    public List<ScheduleWithCourse> getByDay(int day, long semesterId) {
        return scheduleDao.getScheduleByDay(day, semesterId);
    }

    // Ghi — gọi sau khi nhận data từ API
    public void saveAll(List<SemesterEntity> semesters,
                        List<CourseEntity> courses,
                        List<ScheduleEntity> schedules) {
        // Thứ tự INSERT: semester & course trước, schedule sau
        scheduleDao.insertSemesters(semesters);
        scheduleDao.insertCourses(courses);
        scheduleDao.insertSchedules(schedules);
    }

    public void clearSchedules() {
        scheduleDao.deleteAllSchedules();
    }
}