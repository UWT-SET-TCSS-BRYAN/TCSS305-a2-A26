package edu.uw.tcss.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Bicycle class.
 *
 * <p>This test class demonstrates how to test TERRAIN-SEEKING behavior.
 * Bicycle actively seeks TRAIL terrain, preferring trails over other valid terrain.
 * This creates a more complex directional logic than Car's simple preference hierarchy.
 *
 * @author TCSS 305 Instructors
 * @version Winter 2026
 */
class BicycleTest {

    /** Expected mass for a Bicycle. */
    private static final int BICYCLE_MASS = 20;

    /** Expected disabled duration for a Bicycle. */
    private static final int BICYCLE_DISABLED_DURATION = 35;

    private static final int INITIAL_X = 8;

    private static final int INITIAL_Y = 12;

    /**
     * Test that the Bicycle constructor correctly initializes position and direction.
     */
    @Test
    void testConstructorInitializesFieldsCorrectly() {
        final Bicycle bicycle = new Bicycle(INITIAL_X, INITIAL_Y, Direction.WEST);

        assertAll("Constructor should initialize all fields correctly",
            () -> assertEquals(INITIAL_X, bicycle.getX(), "X coordinate should be initialized to 8"),
            () -> assertEquals(INITIAL_Y, bicycle.getY(), "Y coordinate should be initialized to 12"),
            () -> assertEquals(Direction.WEST, bicycle.getDirection(),
                             "Direction should be initialized to WEST")
        );
    }

