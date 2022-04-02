package com.example.nbupiggybank;

public class User {

    public String name,egn,email;

    public User(){

    }
    public User(String name, String egn, String email){
        this.name = name;
        this.egn = egn;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEgn() {
        return egn;
    }

    public void setEgn(String egn) {
        this.egn = egn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
