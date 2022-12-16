package evgen;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public class Animal extends AbstractWorldMapElement {
    // PRIVATE ATTRIBUTES
    private final IWorldMap map;

    private MapDirection facing;
    private int energy;
    private int age = 0;
    private int children = 0;
    private List<IPositionChangeObserver> observers = new LinkedList<>();

    // PUBLIC ATTRIBUTES
    public final int id;
    public final Genotype genes;

    // PRIVATE METHODS
    private void notifyObservers(Vector2d oldPos, Vector2d newPos) {
        for (IPositionChangeObserver o : observers) {
            o.positionChanged(id, oldPos, newPos);
        }
    }

    // PUBLIC METHODS
    public Animal(IWorldMap m, Vector2d p, Genotype g, int e) {
        map = m;
        pos = p;
        id = map.getNextAnimalID();
        facing = MapDirection.fromInt(World.rng.nextInt(8));
        energy = e;
        genes = g;
    }

    public Animal(IWorldMap m, Vector2d p, Genotype g) {
        this(m, p, g, World.settings.getStartingEnergy());
    }

    public Animal(IWorldMap m, Vector2d p) {
        this(m, p, new Genotype());
    }

    public void eat() {
        energy += World.settings.getEnergyGain();
    }

    public void loseEnergy() {
        energy -= World.settings.getProcreationEnergyLoss();
    }

    /**
     * Move an animal according to its genome.
     */
    public void move() {
        facing = facing.updateDirection(genes.nextDirection());
        Pair<Vector2d, MapDirection> p = map.attemptMove(this);
        notifyObservers(pos, p.getValue0());
        pos = p.getValue0();
        facing = p.getValue1();
    }

    /**
     * Age an animal up at the end of an epoch -- decrement energy, increment age.
     * @return true if animal is still alive
     * @return false otherwise
     */
    public boolean ageUp() {
        ++age;
        if (--energy <= 0) {
            return false;
        }
        return true;
    }

    public boolean canProcreate() {
        return energy >= World.settings.getMinProcreationEnergy();
    }

    /**
     * Create a child from this animal and another one.
     * Does not check whether animals can actually procreate.
     * @param other Animal to be the second parent
     * @return Animal -- child of this and other
     */
    public Animal procreate(Animal other) {
        final double ratio = this.energy / (this.energy + other.energy);
        Genotype childGenes = new Genotype(this.genes, other.genes, ratio);
        final int energyLoss = World.settings.getProcreationEnergyLoss();
        energy -= energyLoss;
        other.energy -= energyLoss;
        ++this.children;
        ++other.children;
        return new Animal(map, pos, childGenes, 2*energyLoss);
    }

    public final MapDirection getFacing() { return facing; }
    public final int getEnergy() { return energy; }
    public final int getChildren() { return children; }
    public final int getAge() { return age; }

    @Override
    public String toString() {
        return switch (facing) {
            case EAST -> "\u27a1";
            case WEST -> "\u2b05";
            case NORTH -> "\u2b06";
            case SOUTH -> "\u2b07";
            case NORTHWEST -> "\u2196";
            case NORTHEAST -> "\u2197";
            case SOUTHEAST -> "\u2198";
            case SOUTHWEST -> "\u2199";
        };
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

    public boolean addObserver(IPositionChangeObserver o) {
        return observers.add(o);
    }

    public boolean removeObserver(IPositionChangeObserver o) {
        return observers.remove(o);
    }
}
