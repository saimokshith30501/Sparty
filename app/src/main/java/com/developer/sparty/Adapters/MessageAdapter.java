package com.developer.sparty.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sparty.Models.Modelmessage;
import com.developer.sparty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Myholder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser firebaseUser;
    Context context;
    List<Modelmessage> chatList;

    public MessageAdapter(Context context, List<Modelmessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MessageAdapter.Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.Myholder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.Myholder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Myholder holder, int position) {
        final String message = chatList.get(position).getMessage();
        final String timeStamp = chatList.get(position).getTimestamp();
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        holder.showMessage.setText(message);
        holder.timeTv.setText(dateTime);
        if (position==chatList.size()-1){
            if (chatList.get(position).isSeen()) {
                holder.isSeenTv.setText("Seen");
            }else {
                holder.isSeenTv.setText("Delivered");
            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Intent chatting=new  Intent(context, ChatActivity.class);
//        chatting.putExtra("UID",userUID);
//        chatting.putExtra("NAME",userName);
//        chatting.putExtra("IMAGE",userImage);
//        context.startActivity(chatting);
//        }
//        });
//        }
    }

        @Override
        public int getItemCount () {
            return chatList.size();
        }

    public class Myholder extends RecyclerView.ViewHolder {

        TextView showMessage;
        TextView isSeenTv,timeTv;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            isSeenTv=itemView.findViewById(R.id.isSeen);
            timeTv=itemView.findViewById(R.id.timeTv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
