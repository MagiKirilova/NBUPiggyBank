package com.example.nbupiggybank;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Transaction {
    public String date, inputAmount, nameType, outputAmount;
    public final Timestamp timestamp;

    public Transaction(Timestamp timestamp, String date, String inputAmount, String nameType, String outputAmount) {
        this.timestamp = timestamp;
        this.date = date;
        this.inputAmount = inputAmount;
        this.nameType = nameType;
        this.outputAmount = outputAmount;
    }
}
