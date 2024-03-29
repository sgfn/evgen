package evgen;

import java.util.Random;

import evgen.lib.Pair;

public class GlobeMap extends AbstractWorldMap {
    public GlobeMap(Random r, Settings s, StatTracker st, IFoliageGrower f) {
        super(r, s, st, f);
    }

    public GlobeMap(Random r, Settings s, StatTracker st) {
        super(r, s, st);
    }

//    public GlobeMap() {
//        this(World.rng, World.settings);
//    }

    @Override
    public Pair<Vector2d, MapDirection> getMoveTarget(Animal a) {
        final Vector2d fromPos = a.getPosition();
        final MapDirection fromDir = a.getFacing();
        // Leaving map to the north/south -- stay in place, change direction
        if ((fromPos.y == boundaryLowerLeft.y  && (fromDir == MapDirection.SOUTH || fromDir == MapDirection.SOUTHEAST || fromDir == MapDirection.SOUTHWEST)) ||
            (fromPos.y == boundaryUpperRight.y && (fromDir == MapDirection.NORTH || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.NORTHWEST))) {
            return new Pair<>(fromPos, fromDir.updateDirection(4));
        }
        // Leaving map to the west -- appear at east end
        if (fromPos.x == boundaryLowerLeft.x && (fromDir == MapDirection.WEST || fromDir == MapDirection.NORTHWEST || fromDir == MapDirection.SOUTHWEST)) {
            return new Pair<>(new Vector2d(boundaryUpperRight.x, fromPos.add(fromDir.toUnitVector()).y), fromDir);
        }
        // Leaving map to the east -- appear at west end
        if (fromPos.x == boundaryUpperRight.x && (fromDir == MapDirection.EAST || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.SOUTHEAST)) {
            return new Pair<>(new Vector2d(boundaryLowerLeft.x, fromPos.add(fromDir.toUnitVector()).y), fromDir);
        }
        // Regular behaviour
        return new Pair<>(fromPos.add(fromDir.toUnitVector()), fromDir);
    }
}
