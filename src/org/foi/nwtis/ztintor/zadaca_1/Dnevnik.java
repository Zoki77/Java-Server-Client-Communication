/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa koja se koristi za rad s zapisima podataka u datoteku
 *
 * @author Zoran Tintor
 */
public class Dnevnik {

    private String nazivDatoteke;
    private FileOutputStream fos;
    private File outFile;
    SimpleDateFormat sdf;

    public Dnevnik(String nazivDatoteke) {
        this.nazivDatoteke = nazivDatoteke;
        this.sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

    /**
     * otvaranje dnevnika
     *
     * @return true ako je sve ok, false ako nesto krene krivo
     */
    public boolean otvoriDnevnik() {
        outFile = new File(nazivDatoteke);
        try {
            fos = new FileOutputStream(outFile, true);
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("Otvaranje dnevnika nije uspjelo.");
            return false;
        }
    }

    /**
     * Upisuje trazeni zapis zajedno sa trenutnim vremenom na kraj datoteke loga
     * koja je postavljena prilikom incijalizacije objekta
     *
     * @param zapis - ono sto upisujemo
     * @return true ako je sve ok, false ako nesto krene krivo
     */
    public synchronized boolean upisiZapis(String zapis) {
        String zapisVrijeme = "Trenutno vrijeme: " + sdf.format(new Date()) + "; " + zapis;
        try {
            fos.write(zapisVrijeme.getBytes());
            return true;
        } catch (IOException ex) {
            System.out.println("Upis u dnevnik nije uspio.");
            return false;
        }
    }

    /**
     * zatvaranje dnevnika
     *
     * @return true ako je sve ok, false ako nesto krene krivo
     */
    public boolean zatvoriDnevnik() {
        try {
            fos.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Zatvaranje Dnevnika nije uspjelo.");
            return false;
        }
    }
}
