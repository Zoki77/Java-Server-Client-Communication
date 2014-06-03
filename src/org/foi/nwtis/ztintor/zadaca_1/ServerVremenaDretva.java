/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;

/**
 * Klasa prima komande od korisnika ili administratora te na temelju iste
 * definira i izvršava određene akcije.
 *
 * @author Zoran Tintor
 */
public class ServerVremenaDretva extends Thread {

    private Socket klijent;
    private String nazivDatotekeSerijalizacije;
    private Konfiguracija konfig;
    private String admin;
    private String lozinka;
    private String stringKomanda;
    private String[] splitKomanda;
    private String[] splitOdgovor;
    private String odg;
    private String korisnik;
    private String komandaEvidencija;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private Date adminVrijeme;
    static long razlika = 0;
    static List<Evidencija> evidencijaList = ServerVremena.evidencijaList;

    public ServerVremenaDretva(Socket klijent, String nazivDatotekeSerijalizacije, Konfiguracija konfig) {
        this.klijent = klijent;
        this.nazivDatotekeSerijalizacije = nazivDatotekeSerijalizacije;
        this.konfig = konfig;
        this.admin = konfig.dajPostavku("admin");
        this.lozinka = konfig.dajPostavku("lozinka");
        this.adminVrijeme = null;
    }

    @Override
    public synchronized void start() {
        super.start();
        if (!ServerVremena.isPauza()) {
            System.out.println("Klijent se javio");
        }
    }

    @Override
    public void run() {
        try {
            /**
             * otvaranje ulaza i izlaza na socketu
             */
            InputStream is = klijent.getInputStream();
            OutputStream os = klijent.getOutputStream();

            /**
             * Dohvaćanje komande
             */
            StringBuilder komanda = new StringBuilder();

            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                komanda.append((char) znak);
            }

            /**
             * odlučivanje o ispisu komande s obzirom na pauzu servera
             */
            if (!ServerVremena.isPauza()) {
                System.out.println("Komanda:" + komanda);
            } else {
                if (komanda.indexOf("PASSWD") != -1) {
                    System.out.println("Komanda:" + komanda);
                }
            }
            String odgovor = "";

            stringKomanda = komanda.toString();
            splitKomanda = stringKomanda.split(";| ");
            korisnik = splitKomanda[1].trim();
            komandaEvidencija = splitKomanda[2].trim();

            /**
             * dohvaćanje trenutnog vremena servera
             */
            if (komanda.indexOf("GETTIME") != -1) {
                if (!ServerVremena.isPauza()) {
                    odgovor = "OK " + sdf.format(new Date(new Date().getTime() - razlika));
                } else {
                    odgovor = "Pauza";
                }

                /*
                 * zaustavljanje servera i upis u serializiranu datoteku 
                 */

            } else if (komanda.indexOf("STOP") != -1) {
                if (stringKomanda.contains(admin) && stringKomanda.contains(lozinka) && komanda.indexOf(admin) < komanda.indexOf(lozinka)) {
                    ServerVremena.setKraj(true);
                    odgovor = "OK";
                    FileOutputStream fos = new FileOutputStream(nazivDatotekeSerijalizacije, false);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(new ArrayList<>(evidencijaList));
                    oos.close();
                    fos.close();
                } else {
                    odgovor = "Neispravan user ili lozinka";
                }

                /**
                 * Pauziranje servera
                 */
            } else if (komanda.indexOf("PAUSE") != -1) {
                if (stringKomanda.contains(admin) && stringKomanda.contains(lozinka) && komanda.indexOf(admin) < komanda.indexOf(lozinka)) {
                    if (ServerVremena.isPauza()) {
                        odgovor = "ERROR posluzitelj je vec pauziran";
                    } else {
                        ServerVremena.setPauza(true);
                        odgovor = "OK";
                    }
                } else {
                    odgovor = "Neispravan user ili lozinka";
                }
                /**
                 * Startanje servera
                 */
            } else if (komanda.indexOf("START") != -1) {
                if (stringKomanda.contains(admin) && stringKomanda.contains(lozinka) && komanda.indexOf(admin) < komanda.indexOf(lozinka)) {
                    if (ServerVremena.isPauza()) {
                        odgovor = "OK";
                        ServerVremena.setPauza(false);
                    } else {
                        odgovor = "ERROR posluzitelj nije pauziran";
                    }
                } else {
                    odgovor = "Neispravan user ili lozinka";
                }
                /**
                 * brisanje iz evidencije
                 */
            } else if (komanda.indexOf("CLEAN") != -1) {
                if (stringKomanda.contains(admin) && stringKomanda.contains(lozinka) && komanda.indexOf(admin) < komanda.indexOf(lozinka)) {
                    FileOutputStream fosClean = new FileOutputStream(nazivDatotekeSerijalizacije, false);
                    ObjectOutputStream oosClean = new ObjectOutputStream(fosClean);
                    oosClean.reset();
                    oosClean.close();
                    fosClean.close();
                    odgovor = "OK";
                } else {
                    odgovor = "Neispravan user ili lozinka";
                }
                /**
                 * postavljanje novog vremena servera
                 */
            } else if (komanda.indexOf("SETTIME") != -1) {
                if (stringKomanda.contains(admin) && stringKomanda.contains(lozinka) && komanda.indexOf(admin) < komanda.indexOf(lozinka)) {

                    String vrijeme = splitKomanda[7].trim() + " " + splitKomanda[8].trim();
                    try {
                        adminVrijeme = sdf.parse(vrijeme);
                    } catch (ParseException ex) {
                        System.out.println("Pogreška kod parsiranja vremena");
                    }
                    razlika = new Date().getTime() - adminVrijeme.getTime();
                    odgovor = "OK";
                } else {
                    odgovor = "Neispravan user ili lozinka";
                }
            } else {
                odgovor = "ERROR nepoznata komanda";
            }

            /**
             * dodavanje evidencije u listu
             */
            if (komanda.indexOf("PASSWD") == -1) {
                splitOdgovor = odgovor.split(" ");
                odg = splitOdgovor[0].trim();
                Evidencija evidencija = new Evidencija(korisnik, komandaEvidencija, odg, new Date(), klijent.getInetAddress());
                evidencijaList.add(evidencija);
            }

            System.out.println("odgovor:" + odgovor);
            os.write(odgovor.getBytes());

            os.close();
            is.close();
            klijent.close();

        } catch (IOException ex) {
            Logger.getLogger(ServerVremenaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }
}
