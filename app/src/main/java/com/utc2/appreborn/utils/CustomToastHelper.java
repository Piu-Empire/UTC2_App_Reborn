package com.utc2.appreborn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import android.graphics.Color;

import com.utc2.appreborn.R;

public class CustomToastHelper {

    public static void showToast(Context context, String message) {
        showCustomToast(context, message, false);
    }

    public static void showToast(Context context, int messageResId) {
        if (context != null) showCustomToast(context, context.getString(messageResId), false);
    }

    public static void showErrorToast(Context context, String message) {
        showCustomToast(context, message, true);
    }

    public static void showErrorToast(Context context, int messageResId) {
        if (context != null) showCustomToast(context, context.getString(messageResId), true);
    }
    
    public static void showSuccessToast(Context context, String message) {
        showCustomToast(context, message, false);
    }

    public static void showSuccessToast(Context context, int messageResId) {
        if (context != null) showCustomToast(context, context.getString(messageResId), false);
    }

    private static void showCustomToast(Context context, String message, boolean isError) {
        Activity activity = scanForActivity(context);
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(context, message);
            return;
        }

        final ViewGroup rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(context, message);
            return;
        }
        
        LayoutInflater inflater = LayoutInflater.from(activity);
        View toastView = inflater.inflate(R.layout.layout_custom_toast, rootView, false);
        
        TextView tvMsg = toastView.findViewById(R.id.toastText);
        tvMsg.setText(message);
        
        ImageView icon = toastView.findViewById(R.id.toastIcon);
        CardView cardView = (CardView) toastView;

        if (isError) {
            cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            tvMsg.setTextColor(Color.parseColor("#C62828"));
            icon.setImageResource(R.drawable.ic_warning_triangle);
            icon.setColorFilter(Color.parseColor("#E53935"));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            tvMsg.setTextColor(Color.parseColor("#2E7D32"));
            icon.setImageResource(R.drawable.ic_check);
            icon.setColorFilter(Color.parseColor("#4CAF50"));
        }
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.START;
        
        int bottomMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics());
        int startMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, activity.getResources().getDisplayMetrics());
        
        params.bottomMargin = bottomMarginPx;
        params.leftMargin = startMarginPx;
        params.setMarginStart(startMarginPx);
        
        toastView.setLayoutParams(params);
        rootView.addView(toastView);

        int displayWidth = activity.getResources().getDisplayMetrics().widthPixels;
        toastView.setTranslationX(-displayWidth);
        toastView.setAlpha(0f);
        
        toastView.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    toastView.postDelayed(() -> {
                        toastView.animate()
                                .translationX(displayWidth)
                                .alpha(0f)
                                .setDuration(500)
                                .setInterpolator(new AccelerateInterpolator())
                                .withEndAction(() -> rootView.removeView(toastView))
                                .start();
                    }, 2500);
                }).start();
    }

    private static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
