package controller.view;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Created by Valentin on 03/11/2015.
 */
public class Controller {
    @FXML private Text text;
    @FXML public void test(ActionEvent e){
        text.setText("test2");
    }
}
