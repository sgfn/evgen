package evgen;

public interface IPositionChangeObserver {
    // XXX: Consider deleting the following function from the interface
    void positionChanged(Vector2d oldPosition, Vector2d newPosition);
    void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition);
}
