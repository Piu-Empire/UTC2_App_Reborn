package com.utc2.appreborn.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.databinding.ItemNewsBinding;
import com.utc2.appreborn.ui.home.model.NewsItem;

/**
 * NewsAdapter
 * ──────────────────────────────────────────────────────────────
 * Updated: listener now receives the full {@link NewsItem}
 * (which includes the HTML `content` field) so HomeFragment
 * can forward it to NewsDetailActivity via Intent.
 *
 * Package: com.utc2.appreborn.ui.home.adapter
 */
public class NewsAdapter extends ListAdapter<NewsItem, NewsAdapter.ViewHolder> {

    // ── Click listener ────────────────────────────────────────
    public interface OnNewsClickListener {
        /** Called with the full item — including content HTML. */
        void onNewsClick(NewsItem item);
    }

    private final OnNewsClickListener listener;

    // ── DiffUtil ──────────────────────────────────────────────
    private static final DiffUtil.ItemCallback<NewsItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NewsItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull NewsItem o, @NonNull NewsItem n) {
                    return o.getId().equals(n.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull NewsItem o, @NonNull NewsItem n) {
                    return o.getTitle().equals(n.getTitle())
                            && o.getDate().equals(n.getDate())
                            && o.getSummary().equals(n.getSummary());
                }
            };

    public NewsAdapter(@NonNull OnNewsClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // ── Adapter overrides ─────────────────────────────────────

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // ── ViewHolder ────────────────────────────────────────────

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNewsBinding b;

        ViewHolder(@NonNull ItemNewsBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(@NonNull NewsItem item) {
            b.tvNewsTitle.setText(item.getTitle());
            b.tvNewsDate.setText(item.getDate());
            // Pass the full item (including content HTML) to the listener
            b.getRoot().setOnClickListener(v -> listener.onNewsClick(item));
        }
    }
}