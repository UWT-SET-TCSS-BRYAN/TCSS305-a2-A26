package edu.uw.tcss.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the Car class.
 *
 * <p>This test class demonstrates how to test DIRECTIONAL PREFERENCE and LIGHT behavior.
 * Unlike Truck (which is random), Car has deterministic movement: it prefers to go
 * straight, then left, then right, and only reverses as a last resort. Car also
 * has specific light-stopping behavior for traffic lights and crosswalks.
 *
 * @author TCSS 305 Instructors
 * @version Winter 2026
 */
class CarTest {

    /** Expected mass for a Car. */
    private static final int CAR_MASS = 50;

    /** Expected disabled duration for a Car. */
    private static final int CAR_DISABLED_DURATION = 15;

    /**
     * Test that the Car constructor correctly initializes position and direction.
     */
    @Test
    void testConstructorInitializesFieldsCorrectly() {
        final Car car = new Car(15, 25, Direction.EAST);

        assertAll("Constructor should initialize all fields correctly",
            () -> assertEquals(15, car.getX(), "X coordinate should be initialized to 15"),
            () -> assertEquals(25, car.getY(), "Y coordinate should be initialized to 25"),
            () -> assertEquals(Direction.EAST, car.getDirection(),
                             "Direction should be initialized to EAST")
        );
    }

    /**
     * Test that getDisabledDuration returns the correct value for Car.
     */
    @Test
    void testGetDisabledDuration() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertEquals(CAR_DISABLED_DURATION, car.getDisabledDuration(),
                     "Car disabled duration should be " + CAR_DISABLED_DURATION);
    }

    /**
     * Test that getMass returns the correct value for Car.
     */
    @Test
    void testGetMass() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertEquals(CAR_MASS, car.getMass(),
                     "Car mass should be " + CAR_MASS);
    }

    /**
     * Test that Car can pass through LIGHT terrain with GREEN and YELLOW lights,
     * but stops for RED lights.
     *
     * <p>This is a key behavioral difference between Car and Truck.
     */
    @Test
    void testCanPassLightWithAllLights() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should pass LIGHT with GREEN and YELLOW, but not RED",
            () -> assertTrue(car.canPass(Terrain.LIGHT, Light.GREEN),
                           "Car should pass LIGHT with GREEN light"),
            () -> assertTrue(car.canPass(Terrain.LIGHT, Light.YELLOW),
                           "Car should pass LIGHT with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.LIGHT, Light.RED),
                            "Car should NOT pass LIGHT with RED light (stops at red lights)")
        );
    }

    /**
     * Test that Car can pass through CROSSWALK terrain only with GREEN light.
     * Car stops for both RED and YELLOW crosswalk lights.
     *
     * <p>This is stricter than traffic light behavior.
     */
    @Test
    void testCanPassCrosswalkWithAllLights() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should pass CROSSWALK only with GREEN light",
            () -> assertTrue(car.canPass(Terrain.CROSSWALK, Light.GREEN),
                           "Car should pass CROSSWALK with GREEN light"),
            () -> assertFalse(car.canPass(Terrain.CROSSWALK, Light.YELLOW),
                            "Car should NOT pass CROSSWALK with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.CROSSWALK, Light.RED),
                            "Car should NOT pass CROSSWALK with RED light")
        );
    }

    // TODO: Add tests for STREET terrain with all lights

    // TODO: Add tests for invalid terrain (GRASS, WALL, TRAIL)

    /**
     * Test that chooseDirection prefers to go straight when possible.
     *
     * <p>Car has deterministic preference: Straight → Left → Right → Reverse.
     * When facing NORTH with STREET ahead, it should always choose NORTH.
     */
    @Test
    void testChooseDirectionPrefersStraight() {
        final Car car = new Car(0, 0, Direction.NORTH);

        // Set up neighbors: STREET ahead, left, and right (all valid)
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight)
            Terrain.STREET,  // west (left)
            Terrain.WALL,    // south (reverse - not considered)
            Terrain.STREET   // east (right)
        );

        // Car should choose straight (NORTH) every time
        final Direction chosen = car.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Car should prefer to go straight (NORTH) when available");
    }

    /**
     * Test that chooseDirection turns left when straight is blocked.
     *
     * <p>When straight ahead is impassable, Car should prefer left over right.
     */
    @Test
    void testChooseDirectionTurnsLeftWhenStraightBlocked() {
        final Car car = new Car(0, 0, Direction.NORTH);

        // Set up neighbors: WALL ahead, STREET to left and right
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.STREET,  // west (left - should be chosen)
            Terrain.STREET,  // south (reverse - not considered unless forced)
            Terrain.STREET   // east (right)
        );

        final Direction chosen = car.chooseDirection(neighbors);
        assertEquals(Direction.WEST, chosen,
                     "Car should turn left (WEST) when straight is blocked");
    }

    /**
     * Test that chooseDirection turns right when both straight and left are blocked.
     *
     * <p>Right is the third preference in the hierarchy.
     */
    @Test
    void testChooseDirectionTurnsRightWhenStraightAndLeftBlocked() {
        final Car car = new Car(0, 0, Direction.NORTH);

        // Set up neighbors: WALL ahead and left, STREET to right
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.WALL,    // west (left - blocked)
            Terrain.STREET,  // south (reverse - not considered unless forced)
            Terrain.STREET   // east (right - should be chosen)
        );

        final Direction chosen = car.chooseDirection(neighbors);
        assertEquals(Direction.EAST, chosen,
                     "Car should turn right (EAST) when straight and left are blocked");
    }

    /**
     * Test that chooseDirection reverses only when all other directions are blocked.
     *
     * <p>Reverse is the last resort, used only when straight, left, and right
     * are all impassable.
     */
    @Test
    void testChooseDirectionReversesOnlyWhenForced() {
        final Car car = new Car(0, 0, Direction.NORTH);

        // Set up neighbors: WALL in all directions except reverse
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.WALL,    // west (left - blocked)
            Terrain.STREET,  // south (reverse - only option)
            Terrain.WALL     // east (right - blocked)
        );

        final Direction chosen = car.chooseDirection(neighbors);
        assertEquals(Direction.SOUTH, chosen,
                     "Car should reverse (SOUTH) when all other directions are blocked");
    }

    // TODO: Add tests for chooseDirection with different starting directions (EAST, SOUTH, WEST)

    // TODO: Add tests for collide() method

    // TODO: Add tests for poke() method and disabled state

    // TODO: Add tests for reset() method

    // TODO: Add tests for setters (setX, setY, setDirection)

    /**
     * Helper method to create a neighbor map for chooseDirection testing.
     *
     * @param north the terrain to the north
     * @param west the terrain to the west
     * @param south the terrain to the south
     * @param east the terrain to the east
     * @return a map of directions to terrain types
     */
    private Map<Direction, Terrain> createNeighbors(final Terrain north,
                                                     final Terrain west,
                                                     final Terrain south,
                                                     final Terrain east) {
        return Map.of(
            Direction.NORTH, north,
            Direction.WEST, west,
            Direction.SOUTH, south,
            Direction.EAST, east
        );
    }
}
