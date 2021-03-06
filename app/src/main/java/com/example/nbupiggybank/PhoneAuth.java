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
                Toast.makeText(PhoneAuth.this, "???????? ???????????????? ???????????????? ?????????????????? ??????????!", Toast.LENGTH_SHORT).show();
            } else {
                // ?????????????????? ?????????????? ?? +1 ???? ??????????????, ???????????? ???? ???? ????????????!!!
                String phone = "+1" + phoneNumber;
                sendVerificationCode(phone);
                verifyButton.setVisibility(View.VISIBLE);
            }
        });

        // ?????????? ???? ?????????? ?????????????? ?? ???????????? ?? ???? ?????????????????? ???????? ?? ????????????
        verifyButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phoneVerificationCode)) {
                Toast.makeText(PhoneAuth.this, "???????? ???????????????? ?????????? ??????!", Toast.LENGTH_SHORT).show();
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


    // ???????????????? ???????? ?????????? ?? ?????????? ?? ?????????????????? ???? ???????????? ?????????????????????? ?????? ??????????????????????.
    // ?? ?????????????????? ???? ???????????????????????????? ?????????????????????? ?????? ProfileStartActivity
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    boolean newUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                    if (task.isSuccessful()) {
                        Intent i;
                        if (newUser) {
                            i = new Intent(PhoneAuth.this, Register.class);
                            i.putExtra("phoneNumber", phoneNumber);
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
        // ?????????? ???? ?????????????????? ???? ?????? ?????? ?????????????????? ??????????
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

        // ?????????????????? ???? ???????? ?????? Firebase
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // ?????????? ???? ?????????????? ?? verificationId ???? ?????????????????? ???? credential, ?????????? ???? ???????????? ???? ????????????????????????
            verificationId = s;
        }

        // ?????????????????????????? ???? ???????? ?? ?????????????? ?? ???????? ???? ?????????? ??????????????
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Objects.requireNonNull(phoneVerificationCodeInput.getEditText()).setText(code);
                // ?????????????????? ???? ???????????????????????? ???? ????????
                verifyCode(code);
            }
        }

        // ?????? ?????? ?????????????? ?? ??????????????????????????
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // ???????????????????????? ???? ???????? ???? Firebase.
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

}
