package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MapDirectionTest {
    private static final MapDirection[] mapDirsInOrder = {MapDirection.NORTH, MapDirection.NORTHEAST, MapDirection.EAST, MapDirection.SOUTHEAST, MapDirection.SOUTH, MapDirection.SOUTHWEST, MapDirection.WEST, MapDirection.NORTHWEST};
    @Test
    void testMapDirectionFromInt() {
        for (int i = 0; i < 8; ++i) {
            assertEquals(mapDirsInOrder[i], MapDirection.fromInt(i));
        }
    }

    @Test
    void testMapDirectionToUnitVector() {
        Vector2d[] expectedVectors = {new Vector2d(0, 1), new Vector2d(1, 1), new Vector2d(1, 0), new Vector2d(1, -1), new Vector2d(0, -1), new Vector2d(-1, -1), new Vector2d(-1, 0), new Vector2d(-1, 1)};
        for (int i = 0; i < 8; ++i) {
            assertEquals(expectedVectors[i], mapDirsInOrder[i].toUnitVector());
        }
    }

    @Test
    void testMapDirectionUpdateDirection() {
        MapDirection[] mapDirs = {MapDirection.NORTH, MapDirection.EAST, MapDirection.SOUTH, MapDirection.NORTHWEST};
        MapDirection[] diffMapDirs = {MapDirection.EAST, MapDirection.SOUTH, MapDirection.WEST, MapDirection.SOUTHWEST};
        int[] diffInts = {2, 4, 6, 5};
        MapDirection[] expectedMapDirs = {MapDirection.EAST, MapDirection.WEST, MapDirection.EAST, MapDirection.SOUTH};
        for (int i = 0; i < 4; ++i) {
            assertEquals(expectedMapDirs[i], mapDirs[i].updateDirection(diffMapDirs[i]));
            assertEquals(expectedMapDirs[i], mapDirs[i].updateDirection(diffInts[i]));
        }
    }
}
