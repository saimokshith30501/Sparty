package com.developer.sparty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class signup extends AppCompatActivity {
    TextInputLayout fullname, emailid, password, phone;
    static UserHelperClass userHelperClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fullname = findViewById(R.id.name);
        emailid = findViewById(R.id.email);
        phone = findViewById(R.id.phoneno);
        password = findViewById(R.id.password);
    }

    private Boolean vaidateName() {
        String val = fullname.getEditText().getText().toString();
        if (val.isEmpty()) {
            fullname.setError("Cannot be empty");
            return false;
        } else {
            fullname.setError(null);
            return true;
        }
    }
    private Boolean vaidateEmail() {
        String val = emailid.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            emailid.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            emailid.setError("Invalid email address");
            return false;
        } else {
            emailid.setError(null);
            emailid.setErrorEnabled(false);
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
    private Boolean vaidatePassword() {
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak (a-z,$,4)");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }



    public void addData(View view) {
        if (!vaidateName()| !vaidateEmail() | !vaidatePhone() | !vaidatePassword() ){
            return;
        }
        String regName = fullname.getEditText().getText().toString();
        String regEmail = emailid.getEditText().getText().toString();
        String regPassword = password.getEditText().getText().toString();
        String regPhone = phone.getEditText().getText().toString();
        userHelperClass=new UserHelperClass(regName,regEmail,regPhone,regPassword);
        Intent startActivity = new Intent(signup.this, phoneverify.class);
        startActivity.putExtra("NUM",regPhone);
        startActivity(startActivity);
    }
}
