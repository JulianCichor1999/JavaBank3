package com.company;

public interface Transakcja {
    //informacja o nawiazaniu polaczenia przed transakcja
    String Polaczenie = "Trwa nawiazywanie polaczenia...";

    //metody abstrakcyjne
    void wyplacanie();
    void wplacanie();
}
