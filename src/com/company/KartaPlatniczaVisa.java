package com.company;

public class KartaPlatniczaVisa extends KartaPlatnicza implements Transakcja{

    public KartaPlatniczaVisa(String imie, String nazwisko, long numerKarty, short PIN, double srodki) {
        super(imie,nazwisko,numerKarty,PIN,srodki);
        setProducentKarty(Producenci.VISA);
    }

    public KartaPlatniczaVisa(String imie, String nazwisko, String numerKarty, short PIN, double srodki) {
        super(imie,nazwisko,numerKarty,PIN,srodki);
        setProducentKarty(Producenci.VISA);
    }

    public String doliczSrodki(double noweSrodki) {
        System.out.println(Polaczenie);
        wplacanie();
        noweSrodki *= 1.05;
        srodki += noweSrodki;
        return String.format("Wpłacono %.2f (doliczono 5%s premii dla posiadaczy karty VISA) pieniędzy do konta\n", noweSrodki, "%");
    }

}
