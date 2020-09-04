package kauppa.test;
// Generated by ComTest BEGIN
import java.io.*;
import java.util.*;
import kanta.SailoException;
import static org.junit.Assert.*;
import org.junit.*;
import kauppa.*;
// Generated by ComTest END

/**
 * Test class made by ComTest
 * @version 2020.04.08 18:48:41 // Generated by ComTest
 *
 */
@SuppressWarnings({ "all" })
public class KauppaTest {

  // Generated by ComTest BEGIN  // Kauppa: 42

  private Kauppa kauppa; 
  private String tiedNimi; 
  private File ftied; 

  @Before
  public void alusta() throws SailoException {
     kauppa = new Kauppa(); 
     tiedNimi = "kookauppa"; 
     ftied = new File(tiedNimi+".db"); 
     ftied.delete(); 
     kauppa.lueTiedostosta(tiedNimi); 
  }

  @After
  public void siivoa() {
     ftied.delete(); 
  }
  // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** 
   * testLisaa95 
   * @throws SailoException when error
   */
  @Test
  public void testLisaa95() throws SailoException {    // Kauppa: 95
    Tuote tuote1 = new Tuote(); tuote1.taytaTestiTiedoilla(); 
    Tuote tuote2 = new Tuote(); tuote2.taytaTestiTiedoilla(); 
    kauppa.lisaa(tuote1); 
    Collection<Tuote> loytyneet = kauppa.etsi("", -1); 
    assertEquals("From: Kauppa line: 101", 1, loytyneet.size()); 
    assertEquals("From: Kauppa line: 102", tuote1, loytyneet.iterator().next()); 
    kauppa.lisaa(tuote2); 
    loytyneet = kauppa.etsi("", -1); 
    assertEquals("From: Kauppa line: 105", 2, loytyneet.size()); 
    Iterator<Tuote> it = loytyneet.iterator(); 
    assertEquals("From: Kauppa line: 107", tuote1, it.next()); 
    assertEquals("From: Kauppa line: 108", tuote2, it.next()); 
  } // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** 
   * testEtsi283 
   * @throws SailoException when error
   */
  @Test
  public void testEtsi283() throws SailoException {    // Kauppa: 283
    Tuote t1 = new Tuote(); t1.taytaTestiTiedoilla(); 
    Tuote t2 = new Tuote(); t2.taytaTestiTiedoilla(); 
    kauppa.lisaa(t1); 
    kauppa.lisaa(t2); 
    try {
    kauppa.lisaa(t2); 
    fail("Kauppa: 289 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); } // samaa ei saa laittaa kahdesti
    Collection<Tuote> loytyneet = kauppa.etsi(t1.getTuotenimi(), 1); 
    assertEquals("From: Kauppa line: 291", 1, loytyneet.size()); 
    assertEquals("From: Kauppa line: 292", t1, loytyneet.iterator().next()); 
    loytyneet = kauppa.etsi(t2.getTuotenimi(), 1); 
    assertEquals("From: Kauppa line: 294", 1, loytyneet.size()); 
    assertEquals("From: Kauppa line: 295", t2, loytyneet.iterator().next()); 
    try {
    kauppa.etsi("", 15); 
    fail("Kauppa: 296 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); }
  } // Generated by ComTest END
}