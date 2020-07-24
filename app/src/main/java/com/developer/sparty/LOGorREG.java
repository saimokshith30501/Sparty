package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Button signin,forgot;
    ProgressDialog progressDialog,progressReset;
    private FirebaseAuth mAuth;
    SignInButton signInButton;
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
        forgot=findViewById(R.id.forgot_bt);
        signInButton=findViewById(R.id.google_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating");
        progressReset = new ProgressDialog(this);
        progressReset.setMessage("Sending");
        mAuth=FirebaseAuth.getInstance();
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
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            uname.setError("Field cannot be empty");
            uname.setFocusable(true);
            return false;
        } else if (!val.matches(emailPattern)) {
            uname.setError("Invalid email address");
            uname.setFocusable(true);
            return false;
        } else {
            uname.setError(null);
            uname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = pass.getEditText().getText().toString();
        if (val.isEmpty()) {
            pass.setError("Field cannot be empty");
            pass.setFocusable(true);
            return false;
        } else if (val.length()<6){
            pass.setError("Password length too short");
            pass.setFocusable(true);
            return false;
        }else {
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
           progressDialog.show();
           isuser();
       }
   }

    private void isuser() {
     final String userEntered=uname.getEditText().getText().toString().trim();
     final String passEntered=pass.getEditText().getText().toString().trim();
        mAuth.signInWithEmailAndPassword(userEntered,passEntered)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LOGorREG.this, ProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(LOGorREG.this, "Wrong Credentials",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LOGorREG.this, "Authentication failed."+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onBackPressed(){
        finish();
    }

    public void recoverPass(final View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailEntered=new EditText(this);
        emailEntered.setHint("Enter Registered Email");
        emailEntered.setMinEms(15);
        linearLayout.addView(emailEntered);
//        linearLayout.setBackgroundColor(Color.parseColor("#A9C4DF"));
        linearLayout.setPadding(30,10,30,10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        builder.setTitle("Recover Password").setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 String email=emailEntered.getText().toString().trim();
                 if (vaidateEmail(email)) {
                     beginRecovery(email);
                 }else {
                     Snackbar.make(getWindow().getDecorView().getRootView(), "Enter a valid email", Snackbar.LENGTH_SHORT).show();
                 }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });
        builder.setView(linearLayout);
        builder.setIcon(R.drawable.recovery_icon);
        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressReset.show();
        final int[] s = new int[1];
         mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     progressReset.dismiss();
                     Snackbar.make(getWindow().getDecorView().getRootView(), "Reset Link has been sent successfully", Snackbar.LENGTH_SHORT).show();
                 }
                 else {
                     progressReset.dismiss();
//                     Snackbar.make(findViewById(R.id.viewSnack),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                     Toast.makeText(LOGorREG.this, "Failed "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 progressReset.dismiss();
                 Toast.makeText(LOGorREG.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
    }
    private Boolean vaidateEmail(String val) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            return false;
        } else if (!val.matches(emailPattern)) {
            return false;
        } else {
            return true;
        }
    }
}
