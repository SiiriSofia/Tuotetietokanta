/**
 * 
 */
package kauppa;

import static kauppa.Kanta.alustaKanta;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import kanta.SailoException;

/**
 * Luokka kaikille kaupan tuoteosastoille
 * Avustajat:
 * - Osasto-luokka
 * Vastuut:
 * -   ylläpitää rekisteriä kaupan sisältämistä
 *     tuoteosastoista
 * -   osaa etsiä tuoteosaston ja lajitella ne
 *     järjestykseen
 * -   lukee ja kirjoittaa tuoteosaston tiedostoon
 * @author Siiri
 * @version 4 Mar 2020
 * @version 28 Mar 2020 - Tiedostonluku
 */
public class Osastot { //implements Iterable<Osasto> {

	
	/*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * #import kanta.SailoException;
     * 
     * private Osastot osastot;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "kauppa";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    osastot = new Osastot(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
	
	private Kanta kanta;
    private static Osasto apuosasto = new Osasto();
    
    /**
     * Tarkistetaan että kannassa osastojen tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Osastot(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tuotetietokannan metadata ja tarkistetaan siitä onko
            // Osastot-nimistä taulua olemassa.
            // Jos ei ole, niin luodaan se. Jos taulun rakenteessa on jotain
        	// hämminkiä, niin käyttäjälle ilmoitetaan asiasta virheilmoituksella
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Osastot", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Osastot-taulu
                    try ( PreparedStatement sql = con.prepareStatement(apuosasto.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
   
    /**
     * Lisää uuden osaston tietorakenteeseen.  Ottaa osaston omistukseensa.
     * @param osasto lisätäävän osaston viite.  Huom tietorakenne muuttuu omistajaksi
     * @throws SailoException jos tietorakenne on jo täynnä
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * 
     * Collection<Osasto> loytyneet = osastot.etsi("", 1);
     * loytyneet.size() === 0;
     * 
     * Osasto osasto1 = new Osasto(), osasto2 = new Osasto();
     * osasto1.taytaTestiTiedoilla(); osasto2.taytaTestiTiedoilla();
     * osastot.lisaa(osasto1); 
     * osastot.lisaa(osasto2);
     *  
     * loytyneet = osastot.etsi("%", 1);
     * loytyneet.size() === 2;
     * 
     * // Unique constraint ei hyväksy
     * osastot.lisaa(osasto1); #THROWS SailoException
     * Osasto osasto3 = new Osasto(); Osasto osasto4 = new Osasto(); Osasto osasto5 = new Osasto();
     * osasto3.taytaTestiTiedoilla(); osasto4.taytaTestiTiedoilla(); osasto5.taytaTestiTiedoilla();
     * osastot.lisaa(osasto3); 
     * osastot.lisaa(osasto4); 
     * osastot.lisaa(osasto5); 

     * loytyneet = osastot.etsi("%", 1);
     * loytyneet.size() === 5;
     * Iterator<Osasto> i = loytyneet.iterator();
     * i.next() === osasto1;
     * i.next() === osasto2;
     * i.next() === osasto3;
     * </pre>
     */
    public void lisaa(Osasto osasto) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = osasto.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	osasto.tarkistaId(rs);
            }   
            
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * @param osastoNro Osaston numero, jonka perusteella etsitään osasto
     * osastot-taulusta
     * @return osasto, joka sisältää annetun osastoNron
     * @throws SailoException
     */
    public Osasto hae(int osastoNro) throws SailoException {
        String ehto = ""+osastoNro;
        String kysymys = apuosasto.getKysymys(0);
        Osasto loydetty = new Osasto();
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
            PreparedStatement sql = con.prepareStatement("SELECT * FROM Osastot WHERE " + kysymys + " LIKE ?") ) {
            
            sql.setString(1, ehto);
            try ( ResultSet tulos = sql.executeQuery() ) {
                    loydetty.parse(tulos);
            }
            return loydetty;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }

    /**
     * Palauttaa osastot listassa
     * @param hakuehto hakuehto  
     * @param k etsittävän kentän indeksi 
     * @return osastot listassa
     * @throws SailoException jos tietokannan kanssa ongelmia
     * </pre>
     */
    public Collection<Osasto> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = apuosasto.getKysymys(k);
        if ( k < 0 ) { kysymys = apuosasto.getKysymys(0); ehto = "%"; }
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Osastot WHERE " + kysymys + " LIKE ?") ) {
            ArrayList<Osasto> loytyneet = new ArrayList<Osasto>();
            
            sql.setString(1, ehto);
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                	Osasto o = new Osasto();
                    o.parse(tulokset);
                    loytyneet.add(o);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }

		
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		try {
			new File("testi.db").delete();
            Osastot osastot = new Osastot("testi");
    
            Osasto o1 = new Osasto(), o2 = new Osasto();
            o1.taytaTestiTiedoilla();
            o2.taytaTestiTiedoilla();
            
            osastot.lisaa(o1);
            osastot.lisaa(o2);
            o2.tulosta(System.out);
			
			System.out.println("================= Osastot testi ====================");
			
			int i = 0;
            for (Osasto osasto:osastot.etsi("", -1)) {
                System.out.println("Osasto nro: " + i++);
                osasto.tulosta(System.out);
            }
            
            new File("testi.db").delete();
        } catch ( SailoException ex ) {
            System.out.println(ex.getMessage());
        }


	}

}
