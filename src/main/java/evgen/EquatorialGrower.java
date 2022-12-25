package evgen;

import java.util.Random;

import org.javatuples.Pair;

public class EquatorialGrower extends AbstractFoliageGrower {
    protected void setupPreferredSpots() {
        final Pair<Vector2d, Vector2d> bounds = map.getMapBounds();
        int midY = (bounds.getValue0().y + bounds.getValue1().y) / 2;
        int currY = midY;
        int diffY = 1;
        int currX = bounds.getValue0().x;
        int preferredCounter = preferredSpotAmount;
        while (preferredCounter > 0) {
            if (currX > bounds.getValue1().x) {
                currX = bounds.getValue0().x;
                currY = midY + diffY;
                diffY = (diffY < 0) ? -diffY + 1 : -diffY;
            }
            preferredSpots.add(new Vector2d(currX, currY));
            ++currX;
            --preferredCounter;
        }
    }

    public EquatorialGrower(Random r, Settings s, IWorldMap m) {
        super(r, s, m);
    }

    public EquatorialGrower(IWorldMap m) {
        this(World.rng, World.settings, m);
    }

    public void animalDiedAt(Vector2d pos) {};
}
