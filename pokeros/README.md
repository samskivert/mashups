# Pokeros

The elements being mashed up here are:

  * Poker hand creation mechanic
  * Tile laying / grid building (ala Carcassonne)

This feels more like a traditional board game, but I'm running with that rather than trying to
video-game-ify it.

## Rules

The initial structure I came up with is as follows:

  * We use one shuffled deck of 52 cards
  * The top card is flipped over and placed in the center of the board to start
  * On a player's turn, the flip over the top card and place it somewhere on the grid; it has to
    abut (on N, E, W, S edge) a card already on the board
  * If the new card is part of any valid "poker hand" horizontally or vertically, that hand
    immediately scores points for the player
  * After the last card is played, the player with the most points wins

Note that "hands" are two to five adjacent cards, not five adjacent cards from which the best cards
are chosen. So two, three and four of a kind have to be adjacent, as do flushes and straights. If a
non-matching card is interspersed, that terminates the hand. Due to this, three and four card
versions of straights and flushes are also scored.

This approach means that one player could play a pair, then another could build it to three of a
kind, then another to four of a kind (and similarly with straights and flushes), which seems like
it could result in interesting strategy. I also think it will be interesting to attempt to make
plays that score both horizontally and vertically.

### Tweaks

After playing a bit with the "play the top card" approach, it became pretty apparent that the best
strategy was just to make the highest scoring play with whatever card you had. So I implemented one
of the variations I mention below, which was to give each player three cards from which to choose
their next play, drawing a new card to refill their hand after playing. This introduced a bit more
planning and strategy, because you could see that you had two or three cards that would work
together and aim to make something good out of them. Of course, the opponent then sometimes thwarts
your plans, but that's to be expected.

## Scoring

The scoring for each poker hand is as follows:

  *  1 - Pair
  *  2 - Three card flush
  *  2 - Three card straight
  *  3 - Two pair
  *  4 - Four card flush
  *  4 - Three of a kind
  *  5 - Four card straight
  *  5 - Full house
  *  8 - Five card flush
  * 10 - Five card straight
  * 10 - Four of a kind
  * 10 - Three card straight flush
  * 20 - Four card straight flush
  * 40 - Five card straight flush
  * 60 - Royal flush

I arrived at these scores by making the AI play thousands of games against itself, ranking plays by
the total number of cards in the play (so it would prefer a longer play to a shorter one). Then I
looked at the relative distribution of hands made and came up with scores roughly based on the
frequency of a given hand compared to the frequency of playing one pair. I then reran the AIs for a
few thousand games ranking hands based on the supplied scores. I expected to have to keep tweaking
and repeating this process, but things stabilized after only one set of adjustments to the scores.

These scores are somewhat compressed for the rarer hands (one royal flush occurred for every 6000
pairs played, but I'm not awarding 6000 points for a royal flush; 60 points is enough to
essentially guarantee a win anyway; straight flushes are also pretty rare).

## Play It

If you have one of them new fangled HTML5 browsers, you can play the prototype
[right here](http://samskivert.github.io/mashups/pokeros/). Good luck!

## Variations

Some variations that I considered and/or implemented:

  * Give each player a "hand" of three cards from which to choose their next play. (I implemented
    this.) I could also do the Carcassonne thing of making them play two cards from the hand before
    filling it back up to three cards. (But not this.)
  * Provide a bonus or multiplier when playing a card that scores both horizontally and vertically.
    (I implemented this. Plays that score both horizontally and vertically are pretty rare, and it
    is a nice way to turn the tables on a game that is going poorly.)
  * Use a "stripped deck" (7 to Ace) for faster, possibly higher scoring (per card played), games.
    (I didn't implement this because games seemed fast enough with a full deck.)
  * Provide one or more wildcards; could make it remain "wild" on the board, or have the player
    choose the card (and suit) before placing it. The former would be more powerful but it would be
    power that could be leveraged by either player. (This might be interesting, but it didn't seem
    sufficiently awesome to bother with.)
