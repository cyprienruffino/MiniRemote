package main;

import java.awt.AWTException;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       new Controller();

    }


    public static void main(String[] args) throws AWTException {
      launch(args);
    }

}