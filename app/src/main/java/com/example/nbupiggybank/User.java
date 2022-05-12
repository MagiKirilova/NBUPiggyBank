package com.example.nbupiggybank;

import com.google.firebase.firestore.DocumentReference;

public class User {

    public String name, email, iban, card, cardExpiryDate;
    public Double cardAmount;

    public User(String name, String email, String iban, String card, String cardExpiryDate, Double cardAmount){
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.card = card;
        this.cardExpiryDate = cardExpiryDate;
        this.cardAmount = cardAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
