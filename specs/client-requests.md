# Client Requests: Three New Vehicles

These requests come from the client, the Riverbend Transit Lab, which uses Road Rage to show
city planners how different kinds of traffic share the same streets. The client is not a
programmer. They described what they want in their own words.

Your job in Assignment 2a is to turn each request into a behavior specification like
`bicycle.md`: every behavior written as a numbered rule precise enough to test. Where a
request is unclear, decide what the client most likely meant, write the decision down in your
spec's Decisions section, and say why.

The client's wording is the only source of truth you have. Read it carefully.

---

## Taxi

> Taxis are basically cars. Same streets, same weight, and they're out of action for just as
> long after a crash. They drive like cars too: straight ahead if they can, then left, then
> right, and they only turn around when there's nowhere else to go. They stop at red traffic
> lights like everybody else and ignore yellow and green ones.
>
> Where they're different is crosswalks. A cab will go through a crosswalk on yellow or green,
> no problem. On red, the driver waits, but cabbies are impatient: after waiting three ticks
> they just go. If the light changes while they're sitting there, they go on the change. Every
> time a cab pulls up to a red crosswalk, the patience starts over. And when we hit Reset on the
> simulation, every cab should start fresh too.

## ATV

> ATVs go just about anywhere: streets, grass, trails, crosswalks, you name it. The only thing
> that stops one is a wall. They don't pay attention to any lights at all.
>
> Every tick an ATV picks at random between going straight, turning left, or turning right,
> whichever of those isn't a wall. They'd only turn around if they were boxed in by walls on
> all three of those sides. An ATV weighs 40 and is out for 25 ticks after it loses a crash.

## Human

> People on foot stay on the grass. The only time they step off it is to cross at a crosswalk.
> No streets, no trails, and they don't stand in the middle of an intersection.
>
> Mostly they wander. Each tick they pick at random between straight, left, and right,
> wherever there's grass. But if there's a crosswalk right in front of them or off to either
> side, they head for it: straight ahead first, then left, then right. They won't turn all the
> way around just to get to a crosswalk behind them, otherwise they'd pace back and forth
> forever. If the only place they can go is back the way they came, though, they'll turn
> around.
>
> Remember that the crosswalk lights are for the cars. When a crosswalk light is green, the cars
> have the go, so people hold back. On yellow or red, they cross. A person weighs 10 and takes
> 45 ticks to get back on their feet after being hit.
