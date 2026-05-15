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

    private List<Invoice> invoiceList;

    public InvoiceAdapter(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invoice item = invoiceList.get(position);

        // Mã hóa đơn
        holder.tvID.setText("Mã HD: " + item.getInvoiceCode());

        // Ngày thanh toán — paidAt từ API là ISO String "yyyy-MM-dd'T'HH:mm:ss"
        // hiển thị nguyên hoặc format lại nếu cần
        String paidAt = item.getPaidAt();
        holder.tvDate.setText("Ngày: " + (paidAt != null && !paidAt.isEmpty() ? paidAt : "---"));

        // FIX NPE: dùng getTotalAmount() — null-safe, không gọi getTuition().getAmount()
        double amount = item.getTotalAmount();
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", amount));
    }

    @Override
    public int getItemCount() {
        return invoiceList != null ? invoiceList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvID, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID     = itemView.findViewById(R.id.tvInvoiceID);
            tvDate   = itemView.findViewById(R.id.tvInvoiceDate);
            tvAmount = itemView.findViewById(R.id.tvInvoiceAmount);
        }
    }
}