package evgen;

public interface IMapElement {
    Vector2d getPosition();
    boolean isAt(Vector2d position);
    String getResource();
    String getLabel();
    String getSprite();
}
