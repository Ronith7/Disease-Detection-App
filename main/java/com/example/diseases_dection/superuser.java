package com.example.diseases_dection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class superuser extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superuser);

        Button bu=findViewById(R.id.button);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(superuser.this, ViewImagesActivity.class);
                startActivity(intent);
            }
        });
        Button bu1=findViewById(R.id.button1);
        bu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(superuser.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button bu2=findViewById(R.id.button2);
        bu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(superuser.this, breastcancer.class);
                startActivity(intent);
            }
        });
        Button bu3=findViewById(R.id.button3);
        bu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(superuser.this, lung.class);
                startActivity(intent);
            }
        });

    }

}