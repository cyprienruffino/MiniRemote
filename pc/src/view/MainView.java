package view;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Valentin on 23/12/2015.
 */
public class MainView {
    private Stage primaryStage;
    private GridPane enAttente;
    private GridPane connecte;
    private EventHandler<WindowEvent> closeEvent;

    public MainView(EventHandler<WindowEvent> closeEvent) {
        this.closeEvent=closeEvent;

        enAttente=new GridPane();
        ProgressIndicator progressIndicator=new ProgressIndicator();
        Label label=new Label("En attente d'un client");
        enAttente.addRow(0,progressIndicator);
        enAttente.addRow(1,label);

        Image img=new Image("resource/check-mark.png",120,120,true,true);
        ImageView imageView=new ImageView(img);
        Label coLabel=new Label("Connecté");
        connecte=new GridPane();
        connecte.addRow(0,imageView);
        connecte.addRow(1,coLabel);

        primaryStage=new Stage();
        primaryStage.setTitle("Remote Control");
        primaryStage.getIcons().add(new Image("resource/icone.png"));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(closeEvent);
        setEnAttente();
        primaryStage.show();
    }


    public void setEnAttente(){
        primaryStage.setScene(new Scene(enAttente));
    }

    public void setConnecte(){
        primaryStage.setScene(new Scene(connecte));
    }
}
