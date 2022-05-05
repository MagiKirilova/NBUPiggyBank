package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout inputTextName, inputTextEmail, inputTextPassword;
    private String email;
    private String password;
    private String name;
    private FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        TextView registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        inputTextName = findViewById(R.id.Name);
        inputTextEmail = findViewById(R.id.Email);
        inputTextPassword = findViewById(R.id.Password);

        Objects.requireNonNull(inputTextName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Objects.requireNonNull(inputTextEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Objects.requireNonNull(inputTextPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.registerUser) {
            registerUser();
        }
    }

    private void registerUser() {
        email = email.trim();
        password = password.trim();
        name = name.trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty()) {
            inputTextName.setError("Моля въведете вашето име!");
            inputTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            inputTextEmail.setError("Моля въведете вашият имейл!");
            inputTextEmail.requestFocus();
            return;
        }
        if (!email.matches(emailPattern)) {
            inputTextEmail.setError("Моля въведете правилен имейл!");
            inputTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            inputTextPassword.setError("Моля въведете вашата парола!");
            inputTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            inputTextPassword.setError("Дължината на паролата трябва да е не по-малка от 6 символа!");
            inputTextPassword.requestFocus();
        }

        // Добавяне на имейл като допълнителна автентикация
        // И съшо така добавяне на регистрирания потребител в collection Users
        AuthCredential credentialEmail = EmailAuthProvider.getCredential(email, password);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credentialEmail)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        assert user != null;
                        User userToAdd = new User(name, email);
                        String userid = user.getUid();

                        database.collection("Users").document(userid).set(userToAdd);
                    } else {
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}