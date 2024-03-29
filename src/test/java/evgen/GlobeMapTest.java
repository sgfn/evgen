package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import evgen.lib.Pair;

public class GlobeMapTest {
    private static final long rngSeed = 20010911l;
    private static final Settings s = mock(Settings.class);
    private static final IFoliageGrower f = mock(EquatorialGrower.class);

    private Random rng;

    @BeforeAll
    static void setUpOnce() {}

    @BeforeEach
    void setUp() {
        rng = new Random(rngSeed);
        when(s.getMapWidth()).thenReturn(5);
        when(s.getMapHeight()).thenReturn(5);
        when(s.getStartingFoliage()).thenReturn(0);
    }

    @Test
    void testGlobeMapAttemptMove() {
        GlobeMap m = new GlobeMap(rng, s, f);
        Animal a = mock(Animal.class);
        when(a.getPosition()).thenReturn(new Vector2d(2, 2));
        when(a.getFacing()).thenReturn(MapDirection.NORTH);
        m.place(a);

        // Regular move
        assertEquals(new Pair<>(new Vector2d(2, 3), MapDirection.NORTH), m.getMoveTarget(a));

        // North pole - get turned around
        when(a.getPosition()).thenReturn(new Vector2d(2, 4));
        assertEquals(new Pair<>(new Vector2d(2, 4), MapDirection.SOUTH), m.getMoveTarget(a));

        when(a.getFacing()).thenReturn(MapDirection.NORTHWEST);
        assertEquals(new Pair<>(new Vector2d(2, 4), MapDirection.SOUTHEAST), m.getMoveTarget(a));

        when(a.getPosition()).thenReturn(new Vector2d(0, 4));
        assertEquals(new Pair<>(new Vector2d(0, 4), MapDirection.SOUTHEAST), m.getMoveTarget(a));

        // South pole - same
        when(a.getPosition()).thenReturn(new Vector2d(0, 0));
        when(a.getFacing()).thenReturn(MapDirection.SOUTHWEST);
        assertEquals(new Pair<>(new Vector2d(0, 0), MapDirection.NORTHEAST), m.getMoveTarget(a));

        // Leaving to the west - wrap around
        when(a.getFacing()).thenReturn(MapDirection.WEST);
        assertEquals(new Pair<>(new Vector2d(4, 0), MapDirection.WEST), m.getMoveTarget(a));

        // Leaving to the east - same
        when(a.getPosition()).thenReturn(new Vector2d(4, 0));
        when(a.getFacing()).thenReturn(MapDirection.NORTHEAST);
        assertEquals(new Pair<>(new Vector2d(0, 1), MapDirection.NORTHEAST), m.getMoveTarget(a));
    }
}
