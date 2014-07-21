# Ziggurat

The elements being mashed up here are:

  * Turn-based RPG-style battles (plus rock-paper-scissors) (ala Fire Emblem)
  * 1/2 hour per day of "garden tending"; slow build up of "house" (ala Animal Crossing)

This is a curious combination, but that's exactly why this mashup approach is interesting. I
certainly would not have spontaneously thought to try these elements together.

## Design Notes

### Rock/paper/scissors

There's no "intuitive" set of units that have rock/paper/scissors characteristics (for example,
Fire Emblem's sword/spear/axe trichotomy is pretty arbitrary). Elements aren't really rock paper
scissors-y: "air, water, fire" or "air, water, earth", which is strong or weak against which? Water
beats fire, but does fire beat air? Why?

So I'm going to avoid any sort of concrete fiction and just have a graphical emblem for each unit
type which directly conveys which units this unit is strong against and which it is weak against.
For example:

```
 +-+-+  +-+-+ +-+-+
 \r|*/  \g|r/ \*|g/
  \g/    \*/   \r/
   +      +     +
```

The * indicates the type of this unit, a (r)ed tint shows the unit type against which this unit is
weak and a (g)reen tint shows the unit type against which this unit is strong.

I may still name the three unit types (perhaps `\, / and |`), but just for expository/shorthand
purposes. I'll be sure the full emblem is always visible when you need to decide whether a unit
should attack another unit or is safe from attack from another unit. And I'll also show a + or - in
the battle UI as appropriate.

### Garden tending

I'm going to fudge a little with the Animal Crossing influence. The mechanic that turned up was a
combination of two elements (I'm not sure why I combined them together, but whatever):

1. Only providing 1/2-1 hour of "meaningful" gameplay per day. You can noodle around beyond that,
and there are some grindy things you can do beyond that main experience, but you have to wait until
the next real day (or longer) for the main bits to progress. In AC, this includes: fruit harvesting
(fruit grows on a tree once every three days), buying furniture etc. from Nook's, clothing from
Able Sisters (inventory refreshes each day), finding fossils/oids, finding the bell rock.

2. Build up of house. In AC you can buy things for your house, and you pay off the loans needed to
expand your house (only to then get the next expansion, but hey, you need space for all your shit).
You can only get so many new things for your house per day (due to a limited selection at Nook's
and a limit of ordering five items per day from the Happy Home Showcase). Normally it takes quite a
few days to earn the bells to pay back a loan for a home expansion, but you could grind your way
through that through endless fishing or bug catching or whatnot, but that's definitely not the
"intended" way to play the game. The intent is for you do to a few high-value things once per day
and upgrade your house every week or two.

Anyway, these two things are somewhat related by the idea of time limiting your progress through
the game. There's another mechanic in AC that I think is really cool (which is sort of also in my
list, which is why I'm fudging a little bit), which is the tie to real-time cycles. There's a
day/night cycle in the game (which matches actual day and night according to your DS time
settings), so stores are closed after 9 or 10pm, and they don't open until 8 or 9am (modulo minor
tweaks you can apply to your town). AC also has weekly cycles in that certain visitors come to your
down on certain days of the week. And it has yearly cycles in the form of seasons and special
annual holidays.

I want to introduce a time-limiting factor into this prototype by splitting the game into seven
parts, each of which is only available on a given day of the week. These seven parts will each
yield artifacts which you'll use to build your "house" (not sure what that is yet). So you'll need
to complete seven quests to get all the things you need to build your house, and since this is a
prototype, I think that's going to be the whole enchilada.

However, I think I can add additional replay interest by structuring things such that the order in
which you complete those seven quests has some interesting impact on the final structure of your
"house" (i.e. which "ending" you get), so if you do M/T/W/R/F/S/S then you get one ending, but if
you do M/W/F/S/T/R/S or whatever, you get a different ending. Clearly I'm not going to do all
possible permutations, but I can do a few. Or maybe the variation automatically comes out of
whatever your house is, maybe the order is some seed into an algorithm that does interesting and
unexpected things.

I'll probably either limit you to one "attempt" per day on that day's artifact, or tier the game so
that you can fail, or achieve a bronze, silver or gold result from your performance, which could
motivate you to replay a day to get a better result.

I'll also probably let you run multiple games simultaneously. So if you can't make progress on one
game, you can start another, or maybe you've got seven or eight games going, and on one of them
you're waiting to get a gold on Thursday or whatever, but in various others you can make progress
today, which is a Tuesday. I don't want to punish players who want to play a lot, I just want
someone who's looking for a modest time investment to have a straightforward goal they can attempt
and then put the game away until the next day, satisfied that they've accomplished today's goal.

To that end, I'd like a single day's quest to be a twenty to thirty minute experience. Since
everything I do these days is for phones, I'll aim for something that can be paused at any time and
picked back up without major disruption. If you're "going for the gold" you may have to set aside
the time to concentrate and put in a stellar performance, but if you're just looking for a
distraction, you can pop in, make a few moves, and then put it away until later.

