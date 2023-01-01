package evgen.gui;

import evgen.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

//public class SimulationWindow {
public class SimulationWindow implements Runnable {

//    private final Stage stage = new Stage();
//    private final GridPane globalGrid = new GridPane();
    private final GridPane mapGrid = new GridPane();
    private final VBox statsWrapper = new VBox();
    private final VBox globalStats = new VBox();
    private final GlobalStatsDisplay globalStatsDisplay;
    private final AnimalStatsDisplay animalStatsDisplay = new AnimalStatsDisplay();
    private final StatTracker statTracker;
    private final IWorldMap map;
    private Animal selectedAnimal;
    private ImageView selectedAnimalImage;
    private final Settings settings;
    private final Timeline timeline;
    private final int elementSize = 20;
    HashMap<String, Image> mapElementImages = new HashMap<>();
    public SimulationWindow(int simulationID, Settings settings, Random rng, String logDirPath, int millisDelay) {
        this.settings = settings;
        this.statTracker = logDirPath != null ? new StatTracker(logDirPath, simulationID) : new StatTracker();
        statTracker.importSettings(settings);
        map = (settings.getMapType() == Settings.MapType.GLOBE) ? new GlobeMap(rng, settings, statTracker) : new PortalMap(rng, settings, statTracker);

        for (int i = 0; i < settings.getStartingAnimals(); ++i) {
            map.place(new Animal(rng, settings, map, statTracker));
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(millisDelay), ev -> {
            map.nextEpoch();
//            System.out.println(map.toString());
            renderMap();
            updateStats();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);


        GridPane globalGrid = new GridPane();
        setupGrid(globalGrid, new Vector2d(7, 1));
        setupGrid(mapGrid, new Vector2d(settings.getMapWidth(), settings.getMapHeight()));
        mapGrid.setGridLinesVisible(true);
        mapGrid.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        globalStatsDisplay = new GlobalStatsDisplay(statTracker);

        Button startButton = new Button("start");
        startButton.setOnAction(e -> timeline.play());
        Button pauseButton = new Button("pause");
        pauseButton.setOnAction(e -> timeline.stop());
        HBox buttonWrapper = new HBox();
        buttonWrapper.setSpacing(10);
        buttonWrapper.getChildren().addAll(startButton, pauseButton);
        statsWrapper.setPadding(new Insets(10));
        statsWrapper.getChildren().addAll(globalStatsDisplay, buttonWrapper, animalStatsDisplay);

        globalGrid.add(statsWrapper, 0, 0, 3, 1);
        globalGrid.add(mapGrid, 3, 0, 4, 1);
        Scene scene = new Scene(globalGrid, elementSize * settings.getMapWidth() * 7 / 4, elementSize * settings.getMapHeight());
        Stage stage = new Stage();
        stage.setTitle("evgen #" + simulationID);
        stage.setScene(scene);
        stage.show();
        renderMap();

    }


    public void renderMap() {
        System.out.println("epoch: " + statTracker.getEpoch());
        mapGrid.getChildren().clear();
        mapGrid.setBackground(new Background(new BackgroundFill(Color.rgb(134, 129, 95), null, null)));
        for (int x = 0; x < settings.getMapWidth(); x++) {
            for (int y = 0; y < settings.getMapHeight(); y++) {
                Object object = map.objectAt(new Vector2d(x, y));
                if (object != null) {
//                    System.out.println("object: " + x + " " + y);
//                    ImageView imageView = new ImageView(image);
//                    IMapElement element = (IMapElement) object;
//                    System.out.println("element");
//                    Image img;
//                    System.out.println("img");
//                    try {
//                        img = new Image(new FileInputStream(element.getResource()));
//                        System.out.println("new image");
//                    } catch (FileNotFoundException e) {
//                        System.out.println("not found");
//                        return;
//                    }
//                    ImageView imageView = new ImageView(img);
                    IMapElement element = (IMapElement) object;
                    ImageView imageView = new ImageView(getMapElementImage(element.getResource()));
                    System.out.println("imgView");
                    if (object.getClass().equals(Animal.class)) {
                        setAnimalResource(imageView, (Animal) object);
                        if (selectedAnimal != null && selectedAnimal.equals(object)){
                            markAnimalResource(imageView);
                            selectedAnimalImage = imageView;
                        }
                        imageView.setOnMouseClicked(e -> {
                            boolean untracked = false;
                            if (selectedAnimal != null) {
                                setAnimalResource(selectedAnimalImage, selectedAnimal);
                                if (selectedAnimal.equals(object)) {
                                    selectedAnimal = null;
                                    selectedAnimalImage = null;
                                    animalStatsDisplay.setAnimal(null);
                                    untracked = true;
                                }
                            }
                            if (!untracked) {
                                markAnimalResource(imageView);
                                selectedAnimal = (Animal) object;
                                selectedAnimalImage = imageView;
                                animalStatsDisplay.setAnimal(selectedAnimal);
                            }
                        });
                    }
                    GridPane.setHalignment(imageView, HPos.CENTER);
                    GridPane.setValignment(imageView, VPos.CENTER);
                    imageView.setFitWidth(elementSize);
                    imageView.setFitHeight(elementSize);
                    mapGrid.add(imageView, x, y);
                }
            }
        }
    }

    public void setAnimalResource(ImageView imageView, Animal animal) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.3 - 0.05 * (10 - animal.getEnergyLvl()));
        colorAdjust.setSaturation(1);
        colorAdjust.setBrightness(0.2);
        colorAdjust.setContrast(0);
        imageView.setEffect(colorAdjust);
        imageView.setRotate(45 * animal.getFacing().facing);
    }

    public void markAnimalResource(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-0.6);
        imageView.setEffect(colorAdjust);
    }

    @Override
    public void run() {
    }

    private void setupGrid(GridPane grid, Vector2d dimensions) {
        for (int i = 0; i < dimensions.x; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / dimensions.x);
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setFillWidth(true);
            grid.getColumnConstraints().add(columnConstraints);
        }
        for (int i = 0; i < dimensions.y; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / dimensions.y);
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setFillHeight(true);
            grid.getRowConstraints().add(rowConstraints);
        }
    }
    private void updateStats() {
        globalStatsDisplay.update();
        animalStatsDisplay.update();
    }

    private Image getMapElementImage(String path) {
        Image img = mapElementImages.get(path);
        if (img == null) {
            try {
                img = new Image(new FileInputStream(path));
            } catch (FileNotFoundException e) {
                System.out.println("invalid element resource");
            }
            mapElementImages.put(path, img);
        }
        return img;
    }




}
