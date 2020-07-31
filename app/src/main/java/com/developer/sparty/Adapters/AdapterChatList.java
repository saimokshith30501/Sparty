package com.developer.sparty.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sparty.ChatActivity;
import com.developer.sparty.Models.ModelChatList;
import com.developer.sparty.Models.ModelUser;
import com.developer.sparty.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {
    Context context;
    List<ModelUser> userList;
    private HashMap<String,String> lastMessageMap;
    private HashMap<String,String> lastMessageTime;

    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        lastMessageTime=new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chat_list,parent,false);

    return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid=userList.get(position).getUid();
        String userImage=userList.get(position).getImage();
        String Fname=userList.get(position).getFullname();
        String LMessage=lastMessageMap.get(hisUid);
        String LMessageTime=lastMessageTime.get(hisUid);
        holder.UserName.setText(Fname);
        if (LMessage==null||LMessage.equals("default")) {
            holder.LastMessage.setVisibility(View.GONE);
            holder.timeTv.setVisibility(View.GONE);
        }
        else {
            holder.LastMessage.setVisibility(View.VISIBLE);
            holder.timeTv.setVisibility(View.VISIBLE);
            holder.LastMessage.setText(LMessage);
            holder.timeTv.setText(LMessageTime);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.default_profile).into(holder.profileIv);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.default_profile).into(holder.profileIv);
        }
        if (userList.get(position).getOnlineStatus().equals("Online")){
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }
        else {
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cActivity=new Intent(context, ChatActivity.class);
                cActivity.putExtra("UID",hisUid);
                context.startActivity(cActivity);
            }
        });

    }
     public void setLastMessage(String userId,String lastMessage){
        lastMessageMap.put(userId,lastMessage);
     }
    public void setLastMessageTime(String userId,String lt){
        lastMessageTime.put(userId,lt);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv,onlineStatusIv;
        TextView UserName,LastMessage,timeTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            profileIv=itemView.findViewById(R.id.profileIv);
            onlineStatusIv=itemView.findViewById(R.id.onlineStatusIv);
            UserName=itemView.findViewById(R.id.nameTv);
            LastMessage=itemView.findViewById(R.id.lastMessageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
        }
    }
}
