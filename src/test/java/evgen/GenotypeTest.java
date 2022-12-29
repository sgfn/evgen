package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenotypeTest {
    private static final long rngSeed = 20010911l;
    private static final int mockGenomeLength = 10;
    private static final Settings s = mock(Settings.class);

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
    void testGenotypeDefaultConstructorAndNextDirection() {
        final int[] expectedGenome = {0, 0, 4, 5, 0, 6, 6, 5, 2, 6};

        Genotype g = new Genotype(rng, s);
        assertEquals("0045066526", g.toString());
        assertEquals(0, g.getNextGeneIndex());

        for (int i = 0; i < 111; ++i) {
            assertEquals(i % mockGenomeLength, g.getNextGeneIndex());
            assertEquals(expectedGenome[i % mockGenomeLength], g.nextDirection());
        }
    }

    @Test
    void testGenotypeNextDirectionWithCrazyBehaviour() {
        when(s.getBehaviourType()).thenReturn(Settings.BehaviourType.CRAZY);

        final int[] expectedGenome = {0, 0, 4, 5, 0, 6, 6, 5, 2, 6};
        Genotype g = new Genotype(rng, s);
        assertEquals("0045066526", g.toString());
        assertEquals(0, g.getNextGeneIndex());

        final int[] crazyEngagesAtIndices = {12, 31, 32, 33, 39};
        final int[] crazyJumpsTo = {8, 5, 5, 3, 1};
        int eventIndex = 0;
        int expectedIndex = 0;
        for (int i = 0; i < 40; ++i) {
            if (i == crazyEngagesAtIndices[eventIndex]) {
                expectedIndex = crazyJumpsTo[eventIndex++];
            }
            assertEquals(expectedIndex, g.getNextGeneIndex());
            assertEquals(expectedGenome[expectedIndex], g.nextDirection());
            expectedIndex = (expectedIndex == mockGenomeLength-1) ? 0 : ++expectedIndex; 
        }
    }

    @Test
    void testGenotypeChildConstructorNoMutations() {
        Genotype tg1 = new Genotype(rng, s);
        assertEquals("0045066526", tg1.toString());
        assertEquals(0, tg1.getNextGeneIndex());

        Genotype tg2 = new Genotype(rng, s);
        assertEquals("1164677623", tg2.toString());
        assertEquals(7, tg2.getNextGeneIndex());

        // switchSides will be false
        Genotype cg = new Genotype(tg1, tg2, 0.5);
        assertEquals("0045077623", cg.toString());
        assertEquals(5, cg.getNextGeneIndex());

        // switchSides will be true
        for (int i = 0; i < 6; ++i) {
            rng.nextBoolean();
        }
        Genotype cg2 = new Genotype(tg1, tg2, 0.5);
        assertEquals("1164666526", cg2.toString());
        assertEquals(6, cg2.getNextGeneIndex());

        // Check different ratio; switchSides will be false
        Genotype cg3 = new Genotype(tg1, tg2, 0.29);
        assertEquals("0064677623", cg3.toString());
        assertEquals(7, cg3.getNextGeneIndex());

        // Check different ratio, switchSides will be true
        Genotype cg4 = new Genotype(tg1, tg2, 0.13);
        assertEquals("1164677626", cg4.toString());
        assertEquals(1, cg4.getNextGeneIndex());
    }

    @Test
    void testGenotypeChildConstructorWithStepMutations() {
        // Always 10 mutations
        when(s.getMinMutations()).thenReturn(10);
        when(s.getMaxMutations()).thenReturn(10);

        Genotype tg1 = new Genotype(rng, s);
        assertEquals("0045066526", tg1.toString());
        assertEquals(0, tg1.getNextGeneIndex());

        Genotype tg2 = new Genotype(rng, s);
        assertEquals("1164677623", tg2.toString());
        assertEquals(7, tg2.getNextGeneIndex());

        // switchSides will be false
        Genotype cg = new Genotype(tg1, tg2, 0.5);
        assertEquals("7154766534", cg.toString());
        assertEquals(5, cg.getNextGeneIndex());

        // switchSides will be true
        Genotype cg2 = new Genotype(tg1, tg2, 0.3);
        assertEquals("2073706415", cg2.toString());
        assertEquals(0, cg2.getNextGeneIndex());
    }

    @Test
    void testGenotypeChildConstructorWithRandomMutations() {
        when(s.getMinMutations()).thenReturn(2);
        when(s.getMaxMutations()).thenReturn(4);
        when(s.getMutationType()).thenReturn(Settings.MutationType.RANDOM);

        Genotype tg1 = new Genotype(rng, s);
        assertEquals("0045066526", tg1.toString());
        assertEquals(0, tg1.getNextGeneIndex());

        Genotype tg2 = new Genotype(rng, s);
        assertEquals("1164677623", tg2.toString());
        assertEquals(7, tg2.getNextGeneIndex());

        // switchSides will be false; 3 mutations: indices 1, 7, 9 to 3, 0, 0, respectively
        Genotype cg = new Genotype(tg1, tg2, 0.5);
        assertEquals("0345077020", cg.toString());

        // switchSides will be true; 3 mutations: indices 8, 7, 3 to 1, 4, 3, respectively
        Genotype cg2 = new Genotype(tg1, tg2, 0.2);
        assertEquals("1163677416", cg2.toString());

    }
}
