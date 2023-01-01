package evgen;

import evgen.gui.App;
import javafx.application.Application;

import java.util.Random;

public class World {
    public static Random rng;

    public static void main(String[] args) {
        rng = new Random();
        GenotypeMutationIndexGenerator.init(rng);
        Application.launch(App.class, args);
    }
}
