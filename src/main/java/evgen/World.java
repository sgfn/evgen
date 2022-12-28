package evgen;

import java.util.Random;

public class World {
    public static Random rng;
    public static GenotypeMutationIndexGenerator indexGen;
    // XXX: Settings object probably shouldn't be static, because we'll need multiple,
    // XXX: one for each instance of the simulation
    public static final Settings settings = new Settings();

    public static void main(String[] args) {
        System.out.println("evgen");
        // final long seed = new Random().nextLong();
        final long seed = -6740656269889676863l;
        System.out.println(String.format("seed %d", seed));
        rng = new Random(seed);
        indexGen = new GenotypeMutationIndexGenerator(rng);

        final String configPath = "config/other_config.yaml";
        boolean rc = settings.loadConfig(configPath);
        System.out.println(String.format("Load config file `%s': %s", configPath, rc ? "successful" : "failed!"));

        Simulation simul = new Simulation(rng, settings, 10);
        simul.run();
    }
}
