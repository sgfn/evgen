package evgen.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final String presetConfigsDirPath = "config";

    @Override
    public void start(Stage primaryStage) {
        SetupScreen setupScreen = new SetupScreen(primaryStage, presetConfigsDirPath);
        setupScreen.showSetupScreen();
    }


}
