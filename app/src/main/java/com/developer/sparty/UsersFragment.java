package com.developer.sparty;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.dynamic.IFragmentWrapper;
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
    ArrayList<CONTACTS_DATA> listOfContacts;
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
        listOfContacts=new ArrayList<>();
//        ReadContactsAndShowUsers();
        return view;
    }
    public class CONTACTS_DATA{
        public  String contact_data_name;
        public  String contact_data_phoneNo;
    }
    private void ReadContactsAndShowUsers() {
        Cursor cursor_Android_Contacts=getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        CONTACTS_DATA contacts_data=new CONTACTS_DATA();
        while (cursor_Android_Contacts.moveToNext()){
             String Contact_name=cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
             String mobileNo=cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
             contacts_data.contact_data_name=Contact_name;
             contacts_data.contact_data_phoneNo=mobileNo;
             listOfContacts.add(contacts_data);
        }
        for (int i=0;i<listOfContacts.size();i++){
            Log.d("CONTACT",listOfContacts.get(i).contact_data_phoneNo);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode==1){
//            if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
//                ReadContactsAndShowUsers();
//            }
//            else {
//                Toast.makeText(getContext(), "Permission is required", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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
                        if (modelUser.getFullname().toLowerCase().contains(query.toLowerCase())||
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
