# Bicycle: Behavior Specification

This is a complete specification for one vehicle. Use it as the model for the specs you write
for Taxi, Atv, and Human: the same sections, the same level of detail, and a rule ID on every
statement so that each one can be pointed at by a test.

A good spec answers questions before anyone has to ask them. Where the original request was
vague, the **Decisions** section records what was decided and why.

---

## 1. Identity

| ID | Rule |
|---|---|
| B-1.1 | The class is `Bicycle`, in the package `edu.uw.tcss.model`. |
| B-1.2 | The constructor is `public Bicycle(int x, int y, Direction direction)`. The file loader creates vehicles by reflection and requires exactly this signature. |
| B-1.3 | Mass is **20**. |
| B-1.4 | Disabled duration is **35** updates. |
| B-1.5 | While enabled, the image file name is `bicycle_` followed by the current direction in lowercase, then `.png`. Facing north, that is `bicycle_north.png`. |
| B-1.6 | While disabled, the image file name is `bicycle_disabled.png`. |

## 2. Shared behavior

| ID | Rule |
|---|---|
| B-2.1 | Position, direction, `collide`, `poke`, `isEnabled`, `reset`, and the getters and setters behave exactly as they do for every vehicle, as defined by `AbstractVehicle`. Bicycle adds no state of its own, so it does not override `reset`. |

## 3. Where it can go: `canPass(terrain, light)`

`canPass` answers one question: may the bicycle move onto this terrain while the lights show
this colour? It does not choose a direction.

| Terrain | GREEN | YELLOW | RED |
|---|---|---|---|
| `STREET` | yes | yes | yes |
| `TRAIL` | yes | yes | yes |
| `LIGHT` | yes | no | no |
| `CROSSWALK` | yes | no | no |
| `GRASS` | no | no | no |
| `WALL` | no | no | no |

| ID | Rule |
|---|---|
| B-3.1 | Street and trail are always passable. They have no light, so the light argument is ignored. |
| B-3.2 | A traffic light (`LIGHT`) is passable only when the light is `GREEN`. |
| B-3.3 | A crosswalk (`CROSSWALK`) is passable only when the light is `GREEN`. |
| B-3.4 | Grass and walls are never passable. |

## 4. Which way it faces: `chooseDirection(neighbors)`

`chooseDirection` picks a direction from the terrain on the four sides. **It never looks at the
lights.** If the bicycle picks a direction and the light then forbids moving (section 3), it
waits where it is. It does not turn to go around a red light.

"Straight", "left" and "right" are relative to the direction the bicycle currently faces. The
rules are tried in order, and the first one that produces a direction wins.

| ID | Rule |
|---|---|
| B-4.1 | **Seek a trail.** If straight, left, or right is `TRAIL`, face the first one found, checking straight, then left, then right. |
| B-4.2 | **Otherwise, keep to the road.** Face the first of straight, left, right whose terrain is `STREET`, `LIGHT`, or `CROSSWALK`. |
| B-4.3 | **Otherwise, turn around.** Face the reverse of the current direction. This happens even if the terrain behind is impassable. In that case the bicycle stays put this update and tries again next update. |
| B-4.4 | **Never reverse to reach a trail.** A trail behind the bicycle does not count for B-4.1. Reversing happens only under B-4.3. |
| B-4.5 | `chooseDirection` is deterministic. The same neighbors and the same current direction always give the same answer. |

## 5. Decisions

Each of these was a question the original description left open. The answer is recorded so
that the person implementing it and the person testing it reach the same conclusion.

| Question | Decision | Rule |
|---|---|---|
| Should a bicycle obey lights while on a trail or street? | No. Those squares have no light to obey. | B-3.1 |
| Should it stop for yellow? | Yes, at both traffic lights and crosswalks. Only green lets it through. | B-3.2, B-3.3 |
| When two trails are nearby, which one? | Straight beats left, and left beats right. | B-4.1 |
| If the only trail is behind it, does it turn around for it? | No. That would make it ride back and forth on the same stretch of trail. | B-4.4 |
| If everything ahead is blocked and so is behind, what then? | It still faces backward and waits. Next update it chooses again. | B-4.3 |
| Is any of this random? | No. Unlike Truck, the bicycle always makes the same choice in the same situation, which makes it easy to test exactly. | B-4.5 |

## 6. How to check it

Each rule above should have at least one test that would fail if the rule were broken. Two
examples:

- **B-4.4:** facing north, with `STREET` to the north, west, and east and `TRAIL` to the south,
  `chooseDirection` returns `NORTH`, not `SOUTH`.
- **B-3.2:** `canPass(LIGHT, YELLOW)` returns `false`, and `canPass(LIGHT, GREEN)` returns `true`.

If you cannot write a test for a rule, the rule is not precise enough yet.
