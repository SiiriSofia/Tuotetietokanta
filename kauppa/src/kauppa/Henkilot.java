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
 * Luokka kaikille kaupan työntekijöinä oleville henkilöille
 * Avustajat:
 * - Henkilo-luokka
 * Vastuut:
 * -   ylläpitää varsinaista henkilörekisteriä
 * -   osaa lisätä ja poistaa henkilön rekisteristä
 * -   lukee ja kirjoittaa henkilön tiedostoon
 * -   osaa etsiä ja lajitella henkiloita 
 * @author Siiri
 * @version 3 Mar 2020
 * @version 28 Mar 2020 - Tiedostonluku
 */
public class Henkilot { //implements Iterable<Henkilo> {
	/*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import kanta.SailoException;
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Henkilot henkilot;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testikauppa";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    henkilot = new Henkilot(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
	
	private Kanta kanta;
    private static Henkilo apuhenkilo = new Henkilo();

    /**
     * Tarkistetaan että kannassa tuotteiden tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Henkilot(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            DatabaseMetaData meta = con.getMetaData();
            try ( ResultSet taulu = meta.getTables(null, null, "Henkilot", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan henkilot-taulu
                    try ( PreparedStatement sql = con.prepareStatement(apuhenkilo.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
                
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
   
    /**
     * Lisää uuden henkilon tietorakenteeseen.  Ottaa henkilon omistukseensa.
     * @param henkilo lisätäävän tuotteen viite.  Huom tietorakenne muuttuu omistajaksi
     * @throws SailoException jos tietorakenne on jo täynnä
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * 
     * Collection<Henkilo> loytyneet = henkilot.etsi("", 1);
     * loytyneet.size() === 0;
     * 
     * Henkilo henkilo1 = new Henkilo(), henkilo2 = new Henkilo();
     * henkilot.lisaa(henkilo1); 
     * henkilot.lisaa(henkilo2);
     *  
     * loytyneet = henkilot.etsi("", 1);
     * loytyneet.size() === 2;
     * 
     * // Unique constraint ei hyväksy
     * henkilot.lisaa(henkilo1); #THROWS SailoException
     * Henkilo henkilo3 = new Henkilo(); Henkilo henkilo4 = new Henkilo(); Henkilo henkilo5 = new Henkilo();
     * henkilot.lisaa(henkilo3); 
     * henkilot.lisaa(henkilo4); 
     * henkilot.lisaa(henkilo5); 

     * loytyneet = henkilot.etsi("", 1);
     * loytyneet.size() === 5;
     * Iterator<Henkilo> i = loytyneet.iterator();
     * i.next() === henkilo1;
     * i.next() === henkilo2;
     * i.next() === henkilo3;
     * </pre>
     */
    public void lisaa(Henkilo henkilo) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); 
        		PreparedStatement sql = henkilo.annaLisayslauseke(con) ) {
        	sql.executeUpdate();
        	try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	henkilo.tarkistaId(rs);
            }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Päivittää parametrina annetun henkilön tiedot henkilot-tauluun
     * @param henkilo päivitettävä henkilö
     * @throws SailoException
     */
    public void paivitaHenkilo(Henkilo henkilo) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); 
        		PreparedStatement sql = henkilo.annaPaivityslauseke(con) ) {
        	sql.executeUpdate();
        	try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	henkilo.tarkistaId(rs);
            }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Tarkistaa löytyykö parametrina annettu henkilö nimen perusteella jo
     * listalla olevista henkilöistä
     * @param henkilo
     * @return totuusarvo true jos henkilö löytyy taulusta ennestään
     * @throws SailoException 
     */
    public boolean tarkistaLoytyyko(String henkilonnimi) throws SailoException {
    	 String tarkistettavaNimi = henkilonnimi;
    	 try ( Connection con = kanta.annaKantayhteys();
                 PreparedStatement sql = con.prepareStatement("SELECT * FROM Henkilot " +
    	                "WHERE Nimi = ?")){
    		 	 sql.setString(1, tarkistettavaNimi);
    		 	try ( ResultSet tulokset = sql.executeQuery() ) {
                    if (tulokset.next() == true ) {
                    	return true;
                    }
                }
	        } catch (SQLException e) {
	            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
	        }
    	 return false;
      }
    
    /**
     * @param henkilonNimi Henkilön nimi, jonka perusteella etsitään
     * henkilot-taulusta
     * @return henkilo, joka sisältää annetun henkilonnimen
     * @throws SailoException
     */
    public Henkilo hae(String henkilonNimi) throws SailoException {
        String ehto = ""+henkilonNimi;
        String kysymys = apuhenkilo.getKysymys(1);
        Henkilo loydetty = new Henkilo();
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
            PreparedStatement sql = con.prepareStatement("SELECT * FROM Henkilot WHERE " + kysymys + " LIKE ?") ) {
            
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
     * @param henkiloNro Henkilön numero, jonka perusteella etsitään
     * henkilot-taulusta
     * @return henkilo, joka sisältää annetun henkiloNron
     * @throws SailoException
     */
    public Henkilo hae(int henkiloNro) throws SailoException {
        String ehto = ""+henkiloNro;
        String kysymys = apuhenkilo.getKysymys(0);
        Henkilo loydetty = new Henkilo();
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
            PreparedStatement sql = con.prepareStatement("SELECT * FROM Henkilot WHERE " + kysymys + " LIKE ?") ) {
            
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
     * Palauttaa henkilöt listassa
     * @param hakuehto hakuehto  
     * @param k etsittävän kentän indeksi 
     * @return henkilot listassa
     * @throws SailoException jos tietokannan kanssa ongelmia
     * </pre>
     */
    public Collection<Henkilo> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = apuhenkilo.getKysymys(k);
        if ( k < 0 ) { kysymys = apuhenkilo.getKysymys(0); ehto = "%"; }
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Henkilot WHERE " + kysymys + " LIKE ?") ) {
            ArrayList<Henkilo> loytyneet = new ArrayList<Henkilo>();
            
            sql.setString(1, ehto);
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                	Henkilo h = new Henkilo();
                    h.parse(tulokset);
                    loytyneet.add(h);
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
			new File("kokeilu.db").delete();
            Henkilot henkilot = new Henkilot("kokeilu");
    
            Henkilo h1 = new Henkilo(), h2 = new Henkilo();
            h1.taytaTestiTiedoilla();
            h2.taytaTestiTiedoilla();
            
            henkilot.lisaa(h1);
            henkilot.lisaa(h2);
            h2.tulosta(System.out);
			
			System.out.println("================= Henkilot testi ====================");
			
			int i = 0;
            for (Henkilo henkilo:henkilot.etsi("", -1)) {
                System.out.println("Henkilö nro: " + i++);
                henkilo.tulosta(System.out);
            }
            
            new File("kokeilu.db").delete();
        } catch ( SailoException ex ) {
            System.out.println(ex.getMessage());
        }


	}

}
