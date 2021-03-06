package kauppa.test;
// Generated by ComTest BEGIN
import static org.junit.Assert.*;
import org.junit.*;
import kauppa.*;
// Generated by ComTest END

/**
 * Test class made by ComTest
 * @version 2020.04.07 15:11:07 // Generated by ComTest
 *
 */
@SuppressWarnings({ "all" })
public class OsastoTest {


  // Generated by ComTest BEGIN
  /** testRekisteroi149 */
  @Test
  public void testRekisteroi149() {    // Osasto: 149
    Osasto o1 = new Osasto(); 
    assertEquals("From: Osasto line: 151", 0, o1.getOsastonro()); 
    o1.rekisteroi(); 
    Osasto o2 = new Osasto(); 
    o2.rekisteroi(); 
    int n1 = o1.getOsastonro(); 
    int n2 = o2.getOsastonro(); 
    n1 = n2-1; 
  } // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** testToString233 */
  @Test
  public void testToString233() {    // Osasto: 233
    Osasto osasto = new Osasto(); 
    osasto.parse("2|Osasto2|40|16"); 
    assertEquals("From: Osasto line: 236", true, osasto.toString().startsWith("2|Osasto2|40|16")); 
  } // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** testParse253 */
  @Test
  public void testParse253() {    // Osasto: 253
    Osasto osasto = new Osasto(); 
    osasto.parse("2|Osasto2|40|16"); 
    assertEquals("From: Osasto line: 256", 2, osasto.getOsastonro()); 
    assertEquals("From: Osasto line: 257", true, osasto.toString().startsWith("2|Osasto2|40|16")); 
    osasto.rekisteroi(); 
    int n = osasto.getOsastonro(); 
    osasto.parse(""+(n+4)); 
    osasto.rekisteroi();  // ja tarkistetaan että seuraavalla kertaa tulee yhtä isompi
    assertEquals("From: Osasto line: 262", n+4+1, osasto.getOsastonro()); 
  } // Generated by ComTest END
}