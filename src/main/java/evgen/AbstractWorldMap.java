package evgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.javatuples.Pair;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    // PROTECTED ATTRIBUTES
    protected final Vector2d boundaryLowerLeft;
    protected final Vector2d boundaryUpperRight;

    protected final Random rng;
    protected final Settings settings;
    protected final MapVisualizer mapVis = new MapVisualizer(this);
    protected final IFoliageGrower foliageGen;

    protected int nextAnimalID = 0;
    // Can have multiple animals at same spot, but only one plant
    protected Map<Integer, Animal> animalsByID = new HashMap<>();
    protected Map<Vector2d, SortedSet<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, IMapElement> foliage = new HashMap<>();

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
        growFoliage(settings.getStartingFoliage());
    }

    private void addAnimalToMap(Vector2d pos, Animal animal) {
        if (animals.containsKey(pos)) {
            animals.get(pos).add(animal);
        } else {
            SortedSet<Animal> s = new TreeSet<Animal>(new AnimalComparator());
            s.add(animal);
            animals.put(pos, s);
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

    protected void feedAnimals() {
        // XXX: maybe rethink?
        for (Vector2d spot : animals.keySet()) {
            if (animals.get(spot).size() > 0 && foliage.get(spot) != null) {
                animals.get(spot).first().eat();
                foliage.remove(spot);
                foliageGen.plantEaten(spot);
            }
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
        final Vector2d pos = animal.getPosition();
        if (canPlaceAt(pos)) {
            addAnimalToMap(pos, animal);
            animalsByID.put(animal.getID(), animal);
            animal.addObserver(this);
            return true;
        }
        return false;
    }

    // TODO: implement removing animals from map at their death -- will probably be handled by simulation engine
    // XXX: make sure to inform ToxicCorpsesGrower about death of animals!

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
        SortedSet<Animal> s = animals.get(position);
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
        SortedSet<Animal> s = animals.get(oldPosition);
        assert s != null : String.format("positionChanged invoked from pos %s but no animals are present there!", oldPosition);
        boolean rc = s.remove(a);
        assert rc : String.format("Animal with id %d is not present at pos %s", entityID, oldPosition);
        addAnimalToMap(newPosition, a);
    }

    /**
     * Get unique ID for a new animal to be added to the map
     * @return unique ID for the new animal
     */
    public int getNextAnimalID() {
        return nextAnimalID++;
    }

    /**
     * Get target position and direction of an animal who is about to move,
     * according to its genome and map constraints
     * @param a animal to move
     * @return pair containing target position and direction, in that order
     */
    public abstract Pair<Vector2d, MapDirection> attemptMove(Animal a);

    public Pair<Vector2d, Vector2d> getMapBounds() {
        return new Pair<>(boundaryLowerLeft, boundaryUpperRight);
    }
}
