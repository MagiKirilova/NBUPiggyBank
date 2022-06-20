package com.example.nbupiggybank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.text.DecimalFormat;
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
    private TextInputLayout inputRecipientName, inputAmountToSend, inputIBAN;
    private Button button;
    private String recipientName, amountToSend, recipientIBAN, inputValue;
    private Spinner spinner;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

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
        inputIBAN = view.findViewById(R.id.recipientIban);
        spinner = view.findViewById(R.id.toWhichAccountSpinner);
        button = view.findViewById(R.id.buttonMakeTransferDemo);

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Адаптер
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerTransaction, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getItemAtPosition(position);
                    String itemText = item.toString();

                    if(itemText.equals("Моята сметка")){
                        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
                        documentReferenceUser.addSnapshotListener((value, error) -> {
                            if (value != null && value.exists()) {
                                inputRecipientName.getEditText().setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("name")).toString());
                                inputIBAN.getEditText().setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("iban")).toString());
                            }

                        });
                    } else {
                        inputRecipientName.getEditText().setText(" ");
                        inputIBAN.getEditText().setText(" ");
                    }

                   /* if(item != null){
                        Toast.makeText(getContext(), item.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                    */

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

                //Взимане на userId за намиране на правилния документ на потребителя
                outputCurrentAmount = view.findViewById(R.id.demoAvailableMoney);
        outputAccount = view.findViewById(R.id.demoFromAccount);


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

        Objects.requireNonNull(inputIBAN.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recipientIBAN = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // Търсене на неговото име
        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
        documentReferenceUser.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                outputAccount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("iban")).toString());
                String currentAmountString = value.getData().get("cardAmount").toString();
                Double doubleInputAmount = Double.valueOf(currentAmountString);
                String doubleFormatAmount = (decimalFormat.format(doubleInputAmount));
                outputCurrentAmount.setText(doubleFormatAmount);
            }

        });

        button.setOnClickListener(v -> {

            if (amountToSend.isEmpty()) {
                inputAmountToSend.setError("Моля въведете сума за изпращане!");
                inputAmountToSend.requestFocus();
                return;
            }

            if (Double.parseDouble(amountToSend) < 0.50) {
                inputAmountToSend.setError("Моля въведете сума правилна за изпращане!");
                inputAmountToSend.requestFocus();
                return;
            }
            if(!(outputAccount.getText().toString()).equals(recipientIBAN)) {
                if (Double.parseDouble(amountToSend) > Double.parseDouble(outputCurrentAmount.getText().toString())) {
                    inputAmountToSend.setError("Моля въведете по-малка от наличната сума за изпращане!");
                    inputAmountToSend.requestFocus();
                    return;
                }
            }

            if (recipientName.isEmpty()) {
                inputRecipientName.setError("Моля въведете вашето име!");
                inputRecipientName.requestFocus();
                return;
            }

            if (recipientName.length() < 2) {
                inputRecipientName.setError("Моля въведете вашето име!");
                inputRecipientName.requestFocus();
                return;
            }

            if (recipientIBAN.isEmpty()) {
                inputIBAN.setError("Моля въведете вашият IBAN!");
                inputIBAN.requestFocus();
                return;
            }

            if (recipientIBAN.length() != 22) {
                inputIBAN.setError("Моля въведете 22 символния си IBAN!");
                inputIBAN.requestFocus();
                return;
            }

            Double doubleInputAmount = Double.valueOf(amountToSend);
            amountToSend = (decimalFormat.format(doubleInputAmount));

            //Добавяне на нова транзакция в базата от данни.
            // Проверява се дали въведения IBAN е този на клиента или чужд
            // Ако е този на клиента сумата се добавя в сметката на клиента.
            // Ако е превод към друга сметка, сумата се изважда от налината сума в демо акаунта.

            Timestamp timestamp = Timestamp.now();

            Date dateNew = timestamp.toDate();
            String dateThis = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY).format(dateNew);


            if ((outputAccount.getText().toString()).equals(recipientIBAN)){
                Transaction transaction = new Transaction(timestamp, dateThis, amountToSend, recipientName, "0.0");
                database.collection("Users").document(userId).collection("transactions").document().set(transaction);

                // Търсене на разполагаемата сума и след това добавяне на сумата за превод и прехвърляна на новата сума в базата
                DocumentReference documentReferenceTransferInput = database.collection("Users").document(userId);
                documentReferenceTransferInput.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Double cardAmount = Double.valueOf(documentSnapshot.getLong("cardAmount"));
                        Double updateAmount = cardAmount + Double.parseDouble(amountToSend);
                        database.collection("Users").document(userId).update("cardAmount", updateAmount);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
            } else {
                Transaction transaction2 = new Transaction(timestamp, dateThis, "0.0", recipientName, amountToSend);
                database.collection("Users").document(userId).collection("transactions").document().set(transaction2);

                // Търсене на разполагаемата сума и след това изваждане на сумата за превод и прехвърляна на новата сума в базата
                DocumentReference documentReferenceTransferOutput = database.collection("Users").document(userId);
                documentReferenceTransferOutput.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Double cardAmount = Double.valueOf(documentSnapshot.getLong("cardAmount"));
                        Double updateAmount = cardAmount - Double.parseDouble(amountToSend);
                        database.collection("Users").document(userId).update("cardAmount", updateAmount);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
            }




            // database.collection("Users").document(userId).update("cardAmount");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, HomeFragment.class, null).commit();

            Toast.makeText(getContext(), "Успешна транзакция!",
                    Toast.LENGTH_SHORT).show();

        });

        // Inflate the layout for this fragment
        return view;
    }




}