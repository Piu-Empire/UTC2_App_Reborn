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

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    private List<Invoice> invoiceList;

    public InvoiceAdapter(List<Invoice> invoiceList) { this.invoiceList = invoiceList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invoice item = invoiceList.get(position);

        // Hiển thị mã hóa đơn
        holder.tvID.setText("Mã HD: " + item.getInvoiceID());

        // Hiển thị ngày
        holder.tvDate.setText("Ngày: " + item.getDate());

        // Lấy số tiền từ đối tượng Tuition bên trong Invoice
        // Sau này khi có MySQL, bạn có thể format số này thành "2.500.000 VND"
        long amount = item.getTuition().getAmount();
        holder.tvAmount.setText(String.format("%,d VND", amount));
    }

    @Override
    public int getItemCount() { return invoiceList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvID, tvDate, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvInvoiceID);
            tvDate = itemView.findViewById(R.id.tvInvoiceDate);
            tvAmount = itemView.findViewById(R.id.tvInvoiceAmount);
        }
    }
}