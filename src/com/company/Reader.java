package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Reader {
    public static KartaPlatnicza getKlient(String line) {
        String[] clients = line.split(",");
        String imie = clients[0];
        String nazwisko = clients[1];
        long numerKonta = Long.parseLong(clients[2].replaceAll("\\s+", ""));
        short numerPIN = Short.parseShort(clients[3]);
        double srodki = Double.parseDouble(clients[4]);

        if ("visa".equals(clients[5].toLowerCase())) {
            return new KartaPlatniczaVisa(imie, nazwisko, numerKonta, numerPIN, srodki);
        } else if ("mastercard".equals(clients[5].toLowerCase())) {
            return new KartaPlatniczaMastercard(
                    imie, nazwisko, numerKonta, numerPIN, srodki);
        } else {
            System.out.println("Nie wspieramy Twojej karty!");
        }
        return new KartaPlatnicza();
    }

    public static KartaPlatnicza getKarta(ArrayList<KartaPlatnicza> klienci) {
        Scanner scanner = new Scanner(System.in);
        KartaPlatnicza kartaPlatnicza = new KartaPlatnicza();

        boolean jestKarta = false;
        String nrKarty;
        short pin = -1;

        while (true) {
            System.out.print("Wczytaj swój numer karty: ");
            nrKarty = scanner.nextLine();
            for (KartaPlatnicza kp : klienci) {
                jestKarta = kp.getNumerKarty().equals(nrKarty.replaceAll("\\s+", ""));
                if (jestKarta) {
                    if (kp.getProducentKarty().toString().equals("visa")) {
                        kartaPlatnicza = new KartaPlatniczaVisa(
                                kp.getImie(), kp.getNazwisko(), kp.getNumerKarty(), kp.getPin(), kp.getSrodki());
                    } else if (kp.getProducentKarty().toString().equals("mastercard")) {
                        kartaPlatnicza = new KartaPlatniczaMastercard(
                                kp.getImie(), kp.getNazwisko(), kp.getNumerKarty(), kp.getPin(), kp.getSrodki());
                    }
                    break;
                }
            }
            if (!jestKarta) {
                System.out.println("Nie ma takiej karty w bazie!");
                continue;
            }

            System.out.print("Podaj PIN: ");
            try {
                pin = Short.parseShort(scanner.nextLine());
            } catch (NumberFormatException e) {
//                System.out.println("Podaj 4 cyfrowy kod pin!");
            }

            if (kartaPlatnicza.getPin() == pin) break;
            System.out.println("Błędny kod pin!");
        }

        return kartaPlatnicza;
    }

    public static boolean saveKlienci(ArrayList<KartaPlatnicza> klienci) {
        try {
            FileWriter plik = new FileWriter("klienci.csv");
//        System.out.println("saveKlienci");
            for (KartaPlatnicza klient : klienci) {
                String srodki = String.format("%.2f", klient.getSrodki()).replace(",", ".");
                plik.write(String.format("%s,%s,%s,%d,%s,%s\n",
                        klient.getImie(), klient.getNazwisko(), klient.getNumerKarty(), klient.getPin(),
                        srodki, klient.getProducentKarty().toString()));
            }
            plik.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.print("Wystąpił błąd pliku!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
