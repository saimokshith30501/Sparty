package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LOGorREG extends AppCompatActivity {
    ImageView logo;
    TextView textView,slog;
    TextInputLayout uname,pass;
    Button signin;
    Animation top,bottom;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logreg);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        logo=findViewById(R.id.logo);
        textView=findViewById(R.id.welcome);
        slog=findViewById(R.id.slogan);
        uname=findViewById(R.id.username);
        pass=findViewById(R.id.password);
        signin=findViewById(R.id.signin);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void signup(View view){
        Intent startActivity = new Intent(LOGorREG.this, signup.class);
        Pair[] pairs=new Pair[6];
        pairs[0] = new Pair<View,String>(logo,"main_logo");
        pairs[1] = new Pair<View,String>(textView,"text_logo");
        pairs[2] = new Pair<View,String>(slog,"slogan");
        pairs[3] = new Pair<View,String>(uname,"username_field");
        pairs[4] = new Pair<View,String>(pass,"password_field");
        pairs[5] = new Pair<View,String>(signin,"sign");
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(LOGorREG.this,pairs);
        startActivity(startActivity,options.toBundle());
    }
    private Boolean validateUsername() {
        String val = uname.getEditText().getText().toString();
        if (val.isEmpty()) {
            uname.setError("Field cannot be empty");
            return false;
        } else if (val.length()<10){
            uname.setError("Enter a valid phone number");
            return false;
        }else {
            uname.setError(null);
            uname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = pass.getEditText().getText().toString();
        if (val.isEmpty()) {
            pass.setError("Field cannot be empty");
            return false;
        } else {
            pass.setError(null);
            pass.setErrorEnabled(false);
            return true;
        }
    }
   public void signin(View view) {
       if (!validateUsername() | !validatePassword()) {
           return;
       }
       else {
           isuser();
       }
   }

    private void isuser() {
        progressBar.setVisibility(View.VISIBLE);
     final String userEntered=uname.getEditText().getText().toString().trim();
     final String passEntered=pass.getEditText().getText().toString().trim();
        final DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.child(userEntered);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    uname.setError(null);
                    uname.setEnabled(false);
                    String password=snapshot.child("password").getValue(String.class);
                    if(password.equals(passEntered)){
                        uname.setError(null);
                        uname.setEnabled(false);
                        Toast.makeText(LOGorREG.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else {
                        pass.setError("Wrong Password");
                        pass.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    uname.setError("No such user exist");
                    uname.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
