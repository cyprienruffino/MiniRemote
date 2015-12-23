package main;

import java.awt.AWTException;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        controller=new Controller();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.disconnect();
    }

    public static void main(String[] args) throws AWTException {
      launch(args);
    }

}