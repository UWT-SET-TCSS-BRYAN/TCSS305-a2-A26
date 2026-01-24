# TCSS 305 – Assignment 2: Road Rage

**UW Tacoma | Winter 2026**

## Assignment Instructions

Full instructions available at:
[Assignment 2: Road Rage](https://cfb3.github.io/TCSS305-GUIDES/assignments/a2/)

## Project Structure

```
src/edu/uw/tcss/
├── app/
│   └── RoadRageMain.java       (application entry point - provided)
├── io/
│   └── FileLoader.java         (file loading - provided)
├── logic/
│   ├── Direction.java          (enum - provided)
│   ├── Light.java              (enum - provided)
│   ├── Terrain.java            (enum - provided)
│   ├── Vehicle.java            (interface - provided)
│   └── RoadRageEvent.java      (sealed interface - provided)
├── model/
│   ├── Truck.java              (complete this stub)
│   ├── Car.java                (complete this stub)
│   ├── Bicycle.java            (complete this stub)
│   ├── AbstractVehicle.java    (create after implementing above)
│   ├── Taxi.java               (create from scratch)
│   ├── Atv.java                (create from scratch)
│   └── Human.java              (create from scratch)
└── view/                       (GUI components - provided)

test/edu/uw/tcss/model/
├── TruckTest.java              (partial - complete this)
├── CarTest.java                (partial - complete this)
├── BicycleTest.java            (partial - complete this)
├── TaxiTest.java               (create this)
├── AtvTest.java                (create this)
└── HumanTest.java              (create this)

config/
└── vehicles.txt                (vehicle configuration)

icons/                          (vehicle sprites - provided)
maps/                           (map files - provided)

project root/
├── executive-summary.md        (your submission notes)
└── README.md                   (this file)
```

## Getting Started

1. **Phase 1 - Feel the Pain:**
   - Implement `Truck.java` (complete the stub)
   - Implement `Car.java` (complete the stub)
   - Implement `Bicycle.java` (complete the stub)
   - Complete the test files for each vehicle

2. **Phase 2 - Discover Inheritance:**
   - Notice the code duplication across your three classes
   - Create `AbstractVehicle.java` to extract common code
   - Refactor Truck, Car, and Bicycle to extend AbstractVehicle
   - Verify all tests still pass

3. **Phase 3 - Apply What You Learned:**
   - Implement `Taxi.java` extending AbstractVehicle
   - Implement `Atv.java` extending AbstractVehicle
   - Implement `Human.java` extending AbstractVehicle
   - Create tests for each new vehicle
   - Uncomment each vehicle in `config/vehicles.txt` as you complete it

## Running the Application

1. Ensure at least Truck, Car, and Bicycle compile without errors
2. Run `RoadRageMain.java`
3. The simulation should display vehicles moving on the city map
4. Use GUI controls to start/stop, adjust speed, and enable debug mode

## Running Tests

1. Right-click on the `test` folder in IntelliJ
2. Select **Run 'All Tests'**
3. For coverage: Right-click → **Run with Coverage**
4. Pay special attention to testing random behavior (see `TruckTest.java` for examples)

## What to Submit

- **Phase 1:** `Truck.java`, `Car.java`, `Bicycle.java` with completed tests
- **Phase 2:** `AbstractVehicle.java` with refactored child classes
- **Phase 3:** `Taxi.java`, `Atv.java`, `Human.java` with tests
- Updated `executive-summary.md`
- All code must pass Checkstyle validation
