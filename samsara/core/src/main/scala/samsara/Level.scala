//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

trait Level {

  val terrain :Array[Terrain]

  def createEntities (jiva :Jivaloka)
}

/** Level-related static data. */
object Level {
  val width = 8
  val height = 12

  def random () = new Level {
    val terrain = Array.fill[Terrain](width*height)(Dirt)

    def createEntities (jiva :Jivaloka) {
      jiva.add(new Tree2(Coord(0, 4)))
      jiva.add(new Frog(Coord(3, 7)))
      jiva.add(new Spider(Coord(2, 4)))
      jiva.add(new Tree4(Coord(6, 2)))
      jiva.add(new Nest(Coord(Level.width/2, Level.height-1), 4))
      // TODO
    }
  }
}
