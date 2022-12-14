package evgen;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Vector2d newPosition);
    void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition);
}
