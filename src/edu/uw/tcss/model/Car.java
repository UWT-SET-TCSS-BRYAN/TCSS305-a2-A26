package edu.uw.tcss.model;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.Map;

public class Car implements Vehicle {


    public Car(final int theX, final int theY, final Direction theDir) {

    }

    public Direction chooseDirection(final Map<Direction, Terrain> theNeighbors) {
        return null;
    }

    public boolean canPass(final Terrain theTerrain, final Light theLight) {
        return false;
    }

    public void collide() {

    }

    public int getMass() {
        return 0;
    }

    public String getImageFileName() {
        return "";
    }

    public int getDisabledDuration() {
        return 0;
    }

    public Direction getDirection() {
        return null;
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }


    public boolean isEnabled() {
        return true;
    }

    public void poke() {
    }

    public void reset() {
    }

    public void setDirection(final Direction theDir) {

    }

    public void setX(final int theX) {

    }

    public void setY(final int theY) {

    }

    public String toString() {
        // TODO: You may modify this method as needed for debugging
        return "";
    }
}
