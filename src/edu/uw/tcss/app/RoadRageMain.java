/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.app;

import edu.uw.tcss.view.RoadRageGUI;
import java.awt.EventQueue;

/**
 * Runs the Road Rage program.
 * 
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @version Autumn 2026
 */

public final class RoadRageMain {
    
    /**
     * Private constructor to prevent construction of instances.
     */
    private RoadRageMain() {
        super();
        // do nothing
    }

    /**
     * Constructs the main GUI window frame.
     * 
     * @param args Command line arguments (ignored).
     */
    public static void main(final String... args) {
        EventQueue.invokeLater(RoadRageGUI::new);
    }
}
