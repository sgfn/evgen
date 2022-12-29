package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import evgen.lib.Pair;

public class AnimalTest {
    private static final long rngSeed = 20010911l;
    private static final int mockGenomeLength = 10;
    private static final Settings s = mock(Settings.class);
    private static final IWorldMap m = mock(GlobeMap.class);

    private Random rng;

    @BeforeAll
    static void setUpOnce() {
        when(s.getGenomeLength()).thenReturn(mockGenomeLength);
    }
    
    @BeforeEach
    void setUp() {
        rng = new Random(rngSeed);
        GenotypeMutationIndexGenerator.init(rng);
        when(s.getBehaviourType()).thenReturn(Settings.BehaviourType.PREDESTINED);
        when(s.getMutationType()).thenReturn(Settings.MutationType.STEP);
        when(s.getMinMutations()).thenReturn(0);
        when(s.getMaxMutations()).thenReturn(0);
    }

    @Test
    void testAnimalMove() {
        Genotype g = mock(Genotype.class);
        when(g.nextDirection()).thenReturn(5);
        Animal a = new Animal(rng, s, m, new Vector2d(2, 2));
        when(m.attemptMove(a)).thenReturn(new Pair<>(new Vector2d(0, 0), MapDirection.NORTH));

        // Assert that animal will move exactly as the map tells it to
        a.move();
        assertEquals(new Vector2d(0, 0), a.getPosition());
        assertEquals(MapDirection.NORTH, a.getFacing());
    }

    @Test
    void testAnimalAgeUp() {
        when(s.getStartingEnergy()).thenReturn(4);
        Animal a = new Animal(rng, s, m, new Vector2d(2, 2));
        assertEquals(0, a.getAge());
        assertEquals(4, a.getEnergy());
        for (int i = 1; i < 4; ++i) {
            assertEquals(true, a.ageUp());
            assertEquals(i, a.getAge());
            assertEquals(4-i, a.getEnergy());
        }
        assertEquals(false, a.ageUp());
        assertEquals(4, a.getAge());
        assertEquals(0, a.getEnergy());
    }

    @Test
    void testAnimalProcreate() {
        when(s.getProcreationEnergyLoss()).thenReturn(30);
        when(s.getStartingEnergy()).thenReturn(50, 50, 10, 40);

        Genotype pg1 = new Genotype(rng, s);
        assertEquals("0045066526", pg1.toString());
        Genotype pg2 = new Genotype(rng, s);
        assertEquals("1164677623", pg2.toString());

        Animal pa1 = new Animal(rng, s, m, new Vector2d(2, 2), pg1);
        Animal pa2 = new Animal(rng, s, m, new Vector2d(2, 2), pg2);
        assertEquals(50, pa1.getEnergy());
        assertEquals(50, pa2.getEnergy());
        assertEquals(0, pa1.getChildren());
        assertEquals(0, pa2.getChildren());

        // switchSides=false, ratio=0.5
        Animal ca = pa1.procreate(pa2);
        assertEquals(60, ca.getEnergy());
        assertEquals(20, pa1.getEnergy());
        assertEquals(20, pa2.getEnergy());
        assertEquals(1, pa1.getChildren());
        assertEquals(1, pa2.getChildren());

        assertEquals("0045077623", ca.genes.toString());

        pa1 = new Animal(rng, s, m, new Vector2d(2, 2), pg1);
        pa2 = new Animal(rng, s, m, new Vector2d(2, 2), pg2);
        assertEquals(10, pa1.getEnergy());
        assertEquals(40, pa2.getEnergy());

        // switchSides=false, ratio=0.2
        ca = pa1.procreate(pa2);
        assertEquals(60, ca.getEnergy());
        assertEquals(-20, pa1.getEnergy());
        assertEquals(10, pa2.getEnergy());

        assertEquals("0064677623", ca.genes.toString());
    }
}
