package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.developer.sparty.Notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    String mUID;
    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mUID=user.getUid();
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0582CA")));
        //on start default
        actionBar.setTitle(" "+"Chats");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.show();
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,homeFragment,"");
        ft1.commit();
        //Update token
        checkUserStatus();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        checkOnlineStatus("Online");
        super.onResume();
    }
    private void checkUserStatus(){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            mUID=firebaseUser.getUid();
            //save uid of currently signed in user in shared preferences
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }
        else {
            startActivity(new Intent(DashboardActivity.this,LOGorREG.class));
            finish();
        }
    }

    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(mUID).setValue(mToken);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.home_dashboard:
                    //
                    actionBar.setTitle(" "+"Sparty");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    HomeFragment homeFragment=new HomeFragment();
                    FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,homeFragment,"");
                    ft1.commit();
                    return true;
                case R.id.profile_dashboard:
                    //
                    actionBar.setTitle(" "+"Profile");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    ProfileFragment profileFragment=new ProfileFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,profileFragment,"");
                    ft2.commit();
                    return true;
                case R.id.users_dashboard:
                    //
                    actionBar.setTitle(" "+"Sparty");
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.show();
                    UsersFragment usersFragment =new UsersFragment();
                    FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content, usersFragment,"");
                    ft3.commit();
                    return true;
            }
            return false;
        }
    };
    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(mUID);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        String ts=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        super.onPause();
    }
    public void onBackPressed(){
        String ts=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(ts);
        finish();
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }
}
