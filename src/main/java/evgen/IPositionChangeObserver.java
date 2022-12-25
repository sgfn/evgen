package evgen;

public interface IPositionChangeObserver {
    void positionChanged(int entityID, Vector2d oldPosition, Vector2d newPosition);
}
