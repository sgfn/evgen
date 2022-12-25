package evgen;

import java.util.Comparator;

public class AnimalComparator implements Comparator<Animal> {
    public final int compare(Animal first, Animal second) {
        int res = first.getEnergy() - second.getEnergy();
        if (res == 0) {
            res = first.getAge() - second.getAge();
        }
        if (res == 0) {
            res = first.getChildren() - second.getChildren();
        }
        if (res == 0) {
            res = first.getID() - second.getID();
        }
        return -res;
    }
}
