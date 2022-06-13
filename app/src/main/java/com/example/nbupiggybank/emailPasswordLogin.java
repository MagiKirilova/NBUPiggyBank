package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class emailPasswordLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_login);

        Button emailAndPassAuth = findViewById(R.id.verifyEmailAndPass);
        TextInputLayout emailInput = findViewById(R.id.emailLogin);
        TextInputLayout passwordInput = findViewById(R.id.passwordLogin);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);


        Objects.requireNonNull(emailInput.getEditText()).addTextChangedListener(new TextWatcher() {
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

        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(new TextWatcher() {
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

        emailAndPassAuth.setOnClickListener(view -> {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(emailPasswordLogin.this, "Моля въведете правилен имейл!", Toast.LENGTH_SHORT).show();
            } else {
                emailAndPassAuth(email, password);
            }
        });
    }

    public void emailAndPassAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent i;
                i = new Intent(emailPasswordLogin.this, changePhone.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(emailPasswordLogin.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}