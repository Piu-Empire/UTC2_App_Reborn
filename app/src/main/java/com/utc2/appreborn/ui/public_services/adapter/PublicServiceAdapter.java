package com.utc2.appreborn.ui.public_services.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueActivity;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportActivity;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.ui.public_services.ServiceDetailActivity;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationActivity;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptRegistrationActivity;
import com.utc2.appreborn.ui.public_services.model.TranscriptService;
import com.utc2.appreborn.ui.public_services.model.BaseService;

import java.io.Serializable;
import java.util.List;

public class PublicServiceAdapter extends RecyclerView.Adapter<PublicServiceAdapter.ViewHolder> {

    private final List<BaseService> serviceList;
    private final boolean isResultTab;
    private static final String TAG = "PublicServiceAdapter";

    public PublicServiceAdapter(List<BaseService> serviceList, boolean isResultTab) {
        this.serviceList = serviceList;
        this.isResultTab = isResultTab;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isResultTab ? R.layout.item_public_service_result : R.layout.item_public_service;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaseService item = serviceList.get(position);
        if (item == null) return;
        Context context = holder.itemView.getContext();

        try {
            // 1. Cập nhật Icon dựa trên loại dịch vụ
            if (holder.imgIcon != null) {
                if (item instanceof CardReissueService) {
                    holder.imgIcon.setImageResource(R.drawable.ic_id_card);
                } else if (item instanceof LoanSupportService) {
                    holder.imgIcon.setImageResource(R.drawable.ic_hand_coins);
                } else if (item instanceof TranscriptService) {
                    holder.imgIcon.setImageResource(R.drawable.ic_scroll_text);
                } else if (item instanceof StudentConfirmationService) {
                    holder.imgIcon.setImageResource(R.drawable.ic_id_card_lanyard);
                }
            }

            // 2. Gán dữ liệu cơ bản
            holder.txtTitle.setText(item.getTitle());
            holder.txtDate.setText(item.getDate());

            if (isResultTab) {
                // --- TAB KẾT QUẢ ---
                setupStatusView(holder, item, context);

                View.OnClickListener openDetail = v -> {
                    try {
                        Intent intent = new Intent(context, ServiceDetailActivity.class);
                        if (item instanceof Serializable) {
                            intent.putExtra("SERVICE_DATA", (Serializable) item);
                            context.startActivity(intent);
                        } else {
                            Log.e(TAG, "Dữ liệu không hỗ trợ Serializable!");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi mở chi tiết: " + e.getMessage());
                    }
                };

                if (holder.btnDetail != null) {
                    holder.btnDetail.setOnClickListener(openDetail);
                }
                holder.itemView.setOnClickListener(openDetail);

            } else {
                // --- TAB DANH SÁCH DỊCH VỤ ---
                holder.itemView.setOnClickListener(v -> {
                    try {
                        Intent intent = null;
                        if (item instanceof CardReissueService) {
                            intent = new Intent(context, CardReissueActivity.class);
                        } else if (item instanceof LoanSupportService) {
                            intent = new Intent(context, LoanSupportActivity.class);
                        } else if (item instanceof TranscriptService) {
                            intent = new Intent(context, TranscriptRegistrationActivity.class);
                        } else if (item instanceof StudentConfirmationService) {
                            intent = new Intent(context, StudentConfirmationActivity.class);
                        }

                        if (intent != null) context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi mở trang đăng ký: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi hiển thị tại vị trí: " + position, e);
        }
    }

    private void setupStatusView(ViewHolder holder, BaseService item, Context context) {
        if (holder.txtStatus != null) {
            if (item.getStatus() == 1) {
                holder.txtStatus.setText(R.string.status_approved);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green_success));
            } else {
                holder.txtStatus.setText(R.string.status_pending);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.text_muted_light));
            }
        }
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtStatus, btnDetail;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}