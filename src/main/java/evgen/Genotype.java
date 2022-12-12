package evgen;

import java.util.Random;

public class Genotype {
    public static final int differentGenesAmount = 8;

    public final int genotypeLength;
    private final Random rng;
    private final boolean stepMutations;
    private final boolean crazyBehaviour;

    private int[] genome;
    private int nextGeneIndex;

    private final Settings settings;

    private Genotype(Settings s, Random rng) {
        settings = s;
        genotypeLength = s.getGenomeLength();
        stepMutations = s.getMutationType() == Settings.MutationType.STEP;
        crazyBehaviour = s.getBehaviourType() == Settings.BehaviourType.CRAZY;
        nextGeneIndex = rng.nextInt(genotypeLength);
        genome = new int[genotypeLength];
        this.rng = rng;

        for (int i = 0; i < genotypeLength; ++i) {
            genome[i] = rng.nextInt(differentGenesAmount);
        }
    }

    /**
     * Create a child's genome from two parent's genomes
     * @param pg1 Genome of first parent
     * @param pg2 Genome of second parent
     * @param ratio Ratio to mix genes at (pg1.energy/(pg1.energy+pg2.energy))
     */
    public Genotype(Genotype pg1, Genotype pg2, double ratio) {
        settings = pg1.settings;
        genotypeLength = pg1.genotypeLength;
        stepMutations = pg1.stepMutations;
        crazyBehaviour = pg1.crazyBehaviour;
        rng = new Random();
        nextGeneIndex = rng.nextInt(genotypeLength);
        genome = new int[genotypeLength];

        // randomise side from which we take the genome
        boolean switchSides = rng.nextBoolean();

        // find index at which to cut the genome
        int cut = (int)(ratio * genotypeLength);
        if (cut == genotypeLength) {
            --cut;
        }
        for (int i = 0; i <= cut; ++i) {
            genome[i] = pg1.genome[switchSides ? i : genotypeLength-i-1];
        }
        for (int i = cut+1; i < genotypeLength; ++i) {
            genome[i] = pg2.genome[switchSides ? i : genotypeLength-i-1];
        }

        int mutationAmount = rng.nextInt(settings.getMinMutations(), settings.getMaxMutations());
        for (int i = 0; i < mutationAmount; ++i) {
            int mutationIndex = rng.nextInt(genotypeLength);
            if (!stepMutations) {
                genome[mutationIndex] = rng.nextInt(differentGenesAmount);
            } else {
                int g = genome[mutationIndex] + (rng.nextBoolean() ? 1 : -1);
                if (g == -1) {
                    g = genotypeLength-1;
                } else if (g == genotypeLength) {
                    g = 0;
                }
                genome[mutationIndex] = g;
            }
        }
    }

    /**
     * Create a new genotype -- length, mutation variant and behaviour variant
     * as in the provided settings (with a set seed)
     * @param s Settings object from which to read the data
     * @param seed seed for the RNG
     */
    public Genotype(Settings s, long seed) {
        this(s, new Random(seed));
    }

    /**
     * Create a new genotype -- length, mutation variant and behaviour variant
     * as in the provided settings (with a random seed)
     * @param s Settings object from which to read the data
     */
    public Genotype(Settings s) {
        this(s, new Random());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (final int gene : genome) {
            s.append(gene);
        }
        return s.toString();
    }

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
}
