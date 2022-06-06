package com.example.nbupiggybank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public View view;

    private TextView outputCustomerName, outputCardNumber, outputCardAmount, outputCardExpiryDate;
    private TextView outputCustomerIBAN, outputCustomerEmail, outputCustomerPhone;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Взимане на userId за намиране на правилния документ на потребителя
        outputCustomerName = view.findViewById(R.id.myNameProfile);
        outputCardNumber = view.findViewById(R.id.cardNumber);
        outputCardAmount = view.findViewById(R.id.cardAmount);
        outputCardExpiryDate = view.findViewById(R.id.cardExpiryDate);
        outputCustomerIBAN = view.findViewById(R.id.myIbanProfile);
        outputCustomerEmail = view.findViewById(R.id.myEmailProfile);
        outputCustomerPhone = view.findViewById(R.id.myPhoneProfile);


        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();


        // Търсене на неговото име
        DocumentReference documentReferenceUser = database.collection("Users").document(userId);
        documentReferenceUser.addSnapshotListener((value, error) -> {
            if(value != null && value.exists()) {
                outputCustomerName.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("name")).toString());
                outputCustomerEmail.setText(Objects.requireNonNull(email));
                outputCustomerIBAN.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("iban")).toString());
                String phone = (Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("phoneNumber")).toString());
                outputCustomerPhone.setText(phone);
                outputCardNumber.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("card")).toString());
                outputCardAmount.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardAmount")).toString());
                outputCardExpiryDate.setText(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("cardExpiryDate")).toString());
            }

        });
        // Inflate the layout for this fragment
        return view;
    }
}