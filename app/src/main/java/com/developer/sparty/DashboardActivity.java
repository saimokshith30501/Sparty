package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.developer.sparty.Notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    String mUID;
    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;
    static ArrayList<CONTACTS_DATA> listOfContacts;
    public static final int REQUEST_READ_CONTACTS = 79;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUID = user.getUid();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0582CA")));
        //on start default
        actionBar.setTitle(" " + "Chats");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.show();
        listOfContacts = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
           ReadContactsAndShowUsers();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, homeFragment, "");
        ft1.commit();
        //Update token
        checkUserStatus();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    public class CONTACTS_DATA {
        public String contact_data_name;
        public String contact_data_phoneNo;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  ReadContactsAndShowUsers();
                } else {
                    Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void ReadContactsAndShowUsers() {
        ContentResolver cr=getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur!=null?cur.getCount():0)>0) {
            while (cur!=null&&cur.moveToNext()) {
                CONTACTS_DATA contacts_data=new CONTACTS_DATA();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                contacts_data.contact_data_name=name;
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id}, null);

                    if (pCur.getCount()>0) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (phoneNo.startsWith("+91")||phoneNo.startsWith("0")){
                               phoneNo=phoneNo.replaceAll("\\s+","").replace("+91","");
                            }
                            else {
                                phoneNo=phoneNo.replaceAll("\\s+","");
                            }
                            contacts_data.contact_data_phoneNo = phoneNo;
                        }
                        listOfContacts.add(contacts_data);
                    }
                    else {
                        Log.d("CONTACT", "NO LENGTH");
                    }
                    pCur.close();
                }
            }
        }
        else {
            Log.d("CONTACT", "NO CONTACTS FOUND");
        }
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        checkOnlineStatus("Online");
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            mUID = firebaseUser.getUid();
            //save uid of currently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else {
            startActivity(new Intent(DashboardActivity.this, LOGorREG.class));
            finish();
        }
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.home_dashboard:
                    //
                    actionBar.setTitle(" " + "Sparty");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content, homeFragment, "");
                    ft1.commit();
                    return true;
                case R.id.profile_dashboard:
                    //
                    actionBar.setTitle(" " + "Profile");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content, profileFragment, "");
                    ft2.commit();
                    return true;
                case R.id.users_dashboard:
                    //
                    actionBar.setTitle(" " + "Sparty");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    UsersFragment usersFragment = new UsersFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content, usersFragment, "");
                    ft3.commit();
                    return true;
            }
            return false;
        }
    };

    private void checkOnlineStatus(String status) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(mUID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        String ts = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        super.onPause();
    }

    public void onBackPressed() {
        String ts = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        finish();
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }
}
