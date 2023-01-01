package evgen;

public interface IFoliageGrower {
    /**
     * Get position of next plant to grow
     * @return position, null if no spots available
     */
    Vector2d getPlantSpot();

    /**
     * Inform the foliage generator that a plant at a given spot has been eaten
     * @param pos
     */
    void plantEaten(Vector2d pos);

    /**
     * Inform the foliage generator that an animal has died at a given spot
     * @param pos
     */
    void animalDiedAt(Vector2d pos);

    /**
     * Check whether a given spot is preferred (80% chance for plants to grow there)
     * @param pos
     * @return true if spot is preferred, false otherwise
     */
    boolean isPreferred(Vector2d pos);
    int getFreeSpotsCount();
}
