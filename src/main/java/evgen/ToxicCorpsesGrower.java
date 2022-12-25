package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class ToxicCorpsesGrower extends AbstractFoliageGrower {
    private Map<Vector2d, Integer> spotDeathCounters = new HashMap<>();
    private SortedMap<Integer, LinkedList<Vector2d>> notPreferredByDeaths = new TreeMap<>();
    private int maxPreferredSpotDeaths = 0;

    protected void setupPreferredSpots() {
        preferredSpots.addAll(availableRegularSpots);
    }

    public ToxicCorpsesGrower(Random r, Settings s, IWorldMap m) {
        super(r, s, m);
    }

    public ToxicCorpsesGrower(IWorldMap m) {
        this(World.rng, World.settings, m);
    }

    @Override
    public void animalDiedAt(Vector2d pos) {
        // XXX: consider refactoring to be able to inform about several deaths at same spot at once
        int currAtPos = spotDeathCounters.getOrDefault(pos, 0) + 1;
        spotDeathCounters.put(pos, currAtPos);
        if (preferredSpots.contains(pos) && currAtPos > maxPreferredSpotDeaths) {
            // Remove spot from preferred, add to notPreferred
            if (availablePreferredSpots.remove(pos)) {
                availableRegularSpots.add(pos);
            }
            preferredSpots.remove(pos);
            if (!notPreferredByDeaths.containsKey(currAtPos)) {
                notPreferredByDeaths.put(currAtPos, new LinkedList<>());
            }
            notPreferredByDeaths.get(currAtPos).add(pos);

            // If not enough good spots, find another one in notPreferred, remove from that and add to preferred
            if (preferredSpots.size() < preferredSpotAmount) {
                LinkedList<Vector2d> l = notPreferredByDeaths.get(notPreferredByDeaths.firstKey());
                Vector2d newPreferred = l.removeFirst();
                if (l.isEmpty()) {
                    // Death counters cannot go down, so we'll never need these lists again
                    notPreferredByDeaths.remove(notPreferredByDeaths.firstKey());
                }
                if (availableRegularSpots.remove(newPreferred)) {
                    availablePreferredSpots.add(newPreferred);
                }
                preferredSpots.add(newPreferred);
                maxPreferredSpotDeaths = Math.max(spotDeathCounters.get(newPreferred), maxPreferredSpotDeaths);
            }
        }
    }
}
