package com.shycoder.android.travelmantics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddDealActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;

    private EditText mTitle;
    private EditText mPrice;
    private EditText mDesc;
    private Button mSelect;
    private ImageView mImage;

    private Uri mFilePath;
    private Uri mDownloadUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mTitle = findViewById(R.id.deal_title);
        mPrice = findViewById(R.id.deal_price);
        mDesc = findViewById(R.id.deal_desc);
        mSelect = findViewById(R.id.deal_img_upload);
        mImage = findViewById(R.id.deal_image);

        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void addDeal() {
        String title = mTitle.getText().toString();
        String price = mPrice.getText().toString();
        String desc = mDesc.getText().toString();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(desc)) {
            mDb = FirebaseFirestore.getInstance();
            Map<String, Object> deal = new HashMap<>();
            deal.put("title", title);
            deal.put("price", price);
            deal.put("description", desc);

            if(mDownloadUri != null) {
                deal.put("image_url", mDownloadUri);
            } else {
                deal.put("image_url", "");
            }

            mDb.collection("deals")
                    .add(deal)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(AddDealActivity.this, "Deal added successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    private void uploadImage() {
        StorageReference riversRef = mStorageRef.child("images/"+ UUID.randomUUID().toString());

        riversRef.putFile(mFilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        mDownloadUri = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(AddDealActivity.this, "Something went wrong when uploading your image",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            mFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePath);
                mImage.setImageBitmap(bitmap);

                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deal_menu, menu);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.add_deal:
                addDeal();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(AddDealActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
