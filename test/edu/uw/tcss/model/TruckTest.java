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
 * Comprehensive unit tests for the Truck class.
 *
 * <p>Truck behavior per assignment:
 * <ul>
 *   <li>Mass: 100</li>
 *   <li>Disabled Duration: 5</li>
 *   <li>Travels on: streets, traffic lights, crosswalks</li>
 *   <li>Cannot travel on: grass, walls, trails</li>
 *   <li>Drives through ALL traffic lights without stopping</li>
 *   <li>Stops for RED crosswalk lights; drives through yellow/green</li>
 *   <li>Randomly selects straight, left, or right from valid directions</li>
 *   <li>Reverses only when all three directions are impassable</li>
 * </ul>
 *
 * @author TCSS 305 Instructors
 * @version Autumn 2026
 */
class TruckTest {

    /** Number of iterations for testing random behavior. */
    private static final int TRIES_FOR_RANDOMNESS = 50;

    /** Expected mass for a Truck. */
    private static final int TRUCK_MASS = 100;

    /** Expected disabled duration for a Truck. */
    private static final int TRUCK_DISABLED_DURATION = 5;

    /** Test initial X coordinate. */
    private static final int INITIAL_X = 10;

    /** Test initial Y coordinate. */
    private static final int INITIAL_Y = 20;

    /** An X coordinate different from the initial one, for setter and reset tests. */
    private static final int OTHER_X = 99;

    /** A Y coordinate different from the initial one, for setter and reset tests. */
    private static final int OTHER_Y = 88;

    /** Message when a new Truck is not enabled. */
    private static final String NEW_ENABLED_MESSAGE = "Newly constructed Truck should be enabled";

    /** Message when a Truck is not disabled after a collision. */
    private static final String COLLISION_DISABLED_MESSAGE =
            "Truck should be disabled after collision";

    /** Suffix for messages that count pokes. */
    private static final String POKES = " pokes";

    /** Image file name for a Truck facing north. */
    private static final String NORTH_IMAGE = "truck_north.png";

    // ==================== Constructor Tests ====================

    /**
     * Test that the Truck constructor correctly initializes position and direction.
     */
    @Test
    void testConstructorInitializesFieldsCorrectly() {
        final Truck truck = new Truck(INITIAL_X, INITIAL_Y, Direction.NORTH);

        assertAll("Constructor should initialize all fields correctly",
            () -> assertEquals(INITIAL_X, truck.getX(),
                             "X coordinate should be initialized correctly"),
            () -> assertEquals(INITIAL_Y, truck.getY(),
                             "Y coordinate should be initialized correctly"),
            () -> assertEquals(Direction.NORTH, truck.getDirection(),
                             "Direction should be initialized to NORTH"),
            () -> assertTrue(truck.isEnabled(),
                           NEW_ENABLED_MESSAGE)
        );
    }

