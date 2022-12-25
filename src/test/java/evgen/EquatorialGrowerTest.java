package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EquatorialGrowerTest {
    private static final long rngSeed = 20010911l;
    private static final Settings s = mock(Settings.class);
    private static final IWorldMap m = mock(GlobeMap.class);

    private Random rng;

    @BeforeAll
    static void setUpOnce() {}

    @BeforeEach
    void setUp() {
        rng = new Random(rngSeed);
        when(s.getMapWidth()).thenReturn(5);
        when(s.getMapHeight()).thenReturn(5);
        when(m.getMapBounds()).thenReturn(new Pair<>(new Vector2d(0, 0), new Vector2d(4, 4)));
    }

    @Test
    void testEquatorialGrowerConstructor() {
        EquatorialGrower g = new EquatorialGrower(rng, s, m);
        assertEquals(5, g.preferredSpotAmount);
        assertEquals(5, g.preferredSpots.size());
        assertEquals(5, g.availablePreferredSpots.size());
        assertEquals(20, g.availableRegularSpots.size());

        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                assertEquals(y == 2, g.preferredSpots.contains(new Vector2d(x, y)));
                assertEquals(y == 2, g.availablePreferredSpots.contains(new Vector2d(x, y)));
                assertEquals(y != 2, g.availableRegularSpots.contains(new Vector2d(x, y)));
            }
        }
    }
}
