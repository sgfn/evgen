package evgen.gui;

import evgen.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class SimulationWindow implements Runnable {
    private final GridPane mapGrid = new GridPane();
    private final GlobalStatsDisplay globalStatsDisplay;
    private final AnimalStatsDisplay animalStatsDisplay = new AnimalStatsDisplay();
    private final StatTracker statTracker;
    private final IWorldMap map;
    private Animal selectedAnimal;
    private ImageView selectedAnimalImage;
    private final Settings settings;
    private final Timeline timeline = new Timeline();
    private final int mapWidth = 800;
    private final int mapHeight = 800;
    private final HashMap<String, Image> mapElementImages = new HashMap<>();
    private HashSet<Integer> mostPopularGenotypeIDs;
    private boolean markGenotypes = false;
    private final int millisDelay;

    public SimulationWindow(int simulationID, Settings settings, Random rng, String logDirPath, int millisDelay) {
        if (!settings.success()) {
            System.out.println("Configuration loaded unsuccessfully, starting with default settings");
        }
        this.settings = settings;
        this.millisDelay = millisDelay;
        this.statTracker = logDirPath != null ? new StatTracker(logDirPath, simulationID) : new StatTracker();
        statTracker.importSettings(settings);
        map = (settings.getMapType() == Settings.MapType.GLOBE) ? new GlobeMap(rng, settings, statTracker) : new PortalMap(rng, settings, statTracker);

        for (int i = 0; i < settings.getStartingAnimals(); ++i) {
            map.place(new Animal(rng, settings, map, statTracker));
        }


        GridPane globalGrid = new GridPane();
        setupGrid(globalGrid, new Vector2d(7, 1));
        setupGrid(mapGrid, new Vector2d(settings.getMapWidth(), settings.getMapHeight()));
        mapGrid.setGridLinesVisible(true);
        mapGrid.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        globalStatsDisplay = new GlobalStatsDisplay(statTracker);

        //global simulation controls
        VBox simulationControlsWrapper = new VBox();
        simulationControlsWrapper.setSpacing(10);
        HBox simulationButtonsWrapper = new HBox();
        simulationButtonsWrapper.setSpacing(8);
        Text simulationButtonsHeader = new Text("Simulation controls");
        simulationButtonsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'comic sans ms'");
        Button startButton = new Button("start");
        startButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: green");
        startButton.setOnAction(e -> timeline.play());
        Button pauseButton = new Button("pause");
        pauseButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: red");
        pauseButton.setOnAction(e -> timeline.stop());
        simulationButtonsWrapper.getChildren().addAll(startButton, pauseButton);
        simulationControlsWrapper.getChildren().addAll(simulationButtonsHeader, simulationButtonsWrapper);

        //genome popularity display controls
        VBox genomePopularityWrapper = new VBox();
        genomePopularityWrapper.setSpacing(10);
        Text genomePopularityHeader = new Text("Animals with most popular genotype");
        genomePopularityHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold");
        HBox genomeButtonsWrapper = new HBox();
        genomeButtonsWrapper.setSpacing(8);
        Button showGenomes = new Button("show");
        showGenomes.setOnAction(e -> {
            markGenotypes = true;
            renderMap();
        });
        Button hideGenomes = new Button("hide");
        hideGenomes.setOnAction(e -> {
            markGenotypes = false;
            renderMap();
        });
        genomeButtonsWrapper.getChildren().addAll(showGenomes, hideGenomes);
        genomePopularityWrapper.getChildren().addAll(genomePopularityHeader, genomeButtonsWrapper);

        VBox statsWrapper = new VBox();
        statsWrapper.setPadding(new Insets(10));
        statsWrapper.setSpacing(20);
        statsWrapper.getChildren().addAll(simulationControlsWrapper, globalStatsDisplay, animalStatsDisplay, genomePopularityWrapper);

        globalGrid.add(statsWrapper, 0, 0, 3, 1);
        globalGrid.add(mapGrid, 3, 0, 4, 1);
        Scene scene = new Scene(globalGrid, mapWidth / 4 * 7, mapHeight);
        Stage stage = new Stage();
        stage.setTitle("evgen #" + simulationID);
        stage.setScene(scene);
        stage.show();
        renderMap();

    }

    private void stop() {
        timeline.stop();
    }

    public void renderMap() {
        mapGrid.getChildren().clear();
        mapGrid.setBackground(new Background(new BackgroundFill(Color.rgb(186, 182, 141), null, null)));
        for (int x = 0; x < settings.getMapWidth(); x++) {
            for (int y = 0; y < settings.getMapHeight(); y++) {
                boolean isPreferred = map.isPreferred(new Vector2d(x, y));
                StackPane preferredSpot = new StackPane();
                GridPane.setFillHeight(preferredSpot, true);
                GridPane.setFillWidth(preferredSpot, true);
                Object object = map.objectAt(new Vector2d(x, y));
                if (object != null) {
                    IMapElement element = (IMapElement) object;
                    ImageView imageView = new ImageView(getMapElementImage(element.getResource()));
                    if (object.getClass().equals(Animal.class)) {
                        setAnimalResource(imageView, (Animal) object);
                        if (selectedAnimal != null && selectedAnimal.equals(object)){
                            markAnimalAsTracked(imageView);
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
                                markAnimalAsTracked(imageView);
                                selectedAnimal = (Animal) object;
                                selectedAnimalImage = imageView;
                                animalStatsDisplay.setAnimal(selectedAnimal);
                            }
                        });
                        if (markGenotypes && mostPopularGenotypeIDs != null && mostPopularGenotypeIDs.contains(((Animal) object).id)) {
                            markAnimalAsMostPopular(imageView);
                        }
                    }
                    GridPane.setHalignment(imageView, HPos.CENTER);
                    GridPane.setValignment(imageView, VPos.CENTER);
                    imageView.setFitWidth(Math.min((double) mapWidth / settings.getMapWidth(), (double) mapHeight / settings.getMapHeight()));
                    imageView.setFitHeight(Math.min((double) mapWidth / settings.getMapWidth(), (double) mapHeight / settings.getMapHeight()));
                    preferredSpot.getChildren().add(imageView);
                }
                if (isPreferred) {
                    preferredSpot.setStyle("-fx-background-color: rgb(148, 144, 111)");
                }
                mapGrid.add(preferredSpot, x, y);
            }
        }
    }

    public void setAnimalResource(ImageView imageView, Animal animal) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(-0.8);
        colorAdjust.setSaturation(0.8);
        colorAdjust.setBrightness(0 - 0.1 * (10 - animal.getEnergyLvl()));
        colorAdjust.setContrast(1);
        imageView.setEffect(colorAdjust);
        imageView.setRotate(45 * animal.getFacing().facing);
    }

    public void markAnimalAsTracked(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0);
        colorAdjust.setSaturation(1);
        colorAdjust.setBrightness(0);
        colorAdjust.setContrast(0);
        imageView.setEffect(colorAdjust);
    }

    public void markAnimalAsMostPopular(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.3);
        colorAdjust.setSaturation(1);
        colorAdjust.setBrightness(0);
        colorAdjust.setContrast(0);
        imageView.setEffect(colorAdjust);
    }

    @Override
    public void run() {
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(millisDelay), ev -> {
            if (statTracker.getAnimalCount() == 0) {
                stop();
            }
            map.nextEpoch();
            renderMap();
            updateStats();
            mostPopularGenotypeIDs = statTracker.getMostPopularGenotypeIDs();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
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
