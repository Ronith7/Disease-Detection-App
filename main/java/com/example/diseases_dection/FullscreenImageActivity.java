// FullscreenImageActivity.java
package com.example.diseases_dection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class FullscreenImageActivity extends AppCompatActivity {

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ImageView fullscreenImageView = findViewById(R.id.fullscreen_image_view);

        // Get the image path from the intent
        byte[] imageData = getIntent().getByteArrayExtra("imageData");

        // Load and set the image into the ImageView using BitmapFactory
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        fullscreenImageView.setImageBitmap(bitmap);
    }

    private void downloadImage() {
        try {
            // Create a directory for the downloaded images
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "DownloadedImages");

            // If the directory doesn't exist, create it
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create a new file in the directory with a unique name
            File file = new File(directory, "downloaded_image.jpg");

            // Copy the original image to the new file
            FileOutputStream out = new FileOutputStream(file);
            Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // Notify the user that the download is successful
            Toast.makeText(this, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle any exceptions that may occur during the download
            e.printStackTrace();
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }
}
