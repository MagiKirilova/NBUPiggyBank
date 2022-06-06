package com.example.nbupiggybank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public TransactionAdapter(Context transactionContext, List<TransactionData> transactionData){
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
               parent,false);
       return new TransactionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        outputAmountString =  "- " + String.valueOf(transactionData.get(position).getOutputAmount());
        inputAmountString = String.valueOf(transactionData.get(position).getOutputAmount());
        holder.transactionDate.setText(transactionData.get(position).getDate());
        holder.transactionName.setText(transactionData.get(position).getNameType());
        holder.transactionOutputAmount.setText(outputAmountString);
        holder.transactionInputAmount.setText(inputAmountString);
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
        transactionBgn = itemView.findViewById(R.id.transactionBGN);
        transactionLayout = itemView.findViewById(R.id.layoutTransactionLayout);
    }
}
