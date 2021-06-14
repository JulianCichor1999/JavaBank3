package com.company;

public class KartaPlatnicza extends KartaPlatniczaAbstract {
    private final long numerKarty;
    private final short pin;
    protected double srodki;
    private Producenci producentKarty;

    public KartaPlatnicza(String imie, String nazwisko, long numerKarty, short pin, double srodki) {
        super(imie, nazwisko);
        this.numerKarty = numerKarty;
        this.pin = pin;
        this.srodki = srodki;
    }

    public KartaPlatnicza(String imie, String nazwisko, String numerKarty, short pin, double srodki) {
        this(imie, nazwisko, Long.parseLong(numerKarty), pin, srodki);
    }

    public KartaPlatnicza() {
        super(null, null);
        this.numerKarty = -1;
        this.pin = -1;
        this.srodki = -1;
    }

    public String getNumerKarty() {
        return String.format("%16d", numerKarty).replaceAll("\\s", "0");
    }

    public short getPin() {
        return pin;
    }

    public double getSrodki() {
        return srodki;
    }

    public Producenci getProducentKarty() {
        return producentKarty;
    }

    protected void setProducentKarty(Producenci producentKarty) {
        this.producentKarty = producentKarty;
    }

    public void wyplacPieniadze(double wyplacanaKwota) throws NiewystarczajaceSrodkiException, ZeroWyplataException {
        if (wyplacanaKwota > srodki) {
            throw new NiewystarczajaceSrodkiException();
        } else if (wyplacanaKwota == 0) {
            throw new ZeroWyplataException();
        } else {
            wyplacanie();
            srodki -= wyplacanaKwota;
        }
    }

    public String doliczSrodki(double noweSrodki) {
        srodki += noweSrodki;
        return String.format("Wpłacono %.2f pieniędzy do konta\n", noweSrodki);
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %d, %.2f, %s",
                getImie(), getNazwisko(), getNumerKarty(), getPin(), getSrodki(), getProducentKarty());
    }
}
