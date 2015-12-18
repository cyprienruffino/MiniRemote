package controller.view;


import java.io.IOException;

import controller.communication.wifi.TCPServer;
import controller.communication.wifi.UDPServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Created by Valentin on 03/11/2015.
 */
public class Controller {
    @FXML public void connect(ActionEvent e) throws IOException {
        new UDPServer().attendreRequete();
        new TCPServer().startServer();

    }
}
