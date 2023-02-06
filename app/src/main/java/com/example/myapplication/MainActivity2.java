package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    ImageView imageView;
    EditText editTextTextPersonName;
    Button saveButton, deleteButton, updateButton;
    Bitmap selectedImage;

    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);



        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")){
            Bitmap background = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_android_black_24dp);
            imageView.setImageBitmap(background);
            editTextTextPersonName.setText("");
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.INVISIBLE);
        }else{
            String name = intent.getStringExtra("name");
            editTextTextPersonName.setText(name);
            firstName = name;
            // bunu update fonksiyonu için alırım.
            int position = intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artImageList.get(position));
            saveButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
        }

    }

    public void saveRecord(View view){

        String artName = editTextTextPersonName.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
         byte[] bytes = outputStream.toByteArray();
        // imageimi buraya koydum.

        ContentValues contentValues = new ContentValues();

        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().insert(ArtContentProvider.CONTENT_URI,contentValues);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void deleteRecord(View view){

        String recordName = editTextTextPersonName.getText().toString();

        getContentResolver().delete(ArtContentProvider.CONTENT_URI,"name=?", new String[]{ recordName });

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void updateRecord(View view){

        String artName = editTextTextPersonName.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        // güncel resmi aldım.
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().update(ArtContentProvider.CONTENT_URI,contentValues,"name=?",new String[]{firstName});
        // firstName e göre güncelle.

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void select(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // izin verilirse

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // geri gelen sonucu alırım.

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){

            Uri image = data.getData();
            // gelen datayı Uriye çeviririm.

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
















