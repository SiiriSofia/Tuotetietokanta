/**
 * 
 */
package kauppa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kanta.SailoException;

/**
 * Luokka kaupan käyttöliittymälle
 * Avustajat:
 * - Tuote-luokka
 * - Tuotteet-luokka
 * - Osasto-luokka
 * - Osastot-luokka
 * - Henkilo-luokka
 * - Henkilot-luokka
 * - Vastuu-luokka
 * - Vastuut-luokka
 * Vastuut:
 * -  huolehtii tuotteiden, tuoteosastojen ja henki-
 *    löiden välisestä yhteistyöstä
 * -  välittää kyseisten luokkien tietoja pyydettäessä
 * -  lukee ja kirjoittaa tiedot tiedostoon avustajia
 *    käyttäen 
 * @author Siiri
 * @version 2 Mar 2020
 */
public class Kauppa {
	
	private Tuotteet tuotteet;
	private Henkilot henkilot;
	private Osastot osastot;
	private Vastuut vastuut;
	/*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * #import kanta.SailoException;
     * 
     * private Kauppa kauppa;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    kauppa = new Kauppa();
     *    tiedNimi = "kookauppa";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    kauppa.lueTiedostosta(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */


	/**
	 * Poistaa tuotteistosta tuotteen tiedot  
     * @param tuote tuote joka poistetaan 
	 * @param nro viitenumero, jonka mukaan poistetaan
	 * @return montako tuotetta poistettiin
     * @throws SailoException
     */
    public void poista(int tuoteNro) throws SailoException { 
        tuotteet.poista(tuoteNro);
    } 
		 
	/**
	 * Poistaa vastuut-listasta pyydetyn henkilön vastuut,
     * eli vastuulinkitykset tuoteosastoihin poistuvat, mutta henkilön tiedot säilyy
     * edelleen tiedostossa
	 * @param henkiloNro viite siihen, keneen henkilöön liittyvät tietueet poistetaan
	 * @throws SailoException 
	 */
	public void poistaHenkilonVastuut(int henkiloNro) throws SailoException {
		 vastuut.poistaHenkilonVastuut(henkiloNro);
	 }
	
	/**
	 * Lisätään uusi tuote kauppaan
	 * @param tuote lisättävä tuote
	 * @throws SailoException 
	 * @example
     * <pre name="test">
     * #THROWS SailoException
     * Tuote tuote1 = new Tuote(); tuote1.taytaTestiTiedoilla(); 
     * Tuote tuote2 = new Tuote(); tuote2.taytaTestiTiedoilla(); 
     * kauppa.lisaa(tuote1);
     * Collection<Tuote> loytyneet = kauppa.etsi("", -1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === tuote1;
     * kauppa.lisaa(tuote2);
     * loytyneet = kauppa.etsi("", -1);
     * loytyneet.size() === 2;
     * Iterator<Tuote> it = loytyneet.iterator(); 
     * it.next() === tuote1;
     * it.next() === tuote2;
	 * </pre>
	 */
	public void lisaa(Tuote tuote) throws SailoException {
		tuotteet.lisaa(tuote);
	}

	/**
	 * Lisää uuden henkilö-olion henkilot-listalle
	 * @param h lisättävä henkilö
	 * @throws SailoException 
     */
	public void lisaa(Henkilo h) throws SailoException {
		henkilot.lisaa(h);
	}

	/**
	 * Lisää uuden osasto-olion osastot-listalle
	 * @param o lisättävä osasto
	 * @throws SailoException 
	 */
	public void lisaa(Osasto o) throws SailoException {
		osastot.lisaa(o);
	}
	
	/**
	 * Palauttaa kaupassa olevien osastojen määrän, 
	 * joka haetaan sql-lauseella osastot-taulusta
	 * @return osastojen lukumäärä
	 */
	public int getOsastojenLkm() {
		String lkm = "SELECT COUNT(osastoNro) " +
                "FROM Osastot ";
		return Integer.parseInt(lkm);
	}
	
	/**
	 * Luo uuden vastuu-olion
	 * @param osasto osasto, josta henkilo on vastuussa
	 * @param henkilo henkilo joka on vastuussa
	 * @return palauttaa uuden vastuuolion näillä määreillä
	 * @throws SailoException 
	 */
	public void luoVastuu(Osasto osasto, Henkilo henkilo) throws SailoException {
		Vastuu uusi = new Vastuu();
		uusi.setOsastonro(osasto.getOsastonro());
    	uusi.setHenkilonro(henkilo.getHenkilonro());
    	vastuut.lisaa(uusi);
	}
	 
