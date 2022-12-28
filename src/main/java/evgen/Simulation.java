package evgen;

import java.util.Random;

public class Simulation implements Runnable {
    // PRIVATE ATTRIBUTES
    private final Random rng;
    private final Settings settings;
    private final IWorldMap map;
    private final int epochDelay;

    // PUBLIC METHODS
    public Simulation(Random r, Settings s, int epochDelay) {
        rng = r;
        settings = s;
        map = (s.getMapType() == Settings.MapType.GLOBE) ? new GlobeMap(r, s) : new PortalMap(r, s);

        // XXX: maybe refactor the following into AbstractWorldMap constructor?
        for (int i = 0; i < s.getStartingAnimals(); ++i) {
            map.place(new Animal(rng, settings, map));
        }

        this.epochDelay = epochDelay;
    }

    @Override
    public void run() {
        int epoch = 0;
        System.out.println(String.format("epoch %d\n%s", epoch, map));
        while (true) {
            try {
                Thread.sleep(epochDelay);
            } catch (InterruptedException e) {
                System.out.println("Simulation interrupted.");
                return;
            }

            map.nextEpoch();
            System.out.println(String.format("epoch %d\n%s", ++epoch, map));
            // if (epoch == 69) {
            //     AbstractWorldMap m = (AbstractWorldMap)map;
            //     m.setPDD();
            // }
        }
    }
}
