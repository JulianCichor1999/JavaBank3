package com.company;

class NiewystarczajaceSrodkiException extends Exception {
    public NiewystarczajaceSrodkiException() {
        super("Nie można wypłacić, przekroczyłeś stan konta!");
    }
}
