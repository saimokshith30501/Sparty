package com.developer.sparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.sparty.Extras.CheckConnection;
import com.developer.sparty.Extras.CustomDialog;
import com.developer.sparty.Extras.NetConnection;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rezwan.knetworklib.KNetwork;

import java.util.HashMap;

public class LOGorREG extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient googleSignInClient;
    ImageView logo;
    TextView textView,slog;
    TextInputLayout uname,pass;
    Button signin,forgot,signup;
    CustomDialog customDialog;
    NetConnection netConnection;
//    ProgressDialog progressDialog,progressReset,progressgoogle;
    private FirebaseAuth mAuth;
    Button gsignInButton;
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
        forgot=findViewById(R.id.forgot_bt);
        signup=findViewById(R.id.signup_bt);
        signin=findViewById(R.id.signinb);
        gsignInButton=findViewById(R.id.google_login);
//        progressDialog = new ProgressDialog(this);
//////        progressDialog.setMessage("Authenticating");
//////        progressReset = new ProgressDialog(this);
//////        progressReset.setMessage("Sending");
//////        progressgoogle = new ProgressDialog(this);
//////        progressgoogle.setMessage("Signing in");
        customDialog=new CustomDialog(LOGorREG.this);
        netConnection=new NetConnection(LOGorREG.this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this,gso);
        mAuth=FirebaseAuth.getInstance();
        gsignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                customDialog.startLoadingDialog();
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
              customDialog.dismissLoadingDialog();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=user.getUid();
                            String email=user.getEmail();
                           customDialog.dismissLoadingDialog();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                Intent gsign=new Intent(LOGorREG.this, GoogleSignup.class);
                                gsign.putExtra("EMAIL",email);
                                gsign.putExtra("UID",uid);
                                startActivity(gsign);
                                finish();
                            }
                            else {
                                Toast.makeText(LOGorREG.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LOGorREG.this, DashboardActivity.class));
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            customDialog.dismissLoadingDialog();
                            Toast.makeText(LOGorREG.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customDialog.dismissLoadingDialog();
                Toast.makeText(LOGorREG.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signup(View view){
        Intent startActivity = new Intent(LOGorREG.this, signup.class);
        Pair[] pairs=new Pair[6];
        pairs[0] = new Pair<View,String>(logo,"main_logo");
        pairs[1] = new Pair<View,String>(textView,"logo_name");
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
        if (!new CheckConnection(LOGorREG.this).CheckNet())
        {
            netConnection.startLoadingDialog();
        }
       if (!validateUsername() | !validatePassword()) {
           return;
       }
       else {
           customDialog.startLoadingDialog();
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
                            customDialog.dismissLoadingDialog();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LOGorREG.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            customDialog.dismissLoadingDialog();
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
                     Toast.makeText(LOGorREG.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
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
        customDialog.startLoadingDialog();
        final int[] s = new int[1];
         mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     customDialog.dismissLoadingDialog();
                     Toast.makeText(LOGorREG.this, "Reset Link has been sent successfully", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     customDialog.dismissLoadingDialog();
//                     Snackbar.make(findViewById(R.id.viewSnack),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                     Toast.makeText(LOGorREG.this, "Failed "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 customDialog.dismissLoadingDialog();
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
