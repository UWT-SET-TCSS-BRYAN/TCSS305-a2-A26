package edu.uw.tcss.model;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.Terrain;
import java.util.Map;

// TODO add a class Javadoc comment here, including @author and @version tags.
public class Truck implements Vehicle {

    public Truck(final int x, final int y, final Direction dir) {

    }

    public Direction chooseDirection(final Map<Direction, Terrain> neighbors) {
        return null;
    }

    public boolean canPass(final Terrain terrain, final Light light) {

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


    public void setDirection(final Direction dir) {

    }

    public void setX(final int x) {

    }

    public void setY(final int y) {

    }

    public String toString() {
        return "";
    }
}
