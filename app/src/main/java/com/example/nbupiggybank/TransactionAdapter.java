package com.example.nbupiggybank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
    private final List<TransactionData> transactionData;
    private final Context transactionContext;
    private final String currency = "BGN";
    private String outputAmountString, inputAmountString;


    public TransactionAdapter(Context transactionContext, List<TransactionData> transactionData) {
        this.transactionContext = transactionContext;
        this.transactionData = transactionData;
    }

    @Override
    public String toString() {
        return "TransactionAdapter{" +
                "transactionData=" + transactionData +
                ", transactionContext=" + transactionContext +
                ", currency='" + currency + '\'' +
                ", outputAmountString='" + outputAmountString + '\'' +
                ", inputAmountString='" + inputAmountString + '\'' +
                '}';
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_transactions_item,
                parent, false);
        return new TransactionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // proverka za input/output i promqna na cvqt \ ili s gone i visible
        outputAmountString =String.valueOf(transactionData.get(position).getOutputAmount());
        inputAmountString =String.valueOf(transactionData.get(position).getInputAmount());

        if (inputAmountString.isEmpty()) inputAmountString ="0.0";
        if (outputAmountString.isEmpty()) outputAmountString ="0.0";


        if(outputAmountString.equals("0.0")){
            holder.transactionInputAmount.setVisibility(View.VISIBLE);
            holder.transactionOutputAmount.setVisibility(View.GONE);
            String inputPlus = "+" + inputAmountString;
            holder.transactionInputAmount.setText(inputPlus);

        } else {
            holder.transactionOutputAmount.setVisibility(View.VISIBLE);
            holder.transactionInputAmount.setVisibility(View.GONE);
            String outputMinus = "-" + outputAmountString;
            holder.transactionOutputAmount.setText(outputMinus);
        }

        holder.transactionDate.setText(transactionData.get(position).getDate());
        holder.transactionName.setText(transactionData.get(position).getNameType());
        holder.transactionBgn.setText(currency);


    }


    @Override
    public int getItemCount() {

        return transactionData.size();
    }
}

class TransactionViewHolder extends RecyclerView.ViewHolder {
    ImageView transactionIcon;
    TextView transactionDate;
    TextView transactionName;
    TextView transactionOutputAmount;
    TextView transactionInputAmount;
    TextView transactionBgn;
    RelativeLayout transactionLayout;


    TransactionViewHolder(View itemView) {
        super(itemView);

        transactionIcon = itemView.findViewById(R.id.redPandaIcon);
        transactionDate = itemView.findViewById(R.id.transactionDate);
        transactionName = itemView.findViewById(R.id.transactionName);
        transactionOutputAmount = itemView.findViewById(R.id.amountOutput);
        transactionInputAmount = itemView.findViewById(R.id.amountInput);
        transactionInputAmount.setVisibility(View.GONE);
        transactionOutputAmount.setVisibility(View.GONE);
        transactionBgn = itemView.findViewById(R.id.transactionBGN);
        transactionLayout = itemView.findViewById(R.id.layoutTransactionLayout);
    }
}