	/**
	 * Hakee sen osaston, jolla kyseinen tuote sijaitsee
	 * @param tuote tuote jonka sijaintia haetaan
	 * @return palauttaa etsityn osaston
	 * @throws SailoException 
	 */
	public Osasto etsiTuotteenOsasto(Tuote tuote) throws SailoException {
		return osastot.hae(tuote.getOsastonro());
	}
	
	
	 /** Etsii tietystä tuoteosastosta vastaavat henkilöt
	 * @param osastoNro osaston numero, josta etsityt henkilöt vastaavat
	 * @return palauttaa listan vastuuhenkilöistä
	 * @throws SailoException 
	 */
	public List<Henkilo> etsiVastuuhenkilot(int osastoNro) throws SailoException {
		List<Henkilo> etsityt = new ArrayList<Henkilo>();
		List<Henkilo> loytyneetHenkilot = new ArrayList<Henkilo>();
     	List<Vastuu> loytyneetVastuut = vastuut.annaVastuutOsastolle(osastoNro);
         for (int i = 0; i<loytyneetVastuut.size(); i++) {
             int henkiloNro = loytyneetVastuut.get(i).getHenkilonro();
             Henkilo h = henkilot.hae(henkiloNro);
             if ( h == null ) continue;
             loytyneetHenkilot.add(h);
         }
         for (int i = 0; i<loytyneetHenkilot.size(); i++) {
        	 etsityt.add(loytyneetHenkilot.get(i));
         }
         return etsityt;
	 }
	 
	 /** Etsii tuoteosastot, joista tietty henkilö vastaa
	 * @param henkiloNro henkilön numero, joka on vastuussa
	 * @return palauttaa listan tuoteosastoista, joista henkilö vastaa
	 * @throws SailoException 
	 */
	public List<Osasto> etsiVastuuosastot(int henkiloNro) throws SailoException {
		 List<Osasto> etsityt = new ArrayList<Osasto>();
		 List<Osasto> loytyneetOsastot = new ArrayList<Osasto>();
		 List<Vastuu> loytyneetVastuut = vastuut.annaVastuutHenkilolle(henkiloNro);
         for (int i = 0; i<loytyneetVastuut.size(); i++) {
             int osastoNro = loytyneetVastuut.get(i).getOsastonro();
             Osasto o = osastot.hae(osastoNro);
             if ( o == null ) continue;
             loytyneetOsastot.add(o);
         }
         for (int i = 0; i<loytyneetOsastot.size(); i++) {
        	 etsityt.add(loytyneetOsastot.get(i));
         }
         return etsityt;
	 }
	
	/**
     * Tarkistaa löytyykö parametrina annettu henkilö nimen perusteella jo
     * listalla olevista henkilöistä
     * @param henkilo
     * @return totuusarvo true jos henkilö löytyy taulusta ennestään
     * @throws SailoException 
     */
	public boolean tarkistaLoytyyko(Henkilo henkilo) throws SailoException {
		return henkilot.tarkistaLoytyyko(henkilo.getHenkilonimi());
	}
	
	/**
     * Päivittää parametrina annetun henkilön tiedot henkilot-tauluun
     * @param osasto osasto jolle henkilön vastuu muodostetaan
     * @param henkilo päivitettävä henkilö
     * @throws SailoException
     */
    public void paivitaHenkilo(Osasto osasto, Henkilo henkilo) throws SailoException {
    	henkilot.paivitaHenkilo(henkilo);
    	luoVastuu(osasto, henkilo);
    }
    
    /**
	 * Hakee sen osaston, jolla kyseinen tuote sijaitsee
	 * @param tuote tuote jonka sijaintia haetaan
	 * @return palauttaa etsityn osaston
	 * @throws SailoException 
	 */
	public Henkilo etsi(String henkilonNimi) throws SailoException {
		return henkilot.hae(henkilonNimi);
	}
    
    /**
     * Päivittää tuotteen tiedot
     * @param tuote päivitettävä tuote
     * @throws SailoException
     */
    public void paivitaTuote(Tuote tuote) throws SailoException {
    	tuotteet.paivitaTuote(tuote);
    }
    
    /**
     * Päivittää parametrina annetun henkilön tiedot henkilot-tauluun
     * @param henkilo päivitettävä henkilö
     * @throws SailoException
     */
    public void paivitaHenkilo(Henkilo henkilo) throws SailoException {
    	henkilot.paivitaHenkilo(henkilo);
    }

	/**
	 * Antaa osastot-taulusta osaston, jonka osastoNro annetaan parametrina
	 * @param osastoNro jonka sisältävä osasto haetaan
	 * @return osasto, joka sisältää parametrina annetun osastoNron
	 * @throws SailoException
	 */
	public Osasto annaOsasto(int osastoNro) throws SailoException {
		return osastot.hae(osastoNro);
	}
	

	public Henkilo annaHenkilo(int henkiloNro) throws SailoException {
		return henkilot.hae(henkiloNro);
	}

