/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa koja šalje komande serveru
 *
 * @author Zoran Tintor
 */
public class AdministratorVremena {

    private int port;
    private String nazivDatotekeKonfiguracije;
    private String ipServera;
    private String korisnik;
    private String lozinka;
    private String adminKomanda;
    private String vrijeme;

    public AdministratorVremena(int port, String nazivDatotekeKonfiguracije, String ipServera, String korisnik, String lozinka, String adminKomanda, String vrijeme) {
        this.port = port;
        this.nazivDatotekeKonfiguracije = nazivDatotekeKonfiguracije;
        this.ipServera = ipServera;
        this.korisnik = korisnik;
        this.lozinka = lozinka;
        this.adminKomanda = adminKomanda;
        this.vrijeme = vrijeme;
    }

    public void pokreniAdministratorVremena() {
        try {
            Socket server = new Socket(ipServera, port);

            /**
             * otvaranje ulaza i izlaza na socketu
             */
            OutputStream os = server.getOutputStream();
            InputStream is = server.getInputStream();

            String komanda = "USER " + korisnik + "; PASSWD " + lozinka + "; " + adminKomanda + " " + vrijeme + ";";
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
            System.out.println("Odgovor: " + odgovor);
            is.close();
            os.close();
            server.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(AdministratorVremena.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorVremena.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
