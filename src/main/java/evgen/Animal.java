package evgen;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import evgen.lib.ConsoleColour;
import evgen.lib.Pair;

public class Animal extends AbstractMapElement {
    // PRIVATE ATTRIBUTES
    private final IWorldMap map;
    private final Random rng;
    private final Settings settings;

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
    public Animal(Random r, Settings s, IWorldMap m, Vector2d p, Genotype g, int e) {
        rng = r;
        settings = s;
        map = m;
        pos = p;
        id = map.getNextAnimalID();
        facing = MapDirection.fromInt(rng.nextInt(MapDirection.directionCount));
        energy = e;
        genes = g;
    }

    public Animal(Random r, Settings s, IWorldMap m, Vector2d p, Genotype g) {
        this(r, s, m, p, g, s.getStartingEnergy());
    }

    public Animal(Random r, Settings s, IWorldMap m, Vector2d p) {
        this(r, s, m, p, new Genotype(r, s, World.indexGen));
    }

    public Animal(Random r, Settings s, IWorldMap m) {
        this(r, s, m, new Vector2d(r.nextInt(m.getMapBounds().first.x, m.getMapBounds().second.x + 1),
                                   r.nextInt(m.getMapBounds().first.y, m.getMapBounds().second.y + 1)));
    }

    public Animal(IWorldMap m, Vector2d p) {
        this(World.rng, World.settings, m, p);
    }

    /**
     * Gain energy as if from eating.
     */
    public void eat() {
        energy += settings.getEnergyGain();
    }

    /**
     * Lose energy as if by procreation.
     */
    public void loseEnergy() {
        energy -= settings.getProcreationEnergyLoss();
    }

    /**
     * Move an animal according to its genome.
     */
    public void move() {
        facing = facing.updateDirection(genes.nextDirection());
        Pair<Vector2d, MapDirection> p = map.attemptMove(this);
        notifyObservers(pos, p.first);
        pos = p.first;
        facing = p.second;
    }

    /**
     * Age an animal up at the end of an epoch -- decrement energy, increment age.
     * @return true if animal is still alive, false otherwise
     */
    public boolean ageUp() {
        ++age;
        return --energy > 0;
    }

    /**
     * Check whether the animal has enough energy to procreate.
     */
    public boolean canProcreate() {
        return energy >= settings.getMinProcreationEnergy();
    }

    /**
     * Create a child from this animal and another one.
     * Does not check whether animals can actually procreate.
     * @param other Animal to be the second parent
     * @return Animal -- child of this and other
     */
    public Animal procreate(Animal other) {
        final double ratio = (double)(this.energy) / (this.energy + other.energy);
        Genotype childGenes = new Genotype(this.genes, other.genes, ratio);
        final int energyLoss = settings.getProcreationEnergyLoss();
        energy -= energyLoss;
        other.energy -= energyLoss;
        ++this.children;
        ++other.children;
        return new Animal(rng, settings, map, pos, childGenes, 2*energyLoss);
    }

    public MapDirection getFacing() { return facing; }
    public int getEnergy() { return energy; }
    public int getChildren() { return children; }
    public int getAge() { return age; }
    public int getID() { return id; }

    @Override
    public String toString() {
        String s = switch (facing) {
            case WEST -> "\u2190";
            case NORTH -> "\u2191";
            case SOUTH -> "\u2192";
            case EAST -> "\u2193";
            case NORTHWEST -> "\u2196";
            case NORTHEAST -> "\u2197";
            case SOUTHEAST -> "\u2198";
            case SOUTHWEST -> "\u2199";
        };
        return ConsoleColour.colourise(s, ConsoleColour.Colour.CYAN);
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
