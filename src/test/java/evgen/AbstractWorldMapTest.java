package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractWorldMapTest {
    private static final long rngSeed = 20010911l;
    private static final Settings s = mock(Settings.class);
    
    private Random rng;
    private IFoliageGrower f;

    @BeforeAll
    static void setUpOnce() {}

    @BeforeEach
    void setUp() {
        rng = new Random(rngSeed);
        f = mock(EquatorialGrower.class);
        when(s.getMapWidth()).thenReturn(5);
        when(s.getMapHeight()).thenReturn(5);
        when(s.getStartingFoliage()).thenReturn(0);
    }

    @Test
    void testAbstractWorldMapCanPlaceAt() {
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        assertTrue(m.canPlaceAt(new Vector2d(0, 0)));
        assertTrue(m.canPlaceAt(new Vector2d(0, 4)));
        assertTrue(m.canPlaceAt(new Vector2d(2, 0)));
        assertTrue(m.canPlaceAt(new Vector2d(3, 2)));
        assertTrue(m.canPlaceAt(new Vector2d(4, 4)));
        assertFalse(m.canPlaceAt(new Vector2d(-1, 0)));
        assertFalse(m.canPlaceAt(new Vector2d(0, -15)));
        assertFalse(m.canPlaceAt(new Vector2d(-1, -5000)));
        assertFalse(m.canPlaceAt(new Vector2d(5, 0)));
        assertFalse(m.canPlaceAt(new Vector2d(4, 18)));
        assertFalse(m.canPlaceAt(new Vector2d(5, 18239)));
    }

    @Test
    void testAbstractWorldMapPlace() {
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        Animal a = mock(Animal.class);
        when(a.getPosition()).thenReturn(new Vector2d(3, 3));
        when(a.getID()).thenReturn(1337);

        assertTrue(m.place(a));
        verify(a).addObserver(m);
        assertTrue(m.animalsByID.containsKey(1337));
        assertTrue(m.animals.containsKey(a.getPosition()));
        assertTrue(m.animals.get(a.getPosition()).contains(a));

        Animal b = mock(Animal.class);
        when(b.getPosition()).thenReturn(new Vector2d(-1, 0));
        when(b.getID()).thenReturn(1338);
        assertFalse(m.place(b));
        assertFalse(m.animalsByID.containsKey(1338));
        assertFalse(m.animals.containsKey(b.getPosition()) && m.animals.get(b.getPosition()).contains(b));

        when(b.getPosition()).thenReturn(new Vector2d(3, 3));
        assertTrue(m.place(b));
        verify(b).addObserver(m);
        assertTrue(m.animalsByID.containsKey(1338));
        assertTrue(m.animals.containsKey(b.getPosition()));
        assertTrue(m.animals.get(b.getPosition()).contains(b));
    }

    @Test
    void testAbstractWorldMapGrowFoliage() {
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        when(f.getPlantSpot()).thenReturn(new Vector2d(3, 3), new Vector2d(2, 2), new Vector2d(1, 1), null);

        m.growFoliage(1);
        verify(f).getPlantSpot();
        assertTrue(m.foliage.containsKey(new Vector2d(3, 3)));

        m.growFoliage(5);
        verify(f, times(4)).getPlantSpot();
        assertTrue(m.foliage.containsKey(new Vector2d(3, 3)));
        assertTrue(m.foliage.containsKey(new Vector2d(2, 2)));
        assertTrue(m.foliage.containsKey(new Vector2d(1, 1)));
    }

    @Test
    void testAbstractWorldMapFeedAnimals() {
        when(s.getStartingFoliage()).thenReturn(1);
        when(f.getPlantSpot()).thenReturn(new Vector2d(2, 2));
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        assertTrue(m.foliage.containsKey(new Vector2d(2, 2)));

        m.feedAnimals();

        Animal a = mock(Animal.class);
        when(a.getID()).thenReturn(1337);
        when(a.getPosition()).thenReturn(new Vector2d(3, 3));
        m.place(a);

        m.feedAnimals();
        verify(a, times(0)).eat();

        m.positionChanged(1337, new Vector2d(3, 3), new Vector2d(2, 2));
        m.feedAnimals();
        verify(a, times(1)).eat();
        assertFalse(m.foliage.containsKey(new Vector2d(2, 2)));
        verify(f).plantEaten(new Vector2d(2, 2));
    }

    @Test
    void testAbstractWorldMapObjectAt() {
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        when(f.getPlantSpot()).thenReturn(new Vector2d(3, 3));
        m.growFoliage(1);
        assertEquals(Plant.class, m.objectAt(new Vector2d(3, 3)).getClass());

        Animal a = mock(Animal.class);
        when(a.getPosition()).thenReturn(new Vector2d(3, 3));
        m.place(a);
        assertEquals(a, m.objectAt(new Vector2d(3, 3)));
    }

    @Test
    void testAbstractWorldMapPositionChanged() {
        AbstractWorldMap m = new GlobeMap(rng, s, f);
        assertThrows(AssertionError.class,
        () -> m.positionChanged(1337, new Vector2d(1, 1), new Vector2d(2, 2)));

        Animal a = mock(Animal.class);
        when(a.getPosition()).thenReturn(new Vector2d(3, 3));
        when(a.getID()).thenReturn(1337);
        m.place(a);

        assertThrows(AssertionError.class,
        () -> m.positionChanged(1337, new Vector2d(1, 1), new Vector2d(2, 2)));

        Animal b = mock(Animal.class);
        when(b.getPosition()).thenReturn(new Vector2d(1, 1));
        when(b.getID()).thenReturn(2001);
        m.place(b);

        assertThrows(AssertionError.class,
        () -> m.positionChanged(1337, new Vector2d(1, 1), new Vector2d(2, 2)));

        m.positionChanged(1337, new Vector2d(3, 3), new Vector2d(2, 2));
        assertNull(m.objectAt(new Vector2d(3, 3)));
        assertEquals(a, m.objectAt(new Vector2d(2, 2)));
    }
}
