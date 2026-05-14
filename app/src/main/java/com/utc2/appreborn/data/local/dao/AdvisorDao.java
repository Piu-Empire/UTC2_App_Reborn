// PATH: app/src/main/java/com/utc2/appreborn/data/local/dao/AdvisorDao.java

package com.utc2.appreborn.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utc2.appreborn.data.local.entity.AdvisorEntity;

import java.util.List;

@Dao
public interface AdvisorDao {

    // ─── INSERT ───────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAdvisor(AdvisorEntity advisor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAdvisors(List<AdvisorEntity> advisors);

    // ─── QUERY BY ID ──────────────────────────────────────────────────────────

    @Query("SELECT * FROM advisor WHERE advisor_id = :advisorId LIMIT 1")
    AdvisorEntity getAdvisorById(long advisorId);

    @Query("SELECT * FROM advisor WHERE email = :email LIMIT 1")
    AdvisorEntity getAdvisorByEmail(String email);

    @Query("SELECT * FROM advisor WHERE faculty = :faculty")
    List<AdvisorEntity> getAdvisorsByFaculty(String faculty);

    @Query("SELECT * FROM advisor")
    List<AdvisorEntity> getAllAdvisors();

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Query("DELETE FROM advisor WHERE advisor_id = :advisorId")
    void deleteAdvisorById(long advisorId);
}