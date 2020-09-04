/**
 * 
 */
package kanta;

/**
 * Poikkeusluokka tietorakenteessa tapahtuville poikkeuksille.
 * @author Siiri
 * @version 18 Feb 2020
 */
public class SailoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Poikkeuksen muodostaja jolle tuodaan poikkeuksessa näytettävä viesti
	 * @param viesti Poikkeuksen viesti
	 */
	public SailoException(String viesti) {
		super(viesti);
	}

}
