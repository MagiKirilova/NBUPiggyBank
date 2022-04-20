package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
// Трябва да проверява дали телефона се намира в базата данни
    // Ако е там, трябва да го премести на страницата в която трябва да въведе парола за да се логне в системата
    // Ако не е там, трябва да го премести на страницата в която трябва да се регистрира
    private Button sendPhoneNumberButton;
    private EditText phoneNumber;
    private FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mAuth = FirebaseAuth.getInstance();

       sendPhoneNumberButton = findViewById(R.id.Continue);
       phoneNumber = findViewById(R.id.phone);

       sendPhoneNumberButton.setOnClickListener(view -> {

           String getPhoneNumber = phoneNumber.getText().toString();
           Intent moveToRegisterVerify = new Intent(getApplicationContext(), PhoneAuth.class);

           moveToRegisterVerify.putExtra("phone_key", getPhoneNumber);

           startActivity(moveToRegisterVerify);
       });
    }

    @Override
    public void onClick(View view) {

    }





   /* @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Register) {
            startActivity(new Intent(this, Register.class));
        }*/
}
