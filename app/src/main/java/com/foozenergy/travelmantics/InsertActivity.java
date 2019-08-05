package com.foozenergy.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InsertActivity extends AppCompatActivity {

    public static final int PICTURE_REQUEST_CODE = 12;
    public static final String TAG = "InsertActivity";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public static final String TRAVEL_DEAL = "travel deal";

    private EditText txtTitle;
    private EditText txtPrice;
    private EditText txtDescription;
    private ImageView imageView;
    private Travel travel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        //FirebaseUtil.openFbReference("travel", this);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        txtTitle = findViewById(R.id.title);
        txtPrice = findViewById(R.id.price);
        txtDescription = findViewById(R.id.description);
        imageView = findViewById(R.id.photo);

        Travel travel = getIntent().getParcelableExtra(TRAVEL_DEAL);

        if (travel == null){
            travel = new Travel();
        }

        this.travel = travel;
        txtTitle.setText(travel.getTitle());
        txtPrice.setText(travel.getPrice());
        txtDescription.setText(travel.getDescription());
        showImage(travel.getPhotoUrl());

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert a picture"), PICTURE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK){
            Uri photoUri = data.getData();
            final StorageReference storageReference = FirebaseUtil.storageReference.child(photoUri.getLastPathSegment());
            storageReference.putFile(photoUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            travel.setPhotoUrl(url);
                            showImage(url);
                            String photoName = taskSnapshot.getStorage().getPath();
                            travel.setPhotoName(photoName);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.button).setEnabled(true);
        }
        else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.button).setEnabled(false);
        }
        return true;
    }
    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveData();
                backToMainActivity();
                return true;
            case R.id.delete:
                deleteData();
                backToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void deleteData() {
        if (travel == null){
            Toast.makeText(this, "Unable to delete. Ensure travel deal exist", Toast.LENGTH_SHORT).show();
            return;
        }
            databaseReference.child(travel.getId()).removeValue();
            Toast.makeText(this, "Travel deal deleted successfully", Toast.LENGTH_SHORT).show();
            if (travel.getPhotoName() != null && !travel.getPhotoName().isEmpty()){
                StorageReference storageReference = FirebaseUtil.firebaseStorage.getReference().child(travel.getPhotoName());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: image deleted successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
            }
    }

    private void saveData() {
        travel.setTitle( txtTitle.getText().toString().trim());
        travel.setPrice(txtPrice.getText().toString().trim());
        travel.setDescription(txtDescription.getText().toString().trim());

        //Travel travel = new Travel(title, price, description, "");

        if(travel.getId() == null){
            databaseReference.push().setValue(travel);
            Toast.makeText(this, "Travel deal saved successfully", Toast.LENGTH_SHORT).show();
        }else {
            databaseReference.child(travel.getId()).setValue(travel);
            Toast.makeText(this, "Travel deal updated successfully", Toast.LENGTH_SHORT).show();
        }
        clearTextFields();
    }

    private void clearTextFields() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
    }

    public void showImage(String url){
        if (url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this)
                    .load(url)
                    .override(width, width * 2/3)
                    .into(imageView);
        }
    }
}
