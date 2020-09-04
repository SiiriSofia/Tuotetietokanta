/**
 * 
 */
package kauppa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Luokka SQLite-yhteyden muodostamiseksi.
 * @author Siiri
 * @version 5 Apr 2020
 */
public class Kanta {
    private static HashMap<String, Kanta> kannat = new HashMap<String, Kanta>();
    private String tiedostonPerusNimi = "";
    
    /** 
     * Alustetaan kanta
     * @param nimi tiedoston perusnimi
     */
    private Kanta(String nimi) {
        setTiedosto(nimi);
    }

    /**
     * Alustetaan kantayhteys
     * @param nimi kannan nimi
     * @return kannan tiedot jolla voidaan operoida
     */
    public static Kanta alustaKanta(String nimi) {
        if ( kannat.containsKey(nimi) ) return kannat.get(nimi); 
        Kanta uusi = new Kanta(nimi);
        kannat.put(nimi, uusi);
        return uusi;
    }
    
    /**
     * Asettaa kannan perusnimen. SQL-lite käyttää yhtä tiedostoa koko
     * tietokannalle
     * @param nimi uusi nimi
     */
    public void setTiedosto(String nimi) {
        tiedostonPerusNimi = nimi;
    }
    
    /**
     * Palauttaa tiedoston nimen, jota käytetään tallennukseen
     * @return tallennustiedoston nimi
     */
    public String getTiedostonNimi() {
        return tiedostonPerusNimi + ".db";
    }
    
    /**
     * Antaa tietokantayhteyden
     * @return tietokantayhteys
     * @throws SQLException Jos tietokantayhteyden avaamisessa on ongelmia
     */
    public Connection annaKantayhteys() throws SQLException {
        String sDriver = "org.sqlite.JDBC";
        try {
            Class.forName(sDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("Virhe luokan " + sDriver + "lataamisessa: " + e.getMessage());
        }
        return DriverManager.getConnection("jdbc:sqlite:" + getTiedostonNimi());
    }
}