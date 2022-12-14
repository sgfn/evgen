package evgen;

import java.util.LinkedList;
import java.util.List;

// TODO: finish implementation of Animal stub - should have energy, position, direction etc.
public class Animal extends AbstractWorldMapElement {
    // PRIVATE ATTRIBUTES
    private final IWorldMap map;
    
    private MapDirection facing;
    private List<IPositionChangeObserver> observers = new LinkedList<>();

    // PUBLIC ATTRIBUTES
    public final int id;

    // PRIVATE METHODS
    private void notifyObservers(Vector2d oldPos, Vector2d newPos) {
        for (IPositionChangeObserver o : observers) {
            o.positionChanged(oldPos, newPos);
        }
    }

    // PUBLIC METHODS
    public Animal(IWorldMap m, Vector2d p) {
        map = m;
        pos = p;
        id = map.getNextAnimalID();
    }

    @Override
    public String toString() {
        return "NOT IMPLEMENTED";
    }

    @Override
    public String getResource() {
        return "NOT IMPLEMENTED";
    }

    @Override
    public String getLabel() {
        return "NOT IMPLEMENTED";
    }

    public boolean addObserver(IPositionChangeObserver o) {
        return observers.add(o);
    }

    public boolean removeObserver(IPositionChangeObserver o) {
        return observers.remove(o);
    }
}
