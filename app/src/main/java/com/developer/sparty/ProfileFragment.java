package com.developer.sparty;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment{
    Button logout;
    TextView email,phone,fullname;
    EditText username;
    ImageView profilepic;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;
    //storage
    StorageReference storageReference;
    //path
    String storagePath="Users_Images/";

    FloatingActionButton fab;
    ProgressDialog pd1;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;
    String cameraPermissions[];
    String storagePermissions[];
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
        storageReference=getInstance().getReference();


                //initialising views
        fab=view.findViewById(R.id.float_edit);
        logout =view.findViewById(R.id.profile_logout);
        email =view.findViewById(R.id.profile_email);
        fullname =view.findViewById(R.id.profile_fullname);
        phone =view.findViewById(R.id.profile_phone);
        profilepic =view.findViewById(R.id.profile_pic);
        username =view.findViewById(R.id.profile_username);

        //initiating permissions
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //setting progress dialog
        pd1=new ProgressDialog(getContext());
        pd1.setMessage("Updating");




        Query query=reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds: snapshot.getChildren()){
                String uname = "" + ds.child("username").getValue().toString();
                String mail = "" + ds.child("email").getValue().toString();
                String ph = "" + ds.child("phone").getValue().toString();
                String fname = "" + ds.child("fullname").getValue().toString();
                String image = "" + ds.child("image").getValue().toString();
                email.setText(mail);
                phone.setText(ph);
                fullname.setText(fname);
                username.setText(uname);
                try {
                    Picasso.get().load(image).into(profilepic);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.default_profile).into(profilepic);
                }
            }
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
//               EditorBottomSheet eb=new EditorBottomSheet();
//               eb.show(getFragmentManager(),"");

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
                    if (!checkCameraPermission()){
                        reqCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if(which==1){
                        //from gallery
                    if (!checkStoragePermission()){
                        reqStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    private void finishActivity() {
        if(getActivity() != null) {
            getActivity().finish();
        }
    }
    private boolean checkStoragePermission(){
        boolean res= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return res;
    }
    private void reqStoragePermission(){
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean res= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean res1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return res&&res1;
    }
    private void reqCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
            {
                  if (grantResults.length>0) {
                      boolean camAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                      boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                      if (camAccepted && writeAccepted) {
                          pickFromCamera();
                      } else {
                          Toast.makeText(getActivity(), "Please Enable Permission", Toast.LENGTH_SHORT).show();
                      }
                  }
            }
            break;
            case STORAGE_REQUEST_CODE:{

                if (grantResults.length>0) {
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Please Enable Permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }
    private void pickFromCamera() {

        //intent of picking image from device camera
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"TempPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"TempDes");

        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        //to pick from gallery
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //this method will be called after picking image from gallery or gallery

        if (requestCode==IMAGE_PICK_GALLERY_CODE){
            if (requestCode==RESULT_OK){
                image_uri=data.getData();
                 uploadProfilePic(image_uri);
            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                image_uri=data.getData();
                uploadProfilePic(image_uri);
            }
        }
    }
    private void uploadProfilePic(Uri uri){
           pd1.show();
           String filePathAndName=storagePath+""+"Profile_"+user.getUid();
           StorageReference storageReference1=storageReference.child(filePathAndName);
           storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   //image is uploaded now get its uri and store in database
                  Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                  while (!uriTask.isSuccessful()){
                      Uri dowloadUri=uriTask.getResult();

                      //check if image is uploaded or not
                      if (uriTask.isSuccessful()){
                          HashMap<String,Object> results=new HashMap<>();
                          results.put("image",dowloadUri.toString());
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
                      else {
                          pd1.dismiss();
                          Toast.makeText(getActivity(),"Some Error occured Try again",Toast.LENGTH_SHORT).show();
                      }

                  }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   pd1.dismiss();
                   Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });
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
