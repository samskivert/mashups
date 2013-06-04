//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import com.artemis.World

trait Level {

  val terrain :Array[Terrain]

  def createEntities (world :World)
}

/** Level-related static data. */
object Level {
  val width = 8
  val height = 12

  def random () = new Level {
    val terrain = Array.fill[Terrain](width*height)(Dirt)

    def createEntities (world :World) {
      Entities.addProp(world, 0, 4, 2, 2, Viz.circle(2),
                       Seq(Coord(0, 0), Coord(0, 1), Coord(1, 0), Coord(1, 1)))
      Entities.addProp(world, 3, 7, 2, 2, Viz.circle(2),
                       Seq(Coord(0, 0), Coord(0, 1), Coord(1, 0), Coord(1, 1)))
      Entities.addProp(world, 6, 2, 4, 4, Viz.circle(4),
                       Seq(Coord(1, 0), Coord(2, 0),
                           Coord(0, 1), Coord(1, 1), Coord(2, 1), Coord(3, 1),
                           Coord(0, 2), Coord(1, 2), Coord(2, 2), Coord(3, 2),
                           Coord(1, 3), Coord(2, 3)))
      Entities.addPlayer(world, Level.width/2, Level.height-1, Viz.circle(1))
      // TODO
    }
  }
}
