package com.utc2.appreborn.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.courseregistration.CourseRegistrationFragment;
import com.utc2.appreborn.ui.home.HomeFragment;
import com.utc2.appreborn.ui.profile.ProfileFragment;
import com.utc2.appreborn.ui.public_services.PublicServiceFragment;
import com.utc2.appreborn.ui.results.AcademicResultsFragment;
import com.utc2.appreborn.ui.schedule.ScheduleFragment;
import com.utc2.appreborn.ui.tuition.TuitionFragment;

public class MainActivity extends AppCompatActivity {

    // Tags để quản lý Fragment, tránh tạo mới liên tục gây tốn RAM
    public static final String TAG_HOME     = "tag_home";
    public static final String TAG_SCHEDULE = "tag_schedule";
    public static final String TAG_SERVICES = "tag_services";
    public static final String TAG_TUITION  = "tag_tuition";
    public static final String TAG_PROFILE  = "tag_profile";
    public static final String TAG_RESULT   = "tag_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bật edge-to-edge để nội dung vẽ sau status bar (trong suốt)
        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ── Hiển thị Home mặc định ───────────────────────────

        if (savedInstanceState == null) {
            switchTab(HomeFragment.class, TAG_HOME);
        }

        // ── Setup Bottom Navigation ──────────────────────────

        setupLiquidBar();
    }
    // ─────────────────────────────────────────────────────────
    // Bottom Bar
    // ─────────────────────────────────────────────────────────

    private void setupLiquidBar() {

        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        LiquidBarKt.setupLiquidBottomBar(
                bottomBarCompose,
                id -> {
                    handleNavigation(id);
                    return null;
                }
        );
    }
    //─────────────────────────────────────────────────────
    // Navigation Handler
    // ─────────────────────────────────────────────────────────

    private void handleNavigation(int id) {


        if (id == R.id.nav_home) {

            switchTab(HomeFragment.class, TAG_HOME);

        } else if (id == R.id.nav_schedule) {

            switchTab(ScheduleFragment.class, TAG_SCHEDULE);

        } else if (id == R.id.nav_register) {

            switchTab(CourseRegistrationFragment.class, "tag_register");

        } else if (id == R.id.nav_result) {

            // Dashboard kết quả học tập
            switchTab(
                    AcademicResultsFragment.class,
                    TAG_RESULT
            );

        } else if (id == R.id.nav_profile) {

            switchTab(ProfileFragment.class, TAG_PROFILE);

        } else {

            // Fallback về Home
            switchTab(HomeFragment.class, TAG_HOME);
        }
    }
    // ─────────────────────────────────────────────────────────
    // Switch Main Tabs
    // ─────────────────────────────────────────────────────────

    private <T extends Fragment> void switchTab(
            Class<T> fragmentClass,
            String tag
    ) {

        FragmentManager fm = getSupportFragmentManager();

        Fragment existing = fm.findFragmentByTag(tag);

        // Nếu fragment đang hiển thị thì không reload lại
        if (existing != null && existing.isVisible()) {
            return;
        }

        try {

            Fragment target = (existing != null)
                    ? existing
                    : fragmentClass.getDeclaredConstructor().newInstance();

            // Xóa backstack khi đổi tab
            fm.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );

            fm.beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    )
                    .replace(R.id.fragment_container, target, tag)
                    .commit();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    // Push Detail Fragment
    // ─────────────────────────────────────────────────────────

    public void pushFragment(Fragment fragment, String tag) {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    /**
     * Alias cho AcademicResultsFragment gọi.
     */
    public void navigateTo(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
