//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

sealed trait Terrain {

  val passable :Boolean
  val color :Int
}

object Dirt extends Terrain {

  val passable = true
  val color = 0xFF996633
}

object Grass extends Terrain {

  val passable = true
  val color = 0xFF336600
}

object Water extends Terrain {

  val passable = false
  val color = 0xFF0066FF
}
