package fxKauppa;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ModalController;
import javafx.application.Platform;
import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import fi.jyu.mit.fxgui.StringGrid;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import kanta.SailoException;
import kauppa.Kauppa;
import kauppa.Osasto;
import kauppa.Tuote;
import kauppa.Henkilo;
import static fxKauppa.TietueDialogController.getFieldId;

/**
 * Controller sovelluksen päänäkymälle.
 * @author Siiri
 * @version 8 Feb 2020
 * @version 1 Apr 2020 - Haku- ja vertailuominaisuuksien lisäys
 */
public class KauppaGUIController implements Initializable {
	
	@FXML private ListChooser<Tuote> tuotteetListChooser;
	@FXML private ComboBoxChooser<String> hakuehtoCBChooser;
	@FXML private TextField hakuehtoTextField;
	@FXML private Label osastoNimiLabel;
	@FXML private Label osastoKokoLabel;
	@FXML private Label osastoHyllymaaraLabel;
	@FXML private StringGrid<Henkilo> vastuuhlotStringGrid;
	@FXML private ScrollPane panelTuote;
	@FXML private GridPane gridTuote;
	
	
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta();     
    }
	
    @FXML private void handleHakuehto() {
    	if ( tuoteKohdalla != null )
    		 hae(tuoteKohdalla.getTuotenro()); 
    }

	@FXML
    private void handleMenuAvaa() {
		avaa();
	}

	@FXML
    private void handleMenuTulosta() {
		TulostusController tulostusCtrl = TulostusController.tulosta(null);
		tulostaValitut(tulostusCtrl.getTulostusTextArea());
	}
	
	@FXML
    private void handleMenuLopeta() {
		/*Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Valitse");
        alert.setHeaderText(null);
        alert.setContentText("Tallennetaanko?");

        ButtonType buttonTypeYes = new ButtonType("Kyllä", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Ei", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == buttonTypeYes )*/
		tallenna();    // jos vastaa kyllä, niin tallennetaan
		Platform.exit();
	}

	@FXML
    private void handlePoista() {
		poistaTuote();
	}

	@FXML
    private void handleMuokkaa() {
		muokkaa(1);
    }
	
	@FXML
    private void handleMenuApua() {
		avustus();
	}

	@FXML
    private void handleMenuLisatietoja() {
		ModalController.showModal(TiedotController.class.getResource("TiedotView.fxml"), "Lisätietoja", null, "");
	}
	
	@FXML
    private void handleUusiTuote() {
		uusiTuote();
	}
	
	@FXML
    private void handleTallenna() {
		tallenna();
	}
	
	@FXML
    private void handleLisaaHenkilo() {
		uusiHenkilo();
	}
	
