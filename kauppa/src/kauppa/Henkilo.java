/**
 * 
 */
package kauppa;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;
import kanta.SisaltaaTarkistaja;
import kanta.Tietue;

/**
 * Luokka yksittäiselle henkilölle
 * Vastuut:
 * -  tietää henkilön nimen ja puhelinnumeron
 * -  osaa muuttaa nimen alkamaan isolla alkukirjai-
 *    mella 
 * -  osaa pilkkoa nimen "Etunimi Sukunimi"-muodosta
 *    erillisiksi etunimeksi ja sukunimeksi
 * -  osaa tarkistaa puhelinnumeron muodon oikeel- 
 *    lisuuden
 * -  osaa muuttaa tolppaerotellun tekstitiedoston
 *    käyttöliittymän kenttien tekstiksi    
 * @author Siiri
 * @version 3 Mar 2020
 * @version 28 Mar 2020
 */
public class Henkilo implements Cloneable, Tietue {

	private int henkiloNro = 0;
	private String henkiloNimi = "";
	private String puhNro = "";
	
	private static int seuraavaNro = 1;
	
	/**
     * Antaa tietokannan luontilausekkeen henkilotaululle
     * @return henkilotaulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Henkilot (" +
                "HenkiloId INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "Nimi VARCHAR(100) NOT NULL, " +
                "Puhelinnumero VARCHAR(100) " +
                ")";
    }
    

    /**
     * Antaa henkilön lisäyslausekkeen
     * @param con tietokantayhteys
     * @return henkilön lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Henkilot " +
                "(HenkiloId, Nimi, Puhelinnumero) " +
                "VALUES (?, ?, ?)");
        if ( henkiloNro != 0 ) sql.setInt(1, henkiloNro); else sql.setString(1, null);
        sql.setString(2, henkiloNimi);
        sql.setString(3, puhNro);
        
        return sql;
    }
    
    /**
     * Antaa henkilön päivityslausekkeen eli päivittää tarvittaessa
     * muuttuneen puhelinnumeron, jos syötetty taulussa olemassa
     * olevalle henkilölle muu kuin tyhjä puhelinnumero
     * @param con tietokantayhteys
     * @return henkilön päivityslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPaivityslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("UPDATE Henkilot " +
                "SET Puhelinnumero = ? WHERE HenkiloId = ? ");
        /*if ( ! puhNro.trim().equals("")) */sql.setString(1, puhNro);
        sql.setInt(2,  henkiloNro);
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
        if ( id == henkiloNro ) return;
        setHenkilonro(id);
    }
    
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public boolean tarkistaNimi(ResultSet rs) throws SQLException {
        String nimi = rs.getString(2);
        if ( nimi.equals(henkiloNimi) ) return true;
        return false;
    }

    /** 
     * Ottaa henkilön tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setHenkilonro(tulokset.getInt("HenkiloId"));
        henkiloNimi = tulokset.getString("Nimi");
        puhNro = tulokset.getString("Puhelinnumero");
    }

    /**
     * Antaa henkilön poistolausekkeen
     * @param con tietokantayhteys
     * @return henkilön poistolauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPoistolauseke(Connection con)
            throws SQLException {
        return null;
    }
	
    /**
     * @return henkilön kenttien lukumäärä
     */
    public int getKenttia() {
        return 3;
    }
    
   	 /**
	  * @return ensimmäinen käyttäjän syötettävän kentän indeksi
	  */
	 public int ekaKentta() {
	     return 1;
	 }

   /**
    * @param k minkä kentän kysymys halutaan
    * @return valitun kentän kysymysteksti
    */
   public String getKysymys(int k) {
       switch (k) {
           case 0:
               return "HenkiloId";
           case 1:
               return "Nimi";
           case 2:
               return "Puhelinnumero";
           default:
               return "jotain";
       }
   }
   
   	/**
      * @param k Minkä kentän sisältö halutaan
      * @return valitun kentän sisältö
      * @example
      * <pre name="test">
      *   Henkilo henkilo = new Henkilo();
      *   henkilo.parse("4|Katto Kassinen|040123456");
      *   henkilo.anna(0) === "4";   
      *   henkilo.anna(1) === "Katto Kassinen";   
      *   henkilo.anna(2) === "040123456";
      * </pre>
   */
     public String anna(int k) {
         switch (k) {
             case 0:
                 return "" + henkiloNro;
             case 1:
                 return henkiloNimi;
            case 2:
                return puhNro;
            default:
                return "jotai";
        }
    }
	
     /**
	   * Asetetaan valitun kentän sisältö.  Mikäli asettaminen onnistuu,
	   * palautetaan null, muutoin virheteksti.
	   * @param k minkä kentän sisältö asetetaan
	   * @param s asetettava sisältö merkkijonona
	   * @return null jos ok, muuten virheteksti
	   * @example
	   * <pre name="test">
	   *   Henkilo henkilo = new Henkilo();
	   *   henkilo.aseta(0,"3") === null;
	   *   henkilo.aseta(1,"Katto Kassinen")  === null;
	   *   henkilo.aseta(2,"040asd123456") === "Saa olla vain merkkejä: 0123456789- ";
	   *   henkilo.aseta(2,"040-124321")    === null;
	   *   henkilo.aseta(2,"040 124321")    === null;
	   *   
	   * </pre>
	   */
	  public String aseta(int k, String s) {
	      String st = s.trim();
	      StringBuffer sb = new StringBuffer(st);
	      switch (k) {
	          case 0:
	              setHenkilonro(Mjonot.erota(sb, '$', getHenkilonro()));
	          return null;
	      case 1:
	          henkiloNimi = Mjonot.erota(sb, '$', henkiloNimi);
	          return null;
	      case 2:
	    	  SisaltaaTarkistaja tark = new SisaltaaTarkistaja("0123456789- ");
	          String virhe = tark.tarkista(st);
	          if ( virhe != null ) return virhe;
	          puhNro = st;
	          return null;
	      default:
	          return "Väärä kentän indeksi";
	      }
	  }
	  
	  /**
	    * Tehdään identtinen klooni henkilosta	   
	    * @return Object kloonattu henkilo
	    * @example
	    * <pre name="test">
	    * #THROWS CloneNotSupportedException 
	    *   Henkilo henkilo = new Henkilo();
	    *   henkilo.parse("1|Katto Kassinen|040 1234567");
	    *   Henkilo kopio = henkilo.clone();
	    *   kopio.toString() === henkilo.toString();
	    *   henkilo.parse("1|Väiski Vemmelsääri|040 9876543");
	    *   kopio.toString().equals(henkilo.toString()) === false;
	    * </pre>
	    */
	   @Override
	   public Henkilo clone() throws CloneNotSupportedException { 
	       return (Henkilo)super.clone();
	   }
	 
	/**
	 * Antaa henkilölle seuraavan henkilönumeron
	 * @return henkilön uusi henkiloNro
	 * @example
	 * <pre name="test">
	 * Henkilo h1 = new Henkilo();
	 * h1.getHenkilonro() === 0;
	 * h1.rekisteroi();
	 * Henkilo h2 = new Henkilo();
	 * h2.rekisteroi();
	 * int n1 = h1.getHenkilonro();
	 * int n2 = h2.getHenkilonro();
	 * n1 = n2-1;
	 * </pre>
	 */
	public int rekisteroi() {
		henkiloNro = seuraavaNro;
		seuraavaNro++;
		return henkiloNro;
	}
	
	/**
	 * Hakee henkilön numeron
	 * @return henkilönro
	 */
	public int getHenkilonro() {
		return henkiloNro;
	}
	
	public void setHenkilonro(int nro) {
		this.henkiloNro = nro;
		if (henkiloNro >= seuraavaNro) seuraavaNro = henkiloNro + 1;
	}
	
	/**
	 * Hakee henkilön nimen
	 * @return henkilön nimi
	 */
	public String getHenkilonimi() {
		return henkiloNimi;
	}

	
	/**
	 * Hakee henkilön puhelinnumeron
	 * @return puhelinnumero
	 */
	public String getPuhnro() {
		return puhNro;
	}
	
	/**
	 * Asettaa uuden puhelinnumeron
	 * @param puhnro
	 */
	public void setPuhnro(String puhnro) {
		this.puhNro = puhnro;
	}
	
	/**
	 * Apumetodi testiesimerkkien täyttämiseen
	 * ->Poista tämä kun kaikki pelittää!
	 */
	public void taytaTestiTiedoilla() {
		henkiloNimi = "Testi Testinen" + rand(10,99);
		puhNro = "014-222" + rand(100,999);
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
	 * Tulostetaan henkilön tiedot
	 * @param out tietovirta johon tulostetaan
	 */
	public void tulosta(PrintStream out) {
		out.println(henkiloNimi + ",  puh. " + puhNro);
	}
	
	/**
	 * Tulostetaan tuotteen tiedot
	 * @param os tietovirta johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}
	
    /**
    * Palauttaa henkilön tiedot merkkijonona, jonka voi tallentaa tiedostoon
    * @return henkilö tolppaeroteltuna merkkijonona
    * @example
    * <pre name="test">
    * Henkilo henkilo = new Henkilo();
    *   henkilo.parse("5|Testi Testinen|014-222333");
    *   henkilo.toString().startsWith("5|Testi Testinen|014-222333") === true;
    * </pre>
    */
   @Override
    public String toString() {
       return "" +
    		   getHenkilonro() + "|" +
    		   henkiloNimi + "|" +
               puhNro + "|";
   }
   
   /**
    *  Selvittää henkilön tiedot tolppaerotellusta merkkijonosta
    *  Pitää huolen että seuraava seuraavaNro on suurempi kuin henkiloNro
    * @param rivi josta osaston tiedot otetaan
    * @example
    * <pre name="test">
    *   Henkilo henkilo = new Henkilo();
    *   henkilo.parse("5|Testi Testinen|014-222333");
    *   henkilo.getHenkilonro() === 5;
    *   henkilo.toString().startsWith("5|Testi Testinen|014-222333") === true;
    *   henkilo.rekisteroi();
    *   int n = henkilo.getHenkilonro();
    *   henkilo.parse(""+(n+4));
    *   henkilo.rekisteroi();           // ja tarkistetaan että seuraavalla kertaa tulee yhtä isompi
    *   henkilo.getHenkilonro() === n+4+1;
    */
   public void parse(String rivi) {
       StringBuffer sb = new StringBuffer(rivi);
       setHenkilonro(Mjonot.erota(sb, '|', getHenkilonro()));
       henkiloNimi = Mjonot.erota(sb, '|', henkiloNimi);
       puhNro = Mjonot.erota(sb, '|', puhNro);
   }
   
   @Override
   public boolean equals(Object henkilo) {
      if ( henkilo == null ) return false;
       return this.toString().equals(henkilo.toString());
   }
  
  
   @Override
   public int hashCode() {
        return henkiloNro;
   }
   
	
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Henkilo h1 = new Henkilo();
		Henkilo h2 = new Henkilo();
		
		h1.rekisteroi();
		h2.rekisteroi();
		
		h1.tulosta(System.out);
		h1.taytaTestiTiedoilla();
		h1.tulosta(System.out);
		
		h2.tulosta(System.out);
		h2.taytaTestiTiedoilla();
		h2.tulosta(System.out);

	}

}
