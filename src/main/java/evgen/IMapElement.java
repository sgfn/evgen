package evgen;

public interface IMapElement {
    /**
     * Get current position of element
     * @return position
     */
    Vector2d getPosition();

    /**
     * Check whether the element is at the given position
     * @param position
     * @return true if element present at given position, false otherwise
     */
    boolean isAt(Vector2d position);

    /**
     * Get path of map element resource to display in the GUI
     * @return path
     */
    String getResource();

    /**
     * Get label of map element to display in the GUI
     * @return label
     */
    String getLabel();

    /**
     * Get textual representation of map element to display in the TUI
     * @return sprite
     */
    String getSprite();
}
