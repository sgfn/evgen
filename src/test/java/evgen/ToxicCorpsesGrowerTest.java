package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToxicCorpsesGrowerTest {
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
    void testToxicCorpsesGrowerConstructor() {
        ToxicCorpsesGrower g = new ToxicCorpsesGrower(rng, s, m);
        assertEquals(5, g.preferredSpotAmount);
        assertEquals(25, g.preferredSpots.size());
        assertEquals(25, g.availablePreferredSpots.size());
        assertEquals(0, g.availableRegularSpots.size());
    }

    @Test
    void testToxicCorpsesGrowerAnimalDiedAt() {
        ToxicCorpsesGrower g = new ToxicCorpsesGrower(rng, s, m);
        // Go down to 5 preferred spots - 1 death on each field where x!=0
        for (int x = 1; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                g.animalDiedAt(new Vector2d(x, y));
            }
        }

        assertEquals(5, g.preferredSpots.size());
        assertEquals(5, g.availablePreferredSpots.size());
        assertEquals(20, g.availableRegularSpots.size());

        // Add another death
        g.animalDiedAt(new Vector2d(0, 0));

        assertEquals(5, g.preferredSpots.size());
        assertEquals(5, g.availablePreferredSpots.size());
        assertEquals(20, g.availableRegularSpots.size());
        // First deleted field will become preferred again
        assertFalse(g.preferredSpots.contains(new Vector2d(0, 0)));
        assertTrue(g.preferredSpots.contains(new Vector2d(1, 0)));

        // Bump deaths on x!=0 fields to 2
        for (int x = 1; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                g.animalDiedAt(new Vector2d(x, y));
            }
        }

        // Add a lot of deaths
        for (int i = 0; i < 10; ++i) {
            g.animalDiedAt(new Vector2d(0, 0));
        }

        assertEquals(5, g.preferredSpots.size());
        assertEquals(5, g.availablePreferredSpots.size());
        assertEquals(20, g.availableRegularSpots.size());
        assertFalse(g.preferredSpots.contains(new Vector2d(0, 0)));
        assertTrue(g.preferredSpots.contains(new Vector2d(1, 0)));

        // 0->1 deaths on preferred, shouldn't change anything, max still 2
        g.animalDiedAt(new Vector2d(0, 2));

        assertEquals(5, g.preferredSpots.size());
        assertEquals(5, g.availablePreferredSpots.size());
        assertEquals(20, g.availableRegularSpots.size());
        assertTrue(g.preferredSpots.contains(new Vector2d(0, 2)));

        // No idea what else to test... appears to be working alright
    }
}
