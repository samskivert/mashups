# Grid Poker

The elements being mashed up here are:

  * Poker hand creation mechanic
  * Tile laying / grid building (ala Carcassonne)

This feels more like a traditional board game, but I'm running with that rather than trying to
video-game-ify it. The general structure I came up with is as follows:

  * We use one deck of 52 cards, shuffled naturally
  * The top card is flipped over and placed in the center of the board to start
  * On a player's turn, the flip over the top card and place it somewhere on the grid; it has to
    abut (on N, E, W, S edge) a card already on the board
  * If the new card is part of any valid "poker hand" horizontally or vertically, that hand
    immediately scores points for the player
  * After the last card is played, the player with the most points wins

Note that "hands" are two to five adjacent cards, five adjacent cards from which the best cards are
chosen. So two, three and four of a kind have to be adjacent, as do flushes and straights. If a
non-matching card is interspersed, that terminates the hand. Accordingly, three and four card
versions of straights and flushes are also scored.

This approach means that one player could play a pair, then another could build it to three of a
kind, then another to four of a kind (and similarly with straights and flushes), which seems like
it could result in interesting strategy. I also think it will be interesting to attempt to make
plays that score both horizontally and vertically.

## Scoring

The scoring for each poker hand is as follows:

  *   1 - Pair
  *   2 - Two pair
  *   3 - Three card straight
  *   4 - Three card flush
  *   5 - Three of a kind
  *   7 - Three card straight flush
  *  10 - Four card straight
  *  12 - Four card flush
  *  14 - Four card straight flush
  *  16 - Four of a kind
  *  20 - Five card straight
  *  25 - Five card flush
  *  30 - Full house 
  *  40 - Straight flush
  * 100 - Royal flush

I'll do some math later to figure out the total number of ways to construct each card sequence and
revamp the scores based on that.

## Variations

A few variations I might explore:

  * Give each player a "hand" of three cards from which to choose their next play (could also do
    the Carcassonne thing of making them play two cards from the hand before filling it back up to
    three cards)
  * Provide a bonus or multiplier when playing a card that scores both horizontally and vertically
  * Use a "stripped deck" (7 to Ace) for faster, possibly higher scoring (per card played), games
  * Provide one or more wildcards; could make it remain "wild" on the board, or have the player
    choose the card (and suit) before placing it; the former would be more powerful but it would be
    power that could be leveraged by either player
