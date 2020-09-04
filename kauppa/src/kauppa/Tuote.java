/**
 * 
 */
package kauppa;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;
import java.util.Comparator;

import kanta.SisaltaaTarkistaja;
import kanta.Tietue;

/**
 * Luokka yksittäiselle tuotteelle
 * Vastuut:
 * -  tietää tuotteen kentät (tuotteen nimi, hinta,
 *    ja osaston ja hyllyn numero jossa tuote
 *    sijaitsee)
 * -  osaa tarkistaa, onko tuotteen hinta syötetty
 *    oikeassa muodossa
 * -  osaa trimmata tuotteen nimestä ylimääräiset
 *    välilyönnit pois
 * -  osaa antaa merkkijonona i:nnen kentän tiedot
 * -  osaa laittaa merkkijonon i:nneksi kentäksi
 * -  osaa muuttaa tolppaerotellun tekstitiedoston
 *    käyttöliittymän kenttien tekstiksi
 * @author Siiri
 * @version 18 Feb 2020
 * @version 28 Mar 2020
 * @version 1 Apr 2020 - Haku- ja vertailuominaisuuksien lisäys
 */
public class Tuote implements Cloneable, Tietue {

	private int tuoteNro = 0;
	private String tuoteNimi = "";
	private double tuoteHinta;
	private int tuoteMaxMaara;
	private int osastoNro;
	
	private static int seuraavaNro = 1;
	
	
	/**
     * Antaa tietokannan luontilausekkeen tuotetaululle
     * @return tuotetaulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Tuotteet (" +
                "tuoteNro INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "Tuotenimi VARCHAR(100) NOT NULL, " +
                "Hinta DECIMAL(10,2), " +
                "Maxmaara INTEGER, " +
                "Osastonro INTEGER, " +
                "FOREIGN KEY (osastoNro) REFERENCES Osastot(osastoNro)" +
                ")";
    }
    

    /**
     * Antaa tuotteen lisäyslausekkeen
     * @param con tietokantayhteys
     * @return tuotteen lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Tuotteet" +
                "(tuoteNro, Tuotenimi, Hinta, Maxmaara, Osastonro) " +
                "VALUES (?, ?, ?, ?, ?)");
        
        if ( tuoteNro != 0 ) sql.setInt(1, tuoteNro); else sql.setString(1, null);
        sql.setString(2, tuoteNimi);
        sql.setDouble(3, tuoteHinta);
        sql.setInt(4, tuoteMaxMaara);
        sql.setInt(5, osastoNro);
        
        return sql;
    }
    
    /**
     * Antaa tuotteen päivityslausekkeen
     * @param con tietokantayhteys
     * @return tuotteen päivityslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaPaivityslauseke(Connection con)
            throws SQLException {
    	PreparedStatement sql = con.prepareStatement("UPDATE Tuotteet " +
                "SET Tuotenimi = ?, Hinta = ?, " +
    			"Maxmaara = ?, Osastonro = ? " +
                "WHERE tuoteNro = ? ");
    	sql.setString(1, tuoteNimi);
        sql.setDouble(2, tuoteHinta);
        sql.setInt(3, tuoteMaxMaara);
        sql.setInt(4, osastoNro);
    	sql.setInt(5, tuoteNro);
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
        if ( id == tuoteNro ) return;
        setTuotenro(id);
    }
    
    /** 
     * Ottaa tuotteen tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTuotenro(tulokset.getInt("tuoteNro"));
        tuoteNimi = tulokset.getString("Tuotenimi");
        tuoteHinta = tulokset.getDouble("Hinta");
        tuoteMaxMaara = tulokset.getInt("Maxmaara");
        osastoNro = tulokset.getInt("Osastonro");
    }
    
	/**
    * Palauttaa tuotteiden kenttien lukumäärän
    * @return kenttien lukumäärä
    */
   public int getKenttia() {
       return 5;
   }


