package evgen;

public class Plant extends AbstractMapElement {
    public Plant(Vector2d p) {
        pos = p;
    }

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public String getResource() {
        // TODO: implement when adding GUI
        return "NOT IMPLEMENTED";
    }

    @Override
    public String getLabel() {
        // TODO: implement when adding GUI
        return "NOT IMPLEMENTED";
    }
}
