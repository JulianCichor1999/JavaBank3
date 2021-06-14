package com.company;

import java.util.ArrayList;

public class Tester {
    public static int czyNumerKartyZgadzaSie(String numerKarty, ArrayList<KartaPlatnicza> klienci) {
    boolean jestKarta = false;
    KartaPlatnicza kartaPlatnicza = null;
    int id = 0;
    for (KartaPlatnicza kp : klienci) {
        jestKarta = kp.getNumerKarty().equals(numerKarty.replaceAll("\\s+", ""));
        if (jestKarta) {
            if (kp.getProducentKarty().toString().equals("visa")) {
                kartaPlatnicza = new KartaPlatniczaVisa(
                        kp.getImie(), kp.getNazwisko(), kp.getNumerKarty(), kp.getPin(), kp.getSrodki());
            } else if (kp.getProducentKarty().toString().equals("mastercard")) {
                kartaPlatnicza = new KartaPlatniczaMastercard(
                        kp.getImie(), kp.getNazwisko(), kp.getNumerKarty(), kp.getPin(), kp.getSrodki());
            }
            return id;
        }
        id++;
    }
    return -1;
}
}
