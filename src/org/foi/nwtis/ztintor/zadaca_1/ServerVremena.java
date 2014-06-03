/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ztintor.konfiguracije.NemaKonfiguracije;

/**
 * Priprema postavke za ServerVremenaDretvu te ju pokreće
 *
 * @author Zoran Tintor
 */
public class ServerVremena {

    private int port;
    private String nazivDatotekeKonfiguracije;
    private boolean trebaUcitati = false;
    private String nazivDatotekeSerijalizacije;
    private Konfiguracija konfig;
    private static boolean kraj = false;
    private static boolean pauza = false;
    static List<Evidencija> evidencijaList = new ArrayList<>();

    public ServerVremena(int port, String nazivDatotekeKonfiguracije, boolean trebaUcitati, String nazivDatotekeSerijalizacije) {
        this.port = port;
        this.nazivDatotekeKonfiguracije = nazivDatotekeKonfiguracije;
        this.trebaUcitati = trebaUcitati;
        this.nazivDatotekeSerijalizacije = nazivDatotekeSerijalizacije;
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatotekeKonfiguracije);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ServerVremena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metoda koja pokreće ServerVremenaDretvu i ako je potrebno učitava podatke
     * iz serializirane datoteke
     */
    public void pokreniServerVremena() {
        try {
            ServerSocket server = new ServerSocket(port);
            if (trebaUcitati) {

                FileInputStream fis = new FileInputStream(nazivDatotekeSerijalizacije);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    evidencijaList = (List<Evidencija>) ois.readObject();
                } catch (ClassNotFoundException ex) {
                    System.out.println("Evidencija nije učitana kod pokretanja servera");
                }
                fis.close();
                ois.close();
            }
            while (!kraj) {
                Socket klijent = server.accept();
                if (!pauza) {
                    System.out.println("Zahtjev primljen");
                }
                ServerVremenaDretva dvd = new ServerVremenaDretva(klijent, nazivDatotekeSerijalizacije, konfig);
                dvd.start();

                try {
                    dvd.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerVremena.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerVremena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isKraj() {
        return kraj;
    }

    public static void setKraj(boolean kraj) {
        ServerVremena.kraj = kraj;
    }

    public static boolean isPauza() {
        return pauza;
    }

    public static void setPauza(boolean pauza) {
        ServerVremena.pauza = pauza;
    }
}
