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
 * @version Autumn 2026
 */
public final class FileLoader {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(FileLoader.class.getName());

    /** The folder that holds the city map files. */
    private static final String MAP_FOLDER = "maps" + File.separator;

    /** The map used when config/map.txt is missing or names no map. */
    private static final String DEFAULT_MAP = "city_map1.txt";

    /** The folder that holds the configuration files. */
    private static final String CONFIG_FOLDER = "config" + File.separator;

    /** The configuration file naming which map to load. */
    private static final String MAP_CONFIG = CONFIG_FOLDER + "map.txt";

    /** The filename of the vehicle configuration. */
    private static final String VEHICLES_CONFIG = CONFIG_FOLDER + "vehicles.txt";

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
     * @param frame the Frame containing the panel. Needed for error output.
     * @return the RoadRage simulation based on the city text file.
     */
    public static PropertyChangeEnabledRoadRageControls readCity(final JFrame frame) {
        PropertyChangeEnabledRoadRageControls result = null;

        // Load enabled vehicles and the chosen map from config
        loadVehicleConfig(frame);
        final String cityFile = MAP_FOLDER + loadMapName();

        try (final Scanner input = new Scanner(new File(cityFile))) {
            // First, we read the map description
            // Then, we read where the initial vehicles are
            result = new RoadRage(readGrid(input), readVehicles(input, frame));

        } catch (final IOException ioe) {
            JOptionPane.showMessageDialog(frame,
                    "Could not read city map file " + cityFile + ERROR_MESSAGE_SEPARATOR + ioe.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (final IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(frame,
                    "The city map file " + cityFile + " is not valid" + ERROR_MESSAGE_SEPARATOR
                            + iae.getMessage(),
                    "Map Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * Reads the name of the map to load from the map.txt file. The first line that
     * is neither blank nor a comment names a file in the maps folder.
     *
     * @return the map file name, or the default map if none is configured
     */
    private static String loadMapName() {
        String result = DEFAULT_MAP;

        try (final Scanner configScanner = new Scanner(new File(MAP_CONFIG))) {
            while (configScanner.hasNextLine()) {
                final String line = configScanner.nextLine().trim();
                if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
                    result = line;
                    break;
                }
            }
        } catch (final IOException ioe) {
            LOGGER.log(Level.INFO, "No map config found; using " + DEFAULT_MAP, ioe);
        }
        LOGGER.info("Loading map: " + result);
        return result;
    }

    /**
     * Loads the vehicle configuration from the vehicles.txt file.
     * Populates the set of enabled vehicle class names.
     *
     * @param frame the Frame for error display
     */
    private static void loadVehicleConfig(final JFrame frame) {
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
            JOptionPane.showMessageDialog(frame,
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
     * @param input The input scanner.
     * @return the grid of the terrains.
     */
    private static Terrain[][] readGrid(final Scanner input) {
        final int numRows = input.nextInt();
        final int numColumns = input.nextInt();
        input.nextLine();
        final Terrain[][] grid = new Terrain[numRows][numColumns];
        for (int row = 0; row < numRows; row++) {
            final String line = input.nextLine();
            for (int column = 0; column < numColumns; column++) {
                grid[row][column] = Terrain.valueOf(line.charAt(column));
            }
        }
        checkWallBorder(grid);
        return grid;
    }

    /**
     * Checks that every square on the edge of the map is a wall. A walled edge means
     * every vehicle always has terrain on all four of its sides.
     *
     * @param grid the grid of terrains read from the map file.
     * @throws IllegalArgumentException if any edge square is not a wall.
     */
    private static void checkWallBorder(final Terrain[][] grid) {
        final int lastRow = grid.length - 1;
        for (int row = 0; row <= lastRow; row++) {
            final int lastColumn = grid[row].length - 1;
            for (int column = 0; column <= lastColumn; column++) {
                final boolean onEdge = row == 0 || row == lastRow
                        || column == 0 || column == lastColumn;
                if (onEdge && grid[row][column] != Terrain.WALL) {
                    throw new IllegalArgumentException(
                            "The map must be surrounded by walls (X). Row " + row
                                    + ", column " + column + " is " + grid[row][column] + ".");
                }
            }
        }
    }

    /**
     * Reads the vehicle portion of the map file and instantiates vehicles
     * using reflection based on the enabled vehicles configuration.
     *
     * @param input The input scanner.
     * @param frame The frame for error display.
     * @return the list of Vehicles
     */
    private static List<Vehicle> readVehicles(final Scanner input, final JFrame frame) {
        final List<Vehicle> vehicles = new ArrayList<>();
        final int numVehicles = input.nextInt();
        input.nextLine();

        for (int lineCount = 0; lineCount < numVehicles; lineCount++) {
            final String vehicleType = input.next();
            final int vehicleX = input.nextInt();
            final int vehicleY = input.nextInt();
            final char vehicleDirection = input.next().charAt(0);
            input.nextLine();

            final String className = getClassName(vehicleType);

            // Only create vehicle if its type is enabled in config
            if (enabledVehicles.contains(className)) {
                final Vehicle vehicle = createVehicle(className, vehicleX, vehicleY,
                        Direction.valueOf(vehicleDirection), frame);
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        }
        return vehicles;
    }

    /**
     * Maps a vehicle type from the map file to its class name. A single character
     * is one of the built-in vehicle codes. Anything longer is taken as the class
     * name itself, so a new vehicle can appear on a map without changing this class.
     *
     * @param type the vehicle type from the map file, a code such as "T" or a class name
     * @return the simple class name of the vehicle
     */
    private static String getClassName(final String type) {
        if (type.length() > 1) {
            return type;
        }
        return switch (type.charAt(0)) {
            case 'B' -> "Bicycle";
            case 'A' -> "Atv";
            case 'H' -> "Human";
            case 'T' -> "Truck";
            case 'X' -> "Taxi";
            case 'C' -> "Car";
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
    }

    /**
     * Creates a vehicle instance using reflection.
     *
     * @param className the simple class name (e.g., "Truck")
     * @param x the x coordinate
     * @param y the y coordinate
     * @param direction the initial direction
     * @param frame the frame for error display
     * @return the created Vehicle, or null if creation failed
     */
    private static Vehicle createVehicle(final String className,
                                         final int x,
                                         final int y,
                                         final Direction direction,
                                         final JFrame frame) {
        Vehicle vehicle = null;
        final String fullClassName = VEHICLE_PACKAGE + className;

        try {
            final Class<?> vehicleClass = Class.forName(fullClassName);
            final Constructor<?> constructor =
                    vehicleClass.getConstructor(int.class, int.class, Direction.class);
            vehicle = (Vehicle) constructor.newInstance(x, y, direction);

        } catch (final ClassNotFoundException e) {
            showVehicleError(frame, className,
                    "Class not found. Check that " + className + ".java exists in "
                            + "the edu.uw.tcss.model package and compiles without errors.\n\n"
                            + "Did you spell the class name correctly in vehicles.txt?");

        } catch (final NoSuchMethodException e) {
            showVehicleError(frame, className,
                    "Constructor not found. " + className + " must have a constructor:\n\n"
                            + "public " + className + "(int x, int y, Direction dir)");

        } catch (final InstantiationException | IllegalAccessException
                       | InvocationTargetException e) {
            showVehicleError(frame, className,
                    "Could not create instance: " + e.getMessage());
        }

        return vehicle;
    }

    /**
     * Displays an error message about a vehicle loading failure.
     *
     * @param frame the frame for the dialog
     * @param className the class that failed to load
     * @param message the error details
     */
    private static void showVehicleError(final JFrame frame,
                                         final String className,
                                         final String message) {
        LOGGER.log(Level.WARNING, "Failed to load vehicle {0}: {1}",
                new Object[]{className, message});

        JOptionPane.showMessageDialog(frame,
                "Failed to load vehicle: " + className + "\n\n" + message,
                "Vehicle Loading Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
