package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

public class phoneverify extends AppCompatActivity {
    String phoneno,verifycationCODE;
    Button verifyB;
    PinView pinFromUser;
    Button VerifyOtp;
    TextView otp,verification,detecting;
//    GifImageView phoneVerification;
    TextInputLayout codeLayout;
    LottieAnimationView phoneVerified,phoneVerification;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneverify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        phoneno=getIntent().getStringExtra("NUM");
        mAuth = FirebaseAuth.getInstance();
        phoneVerified = findViewById(R.id.phone_verified);
        phoneVerification = findViewById(R.id.phone_verificaction);
        sendVerificationCodeToUser(phoneno);
        pinFromUser = findViewById(R.id.pin_view);
        VerifyOtp = findViewById(R.id.verifyOtpButton);
        otp = findViewById(R.id.verify_otp);
        verification = findViewById(R.id.verify_verification);
        detecting = findViewById(R.id.verify_detect);
        VerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinFromUser.getText().toString();
                if (!code.isEmpty()) {
                    verifyCode(code);
                } else {
                    pinFromUser.setError("INCORRECT OTP");
                }
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneno) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneno,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(phoneverify.this, "CODE SENT", Toast.LENGTH_LONG).show();
            verifycationCODE=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
          String code= phoneAuthCredential.getSmsCode();
          if(code!=null){
              verifyCode(code);
          }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(phoneverify.this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private void verifyCode(String verfiy){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verifycationCODE,verfiy);
        signintheuser(credential);
    }

    private void signintheuser(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(phoneverify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            otp.setVisibility(View.GONE);
                            detecting.setVisibility(View.GONE);
                            verification.setVisibility(View.GONE);
                            phoneVerification.setVisibility(View.GONE);
                            VerifyOtp.setVisibility(View.GONE);
                            pinFromUser.setVisibility(View.GONE);
                            phoneVerified.setVisibility(View.VISIBLE);
                            regUser();
                        }
                        else {
                           Toast.makeText(phoneverify.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

    private void regUser() {
        mAuth.createUserWithEmailAndPassword(signup.userHelperClass.EMAIL,signup.userHelperClass.PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=user.getUid();
                            String email=user.getEmail();
                            HashMap<Object,String> hashMap= new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("fullname",signup.userHelperClass.FULLNAME);
                            hashMap.put("onlineStatus","");
                            hashMap.put("typingTo","");
                            hashMap.put("phone",signup.userHelperClass.PHONE);
                            hashMap.put("image","");
                            firebaseDatabase=FirebaseDatabase.getInstance();
                            reference=firebaseDatabase.getReference("Users");
                            reference.child(uid).setValue(hashMap);
                            new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run(){
                                    Toast.makeText(phoneverify.this, "Account created with "+user.getEmail(),Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getApplicationContext(), DashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            },2000);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(phoneverify.this, "Authentication failed."+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(phoneverify.this, "Failed."+e.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
