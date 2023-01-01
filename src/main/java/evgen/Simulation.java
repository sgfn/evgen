package evgen;

import java.util.Random;

public class Simulation implements Runnable {
    // PRIVATE ATTRIBUTES
    private final Random rng;
    private final Settings settings;
    public final IWorldMap map;
    private final int epochDelay;
    private final StatTracker statTracker;

    // PUBLIC METHODS
    public Simulation(Random r, Settings s, StatTracker statTracker, int epochDelay) {
        rng = r;
        settings = s;
        this.statTracker = statTracker;
        map = (s.getMapType() == Settings.MapType.GLOBE) ? new GlobeMap(r, s, statTracker) : new PortalMap(r, s, statTracker);

        for (int i = 0; i < s.getStartingAnimals(); ++i) {
            boolean rc = map.place(new Animal(rng, settings, map, statTracker));
            assert rc;
        }

        this.epochDelay = epochDelay;
    }

    @Override
    public void run() {
        int epoch = 0;
        System.out.println(String.format("epoch %d\n%s", epoch, map));
        statTracker.importSettings(settings);
        while (true) {
            try {
                Thread.sleep(epochDelay);
            } catch (InterruptedException e) {
                System.out.println("Simulation interrupted.");
                return;
            }
            map.nextEpoch();
//            System.out.println(String.format("epoch %d\n%s", ++epoch, map));
        }
    }
}
