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
 * Comprehensive unit tests for the Car class.
 *
 * <p>Car behavior per assignment:
 * <ul>
 *   <li>Mass: 50</li>
 *   <li>Disabled Duration: 15</li>
 *   <li>Travels on: streets, traffic lights, crosswalks</li>
 *   <li>Cannot travel on: grass, walls, trails</li>
 *   <li>Stops for RED traffic lights (ignores yellow and green)</li>
 *   <li>Stops for RED and YELLOW crosswalk lights (only passes green)</li>
 *   <li>Prefers: Straight → Left → Right → Reverse (deterministic)</li>
 * </ul>
 *
 * @author TCSS 305 Instructors
 * @version Autumn 2026
 */
class CarTest {

    /** Number of iterations for poke re-enable direction randomness test. */
    private static final int TRIES_FOR_RANDOMNESS = 50;

    /** Expected mass for a Car. */
    private static final int CAR_MASS = 50;

    /** Expected disabled duration for a Car. */
    private static final int CAR_DISABLED_DURATION = 15;

    /** Test initial X coordinate. */
    private static final int INITIAL_X = 15;

    /** Test initial Y coordinate. */
    private static final int INITIAL_Y = 25;

    /** An X coordinate different from the initial one, for setter and reset tests. */
    private static final int OTHER_X = 99;

    /** A Y coordinate different from the initial one, for setter and reset tests. */
    private static final int OTHER_Y = 88;

    /** Message when a new Car is not enabled. */
    private static final String NEW_ENABLED_MESSAGE = "Newly constructed Car should be enabled";

    /** Message when a Car is not disabled after a collision. */
    private static final String COLLISION_DISABLED_MESSAGE =
            "Car should be disabled after collision";

    /** Suffix for messages that count pokes. */
    private static final String POKES = " pokes";

    /** Image file name for a Car facing north. */
    private static final String NORTH_IMAGE = "car_north.png";

    // ==================== Constructor Tests ====================

    /**
     * Test that the Car constructor correctly initializes all fields.
     */
    @Test
    void testConstructorInitializesFieldsCorrectly() {
        final Car car = new Car(INITIAL_X, INITIAL_Y, Direction.EAST);

        assertAll("Constructor should initialize all fields correctly",
            () -> assertEquals(INITIAL_X, car.getX(),
                             "X coordinate should be initialized correctly"),
            () -> assertEquals(INITIAL_Y, car.getY(),
                             "Y coordinate should be initialized correctly"),
            () -> assertEquals(Direction.EAST, car.getDirection(),
                             "Direction should be initialized to EAST"),
            () -> assertTrue(car.isEnabled(),
                           NEW_ENABLED_MESSAGE)
        );
    }

    // ==================== Property Tests ====================

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
     * Test that getDisabledDuration returns the correct value for Car.
     */
    @Test
    void testGetDisabledDuration() {
        final Car car = new Car(0, 0, Direction.NORTH);
        assertEquals(CAR_DISABLED_DURATION, car.getDisabledDuration(),
                     "Car disabled duration should be " + CAR_DISABLED_DURATION);
    }

    /**
     * Test that a newly constructed Car is enabled.
     */
    @Test
    void testIsEnabledInitially() {
        final Car car = new Car(0, 0, Direction.NORTH);
        assertTrue(car.isEnabled(), NEW_ENABLED_MESSAGE);
    }

    // ==================== canPass Tests - Valid Terrain ====================

