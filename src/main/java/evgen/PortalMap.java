package evgen;

import java.util.Random;

import evgen.lib.Pair;

public class PortalMap extends AbstractWorldMap {
    public PortalMap(Random r, Settings s, IFoliageGrower f) {
        super(r, s, f);
    }

    public PortalMap(Random r, Settings s) {
        super(r, s);
    }

    public PortalMap() {
        this(World.rng, World.settings);
    }

    @Override
    public Pair<Vector2d, MapDirection> attemptMove(Animal a) {
        // Leaving map from whichever side
        final Vector2d fromPos = a.getPosition();
        final MapDirection fromDir = a.getFacing();
        if ((fromPos.y == boundaryLowerLeft.y  && (fromDir == MapDirection.SOUTH || fromDir == MapDirection.SOUTHEAST || fromDir == MapDirection.SOUTHWEST)) ||
            (fromPos.y == boundaryUpperRight.y && (fromDir == MapDirection.NORTH || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.NORTHWEST)) ||
            (fromPos.x == boundaryLowerLeft.x  && (fromDir == MapDirection.WEST  || fromDir == MapDirection.NORTHWEST || fromDir == MapDirection.SOUTHWEST)) ||
            (fromPos.x == boundaryUpperRight.x && (fromDir == MapDirection.EAST  || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.SOUTHEAST))) {
            // Hellish portal hurts
            a.loseEnergy();
            return new Pair<>(
                new Vector2d(rng.nextInt(boundaryLowerLeft.x, boundaryUpperRight.x),
                             rng.nextInt(boundaryLowerLeft.y, boundaryUpperRight.y)),
                MapDirection.fromInt(rng.nextInt(MapDirection.directionCount))
            );
        }
        // Regular behaviour
        return new Pair<>(fromPos.add(fromDir.toUnitVector()), fromDir);
    }
}
