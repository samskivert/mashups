# Samsara

The elements being mashed up here are:

  * Reincarnation with incremental environment changes for progress (ala Shirin, Roguelikes)
  * Player and enemies each move one square per turn (ala Nethack)

It's an odd coincidence that these both stem from the roguelike tradition, but that's how the RNG
crumbles.

## Design

The design I came up with for this one is as follows:

  * You play a fruit fly and are trying to transport your genes to Fruit Fly Nirvana
  * The mundane world you are trying to escape is a series of levels; each level is an 8x12 grid of
    squares
  * You are the spawn of laboratory fruit flies who have been genetically modified to be unable to
    fly; you can only "hop" from one square to the next
  * Your goal is to move from the bottom row of a level to the "exit" in the top row of the level
    (which takes you to the next level; nirvana is reached via the exit of level 99)
  * Various obstacles make this task an interesting challenge
  * Your lifespan permits you a fixed number of moves before you kick the bucket
  * On every level, there is a prospective mate; if you can position yourself next to them, you
    mate and generate a bundle of eggs
  * When you inevitably die (be that from old age or a hostile environment), the torch is picked up
    by one of your offspring, who hatches from your most recent mating site
  * If you exhaust the supply of eggs at a mating site, you may have to roll back to previous
    levels to resume your quest

## Obstacles

Obstacles are both passive and active.

Passive obstacles are mainly just impassable squares, which make the geometry of the level
interesting, though we dress them up as tree trunks or stones. But there are also some gameplay
opportunities in passive obstacles like a trickle of water which can be bridged by picking up a
leaf from somewhere on the level and dropping it onto the water to create a bridge.

Active obstacles are things like spiders, ants, frogs, etc. which amy move around the level
themselves and which might crush or eat you if you are not careful.

Currently implemented passive obstacles:

  * trees
  
Currently implemented active obstacles:

  * frogs - eat you if you pass in the 2x2 region in front of them, turn left or right if you step
    into the 1x2 region to their left or right
  * spiders - eat you if you step within 2 squares of them (as calculated by Manhattan distance),
    otherwise they move one square randomly; they also sleep for a random period after eating you
    to give your progeny a chance to slip past

## Seasons

To give the game a nice rhythm, the environment cycles through the four seasons, each of which has
characteristic terrain, passive and active obstacles. Each season lasts for five levels, which
means that you cycle through a full year every twenty levels, for a total of five full cycles on
your way to Nirvana. It could also be interesting to "blend" between two seasons as you pass from
one to the next (on the last level of the old season and first level of the new season).

Seasons are not yet implemented, but I have some ideas for seasonal variation.

### Fall

  * Primarily brown terrain, with spots of orange and green
  * Streams of water that must be traversed by placing leaves on them

### Winter

  * Ice terrain which causes the fly (and other critters?) when landing on it

### Spring

  * Vibrant green terrain with splotches of brown dirt
  * Puddles of water as obstacles (made traversable by leaves)

### Summer

  * Mostly green terrain with bits of yellow and brown
  * Spiders come out in summer
  * Streams (as in fall) with fish which swim back and forth and eat you if you try to cross a leaf
    while a fish is adjacent to it

## Other disorganized notes

### Mechanics

  * allow kamikaze into sleeping spider; kills you and spider?
  * make mates "edible"
  * don't position any MOBs within one square of a mate?
  * make frogs sleep a bit after eating?
  * require you to "walk" toward mate to trigger mating?
  * if you enter a screen and someone is there (spider) move to nearest empty spot?

### Critters

  * beetle - what would it do?
  * ants - march across/down the screen, splat you if you are on the tile into which they are moving

### FX/Pretty

  * trees cast nice bushy/leafy shadow on the ground
  * as fly hops from tile to tile, scale the fly image up as he moves toward the camera then back
    down as he plops onto target (use gnatty buzz SFX for each hop)

### Terrain

  * double (16x24) or quadruple (32x48) the resolution of the terrain to allow for more organic
    looking coloration, and then define passability by the "majority content" of the 8x12 square
  * terrain coloration and distribution would be an interesting place for the "blending" between
    seasons

### Controls

  * PC - arrow keys; already implemented, quite comfortable
  * Mobile - swipe (from fly, or from anywhere really) in the direction of motion
  * Mobile - tap top/left/right/bottom quadrant of screen to move in that direction
  * other?
