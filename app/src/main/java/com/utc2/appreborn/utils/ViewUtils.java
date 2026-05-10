package com.utc2.appreborn.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    // giữ giá trị nằm trong khoảng giới hạn cho phép
    private static int clamp(int value, int min, int max) {
        // kiểm tra hằng số đặc biệt của hệ thống trước khi chặn
        if (value == ViewGroup.LayoutParams.MATCH_PARENT ||
                value == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return value;
        }

        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // truy xuất thuộc tính bố cục của một thành phần giao diện
    private static ViewGroup.LayoutParams getParams(View view) {
        if (view == null) {
            DebugUtils.log(null, "getParams", "View is null!");
            return null;
        }
        return view.getLayoutParams();
    }

    // thiết lập chiều ngang cho thành phần theo đơn vị điểm ảnh
    public static void setWidth(View view, int width) {
        setWidth(view, width, 0, Integer.MAX_VALUE);
    }

    // thiết lập chiều ngang kèm theo các giới hạn kích thước
    public static void setWidth(View view, int width, int min, int max) {
        ViewGroup.LayoutParams params = getParams(view);
        if (params == null) return;

        params.width = clamp(width, min, max);
        view.setLayoutParams(params);
        DebugUtils.log(view, "setWidth", "width=" + params.width + "px");
    }

    // thiết lập chiều cao cho thành phần theo đơn vị điểm ảnh
    public static void setHeight(View view, int height) {
        setHeight(view, height, 0, Integer.MAX_VALUE);
    }

    // thiết lập chiều cao kèm theo các giới hạn kích thước
    public static void setHeight(View view, int height, int min, int max) {
        ViewGroup.LayoutParams params = getParams(view);
        if (params == null) return;

        params.height = clamp(height, min, max);
        view.setLayoutParams(params);
        DebugUtils.log(view, "setHeight", "height=" + params.height + "px");
    }

    // thiết lập đồng thời cả chiều ngang và chiều cao
    public static void setSize(View view, int width, int height) {
        setSize(view, width, height, 0, Integer.MAX_VALUE);
    }

    // thiết lập kích thước đồng thời áp dụng các giới hạn
    public static void setSize(View view, int width, int height, int min, int max) {
        ViewGroup.LayoutParams params = getParams(view);
        if (params == null) return;

        params.width = clamp(width, min, max);
        params.height = clamp(height, min, max);
        view.setLayoutParams(params);
        DebugUtils.log(view, "setSize", "w=" + params.width + "px, h=" + params.height + "px");
    }

    // tự động chuyển đổi đơn vị điểm và đặt chiều ngang
    public static void setWidthDp(Context context, View view, float dp) {
        DebugUtils.log(view, "setWidthDp", dp + "dp");
        setWidth(view, dpToPx(context, dp));
    }

    // tự động chuyển đổi đơn vị điểm và đặt chiều cao
    public static void setHeightDp(Context context, View view, float dp) {
        DebugUtils.log(view, "setHeightDp", dp + "dp");
        setHeight(view, dpToPx(context, dp));
    }

    // thiết lập kích thước từ đơn vị điểm sang điểm ảnh
    public static void setSizeDp(Context context, View view, float wDp, float hDp) {
        DebugUtils.log(view, "setSizeDp", "w=" + wDp + "dp, h=" + hDp + "dp");
        setSize(view, dpToPx(context, wDp), dpToPx(context, hDp));
    }

    // thiết lập khoảng cách bao quanh cho thành phần giao diện
    public static void setMargin(View view, int left, int top, int right, int bottom) {
        setMargin(view, left, top, right, bottom, 0, Integer.MAX_VALUE);
    }

    // thiết lập lề kèm theo các ràng buộc giá trị
    public static void setMargin(View view, int left, int top, int right, int bottom,
                                 int min, int max) {

        if (view == null) return;
        // xác định kiểu dữ liệu lề trước khi ép kiểu nạp
        if (!(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            DebugUtils.log(view, "setMargin", "Params is not MarginLayoutParams");
            return;
        }

        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        params.setMargins(
                clamp(left, min, max),
                clamp(top, min, max),
                clamp(right, min, max),
                clamp(bottom, min, max)
        );

        view.setLayoutParams(params);
        DebugUtils.log(view, "setMargin", String.format("L:%d, T:%d, R:%d, B:%d", left, top, right, bottom));
    }

    // chuyển đổi đơn vị điểm và thiết lập các lề ngoài
    public static void setMarginDp(Context context, View view,
                                   float left, float top, float right, float bottom) {
        DebugUtils.log(view, "setMarginDp", "Converting margins to PX");
        setMargin(view,
                dpToPx(context, left),
                dpToPx(context, top),
                dpToPx(context, right),
                dpToPx(context, bottom)
        );
    }

    // thiết lập khoảng đệm nội dung cho thành phần giao diện
    public static void setPadding(View view, int left, int top, int right, int bottom) {
        setPadding(view, left, top, right, bottom, 0, Integer.MAX_VALUE);
    }

    // thiết lập khoảng đệm nội dung kèm giới hạn chặn
    public static void setPadding(View view, int left, int top, int right, int bottom,
                                  int min, int max) {
        if (view == null) return;

        view.setPadding(
                clamp(left, min, max),
                clamp(top, min, max),
                clamp(right, min, max),
                clamp(bottom, min, max)
        );
        DebugUtils.log(view, "setPadding", String.format("L:%d, T:%d, R:%d, B:%d", left, top, right, bottom));
    }

    // chuyển đổi đơn vị điểm và đặt các khoảng đệm trong
    public static void setPaddingDp(Context context, View view,
                                    float left, float top, float right, float bottom) {
        DebugUtils.log(view, "setPaddingDp", "Converting padding to PX");
        setPadding(view,
                dpToPx(context, left),
                dpToPx(context, top),
                dpToPx(context, right),
                dpToPx(context, bottom)
        );
    }

    // thay đổi trạng thái để thành phần hiện lên màn hình
    public static void show(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            DebugUtils.log(view, "show", "VISIBLE");
        }
    }

    // xóa bỏ thành phần khỏi màn hình và không chiếm chỗ
    public static void hide(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
            DebugUtils.log(view, "hide", "GONE");
        }
    }

    // làm mờ thành phần nhưng vẫn giữ vị trí bố cục
    public static void invisible(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
            DebugUtils.log(view, "invisible", "INVISIBLE");
        }
    }

    // kiểm tra thành phần có đang hiện diện hay không
    public static boolean isVisible(View view) {
        boolean res = view != null && view.getVisibility() == View.VISIBLE;
        DebugUtils.log(view, "isVisible", String.valueOf(res));
        return res;
    }

    // xác định thành phần có đang bị ẩn hoàn toàn không
    public static boolean isGone(View view) {
        boolean res = view != null && view.getVisibility() == View.GONE;
        DebugUtils.log(view, "isGone", String.valueOf(res));
        return res;
    }

    // kiểm tra thành phần có đang ở trạng thái vô hình
    public static boolean isInvisible(View view) {
        boolean res = view != null && view.getVisibility() == View.INVISIBLE;
        DebugUtils.log(view, "isInvisible", String.valueOf(res));
        return res;
    }

    // tính toán chuyển đổi đơn vị điểm sang điểm ảnh thực
    public static int dpToPx(Context context, float dp) {
        if (context == null) return (int) dp;
        // sử dụng hệ số mật độ màn hình để tính toán chuẩn
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    // chuyển đổi từ số lượng điểm ảnh sang đơn vị điểm
    public static float pxToDp(Context context, float px) {
        if (context == null) return px;
        return px / context.getResources().getDisplayMetrics().density;
    }

    // cập nhật lại toàn bộ thông số bố cục của thành phần
    public static void updateLayout(View view, ViewGroup.LayoutParams params) {
        if (view == null || params == null) {
            DebugUtils.log(view, "updateLayout", "View or Params is null!");
            return;
        }
        view.setLayoutParams(params);
        DebugUtils.log(view, "updateLayout", "LayoutParams updated");
    }
}