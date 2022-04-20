package com.example.nbupiggybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextPhone, editTextEmail, editTextPassword;

    private FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        TextView registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextName = findViewById(R.id.Name);
        editTextPhone = findViewById(R.id.phone);
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.registerUser) {
            registerUser();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty()) {
            editTextName.setError("Моля въведете вашето име!");
            editTextName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("Моля въведете вашето ЕГН!");
            editTextPhone.requestFocus();
            return;
        }
        if (phone.length() < 10) {
            editTextPhone.setError("Моля въведете 10 символа за вашето ЕГН!");
            editTextPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Моля въведете вашият имейл!");
            editTextEmail.requestFocus();
            return;
        }
        if (!email.matches(emailPattern)) {
            editTextEmail.setError("Моля въведете правилен имейл!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Моля въведете вашата парола!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Дължината на паролата трябва да е не по-малка от 6 символа!");
            editTextPassword.requestFocus();
        }

        User user = new User(name, phone, email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user1 = new User(name, phone, email);

                        database.collection("Users").add(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(Register.this, "Успешна регистрация!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Register.this, "Неуспешна регистрация! Опитай пак!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Register.this, "Неуспешна регистрация!", Toast.LENGTH_LONG).show();
                    }
                });


    }
}