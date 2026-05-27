package com.utc2.appreborn.ui.tuition.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.model.Invoice;

import java.util.List;
import java.util.Locale;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private static final int VIEW_HEADER = 0;
    private static final int VIEW_ITEM   = 1;

    private final List<Object> items;

    public InvoiceAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? VIEW_HEADER : VIEW_ITEM;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == VIEW_HEADER
                ? R.layout.item_invoice_header
                : R.layout.item_invoice;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_HEADER) {
            if (holder.tvHeader != null) {
                holder.tvHeader.setText((String) items.get(position));
            }
        } else {
            Invoice item = (Invoice) items.get(position);

            // tvInvoiceID — hiển thị label (tên kỳ / tên phòng) + mã HD
            if (holder.tvInvoiceID != null) {
                holder.tvInvoiceID.setText(
                        holder.tvInvoiceID.getContext().getString(
                                R.string.invoice_id_format,
                                item.getLabel(),
                                item.getInvoiceCode()));
            }

            // tvInvoiceDate — ngày thanh toán
            if (holder.tvInvoiceDate != null) {
                String paidAt = item.getPaidAt();
                String display = (paidAt != null && !paidAt.isEmpty()) ? paidAt : "---";
                holder.tvInvoiceDate.setText(
                        holder.tvInvoiceDate.getContext().getString(
                                R.string.invoice_date_format, display));
            }

            // tvInvoiceAmount — tổng tiền
            if (holder.tvInvoiceAmount != null) {
                holder.tvInvoiceAmount.setText(
                        String.format(Locale.getDefault(), "%,.0f VND", item.getTotalAmount()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        TextView tvInvoiceID, tvInvoiceDate, tvInvoiceAmount;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_HEADER) {
                tvHeader = itemView.findViewById(R.id.tvSectionHeader);
            } else {
                tvInvoiceID     = itemView.findViewById(R.id.tvInvoiceID);
                tvInvoiceDate   = itemView.findViewById(R.id.tvInvoiceDate);
                tvInvoiceAmount = itemView.findViewById(R.id.tvInvoiceAmount);
            }
        }
    }
}