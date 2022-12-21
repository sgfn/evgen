package evgen;

import org.javatuples.Pair;

public interface IWorldMap {
    boolean canPlaceAt(Vector2d position);
    boolean place(Animal animal);
    boolean isOccupied(Vector2d position);
    Object objectAt(Vector2d position);
    int getNextAnimalID();
    Pair<Vector2d, MapDirection> attemptMove(Animal a);
    Pair<Vector2d, Vector2d> getMapBounds();
}
