/**
 * 
 */
package kauppa;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka yhdelle tuoteosastolle
 * Vastuut:
 * -  tietää tuoteosaston kentät (tuoteosaston nimi, 
 *    pinta-ala ja hyllymäärä)
 * -  osaa muuttaa tolppaerotellun tekstitiedoston
 *    käyttöliittymän kenttien tekstiksi   
 * @author Siiri
 * @version 4 Mar 2020
 * @version 28 Mar 2020
 */
public class Osasto {

	private int osastoNro = 0;
	private String osastoNimi = null;
	private int osastoKoko = 0;
	private int osastoHyllyMaara = 0;
	
	private static int seuraavaNro = 1;
	
	
	/**
     * Antaa tietokannan luontilausekkeen osastotaululle
     * @return osastotaulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Osastot (" +
                "osastoNro INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "osastoNimi VARCHAR(100), " +
                "osastoKoko INTEGER, " +
                "osastoHyllyMaara INTEGER " +
                ")";
    }
    
    /**
     * Palauttaa k:tta osaston kenttää vastaavan kysymyksen
     * @param k kuinka monennen kentän kysymys palautetaan (0-alkuinen)
     * @return k:netta kenttää vastaava kysymys
     */
    public String getKysymys(int k) {
        switch ( k ) {
        case 0: return "osastoNro";
        case 1: return "osastoNimi";
        case 2: return "osastoKoko";
        case 3: return "osastoHyllyMaara";
        default: return "eimittää";
        }
    }
    

    /**
     * Antaa osaston lisäyslausekkeen
     * @param con tietokantayhteys
     * @return osaston lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Osastot" +
                "(osastoNro, osastoNimi, osastoKoko, osastoHyllyMaara) " +
                "VALUES (?, ?, ?, ?)");
        if ( osastoNro != 0 ) sql.setInt(1, osastoNro); else sql.setString(1, null);
        sql.setString(2, osastoNimi);
        sql.setInt(3, osastoKoko);
        sql.setInt(4, osastoHyllyMaara);
        
        return sql;
    }

    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == osastoNro ) return;
        setOsastonro(id);
    }
    
    /** 
     * Ottaa osaston tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setOsastonro(tulokset.getInt("osastoNro"));
        osastoNimi = tulokset.getString("osastoNimi");
        osastoKoko = tulokset.getInt("osastoKoko");
        osastoHyllyMaara = tulokset.getInt("osastoHyllyMaara");
        osastoNro = tulokset.getInt("osastoNro");
    }
    
    /**
     * Antaa osaston päivityslausekkeen
     * @param con tietokantayhteys
     * @return osaston päivityslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPaivityslauseke(Connection con)
            throws SQLException {
        return null;
    }
    
    /**
     * Antaa osaston poistolausekkeen
     * @param con tietokantayhteys
     * @return osaston poistolauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPoistolauseke(Connection con)
            throws SQLException {
        return null;
    }
    
	/**
	 * Tulostetaan tuoteosaston tiedot
	 * @param out tietovirta johon tulostetaan
	 */
	public void tulosta(PrintStream out) {
		out.println(String.format("%03d", osastoNro) + "  " + osastoNimi + 
				", koko: " + osastoKoko + " m2,  hyllymäärä: " + osastoHyllyMaara);
	}
	
	/**
	 * Tulostetaan tuoteosaston tiedot
	 * @param os tietovirta johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}
	
	/**
	 * Antaa tuoteosastolle seuraavan osastonumeron
	 * @return osaston uusi osastoNro
	 * @example
	 * <pre name="test">
	 * Osasto o1 = new Osasto();
	 * o1.getOsastonro() === 0;
	 * o1.rekisteroi();
	 * Osasto o2 = new Osasto();
	 * o2.rekisteroi();
	 * int n1 = o1.getOsastonro();
	 * int n2 = o2.getOsastonro();
	 * n1 = n2-1;
	 * </pre>
	 */
	public int rekisteroi() {
		osastoNro = seuraavaNro;
		seuraavaNro++;
		return osastoNro;
	}
	
	/**
	 * Hakee tuoteosaston numeron
	 * @return osastonumero
	 */
	public int getOsastonro() {
		return osastoNro;
	}
	
