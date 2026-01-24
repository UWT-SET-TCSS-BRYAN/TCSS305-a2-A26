/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.io;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.PropertyChangeEnabledRoadRageControls;
import edu.uw.tcss.logic.RoadRage;
import edu.uw.tcss.logic.Terrain;
import edu.uw.tcss.model.Vehicle;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A utility class for The Road Rage application.
 * Loads the city map and vehicles from configuration files.
 *
 * @author Charles Bryan
 * @version Winter 2026
 */
public final class FileLoader {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(FileLoader.class.getName());

    /** The filename of the city map. */
    private static final String CITY_FILE =
            "maps" + File.separator + "city_map1.txt";

    /** The filename of the vehicle configuration. */
    private static final String VEHICLES_CONFIG =
            "config" + File.separator + "vehicles.txt";

    /** The package where vehicle classes are located. */
    private static final String VEHICLE_PACKAGE = "edu.uw.tcss.model.";

    /** Character indicating a comment line in config files. */
    private static final char COMMENT_CHAR = '#';


    /** Separator for error messages. */
    private static final String ERROR_MESSAGE_SEPARATOR = ":\n\n";

    /** Set of enabled vehicle types loaded from config. */
    private static Set<String> enabledVehicles;

    /**
     * A private constructor, to prevent external instantiation.
     */
    private FileLoader() {
        super();
    }

