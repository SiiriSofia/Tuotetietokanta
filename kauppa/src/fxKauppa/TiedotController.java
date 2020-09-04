/**
 * 
 */
package fxKauppa;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller näkymälle, josta ilmenee sovelluksen tiedot
 * @author Siiri
 * @version 8 Feb 2020
 */
public class TiedotController implements ModalControllerInterface<String> {
	
	@FXML private Button okButton;
	
	@FXML
    private void handleOK() {
		ModalController.closeStage(okButton);
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleShown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefault(String arg0) {
		// TODO Auto-generated method stub
		
	}
}
