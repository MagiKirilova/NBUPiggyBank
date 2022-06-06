package com.example.nbupiggybank;


public class User {

    public String name, email, iban, card, cardExpiryDate, phone;
    public Double cardAmount;

    public User(String name, String email, String iban, String card, String cardExpiryDate, Double cardAmount, String phone){
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.card = card;
        this.cardExpiryDate = cardExpiryDate;
        this.cardAmount = cardAmount;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
