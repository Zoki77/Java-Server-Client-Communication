/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ztintor.konfiguracije.NemaKonfiguracije;

/**
 * Priprema postavke za KlijentVremenaDretvu te ju pokreće
 *
 * @author Zoran Tintor
 */
public class KlijentVremena {

    private int port;
    private String nazivDatotekeKonfiguracije;
    private String ipServera;
    private String korisnik;

    public KlijentVremena(int port, String nazivDatotekeKonfiguracije, String ipServera, String korisnik) {
        this.port = port;
        this.nazivDatotekeKonfiguracije = nazivDatotekeKonfiguracije;
        this.ipServera = ipServera;
        this.korisnik = korisnik;
    }

    /**
     * Metoda koja pokreće KlijentVremenaDretvu
     */
    public void pokreniKlijentVremena() {
        Konfiguracija konfig;
        int brojDretvi = 0;
        int pauza = 0;
        System.out.println(nazivDatotekeKonfiguracije);
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatotekeKonfiguracije);
            brojDretvi = Integer.parseInt(konfig.dajPostavku("brojDretvi"));
            pauza = (int) (Integer.parseInt(konfig.dajPostavku("pauza")) * (Math.random() * 1000));
        } catch (NemaKonfiguracije ex) {
            System.out.println("Nema konfiguracije za klijenta vremena");
        }

        List<Thread> dretve = new ArrayList<Thread>();
        if (brojDretvi > 9) {
            brojDretvi = 9;
        }
        for (int i = 0; i < brojDretvi; i++) {
            KlijentVremenaDretva dretva = new KlijentVremenaDretva(port, nazivDatotekeKonfiguracije, ipServera, korisnik);
            dretva.start();
            dretve.add(dretva);
            try {
                Thread.sleep(pauza);
            } catch (InterruptedException ex) {
                Logger.getLogger(KlijentVremena.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