	/**
	 * Asettaa uuden osastonumeron
	 * @param nro asetettava uusi osastonro
	 */
	public void setOsastonro(int nro) {
		this.osastoNro = nro;
		if (osastoNro >= seuraavaNro) seuraavaNro = osastoNro + 1;
	}
	
	/**
	 * Hakee osaston koon
	 * @return osaston koko
	 */
	public int getOsastonkoko() {
		return this.osastoKoko;
	}
	
	/**
	 * Hakee osaston hyllymäärän
	 * @return osaston hyllymäärä
	 */
	public int getOsastohyllymaara() {
		return this.osastoHyllyMaara;
	}
	
	/**
	 * Hakee tuoteosaston nimen
	 * @return osaston nimi
	 */
	public String getOsastonimi() {
		return osastoNimi;
	}
	

	/**
	 * Apumetodi testiesimerkkien täyttämiseen
	 * ->Poista tämä kun kaikki pelittää!
	 */
	public void taytaTestiTiedoilla() {
		osastoNimi = "Testiosasto" + rand(1,20);
		osastoKoko = rand(10,80);
		osastoHyllyMaara = rand(50,100);
	}
	
	/**
	 * Arvotaan satunnainen kokonaisluku väliltä [ala,yla]
	 * @param ala arvonnan alaraja
	 * @param yla arvonnan yläraja
	 * @return satunnainen kokonaisluku väliltä [ala,ylä]
	 */
	public static int rand(int ala, int yla) {
		double n = (yla-ala)*Math.random() + ala;
		return (int)Math.round(n);
	}
	
    /**
    * Palauttaa osaston tiedot merkkijonona, jonka voi tallentaa tiedostoon
    * @return osasto tolppaeroteltuna merkkijonona
    * @example
    * <pre name="test">
    * Osasto osasto = new Osasto();
    *   osasto.parse("2|Osasto2|40|16");
    *   osasto.toString().startsWith("2|Osasto2|40|16") === true;
    * </pre>
    */
   @Override
    public String toString() {
       return "" +
    		   getOsastonro() + "|" +
    		   osastoNimi + "|" +
               osastoKoko + "|" +
               osastoHyllyMaara;
   }
   
   /**
    *  Selvittää osaston tiedot tolppaerotellusta merkkijonosta
    *  Pitää huolen että seuraava seuraavaNro on suurempi kuin osastoNro
    * @param rivi josta osaston tiedot otetaan
    * @example
    * <pre name="test">
    *   Osasto osasto = new Osasto();
    *   osasto.parse("2|Osasto2|40|16");
    *   osasto.getOsastonro() === 2;
    *   osasto.toString().startsWith("2|Osasto2|40|16") === true;
    *   osasto.rekisteroi();
    *   int n = osasto.getOsastonro();
    *   osasto.parse(""+(n+4));
    *   osasto.rekisteroi();           // ja tarkistetaan että seuraavalla kertaa tulee yhtä isompi
    *   osasto.getOsastonro() === n+4+1;
    */
   public void parse(String rivi) {
       StringBuffer sb = new StringBuffer(rivi);
       setOsastonro(Mjonot.erota(sb, '|', getOsastonro()));
       osastoNimi = Mjonot.erota(sb, '|', osastoNimi);
       osastoKoko = Mjonot.erota(sb, '|', osastoKoko);
       osastoHyllyMaara = Mjonot.erota(sb, '|', osastoHyllyMaara);
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
		Osasto o1 = new Osasto();
		Osasto o2 = new Osasto();
		
		o1.rekisteroi();
		o2.rekisteroi();
		
		o1.tulosta(System.out);
		o1.taytaTestiTiedoilla();
		o1.tulosta(System.out);
		
		o2.tulosta(System.out);
		o2.taytaTestiTiedoilla();
		o2.tulosta(System.out);
		
		Osasto osasto = new Osasto();
	    osasto.parse("2|Osasto2|40|16");
	    System.out.println(osasto.getOsastonro());
	    osasto.rekisteroi();
	    int n = osasto.getOsastonro();
	    osasto.parse(""+(n+4));
	    osasto.rekisteroi();
	    System.out.println(osasto.getOsastonro());

	}

}
