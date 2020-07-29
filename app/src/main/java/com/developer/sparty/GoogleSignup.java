package com.developer.sparty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GoogleSignup extends AppCompatActivity {
    TextInputLayout uname,fname,phone;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    FirebaseUser user;
    String uid;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signup);
        uid=getIntent().getStringExtra("UID");
        email=getIntent().getStringExtra("EMAIL");
        uname=findViewById(R.id.guname);
        fname=findViewById(R.id.gname);
        phone=findViewById(R.id.gphoneno);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Registering");
    }
    public void signupgoogle(View view){
        if (!vaidateName()| !vaidateUsername() | !vaidatePhone()){
            return;
        }
        else {
            progressDialog.show();
            HashMap<Object,String> hashMap= new HashMap<>();
            hashMap.put("email",email);
            hashMap.put("uid",uid);
            hashMap.put("fullname",fname.getEditText().getText().toString());
            hashMap.put("onlineStatus","");
            hashMap.put("typingTo","");
            hashMap.put("username",uname.getEditText().getText().toString());
            hashMap.put("phone",phone.getEditText().getText().toString());
            hashMap.put("image","");

            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
            DatabaseReference reference=firebaseDatabase.getReference("Users");
            reference.child(uid).setValue(hashMap);
            progressDialog.dismiss();
            startActivity(new Intent(GoogleSignup.this, DashboardActivity.class));
            finish();
        }
    }
    private Boolean vaidateUsername() {
        String val = uname.getEditText().getText().toString();
        String whiteSpace="\\A\\w{2,15}\\z";
        if (val.isEmpty()) {
            uname.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(whiteSpace)) {
            uname.setError("White Spaces are not allowed");
            return false;
        } else {
            uname.setError(null);
            uname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean vaidateName() {
        String val = fname.getEditText().getText().toString();
        if (val.isEmpty()) {
            fname.setError("Cannot be empty");
            return false;
        } else {
            fname.setError(null);
            return true;
        }
    }
    private Boolean vaidatePhone() {
        String val = phone.getEditText().getText().toString();

        if (val.isEmpty()) {
            phone.setError("Field cannot be empty");
            return false;
        } else if (val.length()<10){
            phone.setError("Enter a valid phone number");
            return false;
        }
        else {
            phone.setError(null);
            phone.setErrorEnabled(false);
            return true;
        }
    }
}
