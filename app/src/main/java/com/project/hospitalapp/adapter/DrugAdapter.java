package com.project.hospitalapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.project.hospitalapp.R;
import com.project.hospitalapp.model.Drug;

import java.util.ArrayList;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.ViewHolder> {

    Context context;
    ArrayList<Drug> DrugList;

    public DrugAdapter(Context context, ArrayList<Drug> drugList) {
        this.context = context;
        DrugList = drugList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drug_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drug drug = DrugList.get(position);

        holder.drugNameTextView.setText(drug.itemNameText);
        holder.effectTextView.setText("[효능효과] " + drug.efcyQesitmText);

        // 썸네일 이미지 설정 using Glide
        if (!TextUtils.isEmpty(drug.itemImageUrl)) {
            Glide.with(context)
                    .load(drug.itemImageUrl)
                    .into(holder.thumbnailImageView);
        } else {
            // 기본 썸네일 이미지를 설정하거나 숨깁니다.
            // 예를 들어, holder.thumbnailImageView.setVisibility(View.GONE); 를 사용하여 숨길 수 있습니다.
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(drug);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return DrugList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Drug drug);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView drugNameTextView, effectTextView;
        ImageView thumbnailImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            drugNameTextView = itemView.findViewById(R.id.drugNameTextView);
            effectTextView= itemView.findViewById(R.id.effectTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
        }
    }
}