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

public class PhoneAuth extends AppCompatActivity {
    private TextInputLayout phoneVerificationCodeInput;
    private String phoneNumber, phoneVerificationCode;
    private FirebaseAuth mAuth;
    private String verificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_auth_activity);

        TextInputLayout phoneNumberInput = findViewById(R.id.phoneNumberEdit);
        phoneVerificationCodeInput = findViewById(R.id.phoneCode);
        Button sendCodeButton = findViewById(R.id.phoneAuthButton);
        Button verifyButton = findViewById(R.id.verifyPhone);
        Button newPhoneButton = findViewById(R.id.cannotLogIn);

        verifyButton.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        Objects.requireNonNull(phoneNumberInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneNumber = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Objects.requireNonNull(phoneVerificationCodeInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneVerificationCode = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendCodeButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(PhoneAuth.this, "Моля въведете правилен телефонен номер!", Toast.LENGTH_SHORT).show();
            } else {
                // Телефонът започва с +1 за момента, трябва да се оправи!!!
                String phone = "+1" + phoneNumber;
                sendVerificationCode(phone);
                verifyButton.setVisibility(View.VISIBLE);
            }
        });

        // Взима се кодът въведен в полето и се проверява дали е празно
        verifyButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phoneVerificationCode)) {
                Toast.makeText(PhoneAuth.this, "Моля въведете вашия код!", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(phoneVerificationCode);
            }
        });

        newPhoneButton.setOnClickListener(v -> {
            Intent i;
            i = new Intent(PhoneAuth.this, emailPasswordLogin.class);
            startActivity(i);
            finish();
        });
    }


    // Проверка дали кодът е верен и изпращане на новите потребители към Регистрация.
    // И изпращане на регистрираните потребители към ProfileStartActivity
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    boolean newUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                    if (task.isSuccessful()) {
                        Intent i;
                        if (newUser) {
                            i = new Intent(PhoneAuth.this, Register.class);
                        } else {
                            i = new Intent(PhoneAuth.this, ProfileStartActivity.class);
                        }
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(PhoneAuth.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
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
                Objects.requireNonNull(phoneVerificationCodeInput.getEditText()).setText(code);
                // Функцията за верифициране на кода
                verifyCode(code);
            }
        }

        // Ако има проблем с верификацията
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // Верифициране на кода от Firebase.
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

}
