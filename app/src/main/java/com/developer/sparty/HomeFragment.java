package com.developer.sparty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.developer.sparty.Adapters.AdapterUsers;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ModelUser> modelUsers;
    private List<String> usersList;
    private AdapterUsers adapterUsers;
    FirebaseUser user;
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user= FirebaseAuth.getInstance().getCurrentUser();
        usersList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                usersList.clear();
//                for (DataSnapshot ds:snapshot.getChildren()){
//                    Modelmessage modelmessage=ds.getValue(Modelmessage.class);
//                    if (modelmessage.getSender().equals(user.getUid())){
//                        usersList.add(modelmessage.getReceiver());
//                    }
//                    if (modelmessage.getReceiver().equals(user.getUid())){
//                        usersList.add(modelmessage.getSender());
//                    }
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        return view;
    }

    private void readChats() {
        modelUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelUsers.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelUser user=ds.getValue(ModelUser.class);
                    for (String id:usersList){
                         if (user.getUid().equals(id)){
                             if (modelUsers.size()!=0){
                                 for (ModelUser user1:modelUsers){
                                     if (!user.getUid().equals(user1.getUid())){
                                         modelUsers.add(user);
                                     }
                                 }
                             }
                             else {
                                 modelUsers.add(user);
                             }
                         }
                    }
                }
                adapterUsers=new AdapterUsers(getContext(),modelUsers);
                adapterUsers.notifyDataSetChanged();
                recyclerView.setAdapter(adapterUsers);
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
