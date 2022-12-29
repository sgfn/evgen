package evgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import evgen.lib.Pair;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    // PROTECTED ATTRIBUTES
    protected final Vector2d boundaryLowerLeft;
    protected final Vector2d boundaryUpperRight;

    protected final Random rng;
    protected final Settings settings;
    protected final MapVisualizer mapVis;
    protected final IFoliageGrower foliageGen;

    protected int nextAnimalID = 0;
    // Can have multiple animals at same spot, but only one plant
    protected Map<Integer, Animal> animalsByID = new HashMap<>();
    protected Map<Vector2d, TreeSet<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, IMapElement> foliage = new HashMap<>();

    protected List<Animal> markedForDelete = new LinkedList<>();

    // PRIVATE METHODS
    private AbstractWorldMap(Random r, Settings s, IFoliageGrower f, boolean useDefaultGrowers) {
        rng = r;
        settings = s;
        boundaryLowerLeft = new Vector2d(0, 0);
        boundaryUpperRight = new Vector2d(settings.getMapWidth()-1, settings.getMapHeight()-1);
        if (useDefaultGrowers) {
            foliageGen = (s.getFoliageGrowthType() == Settings.FoliageGrowthType.EQUATOR) ? new EquatorialGrower(this) : new ToxicCorpsesGrower(this);
        } else {
            foliageGen = f;
        }
        mapVis = new MapVisualizer(this, foliageGen);
        growFoliage(settings.getStartingFoliage());
    }

    private void addAnimalToMap(Vector2d pos, Animal animal) {
        if (animals.containsKey(pos)) {
            boolean rc = animals.get(pos).add(animal);
            assert rc;
        } else {
            TreeSet<Animal> s = new TreeSet<>();
            boolean rc = s.add(animal);
            assert rc;
            var x = animals.put(pos, s);
            assert x == null;
        }
        assert animals.containsKey(pos);
        assert animals.get(pos).contains(animal);
    }

    private void removeAnimalFromMap(Animal animal) {
        final Vector2d pos = animal.getPosition();
        assert animals.containsKey(pos);
        assert animals.get(pos).contains(animal);
        final boolean rc = animals.get(pos).remove(animal);
        assert rc;
        assert !animals.get(pos).contains(animal);
    }

    private void markForDelete(Animal a) {
        markedForDelete.add(a);
    }

    private void deleteMarked() {
        for (Animal a : markedForDelete) {
            Debug.println((String.format("Animal with id %d dies", a.getID())));
            removeAnimalFromMap(a);
            Object o = animalsByID.remove(a.getID());
            assert o.equals(a);
            foliageGen.animalDiedAt(a.getPosition());
            a.removeObserver(this);
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

    private void moveAll() {
        for (Animal a : animalsByID.values()) {
            assert a.isAlive();
            assert animals.get(a.getPosition()).contains(a) : String.format("moveAll detected desync between animal pos and set, a=%s, s=%s", a, animals.get(a.getPosition()));
            a.move();
            // PortalMap: teleportation may kill the animal -- XXX: export only to portalmap
            if (!a.isAlive()) {
                markForDelete(a);
            }
        }
        deleteMarked();
    }

    private void feedAndProcreateAll() {
        for (Vector2d spot : animals.keySet()) {
            // Feed strongest from spot -- this will never change the ordering
            if (animals.get(spot).size() > 0 && foliage.get(spot) != null) {
                Animal strongest = animals.get(spot).pollFirst();
                strongest.eat();
                animals.get(spot).add(strongest);
                foliage.remove(spot);
                foliageGen.plantEaten(spot);
            }
            // Allow procreation of two strongest from spot
            if (animals.get(spot).size() > 1) {
                Animal first = animals.get(spot).pollFirst();
                Animal second = animals.get(spot).pollFirst();
                // assert first == animals.get(spot).first() : "First from iterator is not first from set";
                
                // Second one cannot have more energy than first
                assert first.getEnergy() >= second.getEnergy() : "Second has more energy than first" + String.format("c=%s s=%s m.p1.e=%d m.p2.e=%d ", spot.toString(), animals.get(spot), first.getEnergy(), second.getEnergy());
                if (second.canProcreate()) {
                    Debug.print(String.format("procreation c=%s s=%s ", spot.toString(), animals.get(spot)));

                    Animal child = first.procreate(second);
                    // Update positions of both parents in the set
                    animals.get(spot).add(first);
                    animals.get(spot).add(second);

                    assert animals.get(spot).contains(first) : "Assertion after update for first one failed";
                    assert animals.get(spot).contains(second) : "Assertion after update for second one failed";

                    place(child);

                    assert animals.get(spot).contains(first) : "Assertion after child placement for first one failed";
                    assert animals.get(spot).contains(second) : "Assertion after child placement for second one failed";
                    assert animals.get(spot).contains(child) : "Assertion after child placement for child failed";

                    Debug.println(String.format("new_s=%s", animals.get(spot)));
                } else {
                    animals.get(spot).add(first);
                    animals.get(spot).add(second);

                    assert animals.get(spot).contains(first) : "Assertion after update for first one failed";
                    assert animals.get(spot).contains(second) : "Assertion after update for second one failed";
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
    protected AbstractWorldMap(Random r, Settings s, IFoliageGrower f) {
        this(r, s, f, false);
    }

    protected AbstractWorldMap(Random r, Settings s) {
        this(r, s, null, true);
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

    // PUBLIC METHODS
    /**
     * Check whether a given position is in map bounds
     * @param position position to check
     * @return true if position is in bounds, false otherwise
     */
    @Override
    public boolean canPlaceAt(Vector2d position) {
        return boundaryLowerLeft.precedes(position) && boundaryUpperRight.follows(position);
    }

    /**
     * Attempt to place an animal on the map, at its position
     * @param animal Animal to place on the map
     * @return true if the animal was placed successfully, false otherwise
     */
    @Override
    public boolean place(Animal animal) {
        if (animal.getID() == 17) animal.pdb = true;
        final Vector2d pos = animal.getPosition();
        if (canPlaceAt(pos)) {
            addAnimalToMap(pos, animal);
            animalsByID.put(animal.getID(), animal);
            animal.addObserver(this);
            return true;
        }
        return false;
    }

    /**
     * Check whether a given spot on the map is occupied
     * @param position position to check
     * @return true if position is occupied, false otherwise
     */
    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    /**
     * Get object present at given spot on the map.
     * If animals are present at the spot, will return the first one (sorted according to the inner comparator).
     * If no animals are present, will return the plant at the spot.
     * If spot does not have a plant, will return null
     * @param position position to get object from
     * @return object at given position
     */
    @Override
    public Object objectAt(Vector2d position) {
        TreeSet<Animal> s = animals.get(position);
        if (s != null && !s.isEmpty()) {
            return s.first();
        }
        return foliage.get(position);
    }

    @Override
    public String toString() {
        return mapVis.draw(boundaryLowerLeft, boundaryUpperRight);
    }

    /**
     * Handle position changed event of a certain animal present on the map.
     * @param entityID ID of the animal that has moved
     * @param oldPosition previous position of the animal
     * @param newPosition current position of the animal
     */
    @Override
    public void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition) {
        final Animal a = animalsByID.get(entityID);
        assert a != null : String.format("Animal with id %d does not exist", entityID);
        // TreeSet<Animal> s = animals.get(oldPosition);
        assert animals.get(oldPosition) != null : String.format("positionChanged invoked from pos %s but no animals are present there!", oldPosition);
        assert animals.get(oldPosition).contains(a) : String.format("Animal with id %d is not present at pos %s", entityID, oldPosition);
        boolean rc = animals.get(oldPosition).remove(a);
        assert rc;
        addAnimalToMap(newPosition, a);
    }

    /**
     * Get unique ID for a new animal to be added to the map
     * @return unique ID for the new animal
     */
    @Override
    public int getNextAnimalID() {
        return nextAnimalID++;
    }

    /**
     * Get target position and direction of an animal who is about to move,
     * according to its genome and map constraints
     * @param a animal to move
     * @return pair containing target position and direction, in that order
     */
    @Override
    public abstract Pair<Vector2d, MapDirection> attemptMove(Animal a);

    /**
     * Get map boundaries
     * @return pair containing map lower-left and upper-right boundary, in that order
     */
    @Override
    public Pair<Vector2d, Vector2d> getMapBounds() {
        return new Pair<>(boundaryLowerLeft, boundaryUpperRight);
    }

    /**
     * Advance to next epoch -- age animals up, move them, feed them,
     * allow procreation, grow foliage
     */
    @Override
    public void nextEpoch() {
        Debug.println(animalsByID);
        Debug.println(animals);
        cleanUpAll();
        moveAll();
        feedAndProcreateAll();
        growDailyFoliage();
        ageUpAll();
    }

    public void printDebugData() {
        Animal a = animalsByID.get(74);
        Animal b = animalsByID.get(5);
        TreeSet<Animal> s = animals.get(new Vector2d(9, 13));
        Debug.println(a.toString() + s.contains(a));
        Debug.println(b.toString() + s.contains(b));
        for (var elem : s) {
            Debug.println(elem.toString() +" "+ elem.compareTo(a) +" "+ elem.compareTo(b));
        }
        Debug.println("");
    }
}
