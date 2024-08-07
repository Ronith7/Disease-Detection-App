package com.example.diseases_dection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diseases_dection.ml.BreastCancerSlightlyModVgg16;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class breastcancer extends AppCompatActivity {

    Button camera, gallery, predictButton;
    ImageView imageView;
    TextView result;
    int imageSize = 224;
    Bitmap selectedImage; // To store the selected image
    StorageReference storageReference;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breastcancer);

        gallery = findViewById(R.id.b_select_img);
        predictButton = findViewById(R.id.b_analyse);
        result = findViewById(R.id.b_test_result);
        imageView = findViewById(R.id.inputImage);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    // Process the selected image for classification
                    classifyImage(selectedImage);
                    displayDiseaseInfo(result.getText().toString().trim());
                }
                else
                    result.setText("Select an Image first");
            }
        });
    }

    private void uploadImage(String name) {

        //progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("Uploading & Predicting Image....");
        //progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("name", name)
//                .setCustomMetadata("probability", probability+"")
                .build();

        storageReference.putFile(imageUri,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ImageView firebaseImageView = findViewById(R.id.inputImage);
                        firebaseImageView.setImageURI(null);

                        //Toast.makeText(chest.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
                        //Toast.makeText(chest.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void classifyImage(Bitmap image) {
        try {
            BreastCancerSlightlyModVgg16 model = BreastCancerSlightlyModVgg16.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            BreastCancerSlightlyModVgg16.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Malignant","Benign"};
            result.setText(classes[maxPos]);
            uploadImage(classes[maxPos]);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Use a different request code, e.g., 1, for gallery selection
                Uri dat = data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
//                    int dimension = Math.min(image.getWidth(), image.getHeight());
//                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                    imageView.setImageBitmap(image);

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    selectedImage = image; // Save the selected image for later use
                    result.setText("Image Selected");

                    // Set the imageUri
                    imageUri = dat;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void displayDiseaseInfo(String disease) {
        String info = getDiseaseInfo(disease);
        result.setText(info);
    }

    private String getDiseaseInfo(String disease) {
        // You can customize this method to fetch information from the web or use predefined information

        switch (disease) {
            case "Malignant":
                return "\nMalignant breast cancer is detected.\nMalignant breast cancer refers to cancerous tumors that can spread to other parts of the body,"+
                        "posing a significant health risk if left untreated.\n\nFor more information, you can visit: https://en.wikipedia.org/wiki/Breast_cancer";

            case "Benign":
                return "\nBenign breast cancer is detected.\nBenign breast cancer, on the other hand,"+
                        "describes non-cancerous tumors that do not invade nearby tissue or spread to distant areas of the body.\n\nFor more information, you can visit: https://en.wikipedia.org/wiki/Benign_tumor";

            default:
                return "\nUnknown disease. Additional information is not available.";
        }
    }
}
