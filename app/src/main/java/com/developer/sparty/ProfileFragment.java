package com.developer.sparty;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment{
    Button logout;
    TextView email,phone,fullname;
    TextView username;
    LottieAnimationView profilepic;
    ActionBar actionBar;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;
    //storage
    StorageReference storageReference;
    //path
    String currentPhotoPath;
    String storagePath="Users_Images/";

    FloatingActionButton fab;
    ProgressDialog pd1;
    RelativeLayout loadData;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    Uri image_uri;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        //initiating firebase constants
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();


                //initialising views
        fab=view.findViewById(R.id.float_edit);
        logout =view.findViewById(R.id.profile_logout);
        email =view.findViewById(R.id.profile_email);
        fullname =view.findViewById(R.id.profile_fullname);
        phone =view.findViewById(R.id.profile_phone);
        profilepic =view.findViewById(R.id.profile_pic);
        loadData=view.findViewById(R.id.load_data);



        //setting progress dialog
        pd1=new ProgressDialog(getContext());
        pd1.setMessage("Updating");
        Query query=reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds: snapshot.getChildren()){
                String mail = "" + ds.child("email").getValue().toString();
                String ph = "" + ds.child("phone").getValue().toString();
                String fname = "" + ds.child("fullname").getValue().toString();
                String image = "" + ds.child("image").getValue().toString();
                email.setText(mail);
                phone.setText(ph);
                fullname.setText(fname);
                try {
                    Picasso.get().load(image).into(profilepic);
                } catch (Exception e) {
                    profilepic.setAnimation(R.raw.default_profile);
                }
            }
            loadData.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Snackbar.make(v,"Logged out",Snackbar.LENGTH_SHORT).show();
                Intent startActivity = new Intent(getContext(), LOGorREG.class);
                startActivity(startActivity);
                finishActivity();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bs=new BottomSheetDialog(getContext());
                bs.setContentView(R.layout.bottom_sheet_editor);
                ImageView ig=bs.findViewById(R.id.edit_pic);
                ig.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       showEditProfileDialog();
                        bs.dismiss();
                    }
                });
                ImageView ig1=bs.findViewById(R.id.edit_name);
                ig1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditNameDialog();
                        bs.dismiss();
                    }
                });
                ImageView ig2=bs.findViewById(R.id.edit_phone);
                ig2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditPhoneDialog();
                        bs.dismiss();
                    }
                });
                bs.show();
//

            }
        });
        return view;
    }

    private void showEditPhoneDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.sheet_logo3).setTitle("Edit Phone number");
        LinearLayout linearLayout=new LinearLayout(getActivity());
        final EditText PhoneEntered=new EditText(getActivity());
        PhoneEntered.setHint("Enter Phone");
        PhoneEntered.setMinEms(15);
        linearLayout.addView(PhoneEntered);
//        linearLayout.setBackgroundColor(Color.parseColor("#A9C4DF"));
        linearLayout.setPadding(30,10,30,10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String UpPhone=PhoneEntered.getText().toString().trim();
                if (vaidatePhone(UpPhone)) {
                    beginUpdatingPhone(UpPhone);
                }else {
                    Toast.makeText(getActivity(), "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(linearLayout);
        builder.create().show();
    }
    private void showEditNameDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.sheet_logo2).setTitle("Edit Name");
        LinearLayout linearLayout=new LinearLayout(getActivity());
        final EditText NameEntered=new EditText(getActivity());
        NameEntered.setHint("Enter FullName");
        NameEntered.setMinEms(15);
        linearLayout.addView(NameEntered);
//        linearLayout.setBackgroundColor(Color.parseColor("#A9C4DF"));
        linearLayout.setPadding(30,10,30,10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Upname=NameEntered.getText().toString().trim();
                if (vaidateName(Upname)) {
                    beginUpdatingName(Upname);
                }else {
                    Toast.makeText(getActivity(), "Enter a valid name", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(linearLayout);
        builder.create().show();
    }
    private void beginUpdatingName(String Upname) {
        pd1.show();
        HashMap<String,Object> results=new HashMap<>();
        results.put("fullname",Upname);
        reference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd1.dismiss();
                Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd1.dismiss();
                Toast.makeText(getActivity(),"Some Error occured Try again",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void beginUpdatingPhone(String UpPhone) {
        pd1.show();
        HashMap<String,Object> results=new HashMap<>();
        results.put("phone",UpPhone);
        reference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd1.dismiss();
                Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd1.dismiss();
                Toast.makeText(getActivity(),"Some Error occured Try again",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showEditProfileDialog() {
        String opt[]={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.sheet_logo1).setTitle("Choose Image From");
        builder.setItems(opt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                       //from camera
                    askCameraPermissions();
                }
                else if(which==1){
                        //from gallery
                    Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery.setType("image/*");
                    startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                }
            }
        });
        builder.create().show();
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            openCamera();
//            dispatchTakePictureIntent();
        }

    }

    private void openCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
//                dispatchTakePictureIntent();
            }else {
                Toast.makeText(getActivity(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                image_uri=data.getData();
                uploadImageToFirebase(image_uri);
            }
        }
        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                uploadImageToFirebase(contentUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri contentUri) {
        if (contentUri!=null){
            pd1.show();
            final String filePathAndName=storagePath+""+"Profile_"+user.getUid();
            final StorageReference storageReference1=storageReference.child(filePathAndName);
            storageReference1.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //image is uploaded now get its uri and store in database
                    //get uri from firebase
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String,Object> results=new HashMap<>();
                            results.put("image",uri.toString());
                            reference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd1.dismiss();
                                    Toast.makeText(getActivity(),"Uploaded",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd1.dismiss();
                                    Toast.makeText(getActivity(),"Some Error occured Try again",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd1.dismiss();
                            Toast.makeText(getActivity(), "Failed to connect server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd1.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    private void finishActivity() {
        if(getActivity() != null) {
            getActivity().finish();
        }
    }
    private Boolean vaidateName(String val) {
        if (val.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    private Boolean vaidatePhone(String val) {
        if (val.isEmpty()) {
            return false;
        } else if (val.length()<10){
            return false;
        }
        else {
            return true;
        }
    }
}
