package com.developer.sparty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.sparty.Adapters.AdapterUsers;
import com.developer.sparty.Models.ModelUser;
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
public class UsersFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    RelativeLayout relativeLayout;
    TextView textView;
    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=view.findViewById(R.id.users_recyclerView);
        relativeLayout=view.findViewById(R.id.load_data1);
        textView=view.findViewById(R.id.hint);
        relativeLayout.setVisibility(View.INVISIBLE);
        //set properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        userList=new ArrayList<>();
        return view;
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        //get path of users database
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    //get all users except current user
                    if (!modelUser.getUid().equals(fuser.getUid())){
                        userList.add(modelUser);
                    }
                    relativeLayout.setVisibility(View.INVISIBLE);
                    adapterUsers=new AdapterUsers(getActivity(),userList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUsers(final String query) {
        textView.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        //get current user
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        //get path of users database
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    //get all searched users except current user
                    if (!modelUser.getUid().equals(fuser.getUid())){
                        if (modelUser.getfullname().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getPhone().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }
                    adapterUsers=new AdapterUsers(getActivity(),userList);
                    adapterUsers.notifyDataSetChanged();
                    relativeLayout.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.search_icon);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button
                //if search is not empty

                if ((!TextUtils.isEmpty(query.trim()))){
                    //search text contains text,search it
                    searchUsers(query);
                }
                else {
                    //text is empty get all users
//                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called when user press any single letter
                if ((!TextUtils.isEmpty(newText.trim()))){
                    //search text contains text,search it
                    searchUsers(newText);
                }
                else {
                    //text is empty get all users
//                    getAllUsers();
                }
                return false;
            }
        });

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
