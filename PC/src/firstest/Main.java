package firstest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.AbstractCollection;

/**
 * Created by Valentin on 20/10/2015.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("HelloWorld");
        primaryStage.setScene(new Scene(root,300,275));
        primaryStage.show();
    }
    public static void main(String... args){
        launch(args);
    }
}
