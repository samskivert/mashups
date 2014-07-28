//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pythagoras.f.FloatMath;
import pythagoras.f.Points;
import react.RList;
import react.RMap;

/**
 * Aggregates all the elements that make up the environment in which the game is played. A zone is a
 * (logically) rectangular region (though the edges may be non-linear through the use of void
 * terrain), each location in the region is assigned a {@link Terrain}, the region is populated with
 * immobile {@link Prop}s (which can have size greater than 1x1), and mobile {@link Actor}s (which
 * do not have size greater than 1x1).
 */
public class Zone {

  /** The width and height of the zone grid. */
  public final int width, height;

  /** Terrain assignments for all locations in the zone. */
  public final Terrain[] terrain;

  /** The location at which the camera starts. */
  public final Loc start;

  /** All props currently in the zone, mapped by origin. */
  public final TreeMap<Loc,Prop> props = new TreeMap<Loc,Prop>();

  /** All actors in the zone, mapped to their location. */
  public final RMap<Actor,Loc> actors = RMap.create();

  /** A mapping from loc to the actor at that location. */
  public final Map<Loc,Actor> actorsByLoc = new HashMap<Loc,Actor>();

  public Zone (int width, int height, Terrain[] terrain, Loc start) {
    this.width = width;
    this.height = height;
    this.terrain = terrain;
    this.start = start;

    // listen for actors changes and update actorsByLoc
    actors.connect(new RMap.Listener<Actor,Loc>() {
      public void onPut (Actor actor, Loc loc) { actorsByLoc.put(loc, actor); }
      public void onRemove (Actor actor, Loc oldLoc) { actorsByLoc.remove(oldLoc); }
    });
  }

  public Terrain terrain (Loc loc) { return terrain(loc.x, loc.y); }
  public Terrain terrain (int x, int y) {
    return (x < 0 || y < 0 || x >= width || y >= width) ? Terrain.VOID : terrain[y*width+x];
  }

  public static Zone makeTest () {
    int xsize = 48, ysize = xsize;
    Terrain[] terrain = new Terrain[xsize*ysize];
    // fill it with ocean
    for (int ii = 0; ii < terrain.length; ii++) terrain[ii] = Terrain.OCEAN;
    // put a big island in the middle
    int cx = xsize/2, cy = ysize/2;
    for (int yy = 0; yy < ysize; yy++) {
      for (int xx = 0; xx < xsize; xx++) {
        int dist = FloatMath.round(Points.distance(xx, yy, cx, cy));
        if (dist < 10) terrain[yy*xsize+xx] = Terrain.DIRT;
      }
    }
    return new Zone(xsize, ysize, terrain, new Loc(cx, cy));
  }
}
