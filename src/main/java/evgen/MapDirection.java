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

    public static MapDirection fromInt(int dir) {
        return intToDir[dir];
    }

    public final int facing;

    MapDirection(int f) {
        facing = f;
    }

    public Vector2d toUnitVector() {
        return switch(this) {
            case NORTH     -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST      -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH     -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST      -> new Vector2d(-1, 0);
            case NORTHWEST ->new Vector2d(-1, 1);
        };
    }

    public MapDirection updateDirection(int diff) {
        return intToDir[(facing + diff) % 8];
    }

    public MapDirection updateDirection(MapDirection diffDir) {
        return updateDirection(diffDir.facing);
    }
}
