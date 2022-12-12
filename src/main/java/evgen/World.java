package evgen;

public class World {
    public static void main(String[] args) {
        Settings s = new Settings("config/sample_config.yaml");
        Genotype g = new Genotype(s);
        System.out.println("evgen");
        System.out.println(g.toString());
    }
}
