package com.example.nbupiggybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextView banner,registerUser;
    private EditText editTextName, editTextEGN, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextName =  findViewById(R.id.Name);
        editTextEGN = findViewById(R.id.Egn);
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);

        //progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }
    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String egn = editTextEGN.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(name.isEmpty()){
            editTextName.setError("Моля въведете вашето име!");
            editTextName.requestFocus();
            return;
        }
        if(egn.isEmpty()){
            editTextEGN.setError("Моля въведете вашето ЕГН!");
            editTextEGN.requestFocus();
            return;
        }
        if(egn.length() < 10){
            editTextEGN.setError("Моля въведете 10 символа за вашето ЕГН!");
            editTextEGN.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Моля въведете вашият имейл!");
            editTextEmail.requestFocus();
            return;
        }
        if(!email.matches(emailPattern)){
            editTextEmail.setError("Моля въведете правилен имейл!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Моля въведете вашата парола!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("Дължината на паролата трябва да е не по-малка от 6 символа!");
            editTextPassword.requestFocus();
        }

        User user = new User(name, egn, email);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        User user1 = new User(name, egn, email);

                        database.collection("Users").add(user)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(Register.this, "Успешна регистрация!", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(Register.this, "Неуспешна регистрация! Опитай пак!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }else{
                        Toast.makeText(Register.this, "Неуспешна регистрация!", Toast.LENGTH_LONG).show();
                    }
                });


    }
}