    /**
     * Test that getDisabledDuration returns the correct value for Bicycle.
     */
    @Test
    void testGetDisabledDuration() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        assertEquals(BICYCLE_DISABLED_DURATION, bicycle.getDisabledDuration(),
                     "Bicycle disabled duration should be " + BICYCLE_DISABLED_DURATION);
    }

    /**
     * Test that getMass returns the correct value for Bicycle.
     */
    @Test
    void testGetMass() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        assertEquals(BICYCLE_MASS, bicycle.getMass(),
                     "Bicycle mass should be " + BICYCLE_MASS);
    }

    /**
     * Test that Bicycle can pass through TRAIL terrain with all light colors.
     * Trails have no lights, so Bicycle always passes through them.
     */
    @Test
    void testCanPassTrailWithAllLights() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        assertAll("Bicycle should pass TRAIL with all light colors",
            () -> assertTrue(bicycle.canPass(Terrain.TRAIL, Light.GREEN),
                           "Bicycle should pass TRAIL with GREEN light"),
            () -> assertTrue(bicycle.canPass(Terrain.TRAIL, Light.YELLOW),
                           "Bicycle should pass TRAIL with YELLOW light"),
            () -> assertTrue(bicycle.canPass(Terrain.TRAIL, Light.RED),
                           "Bicycle should pass TRAIL with RED light")
        );
    }

    /**
     * Test that Bicycle stops for YELLOW and RED traffic lights.
     *
     * <p>This is stricter than Car, which can pass through YELLOW lights.
     */
    @Test
    void testCanPassLightWithAllLights() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        assertAll("Bicycle should pass LIGHT only with GREEN light",
            () -> assertTrue(bicycle.canPass(Terrain.LIGHT, Light.GREEN),
                           "Bicycle should pass LIGHT with GREEN light"),
            () -> assertFalse(bicycle.canPass(Terrain.LIGHT, Light.YELLOW),
                            "Bicycle should NOT pass LIGHT with YELLOW light"),
            () -> assertFalse(bicycle.canPass(Terrain.LIGHT, Light.RED),
                            "Bicycle should NOT pass LIGHT with RED light")
        );
    }

    // TODO: Add tests for STREET terrain with all lights

    // TODO: Add tests for CROSSWALK terrain with all lights

    // TODO: Add tests for invalid terrain (GRASS, WALL)

    /**
     * Test that chooseDirection seeks trail straight ahead.
     *
     * <p>When a trail is straight ahead, Bicycle should always choose it,
     * even if other valid terrain (like streets) exists to the left or right.
     */
    @Test
    void testChooseDirectionSeeksTrailAhead() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        // Set up neighbors: TRAIL ahead, STREET to left and right
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.TRAIL,   // north (straight - should be chosen for trail)
            Terrain.STREET,  // west (left - valid but not a trail)
            Terrain.WALL,    // south (reverse - not considered)
            Terrain.STREET   // east (right - valid but not a trail)
        );

        final Direction chosen = bicycle.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Bicycle should choose TRAIL straight ahead (NORTH) "
                     + "over other valid terrain");
    }

    /**
     * Test that chooseDirection seeks trail to the left when not ahead.
     *
     * <p>Trail-seeking priority: Straight → Left → Right.
     * If no trail ahead but trail to the left, Bicycle should turn left.
     */
    @Test
    void testChooseDirectionSeeksTrailToLeft() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        // Set up neighbors: STREET ahead, TRAIL to left, STREET to right
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight - valid but not a trail)
            Terrain.TRAIL,   // west (left - should be chosen for trail)
            Terrain.WALL,    // south (reverse - not considered)
            Terrain.STREET   // east (right - valid but not a trail)
        );

        final Direction chosen = bicycle.chooseDirection(neighbors);
        assertEquals(Direction.WEST, chosen,
                     "Bicycle should turn left (WEST) to reach TRAIL "
                     + "when no trail is straight ahead");
    }

    /**
     * Test that chooseDirection seeks trail to the right when not ahead or left.
     *
     * <p>If trails are only to the right, Bicycle should turn right to reach them.
     */
    @Test
    void testChooseDirectionSeeksTrailToRight() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        // Set up neighbors: STREET ahead and left, TRAIL to right
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight - valid but not a trail)
            Terrain.STREET,  // west (left - valid but not a trail)
            Terrain.WALL,    // south (reverse - not considered)
            Terrain.TRAIL    // east (right - should be chosen for trail)
        );

        final Direction chosen = bicycle.chooseDirection(neighbors);
        assertEquals(Direction.EAST, chosen,
                     "Bicycle should turn right (EAST) to reach TRAIL "
                     + "when no trail is ahead or left");
    }

    /**
     * Test that chooseDirection does NOT reverse to face a trail behind.
     *
     * <p>Bicycle seeks trails, but will not reverse just to face one.
     * If valid terrain exists ahead, left, or right, Bicycle follows
     * the standard S → L → R preference and ignores the trail behind.
     */
    @Test
    void testChooseDirectionIgnoresTrailBehind() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        // Set up neighbors: STREET ahead, left, and right; TRAIL behind
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight - should be chosen)
            Terrain.STREET,  // west (left)
            Terrain.TRAIL,   // south (reverse - should be ignored)
            Terrain.STREET   // east (right)
        );

        final Direction chosen = bicycle.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Bicycle should NOT reverse to face TRAIL behind. "
                     + "Should follow normal S→L→R preference and go straight (NORTH)");
    }

    /**
     * Test that chooseDirection follows S→L→R preference when no trails are nearby.
     *
     * <p>When no trails are in S/L/R directions, Bicycle should behave like Car:
     * prefer straight, then left, then right.
     */
    @Test
    void testChooseDirectionFollowsPreferenceWhenNoTrails() {
        final Bicycle bicycle = new Bicycle(0, 0, Direction.NORTH);

        // Set up neighbors: STREET ahead, left, and right (no trails)
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight - should be chosen)
            Terrain.STREET,  // west (left)
            Terrain.WALL,    // south (reverse)
            Terrain.STREET   // east (right)
        );

        final Direction chosen = bicycle.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Bicycle should prefer straight (NORTH) when no trails are nearby");
    }

    // TODO: Add tests for S→L→R→Reverse preference when no trails present

    // TODO: Add tests for reverse behavior when all S/L/R are blocked

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
