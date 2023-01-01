package evgen;

import evgen.lib.Pair;

public interface IWorldMap {
    /**
     * Check whether a given position is in map bounds
     * @param position position to check
     * @return true if position is in bounds, false otherwise
     */
    boolean canPlaceAt(Vector2d position);

    /**
     * Attempt to place an animal on the map, at its position
     * @param animal Animal to place on the map
     * @return true if the animal was placed successfully, false otherwise
     */
    boolean place(Animal animal);

    /**
     * Check whether a given spot on the map is occupied
     * @param position position to check
     * @return true if position is occupied, false otherwise
     */
    boolean isOccupied(Vector2d position);

    /**
     * Get object present at given spot on the map.
     * If animals are present at the spot, will return the first one (sorted according to the inner comparator).
     * If no animals are present, will return the plant at the spot.
     * If spot does not have a plant, will return null
     * @param position position to get object from
     * @return object at given position
     */
    Object objectAt(Vector2d position);

    /**
     * Get map boundaries
     * @return pair containing map lower-left and upper-right boundary, in that order
     */
    Pair<Vector2d, Vector2d> getMapBounds();

    /**
     * Advance to next epoch -- clean up dead bodies, move animals, feed them,
     * allow procreation, grow foliage, age animals up
     */
    void nextEpoch();

    /**
     * Get unique ID for a new animal to be added to the map
     * @return unique ID for the new animal
     */
    int getNextAnimalID();

    /**
     * Get current value of epoch counter
     * @return current epoch
     */
    int getCurrentEpoch();

    /**
     * Get info if spot is preferred
     * @return boolean if spot is preferred
     */
    boolean isPreferred(Vector2d position);
}
