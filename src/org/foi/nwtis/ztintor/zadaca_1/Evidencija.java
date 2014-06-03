/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

/**
 * Klasa za baratanje serializiranim podatcima
 *
 * @author Zoran Tintor
 */
public class Evidencija implements Serializable {

    private String korisnik;
    private String komanda;
    private String odgovor;
    private Date vrijeme;
    private InetAddress adresa;

    public Evidencija(String korisnik, String komanda, String odgovor, Date vrijeme, InetAddress adresa) {
        this.korisnik = korisnik;
        this.komanda = komanda;
        this.odgovor = odgovor;
        this.vrijeme = vrijeme;
        this.adresa = adresa;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public String getKomanda() {
        return komanda;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public InetAddress getAdresa() {
        return adresa;
    }
}
