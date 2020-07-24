package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
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
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#A9C4DF"));
        actionBar=getSupportActionBar();
        actionBar.setTitle("Home");
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setLogo(R.drawable.net);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
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
                    actionBar.setTitle("Home");
                    HomeFragment homeFragment=new HomeFragment();
                    FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,homeFragment,"");
                    ft1.commit();
                    return true;
                case R.id.profile_dashboard:
                    //
                    actionBar.setTitle("Profile");
                    ProfileFragment profileFragment=new ProfileFragment();
                    FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,profileFragment,"");
                    ft2.commit();
                    return true;
                case R.id.settings_dashboard:
                    //
                    actionBar.setTitle("Settings");
                    SettingsFragment settingsFragment=new SettingsFragment();
                    FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,settingsFragment,"");
                    ft3.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getActionBar().hide();
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.logout_action){
            mAuth.signOut();
            Intent startActivity = new Intent(DashboardActivity.this, LOGorREG.class);
            startActivity(startActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        finish();
    }
}
