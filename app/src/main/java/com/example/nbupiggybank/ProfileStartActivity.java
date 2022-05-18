package com.example.nbupiggybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileStartActivity extends AppCompatActivity {
    private static final String TAG = "transaction + ";
    private TextView outputCustomerName, outputCardNumber, outputCardAmount, outputCardExpiryDate;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private List<TransactionData> transactionDataList = new ArrayList<>();
    private Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_start);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Взимане на userId за намиране на правилния документ на потребителя
        outputCustomerName = findViewById(R.id.customerName);
        outputCardNumber = findViewById(R.id.cardNumber);
        outputCardAmount = findViewById(R.id.cardAmount);
        outputCardExpiryDate = findViewById(R.id.cardExpiryDate);
        recyclerView = findViewById(R.id.recyclerViewTransactions);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileStartActivity.this, LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(linearLayoutManager);



        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        // Търсене на неговото име
        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
        documentReferenceUser.addSnapshotListener((value, error) -> {
            if(value != null && value.exists()) {
                outputCustomerName.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("name")).toString());
                outputCardNumber.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("card")).toString());
                outputCardAmount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardAmount")).toString());
                outputCardExpiryDate.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardExpiryDate")).toString());
            }

        });

/*
        database.collectionGroup("transactions").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // TransactionData transactionData = (TransactionData) queryDocumentSnapshots.toObjects(TransactionData.class);
            // transactionDataList.add(transactionData);
            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

            String result = documentSnapshots.toString();
            Log.i(TAG, "onSuccess: " + result);
        });

 */

        Log.d(TAG,"Current transaction list 2 " + transactionDataList.toString());
        getTransactions();
       // TransactionData transactionData = new TransactionData("Jumbo", "11/05/2022","12.00", "0.00" );
        //transactionDataList.add(transactionData);
        Log.d(TAG,"Transaction List 2 "+ transactionDataList.toString());
       //TransactionAdapter transactionAdapter = new TransactionAdapter(ProfileStartActivity.this,transactionDataList);
        //Log.d(TAG,"Transaction adapter "+ transactionAdapter.toString());
        Log.d(TAG, "Counters : " + count);
        //recyclerView.setAdapter(transactionAdapter);
    }

    private void getTransactions(){
        database.collectionGroup("transactions").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " here => " + document.getData());
                    String nameType = document.getString("nameType");
                    String date = document.getString("date");
                    String inputAmount = Objects.requireNonNull(document.getString("inputAmount"));
                    String outputAmount = Objects.requireNonNull(document.getString("outputAmount"));

                    Log.d(TAG, " Transaction From document: " + nameType + date + inputAmount.toString() + outputAmount.toString());

                    TransactionData transactionData = new TransactionData(date, nameType, outputAmount, inputAmount);
                    Log.d(TAG, " Transaction to be added: " + transactionData.toString());
                    transactionDataList.add(transactionData);
                    Log.d(TAG, "Current transaction list " + transactionDataList.toString());
                }
                Log.d(TAG,"Current transaction list final: " + transactionDataList.toString());
                TransactionAdapter transactionAdapter = new TransactionAdapter(ProfileStartActivity.this,transactionDataList);
                recyclerView.setAdapter(transactionAdapter);
                Log.d(TAG, "Number of items in adapter: " + transactionAdapter.getItemCount());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

           //TransactionAdapter transactionAdapter = new TransactionAdapter(ProfileStartActivity.this,transactionDataList);
           // recyclerView.setAdapter(transactionAdapter);
        });

    }


}