/*
 * TCSS 305 - Road Rage
 */

package edu.uw.tcss.view;

import edu.uw.tcss.logic.Direction;
import edu.uw.tcss.logic.Light;
import edu.uw.tcss.logic.RoadRageEvent;
import edu.uw.tcss.logic.Terrain;
import edu.uw.tcss.model.Vehicle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * A drawing panel for the map.
 * 
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @author Charles Bryan
 * @version Autumn 2026
 * 
 */
@SuppressWarnings("DataFlowIssue")
public final class RoadRagePanel extends JPanel implements
        PropertyChangeListener, ChangeListener {

    /**
     * The UID of this class (to avoid warnings).
     */
    @Serial
    private static final long serialVersionUID = 4269666L;

    /**
     * The font used by this panel.
     */
    private static final Font FONT = new Font("SansSerif", Font.BOLD, 9);
    
    /**
     * The stroke used for painting.
     */
    private static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_BUTT,
                                                              BasicStroke.JOIN_MITER, 2,
                                                              new float[] {2, 2, 2, 2}, 0);

    /**
     * The offset to use to position cross walk lines and to use for cross walk light size.
     */
    private static final double CROSSWALK_SCALE = .25;
    
    /**
     * The size in pixels of the directional markers drawn on vehicles in debug
     * mode.
     */
    private static final int MARKER_SIZE = 10;
    
    /**
     * The offset in pixels of the debug messages drawn for each square.
     */
    private static final int DEBUG_OFFSET = 10;
    
    /**
     * The size in pixels of a side of one "square" on the grid.
     */
    private static final int SQUARE_SIZE = 40;
    

    // Instance Fields
    
    /**
     * The terrain grid for the simulation.
     */
    private Terrain[][] myGrid;
    
    /**
     * A flag indicating whether or not we are running in debug mode.
     */
    private boolean myDebugFlag;

    /**
     * The current timestep of the simulation.
     */
    private long myTimestep;
    
    /**
     * The current color of lights.
     */
    private Color myLightColor;
        
    /**
     * The vehicles to move and display. 
     */
    private List<Vehicle> myVehicles;

    // Constructor

    /**
     * Construct a new Panel.
     * @param width width of the 2D grid of Terrain that defines the map
     * @param height height of the 2D grid of Terrain that defines the map
     */
    public RoadRagePanel(final int width, final int height) {
        super();

        myVehicles = new ArrayList<>();
        myGrid = new Terrain[0][0];
        setLightColor(Light.GREEN);
        setPreferredSize(new Dimension(width * SQUARE_SIZE,
                                       height * SQUARE_SIZE));
        setBackground(Color.GREEN);
        setFont(FONT);
    }

    // Instance Methods

    /**
     * Paints this panel on the screen with the specified Graphics object.
     * 
     * @param graphics The Graphics object.
     */
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(STROKE);

        // draw city map

        drawMap(g2);

        // draw vehicles
        for (final Vehicle v : myVehicles) {
            final String imageFilename = "icons//" + v.getImageFileName();
            ImageIcon imgIcon = new ImageIcon(imageFilename);

            if (imgIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                imgIcon = new ImageIcon(getClass().getResource(imageFilename));
            }

            final Image img = imgIcon.getImage();
            g2.drawImage(img, v.getX() * SQUARE_SIZE, v.getY() * SQUARE_SIZE,
                         SQUARE_SIZE, SQUARE_SIZE, this);

            if (myDebugFlag) {
                drawDebugInfo(g2, v);
            }
        }

        if (myDebugFlag) {
            g2.setColor(Color.WHITE);
            g2.drawString("Update # " + myTimestep, DEBUG_OFFSET / 2,
                          FONT.getSize() + DEBUG_OFFSET / 2);
        }
    }
    

    /**
     * Draws the city map with the specified Graphics2D object.
     * 
     * @param graphics The Graphics2D object.
     */
    @SuppressWarnings("OverlyLongMethod")
    private void drawMap(final Graphics2D graphics) {
        for (int y = 0; y < myGrid.length; y++) {
            final int topy = y * SQUARE_SIZE;

            for (int x = 0; x < myGrid[y].length; x++) {
                final int leftx = x * SQUARE_SIZE;

                switch (myGrid[y][x]) {
                    case STREET:
                        graphics.setPaint(Color.LIGHT_GRAY);
                        graphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        drawStreetLines(graphics, x, y);
                        break;

                    case WALL:
                        graphics.setPaint(Color.BLACK);
                        graphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;

                    case TRAIL:
                        graphics.setPaint(Color.YELLOW.darker().darker());
                        graphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;

                    case LIGHT:
                        // draw a circle of appropriate color
                        graphics.setPaint(Color.LIGHT_GRAY);
                        graphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        graphics.setPaint(myLightColor);
                        graphics.fillOval(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;
                        
                    case CROSSWALK:
                        graphics.setPaint(Color.LIGHT_GRAY);
                        graphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        
                        drawCrossWalkLines(graphics, x, y);
                        
                        // draw a small circle of appropriate color centered in the square
                        graphics.setPaint(myLightColor);
                        graphics.fillOval(leftx + (int)  (SQUARE_SIZE * CROSSWALK_SCALE),
                                             topy  + (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                             SQUARE_SIZE / 2, SQUARE_SIZE / 2);
                        break;

                    default:
                }

                drawDebugInfo(graphics, x, y);
            }
        }
    }
    
    /**
     * Draws debugging information, if necessary.
     * 
     * @param graphics The Graphics context to use for drawing.
     * @param x The x-coordinate of the street.
     * @param y The y-coordinate of the street.
     */
    private void drawDebugInfo(final Graphics2D graphics, final int x, final int y) {

        if (myDebugFlag) {
            // draw numbers for the row and column
            final Paint oldPaint = graphics.getPaint();
            graphics.setPaint(Color.BLACK);

            final int leftx = x * SQUARE_SIZE;
            final int topy = y * SQUARE_SIZE;
            graphics.drawString("(" + x + ", " + y + ")", leftx, topy + DEBUG_OFFSET);
            graphics.setPaint(oldPaint);
        }
    }
    
    /**
     * Draws the debug information for a single Vehicle.
     * 
     * @param graphics The graphic context.
     * @param vehicle The Vehicle being drawn.
     */
    @SuppressWarnings("OverlyLongMethod")
    private void drawDebugInfo(final Graphics2D graphics, final Vehicle vehicle) {
        int x = vehicle.getX() * SQUARE_SIZE;
        int y = vehicle.getY() * SQUARE_SIZE;

        // draw numbers on each vehicle
        graphics.setColor(Color.WHITE);
        graphics.drawString(vehicle.toString(), x, y + SQUARE_SIZE - 1);
        graphics.setColor(Color.BLACK);
        graphics.drawString(vehicle.toString(), x + 1, y + SQUARE_SIZE);

        // draw arrow on vehicle for its direction
        final Direction dir = vehicle.getDirection();
        int dx = (SQUARE_SIZE - MARKER_SIZE) / 2;
        int dy = dx;

        switch (dir) {
            case WEST:
                dx = 0;
                break;

            case EAST:
                dx = SQUARE_SIZE - MARKER_SIZE;
                break;

            case NORTH:
                dy = 0;
                break;

            case SOUTH:
                dy = SQUARE_SIZE - MARKER_SIZE;
                break;

            default:
        }

        x += dx;
        y += dy;

        graphics.setColor(Color.YELLOW);
        graphics.fillOval(x, y, MARKER_SIZE, MARKER_SIZE);
    }

    /**
     * Sets the paint color appropriately for the current lights.
     * 
     * @param light The Light to base the color on.
     */
    private void setLightColor(final Light light) {
        switch (light) {
            case GREEN:
                myLightColor = Color.GREEN.darker();
                break;

            case RED:
                myLightColor = Color.RED.darker();
                break;

            case YELLOW:
                myLightColor = Color.YELLOW;
                break;

            default:
        }
    }
    
    /**
     * Draws dotted lines on cross walks.
     * 
     * @param graphics The Graphics context to use for drawing.
     * @param x The x-coordinate of the cross walk.
     * @param y The y-coordinate of the cross walk.
     */
    private void drawCrossWalkLines(final Graphics2D graphics,
                                 final int x, final int y) {
        
        final Paint oldPaint = graphics.getPaint();
        graphics.setPaint(Color.WHITE);

        final int leftx = x * SQUARE_SIZE;
        final int topy = y * SQUARE_SIZE;

        final int rightx = leftx + SQUARE_SIZE;
        final int bottomy = topy + SQUARE_SIZE;

        if (isValidIndex(y - 1, x) && myGrid[y - 1][x] == Terrain.GRASS) {
            // GRASS is above. Let's assume GRASS is also below.
            // DRAW lines on left and right of light
            graphics.drawLine(leftx  + (int) (SQUARE_SIZE * CROSSWALK_SCALE), topy,
                                 leftx  + (int) (SQUARE_SIZE * CROSSWALK_SCALE), bottomy);
            graphics.drawLine(rightx - (int) (SQUARE_SIZE * CROSSWALK_SCALE), topy,
                                 rightx - (int) (SQUARE_SIZE * CROSSWALK_SCALE), bottomy);
        }
        

        if (isValidIndex(y, x - 1) && myGrid[y][x - 1] == Terrain.GRASS) {
            // GRASS is left. Let's assume GRASS is also right.
            // DRAW lines above and below light
            graphics.drawLine(leftx, topy + (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                 rightx, topy + (int) (SQUARE_SIZE * CROSSWALK_SCALE));
            graphics.drawLine(leftx, bottomy - (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                 rightx, bottomy - (int) (SQUARE_SIZE * CROSSWALK_SCALE));
        }

        graphics.setPaint(oldPaint);
    }
    
    /**
     * Draws dotted lines on streets.
     * 
     * @param graphics The Graphics context to use for drawing.
     * @param x The x-coordinate of the street.
     * @param y The y-coordinate of the street.
     */
    private void drawStreetLines(final Graphics2D graphics,
                                 final int x, final int y) {
        
        final Paint oldPaint = graphics.getPaint();
        graphics.setPaint(Color.YELLOW);

        final int leftx = x * SQUARE_SIZE;
        final int topy = y * SQUARE_SIZE;
        final int centerx = leftx + SQUARE_SIZE / 2;
        final int centery = topy + SQUARE_SIZE / 2;
        final int rightx = leftx + SQUARE_SIZE;
        final int bottomy = topy + SQUARE_SIZE;

        if (isValidIndex(y - 1, x) && myGrid[y - 1][x] == Terrain.STREET) {
            graphics.drawLine(centerx, centery, centerx, topy); // above
        }
        if (isValidIndex(y + 1, x) && myGrid[y + 1][x] == Terrain.STREET) {
            graphics.drawLine(centerx, centery, centerx, bottomy); // below
        }
        if (isValidIndex(y, x - 1) && myGrid[y][x - 1] == Terrain.STREET) {
            graphics.drawLine(centerx, centery, leftx, centery); // left
        }
        if (isValidIndex(y, x + 1) && myGrid[y][x + 1] == Terrain.STREET) {
            graphics.drawLine(centerx, centery, rightx, centery); // right
        }

        graphics.setPaint(oldPaint);
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

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getNewValue() instanceof final RoadRageEvent roadRageEvent) {
            switch (roadRageEvent) {
                case final RoadRageEvent.GridChanged e -> myGrid = e.grid();
                case final RoadRageEvent.LightChanged e -> setLightColor(e.newLight());
                case final RoadRageEvent.VehiclesChanged e -> myVehicles = new ArrayList<>(e.vehicles());
                case final RoadRageEvent.TimeChanged e -> myTimestep = e.newTime();
            }
            repaint();
        }
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
        myDebugFlag = ((JToggleButton) event.getSource()).isSelected();
        repaint();
    }
    
} // end class RoadRagePanel

