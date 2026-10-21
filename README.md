# TCSS 305 – Assignment 2: Road Rage

**UW Tacoma | Autumn 2026**

## Assignment Instructions

Assignment 2 comes in two parts, both built in this one repository:

- Assignment 2a: https://cfb3.github.io/TCSS305-26-AUTUMN/assignments/a2a/
- Assignment 2b: https://cfb3.github.io/TCSS305-26-AUTUMN/assignments/a2b/

The assignment pages are the full instructions. This file is only a map of the project.

## Project Structure

```
src/edu/uw/tcss/
├── app/
│   └── RoadRageMain.java       (application entry point - provided)
├── io/
│   └── FileLoader.java         (map and configuration loading - provided)
├── logic/                      (simulation, enums, and events - provided)
├── model/
│   ├── Vehicle.java            (interface - provided)
│   ├── Truck.java              (complete this stub)
│   └── Car.java                (complete this stub)
└── view/                       (GUI components - provided)

test/edu/uw/tcss/model/
├── TruckTest.java              (provided - do not modify)
└── CarTest.java                (provided - do not modify)

config/
├── vehicles.txt                (which vehicles appear in the simulation)
└── map.txt                     (which map in maps/ is loaded)

icons/                          (vehicle images - provided)
maps/                           (map files - provided)

executive-summary.md            (your submission notes)
README.md                       (this file)
```

## Running the Application

1. Run `RoadRageMain.java`.
2. Use the GUI controls to start and stop the simulation, change its speed, and turn on debug mode.
3. To try a different map, change the map name in `config/map.txt` and restart.

## Running Tests

1. Right-click the `test` folder in IntelliJ.
2. Select **Run 'All Tests'**.
