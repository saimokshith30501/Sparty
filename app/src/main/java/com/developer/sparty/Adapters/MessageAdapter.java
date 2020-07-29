package com.developer.sparty.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sparty.Models.Modelmessage;
import com.developer.sparty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
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
    public void onBindViewHolder(@NonNull final MessageAdapter.Myholder holder, final int position) {
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

         holder.Mlayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder builder=new AlertDialog.Builder(v.getRootView().getContext());
                 builder.setTitle("Delete");
                 builder.setMessage("Are you sure?");
                 builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                               deleteMessage(position);
                     }
                 });
                 builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                     }
                 });
                 builder.show();
             }
         });

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

    private void deleteMessage(int position) {
        String mStamp=chatList.get(position).getTimestamp();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        Query dMessage=databaseReference.orderByChild("timestamp").equalTo(mStamp);
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        dMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(user.getUid())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "Message Deleted...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context, "Message has been deleted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
        public int getItemCount () {
            return chatList.size();
        }

    public class Myholder extends RecyclerView.ViewHolder {

        TextView showMessage;
        TextView isSeenTv,timeTv;
        LinearLayout Mlayout;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            Mlayout=itemView.findViewById(R.id.MessageLayout);
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
