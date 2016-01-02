package view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Valentin on 23/12/2015.
 */
public class MainView {
    private Stage primaryStage;
    private VBox enAttente;
    private VBox connecte;
    private EventHandler<WindowEvent> closeEvent;

    public MainView(EventHandler<WindowEvent> closeEvent) {
        this.closeEvent = closeEvent;

        enAttente = new VBox();
        enAttente.setPrefSize(400, 400);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label("En attente d'un client");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        enAttente.getChildren().addAll(progressIndicator, label);
        VBox.setVgrow(progressIndicator, Priority.ALWAYS);
        label.setAlignment(Pos.CENTER);

        connecte = new VBox();
        connecte.setPrefSize(400, 450);
        Image img = new Image("resource/tick.png");
        ImageView imageView = new ImageView(img);
        imageView.setPreserveRatio(true);
        Label coLabel = new Label("Connecté");
        coLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        connecte.getChildren().addAll(imageView, coLabel);
        VBox.setVgrow(imageView, Priority.ALWAYS);
        coLabel.setAlignment(Pos.CENTER);

        primaryStage = new Stage();
        primaryStage.setTitle("Remote Control");
        primaryStage.getIcons().add(new Image("resource/icone.png"));
        primaryStage.setOnCloseRequest(closeEvent);

        //Taille de la police
        DoubleProperty fontSize = new SimpleDoubleProperty(10);
        fontSize.bind(primaryStage.widthProperty().add(primaryStage.heightProperty()).divide(20));
        label.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
        coLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));

        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        setEnAttente();
    }

    public EventHandler getEnAttenteEvent() {
        return event -> setEnAttente();
    }

    public EventHandler getConnecteEvent() {
        return event -> setConnecte();
    }

    private void setEnAttente() {
        Platform.runLater(() ->
        {
            try {
                primaryStage.setScene(new Scene(enAttente));
                primaryStage.show();
            } catch (IllegalArgumentException e) {
                //Si la scene est déjà affiché
            }
        });

    }

    private void setConnecte() {
        Platform.runLater(() ->
        {
            try {
                primaryStage.setScene(new Scene(connecte));
                primaryStage.show();
            } catch (IllegalArgumentException e) {
                //Si la scene est déjà affiché
            }
        });
    }
}
