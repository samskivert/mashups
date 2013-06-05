//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

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
      world.add(new Prop(Coord(0, 4), 2, 2, Viz.circle(2)))
      world.add(new Prop(Coord(3, 7), 2, 2, Viz.circle(2)))
      world.add(new Prop(Coord(6, 2), 4, 4, Viz.circle(4)))
      world.add(new Player(Coord(Level.width/2, Level.height-1), Viz.circle(1)))
      // TODO
    }
  }
}
