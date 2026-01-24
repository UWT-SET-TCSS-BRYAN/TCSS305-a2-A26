/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.logic;

import edu.uw.tcss.model.Vehicle;
import java.util.List;

/**
 * Sealed interface representing type-safe events for the Road Rage simulation's
 * PropertyChangeListener framework. This provides a modern, type-safe alternative
 * to string-based property constants.
 *
 * <p>Each implementing record represents a specific event that can be observed,
 * carrying the actual event data for compile-time safety. This design enables
 * pattern matching and exhaustiveness checking in switch expressions when
 * handling simulation events.</p>
 *
 * <p><strong>Design Pattern:</strong> Sealed Classes + Records (Java 21)
 * <ul>
 *   <li>Sealed interface ensures all event types are known at compile time</li>
 *   <li>Record implementations provide immutability and reduced boilerplate</li>
 *   <li>Pattern matching enables type-safe event handling without casting</li>
 * </ul>
 *
 * <p>Usage example with PropertyChangeListener:</p>
 * <pre>{@code
 * roadRage.addPropertyChangeListener(evt -> {
 *     if (evt.getNewValue() instanceof RoadRageEvent event) {
 *         switch (event) {
 *             case RoadRageEvent.LightChanged e ->
 *                 setLightColor(e.newLight());
 *             case RoadRageEvent.VehiclesChanged e ->
 *                 updateVehicles(e.vehicles());
 *             case RoadRageEvent.GridChanged e ->
 *                 setGrid(e.grid());
 *             case RoadRageEvent.TimeChanged e ->
 *                 updateTime(e.newTime());
 *         }
 *     }
 * });
 * }</pre>
 *
 * @author Charles Bryan
 * @version Winter 2025
 * @see <a href="https://openjdk.org/jeps/409">JEP 409: Sealed Classes</a>
 * @see <a href="https://openjdk.org/jeps/395">JEP 395: Records</a>
 */
public sealed interface RoadRageEvent permits
        RoadRageEvent.LightChanged,
        RoadRageEvent.VehiclesChanged,
        RoadRageEvent.GridChanged,
        RoadRageEvent.TimeChanged {

    /**
     * Returns the timestamp when this event was created.
     *
     * @return timestamp in milliseconds since epoch
     */
    long timestamp();

    /**
     * Returns the property name for this event type, used when registering property-specific
     * listeners via {@link edu.uw.tcss.logic.PropertyChangeEnabledRoadRageControls#addPropertyChangeListener
     * (String, java.beans.PropertyChangeListener)}.
     *
     * <p><strong>Default Implementation:</strong> Returns the simple class name of the event
     * (e.g., "LightChanged", "VehiclesChanged"). This ensures consistency between
     * the event type and its property name.</p>
     *
     * @return the property name string for this event type
     */
    default String getPropertyName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Helper method to get the current time.
     *
     * @return current time in milliseconds since epoch
     */
    static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Event fired when the traffic light changes color.
     *
     * <p>This event provides type-safe access to both the old and new light colors.
     * When fired, this event is associated with property name "LightChanged".</p>
     *
     * @param oldLight the previous light color
     * @param newLight the new light color
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record LightChanged(Light oldLight, Light newLight, long timestamp) implements RoadRageEvent { }

    /**
     * Event fired when the vehicle states change.
     * This occurs after each simulation step when vehicles move or change state.
     *
     * <p>This event provides type-safe access to the current list of vehicles.
     * When fired, this event is associated with property name "VehiclesChanged".</p>
     *
     * @param vehicles the current list of vehicles
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record VehiclesChanged(List<Vehicle> vehicles, long timestamp) implements RoadRageEvent { }

    /**
     * Event fired when the terrain grid is initialized or reset.
     *
     * <p>This event provides type-safe access to the 2D terrain grid.
     * When fired, this event is associated with property name "GridChanged".</p>
     *
     * @param grid the 2D terrain grid
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record GridChanged(Terrain[][] grid, long timestamp) implements RoadRageEvent { }

    /**
     * Event fired when the simulation time step changes.
     *
     * <p>This event provides type-safe access to both the old and new time values.
     * When fired, this event is associated with property name "TimeChanged".</p>
     *
     * @param oldTime the previous time step
     * @param newTime the new time step
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record TimeChanged(long oldTime, long newTime, long timestamp) implements RoadRageEvent { }
}
