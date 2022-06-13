package com.example.nbupiggybank;

public class TransactionData {

    private final String outputAmount;
    private final String inputAmount;
    private final String date;
    private final String nameType;

    public TransactionData(String nameType, String date, String outputAmount, String inputAmount) {
        this.date = date;
        this.nameType = nameType;
        this.outputAmount = outputAmount;
        this.inputAmount = inputAmount;
    }

    @Override
    public String toString() {
        return "TransactionData{" +
                "outputAmount=" + outputAmount +
                ", inputAmount=" + inputAmount +
                ", date='" + date + '\'' +
                ", nameType='" + nameType + '\'' +
                '}';
    }

    public String getOutputAmount() {
        return outputAmount;
    }

    public String getInputAmount() {
        return inputAmount;
    }

    public String getDate() {
        return date;
    }

    public String getNameType() {
        return nameType;
    }
}
