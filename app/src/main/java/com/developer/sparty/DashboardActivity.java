package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0582CA")));
        actionBar.setTitle("Chats");
        //on start default
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,homeFragment,"");
        ft1.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.home_dashboard:
                    //
                    actionBar.setTitle("Chats");
                    actionBar.show();
                    HomeFragment homeFragment=new HomeFragment();
                    FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,homeFragment,"");
                    ft1.commit();
                    return true;
                case R.id.profile_dashboard:
                    //
                    actionBar.hide();
                    ProfileFragment profileFragment=new ProfileFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,profileFragment,"");
                    ft2.commit();
                    return true;
                case R.id.users_dashboard:
                    //
                    actionBar.setTitle("Sparty");
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
    public void onBackPressed(){
        finish();
    }
}
