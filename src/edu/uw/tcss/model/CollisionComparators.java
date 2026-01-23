/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.model;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Factory class providing various Comparators for vehicle collision resolution.
 * Demonstrates the Strategy Pattern: each comparator encapsulates a different
 * algorithm for determining collision winners.
 *
 * @author Charles Bryan
 * @version Winter 2026
 */
public final class CollisionComparators {

    /** Random number generator for random collision outcomes. */
    private static final Random RANDOM = new Random();

    /**
     * Strategy 1: Mass-based comparison (realistic physics).
     * Heavier vehicles win collisions.
     */
    private static final Comparator<Vehicle> BY_MASS =
        Comparator.comparing(Vehicle::getMass);

    /** Bound for random outcome generation for -1 - 1 range. */
    private static final int RANDOM_BOUND = 3;

    /**
     * Strategy 2: Random comparison (chaos mode).
     * Returns -1, 0, or 1 randomly to determine winner.
     */
    private static final Comparator<Vehicle> RANDOM_OUTCOME =
        (_, _) -> RANDOM.nextInt(RANDOM_BOUND) - 1;

    /**
     * Strategy 3: Inverted mass (underdog mode).
     * Lighter vehicles win collisions - David beats Goliath!
     */
    private static final Comparator<Vehicle> INVERTED_MASS =
        Comparator.comparing(Vehicle::getMass).reversed();

    /** Private constructor to prevent instantiation. */
    private CollisionComparators() {
        super();
    }

    /**
     * Factory method: Returns a list of all available collision modes.
     * GUI components can use this to build selection controls.
     *
     * @return list of all collision modes
     */
    public static List<CollisionMode> getAll() {
        return List.of(
            new CollisionMode("Mass (Realistic)", BY_MASS),
            new CollisionMode("Random (Chaos)", RANDOM_OUTCOME),
            new CollisionMode("Inverted Mass (Underdog)", INVERTED_MASS)
        );
    }

    /**
     * Factory method: Returns the default collision comparator (mass-based).
     *
     * @return the default comparator
     */
    public static Comparator<Vehicle> getDefault() {
        return BY_MASS;
    }

    /**
     * A record pairing a collision comparator with its display name.
     * Used by the GUI to present collision mode options to the user.
     *
     * @param displayName the human-readable name for UI display
     * @param comparator the comparator implementation (the strategy)
     */
    public record CollisionMode(String displayName, Comparator<Vehicle> comparator) { }
}
