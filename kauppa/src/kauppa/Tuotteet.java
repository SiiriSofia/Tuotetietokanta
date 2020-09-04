/**
 * 
 */
package kauppa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection; 
import java.sql.*;
import static kauppa.Kanta.alustaKanta;

import kanta.SailoException;

/**
 * Luokka kaikille kaupan tuotteille
 * Avustajat:
 * - Tuote-luokka
 * Vastuut:
 * -  ylläpitää tuoterekisteriä eli osaa lisätä, 
 *     poistaa ja muokata tuotteita 
 * -  lukee ja kirjoittaa tiedostoon
 * -  osaa etsiä tuotteita ja lajitella niitä uuteen
 *    järjestykseen          
 * @author Siiri
 * @version 18 Feb 2020
 * @version 24 Mar 2020 - Tiedostonluku
 * @version 1 Apr 2020 - Haku- ja vertailuominaisuuksien lisäys
 */
public class Tuotteet { //implements Iterable<Tuote>{

	/*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * #import kanta.SailoException;
     * 
     * private Tuotteet tuotteet;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "kookauppa";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    tuotteet = new Tuotteet(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
	
	private Kanta kanta;
    private static Tuote aputuote = new Tuote();

    /**
     * Tarkistetaan että kannassa tuotteiden tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Tuotteet(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tuotetietokannan metadata ja tarkistetaan siitä onko
            // Tuotteet-nimistä taulua olemassa.
            // Jos ei ole, niin luodaan se. Jos taulun rakenteessa on jotain
        	// hämminkiä, niin käyttäjälle ilmoitetaan asiasta virheilmoituksella
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Tuotteet", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Tuotteet-taulu
                    try ( PreparedStatement sql = con.prepareStatement(aputuote.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
   
    /**
     * Lisää uuden tuotteen tietorakenteeseen.  Ottaa tuotteen omistukseensa.
     * @param tuote lisätäävän tuotteen viite.  Huom tietorakenne muuttuu omistajaksi
     * @throws SailoException jos tietorakenne on jo täynnä
     * @example
     * <pre name="test">
     * #THROWS kanta.SailoException 
     * 
     * Collection<Tuote> loytyneet = tuotteet.etsi("", 1);
     * loytyneet.size() === 0;
     * 
     * Tuote tuote1 = new Tuote(), tuote2 = new Tuote();
     * tuotteet.lisaa(tuote1); 
     * tuotteet.lisaa(tuote2);
     *  
     * loytyneet = tuotteet.etsi("", 1);
     * loytyneet.size() === 2;
     * 
     * // Unique constraint ei hyväksy
     * tuotteet.lisaa(tuote1); #THROWS SailoException
     * Tuote tuote3 = new Tuote(); Tuote tuote4 = new Tuote(); Tuote tuote5 = new Tuote();
     * tuotteet.lisaa(tuote3); 
     * tuotteet.lisaa(tuote4); 
     * tuotteet.lisaa(tuote5); 

     * loytyneet = tuotteet.etsi("", 1);
     * loytyneet.size() === 5;
     * Iterator<Tuote> i = loytyneet.iterator();
     * i.next() === tuote1;
     * i.next() === tuote2;
     * i.next() === tuote3;
     * </pre>
     */
    public void lisaa(Tuote tuote) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = tuote.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	tuote.tarkistaId(rs);
            }   
            
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Päivittää tuotteen tiedot
     * @param tuote päivitettävä tuote
     * @throws SailoException
     */
    public void paivitaTuote(Tuote tuote) throws SailoException {
    	try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = tuote.annaPaivityslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	tuote.tarkistaId(rs);
            }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Palauttaa tuotteet listassa
     * @param hakuehto hakuehto  
     * @param k etsittävän kentän indeksi 
     * @return tuotteet listassa
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @example
     * <pre name="test">
     * #THROWS kanta.SailoException
     * Tuote tuote1 = new Tuote(); tuote1.taytaTestiTiedoilla(); 
     * Tuote tuote2 = new Tuote(); tuote2.taytaTestiTiedoilla(); 
     * tuotteet.lisaa(tuote1);
     * tuotteet.lisaa(tuote2);
     * tuotteet.lisaa(tuote2);  #THROWS SailoException  // ei saa lisätä sama id:tä uudelleen
     * Collection<Tuote> loytyneet = tuotteet.etsi("", 1);
     * loytyneet.size() === 2;
     *
     * ftied.delete();
     * </pre>
     */
    public Collection<Tuote> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = aputuote.getKysymys(k);
        if ( k < 0 ) { kysymys = aputuote.getKysymys(0); ehto = "%"; }
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
          PreparedStatement sql = con.prepareStatement("SELECT * FROM Tuotteet " +
        		  " WHERE " + kysymys + " LIKE ? ORDER BY " + kysymys)) {
            ArrayList<Tuote> loytyneet = new ArrayList<Tuote>();
            
        	String jokeritKorjattu = ehto.replace('*', '%');  // hoidetaan tässä jokerimerkkien
        	sql.setString(1, jokeritKorjattu);				  // muuttaminen sql:lle oikeaan muotoon
        	
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                	Tuote t = new Tuote();
                    t.parse(tulokset);
                    loytyneet.add(t);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /** 
     * Poistaa kyseisen tuotteen tuotteet-taulusta
     * @param tuoteNro poistettavan tuotteen tuotenro
     * @throws SailoException 
     */ 
    public void poista(int tuoteNro) throws SailoException { 
    	try ( Connection con = kanta.annaKantayhteys();
        	  PreparedStatement sql = con.prepareStatement("DELETE FROM Tuotteet WHERE tuoteNro = ?")
    	        ) {
        	sql.setInt(1, tuoteNro);
        	sql.executeUpdate();
		} catch (SQLException e) {
			throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
		}
    } 

	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		try {
			new File("kokeilu.db").delete();
            Tuotteet tuotteet = new Tuotteet("kokeilu");
    
            Tuote testituote1 = new Tuote(), testituote2 = new Tuote();
            testituote1.taytaTestiTiedoilla();
            testituote2.taytaTestiTiedoilla();
            
            tuotteet.lisaa(testituote1);
            tuotteet.lisaa(testituote2);
            testituote2.tulosta(System.out);
			
			System.out.println("================= Tuotteet testi ====================");
			
			int i = 0;
            for (Tuote tuote:tuotteet.etsi("", -1)) {
                System.out.println("Tuote nro: " + i++);
                tuote.tulosta(System.out);
            }
            
            new File("kokeilu.db").delete();
        } catch ( SailoException ex ) {
            System.out.println(ex.getMessage());
        }


	}

}
