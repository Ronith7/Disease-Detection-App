package com.example.diseases_dection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        EditText editTextEmail = findViewById(R.id.editText_email);
        EditText editTextPassword = findViewById(R.id.editText_password);
        Button buttonRegister = findViewById(R.id.button_register);
        TextView text1 = findViewById(R.id.text1);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                registerUser(email, password);
            }
        });
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register.this, super_login.class);
                startActivity(intent);
            }
        });

        SpannableString spannableString = new SpannableString("Already Registered? Login Here!");
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.BLUE);
        spannableString.setSpan(blueColorSpan, 20, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text1.setText(spannableString);
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // You can navigate to another activity or perform any other action here
                        } else {
                            // Registration failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(register.this, "Registration failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