   /**
   * Eka kenttä joka on mielekäs kysyttäväksi
   * @return ekan kentän indeksi
   */
    public int ekaKentta() {
    	return 1;
    }
    
   /**
    * Antaa k:n kentän sisällön merkkijonona
    * @param k monenenko kentän sisältö palautetaan
    * @return kentän sisältö merkkijonona
    */
    public String anna(int k) {
        switch ( k ) {
        	case 0: return "" + tuoteNro;
        	case 1: return "" + tuoteNimi;
        	case 2: return String.format("%4.2f", tuoteHinta);
        	case 3: return "" + tuoteMaxMaara;
        	case 4: return "" + osastoNro;
        	default: return "eimittää";
          	}
    }
    
    /**
      * Asettaa k:n kentän arvoksi parametrina tuodun merkkijonon arvon
      * @param k kuinka monennen kentän arvo asetetaan
      * @param jono jonoa joka asetetaan kentän arvoksi
      * @return null jos asettaminen onnistuu, muuten vastaava virheilmoitus.
      */
    public String aseta(int k, String jono) {
	 	String tjono = jono.trim();
	    StringBuffer sb = new StringBuffer(tjono);
	    SisaltaaTarkistaja tark;
	    switch ( k ) {
	         case 0:
	             setTuotenro(Mjonot.erota(sb, '§', getTuotenro()));
	             return null;
	         case 1:
	             tuoteNimi = tjono;
	             return null;
	         case 2:
	             try {
	                 tuoteHinta = Double.parseDouble(tjono);
	                 return null;
		        } catch ( NumberFormatException ex ) {
		       	 	tark = new SisaltaaTarkistaja("0123456789.");
		            return tark.tarkista(tjono);
		        }
	         case 3:
	        	 try {
	            	 tuoteMaxMaara = Integer.parseInt(tjono);
	                 return null;
		         } catch ( NumberFormatException ex ) {
		        	 tark = new SisaltaaTarkistaja("0123456789");
	                 return tark.tarkista(tjono);
		         }
	         case 4:
	        	 try {
	                 osastoNro = Integer.parseInt(tjono);
	                 return null;
	        	 } catch ( NumberFormatException ex ) {
	        		 tark = new SisaltaaTarkistaja("0123456789");
	        		 return tark.tarkista(tjono);
	        	 }
	         default:
	             return "eimittää";
	         }
     }
     
     /**  
     * Tuotteen vertailija  
     */  
    public static class Vertailija implements Comparator<Tuote> {  
        private int k;   
          
        public Vertailija(int k) {  
            this.k = k;  
        }  
          
        @Override  
        public int compare(Tuote tuote1, Tuote tuote2) {  
            return tuote1.getAvain(k).compareToIgnoreCase(tuote2.getAvain(k));  
        }  
    }  
      
  		     
    /**  
     * Antaa k:n kentän sisällön merkkijonona  
     * @param k monenenko kentän sisältö palautetaan  
     * @return kentän sisältö merkkijonona  
     */  
    public String getAvain(int k) {  
        switch ( k ) {  
        case 0: return "" + tuoteNro;  
        case 1: return "" + tuoteNimi.toUpperCase();  
        case 2: return "" + tuoteHinta;
        case 3: return "" + tuoteMaxMaara;  
        case 4: return "" + osastoNro;  
        default: return "nonii";  
        }  
    }  

	/**
	 * Tulostetaan tuotteen tiedot
	 * @param out tietovirta johon tulostetaan
	 */
	public void tulosta(PrintStream out) {
		String tuoteNimiCap = tuoteNimi.substring(0, 1).toUpperCase() + tuoteNimi.substring(1);
		out.println(tuoteNimiCap);
		out.println("Hinta: " + String.format("%4.2f", tuoteHinta) + " e. Varastokapasiteetti: " + tuoteMaxMaara);
	}
	
