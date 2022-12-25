package evgen.lib;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Set-like, indexed utility container which allows polling random elements in O(1) time.
 * Elements must be hashable.
 */
public class RandomSet<E> extends AbstractSet<E> {
    List<E> dta = new ArrayList<E>();
    Map<E, Integer> idx = new HashMap<E, Integer>();

    /**
     * Construct an empty RandomSet
     */
    public RandomSet() {}

    /**
     * Construct a RandomSet filled with items from the given Collection
     * @param items
     */
    public RandomSet(Collection<E> items) {
        for (E item : items) {
            idx.put(item, dta.size());
            dta.add(item);
        }
    }

    /**
     * Add item to set
     * @param item
     * @return false if item was already in the set, true otherwise
     */
    @Override
    public boolean add(E item) {
        if (idx.containsKey(item)) {
            return false;
        }
        idx.put(item, dta.size());
        dta.add(item);
        return true;
    }

    /**
     * Add items from given Collection to the set
     * @param items
     * @return false if any item was already in the set, true otherwise
     */
    @Override
    public boolean addAll(Collection<? extends E> items) {
        @SuppressWarnings(value = "element-type-mismatch")
        boolean rc = true;
        for (E o : items) {
            rc &= add(o);
        }
        return rc;
    }

    /**
     * Override element at position <code>id</code> with last element.
     * @param id
     * @return element which was overriden
     */
    public E removeAt(int id) {
        if (id >= dta.size()) {
            return null;
        }
        E res = dta.get(id);
        idx.remove(res);
        E last = dta.remove(dta.size() - 1);
        // skip filling the hole if last is removed
        if (id < dta.size()) {
            idx.put(last, id);
            dta.set(id, last);
        }
        return res;
    }

    /**
     * Remove item from set
     * @param item
     * @return true if item was present, false otherwise
     */
    @Override
    public boolean remove(Object item) {
        @SuppressWarnings(value = "element-type-mismatch")
        Integer id = idx.get(item);
        if (id == null) {
            return false;
        }
        removeAt(id);
        return true;
    }

    /**
     * Remove items from given Collection from the set
     * @param items
     * @return true if all items were present, false otherwise
     */
    @Override
    public boolean removeAll(Collection<?> items) {
        @SuppressWarnings(value = "element-type-mismatch")
        boolean rc = true;
        for (Object o : items) {
            rc &= remove(o);
        }
        return rc;
    }

    /**
     * Get item at index i
     * @param i
     * @return element at index i
     */
    public E get(int i) {
        return dta.get(i);
    }

    /**
     * Remove and return a random item from the set
     * @param rnd RNG to use
     * @return random set element
     */
    public E pollRandom(Random rnd) {
        if (dta.isEmpty()) {
            return null;
        }
        int id = rnd.nextInt(dta.size());
        return removeAt(id);
    }

    @Override
    public int size() {
        return dta.size();
    }

    @Override
    public Iterator<E> iterator() {
        return dta.iterator();
    }
}
