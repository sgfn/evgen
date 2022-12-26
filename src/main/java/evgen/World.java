package evgen;

import java.util.Random;

public class World {
    public static final Random rng = new Random();
    public static final GenotypeMutationIndexGenerator indexGen = new GenotypeMutationIndexGenerator(rng);
    // XXX: Settings object probably shouldn't be static, because we'll need multiple,
    // XXX: one for each instance of the simulation
    public static final Settings settings = new Settings();

    public static void main(String[] args) {
        System.out.println("evgen");

        final String configPath = "config/sample_config.yaml";
        boolean rc = settings.loadConfig(configPath);
        System.out.println(String.format("Load config file `%s': %s", configPath, rc ? "successful" : "failed!"));

        Simulation simul = new Simulation(rng, settings, 100);
        simul.run();
    }
}
