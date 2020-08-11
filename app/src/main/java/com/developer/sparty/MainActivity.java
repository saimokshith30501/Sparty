package com.developer.sparty;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
     Timer timer;
     ImageView logo;
     TextView name;
     Animation top,bottom;
     FirebaseAuth firebaseAuth;
     FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ActionBar actionBar=getSupportActionBar();
//        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        logo=findViewById(R.id.imageView4);
        name=findViewById(R.id.name);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logo.setAnimation(top);
        name.setAnimation(bottom);
        firebaseAuth = FirebaseAuth.getInstance();
        timer=new Timer();
    }

    @Override
    protected void onStart() {
        CheckUserStatus();
        super.onStart();
    }

    private void CheckUserStatus() {
        user=firebaseAuth.getCurrentUser();
        if(user!=null){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
                }
            },2000);

        }
        else {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent startActivity = new Intent(MainActivity.this, LOGorREG.class);
                    Pair[] pairs=new Pair[2];
                    pairs[0]= new Pair<View,String>(logo,"main_logo");
                    pairs[1]= new Pair<View,String>(logo,"logo_name");
                    ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(startActivity,options.toBundle());
                    finish();
                }
            },2000);

        }
    }
}
