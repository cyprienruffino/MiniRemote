package view;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Controller;

/**
 * Created by Valentin on 23/12/2015.
 */
public class MainView {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Stage primaryStage;
    private VBox enAttente;
    private VBox connecte;
    private Label coLabel;
    private MenuBar menuBar;
    private Menu menuFile;
    private ImageView imageView;

    public MainView(EventHandler<WindowEvent> closeEvent) {

        menuBar = new MenuBar();
        menuFile = new Menu("Options");
        menuBar.getMenus().add(menuFile);
        MenuItem add = new MenuItem("Changer Port");
        add.setOnAction(t -> {
                    TextInputDialog dialog = new TextInputDialog(""+Controller.getInstance().getPort());
                    dialog.setTitle("Changement de port");
                    dialog.setContentText("Port : ");
                    Optional<String> result = dialog.showAndWait();
                    if(result.isPresent()) {
                        try {
                            int port = Integer.parseInt(result.get());
                            Controller.getInstance().setPort(port);
                            Controller.getInstance().restartAfterSend();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
        }
        );
        MenuItem disonnect = new MenuItem("Déconnecter");
        disonnect.setOnAction(t -> {
            Controller.getInstance().restartAfterSend();
        });
        MenuItem about = new MenuItem("A Propos");
        about.setOnAction(t -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("A propos");
            alert.setHeaderText(null);
            alert.setContentText("Application réalisée par :" + LINE_SEPARATOR + "Corentin Colomier" + LINE_SEPARATOR + "Valentin Jubert" + LINE_SEPARATOR + "Cyprien Ruffino" + LINE_SEPARATOR + "Nicolas Serrette");
            alert.showAndWait();
        });

        menuFile.getItems().addAll(add, disonnect, about);

        enAttente = new VBox();
        enAttente.setPrefSize(400, 400);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label("En attente d'un client");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        enAttente.getChildren().addAll(menuBar, progressIndicator, label);
        VBox.setVgrow(progressIndicator, Priority.ALWAYS);
        label.setAlignment(Pos.CENTER);

        connecte = new VBox();
        connecte.setPrefSize(400, 400);
        Image img = new Image("resource/tick.png");
        imageView = new ImageView(img);
        imageView.setPreserveRatio(true);
        coLabel = new Label("Connecté");
        coLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(imageView, Priority.ALWAYS);
        coLabel.setAlignment(Pos.CENTER);

        primaryStage = new Stage();
        primaryStage.setTitle("Remote Control");
        primaryStage.getIcons().add(new Image("resource/icone.png"));
        primaryStage.setOnCloseRequest(closeEvent);

        //Taille de la police
        DoubleProperty fontSize = new SimpleDoubleProperty(10);
        fontSize.bind(primaryStage.widthProperty().add(primaryStage.heightProperty()).divide(40));
        label.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
        coLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));

        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        setEnAttente();
    }

    public void setEnAttente() {
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

    public void setConnecte(String hostName) {
        Platform.runLater(() ->
        {
            coLabel.setText("Connecté à " + hostName);
            try {
                connecte.getChildren().addAll(menuBar, imageView, coLabel);
                primaryStage.setScene(new Scene(connecte));
                primaryStage.show();
            } catch (IllegalArgumentException e) {
                //Si la scene est déjà affiché
            }
        });
    }
}
