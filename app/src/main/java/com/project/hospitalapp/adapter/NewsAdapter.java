package com.project.hospitalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.hospitalapp.NewsActivity;
import com.project.hospitalapp.R;
import com.project.hospitalapp.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    Context context;
    ArrayList<NewsItem> newsItemArrayList;

    public NewsAdapter(Context context, List<NewsItem> newsItemArrayList) {
        this.context = context;
        this.newsItemArrayList = new ArrayList<>(newsItemArrayList);
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_row,parent,false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem newsItem = newsItemArrayList.get(position);
        holder.titleTextView.setText(newsItem.getTitle());
        holder.summaryTextView.setText(newsItem.getSummary());
        Glide.with(context)
                .load(newsItem.getImg()) // 이미지 URL
                .apply(new RequestOptions()
                        .placeholder(R.drawable.outline_image_24) // 로딩 중에 표시할 이미지
                        ) // 로딩 실패 시 표시할 이미지
                .into(holder.newsImage);
    }
    @Override
    public int getItemCount() {
        return newsItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, summaryTextView;
        ImageView newsImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            newsImage = itemView.findViewById(R.id.newsImage);

            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NewsActivity.class);
                    intent.putExtra("newsItem", newsItemArrayList.get(getAdapterPosition()));
                    intent.putExtra("url", newsItemArrayList.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);
                }
            });
        }
    }
}
