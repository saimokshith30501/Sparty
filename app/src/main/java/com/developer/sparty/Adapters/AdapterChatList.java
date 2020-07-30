package com.developer.sparty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sparty.Models.ModelChatList;
import com.developer.sparty.Models.ModelUser;
import com.developer.sparty.R;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {
    Context context;
    List<ModelUser> userList;
    private HashMap<String,Object> lastMessage;

    public AdapterChatList(Context context, List<ModelUser> userList, HashMap<String, Object> lastMessage) {
        this.context = context;
        this.userList = userList;
        this.lastMessage = lastMessage;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chat_list,parent,false);

    return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hUid=userList.get(position).getUid();

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv,onlineStatusIv;
        TextView UserName,LastMessage;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            profileIv=itemView.findViewById(R.id.profileIv);
            onlineStatusIv=itemView.findViewById(R.id.onlineStatusIv);
            UserName=itemView.findViewById(R.id.nameTv);
            LastMessage=itemView.findViewById(R.id.lastMessageTv);
        }
    }
}
