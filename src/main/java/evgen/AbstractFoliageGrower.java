package evgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.javatuples.Pair;

import evgen.lib.RandomSet;

public abstract class AbstractFoliageGrower implements IFoliageGrower {
    protected final Random rng;
    protected final Settings settings;
    protected final IWorldMap map;
    protected final int preferredSpotAmount;
    protected Set<Vector2d> preferredSpots = new HashSet<>();

    protected RandomSet<Vector2d> availableRegularSpots = new RandomSet<>();
    protected RandomSet<Vector2d> availablePreferredSpots = new RandomSet<>();

    protected abstract void setupPreferredSpots();

    public AbstractFoliageGrower(Random r, Settings s, IWorldMap m) {
        rng = r;
        settings = s;
        map = m;
        preferredSpotAmount = settings.getMapHeight() * settings.getMapWidth() / 5;
        final Pair<Vector2d, Vector2d> bounds = m.getMapBounds();
        for (int x = bounds.getValue0().x; x <= bounds.getValue1().x; ++x) {
            for (int y = bounds.getValue0().y; y <= bounds.getValue1().y; ++y) {
                availableRegularSpots.add(new Vector2d(x, y));
            }
        }

        setupPreferredSpots();

        availablePreferredSpots.addAll(preferredSpots);
        availableRegularSpots.removeAll(preferredSpots);
    }

    public void plantEaten(Vector2d pos) {
        if (preferredSpots.contains(pos)) {
            availablePreferredSpots.add(pos);
        } else {
            availableRegularSpots.add(pos);
        }
    }

    public Vector2d getPlantSpot() {
        Vector2d rv = null;
        // 80% chance that a plant will grow at a preferred spot
        if (rng.nextInt(5) != 0 && availablePreferredSpots.size() > 0) {
            rv = availablePreferredSpots.pollRandom(rng);
        } else if (availableRegularSpots.size() > 0) {
            rv = availableRegularSpots.pollRandom(rng);
        }
        return rv;
    }

    public abstract void animalDiedAt(Vector2d pos);
}
