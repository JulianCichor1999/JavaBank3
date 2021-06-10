package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/*
to do
okno potwierdzenia zamkniecia - gdy nacisnie sie nie -> zamyka okno
dodac akcje do przyciskow opcje

 */

public class Main extends JFrame implements Runnable {
    private JMenuBar menuGlowne;
    private JMenu plik;
    private JMenuItem miWyloguj, miZamknij;


    private JLabel labelPrzywitanieKarta;
    private JLabel labelPodajNrKarty;
    private JLabel labelPodajPin;
    private JLabel labelPrzywitanieInfoNrKarty;
    private JLabel labelBledneDane;
    private JLabel labelPowitaniePoImieniu;
    private JLabel labelWyswietlanieSrodkow;
    private JLabel labelWyplata;
    private JLabel labelWplata;

    private JTextField textNumerKartyPole;
    private JTextField textPinPole;
    private JTextField textWyplacanePieniadze;
    private JTextField textWplacanePieniadze;

    private JButton buttonPotwierdzenie;
    private JButton buttonWyswietlSrodki;
    private JButton buttonWyplacPieniadze;
    private JButton buttonWplacPieniadze;
    private JButton buttonWyloguj;
    private JButton buttonWyplac;
    private JButton buttonPowrot;
    private JButton buttonPowrotPIN;

    private ImageIcon karta;


    private int numerAktywnegoPanelu;
    private JPanel panelAktywny;

    ArrayList<KartaPlatnicza> klienci;
    int aktywnaKarta = -1;
//    KartaPlatnicza kartaPlatnicza;



    public static void main(String[] args) {
        EventQueue.invokeLater(new Main("Bank"));
    }

