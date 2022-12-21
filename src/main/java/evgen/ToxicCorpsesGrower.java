package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ToxicCorpsesGrower extends AbstractFoliageGrower {
    private Map<Vector2d, Integer> spotDeathCounters = new HashMap<>();
    private SortedMap<Integer, LinkedList<Vector2d>> notPreferredSpots = new TreeMap<>();
    private int currentHighestDeathAmountOfPreferredSpots = 0;

    protected void setupPreferredSpots() {
        preferredSpots.addAll(availableRegularSpots);
    }

    public ToxicCorpsesGrower(IWorldMap m) {
        super(m);
    }

    public void informAnimalDiedAt(Vector2d pos) {
        // XXX: maybe rethink? refactor to be able to inform about several deaths at same spot at once?
        // XXX: The following has an exceedingly high chance of being bugged to hell. You have been warned.
        int currAtPos = spotDeathCounters.getOrDefault(pos, 0) + 1;
        spotDeathCounters.put(pos, currAtPos);
        if (preferredSpots.contains(pos) && currAtPos > currentHighestDeathAmountOfPreferredSpots) {
            // Remove spot from preferred, add to notPreferred
            preferredSpots.remove(pos);
            if (!notPreferredSpots.containsKey(currAtPos)) {
                notPreferredSpots.put(currAtPos, new LinkedList<>());
            }
            notPreferredSpots.get(currAtPos).add(pos);

            // Find another one in notPreferred, remove from that and add to preferred
            if (preferredSpots.size() < preferredSpotAmount) {
                LinkedList<Vector2d> l = notPreferredSpots.get(notPreferredSpots.firstKey());
                Vector2d newPreferred = l.removeFirst();
                if (l.isEmpty()) {
                    // Death counters cannot go down, so we'll never need these entries again
                    notPreferredSpots.remove(notPreferredSpots.firstKey());
                }
                preferredSpots.add(newPreferred);
                // XXX: max() might not be necessary here
                currentHighestDeathAmountOfPreferredSpots = Math.max(spotDeathCounters.get(newPreferred), currentHighestDeathAmountOfPreferredSpots);
            }
        }
    }
}
