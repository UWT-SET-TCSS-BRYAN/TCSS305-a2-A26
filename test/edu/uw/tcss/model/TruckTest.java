package edu.uw.tcss.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the Truck class.
 *
 * <p>This test class demonstrates how to test RANDOM behavior in vehicles.
 * The Truck randomly selects from valid directions, so we must run multiple
 * iterations and verify that all expected options appear over time.
 *
 * @author TCSS 305 Instructors
 * @version Winter 2026
 */
class TruckTest {

    /** The number of times to run random tests to ensure randomness. */
    private static final int TRIES_FOR_RANDOMNESS = 50;

    /** Expected mass for a Truck. */
    private static final int TRUCK_MASS = 100;

    /** Expected disabled duration for a Truck. */
    private static final int TRUCK_DISABLED_DURATION = 5;

    private static final int INITIAL_X = 10;

    private static final int INITIAL_Y = 25;

    /**
     * Test that the Truck constructor correctly initializes position and direction.
     */
    @Test
    void testConstructorInitializesFieldsCorrectly() {
        final Truck truck = new Truck(INITIAL_X, INITIAL_Y, Direction.NORTH);

        assertAll("Constructor should initialize all fields correctly",
            () -> assertEquals(INITIAL_X, truck.getX(), "X coordinate should be initialized to " + INITIAL_X),
            () -> assertEquals(INITIAL_Y, truck.getY(), "Y coordinate should be initialized to " + INITIAL_Y),
            () -> assertEquals(Direction.NORTH, truck.getDirection(),
                             "Direction should be initialized to NORTH")
        );
    }

    /**
     * Test that getDisabledDuration returns the correct value for Truck.
     */
    @Test
    void testGetDisabledDuration() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertEquals(TRUCK_DISABLED_DURATION, truck.getDisabledDuration(),
                     "Truck disabled duration should be " + TRUCK_DISABLED_DURATION);
    }

    /**
     * Test that getMass returns the correct value for Truck.
     */
    @Test
    void testGetMass() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertEquals(TRUCK_MASS, truck.getMass(),
                     "Truck mass should be " + TRUCK_MASS);
    }

    /**
     * Test that a newly constructed Truck is enabled.
     */
    @Test
    void testIsEnabledInitially() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertTrue(truck.isEnabled(), "Newly constructed Truck should be enabled");
    }

    /**
     * Test that Truck can pass through STREET terrain with all light colors.
     * Trucks always pass through streets regardless of light state.
     */
    @Test
    void testCanPassStreetWithAllLights() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should pass STREET with all light colors",
            () -> assertTrue(truck.canPass(Terrain.STREET, Light.GREEN),
                           "Truck should pass STREET with GREEN light"),
            () -> assertTrue(truck.canPass(Terrain.STREET, Light.YELLOW),
                           "Truck should pass STREET with YELLOW light"),
            () -> assertTrue(truck.canPass(Terrain.STREET, Light.RED),
                           "Truck should pass STREET with RED light")
        );
    }

    /**
     * Test that Truck can pass through LIGHT terrain with all light colors.
     * Trucks ignore traffic lights and drive through them.
     */
    @Test
    void testCanPassLightWithAllLights() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should pass LIGHT with all light colors",
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.GREEN),
                           "Truck should pass LIGHT with GREEN light"),
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.YELLOW),
                           "Truck should pass LIGHT with YELLOW light"),
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.RED),
                           "Truck should pass LIGHT with RED light")
        );
    }

    /**
     * Test that Truck cannot pass through CROSSWALK terrain with RED light.
     * Trucks stop for red crosswalk lights.
     */
    @Test
    void testCanPassCrosswalkWithRedLight() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertFalse(truck.canPass(Terrain.CROSSWALK, Light.RED),
                    "Truck should NOT pass CROSSWALK with RED light");
    }

    // TODO: Add tests for CROSSWALK with GREEN and YELLOW lights

    // TODO: Add tests for invalid terrain (GRASS, WALL, TRAIL)

    /**
     * Test that chooseDirection randomly selects from valid directions.
     *
     * <p>This test demonstrates how to test random behavior. We run the method
     * multiple times and verify that we see multiple different results,
     * proving that the selection is random rather than deterministic.
     */
    @Test
    void testChooseDirectionRandomlySelectsFromValidDirections() {
        final Set<Direction> seenDirections = getDirections();

        // Truck should randomly choose from NORTH, WEST, or EAST
        // After 50 tries, we should have seen at least 2 different directions
        assertTrue(seenDirections.size() >= 2,
                   "Truck should randomly select from valid directions. "
                   + "Expected at least 2 different directions after "
                   + TRIES_FOR_RANDOMNESS + " tries, but only saw: " + seenDirections);

        // None of the chosen directions should be SOUTH (the wall)
        assertFalse(seenDirections.contains(Direction.SOUTH),
                    "Truck should never choose SOUTH when it's a WALL");
    }

    private Set<Direction> getDirections() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        // Set up neighbors: STREET to north, west, and east (all valid)
        // WALL to south (invalid)
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north
            Terrain.STREET,  // west
            Terrain.WALL,    // south
            Terrain.STREET   // east
        );

        // Run chooseDirection many times and collect the results
        final Set<Direction> seenDirections = new HashSet<>();
        for (int i = 0; i < TRIES_FOR_RANDOMNESS; i++) {
            final Direction chosen = truck.chooseDirection(neighbors);
            seenDirections.add(chosen);
        }
        return seenDirections;
    }

    /**
     * Test that chooseDirection returns reverse ONLY when all other directions are blocked.
     *
     * <p>Trucks prefer to go straight, left, or right randomly. They only reverse
     * as a last resort when all three forward-facing directions are impassable.
     */
    @Test
    void testChooseDirectionReversesOnlyWhenForced() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        // Set up neighbors: WALL in all directions except south (reverse)
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north
            Terrain.WALL,    // west
            Terrain.STREET,  // south (reverse direction)
            Terrain.WALL     // east
        );

        // When all forward directions are blocked, Truck must reverse
        final Direction chosen = truck.chooseDirection(neighbors);
        assertEquals(Direction.SOUTH, chosen,
                     "Truck should reverse (SOUTH) when all other directions are blocked");
    }

    // TODO: Add more tests for chooseDirection with different terrain configurations

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
