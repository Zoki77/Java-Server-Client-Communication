/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ztintor.konfiguracije.NemaKonfiguracije;

/**
 * Izvršava klijentske dretve i šalje njihove komande serveru
 *
 * @author Zoran Tintor
 */
public class KlijentVremenaDretva extends Thread {

    private int port;
    private String nazivDatotekeKonfiguracije;
    private String ipServera;
    private String korisnik;
    private static int brojDretvi = 0;
    private Dnevnik dnevnik;
    private int brojPokusaja = 0;
    private int maxBrojPokusaja;
    private long pocetak = 0;
    private long razlika = 0;
    private int interval = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    Konfiguracija konfig = null;

    public KlijentVremenaDretva(int port, String nazivDatotekeKonfiguracije, String ipServera, String korisnik) {
        this.port = port;
        this.nazivDatotekeKonfiguracije = nazivDatotekeKonfiguracije;
        this.ipServera = ipServera;
        this.korisnik = korisnik;

        setName("Dretva " + brojDretvi++);
        try {

            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatotekeKonfiguracije);
            maxBrojPokusaja = Integer.parseInt(konfig.dajPostavku("brojPokusaja"));
            dnevnik = new Dnevnik(konfig.dajPostavku("dnevnik"));
            dnevnik.otvoriDnevnik();
        } catch (NemaKonfiguracije ex) {
            System.out.println("Nema konfiguracije za klijent vremena dretvu");
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        dnevnik.upisiZapis("Ime dretve: " + getName() + "Vrijeme pokretanja: " + sdf.format(new Date()) + ".\n");
        System.out.println("Dretva: " + getName() + " pokrenuta!");
    }

    @Override
    public void run() {
        while (true) {
            pocetak = new Date().getTime();
            try {
                Socket server = new Socket(ipServera, port);
                /**
                 * otvaranje ulaza i izlaza na socketu
                 */
                OutputStream os = server.getOutputStream();
                InputStream is = server.getInputStream();

                String komanda = "USER " + korisnik + ";GETTIME;";
                os.write(komanda.getBytes());

                os.flush();
                server.shutdownOutput();

                /**
                 * Dohvaćanje odgovora
                 */
                StringBuilder odgovor = new StringBuilder();
                while (true) {
                    int znak = is.read();
                    if (znak == -1) {
                        break;
                    }
                    odgovor.append((char) znak);
                }

                System.out.println("Dretva:" + getName() + " Odgovor: " + odgovor);

                /**
                 * Upisivanje u dnevnik
                 */
                if (odgovor.toString().indexOf("Pauza")!= -1) {
                    dnevnik.upisiZapis("Ime dretve: " + getName() + "; Korisnik: " + korisnik + "; " + "Server je pauziran.\n");
                    brojPokusaja++;
                    System.out.println("-----------------------");
                } else {
                    dnevnik.upisiZapis("Ime dretve: " + getName() + "; Korisnik: " + korisnik + "; " + "Vrijeme i odgovor servera: " + odgovor.toString() + ".\n");
                }
                is.close();
                os.close();
                server.close();

            } catch (IOException ex) {
                brojPokusaja++;
            }
            /**
             * uvjet za prekid rada dretve
             */
            if (brojPokusaja >= maxBrojPokusaja) {
                dnevnik.upisiZapis("Ime dretve: " + getName() + "; Korisnik: " + korisnik + "; " + "Prekoračen max broj pokušaja.\n");
                dnevnik.zatvoriDnevnik();
                System.out.println(getName() + " dosla do max broj pokusaja. umirem.. 100 puta dnevno");
                break;
            }
            /**
             * korekcija i postavljanje intervala između odgovora jedne dretve
             */
            try {
                interval = Integer.parseInt(konfig.dajPostavku("interval"));
                razlika = new Date().getTime() - pocetak;
                sleep((interval * 1000) - razlika);
            } catch (InterruptedException ex) {
                Logger.getLogger(KlijentVremenaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }
}