    /**
     * Test that Car can pass through STREET terrain with all light colors.
     */
    @Test
    void testCanPassStreetWithAllLights() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should pass STREET with all light colors",
            () -> assertTrue(car.canPass(Terrain.STREET, Light.GREEN),
                           "Car should pass STREET with GREEN light"),
            () -> assertTrue(car.canPass(Terrain.STREET, Light.YELLOW),
                           "Car should pass STREET with YELLOW light"),
            () -> assertTrue(car.canPass(Terrain.STREET, Light.RED),
                           "Car should pass STREET with RED light")
        );
    }

    /**
     * Test that Car stops for RED traffic lights but passes GREEN and YELLOW.
     */
    @Test
    void testCanPassLightWithAllLights() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should pass LIGHT with GREEN/YELLOW, stop at RED",
            () -> assertTrue(car.canPass(Terrain.LIGHT, Light.GREEN),
                           "Car should pass LIGHT with GREEN light"),
            () -> assertTrue(car.canPass(Terrain.LIGHT, Light.YELLOW),
                           "Car should pass LIGHT with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.LIGHT, Light.RED),
                            "Car should NOT pass LIGHT with RED light")
        );
    }

    /**
     * Test that Car stops for RED and YELLOW crosswalk lights, only passes GREEN.
     */
    @Test
    void testCanPassCrosswalkWithAllLights() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should only pass CROSSWALK with GREEN light",
            () -> assertTrue(car.canPass(Terrain.CROSSWALK, Light.GREEN),
                           "Car should pass CROSSWALK with GREEN light"),
            () -> assertFalse(car.canPass(Terrain.CROSSWALK, Light.YELLOW),
                            "Car should NOT pass CROSSWALK with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.CROSSWALK, Light.RED),
                            "Car should NOT pass CROSSWALK with RED light")
        );
    }

    // ==================== canPass Tests - Invalid Terrain ====================

    /**
     * Test that Car cannot pass through GRASS terrain.
     */
    @Test
    void testCannotPassGrass() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should NOT pass GRASS with any light",
            () -> assertFalse(car.canPass(Terrain.GRASS, Light.GREEN),
                            "Car should NOT pass GRASS with GREEN light"),
            () -> assertFalse(car.canPass(Terrain.GRASS, Light.YELLOW),
                            "Car should NOT pass GRASS with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.GRASS, Light.RED),
                            "Car should NOT pass GRASS with RED light")
        );
    }

    /**
     * Test that Car cannot pass through WALL terrain.
     */
    @Test
    void testCannotPassWall() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should NOT pass WALL with any light",
            () -> assertFalse(car.canPass(Terrain.WALL, Light.GREEN),
                            "Car should NOT pass WALL with GREEN light"),
            () -> assertFalse(car.canPass(Terrain.WALL, Light.YELLOW),
                            "Car should NOT pass WALL with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.WALL, Light.RED),
                            "Car should NOT pass WALL with RED light")
        );
    }

    /**
     * Test that Car cannot pass through TRAIL terrain.
     */
    @Test
    void testCannotPassTrail() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Car should NOT pass TRAIL with any light",
            () -> assertFalse(car.canPass(Terrain.TRAIL, Light.GREEN),
                            "Car should NOT pass TRAIL with GREEN light"),
            () -> assertFalse(car.canPass(Terrain.TRAIL, Light.YELLOW),
                            "Car should NOT pass TRAIL with YELLOW light"),
            () -> assertFalse(car.canPass(Terrain.TRAIL, Light.RED),
                            "Car should NOT pass TRAIL with RED light")
        );
    }

    // ==================== chooseDirection Tests - S → L → R → Reverse ====================

    /**
     * Test that Car prefers to go straight when possible.
     */
    @Test
    void testChooseDirectionPrefersStraight() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (straight)
            Terrain.STREET,  // west (left)
            Terrain.STREET,  // south (reverse)
            Terrain.STREET   // east (right)
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car should prefer straight (NORTH) when available");
    }

    /**
     * Test that Car turns left when straight is blocked.
     */
    @Test
    void testChooseDirectionTurnsLeftWhenStraightBlocked() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.STREET,  // west (left)
            Terrain.STREET,  // south (reverse)
            Terrain.STREET   // east (right)
        );

        assertEquals(Direction.WEST, car.chooseDirection(neighbors),
                     "Car should turn left (WEST) when straight is blocked");
    }

    /**
     * Test that Car turns right when straight and left are blocked.
     */
    @Test
    void testChooseDirectionTurnsRightWhenStraightAndLeftBlocked() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.WALL,    // west (left - blocked)
            Terrain.STREET,  // south (reverse)
            Terrain.STREET   // east (right)
        );

        assertEquals(Direction.EAST, car.chooseDirection(neighbors),
                     "Car should turn right (EAST) when straight and left are blocked");
    }

    /**
     * Test that Car reverses only when all other directions are blocked.
     */
    @Test
    void testChooseDirectionReversesOnlyWhenForced() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.WALL,    // north (straight - blocked)
            Terrain.WALL,    // west (left - blocked)
            Terrain.STREET,  // south (reverse - only option)
            Terrain.WALL     // east (right - blocked)
        );

        assertEquals(Direction.SOUTH, car.chooseDirection(neighbors),
                     "Car should reverse (SOUTH) when all other directions are blocked");
    }

    /**
     * Test chooseDirection when facing EAST.
     */
    @Test
    void testChooseDirectionFacingEast() {
        final Car car = new Car(0, 0, Direction.EAST);

        // When facing EAST: straight=EAST, left=NORTH, right=SOUTH, reverse=WEST
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (left when facing EAST)
            Terrain.STREET,  // west (reverse when facing EAST)
            Terrain.STREET,  // south (right when facing EAST)
            Terrain.WALL     // east (straight - blocked)
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car facing EAST should turn left (NORTH) when straight is blocked");
    }

    /**
     * Test chooseDirection when facing SOUTH.
     */
    @Test
    void testChooseDirectionFacingSouth() {
        final Car car = new Car(0, 0, Direction.SOUTH);

        // When facing SOUTH: straight=SOUTH, left=EAST, right=WEST, reverse=NORTH
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (reverse when facing SOUTH)
            Terrain.WALL,    // west (right - blocked)
            Terrain.WALL,    // south (straight - blocked)
            Terrain.WALL     // east (left - blocked)
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car facing SOUTH should reverse (NORTH) when all forward blocked");
    }

    /**
     * Test chooseDirection when facing WEST.
     */
    @Test
    void testChooseDirectionFacingWest() {
        final Car car = new Car(0, 0, Direction.WEST);

        // When facing WEST: straight=WEST, left=SOUTH, right=NORTH, reverse=EAST
        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.STREET,  // north (right when facing WEST)
            Terrain.WALL,    // west (straight - blocked)
            Terrain.WALL,    // south (left - blocked)
            Terrain.STREET   // east (reverse when facing WEST)
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car facing WEST should turn right (NORTH) when straight and left blocked");
    }

    /**
     * Test that Car considers LIGHT terrain as valid.
     */
    @Test
    void testChooseDirectionWithLightTerrain() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.LIGHT,   // north (straight)
            Terrain.WALL,    // west
            Terrain.WALL,    // south
            Terrain.WALL     // east
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car should consider LIGHT terrain as valid for movement");
    }

    /**
     * Test that Car considers CROSSWALK terrain as valid.
     */
    @Test
    void testChooseDirectionWithCrosswalkTerrain() {
        final Car car = new Car(0, 0, Direction.NORTH);

        final Map<Direction, Terrain> neighbors = createNeighbors(
            Terrain.CROSSWALK,  // north (straight)
            Terrain.WALL,       // west
            Terrain.WALL,       // south
            Terrain.WALL        // east
        );

        assertEquals(Direction.NORTH, car.chooseDirection(neighbors),
                     "Car should consider CROSSWALK terrain as valid for movement");
    }

    // ==================== Collision and Disabled State Tests ====================

    /**
     * Test that collide() disables the Car.
     */
    @Test
    void testCollideDisablesCar() {
        final Car car = new Car(0, 0, Direction.NORTH);
        assertTrue(car.isEnabled(), "Car should start enabled");

        car.collide();

        assertFalse(car.isEnabled(), COLLISION_DISABLED_MESSAGE);
    }

    /**
     * Test that poke() counts down and re-enables after correct number of calls.
     */
    @Test
    void testPokeCountsDownAndReenables() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.collide();
        assertFalse(car.isEnabled(), COLLISION_DISABLED_MESSAGE);

        // Poke CAR_DISABLED_DURATION - 1 times (should still be disabled)
        for (int i = 0; i < CAR_DISABLED_DURATION - 1; i++) {
            car.poke();
            assertFalse(car.isEnabled(),
                       "Car should still be disabled after " + (i + 1) + POKES);
        }

        // One more poke should re-enable
        car.poke();
        assertTrue(car.isEnabled(),
                   "Car should be enabled after " + CAR_DISABLED_DURATION + POKES);
    }

    /**
     * Test that poke() sets a random direction when re-enabled.
     */
    @Test
    void testPokeAssignsRandomDirectionWhenReenabled() {
        final Set<Direction> seenDirections = new HashSet<>();

        for (int trial = 0; trial < TRIES_FOR_RANDOMNESS; trial++) {
            final Car car = new Car(0, 0, Direction.NORTH);
            car.collide();

            for (int i = 0; i < CAR_DISABLED_DURATION; i++) {
                car.poke();
            }

            seenDirections.add(car.getDirection());
        }

        assertTrue(seenDirections.size() >= 2,
                   "Car should get random direction when re-enabled. "
                   + "Saw only: " + seenDirections);
    }

    // ==================== Reset Tests ====================

    /**
     * Test that reset() restores position to initial values.
     */
    @Test
    void testResetRestoresPosition() {
        final Car car = new Car(INITIAL_X, INITIAL_Y, Direction.NORTH);

        car.setX(OTHER_X);
        car.setY(OTHER_Y);
        car.setDirection(Direction.SOUTH);

        car.reset();

        assertAll("Reset should restore initial state",
            () -> assertEquals(INITIAL_X, car.getX(),
                             "X should be restored to initial value"),
            () -> assertEquals(INITIAL_Y, car.getY(),
                             "Y should be restored to initial value"),
            () -> assertEquals(Direction.NORTH, car.getDirection(),
                             "Direction should be restored to initial value")
        );
    }

    /**
     * Test that reset() re-enables a disabled Car.
     */
    @Test
    void testResetReenablesDisabledCar() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.collide();
        assertFalse(car.isEnabled(), COLLISION_DISABLED_MESSAGE);

        car.reset();

        assertTrue(car.isEnabled(), "Car should be enabled after reset");
    }

    // ==================== Setter Tests ====================

    /**
     * Test setX() updates X coordinate.
     */
    @Test
    void testSetX() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.setX(OTHER_X);
        assertEquals(OTHER_X, car.getX(), "setX should update X coordinate");
    }

    /**
     * Test setY() updates Y coordinate.
     */
    @Test
    void testSetY() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.setY(OTHER_Y);
        assertEquals(OTHER_Y, car.getY(), "setY should update Y coordinate");
    }

    /**
     * Test setDirection() updates direction.
     */
    @Test
    void testSetDirection() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.setDirection(Direction.WEST);
        assertEquals(Direction.WEST, car.getDirection(),
                     "setDirection should update direction");
    }

    // ==================== Image Filename Tests ====================

    /**
     * Test getImageFileName() returns correct filename when enabled.
     */
    @Test
    void testGetImageFileNameWhenEnabled() {
        final Car car = new Car(0, 0, Direction.NORTH);
        assertEquals(NORTH_IMAGE, car.getImageFileName(),
                     "Image filename should include direction when enabled");
    }

    /**
     * Test getImageFileName() returns correct filename for each direction.
     */
    @Test
    void testGetImageFileNameForAllDirections() {
        final Car car = new Car(0, 0, Direction.NORTH);

        assertAll("Image filename should reflect current direction",
            () -> assertEquals(NORTH_IMAGE, imageFileNameFacing(car, Direction.NORTH)),
            () -> assertEquals("car_south.png", imageFileNameFacing(car, Direction.SOUTH)),
            () -> assertEquals("car_east.png", imageFileNameFacing(car, Direction.EAST)),
            () -> assertEquals("car_west.png", imageFileNameFacing(car, Direction.WEST))
        );
    }

    /**
     * Test getImageFileName() returns disabled filename when disabled.
     */
    @Test
    void testGetImageFileNameWhenDisabled() {
        final Car car = new Car(0, 0, Direction.NORTH);
        car.collide();

        assertEquals("car_disabled.png", car.getImageFileName(),
                     "Image filename should be 'car_disabled.png' when disabled");
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
     * Turns the Car to face the given direction and returns its image file name.
     *
     * @param car the Car to turn
     * @param direction the direction to face
     * @return the image file name the Car reports while facing that direction
     */
    private static String imageFileNameFacing(final Car car, final Direction direction) {
        car.setDirection(direction);
        return car.getImageFileName();
    }
}
