package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button continueButton = findViewById(R.id.Continue);

        continueButton.setOnClickListener(view -> {
            Intent moveToRegisterVerify = new Intent(getApplicationContext(), PhoneAuth.class);
            startActivity(moveToRegisterVerify);
        });
    }
}

