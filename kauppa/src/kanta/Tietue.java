/**
 * 
 */
package kanta;

/**
 * Rajapinta tietueelle johon voidaan taulukon avulla rakentaa 
 * "attribuutit". Muokattu Vesan mallista.
 * @author Siiri
 * @version 31 Mar 2020
 */
public interface Tietue {

      /**
       * @return tietueen kenttien lukumäärä
       * @example
       * <pre name="test">
       *   #import kauppa.Henkilo;
       *   Henkilo henkilo = new Henkilo();
       *   henkilo.getKenttia() === 3;
       * </pre>
       */
      public abstract int getKenttia();
  
  
      /**
       * @return ensimmäinen käyttäjän syötettävän kentän indeksi
       * @example
       * <pre name="test">
       *   Henkilo henkilo = new Henkilo();
       *   henkilo.ekaKentta() === 1;
       * </pre>
       */
      public abstract int ekaKentta();
  
  
      /**
       * @param k minkä kentän kysymys halutaan
       * @return valitun kentän kysymysteksti
       * @example
       * <pre name="test">
       *   Henkilo henkilo = new Henkilo();
       *   henkilo.getKysymys(2) === "Puhelinnumero";
       * </pre>
       */
      public abstract String getKysymys(int k);
  
  
      /**
       * @param k Minkä kentän sisältö halutaan
       * @return valitun kentän sisältö
       * @example
       * <pre name="test">
       *   Henkilo henkilo = new Henkilo();
       *   henkilo.parse("1|Katto Kassinen|040 123345");
       *   henkilo.anna(0) === "1";   
       *   henkilo.anna(1) === "Katto Kassinen";   
       *   henkilo.anna(2) === "040 123345";  
       * </pre>
       */
      public abstract String anna(int k);
  
      
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
       * </pre>
       */
      public abstract String aseta(int k, String s);
  
  
      /**
       * Tehdään identtinen klooni tietueesta
       * @return kloonattu tietue
       * @throws CloneNotSupportedException jos kloonausta ei tueta
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
     public abstract Tietue clone() throws CloneNotSupportedException;
 
 
     /**
      * Palauttaa tietueen tiedot merkkijonona jonka voi tallentaa tiedostoon.
      * @return tietue tolppaeroteltuna merkkijonona 
      * @example
      * <pre name="test">
      *   Henkilo henkilo = new Henkilo();
      *   henkilo.parse("5|Testi Testinen|014-222333");
      *   henkilo.toString().startsWith("5|Testi Testinen|014-222333") === true;
      * </pre>
      */
     @Override
     public abstract String toString(); 

}