    public Main(String title) {
//        wstepne ustawienia okna
        super(title);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        setSize(new Dimension(640, 440));
        setLocation(dim.width / 4, dim.height / 4);
        setContentPane(new JPanel());

//        wczytanie kart
        klienci = Reader.getKlienci();

//        zamykanie okna
        WindowClosingListener windowClosingListener = new WindowClosingListener();
        addWindowListener(windowClosingListener);

//        akcje menu oraz skroty klawiszowe
        CloseAction closeAction = new CloseAction();
        LogoutAction logoutAction = new LogoutAction();

//        pasek menu na górze
        menuGlowne = new JMenuBar();
        plik = new JMenu("Ustawienia");
        miWyloguj = new JMenuItem(logoutAction);
        miZamknij = new JMenuItem(closeAction);

        setJMenuBar(menuGlowne);
        menuGlowne.add(plik);
        plik.add(miWyloguj);
        plik.add(miZamknij);

//        elemety do paneli poczatkowych
        karta = new ImageIcon("citi-simplicity-300x194.png");
        labelPrzywitanieKarta = new JLabel();
        labelPrzywitanieKarta.setIcon(karta);
        textNumerKartyPole = new JTextField(10);
        textPinPole = new JTextField(4);
        textWyplacanePieniadze = new JTextField();
        buttonPotwierdzenie = new JButton("Potwierdź");
        buttonWyplac = new JButton("Wypłać");
        labelPodajNrKarty = new JLabel("Witamy w banku! Podaj swój numer karty płatniczej!");
        labelPodajPin = new JLabel("Podaj PIN:");
        labelBledneDane = new JLabel("Podałeś błędne dane!");
        labelPrzywitanieInfoNrKarty = new JLabel();
        labelWyplata = new JLabel();
        labelWplata = new JLabel();
        textWplacanePieniadze = new JTextField();
        buttonWyplac = new JButton("Wpłać");
        buttonPowrot = new JButton("Powrót");
        buttonPowrotPIN = new JButton("Powrót");


//        elementy do panelu opcje
        labelPowitaniePoImieniu = new JLabel("");
        labelWyswietlanieSrodkow = new JLabel("");
        buttonWyswietlSrodki = new JButton("Wyświetl środki");
        buttonWyplacPieniadze = new JButton("Wypłać pieniądze");
        buttonWplacPieniadze = new JButton("Wpłać pieniądze");
        buttonWyloguj = new JButton("Wyloguj się");

//        inicjowanie panelu
        panelAktywny = new JPanel();
        BoxLayout layoutPowitalny = new BoxLayout(panelAktywny,BoxLayout.Y_AXIS);
        panelAktywny.setLayout(layoutPowitalny);

        changePanel(1);

        add(panelAktywny);

        buttonPotwierdzenie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (numerAktywnegoPanelu == 1) {
                    aktywnaKarta = KartaPlatnicza.czyNumerKartyZgadzaSie(textNumerKartyPole.getText(), klienci);


//                  gdy uzytkownik podal błędny nr karty
                    if (aktywnaKarta == -1) {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono karty o podanym numerze w naszym banku.", "Brak karty", JOptionPane.ERROR_MESSAGE);
                        //changePanel(1, "Nie ma takiej karty w bazie!");
                        return;
                    }

//                  gdy uzytkownik podal poprawnie nr karty
                    changePanel(2);

                } else if (numerAktywnegoPanelu == 2) {
                    if (textPinPole.getText().length() == 0) return;

//                  gdy użytkownik podał poprawny kod PIN
                    if (klienci.get(aktywnaKarta).getPIN() == Short.parseShort(textPinPole.getText())) {
                        changePanel(3);
                        return;
                    }

//                  gdy uzytkownik podal bledny PIN
                    //changePanel(1, "Podałeś błędny PIN!");
                    JOptionPane.showMessageDialog(null, "Wprowadzono niepoprawny numer PIN.", "Błąd uwierzytelnienia", JOptionPane.ERROR_MESSAGE);
                }
                else if (numerAktywnegoPanelu == 3) {
                    changePanel(1);
                } else if (numerAktywnegoPanelu == 5) {
                    try {
                        klienci.get(aktywnaKarta).wyplacPieniadze(Float.parseFloat(textWyplacanePieniadze.getText()));
                        JOptionPane.showMessageDialog(null, String.format("Wypłacono %szł", textWyplacanePieniadze.getText()), "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Trwa nawiązywanie połączenia...", "Informacja", JOptionPane.INFORMATION_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Trwa wypłacanie gotówki...", "Informacja", JOptionPane.INFORMATION_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Transakcja zakończona pomyślnie.", "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
                        //Info o zakończeniu wypłaty pasuje jeszcze troszki poprawić

                    } catch (NiewystarczajaceSrodkiException niewystarczajaceSrodkiException) {
                        niewystarczajaceSrodkiException.printStackTrace();
                    } catch (ZeroWyplataException zeroWyplataException) {
                        zeroWyplataException.printStackTrace();
                    } catch (NumberFormatException err) {
                        JOptionPane.showMessageDialog(null, "Wprowadziłeś niepoprawne dane!", "Informacja", JOptionPane.ERROR_MESSAGE);
                        System.out.println(err.toString());
                        return;
                    }
                    changePanel(3);
                }
                else if (numerAktywnegoPanelu == 6) {
                    try {
                        String kwotaWplaty = klienci.get(aktywnaKarta).doliczSrodki(Float.parseFloat(textWplacanePieniadze.getText()));
                        JOptionPane.showMessageDialog(null, kwotaWplaty, "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Trwa nawiązywanie połączenia...", "Informacja", JOptionPane.INFORMATION_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Oczekiwanie na wpłatę gotówki...", "Informacja", JOptionPane.INFORMATION_MESSAGE);
//                        JOptionPane.showMessageDialog(null, "Transakcja zakończona pomyślnie.", "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
                        //Info o zakończeniu wpłaty też pasuje jeszcze troszki poprawić
                    } catch (NumberFormatException err) {
                        JOptionPane.showMessageDialog(null, "Wprowadziłeś niepoprawne dane!", "Informacja", JOptionPane.ERROR_MESSAGE);
                        System.out.println(err.toString());
                        return;
                    }

                    changePanel(3);
                }
                else {
                    changePanel(3);
                }
            }
        });

