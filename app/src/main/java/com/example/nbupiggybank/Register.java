package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout inputTextName, inputTextEmail, inputTextPassword;
    private String email;
    private String password;
    private String name;
    private String iban;
    private String card;
    private String cardExpiryDate;
    private Double cardAmount;
    private FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    HashMap<String, String> accountInfo = new HashMap<>();
    HashMap<String, Double> cardInfo = new HashMap<>();

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

        createAccountInfo(accountInfo);
        getIbanAndCard(accountInfo);

        createCardInfo(cardInfo);
        getExpiryDateAndAmount(cardInfo);


        // Добавяне на имейл като допълнителна автентикация
        // И съшо така добавяне на регистрирания потребител в collection Users
        AuthCredential credentialEmail = EmailAuthProvider.getCredential(email, password);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credentialEmail)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        assert user != null;
                        User userToAdd = new User(name, email, iban, card, cardExpiryDate, cardAmount);
                        String userid = user.getUid();

                        database.collection("Users").document(userid).set(userToAdd);

                        Intent i;
                        i = new Intent(Register.this, ProfileStartActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Register.this, "Изникна проблем с регистрацията!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
    // При регистриране на потребител се взима 1 от 3-те акаунта
    private void getIbanAndCard(HashMap<String, String> accountInfo){
       Random random = new Random();
       List<String> keys = new ArrayList<>(accountInfo.keySet());
       iban = keys.get(random.nextInt(keys.size()));
       card = accountInfo.get(iban);
    }


    // Понеже приложението е демо, за сега има само 3 акаунта с по 1 карта вътре. Данните не са инстински
    private void createAccountInfo(HashMap<String, String> accountInfo){
        accountInfo.put("BG09PBBB94007754115719","4062********3156");
        accountInfo.put("BG22PBBB91551177529236","4894********1499");
        accountInfo.put("BG95PBBB94006116647374","4893********8705");
    }

    private void getExpiryDateAndAmount(HashMap<String, Double> cardInfo){
        Random random = new Random();
        List<String> keys = new ArrayList<>(cardInfo.keySet());
        cardExpiryDate = keys.get(random.nextInt(keys.size()));
        cardAmount = cardInfo.get(cardExpiryDate);
    }

    private void createCardInfo(HashMap<String, Double> cardInfo){
        cardInfo.put("05/23", 456.78);
        cardInfo.put("09/24", 532.25);
        cardInfo.put("01/26", 498.55);
    }
}