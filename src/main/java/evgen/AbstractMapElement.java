package evgen;

public abstract class AbstractMapElement implements IMapElement {
    protected Vector2d pos;

    @Override
    public Vector2d getPosition() {
        return pos;
    }

    @Override
    public boolean isAt(Vector2d position) {
        return pos.equals(position);
    }
}
