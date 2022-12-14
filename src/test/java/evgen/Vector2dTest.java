package evgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Vector2dTest {
    private static final Vector2d zeroVector = new Vector2d(0, 0);

    @Test
    void testVector2dEquals() {
        assertTrue(zeroVector.equals(zeroVector));
        assertFalse(zeroVector.equals(new Object()));
        assertFalse(zeroVector.equals(null));

        assertTrue(zeroVector.equals(new Vector2d(0, 0)));
        assertFalse(zeroVector.equals(new Vector2d(9001, -5)));
        assertFalse(zeroVector.equals(new Vector2d(0, 1)));
        assertFalse(zeroVector.equals(new Vector2d(-1, 0)));
    }

    @Test
    void testVector2dToString() {
        assertEquals(zeroVector.toString(), "(0,0)");
        assertEquals(new Vector2d(1, 5).toString(), "(1,5)");
        assertEquals(new Vector2d(-9001, -12345).toString(), "(-9001,-12345)");
    }

    @Test
    void testVector2dPrecedes() {
        assertFalse(zeroVector.precedes(new Vector2d(-1, -3)));
        assertFalse(zeroVector.precedes(new Vector2d(-1, 0)));
        assertFalse(zeroVector.precedes(new Vector2d(0, -300)));
        assertTrue(zeroVector.precedes(new Vector2d(0, 0)));
        assertTrue(zeroVector.precedes(new Vector2d(5, 0)));
        assertTrue(zeroVector.precedes(new Vector2d(0, 191919)));
        assertTrue(zeroVector.precedes(new Vector2d(123, 321)));
    }

    @Test
    void testVector2dFollows() {
        assertTrue(zeroVector.follows(new Vector2d(-1, -3)));
        assertTrue(zeroVector.follows(new Vector2d(-1, 0)));
        assertTrue(zeroVector.follows(new Vector2d(0, -300)));
        assertTrue(zeroVector.follows(new Vector2d(0, 0)));
        assertFalse(zeroVector.follows(new Vector2d(5, 0)));
        assertFalse(zeroVector.follows(new Vector2d(0, 191919)));
        assertFalse(zeroVector.follows(new Vector2d(123, 321)));
    }

    @Test
    void testVector2dUpperRight() {
        assertEquals(zeroVector.upperRight(zeroVector), zeroVector);
        assertEquals(zeroVector.upperRight(new Vector2d(-15, -92)), zeroVector);
        assertEquals(zeroVector.upperRight(new Vector2d(-127, 66)), new Vector2d(0, 66));
        assertEquals(zeroVector.upperRight(new Vector2d(5, -2)), new Vector2d(5, 0));
        assertEquals(zeroVector.upperRight(new Vector2d(5, 2)), new Vector2d(5, 2));
    }

    @Test
    void testVector2dLowerLeft() {
        assertEquals(zeroVector.lowerLeft(zeroVector), zeroVector);
        assertEquals(zeroVector.lowerLeft(new Vector2d(-15, -92)), new Vector2d(-15, -92));
        assertEquals(zeroVector.lowerLeft(new Vector2d(-127, 66)), new Vector2d(-127, 0));
        assertEquals(zeroVector.lowerLeft(new Vector2d(5, -2)), new Vector2d(0, -2));
        assertEquals(zeroVector.lowerLeft(new Vector2d(5, 2)), zeroVector);
    }

    @Test
    void testVector2dAdd() {
        assertEquals(zeroVector.add(zeroVector), zeroVector);
        assertEquals(zeroVector.add(new Vector2d(15, 92)), new Vector2d(15, 92));
        assertEquals(zeroVector.add(new Vector2d(-127, 66)), new Vector2d(-127, 66));
        assertEquals(new Vector2d(1, 2).add(new Vector2d(-5, 6)), new Vector2d(-4, 8));
    }

    @Test
    void testVector2dSubtract() {
        assertEquals(zeroVector.subtract(zeroVector), zeroVector);
        assertEquals(zeroVector.subtract(new Vector2d(15, 92)), new Vector2d(-15, -92));
        assertEquals(zeroVector.subtract(new Vector2d(-127, 66)), new Vector2d(127, -66));
        assertEquals(new Vector2d(1, 2).subtract(new Vector2d(-5, 6)), new Vector2d(6, -4));
    }

    @Test
    void testVector2dOpposite() {
        assertEquals(zeroVector.opposite(), zeroVector);
        assertEquals(new Vector2d(15, 92).opposite(), new Vector2d(-15, -92));
        assertEquals(new Vector2d(-127, 66).opposite(), new Vector2d(127, -66));
    }
}
