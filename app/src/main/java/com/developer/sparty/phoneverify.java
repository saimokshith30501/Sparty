package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class phoneverify extends AppCompatActivity {
    String phoneno,verifycationCODE;
    Button verifyB;
    TextInputLayout codeLayout;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneverify);
        verifyB=findViewById(R.id.verify_btn);
        codeLayout=findViewById(R.id.code_user);
        progressBar=findViewById(R.id.progress_bar);
        phoneno=getIntent().getStringExtra("NUM");
        progressBar.setVisibility(View.VISIBLE);
        sendVerificationCodeToUser(phoneno);
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
            verifycationCODE=s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
          String code= phoneAuthCredential.getSmsCode();
          if(code!=null){
              progressBar.setVisibility(View.VISIBLE);
              verifyCode(code);
          }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(phoneverify.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                            firebaseDatabase=FirebaseDatabase.getInstance();
                            reference=firebaseDatabase.getReference("Users");
                            reference.child(phoneno).setValue(signup.userHelperClass);
                            Intent intent=new Intent(getApplicationContext(),LOGorREG.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                           Toast.makeText(phoneverify.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
