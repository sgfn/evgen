package evgen.gui;

import evgen.StatTracker;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GlobalStatsDisplay extends VBox {

    StatTracker statTracker;
    Text epoch = new Text();
    Text animalCount = new Text();
    Text foliageCount = new Text();
    Text freeFieldsCount = new Text();
    Text mostPopularGenotype = new Text();
    Text avgEnergyLvl = new Text();
    Text avgLifeLength = new Text();

    public GlobalStatsDisplay(StatTracker statTracker) {
        this.statTracker = statTracker;
        Text header = new Text("Global simulation stats");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold");
        this.getChildren().addAll(header, epoch, animalCount, foliageCount, freeFieldsCount, mostPopularGenotype, avgEnergyLvl, avgLifeLength);
        update();
    }

    public void update() {
        epoch.setText("epoch: " + statTracker.getEpoch());
        animalCount.setText("animal count: " + statTracker.getAnimalCount());
        foliageCount.setText("foliage count: " + statTracker.getFoliageCount());
        freeFieldsCount.setText("free fields count: " + statTracker.getFreeFieldsCount());
        mostPopularGenotype.setText("most popular genotype: " + statTracker.getMostPopularGenotype().toString());
        avgEnergyLvl.setText("average energy: " + statTracker.getAvgEnergy());
        avgLifeLength.setText("average lifespan: " + statTracker.getAvgLifeLength());
    }


}
