package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/*
to do
okno potwierdzenia zamkniecia - gdy nacisnie sie nie -> zamyka okno
dodac akcje do przyciskow opcje

 */

public class Main extends JFrame implements Runnable {
    private JMenuBar menuGlowne;
    private JMenu plik;
    private JMenuItem miWyloguj, miZamknij;

    private Map<String, JLabel> componentJLabels;
    private Map<String, JTextField> componentJTextField;
    private Map<String, JButton> componentJButton;

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
        componentJLabels = new HashMap<>();
        componentJTextField = new HashMap<>();
        componentJButton = new HashMap<>();
        klienci = new ArrayList<>();

//        wczytanie kart
        try (Stream<String> stream = Files.lines(Paths.get("klienci.csv"))) {

            stream.forEach(line -> {
                klienci.add(Reader.getKlient(line));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        componentJLabels.put("labelPrzywitanieKarta", new JLabel());
        componentJLabels.get("labelPrzywitanieKarta").setIcon(karta);

        componentJTextField.put("textNumerKartyPole", new JTextField(10));
        componentJTextField.put("textPinPole", new JTextField(4));
        componentJTextField.put("textWyplacanePieniadze", new JTextField());

        componentJButton.put("buttonPotwierdzenie", new JButton("Potwierdź"));
        componentJButton.put("buttonWyplac", new JButton("Wypłać"));

        componentJLabels.put("labelPodajNrKarty", new JLabel("Witamy w banku! Podaj swój numer karty płatniczej!"));
        componentJLabels.put("labelPodajPin", new JLabel("Podaj PIN:"));
        componentJLabels.put("labelBledneDane", new JLabel("Podałeś błędne dane!"));
        componentJLabels.put("labelPrzywitanieInfoNrKarty", new JLabel());
        componentJLabels.put("labelWyplata", new JLabel());
        componentJLabels.put("labelWplata", new JLabel());

        componentJTextField.put("textWplacanePieniadze", new JTextField());

        componentJButton.put("buttonPowrot", new JButton("Powrót"));
        componentJButton.put("buttonPowrotPIN", new JButton("Powrót"));


//        elementy do panelu opcje
        componentJLabels.put("labelPowitaniePoImieniu", new JLabel());
        componentJLabels.put("labelWyswietlanieSrodkow", new JLabel());

        componentJButton.put("buttonWyswietlSrodki", new JButton("Wyświetl środki"));
        componentJButton.put("buttonWyplacPieniadze", new JButton("Wypłać pieniądze"));
        componentJButton.put("buttonWplacPieniadze", new JButton("Wpłać pieniądze"));
        componentJButton.put("buttonWyloguj", new JButton("Wyloguj się"));

//        inicjowanie panelu
        panelAktywny = new JPanel();
        BoxLayout layoutPowitalny = new BoxLayout(panelAktywny,BoxLayout.Y_AXIS);
        panelAktywny.setLayout(layoutPowitalny);

        changePanel(1);

        add(panelAktywny);

        componentJButton.get("buttonPotwierdzenie").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (numerAktywnegoPanelu == 1) {
                    aktywnaKarta = KartaPlatnicza.czyNumerKartyZgadzaSie(componentJTextField.get("textNumerKartyPole").getText(), klienci);


//                  gdy uzytkownik podal błędny nr karty
                    if (aktywnaKarta == -1) {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono karty o podanym numerze w naszym banku.", "Brak karty", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

//                  gdy uzytkownik podal poprawnie nr karty
                    changePanel(2);

                } else if (numerAktywnegoPanelu == 2) {
                    if (componentJTextField.get("textPinPole").getText().length() == 0) return;

//                  gdy użytkownik podał poprawny kod PIN
                    if (klienci.get(aktywnaKarta).getPIN() == Short.parseShort(componentJTextField.get("textPinPole").getText())) {
                        changePanel(3);
                        return;
                    }

//                  gdy uzytkownik podal bledny PIN
                    JOptionPane.showMessageDialog(null, "Wprowadzono niepoprawny numer PIN.", "Błąd uwierzytelnienia", JOptionPane.ERROR_MESSAGE);
                }
                else if (numerAktywnegoPanelu == 3) {
                    changePanel(1);
                } else if (numerAktywnegoPanelu == 5) {
                    try {
                        klienci.get(aktywnaKarta).wyplacPieniadze(Float.parseFloat(componentJTextField.get("textWyplacanePieniadze").getText()));
                        JOptionPane.showMessageDialog(null, String.format("Wypłacono %szł", componentJTextField.get("textWyplacanePieniadze").getText()), "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
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
                        String kwotaWplaty = klienci.get(aktywnaKarta).doliczSrodki(Float.parseFloat(componentJTextField.get("textWplacanePieniadze").getText()));
                        JOptionPane.showMessageDialog(null, kwotaWplaty, "Podsumowanie", JOptionPane.PLAIN_MESSAGE);
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

        componentJButton.get("buttonPowrot").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(3);
            }
        });

        componentJButton.get("buttonPowrotPIN").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(1);
            }
        });

        componentJButton.get("buttonWyswietlSrodki").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(4); }
        });

        componentJButton.get("buttonWyplacPieniadze").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(5); }
        });

        componentJButton.get("buttonWplacPieniadze").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changePanel(6); }
        });

        componentJButton.get("buttonWyloguj").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePanel(1);

                componentJTextField.get("textNumerKartyPole").setText("");
                componentJTextField.get("textPinPole").setText("");}
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
        panelAktywny.remove(componentJLabels.get("labelBledneDane"));
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

            panelAktywny.remove(componentJLabels.get("labelPodajNrKarty"));
            panelAktywny.remove(componentJTextField.get("textNumerKartyPole"));
            panelAktywny.remove(componentJLabels.get("labelBledneDane"));
            panelAktywny.remove(componentJLabels.get("labelPodajNrKarty"));
            panelAktywny.remove(componentJTextField.get("textNumerKartyPole"));
            panelAktywny.remove(componentJLabels.get("labelBledneDane"));


            componentJLabels.put("labelPrzywitanieInfoNrKarty", new JLabel(String.format("Numer karty: %s", componentJTextField.get("textNumerKartyPole").getText())));
            panelAktywny.add(componentJLabels.get("labelPrzywitanieInfoNrKarty"));
            panelAktywny.add(componentJLabels.get("labelPodajPin"));
            panelAktywny.add(componentJTextField.get("textPinPole"));

            panelAktywny.add(componentJButton.get("buttonPotwierdzenie"));
            panelAktywny.add(componentJButton.get("buttonPowrotPIN"));
        } else if (stage == 3) {
            numerAktywnegoPanelu = 3;

            panelAktywny.remove(componentJLabels.get("labelPrzywitanieKarta"));
            panelAktywny.remove(componentJLabels.get("labelPrzywitanieInfoNrKarty"));
            panelAktywny.remove(componentJLabels.get("labelPodajPin"));
            panelAktywny.remove(componentJTextField.get("textPinPole"));
            panelAktywny.remove(componentJButton.get("buttonPotwierdzenie"));
            panelAktywny.remove(componentJLabels.get("labelWyswietlanieSrodkow"));
            panelAktywny.remove(componentJLabels.get("labelWyplata"));
            panelAktywny.remove(componentJTextField.get("textWyplacanePieniadze"));
            panelAktywny.remove(componentJLabels.get("labelWplata"));
            panelAktywny.remove(componentJTextField.get("textWplacanePieniadze"));
            panelAktywny.remove(componentJButton.get("buttonPowrot"));
            panelAktywny.remove(componentJButton.get("buttonPowrotPIN"));

            componentJLabels.put("labelPowitaniePoImieniu", new JLabel(
                    String.format("Sz. P. %s %s", klienci.get(aktywnaKarta).getImie(), klienci.get(aktywnaKarta).getNazwisko())));
            panelAktywny.add(componentJLabels.get("labelPowitaniePoImieniu"));
            panelAktywny.add(componentJButton.get("buttonWyswietlSrodki"));
            panelAktywny.add(componentJButton.get("buttonWyplacPieniadze"));
            panelAktywny.add(componentJButton.get("buttonWplacPieniadze"));
            panelAktywny.add(componentJButton.get("buttonWyloguj"));

        } else if (stage == 4) {
            numerAktywnegoPanelu = 4;

            panelAktywny.remove(componentJLabels.get("labelPowitaniePoImieniu"));
            panelAktywny.remove(componentJButton.get("buttonWyswietlSrodki"));
            panelAktywny.remove(componentJButton.get("buttonWyplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWyloguj"));
            componentJLabels.put("labelWyswietlanieSrodkow", new JLabel(
                    String.format("Masz %.2f pieniędzy na koncie!", klienci.get(aktywnaKarta).srodki)));
            panelAktywny.add(componentJLabels.get("labelWyswietlanieSrodkow"));
            //panelAktywny.add(buttonPotwierdzenie);
            panelAktywny.add(componentJButton.get("buttonPowrot"));

        } else if (stage == 5) {
            numerAktywnegoPanelu = 5;

            panelAktywny.remove(componentJLabels.get("labelPowitaniePoImieniu"));
            panelAktywny.remove(componentJButton.get("buttonWyswietlSrodki"));
            panelAktywny.remove(componentJButton.get("buttonWyplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWyloguj"));
            componentJLabels.put("labelWyplata", new JLabel(
                    String.format("Ile chcesz wypłacić pieniędzy:")));
            componentJTextField.put("textWyplacanePieniadze", new JTextField());
            componentJButton.put("buttonWyplac", new JButton("Wypłać"));
//            buttonWyplac.addActionListener(klienci.get(aktywnaKarta).wyplacPieniadze(Float.parseFloat(textWyplacanePieniadze.getText())));
            panelAktywny.add(componentJLabels.get("labelWyplata"));
            panelAktywny.add(componentJTextField.get("textWyplacanePieniadze"));
            panelAktywny.add(componentJButton.get("buttonPotwierdzenie"));
            panelAktywny.add(componentJButton.get("buttonPowrot"));

        } else if (stage == 6) {
            numerAktywnegoPanelu = 6;

            panelAktywny.remove(componentJLabels.get("labelPowitaniePoImieniu"));
            panelAktywny.remove(componentJButton.get("buttonWyswietlSrodki"));
            panelAktywny.remove(componentJButton.get("buttonWyplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWyloguj"));
            componentJLabels.put("labelWplata", new JLabel(
                    String.format("Ile chcesz wpłacić pieniędzy:")));
            componentJTextField.put("textWplacanePieniadze", new JTextField());

            componentJButton.put("buttonWyplac", new JButton("Wpłać"));
            panelAktywny.add(componentJLabels.get("labelWplata"));
            panelAktywny.add(componentJTextField.get("textWplacanePieniadze"));
            panelAktywny.add(componentJButton.get("buttonPotwierdzenie"));
            panelAktywny.add(componentJButton.get("buttonPowrot"));

        } else {
            numerAktywnegoPanelu = 1;

            panelAktywny.remove(componentJLabels.get("labelPrzywitanieInfoNrKarty"));
            panelAktywny.remove(componentJLabels.get("labelPodajPin"));
            panelAktywny.remove(componentJTextField.get("textPinPole"));

            panelAktywny.remove(componentJLabels.get("labelPowitaniePoImieniu"));
            panelAktywny.remove(componentJButton.get("buttonWyswietlSrodki"));
            panelAktywny.remove(componentJButton.get("buttonWyplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWplacPieniadze"));
            panelAktywny.remove(componentJButton.get("buttonWyloguj"));
            panelAktywny.remove(componentJButton.get("buttonPowrotPIN"));

            panelAktywny.remove(componentJLabels.get("labelWyswietlanieSrodkow"));
            panelAktywny.remove(componentJLabels.get("labelWyplata"));
            panelAktywny.remove(componentJTextField.get("textWyplacanePieniadze"));
            panelAktywny.remove(componentJLabels.get("labelWplata"));
            panelAktywny.remove(componentJTextField.get("textWplacanePieniadze"));

            panelAktywny.add(componentJLabels.get("labelPrzywitanieKarta"));
            if (err != null) {
                componentJLabels.put("labelBledneDane", new JLabel(err));
                panelAktywny.add(componentJLabels.get("labelBledneDane"));
            }
            panelAktywny.add(componentJLabels.get("labelPodajNrKarty"));
            panelAktywny.add(componentJTextField.get("textNumerKartyPole"));

            panelAktywny.add(componentJButton.get("buttonPotwierdzenie"));
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

            componentJTextField.get("textNumerKartyPole").setText("");
            componentJTextField.get("textPinPole").setText("");

        }
    }
}
