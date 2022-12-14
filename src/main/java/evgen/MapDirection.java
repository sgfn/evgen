package evgen;

public enum MapDirection {
    NORTH(0),
    NORTHEAST(1),
    EAST(2),
    SOUTHEAST(3),
    SOUTH(4),
    SOUTHWEST(5),
    WEST(6),
    NORTHWEST(7);

    public static final MapDirection[] intToDir = new MapDirection[] {
        NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
    };

    public final int facing;

    MapDirection(int f) {
        facing = f;
    }

    public Vector2d toUnitVector() {
        switch(this) {
            case NORTH:     return new Vector2d(0, 1);
            case NORTHEAST: return new Vector2d(1, 1);
            case EAST:      return new Vector2d(1, 0);
            case SOUTHEAST: return new Vector2d(1, -1);
            case SOUTH:     return new Vector2d(0, -1);
            case SOUTHWEST: return new Vector2d(-1, -1);
            case WEST:      return new Vector2d(-1, 0);
            case NORTHWEST: return new Vector2d(-1, 1);
            default:        return null;
        }
    }

    public MapDirection updateDirection(int diff) {
        return intToDir[(facing + diff) % 8];
    }

    public MapDirection updateDirection(MapDirection diffDir) {
        return updateDirection(diffDir.facing);
    }
}
