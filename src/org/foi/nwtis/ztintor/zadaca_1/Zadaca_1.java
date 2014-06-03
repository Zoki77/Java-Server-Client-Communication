/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.zadaca_1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Glavna klasa programa u kojoj odabiremo jednu od opcoja definiranih pomoću
 * regExp-a Opcije su pokretanje servera, klijenta, administratora i ispis
 * evidencije
 *
 * @author Zoran Tintor
 */
public class Zadaca_1 {

    public static void main(String[] args) {

        int port;
        String nazivDatotekeKonfiguracije;
        boolean trebaUcitati;
        String nazivDatotekeSerijalizacije;
        Pattern pattern;
        Matcher m;
        boolean status;
        StringBuilder sb = new StringBuilder();
        for (int i = 0;
                i < args.length;
                i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();

        /**
         * U switchu provjeravamo 1 argument programa i na temelju njega
         * provjeravamo željeni regExp te ako je unos ispravan izvršavamo daljne
         * akcije
         */
        switch (args[0]) {
            case "-server":
                // -server -port port -konf datoteka[.txt | .xml] [-load] -s datoteka
                String sintaksaServer = "^-server -port ([8-9]\\d{3}) -konf ([^\\s]+\\.(?i)(txt|xml))( +-load)? -s ([^\\s]+\\.[^\\s]+) *$";
                pattern = Pattern.compile(sintaksaServer);
                m = pattern.matcher(p);
                status = m.matches();
                if (status) {
                    port = Integer.parseInt(m.group(1));
                    nazivDatotekeKonfiguracije = m.group(2);
                    trebaUcitati = m.group(4) != null;
                    nazivDatotekeSerijalizacije = m.group(5);
                    ServerVremena server = new ServerVremena(port, nazivDatotekeKonfiguracije, trebaUcitati, nazivDatotekeSerijalizacije);
                    server.pokreniServerVremena();
                } else {
                    System.out.println("Sintaksa za server ne odgovara");
                }
                break;

            case "-user":
                //-user -ts ipadresa -port port -u korisnik -konf datoteka[.txt | .xml]
                String sintaksaUser = "^-user -ts (([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]))"
                        + " -port ([8-9]\\d{3}) -u ([a-zA-Z]{3,}) -konf ([^\\s]+\\.(?i)(txt|xml))$";
                pattern = Pattern.compile(sintaksaUser);
                m = pattern.matcher(p);
                status = m.matches();
                if (status) {
                    port = Integer.parseInt(m.group(6));
                    nazivDatotekeKonfiguracije = m.group(8);
                    String ipServera = m.group(1);
                    String korisnik = m.group(7);
                    KlijentVremena kv = new KlijentVremena(port, nazivDatotekeKonfiguracije, ipServera, korisnik);
                    kv.pokreniKlijentVremena();
                } else {
                    System.out.println("Sintaksa za usera ne odgovara");
                }
                break;

            case "-admin":
                //-admin -ts ipadresa -port port -u korisnik -p lozinka -konf datoteka[.txt | .xml] [-t dd.mm.yyyy hh:mm:ss | [PAUSE | START | STOP | CLEAN]]
                String sintaksaAdmin = "^-admin -ts (([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]))"
                        + " -port ([8-9]\\d{3}) -u ([a-zA-Z]{3,}) -p ([^\\s]{3,}) -konf ([^\\s]+\\.(?i)(txt|xml))"
                        + "( +-t (([012]\\d|3[01])\\.(0\\d|1[012])\\.(\\d{4}) ([01]\\d|2[0-4])\\:([0-5]\\d)\\:([0-5]\\d))| +(PAUSE|START|STOP|CLEAN)?)?$";
                pattern = Pattern.compile(sintaksaAdmin);
                m = pattern.matcher(p);
                status = m.matches();
                if (status) {
                    port = Integer.parseInt(m.group(6));
                    nazivDatotekeKonfiguracije = m.group(9);
                    String ipServera = m.group(1);
                    String korisnik = m.group(7);
                    String lozinka = m.group(8);
                    String adminKomanda = "";
                    String vrijeme = "";

                    if (m.group(19) == null && m.group(12) == null) {
                        adminKomanda = "GETTIME";
                    }

                    if (m.group(19) != null) {
                        adminKomanda = m.group(19);
                    }

                    if (m.group(12) != null) {
                        vrijeme = m.group(12);
                        adminKomanda = "SETTIME";
                    }

                    AdministratorVremena sysAdmin = new AdministratorVremena(port,
                            nazivDatotekeKonfiguracije,
                            ipServera, korisnik, lozinka, adminKomanda, vrijeme);
                    sysAdmin.pokreniAdministratorVremena();
                } else {
                    System.out.println("Sintaksa za admina ne odgovara");
                }
                break;

            /**
             * Ispis podataka iz serializirane datoteke na konzolu
             */
            case "-show":
                //-show -s datoteka
                String sintaksaShow = "^-show -s ([^\\s]+\\.[^\\s]+) *$";
                pattern = Pattern.compile(sintaksaShow);
                m = pattern.matcher(p);
                status = m.matches();
                if (status) {
                    nazivDatotekeSerijalizacije = m.group(1);

                    try {
                        FileInputStream fis = new FileInputStream(nazivDatotekeSerijalizacije);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        List<Evidencija> ispisEvidencija = (List<Evidencija>) ois.readObject();
                        fis.close();
                        ois.close();
                        for (int i = 0; i < ispisEvidencija.size(); i++) {
                            System.out.println(ispisEvidencija.get(i).getKorisnik() + "  " + ispisEvidencija.get(i).getKomanda() + "  " + ispisEvidencija.get(i).getOdgovor() + "  " + ispisEvidencija.get(i).getVrijeme() + "  " + ispisEvidencija.get(i).getAdresa() + "\n");
                        }
                    } catch (IOException ex) {
                        System.out.println("Datoteka je prazna");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Zadaca_1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            default:
                System.out.println("Unos ne odgovara");
        }
    }
}
