package evgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Genotype {
    public final int genotypeLength;
    private final boolean stepMutations;
    private final boolean crazyBehaviour;

    private final Random rng;
    private final Settings settings;

    private int[] genome;
    private int nextGeneIndex;

    /**
     * Create a new genotype -- length, mutation variant and behaviour variant
     * as in the provided settings; use provided RNG
     * @param r Random object to use as RNG
     * @param s Settings object to use as settings
     */
    public Genotype(Random r, Settings s) {
        rng = r;
        settings = s;
        genotypeLength = settings.getGenomeLength();
        stepMutations = settings.getMutationType() == Settings.MutationType.STEP;
        crazyBehaviour = settings.getBehaviourType() == Settings.BehaviourType.CRAZY;
        nextGeneIndex = rng.nextInt(genotypeLength);
        genome = new int[genotypeLength];

        for (int i = 0; i < genotypeLength; ++i) {
            genome[i] = rng.nextInt(MapDirection.directionCount);
        }
    }

    /**
     * Create a new genotype -- length, mutation variant and behaviour variant
     * as in the global settings; use global RNG
     */
    // public Genotype() {
    //     this(World.rng, World.settings);
    // }

    /**
     * Create a child's genome from two parent's genomes
     * @param pg1 Genome of first parent
     * @param pg2 Genome of second parent
     * @param ratio Ratio to mix genes at (pg1.energy/(pg1.energy+pg2.energy))
     */
    public Genotype(Genotype pg1, Genotype pg2, double ratio) {
        rng = pg1.rng;
        settings = pg1.settings;
        genotypeLength = pg1.genotypeLength;
        stepMutations = pg1.stepMutations;
        crazyBehaviour = pg1.crazyBehaviour;
        nextGeneIndex = rng.nextInt(genotypeLength);
        genome = new int[genotypeLength];

        // randomise side from which we take the genome
        final boolean switchSides = rng.nextBoolean();

        // find index at which to cut the genome
        final int cut = switchSides ? genotypeLength-(int)(ratio * genotypeLength) : (int)(ratio * genotypeLength);
        Debug.println(String.format("switchSides=%s cut=%d ratio=%f", switchSides, cut, ratio));
        for (int i = switchSides ? cut : 0; i < (switchSides ? genotypeLength : cut); ++i) {
            genome[i] = pg1.genome[i];
        }
        for (int i = switchSides ? 0 : cut; i < (switchSides ? cut : genotypeLength); ++i) {
            genome[i] = pg2.genome[i];
        }

        int mutationAmount = settings.getMinMutations();
        if (settings.getMinMutations() < settings.getMaxMutations()) {
            mutationAmount = rng.nextInt(settings.getMinMutations(), settings.getMaxMutations()+1);
        }

        List<Integer> mutationIndices = GenotypeMutationIndexGenerator.getInstance().pollRandomSeveral(genotypeLength, mutationAmount);
        for (final int mutationIndex : mutationIndices) {
            if (!stepMutations) {
                int g;
                do {
                    g = rng.nextInt(MapDirection.directionCount);
                } while (g == genome[mutationIndex]);
                genome[mutationIndex] = g;
            } else {
                final int g = genome[mutationIndex] + (rng.nextBoolean() ? 1 : -1);
                genome[mutationIndex] = (g+8) % MapDirection.directionCount;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (final int gene : genome) {
            s.append(gene);
        }
        return s.toString();
    }

    /**
     * Get next direction from genome (perhaps mutated), advance to next gene
     * @return direction -- difference to add to current animal direction
     */
    public int nextDirection() {
        int dir = genome[nextGeneIndex];
        // 20% chance to go to a random gene if crazyBehaviour enabled
        if (crazyBehaviour && rng.nextInt(5) == 0) {
            nextGeneIndex = rng.nextInt(genotypeLength);
        } else {
            ++nextGeneIndex;
            if (nextGeneIndex == genotypeLength) {
                nextGeneIndex = 0;
            }
        }
        return dir;
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Genotype) && Arrays.equals(((Genotype) obj).genome, this.genome));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genome);
    }

    public int getNextGeneIndex() { return nextGeneIndex; }
}