### Unit variation

The core gameplay will be turn-based battles with a smallish number of units (5-10) versus a less
smallish number of computer controlled enemy units (they will necessarily be more numerous and more
expendible because the AI won't be awesome and I'll have to make it up in volume).

Since I'm keeping things abstract, I'm going to derive my unit types based on variation of four
attributes: movement, attack strength, defense strength, and attack range. There will be a
baseline for each attribute, and unit variation will mean increasing one attribute while
decreasing another.

The interesting variations are:

```
Move  Attack  Defend  Range
o     o       o       o        # default values across the board
+     o       -       o        # moves far, vulnerable
+     -       o       o        # moves far, weak attack
o     +       -       o        # hits hard, vulnerable
-     +       o       o        # hits hard, moves slow
-     o       +       o        # thick armor, moves slow
o     o       -       +        # long range, vulnerable
-     o       o       +        # long range, moves slow
```

I'll omit (what I consider to be) the non-interesting varations:

```
Move  Attack  Defend  Range
+     o       o       -        # reduced range isn't meaningful
o     +       o       -        # because "default" range is 1
o     o       +       -        # ...
o     -       +       o        # weak attack, thick armor = probably boring
o     -       o       +        # weak attack, long range = boring
```

Movement will probably be three by default, one or two for weak movement, and four or five for
strong movement. Range will be one by default and two or three for long range. Attack and defense
will likely range down by 50% and up by 50% for weak and strong variants. I'll start with those
values anyway and tweak them based on play.

#### Unit attribute powerup

Neat idea: powerups in the world (move unit onto them to acquire) which cause a unit's bonus to
double for a limited time. Maybe three or four turns. So for example, movement could normally be
three, and strong movement units move four, but if you get the powerup, you move five. Range of one
by default, two for strong range units, and three when strong range units have the powerup.
Attack/defense bonus would go from 50% to 100%. If the "balanced" unit picked up the bonus, perhaps
it would pick an attribute at random to improve (to the basic bonus level, not doubled), or maybe
you get to choose. The powerup would not influence the unit's weakness.

### Daily variation

One of the things that I think should change every day is the terrain/dungeon layout. I'll almost
certainly go with randomly generated levels on which the quest plays out, so that means I'll have
to come up with seven different level generation variants. If that proves too onerous, maybe I'll
come up with three or four and then vary something else to get the necessary seven variants. I
could also come up with six variants and have Sunday choose a variation at random.

Some level generation ideas:

  * Nethack-style rooms with connecting passages. This is tried and true, and provides the only
    approach to fog of war that I don't find really annoying (rooms dark until you enter them,
    then the whole room becomes and stays visible).

  * Islands in a sea: the arena is a bunch of little islands, and you move between them via
    teleporters or some other warpy-thing. This makes it differ from rooms and corridors in two
    ways: some islands will be close enough to one another to be reachable by range units, and the
    connection geometry can be weird: teleporters don't have to link to the nearest island. The
    teleporter destinations could also be non-obvious and have to be discovered.

  * Field with barriers: the arena is generally open, but there are barriers to hide behind and
    which can necessitate some maneuvering to get where you want to go.

  * Maze: a dungeon that is almost entirely passageways, with perhaps some double-wide passages,
    and plazas, but substantially different from rooms and passages. This might be annoying with a
    lot of units, or it might be interesting in that you have to split your forces up more than
    you would normally.

  * Hedge maze: geometrically like maze, but with low walls, which range units can attack over.
    Perhaps the walls could be targeted as well (or some spots in the walls) to open up passages.
    This could equally apply to normal mazes.

  * Rivers and bridges: arena is broken up into "room-like" areas by myriad rivers, and rooms are
    joined by bridges. This is kind of like hedge maze with rooms, in that range units can attack
    from island to island, but it's more connected than maze in that there will usually be a
    bridge to get from one island to another and there will be more room to arrange units into
    different formations.

I could also vary the "goal" in the levels. Either on a different schedule, just to keep different
games interesting, or have particular level types have particular goals as well.

Some goal ideas:

  * Defeat all the enemies. Tried and true. Usually fun and challenging.

  * Get to (by defeating some, but not necessarily all minions), and defeat the boss. See above.

  * Collect N things from around the board.

  * Take over, and hold, N structures around the board. The difference from the collection goal is
    that in this goal, a structure could be taken back by the enemy if you leave it undefended and
    they move in and retake it. "Taking" a structure would also probably be Advance Wars style,
    in that you have to park a unit on the structure for a couple of turns.

  * Destroy N structures around the board. A compromise between the above two. You have to spend
    some time/effort destroying a structure, rather than just hopping onto it and picking it up,
    but once it's destroyed, you don't have to worry about the enemy bringing it back.

  * Take over the enemy base. This is a bit different from "take over N" because the enemy would
    naturally put a lot of effort into protecting its base. So it's more like "defeat all enemies"
    with more opportunity for cleverness.

  * Chaperone an NPC unit from one end of the board to the other. Naturally the unit is relatively
    weak and must be protected from harm.
