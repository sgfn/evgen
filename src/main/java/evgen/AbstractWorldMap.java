package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import evgen.lib.Pair;

public abstract class AbstractWorldMap implements IWorldMap {
    // PROTECTED ATTRIBUTES
    protected final Vector2d boundaryLowerLeft;
    protected final Vector2d boundaryUpperRight;

    protected final Random rng;
    protected final Settings settings;
    protected final MapVisualizer mapVis;
    protected final IFoliageGrower foliageGen;
    protected final StatTracker statTracker;

    protected int nextAnimalID = 0;
    protected int epoch = 0;
    // Can have multiple animals at same spot, but only one plant
    protected Map<Integer, Animal> animalsByID = new HashMap<>();
    protected Map<Vector2d, TreeSet<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, IMapElement> foliage = new HashMap<>();

    protected List<Animal> markedForDelete = new LinkedList<>();

    // PRIVATE METHODS
    private AbstractWorldMap(Random r, Settings s, StatTracker st, IFoliageGrower f, boolean useDefaultGrowers) {
        rng = r;
        settings = s;
        statTracker = st;
        boundaryLowerLeft = new Vector2d(0, 0);
        boundaryUpperRight = new Vector2d(settings.getMapWidth()-1, settings.getMapHeight()-1);
        if (useDefaultGrowers) {
            foliageGen = (s.getFoliageGrowthType() == Settings.FoliageGrowthType.EQUATOR) ? new EquatorialGrower(this) : new ToxicCorpsesGrower(this);
        } else {
            foliageGen = f;
        }
        mapVis = new MapVisualizer(this, foliageGen);
        initAnimalStructures();
        growFoliage(settings.getStartingFoliage());
    }

    private void initAnimalStructures() {
        for (int x = boundaryLowerLeft.x; x <= boundaryUpperRight.x; ++x) {
            for (int y = boundaryLowerLeft.y; y <= boundaryUpperRight.y; ++y) {
                animals.put(new Vector2d(x, y), new TreeSet<>());
            }
        }
    }

    private void addAnimalToMap(Vector2d pos, Animal animal) {
        boolean rc = animals.get(pos).add(animal);
        assert rc;
        assert animals.get(pos).contains(animal);
    }

    private void removeAnimalFromMap(Vector2d pos, Animal animal) {
        assert animals.get(pos).contains(animal);
        final boolean rc = animals.get(pos).remove(animal);
        assert rc;
        assert !animals.get(pos).contains(animal);
    }

    private void removeAnimalFromMap(Animal animal) {
        removeAnimalFromMap(animal.getPosition(), animal);
    }

    protected void markForDelete(Animal a) {
        markedForDelete.add(a);
    }

    private void deleteMarked() {
        for (Animal a : markedForDelete) {
            Debug.println((String.format("Animal with id %d dies", a.getID())));
            removeAnimalFromMap(a);
            Object o = animalsByID.remove(a.getID());
            assert o.equals(a);
            foliageGen.animalDiedAt(a.getPosition());
            // a.removeObserver(this);
            a.death(epoch);
            a = null;
        }
        markedForDelete.clear();
    }

    private void cleanUpAll() {
        for (Animal a : animalsByID.values()) {
            if (!a.isAlive()) {
                markForDelete(a);
            }
        }
        deleteMarked();
    }

    /**
     * Get target position and direction of an animal which is about to move,
     * according to its genome and map constraints
     * @param a animal to move
     * @return pair containing target position and direction, in that order
     */
    protected abstract Pair<Vector2d, MapDirection> getMoveTarget(Animal a);

    private void moveAll() {
        for (Animal a : animalsByID.values()) {
            assert a.isAlive();
            assert animals.get(a.getPosition()).contains(a) : String.format("moveAll detected desync between animal pos and set, a=%s, s=%s", a, animals.get(a.getPosition()));
            a.updateFacing();
            removeAnimalFromMap(a.getPosition(), a);
            Pair<Vector2d, MapDirection> target = getMoveTarget(a);
            a.move(target);
            addAnimalToMap(target.first, a);
        }
        deleteMarked();
    }

