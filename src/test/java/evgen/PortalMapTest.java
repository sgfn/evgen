package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PortalMapTest {
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
    void testPortalMapAttemptMove() {
        PortalMap m = new PortalMap(rng, s, f);
        Animal a = mock(Animal.class);
        when(a.getPosition()).thenReturn(new Vector2d(2, 2));
        when(a.getFacing()).thenReturn(MapDirection.NORTH);
        m.place(a);

        // Regular move
        assertEquals(new Pair<>(new Vector2d(2, 3), MapDirection.NORTH), m.attemptMove(a));

        // Any side - get teleported, lose energy
        when(a.getPosition()).thenReturn(new Vector2d(2, 4));
        assertEquals(new Pair<>(new Vector2d(0, 3), MapDirection.NORTH), m.attemptMove(a));
        verify(a, times(1)).loseEnergy();

        when(a.getPosition()).thenReturn(new Vector2d(0, 0));
        when(a.getFacing()).thenReturn(MapDirection.SOUTHWEST);
        assertEquals(new Pair<>(new Vector2d(2, 3), MapDirection.NORTH), m.attemptMove(a));
        verify(a, times(2)).loseEnergy();

        when(a.getFacing()).thenReturn(MapDirection.WEST);
        assertEquals(new Pair<>(new Vector2d(1, 2), MapDirection.SOUTHWEST), m.attemptMove(a));
        verify(a, times(3)).loseEnergy();

        when(a.getPosition()).thenReturn(new Vector2d(4, 4));
        when(a.getFacing()).thenReturn(MapDirection.SOUTHEAST);
        assertEquals(new Pair<>(new Vector2d(3, 3), MapDirection.NORTHEAST), m.attemptMove(a));
        verify(a, times(4)).loseEnergy();
    }
}
