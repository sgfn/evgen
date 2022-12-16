package evgen;

public class World {
    public static void printCurrent(IWorldMap m, Animal a) {
        System.out.println(String.format("Animal at pos %s, facing %s", a.getPosition(), a.getFacing()));
        System.out.println(String.format("GENES: %s (next at index %d)", a.genes, a.genes.getNextGeneIndex()));
        System.out.println(m);
    }

    public static void main(String[] args) {
        Settings s = new Settings("config/sample_config.yaml");
        IWorldMap m = new GlobeMap(s);
        Animal a = new Animal(s, m, new Vector2d(3, 5));
        m.place(a);
        System.out.println("evgen");
        printCurrent(m, a);
        for (int i=0; i<5; ++i) {
            System.out.println(String.format("MOVE %d", i));
            a.move();
            printCurrent(m, a);
        }
    }
}
