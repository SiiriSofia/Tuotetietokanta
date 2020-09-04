package fxKauppa;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import kauppa.Kauppa;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * Ohjelmointi 2 -kurssin harjoitustyö: Tuotetietokanta, SQL-versio
 * Tietokantahaut toteutettu javakoodiin upotetuilla sql-pyynnöillä
 *
 * Kaupan tuotetietokanta-sovelluksen main-ohjelma
 * @author Siiri
 * @version 8 Feb 2020
 */
public class KauppaMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final FXMLLoader ldr = new FXMLLoader(getClass().getResource("KauppaGUIView.fxml"));
			final Pane root = (Pane)ldr.load();
			final KauppaGUIController kauppaCtrl = (KauppaGUIController)ldr.getController();
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("kauppa.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Kauppa");
			
			primaryStage.setOnCloseRequest((event) -> {
               if ( !kauppaCtrl.voikoSulkea() ) event.consume();
			});
			
			Kauppa kauppa = new Kauppa();
			kauppaCtrl.setKauppa(kauppa);
			
			primaryStage.show();
			
			Application.Parameters params = getParameters(); 
			if ( params.getRaw().size() > 0 ) 
				kauppaCtrl.lueTiedosto(params.getRaw().get(0));  
		    else
		        if ( !kauppaCtrl.avaa() ) Platform.exit();
		     
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Käynnistetään käyttöliittymä
	 * @param args komentorivin parametrit
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
