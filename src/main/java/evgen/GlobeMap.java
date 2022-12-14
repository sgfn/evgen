package evgen;

import org.javatuples.Pair;

public class GlobeMap extends AbstractWorldMap {
    public GlobeMap(Settings s) {
        super(s.getMapWidth(), s.getMapHeight());
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {}

    public Pair<Vector2d, MapDirection> attemptMove(Vector2d fromPos, MapDirection fromDir) {
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
