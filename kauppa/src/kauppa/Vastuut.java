/**
 * 
 */
package kauppa;

import static kauppa.Kanta.alustaKanta;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kanta.SailoException;

/**
 * Luokka kaikille kaupan vastuille
 * Avustajat:
 * - Osasto-luokka
 * - Henkilo-luokka
 * Vastuut:
 * -  tietää henkilöiden ja tuoteosastojen väliset
 *    yhteydet eli vastuut
 * -  ylläpitää vastuuyhteyksien rekisteriä eli osaa
 *    lisätä, poistaa ja muokata vastuita
 * -  lukee ja kirjoittaa tiedostoon    
 * @author Siiri
 * @version 4 Mar 2020
 * @version 28 Mar 2020 - Tiedostonluku
 */
public class Vastuut { //implements Iterable<Vastuu> {
	
	private Kanta kanta;
    private static Vastuu apuvastuu = new Vastuu();

    /**
     * Tarkistetaan että kannassa tuotteiden tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Vastuut(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Vastuut", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Vastuut-taulu
                    try ( PreparedStatement sql = con.prepareStatement(apuvastuu.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
   
    /**
     * Lisää uuden vastuun tietorakenteeseen.  Ottaa vastuun omistukseensa.
     * @param vastuu lisättävän vastuun viite.  Huom tietorakenne muuttuu omistajaksi
     * @throws SailoException jos tietorakenne on jo täynnä
     */
    public void lisaa(Vastuu vastuu) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = vastuu.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            /*try ( ResultSet rs = sql.getGeneratedKeys() ) {
            	vastuu.tarkistaId(rs);
            }   */
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    

    /**
	 * Antaa listan etsityistä vastuista annetulla ehdolla
	 * @param henkiloNro henkilön numero
	 * @return Palauttaa listan vastuista, jotka liittyvät parametrina annettuun henkilönumeroon
     * @throws SailoException jos harrastusten hakeminen tietokannasta epäonnistuu
     */
    public List<Vastuu> annaVastuutHenkilolle(int henkiloNro) throws SailoException {
    	List<Vastuu> loydetyt = new ArrayList<Vastuu>();
    	
    	try ( Connection con = kanta.annaKantayhteys();
        	  PreparedStatement sql = con.prepareStatement("SELECT * FROM Vastuut WHERE henkiloNro = ?")
    	        ) {
        	sql.setInt(1, henkiloNro);
        	try ( ResultSet tulokset = sql.executeQuery() )  {
            	while ( tulokset.next() ) {
            		Vastuu v = new Vastuu();
            		v.parse(tulokset);
            		loydetyt.add(v);
    			}
        	}
        	
		} catch (SQLException e) {
			throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
		}
    	return loydetyt;
    }
    
    /**
   	 * Antaa listan etsityistä vastuista annetulla ehdolla
   	 * @param osastoNro osaston numero
   	 * @return Palauttaa listan vastuista, jotka liittyvät parametrina annettuun osastonumeroon
     * @throws SailoException jos harrastusten hakeminen tietokannasta epäonnistuu
     */
    public List<Vastuu> annaVastuutOsastolle(int osastoNro) throws SailoException {
    	List<Vastuu> loydetyt = new ArrayList<Vastuu>();
    	
    	try ( Connection con = kanta.annaKantayhteys();
        	  PreparedStatement sql = con.prepareStatement("SELECT * FROM Vastuut WHERE osastoNro = ?")
    	        ) {
        	sql.setInt(1, osastoNro);
        	try ( ResultSet tulokset = sql.executeQuery() )  {
            	while ( tulokset.next() ) {
            		Vastuu v = new Vastuu();
            		v.parse(tulokset);
            		loydetyt.add(v);
    			}
        	}
        	
		} catch (SQLException e) {
			throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
		}
    	return loydetyt;
    }
    
    public Collection<Vastuu> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = apuvastuu.getKysymys(k);
        if ( k < 0 ) { kysymys = apuvastuu.getKysymys(0); ehto = ""; }
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Vastuut WHERE " + kysymys + " LIKE ?") ) {
            ArrayList<Vastuu> loytyneet = new ArrayList<Vastuu>();
            
            sql.setString(1, "%" + ehto + "%");
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                	Vastuu v = new Vastuu();
                    v.parse(tulokset);
                    loytyneet.add(v);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /** 
     * Poistaa vastuut-listasta pyydetyn henkilön vastuut,
     * eli vastuulinkitykset tuoteosastoihin poistuvat, mutta henkilön tiedot säilyy
     * edelleen tiedostossa
     * @param henkiloNro viite siihen, keneen henkilöön liittyvät tietueet poistetaan 
     * @throws SailoException 
     */ 
    public void poistaHenkilonVastuut(int henkiloNro) throws SailoException { 
    	try ( Connection con = kanta.annaKantayhteys();
        	  PreparedStatement sql = con.prepareStatement("DELETE FROM Vastuut WHERE henkiloNro = ?")
    	        ) {
        	sql.setInt(1, henkiloNro);
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
	            Vastuut vastuut = new Vastuut("kokeilu");
	    
	            Vastuu v1 = new Vastuu(), v2 = new Vastuu();
				v1.setOsastonro(2); v1.setHenkilonro(5);
				v2.setOsastonro(4); v2.setHenkilonro(7);
				vastuut.lisaa(v1); vastuut.lisaa(v2);
	            v1.tulosta(System.out);
				
				System.out.println("================= Vastuut testi ====================");
				
				int i = 0;
	            for (Vastuu vastuu:vastuut.etsi("", -1)) {
	                System.out.println("Vastuu nro: " + i++);
	                vastuu.tulosta(System.out);
	            }
	            
	            new File("kokeilu.db").delete();
	        } catch ( SailoException ex ) {
	            System.out.println(ex.getMessage());
	        }

	}

}
