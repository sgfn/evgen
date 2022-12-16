package evgen;

import java.util.Random;

import org.javatuples.Pair;

public class PortalMap extends AbstractWorldMap {
    private final Random rng;

    public PortalMap(Settings s, long seed) {
        super(s.getMapWidth(), s.getMapHeight());
        rng = new Random(seed);
    }

    public PortalMap(Settings s) {
        super(s.getMapWidth(), s.getMapHeight());
        rng = new Random();
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {}

    public Pair<Vector2d, MapDirection> attemptMove(Animal a) {
        // Leaving map from whichever side
        final Vector2d fromPos = a.getPosition();
        final MapDirection fromDir = a.getFacing();
        if ((fromPos.y == boundaryLowerLeft.y  && (fromDir == MapDirection.SOUTH || fromDir == MapDirection.SOUTHEAST || fromDir == MapDirection.SOUTHWEST)) ||
            (fromPos.y == boundaryUpperRight.y && (fromDir == MapDirection.NORTH || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.NORTHWEST)) ||
            (fromPos.x == boundaryLowerLeft.x  && (fromDir == MapDirection.WEST  || fromDir == MapDirection.NORTHWEST || fromDir == MapDirection.SOUTHWEST)) ||
            (fromPos.x == boundaryUpperRight.x && (fromDir == MapDirection.EAST  || fromDir == MapDirection.NORTHEAST || fromDir == MapDirection.SOUTHEAST))) {
            a.loseEnergy();
            return new Pair<>(
                new Vector2d(rng.nextInt(boundaryLowerLeft.x, boundaryUpperRight.x),
                             rng.nextInt(boundaryLowerLeft.y, boundaryUpperRight.y)),
                MapDirection.intToDir[rng.nextInt(8)]
            );
        }
        // Regular behaviour
        return new Pair<>(fromPos.add(fromDir.toUnitVector()), fromDir);
    }
}
