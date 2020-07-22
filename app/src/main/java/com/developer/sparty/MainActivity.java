package com.developer.sparty;

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

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
     Timer timer;
     ImageView logo,name;
     Animation top,bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        logo=findViewById(R.id.imageView4);
        name=findViewById(R.id.imageView);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logo.setAnimation(top);
        name.setAnimation(bottom);
        timer=new Timer();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent startActivity = new Intent(MainActivity.this, LOGorREG.class);
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,logo,"main_logo");
                startActivity(startActivity,options.toBundle());
                finish();
            }
        },3000);
    }
}
