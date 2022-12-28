package evgen;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import evgen.lib.ConsoleColour;
import evgen.lib.Pair;

public class Animal extends AbstractMapElement implements Comparable<Animal> {
    // PRIVATE ATTRIBUTES
    private final IWorldMap map;
    private final Random rng;
    private final Settings settings;

    private MapDirection facing;
    private int energy;
    private int age = 0;
    private int children = 0;
    private List<IPositionChangeObserver> observers = new LinkedList<>();

    public boolean pdb = false;

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
        if(pdb) System.out.println("animal eating");
        energy += settings.getEnergyGain();
    }

    /**
     * Lose energy as if by procreation.
     */
    public void loseEnergy() {
        if(pdb) System.out.println("animal losing energy");
        energy -= settings.getProcreationEnergyLoss();
    }

    /**
     * Move an animal according to its genome.
     */
    public void move() {
        facing = facing.updateDirection(genes.nextDirection());
        Pair<Vector2d, MapDirection> p = map.attemptMove(this);
        Vector2d oldPos = pos;
        pos = p.first;
        facing = p.second;
        if(pdb) System.out.println(String.format("animal moving from %s to %s", oldPos, pos));
        notifyObservers(oldPos, pos);
    }

    /**
     * Check whether the animal is alive.
     * @return true if animal is still alive, false otherwise
     */
    public boolean isAlive() {
        if(pdb) System.out.println(String.format("animal checking whether alive: %s", energy > 0));
        return energy > 0;
    }

    /**
     * Age an animal up at the end of an epoch -- decrement energy, increment age.
     * @return true if animal is still alive, false otherwise
     */
    public boolean ageUp() {
        ++age;
        --energy;
        if(pdb) System.out.println(String.format("animal aging up: age=%d energy=%d", age, energy));
        return isAlive();
    }

    /**
     * Check whether the animal has enough energy to procreate.
     */
    public boolean canProcreate() {
        if(pdb) System.out.println(String.format("animal checking whether can procreate: %s", energy >= settings.getMinProcreationEnergy()));
        return energy >= settings.getMinProcreationEnergy();
    }

    /**
     * Create a child from this animal and another one.
     * Does not check whether animals can actually procreate.
     * @param other Animal to be the second parent
     * @return Animal -- child of this and other
     */
    public Animal procreate(Animal other) {
        if(pdb) System.out.println(String.format("animal initiating procreation %s", "."));
        System.out.print(String.format("p1.e=%d p2.e=%d ", this.energy, other.energy));
        final double ratio = (double)(this.energy) / (this.energy + other.energy);
        assert ratio > 0 && ratio < 1;
        Genotype childGenes = new Genotype(this.genes, other.genes, ratio);
        this.loseEnergy();
        other.loseEnergy();
        ++this.children;
        ++other.children;
        return new Animal(rng, settings, map, pos, childGenes, 2*settings.getProcreationEnergyLoss());
    }

    public MapDirection getFacing() { return facing; }
    public int getEnergy() { return energy; }
    public int getChildren() { return children; }
    public int getAge() { return age; }
    public int getID() { return id; }

    @Override
    public String toString() {
        return String.format("A%d.p%s.f%s.e%d.%dy.%dc", id, pos, facing, energy, age, children);
    }

    @Override
    public String getSprite() {
        String s = switch (facing) {
            case WEST -> "\u2190";
            case NORTH -> "\u2191";
            case EAST -> "\u2192";
            case SOUTH -> "\u2193";
            case NORTHWEST -> "\u2196";
            case NORTHEAST -> "\u2197";
            case SOUTHEAST -> "\u2198";
            case SOUTHWEST -> "\u2199";
        };
        return ConsoleColour.colourise(s, ConsoleColour.Colour.CYAN);
    }

    @Override
    public int compareTo(Animal other) {
        if (this.id == other.id) {
            return 0;
        }

        int res = this.energy - other.energy;
        if (res == 0) {
            res = this.age - other.age;
        }
        if (res == 0) {
            res = this.children - other.children;
        }
        if (res == 0) {
            res = this.id - other.id;
        }
        return -res;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Animal) && (this.compareTo((Animal) o) == 0);
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
