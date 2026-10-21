/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.logic;

import java.beans.PropertyChangeListener;

/**
 * Allows implementing classes to leverage the {@code PropertyChange}
 * framework of the Observer Design Pattern to inform interested parties
 * of updates to the Road Rage simulation state.
 *
 * <p>Defines behaviors allowing PropertyChangeListeners to be added or removed from a
 * {@code RoadRageControls} object. Objects from implementing classes should inform added
 * {@code PropertyChangeListeners} when methods defined in {@code RoadRageControls} mutate
 * the state of the object.</p>
 *
 * <h2>Event System Overview</h2>
 *
 * <p>This interface uses Java's PropertyChangeListener framework combined with modern
 * type-safe {@link RoadRageEvent} objects. When simulation state changes, listeners receive
 * PropertyChangeEvents where the {@code newValue} contains a {@link RoadRageEvent} record
 * with the actual event data.</p>
 *
 * <h3>Available Events</h3>
 * <p>The following events can be observed, each as a sealed {@link RoadRageEvent} subtype:</p>
 * <ul>
 *   <li><strong>{@link RoadRageEvent.LightChanged}</strong> (property name: "LightChanged")
 *       - Fired when the traffic light changes color</li>
 *   <li><strong>{@link RoadRageEvent.VehiclesChanged}</strong> (property name: "VehiclesChanged")
 *       - Fired when vehicles move or change state</li>
 *   <li><strong>{@link RoadRageEvent.GridChanged}</strong> (property name: "GridChanged")
 *       - Fired when the terrain grid is initialized or reset</li>
 *   <li><strong>{@link RoadRageEvent.TimeChanged}</strong> (property name: "TimeChanged")
 *       - Fired when the simulation time step advances</li>
 * </ul>
 *
 * <h3>Usage Patterns</h3>
 *
 * <h4>Pattern 1: Listen to All Events</h4>
 * <p>Register a listener without specifying a property name to receive all events.
 * Use pattern matching to handle different event types:</p>
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
 * <h4>Pattern 2: Listen to Specific Events</h4>
 * <p>Register a listener with a specific property name to receive only that event type:</p>
 * <pre>{@code
 * // Listen only for light changes
 * roadRage.addPropertyChangeListener("LightChanged", evt -> {
 *     if (evt.getNewValue() instanceof RoadRageEvent.LightChanged e) {
 *         setLightColor(e.newLight());
 *     }
 * });
 * }</pre>
 *
 * <h3>Type Safety Benefits</h3>
 * <p>Unlike traditional string-based property changes, this system provides:</p>
 * <ul>
 *   <li>Compile-time type checking with pattern matching</li>
 *   <li>IDE autocomplete for event types and their data fields</li>
 *   <li>Exhaustiveness checking in switch expressions</li>
 *   <li>Immutable event data via records</li>
 *   <li>No casting required when using pattern matching</li>
 * </ul>
 *
 * @author Charles Bryan
 * @version Autumn 2026
 * @see RoadRageEvent
 * @see RoadRageControls
 */
public interface PropertyChangeEnabledRoadRageControls extends RoadRageControls {

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered for
     * all properties. The same listener object may be added more than once, and will be
     * called as many times as it is added. If listener is null, no exception is thrown and
     * no action is taken.
     *
     * @param listener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(PropertyChangeListener listener);


    /**
     * Add a PropertyChangeListener for a specific property. The listener will be invoked only
     * when a call on firePropertyChange names that specific property. The same listener object
     * may be added more than once. For each property, the listener will be invoked the number
     * of times it was added for that property. If propertyName or listener is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties. If listener was added
     * more than once to the same event source, it will be notified one less time after being
     * removed. If listener is null, or was never added, no exception is thrown and no action
     * is taken.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property. If listener was added more than
     * once to the same event source for the specified property, it will be notified one less
     * time after being removed. If propertyName is null, no exception is thrown and no action
     * is taken. If listener is null, or was never added for the specified property, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(String propertyName,
                                      PropertyChangeListener listener);
}
