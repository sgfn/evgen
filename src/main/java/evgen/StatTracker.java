package evgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StatTracker {

    private int animalCount = 0;
    private int foliageCount = 0;
    private int freeFieldsCount = 0;
    private final HashMap<Genotype, HashSet<Integer>> genotypePopularity = new HashMap<>();
    private int totalEnergy = 0;
    private int totalLifeLength = 0;
    private int deathCount = 0;
    private int epoch = 0;

    private File logsFile = null;
    private FileWriter fileWriter;

    public StatTracker() {} //stat tracker not logging stats to file
    public StatTracker(String path, int statTrackerID) {    //stat tracker logging stats to file
        if (path != null) {
            this.logsFile = new File(path + "/evgen" + statTrackerID + "_logs.csv");
            try {
                logsFile.createNewFile();
                fileWriter = new FileWriter(logsFile);
                fileWriter.write("day,animalCount,foliageCount,freeFieldsCount,mostPopularGenome,avgEnergyLvl,avgLifeLength");
                fileWriter.flush();
            } catch (IOException e) {
                System.out.println("invalid stat tracker csv path");
            }
        }
    }

    public void nextEpoch() {
        epoch++;
    }
    public void setAnimalCount(int animalCount) {
        this.animalCount = animalCount;
    }

    public void setFoliageCount(int foliageCount) {
        this.foliageCount = foliageCount;
    }

    public void setFreeFieldsCount(int freeFieldsCount) {
        this.freeFieldsCount = freeFieldsCount;
    }

    public void addGenotype(Genotype genotype, int animalID) {
        HashSet<Integer> list = genotypePopularity.get(genotype);
        if (list == null) {
            list = new HashSet<>();
            list.add(animalID);
            genotypePopularity.put(genotype, list);
        } else {
            genotypePopularity.get(genotype).add(animalID);
        }
    }

    public void updateTotalEnergy(int energy) {
        this.totalEnergy += energy;
    }

    public void animalDied(Animal animal) {
        deathCount += 1;
        totalLifeLength += animal.getAge();
        genotypePopularity.get(animal.genes).remove(animal.id);
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public int getFoliageCount() {
        return foliageCount;
    }

    public int getFreeFieldsCount() {
        return freeFieldsCount;
    }

    public int getEpoch() {
        return epoch;
    }

    public Genotype getMostPopularGenotype() {
        Map.Entry<Genotype, HashSet<Integer>> max = null;
        for (Map.Entry<Genotype, HashSet<Integer>> entry : genotypePopularity.entrySet()) {
            if (max == null || max.getValue().size() < entry.getValue().size()) {
                max = entry;
            }
        }
        return max == null ? null : max.getKey();
    }

    public HashSet<Integer> getMostPopularGenotypeIDs() {
        return genotypePopularity.get(getMostPopularGenotype());
    }

    public double getAvgEnergy() {
        return Math.round(100.0 * totalEnergy / animalCount) / 100.0;
    }

    public double getAvgLifeLength() {
        return Math.round(100.0 * totalLifeLength / deathCount) / 100.0;
    }

    public void logEpoch() {
        if (logsFile != null) {
            try {
                fileWriter.write("\n" + String.join(",", Integer.toString(epoch), Integer.toString(animalCount), Integer.toString(foliageCount), Integer.toString(freeFieldsCount), getMostPopularGenotype().toString(), Double.toString(getAvgEnergy()), Double.toString(getAvgLifeLength())));
                fileWriter.flush();
            } catch (IOException e) {
                System.out.println("wrong path");
            }
        }
    }

    public void importSettings(Settings settings) {
        setAnimalCount(settings.getStartingAnimals());
        setFoliageCount(settings.getStartingFoliage());
        setFreeFieldsCount(settings.getMapWidth() * settings.getMapHeight() - settings.getStartingAnimals() - settings.getStartingFoliage());
        updateTotalEnergy(settings.getStartingAnimals() * settings.getStartingEnergy());
    }
}
