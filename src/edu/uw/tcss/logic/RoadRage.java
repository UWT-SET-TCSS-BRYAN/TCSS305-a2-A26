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
 * @version 1 OCT 2018
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
     * @param theGrid the 2D grid of Terrain that defines the map
     * @param theVehicles the Vehicles on the map
     */
    public RoadRage(final Terrain[][] theGrid, final List<Vehicle> theVehicles) {
        super();
        myVehicles = new ArrayList<>(theVehicles);
        myGrid = theGrid.clone();
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
    public void addPropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(theListener);
    }
    

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(theListener);
    }
    
    @Override
    public void addPropertyChangeListener(final String thePropertyName,
                                          final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(thePropertyName, theListener);
        
    }

    @Override
    public void removePropertyChangeListener(final String thePropertyName,
                                             final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(thePropertyName, theListener);
        
    }
    
    /**
     * Tests whether the square at the given x/y position exists on the map.
     * 
     * @param theX The x position.
     * @param theY The y position.
     * @return true if the position exists on the map, false otherwise.
     */
    private boolean isValidIndex(final int theY, final int theX) {
        return theY >= 0 && theY < myGrid.length
            && theX >= 0 && theX < myGrid[theY].length;
    }
    
    /**
     * Generates a read-only neighbors map for the specified vehicle.
     * 
     * @param theMover The vehicle.
     * @return The neighbors map.
     */
    private Map<Direction, Terrain> generateNeighbors(final Vehicle theMover) {
        final int x = theMover.getX();
        final int y = theMover.getY();
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
     * @param theLight The Light to base the color on.
     */
    private void setLightColor(final Light theLight) {
        final Light old = myLight;
        myLight = theLight;
        final var event = new RoadRageEvent.LightChanged(old, myLight, RoadRageEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
    }
    
    /**
     * Sets the time step for the simulation.
     *
     * @param theTimestep the time to set.
     */
    private void setTimeStep(final long theTimestep) {
        final long old = myTimestep;
        myTimestep = theTimestep;
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
     * @param theV1 the first vehicle
     * @param theV2 the second vehicle
     */
    private void resolveCollision(final Vehicle theV1, final Vehicle theV2) {
        if (theV1.isEnabled() && theV2.isEnabled()) {
            final int result = myCollisionComparator.compare(theV1, theV2);
            if (result < 0) {
                theV1.collide();  // v1 is "lesser" - v1 becomes disabled
            } else if (result > 0) {
                theV2.collide();  // v2 is "lesser" - v2 becomes disabled
            } else if (RAND.nextBoolean()) {
                theV1.collide();  // tie - coin flip
            } else {
                theV2.collide();
            }
        }
    }

    @Override
    public void setCollisionComparator(final Comparator<Vehicle> theComparator) {
        myCollisionComparator =
                Objects.requireNonNullElseGet(theComparator,
                        CollisionComparators::getDefault);
    }
}