        buttonPowrot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(3);
            }
        });

        buttonPowrotPIN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(1);
            }
        });

        buttonWyswietlSrodki.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(4); }
        });

        buttonWyplacPieniadze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(5); }
        });

        buttonWplacPieniadze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(6); }
        });

        buttonWyloguj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(1);

                textNumerKartyPole.setText("");
                textPinPole.setText("");}
        });
    }

    @Override
    public void run() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    private JFrame getMainWindow() { return this; }

    private void changePanel(int stage) { changePanel(stage, null); }

    private void changePanel(int stage, String err) {
        panelAktywny.remove(labelBledneDane);
        /*
            stage:
            1 (default): panel do podania nr karty
            2: panel do podania nr pin
            3: panel z wyborem opcji
            4: panel wyświetl środki
            5: panel wypłać pieniądze
            6: panel wpłać pieniądze
         */
        if (stage == 2) {
            numerAktywnegoPanelu = 2;

            panelAktywny.remove(labelPodajNrKarty);
            panelAktywny.remove(textNumerKartyPole);
            panelAktywny.remove(labelBledneDane);
            panelAktywny.remove(labelPodajNrKarty);
            panelAktywny.remove(textNumerKartyPole);
            panelAktywny.remove(labelBledneDane);


            labelPrzywitanieInfoNrKarty = new JLabel(String.format("Numer karty: %s", textNumerKartyPole.getText()));
            panelAktywny.add(labelPrzywitanieInfoNrKarty);
            panelAktywny.add(labelPodajPin);
            panelAktywny.add(textPinPole);

            panelAktywny.add(buttonPotwierdzenie);
            panelAktywny.add(buttonPowrotPIN);
        } else if (stage == 3) {
            numerAktywnegoPanelu = 3;

            panelAktywny.remove(labelPrzywitanieKarta);
            panelAktywny.remove(labelPrzywitanieInfoNrKarty);
            panelAktywny.remove(labelPodajPin);
            panelAktywny.remove(textPinPole);
            panelAktywny.remove(buttonPotwierdzenie);
            panelAktywny.remove(labelWyswietlanieSrodkow);
            panelAktywny.remove(labelWyplata);
            panelAktywny.remove(textWyplacanePieniadze);
            panelAktywny.remove(labelWplata);
            panelAktywny.remove(textWplacanePieniadze);
            panelAktywny.remove(buttonPowrot);
            panelAktywny.remove(buttonPowrotPIN);

            labelPowitaniePoImieniu = new JLabel(
                    String.format("Sz. P. %s %s", klienci.get(aktywnaKarta).getImie(), klienci.get(aktywnaKarta).getNazwisko()));
            panelAktywny.add(labelPowitaniePoImieniu);
            panelAktywny.add(buttonWyswietlSrodki);
            panelAktywny.add(buttonWyplacPieniadze);
            panelAktywny.add(buttonWplacPieniadze);
            panelAktywny.add(buttonWyloguj);

        } else if (stage == 4) {
            numerAktywnegoPanelu = 4;

            panelAktywny.remove(labelPowitaniePoImieniu);
            panelAktywny.remove(buttonWyswietlSrodki);
            panelAktywny.remove(buttonWyplacPieniadze);
            panelAktywny.remove(buttonWplacPieniadze);
            panelAktywny.remove(buttonWyloguj);
            labelWyswietlanieSrodkow = new JLabel(
                    String.format("Masz %.2f pieniędzy na koncie!", klienci.get(aktywnaKarta).srodki));
            panelAktywny.add(labelWyswietlanieSrodkow);
            //panelAktywny.add(buttonPotwierdzenie);
            panelAktywny.add(buttonPowrot);

        } else if (stage == 5) {
            numerAktywnegoPanelu = 5;

            panelAktywny.remove(labelPowitaniePoImieniu);
            panelAktywny.remove(buttonWyswietlSrodki);
            panelAktywny.remove(buttonWyplacPieniadze);
            panelAktywny.remove(buttonWplacPieniadze);
            panelAktywny.remove(buttonWyloguj);
            labelWyplata = new JLabel(
                    String.format("Ile chcesz wypłacić pieniędzy:"));
            textWyplacanePieniadze = new JTextField();
            buttonWyplac = new JButton("Wypłać");
//            buttonWyplac.addActionListener(klienci.get(aktywnaKarta).wyplacPieniadze(Float.parseFloat(textWyplacanePieniadze.getText())));
            panelAktywny.add(labelWyplata);
            panelAktywny.add(textWyplacanePieniadze);
            panelAktywny.add(buttonPotwierdzenie);
            panelAktywny.add(buttonPowrot);

        } else if (stage == 6) {
            numerAktywnegoPanelu = 6;

            panelAktywny.remove(labelPowitaniePoImieniu);
            panelAktywny.remove(buttonWyswietlSrodki);
            panelAktywny.remove(buttonWyplacPieniadze);
            panelAktywny.remove(buttonWplacPieniadze);
            panelAktywny.remove(buttonWyloguj);
            labelWplata = new JLabel(
                    String.format("Ile chcesz wpłacić pieniędzy:"));
            textWplacanePieniadze = new JTextField();

            /*if(textWplacanePieniadze.getText()=="")
            {
                JOptionPane.showMessageDialog(null,
                        "Eggs are not supposed to be green.",
                        "Inane warning",
                        panelAktywny.getWindow().WARNING_MESSAGE);
            }*/

            buttonWyplac = new JButton("Wpłać");
            panelAktywny.add(labelWplata);
            panelAktywny.add(textWplacanePieniadze);
            panelAktywny.add(buttonPotwierdzenie);
            panelAktywny.add(buttonPowrot);

        } else {
            numerAktywnegoPanelu = 1;

            panelAktywny.remove(labelPrzywitanieInfoNrKarty);
            panelAktywny.remove(labelPodajPin);
            panelAktywny.remove(textPinPole);

            panelAktywny.remove(labelPowitaniePoImieniu);
            panelAktywny.remove(buttonWyswietlSrodki);
            panelAktywny.remove(buttonWyplacPieniadze);
            panelAktywny.remove(buttonWplacPieniadze);
            panelAktywny.remove(buttonWyloguj);
            panelAktywny.remove(buttonPowrotPIN);

            panelAktywny.remove(labelWyswietlanieSrodkow);
            panelAktywny.remove(labelWyplata);
            panelAktywny.remove(textWyplacanePieniadze);
            panelAktywny.remove(labelWplata);
            panelAktywny.remove(textWplacanePieniadze);

            panelAktywny.add(labelPrzywitanieKarta);
            if (err != null) {
                labelBledneDane = new JLabel(err);
                panelAktywny.add(labelBledneDane);
            }
            panelAktywny.add(labelPodajNrKarty);
            panelAktywny.add(textNumerKartyPole);

            panelAktywny.add(buttonPotwierdzenie);
        }
        SwingUtilities.updateComponentTreeUI(panelAktywny);
    }

    class WindowClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            int pomocnicza = JOptionPane.showOptionDialog(e.getWindow(),
                    "Czy chcesz zamknąć okno?",
                    "Potwierdzenie",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {"Tak", "Nie"},
                    1);
            if (pomocnicza == JOptionPane.YES_OPTION) {
                Reader.saveKlienci(klienci);
                System.exit(0);
            }
        }
    }

    class CloseAction extends AbstractAction {
        public CloseAction() {
            putValue(Action.NAME, "Zamknij");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl Z"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Main kf = (Main) getMainWindow();
            kf.dispatchEvent(new WindowEvent(kf, WindowEvent.WINDOW_CLOSING));
        }
    }

    class LogoutAction extends AbstractAction {
        public LogoutAction() {
            putValue(Action.NAME, "Wyloguj");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl L"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            changePanel(1);

            textNumerKartyPole.setText("");
            textPinPole.setText("");

        }
    }
}
