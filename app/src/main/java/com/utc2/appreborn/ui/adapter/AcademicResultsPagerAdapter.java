package com.utc2.appreborn.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.utc2.appreborn.ui.results.AcademicResultsFragment;
import com.utc2.appreborn.ui.results.grades.GradesFragment;
import com.utc2.appreborn.ui.results.leaderboard.LeaderboardFragment;
import com.utc2.appreborn.ui.results.scholarship.ScholarshipFragment;
import com.utc2.appreborn.ui.results.warning.WarningsFragment;
/**
 * AcademicResultsPagerAdapter
 *
 * FragmentStateAdapter quản lý 4 tab của module Kết quả học tập.
 * Tab index:
 *   0 → GradesFragment      (Xem điểm)
 *   1 → LeaderboardFragment  (Bảng xếp hạng)
 *   2 → ScholarshipFragment  (Học bổng)
 *   3 → WarningsFragment     (Cảnh báo học vụ)
 */
public class AcademicResultsPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 4;

    public AcademicResultsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new GradesFragment();
            case 1: return new LeaderboardFragment();
            case 2: return new ScholarshipFragment();
            case 3: return new WarningsFragment();
            default: return new GradesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
