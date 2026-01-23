/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.model;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.Map;

/**
 * An interface for objects that can move in four directions.
 *
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @version 1.1
 */
public interface Vehicle {

    /**
     * Returns whether or not this object may move onto the given type of
     * terrain, when the street lights are the given color.
     * 
     * @param theTerrain The terrain.
     * @param theLight The light color.
     * @return whether or not this object may move onto the given type of
     *         terrain when the street lights are the given color.
     */
    boolean canPass(Terrain theTerrain, Light theLight);

    /**
     * Returns the direction this object would like to move, based on the given
     * map of the neighboring terrain.
     * 
     * @param theNeighbors The map of neighboring terrain.
     * @return the direction this object would like to move.
     */
    Direction chooseDirection(Map<Direction, Terrain> theNeighbors);

    /**
     * Called when this Vehicle loses a collision.
     * Marks this vehicle as disabled.
     */
    void collide();

    /**
     * Returns the number of updates this vehicle stays disabled after a collision.
     *
     * @return the number of updates.
     */
    int getDisabledDuration();

    /**
     * Returns the mass of this vehicle. Heavier vehicles survive collisions
     * with lighter vehicles.
     *
     * @return the mass of this vehicle.
     */
    int getMass();

    /**
     * Returns the file name of the image for this Vehicle object, such as
     * "car.gif".
     * 
     * @return the file name.
     */
    String getImageFileName();

    /**
     * Returns this Vehicle object's direction.
     * 
     * @return the direction.
     */
    Direction getDirection();

    /**
     * Returns this Vehicle object's x-coordinate.
     * 
     * @return the x-coordinate.
     */
    int getX();

    /**
     * Returns this Vehicle object's y-coordinate.
     * 
     * @return the y-coordinate.
     */
    int getY();

    /**
     * Returns whether this Vehicle object is enabled and should move on the map.
     *
     * @return true if the object is enabled, false otherwise.
     */
    boolean isEnabled();

    /**
     * Called by the UI to notify a disabled vehicle that 1 movement round has
     * passed, so that it will become 1 move closer to becoming enabled again.
     */
    void poke();

    /**
     * Moves this vehicle back to its original position.
     */
    void reset();

    /**
     * Sets this object's facing direction to the given value.
     * 
     * @param theDir The new direction.
     */
    void setDirection(Direction theDir);

    /**
     * Sets this object's x-coordinate to the given value.
     * 
     * @param theX The new x-coordinate.
     */
    void setX(int theX);

    /**
     * Sets this object's y-coordinate to the given value.
     * 
     * @param theY The new y-coordinate.
     */
    void setY(int theY);

} // end of interface Vehicle

