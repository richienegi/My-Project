package com.example.ravisharma.school;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ravisharma.school.model.Images;
import com.example.ravisharma.school.model.Info;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UserHome extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 234;

    ImageView userImage;
    Button choose, upload;
    Uri filePath;

    DatabaseReference databaseImageUpload, databaseImgShow;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userImage = (ImageView) findViewById(R.id.userImg);
        choose = (Button) findViewById(R.id.selectImg);
        upload = (Button) findViewById(R.id.uploadImg);

        mAuth=FirebaseAuth.getInstance();
        databaseImageUpload = FirebaseDatabase.getInstance().getReference("image");
        databaseImgShow = databaseImageUpload.child(mAuth.getCurrentUser().getUid());
        databaseImgShow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Images img = dataSnapshot.getValue(Images.class);
                try{
                    Picasso.get().load(img.getImageLink()).into(userImage);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);
        userImage.setOnClickListener(this);
        upload.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if(v==choose || v==userImage){
            showFileChooser();
        }
        if(v==upload){
            uploadFile();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userImage.setImageBitmap(bitmap);
                upload.setEnabled(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference()
                    .child("images/"+mAuth.getCurrentUser().getUid()+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Images img = new Images(mAuth.getCurrentUser().getUid(), taskSnapshot.getDownloadUrl().toString());
                            databaseImageUpload.child(mAuth.getCurrentUser().getUid()).setValue(img);
                            startActivity(new Intent(UserHome.this, Dashboard.class));
                            progressDialog.dismiss();
                            upload.setEnabled(false);
                            Toast.makeText(UserHome.this, "Profile Pic Updated", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(UserHome.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    });
        }
        else {
            Toast.makeText(UserHome.this, "Choose Image To Upload", Toast.LENGTH_SHORT).show();
        }
    }
}
