//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.TreeSet;

import playn.core.Image;
import playn.core.Platform;

import react.Function;
import react.IntValue;
import react.RSet;

import tripleplay.util.TypedStorage;

/** Keeps track of win/loss history. */
public class History {

  public static final class Game implements Comparable<Game> {
    public final long completed;
    public final int[] scores;

    public Game (long completed, int[] scores) {
      this.completed = completed;
      this.scores = scores;
    }

    public boolean win () { return scores[0] > scores[1]; }
    public boolean loss () { return scores[1] > scores[0]; }

    public Image icon (Media media) {
      if (win()) return media.smile;
      else if (loss()) return media.frown;
      else return null;
    }

    @Override public int compareTo (Game other) {
      if (completed < other.completed) return -1;
      else if (completed > other.completed) return 1;
      else return 0;
    }
  }

  /** The total number of wins. */
  public final IntValue wins;

  /** The total number of losses. */
  public final IntValue losses;

  /** A set of recently played games (sorted oldest to newest). */
  public final RSet<Game> recents;

  public History (Platform plat) {
    _plat = plat;
    _ts = new TypedStorage(plat.log(), plat.storage());
    wins = _ts.valueFor("wins", 0);
    losses = _ts.valueFor("losses", 0);
    recents = _ts.setFor("recents", DECODE, ENCODE, new TreeSet<Game>());
  }

  /** Records a completed game to the history. */
  public void noteGame (int winIdx, int[] scores) {
    // increment wins/losses as appropriate
    switch (winIdx) {
    case 0: wins.increment(1); break;
    case 1: losses.increment(1); break;
    default: break; // nada
    }

    // add the game to the recent games list, making room if needed
    if (recents.size() == MAX_RECENT_GAMES) recents.remove(recents.iterator().next());
    recents.add(new Game(System.currentTimeMillis(), scores));
  }

  private Function<String,Game> DECODE = new Function<String,Game>() {
    public Game apply (String data) {
      try {
        String[] bits = data.split("\t");
        int[] scores = new int[bits.length-1];
        for (int ii = 0; ii < scores.length; ii++) scores[ii] = Integer.parseInt(bits[ii+1]);
        return new Game(Long.parseLong(bits[0]), scores);
      } catch (Throwable t) {
        _plat.log().warn("Failure decoding game '" + data + "'", t);
        return new Game(0, new int[] { 0, 0 });
      }
    }
  };

  private Function<Game,String> ENCODE = new Function<Game,String>() {
    public String apply (Game game)  {
      StringBuilder buf = new StringBuilder();
      buf.append(game.completed);
      for (int score : game.scores) buf.append("\t").append(score);
      return buf.toString();
    }
  };

  private final Platform _plat;
  protected final TypedStorage _ts;
  protected static final int MAX_RECENT_GAMES = 15;
}
