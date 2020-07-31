package com.developer.sparty;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.developer.sparty.Adapters.AdapterChatList;
import com.developer.sparty.Adapters.AdapterUsers;
import com.developer.sparty.Models.ModelChatList;
import com.developer.sparty.Models.ModelUser;
import com.developer.sparty.Models.Modelmessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ModelChatList> chatListList;
    private List<ModelUser> usersList;
    private AdapterChatList adapterChatList;
    RelativeLayout relativeLayout;
    FirebaseUser CurrentUser;
    DatabaseReference reference;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.chats_recyclerview);
        relativeLayout=view.findViewById(R.id.load_chats);
        recyclerView.setHasFixedSize(true);
        CurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        chatListList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(CurrentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatListList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChatList modelChatList=ds.getValue(ModelChatList.class);
                    chatListList.add(modelChatList);
                }
                loadChats();
                relativeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadChats() {
        usersList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    for (ModelChatList cl:chatListList) {
                      if (modelUser.getUid()!=null&& modelUser.getUid().equals(cl.getId())){
                          usersList.add(modelUser);
                          break;
                      }
                    }
                   adapterChatList =new AdapterChatList(getContext(),usersList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0;i<usersList.size();i++){
                         lastMessage(usersList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String USERID) {
        DatabaseReference rf=FirebaseDatabase.getInstance().getReference("Chats");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage="default",LastMessageTime="default";
                for (DataSnapshot ds:snapshot.getChildren()){
                    Modelmessage chat=ds.getValue(Modelmessage.class);
                    if (chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String receiver=chat.getReceiver();
                    if (sender==null||receiver==null){
                        continue;
                    }
                    if (chat.getReceiver().equals(CurrentUser.getUid())
                            &&chat.getSender().equals(USERID)
                            ||chat.getReceiver().equals(USERID)
                            &&chat.getSender().equals(CurrentUser.getUid())
                    ){   Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(chat.getTimestamp()));
                        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                         theLastMessage=chat.getMessage();
                        LastMessageTime=dateTime;
                    }
                }
                adapterChatList.setLastMessage(USERID,theLastMessage);
                adapterChatList.setLastMessageTime(USERID,LastMessageTime);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}
