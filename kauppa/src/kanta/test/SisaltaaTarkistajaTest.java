package kanta.test;
// Generated by ComTest BEGIN
import static org.junit.Assert.*;
import org.junit.*;
import static kanta.SisaltaaTarkistaja.*;
import kanta.*;
// Generated by ComTest END

/**
 * Test class made by ComTest
 * @version 2020.03.31 16:02:40 // Generated by ComTest
 *
 */
@SuppressWarnings({ "all" })
public class SisaltaaTarkistajaTest {


  // Generated by ComTest BEGIN
  /** testOnkoVain36 */
  @Test
  public void testOnkoVain36() {    // SisaltaaTarkistaja: 36
    assertEquals("From: SisaltaaTarkistaja line: 37", false, onkoVain("123","12")); 
    assertEquals("From: SisaltaaTarkistaja line: 38", true, onkoVain("123","1234")); 
    assertEquals("From: SisaltaaTarkistaja line: 39", true, onkoVain("","1234")); 
  } // Generated by ComTest END


  // Generated by ComTest BEGIN
  /** testTarkista56 */
  @Test
  public void testTarkista56() {    // SisaltaaTarkistaja: 56
    SisaltaaTarkistaja tar = new SisaltaaTarkistaja("123"); 
    assertEquals("From: SisaltaaTarkistaja line: 58", null, tar.tarkista("12")); 
    assertEquals("From: SisaltaaTarkistaja line: 59", "Saa olla vain merkkejä: 123", tar.tarkista("14")); 
    assertEquals("From: SisaltaaTarkistaja line: 60", null, tar.tarkista("")); 
  } // Generated by ComTest END
}