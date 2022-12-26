package evgen;

public interface IFoliageGrower {
    void plantEaten(Vector2d pos);
    Vector2d getPlantSpot();
    void animalDiedAt(Vector2d pos);
    boolean isPreferred(Vector2d pos);
}
