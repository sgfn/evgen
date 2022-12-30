package evgen;

public interface IPositionChangeObserver {
    /**
     * Handle position changed event of a certain animal present on the map.
     * @param entityID ID of the animal that has moved
     * @param oldPosition previous position of the animal
     * @param newPosition current position of the animal
     */
    void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition);
}
