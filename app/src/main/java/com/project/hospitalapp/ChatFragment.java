package com.project.hospitalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.hospitalapp.adapter.ChatAdapter;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    Chat chat;

    EditText editChat;
    ImageView imgSend;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chat"); // todo: chat_email 경로 만들어서 그 안에 저장되도록 하기.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        editChat = rootView.findViewById(R.id.editChat);
        imgSend = rootView.findViewById(R.id.imgSend);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String email = sp.getString(Config.USER_EMAIL, "");

        String subPath = email.substring(0, email.indexOf("@")) + "_" + "Chat";

        DatabaseReference chatRef = ref.child(subPath);


        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showProgress();

                // 데이터를 가져온 후에 실행되는 콜백

                chatArrayList.clear();

                adapter = new ChatAdapter(getActivity(), chatArrayList);

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    chat = messageSnapshot.getValue(Chat.class);

                    chatArrayList.add(chat);

                }

                if(!chatArrayList.isEmpty()) {
                    Collections.sort(chatArrayList, new Comparator<Chat>() {
                        @Override
                        public int compare(Chat chat1, Chat chat2) {
                            return Integer.compare(chat1.id, chat2.id);
                        }
                    });
                }

                chatArrayList.add(new Chat());

                dismissProgress();
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(chatArrayList.size() - 1);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgress();

                // 데이터 읽기가 취소되었을 때 호출되는 콜백
                Log.i("TEST", "loadMessages:onCancelled", databaseError.toException());
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editChat.getText().toString().trim();

                if(question.isEmpty()){

                    Snackbar.make(imgSend,
                            "메세지를 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String requestUrl = "/chat?" + "question=" + question;

                if (chatArrayList.size() >= 3) {
                    requestUrl += "&question-list=";
                    requestUrl += "질문1: " + chatArrayList.get(chatArrayList.size()-3).question + ", ";
                    requestUrl += "질문2: " + chatArrayList.get(chatArrayList.size()-2).question;
                } else if(chatArrayList.size() == 2) {
                    requestUrl += "&question-list=";
                    requestUrl += chatArrayList.get(chatArrayList.size()-2).question;
                }

                Log.i("TEST_URL", requestUrl);

                RequestQueue queue = Volley.newRequestQueue(getActivity());

                showProgress();

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET,
                        Config.HOST + requestUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgress();

                                try {
                                    if (response.getString("result").equals("success")) {

                                        Log.i("TEST", "api 호출 성공?");

                                        String answer = response.getJSONObject("answer").getString("content");

                                        chat = new Chat(chatArrayList.size(), question, answer);

                                        chatArrayList.add(chatArrayList.size() - 1, chat);

                                        chatRef.child("chat" + (chatArrayList.size() - 1)).setValue(chat);

                                        adapter.notifyDataSetChanged();

                                        recyclerView.scrollToPosition(chatArrayList.size() - 1); // 상황에 따라 수정하기.
                                        // -2는 질문 바로 위로 스크롤, -1은 스크롤 맨 밑으로.

                                        editChat.setText("");

                                    }
                                } catch (JSONException e) {

                                    Log.e("TEST", "파싱에러");
                                    Log.e("TEST", e.toString());
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissProgress();

                                Log.e("TEST", "서버에러");
                                Log.e("TEST", error.toString());
                            }
                        }
                );

                request.setRetryPolicy(new DefaultRetryPolicy(
                        40000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));


                queue.add(request);

            }
        });


        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(chatArrayList.isEmpty()){
                    return;
                }

                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Scroll the RecyclerView to the last item
                            if (chatArrayList.size() > 1) {
                                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 0);
                }
            }
        });


        return rootView;
    }

    Dialog dialog;

    void showProgress(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void dismissProgress(){
        dialog.dismiss();
    }
}