    /**
     * Read the city text file and build a RoadRage simulation based on the file.
     *
     * @param theFrame the Frame containing the panel. Needed for error output.
     * @return the RoadRage simulation based on the city text file.
     */
    public static PropertyChangeEnabledRoadRageControls readCity(final JFrame theFrame) {
        PropertyChangeEnabledRoadRageControls result = null;

        // Load enabled vehicles from config
        loadVehicleConfig(theFrame);

        try (final Scanner input = new Scanner(new File(CITY_FILE))) {
            // First, we read the map description
            // Then, we read where the initial vehicles are
            result = new RoadRage(readGrid(input), readVehicles(input, theFrame));

        } catch (final IOException ioe) {
            JOptionPane.showMessageDialog(theFrame,
                    "Could not read city map file " + CITY_FILE + ERROR_MESSAGE_SEPARATOR + ioe.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * Loads the vehicle configuration from the vehicles.txt file.
     * Populates the set of enabled vehicle class names.
     *
     * @param theFrame the Frame for error display
     */
    private static void loadVehicleConfig(final JFrame theFrame) {
        enabledVehicles = new HashSet<>();

        try (final Scanner configScanner = new Scanner(new File(VEHICLES_CONFIG))) {
            while (configScanner.hasNextLine()) {
                final String line = configScanner.nextLine().trim();

                // Skip empty lines and comments
                if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
                    enabledVehicles.add(line);
                }
            }
            LOGGER.info(() -> "Loaded vehicle config: " + enabledVehicles);

        } catch (final IOException ioe) {
            final String message = "Could not read vehicle config file " + VEHICLES_CONFIG;
            LOGGER.log(Level.WARNING,
                    message + ". All vehicles will be disabled.", ioe);
            JOptionPane.showMessageDialog(theFrame,
                    message
                            + ERROR_MESSAGE_SEPARATOR + ioe.getMessage()
                            + "\n\nNo vehicles will be loaded.",
                    "Configuration Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Reads the grid portion of the map file.
     *
     * @param theInput The input scanner.
     * @return the grid of the terrains.
     */
    private static Terrain[][] readGrid(final Scanner theInput) {
        final int numRows = theInput.nextInt();
        final int numColumns = theInput.nextInt();
        theInput.nextLine();
        final Terrain[][] grid = new Terrain[numRows][numColumns];
        for (int row = 0; row < numRows; row++) {
            final String line = theInput.nextLine();
            for (int column = 0; column < numColumns; column++) {
                grid[row][column] = Terrain.valueOf(line.charAt(column));
            }
        }
        return grid;
    }

    /**
     * Reads the vehicle portion of the map file and instantiates vehicles
     * using reflection based on the enabled vehicles configuration.
     *
     * @param theInput The input scanner.
     * @param theFrame The frame for error display.
     * @return the list of Vehicles
     */
    private static List<Vehicle> readVehicles(final Scanner theInput, final JFrame theFrame) {
        final List<Vehicle> vehicles = new ArrayList<>();
        final int numVehicles = theInput.nextInt();
        theInput.nextLine();

        for (int lineCount = 0; lineCount < numVehicles; lineCount++) {
            final char vehicleType = theInput.next().charAt(0);
            final int vehicleX = theInput.nextInt();
            final int vehicleY = theInput.nextInt();
            final char vehicleDirection = theInput.next().charAt(0);
            theInput.nextLine();

            final String className = getClassName(vehicleType);

            // Only create vehicle if its type is enabled in config
            if (enabledVehicles.contains(className)) {
                final Vehicle vehicle = createVehicle(className, vehicleX, vehicleY,
                        Direction.valueOf(vehicleDirection), theFrame);
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        }
        return vehicles;
    }

    /**
     * Maps a vehicle type character to its class name.
     *
     * @param theType the vehicle type character from the map file
     * @return the class name, or null if unknown type
     */
    private static String getClassName(final char theType) {
        return switch (theType) {
            case 'B' -> "Bicycle";
            case 'A' -> "Atv";
            case 'H' -> "Human";
            case 'T' -> "Truck";
            case 'X' -> "Taxi";
            case 'C' -> "Car";
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + theType);
        };
    }

    /**
     * Creates a vehicle instance using reflection.
     *
     * @param theClassName the simple class name (e.g., "Truck")
     * @param theX the x coordinate
     * @param theY the y coordinate
     * @param theDirection the initial direction
     * @param theFrame the frame for error display
     * @return the created Vehicle, or null if creation failed
     */
    private static Vehicle createVehicle(final String theClassName,
                                         final int theX,
                                         final int theY,
                                         final Direction theDirection,
                                         final JFrame theFrame) {
        Vehicle vehicle = null;
        final String fullClassName = VEHICLE_PACKAGE + theClassName;

        try {
            final Class<?> vehicleClass = Class.forName(fullClassName);
            final Constructor<?> constructor =
                    vehicleClass.getConstructor(int.class, int.class, Direction.class);
            vehicle = (Vehicle) constructor.newInstance(theX, theY, theDirection);

        } catch (final ClassNotFoundException e) {
            showVehicleError(theFrame, theClassName,
                    "Class not found. Check that " + theClassName + ".java exists in "
                            + "the edu.uw.tcss.model package and compiles without errors.\n\n"
                            + "Did you spell the class name correctly in vehicles.txt?");

        } catch (final NoSuchMethodException e) {
            showVehicleError(theFrame, theClassName,
                    "Constructor not found. " + theClassName + " must have a constructor:\n\n"
                            + "public " + theClassName + "(int theX, int theY, Direction theDir)");

        } catch (final InstantiationException | IllegalAccessException
                       | InvocationTargetException e) {
            showVehicleError(theFrame, theClassName,
                    "Could not create instance: " + e.getMessage());
        }

        return vehicle;
    }

    /**
     * Displays an error message about a vehicle loading failure.
     *
     * @param theFrame the frame for the dialog
     * @param theClassName the class that failed to load
     * @param theMessage the error details
     */
    private static void showVehicleError(final JFrame theFrame,
                                         final String theClassName,
                                         final String theMessage) {
        LOGGER.log(Level.WARNING, "Failed to load vehicle {0}: {1}",
                new Object[]{theClassName, theMessage});

        JOptionPane.showMessageDialog(theFrame,
                "Failed to load vehicle: " + theClassName + "\n\n" + theMessage,
                "Vehicle Loading Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
