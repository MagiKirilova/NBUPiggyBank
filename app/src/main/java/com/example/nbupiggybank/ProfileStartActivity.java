package com.example.nbupiggybank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileStartActivity extends AppCompatActivity {
    private TextView outputCustomerName;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_start);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Взимане на userId за намиране на правилния документ на потребителя
        outputCustomerName = findViewById(R.id.customerName);
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Търсене на неговото име
        DocumentReference documentReference = database.collection("Users").document(userId);
        documentReference.addSnapshotListener((value, error) -> {
            if(value != null && value.exists())
                outputCustomerName.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("name")).toString());
        });
    }
}