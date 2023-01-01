package evgen.gui;

import evgen.Animal;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class AnimalStatsDisplay extends VBox {
    Animal animal = null;
    Text id = new Text("animal id: ");
    Text genome = new Text("genome: ");
    Text energy = new Text("energy: ");
    Text eatenFoliage = new Text("foliage eaten: ");
    Text children = new Text("children count: ");
    Text age = new Text("age: ");

    public AnimalStatsDisplay() {
        Text header = new Text("Tracked animal stats");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold");
        this.getChildren().addAll(header, id, genome, energy, eatenFoliage, children, age);
        update();
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
        update();
    }

    public void update() {
        if (animal != null) {
            id.setText("tracked animal id: " + animal.id);
            genome.setText("genome: " + animal.genes.toString());
            energy.setText("energy: " + animal.getEnergy());
            eatenFoliage.setText("foliage eaten: " + animal.getEatenFoliage());
            children.setText("children count: " + animal.getChildren());
            age.setText(animal.isAlive() ? "age: " + animal.getAge() : "died at: " + animal.getDeathEpoch());
        } else {
            id.setText("tracked animal id: ");
            genome.setText("genome: ");
            energy.setText("energy: ");
            eatenFoliage.setText("foliage eaten: ");
            children.setText("children count: ");
            age.setText("age: ");
        }
    }
}
