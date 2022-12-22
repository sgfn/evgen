package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import evgen.lib.RandomSet;

public class GenotypeMutationIndexGenerator {
    private final Random rng;

    private Map<Integer, RandomSet<Integer>> mapOfSets = new HashMap<>();

    public GenotypeMutationIndexGenerator(Random r) {
        rng = r;
    }

    public void addHandlingInteger(int n) {
        if (!mapOfSets.containsKey(n)) {
            RandomSet<Integer> rs = new RandomSet<>();
            for (int i = 0; i < n; ++i) {
                rs.add(i);
            }
            mapOfSets.put(n, rs);
        }
    }

    public List<Integer> pollRandomSeveral(int fromWhich, int amount) {
        if (!mapOfSets.containsKey(fromWhich)) {
            addHandlingInteger(fromWhich);
        }

        List<Integer> l = new LinkedList<>();
        for (int i = 0; i < amount; ++i) {
            l.add(mapOfSets.get(fromWhich).pollRandom(rng));
        }
        mapOfSets.get(fromWhich).addAll(l);
        return l;
    }
}
