package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.sparty.Adapters.MessageAdapter;
import com.developer.sparty.Models.Modelmessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    EditText Message;
    TextView Cname,Cstatus;
    ImageButton SendMessage;
    ImageView Cpic;
    RecyclerView recyclerView;
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference,databaseReference;
    String UserUid;
    String MyUid;
    String uName;
    String image;
    Animation sendB;
    String oStatus;
    MessageAdapter messageAdapter;
    List<Modelmessage> chatList;
    //for checking user has seen msg or not
    ValueEventListener valueEventListener;
    DatabaseReference userRefForSeen;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Message=findViewById(R.id.chat_message);
        Cname=findViewById(R.id.chat_user_name);
        Cstatus=findViewById(R.id.chat_user_status);
        SendMessage=findViewById(R.id.chat_sendbutton);
        Cpic=findViewById(R.id.chat_user_image);
        recyclerView=findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user=firebaseAuth.getCurrentUser();
        sendB= AnimationUtils.loadAnimation(this,R.anim.send_button_anim);
        MyUid=user.getUid();
        UserUid=getIntent().getStringExtra("UID");


        databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        Query uQuery=databaseReference.orderByChild("uid").equalTo(UserUid);
        uQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    uName=""+ds.child("fullname").getValue().toString();
                    image=""+ds.child("image").getValue().toString();
                    oStatus=""+ds.child("onlineStatus").getValue().toString();

                    if (oStatus.equals("Online")){
                        Cstatus.setText(oStatus);
                    }
                    else {
                        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(oStatus));
                        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                        Cstatus.setText("Seen at:"+dateTime);
                    }
                    Cname.setText(uName);
                    try {
                        Picasso.get().load(image).into(Cpic);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.default_profile).into(Cpic);
                    }
                }
                readMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage.setTranslationY(100);
                String MSG=Message.getText().toString().trim();
                //check if text is empty or not
                if (TextUtils.isEmpty(MSG)){
                    Toast.makeText(ChatActivity.this, "Cannot Send Empty Text", Toast.LENGTH_SHORT).show();
                    SendMessage.animate().translationYBy(-100).setDuration(500);
                }
                else {
                    sendMessage(MSG);
                    SendMessage.animate().translationYBy(-100).setDuration(500);
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seenMessage();
    }

    private void seenMessage() {
        userRefForSeen =FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener= userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Modelmessage chat = ds.getValue(Modelmessage.class);
                    if (chat.getReceiver().equals(MyUid)&&chat.getSender().equals(UserUid)){
                        HashMap<String,Object> hashseenMap=new HashMap<>();
                        hashseenMap.put("isSeen",true);
                        ds.getRef().updateChildren(hashseenMap);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String msg) {
        reference=database.getReference();
        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",MyUid);
        hashMap.put("receiver",UserUid);
        hashMap.put("message",msg);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        reference.child("Chats").push().setValue(hashMap);
        Message.setText("");
    }
    private void readMessages(){
        reference=database.getReference("Chats");
        chatList=new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Modelmessage modelmessage=ds.getValue(Modelmessage.class);
                    if (modelmessage.getReceiver().equals(MyUid)&&modelmessage.getSender().equals(UserUid)
                        ||modelmessage.getReceiver().equals(UserUid)&&modelmessage.getSender().equals(MyUid)){
                        chatList.add(modelmessage);
                    }
                }
                //adapter
                messageAdapter=new MessageAdapter(getApplicationContext(),chatList);
                messageAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update
        dbRef.updateChildren(hashMap);
    }
    @Override
    protected void onPause() {

        super.onPause();
        String ts= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        userRefForSeen.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("Online");
        super.onResume();
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }
}
