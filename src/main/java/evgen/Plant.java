package evgen;

import evgen.lib.ConsoleColour;

public class Plant extends AbstractMapElement {
    public Plant(Vector2d p) {
        pos = p;
    }

    @Override
    public String toString() {
        return String.format("P%s", pos.toString());
    }

    @Override
    public String getSprite() {
        return ConsoleColour.colourise("*", ConsoleColour.Colour.GREEN);
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
