package evgen;

import java.util.Random;

public class World {
    // XXX: Consider adding the ability to set a seed for the RNG
    public static final Random rng = new Random();
    // XXX: Settings object probably shouldn't be static, because we'll need multiple,
    // XXX: one for each instance of the simulation
    public static final Settings settings = new Settings();

    public static void printCurrent(IWorldMap m, Animal a) {
        System.out.println(String.format("Animal at pos %s, facing %s", a.getPosition(), a.getFacing()));
        System.out.println(String.format("GENES: %s (next at index %d)", a.genes, a.genes.getNextGeneIndex()));
        System.out.println(m);
    }

    public static void main(String[] args) {
        System.out.println("evgen");

        final String configPath = "config/sample_config.yaml";
        boolean rc = settings.loadConfig(configPath);
        System.out.println(String.format("Load config file `%s': %s", configPath, rc ? "successful" : "failed!"));

        IWorldMap m = new GlobeMap();
        Animal a = new Animal(m, new Vector2d(3, 5));
        m.place(a);
        printCurrent(m, a);

        for (int i=0; i<5; ++i) {
            System.out.println(String.format("MOVE %d", i));
            a.move();
            printCurrent(m, a);
        }
    }
}
