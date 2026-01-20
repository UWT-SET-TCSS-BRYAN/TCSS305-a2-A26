/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.model;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Factory class providing various Comparators for vehicle collision resolution.
 * Each inner class implements a different collision strategy.
 *
 * @author Charles Bryan
 * @version Winter 2025
 */
public final class CollisionComparators {

    /** Private constructor to prevent instantiation. */
    private CollisionComparators() {
        super();
    }

    /**
     * Returns a list of all available collision modes.
     * GUI components can use this to build selection controls.
     *
     * @return list of all collision modes
     */
    public static List<CollisionMode> getAll() {
        return List.of(
            new CollisionMode("Mass (Realistic)", new Mass()),
//            new CollisionMode("Death Time (Legacy)", new DeathTime()),
            new CollisionMode("Random (Chaos)", new RandomOutcome()),
            new CollisionMode("Inverted Mass (Underdog)", new Inverted())
        );
    }

//    public static List<CollisionMode> getAll() {
//        return List.of(
//                new CollisionMode("Mass (Realistic)",
//                        Comparator.comparing(Vehicle::getMass)),
//                new CollisionMode("Death Time (Legacy)",
//                        Comparator.comparing(Vehicle::getDeathTime).reversed()),
//                new CollisionMode("Random (Chaos)",
//                        (v1, v2) -> new Random().nextInt(3) - 1),
//                new CollisionMode("Inverted Mass (Underdog)",
//                        Comparator.comparing(Vehicle::getMass).reversed())
//        );
//    }

    /**
     * Returns the default collision comparator (mass-based).
     *
     * @return the default comparator
     */
    public static Comparator<Vehicle> getDefault() {
        return new Mass();
    }

    /**
     * A wrapper that pairs a collision comparator with its display name.
     *
     * @param displayName the human-readable name for UI display
     * @param comparator the comparator implementation
     */
    public record CollisionMode(String displayName, Comparator<Vehicle> comparator) { }

    /**
     * Compares vehicles by mass - heavier vehicles win collisions.
     * This is the realistic/physics-based mode.
     */
    public static class Mass implements Comparator<Vehicle> {
        @Override
        public int compare(final Vehicle theV1, final Vehicle theV2) {
            return Integer.compare(theV1.getMass(), theV2.getMass());
        }
    }

    /**
     * Compares vehicles by death time - lower death time wins.
     * This replicates the original/legacy collision behavior.
     */
    public static class DeathTime implements Comparator<Vehicle> {
        @Override
        public int compare(final Vehicle theV1, final Vehicle theV2) {
            // Lower death time = wins, so reverse the comparison
            return Integer.compare(theV2.getDeathTime(), theV1.getDeathTime());
        }
    }

    /**
     * Random collision outcomes - chaos mode!
     */
    public static class RandomOutcome implements Comparator<Vehicle> {
        /** Random number generator. */
        private static final Random RAND = new Random();

        @Override
        public int compare(final Vehicle theV1, final Vehicle theV2) {
            // Returns -1, 0, or 1 randomly
            return RAND.nextInt(3) - 1;
        }
    }

    /**
     * Inverted mass comparison - lighter vehicles win.
     * David vs Goliath mode - the underdog always wins!
     */
    public static class Inverted implements Comparator<Vehicle> {
        @Override
        public int compare(final Vehicle theV1, final Vehicle theV2) {
            // Inverted: lighter vehicle wins
            return Integer.compare(theV2.getMass(), theV1.getMass());
        }
    }
}
