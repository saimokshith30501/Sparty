package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.developer.sparty.Adapters.MessageAdapter;
import com.developer.sparty.Models.ModelUser;
import com.developer.sparty.Models.Modelmessage;
import com.developer.sparty.Notifications.Data;
import com.developer.sparty.Notifications.Sender;
import com.developer.sparty.Notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    EditText Message;
    TextView Cname,Cstatus;
    ImageButton SendMessage,Attachments;
    ImageView Cpic;
    RecyclerView recyclerView;
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference,databaseReference;
    String UserUid;
    String MyUid;
    StorageReference storageReference;
    String uName;
    String image;
    Animation sendB;
    String oStatus;
    String tStatus;
    MessageAdapter messageAdapter;
    List<Modelmessage> chatList;
    private RequestQueue requestQueue;
    private BottomSheetDialog bottomSheetDialog;
    ProgressDialog pd1;
    boolean notify=false;
    boolean notified=true;
    //for checking user has seen msg or not
    ValueEventListener valueEventListener;
    DatabaseReference userRefForSeen;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    View bs;
    Uri image_uri;
    String storagePath="Chat_Images/";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView=findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        bottomSheetDialog=new BottomSheetDialog(ChatActivity.this);
        bs=getLayoutInflater().inflate(R.layout.bottom_sheet_chat,null);
        bottomSheetDialog.setContentView(bs);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Message=findViewById(R.id.chat_message);
        Cname=findViewById(R.id.chat_user_name);
        Cstatus=findViewById(R.id.chat_user_status);
        SendMessage=findViewById(R.id.chat_sendbutton);
        Attachments=findViewById(R.id.chat_attach);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        Cpic=findViewById(R.id.chat_user_image);
        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user=firebaseAuth.getCurrentUser();
        pd1=new ProgressDialog(ChatActivity.this);
        pd1.setMessage("Sending image...");
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
                    tStatus=""+ds.child("typingTo").getValue().toString();
                    if (tStatus.equals(MyUid)) {
                        Cstatus.setTextColor(Color.parseColor("#27F9C2"));
                        Cstatus.setText("Typing...");
                    }
                    else {
                        if (oStatus.equals("Online"))
                        {
                            Cstatus.setTextColor(Color.WHITE);
                            Cstatus.setText(oStatus);
                           notified=false;
                        }
                        else {
                            notified=true;
                            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(oStatus));
                            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                            Cstatus.setTextColor(Color.WHITE);
                            Cstatus.setText("Last Seen: "+dateTime);
                        }
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
        SendMessage.setOnClickListener(v -> {
            SendMessage.setTranslationY(100);
            String MSG=Message.getText().toString().trim();
            notify=true;
            //check if text is empty or not
            if (TextUtils.isEmpty(MSG)){
                Toast.makeText(ChatActivity.this, "Cannot Send Empty Text", Toast.LENGTH_SHORT).show();
                SendMessage.animate().translationYBy(-100).setDuration(500);
            }
            else {
                sendMessage(MSG);
                SendMessage.animate().translationYBy(-100).setDuration(500);
            }
            Message.setText("");
        });
        Message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (s.toString().length()==0){
                   checkTypingStatus("");
               }
               else {
                   checkTypingStatus(UserUid);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {

            if(item.getItemId()==R.id.delete_chat)
            {
                Toast.makeText(ChatActivity.this, "Clear", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        seenMessage();
    }
    public void openBsheet(View view){
           bottomSheetDialog.show();
           View opengallery=bs.findViewById(R.id.add_photo);
           opengallery.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   //from gallery
                   Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   gallery.setType("image/*");
                   startActivityForResult(gallery, GALLERY_REQUEST_CODE);
//                   String action=gallery.getAction();
//                   String type=gallery.getType();
//                   if(Intent.ACTION_SEND.equals(action)&&type!=null){
//                        if (type.startsWith("image/*")) {
//                            try {
//                                handleSendImage(gallery);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                   }
                   bottomSheetDialog.dismiss();
               }
           });
    }

    private void handleSendImage(Intent gallery) throws IOException {
        Uri imageUri=(Uri)gallery.getParcelableExtra(Intent.EXTRA_STREAM);
        sendImageMessage(imageUri);
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
    private void sendMessage(final String msg) {

        reference=database.getReference();
        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",MyUid);
        hashMap.put("receiver",UserUid);
        hashMap.put("message",msg);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        hashMap.put("type","text");
        reference.child("Chats").push().setValue(hashMap);

        DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                if (notify&&notified){
                    sendNotification(UserUid,modelUser
                    .getFullname(),msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference("Chatlist").child(MyUid).child(UserUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if (!snapshot.exists()){
                      chatRef1.child("id").setValue(UserUid);
                  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference("Chatlist").child(UserUid).child(MyUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(MyUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void sendImageMessage(Uri contentUri) throws IOException {
        if (contentUri!=null){
            String timeStamp=""+System.currentTimeMillis();
            final String filePathAndName=storagePath+""+"SentImages_"+timeStamp;
            Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentUri);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            //to convert image to bytes
            byte[] data=baos.toByteArray();
            final StorageReference storageReference1= FirebaseStorage.getInstance().getReference().child(filePathAndName);
            storageReference1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //image is uploaded now get its uri and store in database
                    //get uri from firebase

                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("sender",MyUid);
                            hashMap.put("receiver",UserUid);
                            hashMap.put("message",uri.toString());
                            hashMap.put("timestamp",timeStamp);
                            hashMap.put("isSeen",false);
                            hashMap.put("type","image");
                            databaseReference.child("Chats").push().setValue(hashMap);
                            DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser modelUser=snapshot.getValue(ModelUser.class);
                                    pd1.dismiss();
                                    if (notify&&notified){
                                        sendNotification(UserUid,modelUser
                                                .getFullname(),"SENT AN IMAGE");
                                    }
                                    notify=false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            final DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference("Chatlist").child(MyUid).child(UserUid);
                            chatRef1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        chatRef1.child("id").setValue(UserUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            final DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference("Chatlist").child(UserUid).child(MyUid);
                            chatRef2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        chatRef2.child("id").setValue(MyUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd1.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd1.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            pd1.dismiss();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendNotification(String userUid, final String getfullname, final String msg) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(UserUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(MyUid,getfullname+": "+msg,"New Message",userUid,R.drawable.app_logo);
                    Sender sender=new Sender(data,token.getToken());
                    //fcm json object request
                    try {
                        JSONObject senderJsonObj=new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                response -> {
                                    //response
                                    Log.d("JSON_RESPONSE","onResponse "+response.toString());
                                }, error -> Log.d("JSON_RESPONSE","onResponse "+error.toString())){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> headers=new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAybt19gw:APA91bFX8GtkI0MBnuOcq7dWbVtTZL6unO1mKOV34du2fOBo_Xw-e12y2BGblJLOGop9n8jrfUUdq3Cvh-EYyJd8kVF6c8sPrgoEEeaF7EqNfViVGyrLTd1btBKVpDbF-nFzydn_7neh");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    private void checkTypingStatus(String Tstatus){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo",Tstatus);
        //update
        dbRef.updateChildren(hashMap);
    }
    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("");
        String ts=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        userRefForSeen.removeEventListener(valueEventListener);
    }
    protected void onResume() {
        checkOnlineStatus("Online");
        super.onResume();
    }
    protected void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_options, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                image_uri= data.getData();
                try {
                    pd1.show();
                    notify=true;
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
//

}