	/**
      * Palauttaa k:tta tuotetta kenttää vastaavan kysymyksen
      * @param k kuinka monennen kentän kysymys palautetaan (0-alkuinen)
      * @return k:netta kenttää vastaava kysymys
      */
     public String getKysymys(int k) {
         switch ( k ) {
         case 0: return "tuoteNro";
         case 1: return "Tuotenimi";
         case 2: return "Hinta";
         case 3: return "Maxmaara";
         case 4: return "Osastonro";
         default: return "eimittää";
         }
     }
	
	/**
	 * Tulostetaan tuotteen tiedot
	 * @param os tietovirta johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}
	
	/**
	 * Antaa tuotteelle seuraavan tuotenumeron
	 * @return tuotteen uusi tuoteNro
	 * @example
	 * <pre name="test">
	 * Tuote tuote1 = new Tuote();
	 * tuote1.getTuotenro() === 0;
	 * tuote1.rekisteroi();
	 * Tuote tuote2 = new Tuote();
	 * tuote2.rekisteroi();
	 * int n1 = tuote1.getTuotenro();
	 * int n2 = tuote2.getTuotenro();
	 * n1 = n2-1;
	 * </pre>
	 */
	public int rekisteroi() {
		tuoteNro = seuraavaNro;
		seuraavaNro++;
		return tuoteNro;
	}
	
	/**
	 * Hakee tuotteen numeron
	 * @return tuotenumero
	 */
	public int getTuotenro() {
		return tuoteNro;
	}

	/**
	* Asettaa tunnusnumeron ja samalla varmistaa että
	* seuraava numero on aina suurempi kuin tähän mennessä suurin.
	* @param nr asetettava tunnusnumero
    */
	private void setTuotenro(int nr) {
		tuoteNro = nr;
		if (tuoteNro >= seuraavaNro) seuraavaNro = tuoteNro + 1;
   }

	/**
	 * Asettaa uuden merkkijonon tuotenimi-muuttujaan
	 * @param s merkkijono joka asetetaan tuotenimeksi
	 */
	public String setTuotenimi(String s) {
		this.tuoteNimi= s;
		return tuoteNimi;
	}
	/**
	 * Asettaa uuden liukuluvun tuotehinta-muuttujaan
	 * @param s merkkijonona asetettava hinta
	 */
	public double setTuotehinta(String s) {
		this.tuoteHinta = Double.parseDouble(s);
		return this.tuoteHinta;
	}
	/**
	 * Asettaa uuden numeron osastonro-muuttujaan
	 * @param s merkkijonona asetettava osastonumero
	 */
	public int setOsastonro(String s) {
		this.osastoNro = Integer.parseInt(s);
		return this.osastoNro;
	}
	
	/**
	 * Hakee tuotteen nimen
	 * @return tuotenimi
	 */
	public String getTuotenimi() {
		return tuoteNimi;
	}
	
	public int getOsastonro() {
		return this.osastoNro;
	}
	

	/**
	 * Hakee tuotteen varastokapasiteetin eli maksimimäärän
	 * @return tuotteen maksimimäärä
	 */
	public int getMaxMaara() {
		return tuoteMaxMaara;
	}

	/**
	 * Hakee tuotteen hinnan
	 * @return tuotteen hinta
	 */
	public double getTuotehinta() {
		return tuoteHinta;
	}
	
	/**
	 * Apumetodi testiesimerkkien täyttämiseen
	 * ->Poista tämä kun kaikki pelittää!
	 */
	public void taytaTestiTiedoilla() {
		tuoteNimi = "Testituote" + rand(1000,9999);
		tuoteHinta = rand(1,10);
		tuoteMaxMaara = rand(10,100);
		osastoNro = rand(1,6); //oletetaan testivaiheessa että luotuna 6 tuoteosastoa
	}
	
