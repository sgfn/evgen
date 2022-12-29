package evgen;

import java.util.Random;

public class World {
    public static Random rng;
    // XXX: Settings object probably shouldn't be static, because we'll need multiple,
    // XXX: one for each instance of the simulation
    public static final Settings settings = new Settings();

    public static void main(String[] args) {
        System.out.println("evgen");

        final long seed = new Random().nextLong();
        System.out.println(String.format("seed: %d", seed));
        rng = new Random(seed);
        GenotypeMutationIndexGenerator.init(rng);

        final String configPath = "config/default_config.yaml";
        boolean rc = settings.loadConfig(configPath);
        System.out.println(String.format("Load config file `%s': %s", configPath, rc ? "successful" : "failed!"));

        Simulation simul = new Simulation(rng, settings, 10);
        simul.run();
    }
}
