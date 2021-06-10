package com.company;

public enum Producenci {
    VISA, MASTERCARD;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
