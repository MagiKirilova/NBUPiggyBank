package com.example.nbupiggybank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public View view;

    private TextView outputCustomerName, outputCardAmount;
    private static final String TAG = "transaction + ";
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private final List<TransactionData> transactionDataList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        outputCustomerName = view.findViewById(R.id.homeCustomerName);
        outputCardAmount = view.findViewById(R.id.homeCardAmount);
        recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(linearLayoutManager);

        transactionDataList.clear();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Търсене на неговото име
        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
        documentReferenceUser.addSnapshotListener((value, error) -> {
            if(value != null && value.exists()) {
                outputCustomerName.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("name")).toString());
                outputCardAmount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardAmount")).toString());
            }

        });

        getTransactions(userId);


        return view;
    }

    private void getTransactions(String userId){
        database.collection("Users").document(userId).collection("transactions").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " here => " + document.getData());
                    String nameType = document.getString("nameType");
                    String date = document.getString("date");
                    String inputAmount = Objects.requireNonNull(document.getString("inputAmount"));
                    String outputAmount = Objects.requireNonNull(document.getString("outputAmount"));

                    TransactionData transactionData = new TransactionData(nameType, date, outputAmount, inputAmount);
                    transactionDataList.add(transactionData);
                }
                TransactionAdapter transactionAdapter = new TransactionAdapter(getContext(),transactionDataList);
                recyclerView.setAdapter(transactionAdapter);
                Log.d(TAG, "Number of items in adapter: " + transactionAdapter.getItemCount());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }


        });

    }
}