	/**
	 * Apumetodi testiesimerkkien täyttämiseen
	 * ->Poista tämä kun kaikki pelittää!
	 */
	public void taytaTestiTiedoilla(int osastoNro) {
		tuoteNimi = "Testituote " + rand(1000,9999);
		tuoteHinta = rand(1,10);
		tuoteMaxMaara = rand(10,100);
		this.osastoNro = osastoNro;
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
     * Palauttaa tuotteen tiedot merkkijonona, jonka voi tallentaa tiedostoon
     * @return tuote tolppaeroteltuna merkkijonona
     * @example
     * <pre name="test">
     * Tuote tuote = new Tuote();
     *   tuote.parse("10|Testituote192|0.50|46|3");
     *   tuote.toString().startsWith("10|Testituote192|0.50|") === true; // on enemmänkin kuin 3 kenttää, siksi loppu |
     * </pre>
     */
    @Override
     public String toString() {
        return "" +
        		getTuotenro() + "|" +
                tuoteNimi + "|" +
                String.format("%4.2f", tuoteHinta) + "|" +
                tuoteMaxMaara + "|" +
	            osastoNro + "|";
    }
    
    /**
     *  Selvittää tuotteen tiedot tolppaerotellusta merkkijonosta
     *  Pitää huolen että seuraava seuraavaNro on suurempi kuin tuoteNro
     * @param rivi josta tuotteen tiedot otetaan
     * @example
     * <pre name="test">
     *   Tuote tuote = new Tuote();
     *   tuote.parse("10|Testituote192|0.50|46|3");
     *   tuote.getTuotenro() === 10;
     *   tuote.toString().startsWith("10|Testituote192|0.50|") === true;
     *   tuote.rekisteroi();
     *   int n = tuote.getTuotenro();
     *   tuote.parse(""+(n+20));
     *   tuote.rekisteroi();           // ja tarkistetaan että seuraavalla kertaa tulee yhtä isompi
     *   tuote.getTuotenro() === n+20+1;
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        setTuotenro(Mjonot.erota(sb, '|', getTuotenro()));
        tuoteNimi = Mjonot.erota(sb, '|', tuoteNimi);
        tuoteHinta = Mjonot.erota(sb, '|', tuoteHinta);
        tuoteMaxMaara = Mjonot.erota(sb, '|', tuoteMaxMaara);
        osastoNro = Mjonot.erota(sb, '|', osastoNro);
    }

    /**
    * Tehdään identtinen klooni tuotteesta
    * @return Object kloonattu tuote
    * @example
    * <pre name="test">
    * #THROWS CloneNotSupportedException 
    *   Tuote tuote = new Tuote();
    *   tuote.parse("10|Testituote192|0.50|46|3");
    *   Tuote kopio = tuote.clone();
    *   kopio.toString() === tuote.toString();
    *   tuote.parse("10|Testituote192|0.50|46|3");
    *   kopio.toString().equals(tuote.toString()) === true;
    * </pre>
    */
     @Override
   public Tuote clone() throws CloneNotSupportedException {
    	 Tuote uusi;
       uusi = (Tuote) super.clone();
       return uusi;
   }
    
    @Override
    public boolean equals(Object tuote) {
       if ( tuote == null ) return false;
        return this.toString().equals(tuote.toString());
    }
   
   
    @Override
    public int hashCode() {
         return tuoteNro;
    }
    
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Tuote tuote1 = new Tuote();
		Tuote tuote2 = new Tuote();
		Tuote tuote3 = new Tuote();
		Tuote tuote4 = new Tuote();
		
		tuote1.rekisteroi();
		tuote2.rekisteroi();
		tuote3.rekisteroi();
		tuote4.rekisteroi();
		
		tuote1.tulosta(System.out);
		tuote1.taytaTestiTiedoilla();
		tuote1.tulosta(System.out);
		
		tuote2.tulosta(System.out);
		tuote2.taytaTestiTiedoilla();
		tuote2.tulosta(System.out);
		
		tuote3.tulosta(System.out);
		tuote3.taytaTestiTiedoilla(2);
		tuote3.tulosta(System.out);
		
		tuote4.tulosta(System.out);
		tuote4.taytaTestiTiedoilla(3);
		tuote4.tulosta(System.out);

	}

}
