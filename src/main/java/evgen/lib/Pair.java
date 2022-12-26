package evgen.lib;

import java.util.Objects;

/**
 * Tuple-like container holding two objects of arbitrary types.
 */
public class Pair<E, F> {
    public final E first;
    public final F second;

    /**
     * Create a pair from two objects
     * @param first
     * @param second
     */
    public Pair(E first, F second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Check if two pairs are equal
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> that = (Pair<?, ?>) other;
        return this.first.equals(that.first) && this.second.equals(that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }
}
