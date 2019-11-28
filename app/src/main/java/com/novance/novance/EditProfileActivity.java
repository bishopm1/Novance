package com.novance.novance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.Type;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //creates global user variable
    User u;

    //Profile Fragment tag
    private static final String TAG = "ProfileFragment";

    private StorageTask uploadTask;
    StorageReference storageReference;
    DatabaseReference reference;
    Uri uri;
    CircleImageView editProfileImageView;
    boolean newImageSelected;

    //TODO old profile image should be removed from firebase after changed
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //gets user passed from login/create account activity
        Intent i = getIntent();
        u = i.getParcelableExtra("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditText nameEditText = findViewById(R.id.nameEditText);
        nameEditText.setText(u.getFullName());
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setText(u.getUsername());
        editProfileImageView = (CircleImageView) findViewById(R.id.editProfileImageView);
        if (u.getProfileImageUri() != null) {
            // Image link from internet
            String link = u.getProfileImageUri().toString();

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle_black_240dp)
                    .error(R.drawable.ic_account_circle_black_240dp);

            Glide.with(this).load(link).apply(options).into(editProfileImageView);
            editProfileImageView.setBorderColor(Color.parseColor("#243248"));
            editProfileImageView.setBorderWidth(2);
        }
        //TODO do the same for bio and profile picture
        //TODO handle editing of all elements inluding profile picture and bio

        //handling click and editing of profile picture
        storageReference = FirebaseStorage.getInstance().getReference("profilePictures");
        reference = FirebaseDatabase.getInstance().getReference("users").child(u.getId());

        editProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(EditProfileActivity.this);
            }
        });

        //TODO finish coding save button
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newImageSelected) {
                    u.setProfileImageUri(uri);
                    saveUser(u);
                    uploadImage(u.getProfileImageUri());
                }
                //creates intent for main activity and starts
                Intent i = new Intent(EditProfileActivity.this, MainNavActivity.class);
                //passes user to next activity
                i.putExtra("user", u);
                i.putExtra("comingFrom", "EditProfileActivity");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                uri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCrop(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                editProfileImageView.setImageURI(uri);
                newImageSelected = true;
                Toast.makeText(this, "Profile image updated successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCrop(Uri imageUri) {
        CropImage.ActivityBuilder activityBuilder = CropImage.activity(imageUri);
        activityBuilder.setAspectRatio(1, 1);
        activityBuilder.setGuidelines(CropImageView.Guidelines.ON);
        activityBuilder.setMultiTouchEnabled(true);
        activityBuilder.start(this);
    }

    private void uploadImage(Uri imageUri) {
        final ProgressBar pb = new ProgressBar(getApplicationContext());
        pb.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + u.getId() + ".ProfileImage");

            //TODO delete olf profile photo from database

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(u.getId());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pb.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void saveUser(User u) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(u);
        editor.putString("user", json);
        editor.apply();
        Log.d(TAG, "User data saved internally");
    }
    //TODO toast not showing in some of app
    //TODO while profile image is loading put progress circle in spot
    //TODO remove old image from firebase when new profile image is selected
}