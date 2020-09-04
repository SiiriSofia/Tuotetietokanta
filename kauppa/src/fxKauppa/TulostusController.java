/**
 * 
 */
package fxKauppa;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.print.PrinterJob; 
import javafx.scene.web.WebEngine; 

/**
 * Controller tulostusnäkymälle
 * @author Siiri
 * @version 8 Feb 2020
 * @version 1 Apr 2020 - Viimeistellään tulostusnäkymä näkymään ja toimimaan oikein
 */
public class TulostusController implements ModalControllerInterface<String> {

	@FXML private TextArea tulostusTextArea;
	
	@FXML
    private void handleTulosta() {
        PrinterJob job = PrinterJob.createPrinterJob(); 
        if ( job != null && job.showPrintDialog(null) ) { 
            WebEngine webEngine = new WebEngine(); 
            webEngine.loadContent("<pre>" + tulostusTextArea.getText() + "</pre>"); 
            webEngine.print(job); 
            job.endJob(); 
        } 
	}
	
	@FXML
    private void handlePeruuta() {
		ModalController.closeStage(tulostusTextArea);
	}
	
	@Override
	public String getResult() {
		return null;
	}   
	
	 @Override 
	 public void setDefault(String oletus) {
	     if ( oletus == null ) return;
	     tulostusTextArea.setText(oletus);
	 }
	 
	/**
	 * Mitä tehdään kun dialogi on näytetty
     */
	 @Override
	 public void handleShown() {
		 //
	 }
	 
	/**
     * Näyttää tulostusalueessa tekstin
	 * @param tulostus tulostettava teskti
	 * @return 
     */
	 public static TulostusController tulosta(String tulostus) {
		 return ModalController.showModeless(TulostusController.class.getResource("TulostusView.fxml"),
		 "Tulostus", tulostus);
     }
	 
	 /**
	  * Getteri tulostusalueelle
	 * @return palauttaa viitteen tulostusalueeseen
	 */
	public TextArea getTulostusTextArea() {
		 return tulostusTextArea;
	 }
}
