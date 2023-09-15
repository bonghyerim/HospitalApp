package com.project.hospitalapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.hospitalapp.R;
import com.project.hospitalapp.model.Chat;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    Context context;
    ArrayList<Chat> chatArrayList;

    public ChatAdapter(Context context, ArrayList<Chat> chatArrayList) {
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = chatArrayList.get(position);

        if(position == chatArrayList.size() - 1){
            holder.txtQuestion.setVisibility(View.GONE);
            holder.txtAnswer.setVisibility(View.GONE);
            holder.imgAIProfile.setVisibility(View.GONE);

        } else {
            holder.txtQuestion.setVisibility(View.VISIBLE);
            holder.txtAnswer.setVisibility(View.VISIBLE);
            holder.imgAIProfile.setVisibility(View.VISIBLE);

            holder.txtQuestion.setText(chat.question);
            holder.txtAnswer.setText(chat.answer);

        }
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtQuestion;
        TextView txtAnswer;
        ImageView imgAIProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            imgAIProfile = itemView.findViewById(R.id.imgAIProfile);
        }

    }

}
