package evgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import evgen.lib.RandomSet;

public class ToxicCorpsesGrower extends AbstractFoliageGrower {
    private Map<Vector2d, Integer> spotDeathCounters = new HashMap<>();
    private SortedMap<Integer, RandomSet<Vector2d>> notPreferredByDeaths = new TreeMap<>();
    private int maxPreferredSpotDeaths = 0;

    protected void setupPreferredSpots() {
        preferredSpots.addAll(availableRegularSpots);
    }

    public ToxicCorpsesGrower(Random r, Settings s, IWorldMap m) {
        super(r, s, m);
    }

    // public ToxicCorpsesGrower(IWorldMap m) {
    //     this(World.rng, World.settings, m);
    // }

    @Override
    public void animalDiedAt(Vector2d pos) {
        int currAtPos = spotDeathCounters.getOrDefault(pos, 0) + 1;
        spotDeathCounters.put(pos, currAtPos);
        if (preferredSpots.contains(pos) && currAtPos > maxPreferredSpotDeaths) {
            // Remove spot from preferred, add to notPreferred
            if (availablePreferredSpots.remove(pos)) {
                availableRegularSpots.add(pos);
            }
            preferredSpots.remove(pos);
            if (!notPreferredByDeaths.containsKey(currAtPos)) {
                notPreferredByDeaths.put(currAtPos, new RandomSet<>());
            }
            notPreferredByDeaths.get(currAtPos).add(pos);

            // If not enough good spots, find another one in notPreferred, remove from that and add to preferred
            if (preferredSpots.size() < preferredSpotAmount) {
                RandomSet<Vector2d> s = notPreferredByDeaths.get(notPreferredByDeaths.firstKey());
                Vector2d newPreferred = s.pollRandom(rng);
                assert newPreferred != null : "New preferred spot is null";
                if (s.isEmpty()) {
                    // Death counters cannot go down, so we'll never need these sets again
                    notPreferredByDeaths.remove(notPreferredByDeaths.firstKey());
                }
                if (availableRegularSpots.remove(newPreferred)) {
                    availablePreferredSpots.add(newPreferred);
                }
                preferredSpots.add(newPreferred);
                maxPreferredSpotDeaths = Math.max(spotDeathCounters.getOrDefault(newPreferred, 0), maxPreferredSpotDeaths);
            }
        }

        // Update notPreferredByDeaths
        if (!preferredSpots.contains(pos)) {
            RandomSet<Vector2d> s = notPreferredByDeaths.get(currAtPos-1);
            if (s != null) {
                s.remove(pos);
                if (s.isEmpty()) {
                    // Death counters cannot go down, so we'll never need these sets again
                    notPreferredByDeaths.remove(currAtPos-1);
                }
            }

            if (!notPreferredByDeaths.containsKey(currAtPos)) {
                notPreferredByDeaths.put(currAtPos, new RandomSet<>());
            }
            notPreferredByDeaths.get(currAtPos).add(pos);
        }
    }
}
