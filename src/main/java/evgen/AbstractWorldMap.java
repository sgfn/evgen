package evgen;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.javatuples.Pair;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    // PROTECTED ATTRIBUTES
    protected final Vector2d boundaryLowerLeft;
    protected final Vector2d boundaryUpperRight;

    protected final MapVisualizer mapVis = new MapVisualizer(this);
    protected final IFoliageGrower foliageGen;

    protected int nextAnimalID = 0;
    // Can have multiple animals at same spot, but only one plant
    protected Map<Integer, Animal> animalsByID = new HashMap<>();
    protected Map<Vector2d, SortedSet<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, IMapElement> foliage = new HashMap<>();

    // PROTECTED METHODS
    protected AbstractWorldMap() {
        boundaryLowerLeft = new Vector2d(0, 0);
        boundaryUpperRight = new Vector2d(World.settings.getMapWidth()-1, World.settings.getMapHeight()-1);
        foliageGen = (World.settings.getFoliageGrowthType() == Settings.FoliageGrowthType.EQUATOR) ? new EquatorialGrower(this) : new ToxicCorpsesGrower(this);
        growFoliage(World.settings.getStartingFoliage());
    }

    protected void addAnimalToMap(Vector2d pos, Animal animal) {
        if (animals.containsKey(pos)) {
            animals.get(pos).add(animal);
        } else {
            SortedSet<Animal> s = new TreeSet<Animal>(new Comparator<Animal>() {
                // TODO: make sure the animals are stored in the correct order!
                public final int compare(Animal first, Animal second) {
                    int res = first.getEnergy() - second.getEnergy();
                    if (res == 0) {
                        res = first.getAge() - second.getAge();
                    }
                    if (res == 0) {
                        res = first.getChildren() - second.getChildren();
                    }
                    if (res == 0) {
                        res = first.id - second.id;
                    }
                    return res;
                }
            });
            s.add(animal);
            animals.put(pos, s);
        }
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
    @Override
    public boolean canPlaceAt(Vector2d position) {
        return boundaryLowerLeft.precedes(position) && boundaryUpperRight.follows(position);
    }

    @Override
    public boolean place(Animal animal) {
        final Vector2d pos = animal.getPosition();
        if (canPlaceAt(pos)) {
            addAnimalToMap(pos, animal);
            animalsByID.put(animal.id, animal);
            animal.addObserver(this);
            return true;
        }
        return false;
    }

    // TODO: implement removing animals from map at their death -- will probably be handled by simulation engine
    // XXX: make sure to inform ToxicCorpsesGrower about death of animals!

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

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

    public int getNextAnimalID() {
        return nextAnimalID++;
    }

    public abstract Pair<Vector2d, MapDirection> attemptMove(Animal a);

    public Pair<Vector2d, Vector2d> getMapBounds() {
        return new Pair<>(boundaryLowerLeft, boundaryUpperRight);
    }
}
