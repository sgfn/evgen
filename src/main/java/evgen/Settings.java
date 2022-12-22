package evgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Settings {
    // VARIANT ENUM DEFINITIONS
    public enum MapType {
        GLOBE, PORTAL
    }
    public enum FoliageGrowthType {
        EQUATOR, TOXIC
    }
    public enum MutationType {
        RANDOM, STEP
    }
    public enum BehaviourType {
        PREDESTINED, CRAZY
    }

    // VALUES
    private int mapWidth;
    private int mapHeight;
    private int startingFoliage;
    private int energyGain;
    private int dailyFoliageGrowth;
    private int startingAnimals;
    private int startingEnergy;
    private int minProcreationEnergy;
    private int procreationEnergyLoss;
    private int minMutations;
    private int maxMutations;
    private int genomeLength;

    // VARIANTS
    private MapType mapType;
    private FoliageGrowthType foliageGrowthType;
    private MutationType mutationType;
    private BehaviourType behaviourType;

    // meta
    private boolean loadSuccessful;

    // PRIVATE METHODS
    private boolean loadConfigInternal(String configPath) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(configPath));
        } catch (FileNotFoundException e) {
            return false;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        try {
            for (final String category : data.keySet()) {
                final Object val = data.get(category);
                switch (category) {
                    case "map_width":
                        mapWidth = (int)val;
                        break;
                    case "map_height":
                        mapHeight = (int)val;
                        break;
                    case "starting_foliage":
                        startingFoliage = (int)val;
                        break;
                    case "energy_gain":
                        energyGain = (int)val;
                        break;
                    case "daily_foliage_growth":
                        dailyFoliageGrowth = (int)val;
                        break;
                    case "starting_animals":
                        startingAnimals = (int)val;
                        break;
                    case "starting_energy":
                        startingEnergy = (int)val;
                        break;
                    case "min_procreation_energy":
                        minProcreationEnergy = (int)val;
                        break;
                    case "procreation_energy_loss":
                        procreationEnergyLoss = (int)val;
                        break;
                    case "min_mutations":
                        minMutations = (int)val;
                        break;
                    case "max_mutations":
                        maxMutations = (int)val;
                        break;
                    case "genome_length":
                        genomeLength = (int)val;
                        break;
                    case "map_variant":
                        if (((String)val).equals("globe")) {
                            mapType = MapType.GLOBE;
                        } else if (((String)val).equals("portal")) {
                            mapType = MapType.PORTAL;
                        } else {
                            return false;
                        }
                        break;
                    case "foliage_variant":
                        if (((String)val).equals("equator")) {
                            foliageGrowthType = FoliageGrowthType.EQUATOR;
                        } else if (((String)val).equals("toxic")) {
                            foliageGrowthType = FoliageGrowthType.TOXIC;
                        } else {
                            return false;
                        }
                        break;
                    case "mutation_variant":
                        if (((String)val).equals("random")) {
                            mutationType = MutationType.RANDOM;
                        } else if (((String)val).equals("step")) {
                            mutationType = MutationType.STEP;
                        } else {
                            return false;
                        }
                        break;
                    case "behaviour_variant":
                        if (((String)val).equals("predestined")) {
                            behaviourType = BehaviourType.PREDESTINED;
                        } else if (((String)val).equals("crazy")) {
                            behaviourType = BehaviourType.CRAZY;
                        } else {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }

        return true;
    }

    public Settings() {
        loadSuccessful = false;
        restoreDefaults();
    }

    public Settings(String configPath) {
        loadConfig(configPath);
    }

    public void restoreDefaults() {
        // TODO: find sensible default values
        mapWidth = 20;
        mapHeight = 10;
        startingFoliage = 5;
        energyGain = 5;
        dailyFoliageGrowth = 2;
        startingAnimals = 2;
        startingEnergy = 30;
        minProcreationEnergy = 15;
        procreationEnergyLoss = 10;
        minMutations = 0;
        maxMutations = 5;
        genomeLength = 32;
        mapType = MapType.GLOBE;
        foliageGrowthType = FoliageGrowthType.EQUATOR;
        mutationType = MutationType.RANDOM;
        behaviourType = BehaviourType.PREDESTINED;
    }

    // XXX: When loading new config, make sure we recreate the entire thing
    public boolean loadConfig(String configPath) {
        // Load defaults, overwrite with config, then restore defaults if anything went wrong
        restoreDefaults();
        loadSuccessful = loadConfigInternal(configPath);
        if (!loadSuccessful) {
            restoreDefaults();
        }
        return loadSuccessful;
    }

    // GETTERS
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getStartingFoliage() { return startingFoliage; }
    public int getEnergyGain() { return energyGain; }
    public int getDailyFoliageGrowth() { return dailyFoliageGrowth; }
    public int getStartingAnimals() { return startingAnimals; }
    public int getStartingEnergy() { return startingEnergy; }
    public int getMinProcreationEnergy() { return minProcreationEnergy; }
    public int getProcreationEnergyLoss() { return procreationEnergyLoss; }
    public int getMinMutations() { return minMutations; }
    public int getMaxMutations() { return maxMutations; }
    public int getGenomeLength() { return genomeLength; }
    public MapType getMapType() { return mapType; }
    public FoliageGrowthType getFoliageGrowthType() { return foliageGrowthType; }
    public MutationType getMutationType() { return mutationType; }
    public BehaviourType getBehaviourType() { return behaviourType; }
    public boolean success() { return loadSuccessful; }
}
