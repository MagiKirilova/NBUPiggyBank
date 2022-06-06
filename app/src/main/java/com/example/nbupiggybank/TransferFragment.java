package com.example.nbupiggybank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {

    //Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "";

    public View view;

    private TextView outputCurrentAmount, outputAccount;
    private TextInputLayout inputRecipientName, inputAmountToSend;
    private Button button;
    private String recipientName, amountToSend;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    //Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransferFragment.
     */
    //Rename and change types and number of parameters
    public static TransferFragment newInstance(String param1, String param2) {
        TransferFragment fragment = new TransferFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        inputRecipientName = view.findViewById(R.id.recipientName);
        inputAmountToSend = view.findViewById(R.id.amountToSend);

        button = view.findViewById(R.id.buttonMakeTransferDemo);


        //Взимане на userId за намиране на правилния документ на потребителя
        outputCurrentAmount = view.findViewById(R.id.demoAvailableMoney);
        outputAccount = view.findViewById(R.id.demoFromAccount);


        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Objects.requireNonNull(inputRecipientName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recipientName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Objects.requireNonNull(inputAmountToSend.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amountToSend = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        // Търсене на неговото име
        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
        documentReferenceUser.addSnapshotListener((value, error) -> {
            if(value != null && value.exists()) {
                outputAccount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("iban")).toString());
                outputCurrentAmount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardAmount")).toString());
            }

        });

        button.setOnClickListener(v -> {

            Timestamp timestamp = Timestamp.now();

            Date dateNew = timestamp.toDate();
            String dateThis = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY).format(dateNew);

            Transaction transaction = new Transaction(timestamp, dateThis, "0.0", recipientName, amountToSend);
            database.collection("Users").document(userId).collection("transactions").document().set(transaction);

            // Търсене на разполагаемата сума и след това изваждане на сумата за превод и прехвърляна на новата сума в базата
            DocumentReference documentReferenceTransfer = database.collection("Users").document(userId);
           documentReferenceTransfer.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Double cardAmount = Double.valueOf(documentSnapshot.getLong("cardAmount"));
                    Double updateAmount = cardAmount - Double.parseDouble(amountToSend);
                    database.collection("Users").document(userId).update("cardAmount", updateAmount);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
           });

           // database.collection("Users").document(userId).update("cardAmount");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, HomeFragment.class , null).commit();

            Toast.makeText(getContext(), "Успешна транзакция!",
                    Toast.LENGTH_SHORT).show();

        });
        // Inflate the layout for this fragment
        return view;
    }


}