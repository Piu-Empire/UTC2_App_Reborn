package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.home.HomeFragment;
import com.utc2.appreborn.ui.profile.ProfileFragment;
import com.utc2.appreborn.ui.public_services.PublicServiceFragment;
import com.utc2.appreborn.ui.schedule.ScheduleFragment;
import com.utc2.appreborn.ui.tuition.TuitionFragment;
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    // Tags để quản lý Fragment, tránh tạo mới liên tục gây tốn RAM
    public static final String TAG_HOME     = "tag_home";
    public static final String TAG_SCHEDULE = "tag_schedule";
    public static final String TAG_SERVICES = "tag_services";
    public static final String TAG_TUITION  = "tag_tuition";
    public static final String TAG_PROFILE  = "tag_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLiquidBar();

        // Mặc định vào Home khi mở App
        if (savedInstanceState == null) {
            switchTab(HomeFragment.class, TAG_HOME);
        }
    }

    private void setupLiquidBar() {
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);
        LiquidBarKt.setupLiquidBottomBar(bottomBarCompose, navId -> {
            handleBottomNavSelection(navId);
            return null;
        });
    }

    private void handleBottomNavSelection(int navId) {
        if (navId == R.id.nav_home) {
            switchTab(HomeFragment.class, TAG_HOME);
        } else if (navId == R.id.nav_schedule) {
            switchTab(ScheduleFragment.class, TAG_SCHEDULE);
        } else if (navId == R.id.nav_register) {
            Intent intent = new Intent(this, com.utc2.appreborn.ui.courseregistration.CourseRegistrationActivity.class);
            startActivity(intent);
        } else if (navId == R.id.nav_result) {
            //switchTab(ResultFragment.class, TAG_RESULT);
        } else if (navId == R.id.nav_profile) {
            switchTab(ProfileFragment.class, TAG_PROFILE);
        } else {
            // Nếu null hoặc không khớp, quay về Home cho an toàn
            switchTab(HomeFragment.class, TAG_HOME);
        }
    }

    /**
     * Chuyển đổi giữa các Tab chính ở Bottom Bar.
     * Tự động dùng lại Fragment cũ nếu đã tồn tại, xóa Backstack để tránh lỗi điều hướng.
     */
    private <T extends Fragment> void switchTab(Class<T> fragmentClass, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment existing = fm.findFragmentByTag(tag);

        // Nếu đang ở chính trang đó rồi thì không làm gì cả
        if (existing != null && existing.isVisible()) return;

        try {
            Fragment target = (existing != null)
                    ? existing
                    : fragmentClass.getDeclaredConstructor().newInstance();

            // Xóa sạch lịch sử các trang chi tiết trước đó khi nhấn Tab mới
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, target, tag)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dùng khi muốn mở trang chi tiết (vd: Tin tức chi tiết) đè lên Tab hiện tại
     */
    public void pushFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}