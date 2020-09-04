/**
 * 
 */
package fxKauppa;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller näkymälle, joka avautuu ensimmäisenä ohjelman auetessa.
 * Näkymässä syötetään tekstikenttään tiedoston nimi, joka halutaan avata
 * tuotetietokanta-sovelluksessa. 
 * @author Siiri
 * @version 8 Feb 2020
 */
public class KaupanNimiController implements ModalControllerInterface<String> {

	@FXML private TextField nimensyottoTextField;
	private String syotettyNimi;
	
	
	@FXML
    private void handleAvaa() {
		syotettyNimi = nimensyottoTextField.getText();
		ModalController.closeStage(nimensyottoTextField);
	}
	
	@FXML
    private void handlePeruuta() {
		ModalController.closeStage(nimensyottoTextField);
	}
	
	@Override
	public String getResult() { 
		return syotettyNimi;
	}
	
	@Override
	public void setDefault(String oletus) {
		nimensyottoTextField.setText(oletus);
	}
	
	/**
	 * Mitä tehdään kun dialogi on näytetty
	 */
	@Override
	public void handleShown() {
		nimensyottoTextField.requestFocus();
	}
	
	/**
     * Luodaan nimenkysymisdialogi ja palautetaan siihen kirjoitettu nimi tai null
	 * @param modalityStage mille ollaan modaalisia, null = sovellukselle
	 * @param oletus mitä nimeä näytetään oletuksena
     * @return null jos painetaan Cancel, muuten kirjoitettu nimi
     */
	 public static String kysyNimi(Stage modalityStage, String oletus) {
		 return ModalController.showModal(
			 KaupanNimiController.class.getResource("KaupanNimiView.fxml"),
             "Kauppa",
             modalityStage, oletus);
	  }
	
}