	/**
     * Palauttaa tuotteet listassa
     * @param hakuehto hakuehto  
     * @param k etsittävän kentän indeksi 
     * @return tuotteet listassa
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Tuote t1 = new Tuote(); t1.taytaTestiTiedoilla(); 
     * Tuote t2 = new Tuote(); t2.taytaTestiTiedoilla(); 
     * kauppa.lisaa(t1);
     * kauppa.lisaa(t2);
     * kauppa.lisaa(t2);  #THROWS SailoException // samaa ei saa laittaa kahdesti
     * Collection<Tuote> loytyneet = kauppa.etsi(t1.getTuotenimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === t1;
     * loytyneet = kauppa.etsi(t2.getTuotenimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === t2;
     * kauppa.etsi("", 15); #THROWS SailoException
     * </pre>
     */
    public Collection<Tuote> etsi(String hakuehto, int k) throws SailoException {
        return tuotteet.etsi(hakuehto,k);
    }
    
    /**
     * Luo tietokannan. Jos annettu tiedosto on jo olemassa ja
     * sisältää tarvitut taulut, ei luoda mitään
     * @param nimi tietokannan nimi
     * @throws SailoException jos tietokannan luominen epäonnistuu
     */
    public void lueTiedostosta(String nimi) throws SailoException {
    	osastot = new Osastot(nimi);
        tuotteet = new Tuotteet(nimi);
        henkilot = new  Henkilot(nimi);
        vastuut = new  Vastuut(nimi);
        
    }
    
    
    /**
     * Tallentaa kaupan tiedot tiedostoon.  
     * Tässä tietokantaversiossa ei tarvitse tehdä mitään
     * @throws SailoException jos tallettamisessa ongelmia, nyt ei siis käytännössö heitä koskaan
     */
    public void tallenna() throws SailoException {
        return;
    }

    /**
     * Testiohjelma kerhosta
     * @param args ei käytössä
     */
    public static void main(String args[]) {

        try {
            new File("kokeilu.db").delete();
            Kauppa kauppa = new Kauppa();
            kauppa.lueTiedostosta("kokeilu");
            
            Osasto o1 = new Osasto();   o1.taytaTestiTiedoilla();
    		Osasto o2 = new Osasto();   o2.taytaTestiTiedoilla();
    		Osasto o3 = new Osasto();   o3.taytaTestiTiedoilla();
    		Osasto o4 = new Osasto();	o4.taytaTestiTiedoilla();
    		Osasto o5 = new Osasto(); 	o5.taytaTestiTiedoilla();
    		Osasto o6 = new Osasto();	o6.taytaTestiTiedoilla();
    		kauppa.lisaa(o1); kauppa.lisaa(o2); kauppa.lisaa(o3); 
    		kauppa.lisaa(o4); kauppa.lisaa(o5); kauppa.lisaa(o6);

            Tuote t1 = new Tuote(), t2 = new Tuote(), t3 = new Tuote();
            Tuote t4 = new Tuote(),  t5 = new Tuote();
            t1.taytaTestiTiedoilla();
            t2.taytaTestiTiedoilla();
            t3.taytaTestiTiedoilla();
            t4.taytaTestiTiedoilla();
            t5.taytaTestiTiedoilla();

            kauppa.lisaa(t1);
            kauppa.lisaa(t2);
            kauppa.lisaa(t3);
            kauppa.lisaa(t4);
            kauppa.lisaa(t5);
            
            Henkilo h1 = new Henkilo(); h1.taytaTestiTiedoilla();  kauppa.lisaa(h1);
            Henkilo h2 = new Henkilo(); h2.taytaTestiTiedoilla();  kauppa.lisaa(h2);
            Henkilo h3 = new Henkilo(); h3.taytaTestiTiedoilla();  kauppa.lisaa(h3);
            Henkilo h4 = new Henkilo(); h4.taytaTestiTiedoilla();  kauppa.lisaa(h4);
            
            kauppa.luoVastuu(o1, h1); kauppa.luoVastuu(o1, h2); kauppa.luoVastuu(o2, h3);
            kauppa.luoVastuu(o2, h4); kauppa.luoVastuu(o3, h1); kauppa.luoVastuu(o3, h2);
            kauppa.luoVastuu(o4, h2); kauppa.luoVastuu(o5, h3); kauppa.luoVastuu(o6, h4);

            System.out.println("============= Kaupan testi =================");
            
            Collection<Tuote> tuotteet = kauppa.etsi("", -1);
            int i = 0;
            for (Tuote tuote : tuotteet) {
                System.out.println("Tuote paikassa: " + i);
                tuote.tulosta(System.out);
                Osasto tuotteenOsasto = kauppa.etsiTuotteenOsasto(tuote);
                tuotteenOsasto.tulosta(System.out);
                List<Henkilo> vastuuhenkilot = kauppa.etsiVastuuhenkilot(tuote.getOsastonro());   
       	     	for (Henkilo h:vastuuhenkilot) {
       	     		h.tulosta(System.out);  
       	     	}
                i++;
            }

        } catch ( SailoException ex ) {
            System.out.println(ex.getMessage());
        }
        
        new File("kokeilu.db").delete();
    }
}
