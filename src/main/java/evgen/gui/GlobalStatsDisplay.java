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
        this.getChildren().addAll(epoch, animalCount, foliageCount, freeFieldsCount, mostPopularGenotype, avgEnergyLvl, avgLifeLength);
        update();
    }

    public void update() {
        epoch.setText("Current epoch: " + statTracker.getEpoch());
        animalCount.setText("Current animal count: " + statTracker.getAnimalCount());
        foliageCount.setText("Current foliage count: " + statTracker.getFoliageCount());
        freeFieldsCount.setText("Current free fields count: " + statTracker.getFreeFieldsCount());
        mostPopularGenotype.setText("Current most popular genotype: " + statTracker.getMostPopularGenotype().toString());
        avgEnergyLvl.setText("Current average animal energy level: " + statTracker.getAvgEnergy());
        avgLifeLength.setText("Current average animal life length: " + statTracker.getAvgLifeLength());
    }


}
