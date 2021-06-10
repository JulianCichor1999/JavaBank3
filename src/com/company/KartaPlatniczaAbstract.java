package com.company;

abstract public class KartaPlatniczaAbstract extends Osoba implements Transakcja {

    public KartaPlatniczaAbstract(String imie, String nazwisko) {
        super(imie, nazwisko);
    }

    @Override
    public void wyplacanie() {
        System.out.println("Trwa wypłacanie gotówki. ");
    }

    @Override
    public void wplacanie() {
        System.out.println("Oczekiwanie na wpłatę gotówki.");
    }
}
