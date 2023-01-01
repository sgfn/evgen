package evgen.gui;

import evgen.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Polygon;

public class AnimalGuiElement implements IPositionChangeObserver {

    GridPane grid;
    Animal animal;
    Polygon triangle;

//    public AnimalGuiElement(GridPane grid) {
    public AnimalGuiElement(GridPane grid, Animal animal) {
        this.grid = grid;
        this.animal = animal;
        this.animal.addObserver(this);
//        this.animal.addGuiElement(this);
        this.triangle = new Polygon(15.0, 0.0, 0.0, 40.0, 30.0, 40.0);
        GridPane.setHalignment(triangle, HPos.CENTER);
        GridPane.setValignment(triangle, VPos.CENTER);
        Vector2d animalPosition = animal.getPosition();
        grid.add(this.triangle, animalPosition.x, animalPosition.y);
    }

    public void updateEnergy() {

    }

    public void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition) {
        grid.getChildren().remove(triangle);
        grid.add(triangle, newPosition.x, newPosition.y);
        updateEnergy();
        setRotation();
    }

    public void animalDied() {
        grid.getChildren().remove(triangle);
    }

    public void setRotation() {
        this.triangle.setRotate(animal.getFacing().facing * 45);
    }


}
