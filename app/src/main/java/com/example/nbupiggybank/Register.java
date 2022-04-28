package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextEmail, editTextPassword;

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
        String name = editTextName.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty()) {
            editTextName.setError("Моля въведете вашето име!");
            editTextName.requestFocus();
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

        // Добавяне на имейл като допълнителна автентикация
        // И съшо така добавяне на регистрирания потребител в collection Users
        AuthCredential credentialEmail = EmailAuthProvider.getCredential(email, password);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credentialEmail)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        assert user != null;
                        User userToAdd = new User(name, email, password);
                        String userid = user.getUid();

                        database.collection("Users").document(userid).set(userToAdd);
                    } else {
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}