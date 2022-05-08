package com.example.nbupiggybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class changePhone extends AppCompatActivity {
    private String newPhoneNumber, newPhoneCode, verificationId;
    private TextInputLayout newPhoneCodeInput;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        TextInputLayout newPhoneNumberInput = findViewById(R.id.newPhoneNumber);
        TextInputLayout newPhoneCodeInput= findViewById(R.id.newPhoneCode);
        Button sendCodeButton = findViewById(R.id.sendCodeForNewPhoneNumber);
        Button changePhoneButton = findViewById(R.id.changePhone);

        changePhoneButton.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        Objects.requireNonNull(newPhoneNumberInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newPhoneNumber = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Objects.requireNonNull(newPhoneCodeInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newPhoneCode = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendCodeButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(newPhoneNumber)) {
                Toast.makeText(changePhone.this, "Моля въведете правилен телефонен номер!", Toast.LENGTH_SHORT).show();
            } else {
                // Телефонът започва с +1 за момента, трябва да се оправи!!!
                String phone = "+1" + newPhoneNumber;
                sendVerificationCode(phone);
                changePhoneButton.setVisibility(View.VISIBLE);
            }
        });

        changePhoneButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(newPhoneCode)) {
                Toast.makeText(changePhone.this, "Моля въведете вашия код!", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(newPhoneCode);
            }
        });

    }

    private void updatePhoneNumber(PhoneAuthCredential credential) {
        Objects.requireNonNull(mAuth.getCurrentUser()).updatePhoneNumber(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i;
                        i = new Intent(changePhone.this, ProfileStartActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(changePhone.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        // Метод за изпращане на код към телефонен номер
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // Изпращане на кода към Firebase
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // Кодът се свързва с verificationId за създаване на credential, който ни трябва за автентикация
            verificationId = s;
        }

        // Верификацията на кода е успешна и може да стане веднага
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Objects.requireNonNull(newPhoneCodeInput.getEditText()).setText(code);
                // Функцията за верифициране на кода
                verifyCode(code);
            }
        }

        // Ако има проблем с верификацията
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(changePhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // Верифициране на кода от Firebase.
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        updatePhoneNumber(credential);
    }

}