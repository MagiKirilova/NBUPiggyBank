package com.example.nbupiggybank;

public class User {

    public String name, email, iban, card;

    public User(String name, String email, String iban, String card){
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.card = card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
