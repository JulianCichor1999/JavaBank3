package com.company;

public class ZeroWyplataException extends Exception {
    public ZeroWyplataException() {
        super("Nie możesz wypłacić 0zł!");
    }
}
