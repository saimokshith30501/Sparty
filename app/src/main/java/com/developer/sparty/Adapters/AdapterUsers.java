package com.developer.sparty.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.sparty.ChatActivity;
import com.developer.sparty.Models.ModelUser;
import com.developer.sparty.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.Myholder> {
    Context context;
    List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);

        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        final String userUID=userList.get(position).getUid();
       final String userImage=userList.get(position).getImage();
       final String userName=userList.get(position).getUsername();
       String userPhone=userList.get(position).getPhone();

       //
        holder.Name.setText(userName);
        holder.Phone.setText(userPhone);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.default_profile).into(holder.Image);
        }
        catch(Exception e) {
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatting=new Intent(context, ChatActivity.class);
                chatting.putExtra("UID",userUID);
                chatting.putExtra("NAME",userName);
                chatting.putExtra("IMAGE",userImage);
                context.startActivity(chatting);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class  Myholder extends RecyclerView.ViewHolder{

        ImageView Image;
        TextView Name;
        TextView Phone;
        public Myholder(@NonNull View itemView) {
            super(itemView);

            Image=itemView.findViewById(R.id.search_user_image);
            Name=itemView.findViewById(R.id.search_user_name);
            Phone=itemView.findViewById(R.id.search_user_phone);

        }
    }

}