    // ==================== Property Tests ====================

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
     * Test that getDisabledDuration returns the correct value for Truck.
     */
    @Test
    void testGetDisabledDuration() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertEquals(TRUCK_DISABLED_DURATION, truck.getDisabledDuration(),
                     "Truck disabled duration should be " + TRUCK_DISABLED_DURATION);
    }

    /**
     * Test that a newly constructed Truck is enabled.
     */
    @Test
    void testIsEnabledInitially() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertTrue(truck.isEnabled(), NEW_ENABLED_MESSAGE);
    }

    // ==================== canPass Tests - Valid Terrain ====================

    /**
     * Test that Truck can pass through STREET terrain with all light colors.
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
     * Trucks drive through ALL traffic lights without stopping.
     */
    @Test
    void testCanPassLightWithAllLights() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should pass LIGHT with all light colors (ignores traffic lights)",
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.GREEN),
                           "Truck should pass LIGHT with GREEN light"),
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.YELLOW),
                           "Truck should pass LIGHT with YELLOW light"),
            () -> assertTrue(truck.canPass(Terrain.LIGHT, Light.RED),
                           "Truck should pass LIGHT with RED light")
        );
    }

    /**
     * Test that Truck can pass through CROSSWALK with GREEN light.
     */
    @Test
    void testCanPassCrosswalkWithGreenLight() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertTrue(truck.canPass(Terrain.CROSSWALK, Light.GREEN),
                   "Truck should pass CROSSWALK with GREEN light");
    }

    /**
     * Test that Truck can pass through CROSSWALK with YELLOW light.
     */
    @Test
    void testCanPassCrosswalkWithYellowLight() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertTrue(truck.canPass(Terrain.CROSSWALK, Light.YELLOW),
                   "Truck should pass CROSSWALK with YELLOW light");
    }

    /**
     * Test that Truck stops at CROSSWALK with RED light.
     */
    @Test
    void testCanPassCrosswalkWithRedLight() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertFalse(truck.canPass(Terrain.CROSSWALK, Light.RED),
                    "Truck should NOT pass CROSSWALK with RED light");
    }

    // ==================== canPass Tests - Invalid Terrain ====================

    /**
     * Test that Truck cannot pass through GRASS terrain.
     */
    @Test
    void testCannotPassGrass() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should NOT pass GRASS with any light",
            () -> assertFalse(truck.canPass(Terrain.GRASS, Light.GREEN),
                            "Truck should NOT pass GRASS with GREEN light"),
            () -> assertFalse(truck.canPass(Terrain.GRASS, Light.YELLOW),
                            "Truck should NOT pass GRASS with YELLOW light"),
            () -> assertFalse(truck.canPass(Terrain.GRASS, Light.RED),
                            "Truck should NOT pass GRASS with RED light")
        );
    }

    /**
     * Test that Truck cannot pass through WALL terrain.
     */
    @Test
    void testCannotPassWall() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should NOT pass WALL with any light",
            () -> assertFalse(truck.canPass(Terrain.WALL, Light.GREEN),
                            "Truck should NOT pass WALL with GREEN light"),
            () -> assertFalse(truck.canPass(Terrain.WALL, Light.YELLOW),
                            "Truck should NOT pass WALL with YELLOW light"),
            () -> assertFalse(truck.canPass(Terrain.WALL, Light.RED),
                            "Truck should NOT pass WALL with RED light")
        );
    }

    /**
     * Test that Truck cannot pass through TRAIL terrain.
     */
    @Test
    void testCannotPassTrail() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Truck should NOT pass TRAIL with any light",
            () -> assertFalse(truck.canPass(Terrain.TRAIL, Light.GREEN),
                            "Truck should NOT pass TRAIL with GREEN light"),
            () -> assertFalse(truck.canPass(Terrain.TRAIL, Light.YELLOW),
                            "Truck should NOT pass TRAIL with YELLOW light"),
            () -> assertFalse(truck.canPass(Terrain.TRAIL, Light.RED),
                            "Truck should NOT pass TRAIL with RED light")
        );
    }

    // ==================== chooseDirection Tests ====================

    /**
     * Test that chooseDirection randomly selects from valid directions.
     */
    @Test
    void testChooseDirectionRandomlySelectsFromValidDirections() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north
            Terrain.STREET,  // west
            Terrain.WALL,    // south
            Terrain.STREET   // east
        );

        final Set<Direction> seenDirections = new HashSet<>();
        for (int i = 0; i < TRIES_FOR_RANDOMNESS; i++) {
            seenDirections.add(truck.chooseDirection(neighbors));
        }

        assertTrue(seenDirections.size() >= 2,
                   "Truck should randomly select from valid directions. "
                   + "Expected at least 2 different directions, but only saw: " + seenDirections);

        assertFalse(seenDirections.contains(Direction.SOUTH),
                    "Truck should never choose SOUTH when it's a WALL");
    }

    /**
     * Test that chooseDirection can select all three valid directions.
     */
    @Test
    void testChooseDirectionCanSelectAllValidDirections() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north
            Terrain.STREET,  // west
            Terrain.WALL,    // south
            Terrain.STREET   // east
        );

        final Set<Direction> seenDirections = new HashSet<>();
        for (int i = 0; i < TRIES_FOR_RANDOMNESS * 2; i++) {
            seenDirections.add(truck.chooseDirection(neighbors));
        }

        // With enough tries, we should see all three valid directions
        assertAll("Truck should be able to choose any valid direction",
            () -> assertTrue(seenDirections.contains(Direction.NORTH),
                           "Truck should sometimes choose NORTH"),
            () -> assertTrue(seenDirections.contains(Direction.WEST),
                           "Truck should sometimes choose WEST"),
            () -> assertTrue(seenDirections.contains(Direction.EAST),
                           "Truck should sometimes choose EAST")
        );
    }

    /**
     * Test that chooseDirection reverses only when all other directions are blocked.
     */
    @Test
    void testChooseDirectionReversesOnlyWhenForced() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north
            Terrain.WALL,    // west
            Terrain.STREET,  // south (reverse)
            Terrain.WALL     // east
        );

        final Direction chosen = truck.chooseDirection(neighbors);
        assertEquals(Direction.SOUTH, chosen,
                     "Truck should reverse when all other directions are blocked");
    }

    /**
     * Test that chooseDirection never reverses when forward options exist.
     */
    @Test
    void testChooseDirectionNeverReversesWhenForwardOptionsExist() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (only forward option)
            Terrain.WALL,    // west
            Terrain.STREET,  // south (reverse)
            Terrain.WALL     // east
        );

        for (int i = 0; i < TRIES_FOR_RANDOMNESS; i++) {
            final Direction chosen = truck.chooseDirection(neighbors);
            assertNotEquals(Direction.SOUTH, chosen,
                           "Truck should never reverse when forward options exist");
        }
    }

    /**
     * Test chooseDirection with only one valid forward direction.
     */
    @Test
    void testChooseDirectionWithOneValidDirection() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north
            Terrain.STREET,  // west (only valid forward option)
            Terrain.WALL,    // south
            Terrain.WALL     // east
        );

        final Direction chosen = truck.chooseDirection(neighbors);
        assertEquals(Direction.WEST, chosen,
                     "Truck should choose the only valid forward direction");
    }

    /**
     * Test chooseDirection with LIGHT terrain (valid for Truck).
     */
    @Test
    void testChooseDirectionWithLightTerrain() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.LIGHT,   // north
            Terrain.WALL,    // west
            Terrain.WALL,    // south
            Terrain.WALL     // east
        );

        final Direction chosen = truck.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Truck should be able to choose LIGHT terrain");
    }

    /**
     * Test chooseDirection with CROSSWALK terrain (valid for Truck).
     */
    @Test
    void testChooseDirectionWithCrosswalkTerrain() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.CROSSWALK,  // north
            Terrain.WALL,       // west
            Terrain.WALL,       // south
            Terrain.WALL        // east
        );

        final Direction chosen = truck.chooseDirection(neighbors);
        assertEquals(Direction.NORTH, chosen,
                     "Truck should be able to choose CROSSWALK terrain");
    }

    // ==================== Collision and Disabled State Tests ====================

    /**
     * Test that collide() disables the Truck.
     */
    @Test
    void testCollideDisablesTruck() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertTrue(truck.isEnabled(), "Truck should start enabled");

        truck.collide();

        assertFalse(truck.isEnabled(), COLLISION_DISABLED_MESSAGE);
    }

    /**
     * Test that poke() counts down and re-enables after correct number of calls.
     */
    @Test
    void testPokeCountsDownAndReenables() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.collide();
        assertFalse(truck.isEnabled(), COLLISION_DISABLED_MESSAGE);

        // Poke TRUCK_DISABLED_DURATION - 1 times (should still be disabled)
        for (int i = 0; i < TRUCK_DISABLED_DURATION - 1; i++) {
            truck.poke();
            assertFalse(truck.isEnabled(),
                       "Truck should still be disabled after " + (i + 1) + POKES);
        }

        // One more poke should re-enable
        truck.poke();
        assertTrue(truck.isEnabled(),
                   "Truck should be enabled after " + TRUCK_DISABLED_DURATION + POKES);
    }

    /**
     * Test that poke() sets a random direction when re-enabled.
     */
    @Test
    void testPokeAssignsRandomDirectionWhenReenabled() {
        final Set<Direction> seenDirections = new HashSet<>();

        for (int trial = 0; trial < TRIES_FOR_RANDOMNESS; trial++) {
            final Truck truck = new Truck(0, 0, Direction.NORTH);
            truck.collide();

            for (int i = 0; i < TRUCK_DISABLED_DURATION; i++) {
                truck.poke();
            }

            seenDirections.add(truck.getDirection());
        }

        assertTrue(seenDirections.size() >= 2,
                   "Truck should get random direction when re-enabled. "
                   + "Saw only: " + seenDirections);
    }

    // ==================== Reset Tests ====================

    /**
     * Test that reset() restores position to initial values.
     */
    @Test
    void testResetRestoresPosition() {
        final Truck truck = new Truck(INITIAL_X, INITIAL_Y, Direction.NORTH);

        truck.setX(OTHER_X);
        truck.setY(OTHER_Y);
        truck.setDirection(Direction.SOUTH);

        truck.reset();

        assertAll("Reset should restore initial state",
            () -> assertEquals(INITIAL_X, truck.getX(),
                             "X should be restored to initial value"),
            () -> assertEquals(INITIAL_Y, truck.getY(),
                             "Y should be restored to initial value"),
            () -> assertEquals(Direction.NORTH, truck.getDirection(),
                             "Direction should be restored to initial value")
        );
    }

    /**
     * Test that reset() re-enables a disabled Truck.
     */
    @Test
    void testResetReenablesDisabledTruck() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.collide();
        assertFalse(truck.isEnabled(), COLLISION_DISABLED_MESSAGE);

        truck.reset();

        assertTrue(truck.isEnabled(), "Truck should be enabled after reset");
    }

    // ==================== Setter Tests ====================

    /**
     * Test setX() updates X coordinate.
     */
    @Test
    void testSetX() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.setX(OTHER_X);
        assertEquals(OTHER_X, truck.getX(), "setX should update X coordinate");
    }

    /**
     * Test setY() updates Y coordinate.
     */
    @Test
    void testSetY() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.setY(OTHER_Y);
        assertEquals(OTHER_Y, truck.getY(), "setY should update Y coordinate");
    }

    /**
     * Test setDirection() updates direction.
     */
    @Test
    void testSetDirection() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.setDirection(Direction.WEST);
        assertEquals(Direction.WEST, truck.getDirection(),
                     "setDirection should update direction");
    }

    // ==================== Image Filename Tests ====================

    /**
     * Test getImageFileName() returns correct filename when enabled.
     */
    @Test
    void testGetImageFileNameWhenEnabled() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        assertEquals(NORTH_IMAGE, truck.getImageFileName(),
                     "Image filename should include direction when enabled");
    }

    /**
     * Test getImageFileName() returns correct filename for each direction.
     */
    @Test
    void testGetImageFileNameForAllDirections() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);

        assertAll("Image filename should reflect current direction",
            () -> assertEquals(NORTH_IMAGE, imageFileNameFacing(truck, Direction.NORTH)),
            () -> assertEquals("truck_south.png", imageFileNameFacing(truck, Direction.SOUTH)),
            () -> assertEquals("truck_east.png", imageFileNameFacing(truck, Direction.EAST)),
            () -> assertEquals("truck_west.png", imageFileNameFacing(truck, Direction.WEST))
        );
    }

    /**
     * Test getImageFileName() returns disabled filename when disabled.
     */
    @Test
    void testGetImageFileNameWhenDisabled() {
        final Truck truck = new Truck(0, 0, Direction.NORTH);
        truck.collide();

        assertEquals("truck_disabled.png", truck.getImageFileName(),
                     "Image filename should be 'truck_disabled.png' when disabled");
    }

    // ==================== Helper Methods ====================

    /**
     * Helper method to create a neighbor map for chooseDirection testing.
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

    /**
     * Turns the Truck to face the given direction and returns its image file name.
     *
     * @param truck the Truck to turn
     * @param direction the direction to face
     * @return the image file name the Truck reports while facing that direction
     */
    private static String imageFileNameFacing(final Truck truck, final Direction direction) {
        truck.setDirection(direction);
        return truck.getImageFileName();
    }
}
