package evgen;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.javatuples.Pair;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    // PROTECTED ATTRIBUTES
    protected final Vector2d boundaryLowerLeft;
    protected final Vector2d boundaryUpperRight;

    protected final MapVisualizer mapVis = new MapVisualizer(this);

    protected int nextAnimalID = 0;
    // Can have multiple animals at same spot, but only one plant
    protected Map<Integer, Animal> animalsByID = new HashMap<>();
    protected Map<Vector2d, SortedSet<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, IMapElement> foliage = new HashMap<>();

    // XXX: Do we need to be able to add observers to a map? Perhaps adding plants can be resolved in a different way?
    protected List<IPositionChangeObserver> observers = new LinkedList<>();

    // PROTECTED METHODS
    protected AbstractWorldMap(int width, int height) {
        boundaryLowerLeft = new Vector2d(0, 0);
        boundaryUpperRight = new Vector2d(width-1, height-1);
    }

    protected void addAnimalToMap(Vector2d pos, Animal animal) {
        if (animals.containsKey(pos)) {
            animals.get(pos).add(animal);
        } else {
            SortedSet<Animal> s = new TreeSet<Animal>(new Comparator<Animal>() {
                public final int compare(Animal first, Animal second) {
                    // TODO: implement comparator - sort by: energy desc, age desc, children desc, id
                    return 0;
                }
            });
            s.add(animal);
            animals.put(pos, s);
        }
    }

    protected void notifyObservers(Vector2d oldPos, Vector2d newPos) {
        for (IPositionChangeObserver o : observers) {
            o.positionChanged(oldPos, newPos);
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

    // TODO: implement removing animals from map at their death

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

    // Do not mark as throws -- it should never throw anything, if it does,
    // something's gone terribly wrong and we need to stop the entire thing anyway
    @Override
    public void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition) {
        Animal a = animalsByID.get(entityID);
        if (a == null) {
            throw new RuntimeException(String.format("Animal with id %d does not exist", entityID));
        }
        SortedSet<Animal> s = animals.get(oldPosition);
        if (s == null) {
            throw new RuntimeException(String.format("positionChanged invoked from pos %s but no animals are present there!", oldPosition));
        }
        if (!s.remove(a)) {
            throw new RuntimeException(String.format("Animal with id %d is not present at pos %s", entityID, oldPosition));
        }
        addAnimalToMap(newPosition, a);
    }

    public void addObserver(IPositionChangeObserver o) {
        observers.add(o);
    }

    public void removeObserver(IPositionChangeObserver o) {
        observers.remove(o);
    }

    public int getNextAnimalID() {
        return nextAnimalID++;
    }

    public abstract Pair<Vector2d, MapDirection> attemptMove(Vector2d fromPos, MapDirection fromDir);
}