    private void feedAndProcreateAll() {
        for (Vector2d spot : animals.keySet()) {
            TreeSet<Animal> s = animals.get(spot);
            // Feed strongest from spot
            if (s.size() > 0 && foliage.get(spot) != null) {
                Animal strongest = s.pollFirst();
                strongest.eat();
                s.add(strongest);
                foliage.remove(spot);
                foliageGen.plantEaten(spot);
            }
            // Allow procreation of two strongest from spot
            if (s.size() > 1) {
                Animal first = s.pollFirst();
                Animal second = s.pollFirst();
                
                // Second one cannot have more energy than first
                assert first.getEnergy() >= second.getEnergy() : "Second has more energy than first" + String.format("c=%s s=%s m.p1.e=%d m.p2.e=%d ", spot.toString(), s, first.getEnergy(), second.getEnergy());
                if (second.canProcreate()) {
                    Debug.print(String.format("procreation c=%s s=%s ", spot.toString(), s));

                    Animal child = first.procreate(second);
                    // Update positions of both parents in the set
                    s.add(first);
                    s.add(second);

                    assert s.contains(first) : "Assertion after update for first one failed";
                    assert s.contains(second) : "Assertion after update for second one failed";

                    place(child);

                    assert s.contains(first) : "Assertion after child placement for first one failed";
                    assert s.contains(second) : "Assertion after child placement for second one failed";
                    assert s.contains(child) : "Assertion after child placement for child failed";

                    Debug.println(String.format("new_s=%s", s));
                } else {
                    s.add(first);
                    s.add(second);

                    assert s.contains(first) : "Assertion after update for first one failed";
                    assert s.contains(second) : "Assertion after update for second one failed";
                }
            }
        }
    }

    private void growDailyFoliage() {
        growFoliage(settings.getDailyFoliageGrowth());
    }

    private void ageUpAll() {
        for (Animal a : animalsByID.values()) {
            assert a.isAlive();
            a.ageUp();
        }
    }

    // PROTECTED METHODS
    protected AbstractWorldMap(Random r, Settings s, StatTracker st, IFoliageGrower f) {
        this(r, s, st, f, false);
    }

    protected AbstractWorldMap(Random r, Settings s, StatTracker st) {
        this(r, s, st, null, true);
    }

    protected void growFoliage(int amount) {
        Vector2d spot;
        for (int i = 0; i < amount; ++i) {
            spot = foliageGen.getPlantSpot();
            if (spot == null) {
                return;
            }
            Plant p = new Plant(spot);
            foliage.put(p.getPosition(), p);
        }
    }

    protected void updateEpochStats() {
        statTracker.setAnimalCount(animalsByID.size());
        statTracker.setFoliageCount(foliage.size());
        statTracker.setFreeFieldsCount(foliageGen.getFreeSpotsCount());
    }

    // PUBLIC METHODS
    @Override
    public boolean canPlaceAt(Vector2d position) {
        return boundaryLowerLeft.precedes(position) && boundaryUpperRight.follows(position);
    }

    @Override
    public boolean place(Animal animal) {
        final Vector2d pos = animal.getPosition();
        if (canPlaceAt(pos)) {
            addAnimalToMap(pos, animal);
            animalsByID.put(animal.getID(), animal);
            return true;
        }
        return false;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }


    @Override
    public Object objectAt(Vector2d position) {
        TreeSet<Animal> s = animals.get(position);
        if (!s.isEmpty()) {
            return s.first();
        }
        return foliage.get(position);
    }

    @Override
    public void nextEpoch() {
        Debug.println(animalsByID);
        Debug.println(animals);
        ++epoch;
        statTracker.nextEpoch();
        cleanUpAll();
        moveAll();
        feedAndProcreateAll();
        growDailyFoliage();
        ageUpAll();
        updateEpochStats();
        statTracker.logEpoch();
    }

    @Override
    public int getNextAnimalID() {
        return nextAnimalID++;
    }

    @Override
    public int getCurrentEpoch() {
        return epoch;
    }

    @Override
    public Pair<Vector2d, Vector2d> getMapBounds() {
        return new Pair<>(boundaryLowerLeft, boundaryUpperRight);
    }

    @Override
    public String toString() {
        return mapVis.draw(boundaryLowerLeft, boundaryUpperRight);
    }
}
