package com.utc2.appreborn.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.ItemSearchResultBinding;

/**
 * SearchResultAdapter
 * ──────────────────────────────────────────────────────────────
 * Hiển thị danh sách kết quả tìm kiếm trong RecyclerView.
 *
 * Hai loại item:
 *   • FEATURE — icon tính năng + tên + mô tả ngắn
 *   • NEWS    — icon tin tức + tiêu đề + ngày đăng
 *
 * Dùng ListAdapter + DiffUtil để cập nhật list hiệu quả.
 *
 * Package: com.utc2.appreborn.ui.search
 */
public class SearchResultAdapter
        extends ListAdapter<SearchResult, SearchResultAdapter.ViewHolder> {

    // ── Listener ──────────────────────────────────────────────
    public interface OnResultClickListener {
        void onResultClick(SearchResult item);
    }

    private final OnResultClickListener listener;

    // ── DiffUtil ──────────────────────────────────────────────
    private static final DiffUtil.ItemCallback<SearchResult> DIFF =
            new DiffUtil.ItemCallback<SearchResult>() {
                @Override
                public boolean areItemsTheSame(@NonNull SearchResult a, @NonNull SearchResult b) {
                    // Dùng title + type làm identity
                    return a.getType() == b.getType()
                            && a.getTitle().equals(b.getTitle());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SearchResult a, @NonNull SearchResult b) {
                    return a.getTitle().equals(b.getTitle())
                            && a.getSubtitle().equals(b.getSubtitle());
                }
            };

    // ── Constructor ───────────────────────────────────────────
    public SearchResultAdapter(@NonNull OnResultClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    // ═══════════════════════════════════════════════════════════
    //  Adapter overrides
    // ═══════════════════════════════════════════════════════════

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchResultBinding b = ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // ═══════════════════════════════════════════════════════════
    //  ViewHolder
    // ═══════════════════════════════════════════════════════════

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchResultBinding b;

        ViewHolder(@NonNull ItemSearchResultBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(@NonNull SearchResult item) {
            b.tvResultTitle.setText(item.getTitle());
            b.tvResultSubtitle.setText(item.getSubtitle());

            // Icon: tính năng dùng icon riêng, tin tức dùng ic_news chung
            if (item.getType() == SearchResult.Type.FEATURE && item.getIconRes() != 0) {
                b.ivResultIcon.setImageResource(item.getIconRes());
            } else {
                b.ivResultIcon.setImageResource(R.drawable.ic_notification); // icon tin tức
            }

            // Badge loại kết quả
            if (item.getType() == SearchResult.Type.FEATURE) {
                b.tvResultBadge.setText("Tính năng");
                b.tvResultBadge.setVisibility(View.VISIBLE);
            } else {
                b.tvResultBadge.setText("Tin tức");
                b.tvResultBadge.setVisibility(View.VISIBLE);
            }

            b.getRoot().setOnClickListener(v -> listener.onResultClick(item));
        }
    }
}