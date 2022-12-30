package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import evgen.lib.RandomSet;

public class GenotypeMutationIndexGenerator {
    private static GenotypeMutationIndexGenerator instance;

    private final Random rng;

    private Map<Integer, RandomSet<Integer>> mapOfSets = new HashMap<>();

    private GenotypeMutationIndexGenerator(Random r) {
        rng = r;
    }

    private synchronized void addHandlingInteger(int n) {
        if (!mapOfSets.containsKey(n)) {
            RandomSet<Integer> rs = new RandomSet<>();
            for (int i = 0; i < n; ++i) {
                rs.add(i);
            }
            mapOfSets.put(n, rs);
        }
    }

    /**
     * Initialise the GenotypeMutationIndexGenerator singleton.
     * @param r RNG to use
     */
    public static void init(Random r) {
        instance = new GenotypeMutationIndexGenerator(r);
    }

    /**
     * Get instance of the GenotypeMutationIndexGenerator singleton.
     * @return instance, may be null if init() was never called
     */
    public static GenotypeMutationIndexGenerator getInstance() {
        return instance;
    }

    /**
     * Get list of integers (mutation indices), a random subset of the set of all possible integers between 0 and {@code genotypeLength}-1.
     * Thread-safe.
     * @param genotypeLength length of genotype
     * @param amount amount of indices to get
     * @return list of indices
     */
    public synchronized List<Integer> pollRandomSeveral(int genotypeLength, int amount) {
        if (!mapOfSets.containsKey(genotypeLength)) {
            addHandlingInteger(genotypeLength);
        }

        List<Integer> l = new LinkedList<>();
        for (int i = 0; i < amount; ++i) {
            l.add(mapOfSets.get(genotypeLength).pollRandom(rng));
        }
        mapOfSets.get(genotypeLength).addAll(l);
        return l;
    }
}
