package com.developer.sparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    Button logout;
    TextView email,phone,fullname;
    EditText username;
    ImageView profilepic;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;
    FloatingActionButton fab;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        fab=view.findViewById(R.id.float_edit);
        logout =view.findViewById(R.id.profile_logout);
        email =view.findViewById(R.id.profile_email);
        fullname =view.findViewById(R.id.profile_fullname);
        phone =view.findViewById(R.id.profile_phone);
        profilepic =view.findViewById(R.id.profile_pic);
        username =view.findViewById(R.id.profile_username);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Users");
        Query query=reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds: snapshot.getChildren()){
                String uname = "" + ds.child("username").getValue().toString();
                String mail = "" + ds.child("email").getValue().toString();
                String ph = "" + ds.child("phone").getValue().toString();
                String fname = "" + ds.child("fullname").getValue().toString();
                String image = "" + ds.child("image").getValue().toString();
                email.setText(mail);
                phone.setText(ph);
                fullname.setText(fname);
                username.setText(uname);
                try {
                    Picasso.get().load(image).into(profilepic);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.default_profile).into(profilepic);
                }
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Snackbar.make(v,"Logged out",Snackbar.LENGTH_SHORT).show();
                Intent startActivity = new Intent(getContext(), LOGorREG.class);
                startActivity(startActivity);
                finishActivity();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               EditorBottomSheet eb=new EditorBottomSheet();
               eb.
               eb.show(getFragmentManager(),"");
            }
        });











        return view;
    }
    private void finishActivity() {
        if(getActivity() != null) {
            getActivity().finish();
        }
    }
}
