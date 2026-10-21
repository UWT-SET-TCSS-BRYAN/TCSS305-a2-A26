/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.logic;

import edu.uw.tcss.model.Vehicle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


/**
 * Implementation the behaviors of the RoadRage simulation. 
 * 
 * @author Charles Bryan
 * @version Autumn 2026
 */
public class RoadRage implements PropertyChangeEnabledRoadRageControls {
    
    /**
     * Random number generator for tie-breaking collisions.
     */
    private static final Random RAND = new Random();

    /**
     * The number of clock ticks between light changes.
     */
    private static final int LIGHT_CHANGE_TICKS = 15;
    
    /**
     * The terrain grid for the simulation.
     */
    private final Terrain[][] myGrid;
    
    /**
     * The current timestep of the simulation.
     */
    private long myTimestep;
    
    /**
     * The current status of lights.
     */
    private Light myLight;
    
    /**
     * The vehicles to move and display.
     */
    private final List<Vehicle> myVehicles;

    /**
     * The Comparator used to resolve collisions.
     * Default uses mass-based comparison (heavier wins).
     */
    private Comparator<Vehicle> myCollisionComparator = CollisionComparators.getDefault();

    /**
     * Manager for Property Change Listeners.
     */
    private final PropertyChangeSupport myPcs;
    
    /**
     * Sets the initial state of a RoadRage Object. 
     * 
     * @param grid the 2D grid of Terrain that defines the map
     * @param vehicles the Vehicles on the map
     */
    public RoadRage(final Terrain[][] grid, final List<Vehicle> vehicles) {
        super();
        myVehicles = new ArrayList<>(vehicles);
        myGrid = grid.clone();
        myPcs = new PropertyChangeSupport(this);
    }
    
    @SuppressWarnings("LawOfDemeter")
    @Override
    public void advance() {
        for (final Vehicle v : myVehicles) {
            final Map<Direction, Terrain> neighbors = generateNeighbors(v);

            // move the vehicle
            if (v.isEnabled()) {
                final Direction newDirection = v.chooseDirection(neighbors);
                v.setDirection(newDirection);

                // move one square in current direction, if it's okay to do so
                if (v.canPass(neighbors.get(newDirection), myLight)) {
                    v.setX(v.getX() + newDirection.dx());
                    v.setY(v.getY() + newDirection.dy());
                }
            } else {
                // become one move closer to revival
                v.poke();
            }

            // look for collisions
            for (final Vehicle other : myVehicles) {
                if (v == other) { // don't collide with self
                    continue;
                }

                if (v.getX() == other.getX() && v.getY() == other.getY()) { //NOPMD
                    // resolve collision using the current Comparator
                    resolveCollision(v, other);
                }
            }
        }
        advanceTimeStep();
        if (myTimestep % LIGHT_CHANGE_TICKS == 0) {
            setLightColor(myLight.advance());
        }
        fireVehicleChange();
    }
    
    @Override
    public void start() {
        reset();
    }
    
    @Override
    public void reset() {
        resetVehicles();
        setLightColor(Light.GREEN);
        setTimeStep(0);
        fireGridChange();
        fireVehicleChange();
    }
    
    @Override
    public int getHeight() {
        return myGrid.length;
    }

    @Override
    public int getWidth() {
        return myGrid[0].length;
    }
    
 
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        myPcs.addPropertyChangeListener(listener);
    }
    

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        myPcs.removePropertyChangeListener(listener);
    }
    
    @Override
    public void addPropertyChangeListener(final String propertyName,
                                          final PropertyChangeListener listener) {
        myPcs.addPropertyChangeListener(propertyName, listener);
        
    }

    @Override
    public void removePropertyChangeListener(final String propertyName,
                                             final PropertyChangeListener listener) {
        myPcs.removePropertyChangeListener(propertyName, listener);
        
    }
    
    /**
     * Tests whether the square at the given x/y position exists on the map.
     * 
     * @param x The x position.
     * @param y The y position.
     * @return true if the position exists on the map, false otherwise.
     */
    private boolean isValidIndex(final int y, final int x) {
        return y >= 0 && y < myGrid.length
            && x >= 0 && x < myGrid[y].length;
    }
    
    /**
     * Generates a read-only neighbors map for the specified vehicle.
     * 
     * @param mover The vehicle.
     * @return The neighbors map.
     */
    private Map<Direction, Terrain> generateNeighbors(final Vehicle mover) {
        final int x = mover.getX();
        final int y = mover.getY();
        final Map<Direction, Terrain> result = new HashMap<>();

        for (final Direction dir : Direction.values()) {
            if (isValidIndex(y + dir.dy(), x + dir.dx())) {
                result.put(dir, myGrid[y + dir.dy()][x + dir.dx()]);
            }
        }
        return Collections.unmodifiableMap(result);
    }
    
    /**
     * Sets the paint color appropriately for the current lights.
     *
     * @param light The Light to base the color on.
     */
    private void setLightColor(final Light light) {
        final Light old = myLight;
        myLight = light;
        final var event = new RoadRageEvent.LightChanged(old, myLight, RoadRageEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
    }
    
    /**
     * Sets the time step for the simulation.
     *
     * @param timestep the time to set.
     */
    private void setTimeStep(final long timestep) {
        final long old = myTimestep;
        myTimestep = timestep;
        final var event = new RoadRageEvent.TimeChanged(old, myTimestep, RoadRageEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
    }
    
    /**
     * Advances the time step by 1 for the simulation. 
     */
    private void advanceTimeStep() {
        setTimeStep(myTimestep + 1);
    }
    
    /**
     * Inform PropertyChangeListeners of the current state of vehicles.
     */
    private void fireVehicleChange() {
        final var event = new RoadRageEvent.VehiclesChanged(
                new ArrayList<>(myVehicles), RoadRageEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
    }
    
    /**
     * Inform PropertyChangeListeners of the current 2D Terrain grid state.
     */
    private void fireGridChange() {
        final var event = new RoadRageEvent.GridChanged(myGrid.clone(), RoadRageEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
    }
    
    /**
     * Reset all vehicles to their original state.
     */
    private void resetVehicles() {
        for (final Vehicle mov : myVehicles) {
            mov.reset();
        }
    }

    /**
     * Resolves a collision between two vehicles using the current Comparator.
     * The vehicle that compares as "lesser" becomes disabled.
     * If vehicles are equal, one is randomly selected to become disabled.
     *
     * @param v1 the first vehicle
     * @param v2 the second vehicle
     */
    private void resolveCollision(final Vehicle v1, final Vehicle v2) {
        if (v1.isEnabled() && v2.isEnabled()) {
            final int result = myCollisionComparator.compare(v1, v2);
            if (result < 0) {
                v1.collide();  // v1 is "lesser" - v1 becomes disabled
            } else if (result > 0) {
                v2.collide();  // v2 is "lesser" - v2 becomes disabled
            } else if (RAND.nextBoolean()) {
                v1.collide();  // tie - coin flip
            } else {
                v2.collide();
            }
        }
    }

    @Override
    public void setCollisionComparator(final Comparator<Vehicle> comparator) {
        myCollisionComparator =
                Objects.requireNonNullElseGet(comparator,
                        CollisionComparators::getDefault);
    }
}
