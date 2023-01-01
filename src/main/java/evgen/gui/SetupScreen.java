package evgen.gui;

import evgen.Settings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class SetupScreen {

    private final Stage stage;
    private final String presetDirPath;

    private File presetConfig = null;
    private File customConfig = null;
    private boolean customConfigSelected;
    private File csvDirectory = null;
    public SetupScreen(Stage stage, String presetDirPath) {
        this.stage = stage;
        this.presetDirPath = presetDirPath;
    }

    private int simulationCount = 0;

    public void showSetupScreen() {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(10));
        wrapper.setSpacing(10);

        Button startSimulation = new Button("start simulation");
        startSimulation.setDisable(true);



        HBox presetConfigWrapper = new HBox();
        presetConfigWrapper.setVisible(false);
        presetConfigWrapper.setManaged(false);
        HBox customConfigWrapper = new HBox();
        customConfigWrapper.setVisible(false);
        customConfigWrapper.setManaged(false);

        //config type section
        HBox configTypeWrapper = new HBox();
        configTypeWrapper.setSpacing(8);
        Label configTypeLabel = new Label("select configuration type");
        ChoiceBox<String> configTypeSelector = new ChoiceBox<>();
        configTypeWrapper.getChildren().addAll(configTypeLabel, configTypeSelector);
        configTypeSelector.getItems().addAll("preset configuration", "custom configuration");
        configTypeSelector.setOnAction( ev -> {
            if (configTypeSelector.getValue().equals("preset configuration")) {
                customConfigSelected = false;
                if (presetConfig == null) {
                    startSimulation.setDisable(true);
                }
                presetConfigWrapper.setVisible(true);
                presetConfigWrapper.setManaged(true);
                customConfigWrapper.setVisible(false);
                customConfigWrapper.setManaged(false);
            } else {
                customConfigSelected = true;
                if (customConfig == null) {
                    startSimulation.setDisable(true);
                }
                presetConfigWrapper.setVisible(false);
                presetConfigWrapper.setManaged(false);
                customConfigWrapper.setVisible(true);
                customConfigWrapper.setManaged(true);
            }
        });



        //preset config selection section
        presetConfigWrapper.setSpacing(8);
        Label presetSelectorLabel = new Label("select preset");
        String[] presets = new File(this.presetDirPath).list( (File dir, String name) -> {
            if (name.contains(".")) {
                return name.substring(name.lastIndexOf(".") + 1).equals("yaml");
            }
            return false;
        });
        ChoiceBox<String> presetSelector = new ChoiceBox<>();
        if (presets != null) {
            for (String preset : presets) {
                presetSelector.getItems().add(preset);
            }
        }
        presetSelector.setOnAction(ev -> {
            presetConfig = new File(presetDirPath + "/" + presetSelector.getValue());
            startSimulation.setDisable(false);
        });
        presetConfigWrapper.getChildren().addAll(presetSelectorLabel, presetSelector);



        //custom config selection section
        customConfigWrapper.setSpacing(8);
        Text selectedConfigPath = new Text();
        FileChooser selectConfig = new FileChooser();
        selectConfig.getExtensionFilters().add(new FileChooser.ExtensionFilter("configuration files", "*.yaml"));
        selectConfig.setTitle("select configuration file");
        Button loadConfig = new Button("select configuration file");
        loadConfig.setOnAction(e -> {
            System.out.println("clicked");
            customConfig = selectConfig.showOpenDialog(this.stage);
            if (customConfig != null) {
                selectedConfigPath.setText(customConfig.getName());
                startSimulation.setDisable(false);
            } else {
                selectedConfigPath.setText("invalid file name!");
                Alert wrongPathAlert = new Alert(Alert.AlertType.ERROR);
                wrongPathAlert.setTitle("path problem");
                wrongPathAlert.setContentText("the file you have chosen does not exist!");
                wrongPathAlert.show();
            }
        });
        customConfigWrapper.getChildren().addAll(loadConfig, selectedConfigPath);



        //saving logs section
        HBox csvWrapper = new HBox();
        csvWrapper.setSpacing(8);
        Label saveCsvLabel = new Label("Save statistics to .csv");
        CheckBox saveToCsv = new CheckBox();
        Text csvPath = new Text();
        Button changeCsvDirectory = new Button("change directory");
        DirectoryChooser selectCsv = new DirectoryChooser();
        selectCsv.setTitle("select csv target directory");
        csvWrapper.getChildren().addAll(saveCsvLabel, saveToCsv, csvPath);
        saveToCsv.setOnAction(ev -> {
            if (saveToCsv.isSelected()) {
                csvDirectory = selectCsv.showDialog(this.stage);
                if (csvDirectory != null) {
                    csvPath.setText(csvDirectory.getName());
                    startSimulation.setDisable(false);
                } else {
                    csvPath.setText("invalid directory name!");
                    Alert wrongPathAlert = new Alert(Alert.AlertType.ERROR);
                    wrongPathAlert.setTitle("path problem");
                    wrongPathAlert.setContentText("the file you have chosen does not exist!");
                    wrongPathAlert.show();
                    startSimulation.setDisable(true);
                }
                csvWrapper.getChildren().add(changeCsvDirectory);
            } else {
                startSimulation.setDisable(false);
                csvDirectory = null;
                csvPath.setText("");
                csvWrapper.getChildren().remove(changeCsvDirectory);
            }

        });
        changeCsvDirectory.setOnAction( ev -> {
            csvDirectory = selectCsv.showDialog(this.stage);
            if (csvDirectory != null) {
                csvPath.setText(csvDirectory.getName());
            } else {
                Alert wrongPathAlert = new Alert(Alert.AlertType.ERROR);
                wrongPathAlert.setTitle("path problem");
                wrongPathAlert.setContentText("the file you have chosen does not exist!");
                wrongPathAlert.show();
            }
        });

        //set delay
        HBox delayWrapper = new HBox();
        Label setDelay = new Label("set simulation delay: ");
        TextField delayValue = new TextField("1000");
        delayValue.setOnAction (e -> {
            if (Integer.valueOf(delayValue.getText()) < 50) {
                startSimulation.setDisable(true);
            } else {
                startSimulation.setDisable(false);
            }
        });
        delayWrapper.getChildren().addAll(setDelay, delayValue);


        startSimulation.setOnAction(ev -> {
            if (Integer.valueOf(delayValue.getText()) >= 50) {
                Thread simulationWindow = new Thread(new SimulationWindow(simulationCount++, new Settings(customConfigSelected ? customConfig.getAbsolutePath() : presetConfig.getAbsolutePath()), new Random(), csvDirectory != null ? csvDirectory.getAbsolutePath() : null, Integer.valueOf(delayValue.getText())));
                simulationWindow.start();
            }
        });


        wrapper.getChildren().addAll(configTypeWrapper, presetConfigWrapper, customConfigWrapper, csvWrapper, delayWrapper, startSimulation);

        Scene scene = new Scene(wrapper, 600, 200);
        try {
            this.stage.getIcons().add(new Image(new FileInputStream("src/main/resources/icon.png")));
        } catch (FileNotFoundException e) {
            System.out.println("icon file not found");
        }
        this.stage.setTitle("Evolutionary Generator Setup");
        this.stage.setScene(scene);
        this.stage.show();
    }

}