// =====================================================
// Muita ei-käyttöliittymään liittyviä metodeja

	private String kaupannimi = "kookauppa";
	private Kauppa kauppa;
	private Tuote tuoteKohdalla;
	private TextField[] edits;
	private Label[] osastotiedot;
	private int kentta = 0; 
	private static Henkilo apuhenkilo = new Henkilo();
	private static Tuote aputuote = new Tuote();
	
	
	 /**
     * Tekee tarvittavat muut alustukset.
     * Alustetaan myös tuotelistan kuuntelija 
     */
    protected void alusta() {
    	tuotteetListChooser.clear();
    	tuotteetListChooser.addSelectionListener(e -> naytaTuote());
        hakuehtoCBChooser.clear();  
        for (int k = aputuote.ekaKentta(); k < aputuote.getKenttia(); k++)  {
        	hakuehtoCBChooser.add(aputuote.getKysymys(k), null);
        }
        hakuehtoCBChooser.getSelectionModel().select(0);
         
        edits = TietueDialogController.luoKentat(gridTuote, aputuote);
        osastotiedot = new Label[] {osastoNimiLabel, osastoKokoLabel, osastoHyllymaaraLabel};
        for (TextField edit: edits)  {
        	if ( edit != null ) {  
                edit.setEditable(false);  
                edit.setOnMouseClicked(e -> { if ( e.getClickCount() > 1 ) muokkaa(getFieldId(e.getSource(),0)); });  
                edit.focusedProperty().addListener((a,o,n) -> kentta = getFieldId(edit,kentta));
            }    

	        int eka = apuhenkilo.ekaKentta(); 
	        int lkm = apuhenkilo.getKenttia(); 
	        String[] headings = new String[lkm-eka]; 
	        for (int i=0, k=eka; k<lkm; i++, k++) headings[i] = apuhenkilo.getKysymys(k); 
	        vastuuhlotStringGrid.initTable(headings); 
	        vastuuhlotStringGrid.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 
	        vastuuhlotStringGrid.setEditable(false); 
	        vastuuhlotStringGrid.setPlaceholder(new Label("Ei vielä henkilöitä")); 
	        vastuuhlotStringGrid.setColumnSortOrderNumber(1); 
	        vastuuhlotStringGrid.setColumnSortOrderNumber(2); 
	        vastuuhlotStringGrid.setColumnWidth(1, 110); 
	        //Henkilön tuplaklikkauksella ja F2:lla aukeaa kysymysikkuna, joka kysyy mitä henkilön tiedoille tehdään
	        vastuuhlotStringGrid.setOnMouseClicked( e -> { if ( e.getClickCount() > 1 ) handleMitaHenkilolleTehdaan(); } );
	        vastuuhlotStringGrid.setOnKeyPressed( e -> {if ( e.getCode() == KeyCode.F2 ) handleMitaHenkilolleTehdaan();}); 
        }
    }
 
    /**
     * Avaa alertti-ikkunan, jossa kysytään mitä henkilön tiedoille tehdään.
     * Muokkaa-nappi avaa muokkausnäkymän, poista-nappi poistaa henkilön kyseisen 
     * tuoteosaston yhteydestä.
     */
    private void handleMitaHenkilolleTehdaan() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Valitse");
        alert.setHeaderText(null);
        alert.setContentText("Mitä henkilön tiedoille tehdään?");

        ButtonType buttonTypeMuokkaa = new ButtonType("Muokkaa", ButtonData.APPLY);
        ButtonType buttonTypePoista = new ButtonType("Poista", ButtonData.APPLY);
        ButtonType buttonTypeCancel = new ButtonType("Peruuta", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeMuokkaa, buttonTypePoista, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == buttonTypeMuokkaa ) muokkaaHenkiloa();
        if ( result.get() == buttonTypePoista ) poistaHenkilo();
	}
	
    /**
     * Luo uuden tuotteen jota aletaan editoimaan 
     */
    protected void uusiTuote() {
            Tuote uusi = new Tuote();
            uusi = TietueDialogController.kysyTietue(null, uusi, 1);    
            if ( uusi == null ) return;
            //uusi.rekisteroi();
            try {
				kauppa.lisaa(uusi);
			} catch (SailoException e) {
				Dialogs.showMessageDialog("Ongelmia uuden luomisessa " + e.getMessage());
	            return;
			}
            hae(uusi.getTuotenro());
    }
	/**
	 * Hakee tuotteen tiedot listaan
	 * @param tnr tuotteen numero joka aktivoidaan haun jälkeen
	 */
	private void hae(int tnr) {
		int tnro = tnr;
        if ( tnro <= 0 ) {  
            Tuote kohdalla = tuoteKohdalla;  
            if ( kohdalla != null ) tnro = kohdalla.getTuotenro();  
        } 
		int k = hakuehtoCBChooser.getSelectionModel().getSelectedIndex() + aputuote.ekaKentta();
	    String ehto = hakuehtoTextField.getText();
	    if (ehto.indexOf('*') < 0) ehto = "*" + ehto + "*";

	    tuotteetListChooser.clear();
		
	    int index = 0;
	    Collection<Tuote> tuotteet;
	        try {
				tuotteet = kauppa.etsi(ehto, k);
		        int i = 0;
		        for (Tuote tuote:tuotteet) {
		           if (tuote.getTuotenro() == tnro) index = i;
		           tuotteetListChooser.add(tuote.getTuotenimi(), tuote);
		           i++;
		        }
	        } catch (SailoException e) {
				Dialogs.showMessageDialog("Tuotteen hakemisessa ongelmia! " + e.getMessage());
			}
	    tuotteetListChooser.setSelectedIndex(index); // tästä tulee muutosviesti joka näyttää tuotteen
    }

	/**
	 * Näyttää listasta valitun tuotteen tiedot
	 */
	protected void naytaTuote() {
		tuoteKohdalla = tuotteetListChooser.getSelectedObject();
		
		if(tuoteKohdalla == null) return;
		
		TietueDialogController.naytaTietue(edits, tuoteKohdalla);
		try {
			naytaOsasto(osastotiedot, kauppa.etsiTuotteenOsasto(tuoteKohdalla));
			naytaHenkilot(kauppa.etsiTuotteenOsasto(tuoteKohdalla));
		} catch (SailoException e) {
			Dialogs.showMessageDialog("Tuotteen näyttämisessä ongelmia! " + e.getMessage());
		}
	}
	
	/**
	 * Näyttää kohdalla olevasta tuoteosastosta vastuussa olevat henkilöt taulukossa
	 * @param osasto jonka vastuuhenkilöt halutaan näyttää
	 */
	private void naytaHenkilot(Osasto osasto) {
		vastuuhlotStringGrid.clear();
	    if ( osasto == null ) return;
        List<Henkilo> henkilot;
		try {
			henkilot = kauppa.etsiVastuuhenkilot(osasto.getOsastonro());
	        if ( henkilot.size() == 0 ) return;
	        for (Henkilo henkilo: henkilot) naytaHenkilo(henkilo);
		} catch (SailoException e) {
			Dialogs.showMessageDialog("Henkilön näyttämisessä ongelmia! " + e.getMessage());
		}
	}
	
	/**
	 * Näyttää yhden henkilön tiedot taulukon rivillä
	 * @param henkilo jonka tiedot halutaan näyttää
	 */
	private void naytaHenkilo(Henkilo henkilo) {
        int kenttia = henkilo.getKenttia(); 
        String[] rivi = new String[kenttia-henkilo.ekaKentta()]; 
        for (int i=0, k=henkilo.ekaKentta(); k < kenttia; i++, k++) 
            rivi[i] = henkilo.anna(k); 
        vastuuhlotStringGrid.add(henkilo,rivi);
    }
	
	/**
	 * Tekee uuden tyhjän henkilön editointia varten
	 * Jos tietoihin syötetään henkilön nimeksi sama merkkijono kuin jollain toisella aiemmin 
	 * henkilötiedostoon lisätyllä henkilöllä, niin lisätään ennestään löytyvä henkilö tämän
	 * osaston kohdalle. Muut tiedot haetaan aiemmasta. Niitä voi muokata erikseen muokkaa-napista. 
	 * Henkilö lisätään kohdalla olevan tuoteosaston vastuuhenkilöksi.
     */
	private void uusiHenkilo() {
        if ( tuoteKohdalla == null ) return;
        try {
            Henkilo uusi = new Henkilo();
            Osasto osastoKohdalla = kauppa.etsiTuotteenOsasto(tuoteKohdalla);
            uusi = TietueDialogController.kysyTietue(null, uusi, 1);
            if ( uusi == null ) return;
            if (kauppa.tarkistaLoytyyko(uusi)) { //Jos samanniminen hlö löytyy ennestään taulusta
            	Henkilo samaHenkilo = kauppa.etsi(uusi.getHenkilonimi()); //niin haetaan sen
            	kauppa.luoVastuu(osastoKohdalla, samaHenkilo); 			//tiedot ja tehdään sille vastuu
            }
            else {																			
	            kauppa.lisaa(uusi);					//muussa tapauksessa jos ei löydy ennestään niin
	            kauppa.luoVastuu(osastoKohdalla, uusi);//luodaan uusi hlö ja sille vastuu
            }
            vastuuhlotStringGrid.selectRow(1000);  // järjestetään viimeinen rivi valituksi
        } catch (SailoException e) {
            Dialogs.showMessageDialog("Lisääminen epäonnistui: " + e.getMessage());
        }
    }
	
	 /**
	  * Aukaisee tuotteen tiedot tietuedialogi-näkymään.
	  * Jos olemassa olevan tuotteen tietoja muokataan, tiedot päivitetään
	  * alkuperäisen päälle.
	 * @param k indeksi joka kertoo monettako tietorakenteen tuotetta halutaan muokata
	 */
	private void muokkaa(int k) { 
         if ( tuoteKohdalla == null ) return; 
         try { 
             Tuote tuote; 
             tuote = TietueDialogController.kysyTietue(null, tuoteKohdalla.clone(), k);   
             if ( tuote == null ) return; 
             kauppa.paivitaTuote(tuote); 
             hae(tuote.getTuotenro()); 
         } catch (CloneNotSupportedException e) { 
             // 
         } catch (SailoException e) { 
             Dialogs.showMessageDialog(e.getMessage()); 
         } 
     }    
	 
	 /**
	 * Aukaisee henkilön tiedot tietuedialogi-näkymään.
	 * Jos tietoja muokataan, tiedot päivittyvät kaikkiin henkilön esiintymiin
	 */
	private void muokkaaHenkiloa() {
		 int r = vastuuhlotStringGrid.getRowNr();
         if ( r < 0 ) return; // klikattu ehkä otsikkoriviä
         Henkilo henkilo = vastuuhlotStringGrid.getObject();
         if ( henkilo == null ) return;
         int k = vastuuhlotStringGrid.getColumnNr()+henkilo.ekaKentta();
         try {
        	 henkilo = TietueDialogController.kysyTietue(null, henkilo.clone(), k);
             if ( henkilo == null ) return;
             kauppa.paivitaHenkilo(henkilo); 
             naytaHenkilot(kauppa.etsiTuotteenOsasto(tuoteKohdalla)); 
             vastuuhlotStringGrid.selectRow(r);  // järjestetään sama rivi takaisin valituksi
         } catch (CloneNotSupportedException  e) { /* clone on tehty */  
         } catch (SailoException e) {
             Dialogs.showMessageDialog("Ongelmia lisäämisessä: " + e.getMessage());
         }
     }
	
	/** 
     * Poistetaan henkilotaulukosta valitulla kohdalla oleva henkilö 
     */ 
    private void poistaHenkilo() { 
        int rivi = vastuuhlotStringGrid.getRowNr(); 
        if ( rivi < 0 ) return; 
        Henkilo henkilo = vastuuhlotStringGrid.getObject(); 
        if ( henkilo == null ) return; 
        try {
			kauppa.poistaHenkilonVastuut(henkilo.getHenkilonro());
	        naytaHenkilot(kauppa.etsiTuotteenOsasto(tuoteKohdalla)); 
	        int harrastuksia = vastuuhlotStringGrid.getItems().size();  
	        if ( rivi >= harrastuksia ) rivi = harrastuksia -1; 
	        vastuuhlotStringGrid.getFocusModel().focus(rivi); 
	        vastuuhlotStringGrid.getSelectionModel().select(rivi);
        } catch (SailoException e) {
        	 Dialogs.showMessageDialog("Poistaminen epäonnistui: " + e.getMessage());
		} 
    } 
 
 
    /**
     * Poistetaan listalta valittu tuote, dialogi vielä varmistaa että halutaan varmasti poistaa
     */ 
    private void poistaTuote() { 
        Tuote tuote = tuoteKohdalla; 
        if ( tuote == null ) return; 
        if ( !Dialogs.showQuestionDialog("Poisto", "Poistetaanko tuote: " + tuote.getTuotenimi(), "Kyllä", "Ei") ) 
            return; 
        try {
			kauppa.poista(tuote.getTuotenro());
		} catch (SailoException e) {
			Dialogs.showMessageDialog("Poistaminen epäonnistui: " + e.getMessage());
		}  
        int index = tuotteetListChooser.getSelectedIndex(); 
        hae(0); 
        tuotteetListChooser.setSelectedIndex(index); 
    } 
  

	
	/**
	 * Näytetään tuotteen tiedot käyttöliittymän komponentteihin
	 * Jos tuoteosaston numero on tuntematon eli osastoista ei löydy sellaista, 
	 * niin ilmoitetaan osastojen tietonäkymässä asiasta
	 * @param osastotiedot labeltaulukko, jonka kenttiin asetetaan osaston tiedot
	 * @param osasto näytettävä osasto
	 */
	public void naytaOsasto(Label[] osastotiedot, Osasto osasto) {
		if (osasto.getOsastonro() == 0) {
			osastotiedot[0].setText("Tuoteosastoa ei ole olemassa.");
			osastotiedot[1].setText("Kaupassa on yhteensä " + kauppa.getOsastojenLkm() + " osastoa.");
			osastotiedot[2].setText("");
			
		} else {
			osastotiedot[0].setText("Osasto " + osasto.getOsastonro() + ":  " + osasto.getOsastonimi());
			osastotiedot[1].setText("Tuoteosaston koko: " + osasto.getOsastonkoko() + " m2");
			osastotiedot[2].setText("Hyllyjen määrä: " + osasto.getOsastohyllymaara());
		}
	}
	
	
	/**
	 * Tulostaa tuotteen tiedot
	 * @param os tietovirta johon tulostetaan
	 * @param tuote tulostettava tuote
	 */
	 public void tulosta(PrintStream os, final Tuote tuote) {
	     os.println("----------------------------------------------");
	     os.println("Tuote:");
	     tuote.tulosta(os);
	     os.println("");
	     os.println("Sijainti:");
	     try {
			kauppa.etsiTuotteenOsasto(tuote).tulosta(os);
		     os.println("");
		     os.println("Tilausvastaavat:");
		     List<Henkilo> vastuuhenkilot = kauppa.etsiVastuuhenkilot(tuote.getOsastonro());   
		     for (Henkilo h:vastuuhenkilot)
		         h.tulosta(os);  
	     } catch (SailoException e) {
	    	 Dialogs.showMessageDialog("Tietojen hakemisessa ongelmia! " + e.getMessage());
		}
	 }
	 
 	/**
 	* Tulostaa listassa olevat tuotteet tekstialueeseen
    * @param text alue johon tulostetaan
    */
	 public void tulostaValitut(TextArea text) {
		  try (PrintStream os = TextAreaOutputStream.getTextPrintStream(text)) {
			  os.println("Tulostetaan tuotteiden tiedot:");
			  for (Tuote tuote: tuotteetListChooser.getObjects()) { 
	        	  tulosta(os, tuote);
	              os.println("\n");
	          }
		  }
	 }
    
	/**
     * Alustaa kerhon lukemalla sen valitun nimisestä tiedostosta
     * @param nimi tiedosto josta kerhon tiedot luetaan
     * @return null jos onnistuu, muuten virhe tekstinä
     */
    protected String lueTiedosto(String nimi) {
       kaupannimi = nimi;
       setTitle("Kauppa - " + kaupannimi);
       try {
          kauppa.lueTiedostosta(nimi);
          hae(0);
          return null;
        } catch (SailoException e) {
           hae(0);
           String virhe = e.getMessage(); 
           if ( virhe != null ) Dialogs.showMessageDialog(virhe);
           return virhe;
        }
    }
    
	/**
	 * Kysytään tiedoston nimi ja luetaan se
	 * @return true jos onnistui, false jos ei
	 */
    public boolean avaa() {
    	String uusinimi = KaupanNimiController.kysyNimi(null, kaupannimi);
	    if (uusinimi == null) return false;
	    lueTiedosto(uusinimi);
	    return true;
	 }
	
   /**
    * Tarkistetaan onko tallennus tehty
  	* @return true jos saa sulkea sovelluksen, false jos ei
    */
    public boolean voikoSulkea() {
       tallenna();
       return true;
    }
    
    private void setTitle(String title) {
    	ModalController.getStage(hakuehtoTextField).setTitle(title);
    }
   
	
   /**
    * Tietojen tallennus
    * @return null jos onnistuu, muuten virhe tekstinä
    */
    private String tallenna() {
       try {
           kauppa.tallenna();
           return null;
       } catch (SailoException ex) {
           Dialogs.showMessageDialog("Tallennuksessa ongelmia! " + ex.getMessage());
          return ex.getMessage();
       }
   }
	
	/**
	 * Näytetään ohjelman suunnitelma erillisessä selaimessa.
	 */
	private void avustus() {
        Desktop desktop = Desktop.getDesktop();
        try {
        	URI uri = new URI("https://tim.jyu.fi/view/kurssit/tie/ohj2/2020k/ht/ssmurtol");
        	desktop.browse(uri);
	     } catch (URISyntaxException e) {
	    	 return;
	     } catch (IOException e) {
	        return;
	     }
	}
	
	/**
	 * @param kauppa Kauppa jota tässä käyttöliittymässä käytetään
	 */
	public void setKauppa(Kauppa kauppa) {
		this.kauppa = kauppa;
	}
	
}
