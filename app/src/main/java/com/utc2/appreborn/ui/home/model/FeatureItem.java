package com.utc2.appreborn.ui.home.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * FeatureItem
 * ──────────────────────────────────────────────────────────────
 * Dùng @StringRes thay vì String title để locale tự đổi
 * khi người dùng chuyển ngôn ngữ trong Settings.
 *
 * FeatureAdapter sẽ gọi context.getString(item.getTitleRes())
 * — Android tự chọn values-en/strings.xml hay values/strings.xml
 * tuỳ theo locale hiện tại.
 */
public class FeatureItem {

    private final String id;

    @DrawableRes
    private final int iconRes;

    @StringRes
    private final int titleRes; // R.string.xxx — KHÔNG dùng String cứng

    public FeatureItem(String id, @DrawableRes int iconRes, @StringRes int titleRes) {
        this.id       = id;
        this.iconRes  = iconRes;
        this.titleRes = titleRes;
    }

    public String getId()       { return id; }
    public int    getIconRes()  { return iconRes; }

    /** @StringRes ID — adapter dùng context.getString(item.getTitleRes()) */
    public int    getTitleRes() { return titleRes; }
}