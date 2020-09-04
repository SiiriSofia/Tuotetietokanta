/**
 * 
 */
package kauppa;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka yksittäiselle vastuulle (=relaation liima, sisältää henkiloNro-osastoNro-parin)
 * Vastuut:
 *  -  Tietää kunkin vastuuparin sisältämän
 *     henkilöolion numeron ja tuoteosaston numeron
 *  -  Muuttaa tolppaerotellun tiedon tiedostosta
 *     käyttöliittymän hyödynnettävään muotoon
 * @author Siiri
 * @version 4 Mar 2020
 * @version 28 Mar 2020
 */
public class Vastuu {
	
	private int osastoNro;
	private int henkiloNro;
	
	
	public Vastuu() {
		this.osastoNro = 0;
		this.henkiloNro = 0;
	}
	
	/**
     * Antaa tietokannan luontilausekkeen vastuutaululle
     * @return vastuutaulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Vastuut (" +
                "osastoNro INTEGER NOT NULL , " +
                "henkiloNro INTEGER NOT NULL, " +
                "PRIMARY KEY (osastoNro, henkiloNro) " +
                "FOREIGN KEY (osastoNro) REFERENCES Osastot(osastoNro)" +
                "FOREIGN KEY (henkiloNro) REFERENCES Henkilot(henkiloNro)" +
                ")";
    }
    

    /**
     * Antaa vastuun lisäyslausekkeen
     * @param con tietokantayhteys
     * @return vastuun lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Vastuut " +
                "(osastoNro, henkiloNro) " +
                "VALUES (?, ?)");
        
        if ( osastoNro != 0 ) sql.setInt(1, osastoNro);
        if ( henkiloNro != 0 ) sql.setInt(2, henkiloNro);
        
        return sql;
    }
    
    /** 
     * Ottaa vastuun tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setOsastonro(tulokset.getInt("osastoNro"));
        setHenkilonro(tulokset.getInt("henkiloNro"));
    }
    
    /**
     * Antaa vastuun päivityslausekkeen
     * @param con tietokantayhteys
     * @return vastuun päivityslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPaivityslauseke(Connection con)
            throws SQLException {
        return null;
    }
    
    /**
     * Antaa vastuun poistolausekkeen
     * @param con tietokantayhteys
     * @return vastuun poistolauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPoistolauseke(Connection con)
            throws SQLException {
        return null;
    }
    
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    /*public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id1 = rs.getInt(1);
        if ( id1 == osastoNro) return;
        setOsastonro(id1);
    }*/
    
    public String getKysymys(int k) {
        switch ( k ) {
        case 0: return "osastoNro";
        case 1: return "henkiloNro";
        default: return "eimittää";
        }
    }
	
	/**
	 * Hakee vastuuseen liittyvän henkilön henkiloNron
	 * @return henkilönumero
	 */
	public int getHenkilonro() {
		return henkiloNro;
	}
	
	/**
	 * Asettaa vastuulle uuden henkiloNron
	 * @param nro asetettava numero
	 */
	public void setHenkilonro(int nro) {
		this.henkiloNro = nro;
	}
	
	/**
	 * Hakee vastuuseen liittyvän tuoteosaston osastoNron
	 * @return osastonumero
	 */
	public int getOsastonro() {
		return osastoNro;
	}
	
	/**
	 * Asettaa vastuulle uuden osastoNron
	 * @param nro asetettava numero
	 */
	public void setOsastonro(int nro) {
		this.osastoNro = nro;
	}

	
	/**
	 * Tulostetaan vastuun tiedot
	 * @param out tietovirta johon tulostetaan
	 */
	public void tulosta(PrintStream out) {
		out.println("Henkilö nro " + getHenkilonro() + " vastaa osastosta nro " + getOsastonro());
	}
	
	/**
	 * Tulostetaan vastuun tiedot
	 * @param os tietovirta johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}
	
	   /**
	    * Palauttaa vastuun tiedot merkkijonona, jonka voi tallentaa tiedostoon
	    * @return vastuu tolppaeroteltuna merkkijonona
	    * @example
	    * <pre name="test">
	    * Vastuu vastuu = new Vastuu();
	    *   vastuu.parse("2|5");
	    *   vastuu.toString().startsWith("2|5") === true;
	    * </pre>
	    */
	   @Override
	    public String toString() {
	       return "" +
	    		   getHenkilonro() + "|" +
	    		   getOsastonro();
	   }
	   
	   /**
	    *  Selvittää vastuun tiedot tolppaerotellusta merkkijonosta
	    * @param rivi josta vastuun tiedot otetaan
	    * @example
	    * <pre name="test">
	    *   Vastuu vastuu = new Vastuu();
	    *   vastuu.parse("2|5");
	    *   vastuu.toString().startsWith("2|5") === true;
	    */
	   public void parse(String rivi) {
	       StringBuffer sb = new StringBuffer(rivi);
	       setHenkilonro(Mjonot.erota(sb, '|', getHenkilonro()));
	       setOsastonro(Mjonot.erota(sb, '|', getOsastonro()));
	   }

	   
	   @Override
	   public boolean equals(Object osasto) {
	      if ( osasto == null ) return false;
	       return this.toString().equals(osasto.toString());
	   }
	  
	  
	   @Override
	   public int hashCode() {
	        return osastoNro;
	   }
	   
	
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		
		Vastuu v1 = new Vastuu();
		Vastuu v2 = new Vastuu();
		Vastuu v3 = new Vastuu();
		Osasto o1 = new Osasto(); o1.rekisteroi();
		Osasto o2 = new Osasto(); o2.rekisteroi();
		Osasto o3 = new Osasto(); o3.rekisteroi();
		Henkilo h1 = new Henkilo(); h1.rekisteroi();
		Henkilo h2 = new Henkilo(); h2.rekisteroi();
		Henkilo h3 = new Henkilo(); h3.rekisteroi();
		
		v1.setOsastonro(o1.getOsastonro());
		v1.setHenkilonro(h1.getHenkilonro());
		
		v2.setOsastonro(o2.getOsastonro());
		v2.setHenkilonro(h3.getHenkilonro());
		
		v3.setOsastonro(o3.getOsastonro());
		v3.setHenkilonro(h2.getHenkilonro());
		
		v1.tulosta(System.out);
		v2.tulosta(System.out);
		v3.tulosta(System.out);

	}

}
