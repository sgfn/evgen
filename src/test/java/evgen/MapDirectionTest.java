package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MapDirectionTest {
    @Test
    void testMapDirectionUpdateDirection() {
        MapDirection[] mapDirs = {MapDirection.NORTH, MapDirection.EAST, MapDirection.SOUTH, MapDirection.NORTHWEST};
        MapDirection[] diffMapDirs = {MapDirection.EAST, MapDirection.SOUTH, MapDirection.WEST, MapDirection.SOUTHWEST};
        MapDirection[] expectedMapDirs = {MapDirection.EAST, MapDirection.WEST, MapDirection.EAST, MapDirection.SOUTH};
        for (int i = 0; i < 4; ++i) {
            assertEquals(mapDirs[i].updateDirection(diffMapDirs[i]), expectedMapDirs[i]);
        }
    }
}
