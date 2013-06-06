//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

abstract class Prop extends Entity with Footed

class Tree2 (val start :Coord) extends Prop {
  val foot = Coord.square(2)
  val viz = Viz(2, 2).circleSF(1/2f, 1/2f, 1/2f, 0xFFCC6600)
}

class CornerTree (val corner :Int) extends Prop {
  val foot = Coord.square(2)
  val start = corner match {
    case 0 => Coord(0, 0)
    case 1 => Coord(Level.width-2, 0)
    case 2 => Coord(Level.width-2, Level.height-2)
    case 3 => Coord(0, Level.height-2)
  }
  val viz = corner match {
    case 0 => Viz(2, 2).circleSF(0, 0, 1, 0xFF663333)
    case 1 => Viz(2, 2).circleSF(1, 0, 1, 0xFF663333)
    case 2 => Viz(2, 2).circleSF(1, 1, 1, 0xFF663333)
    case 3 => Viz(2, 2).circleSF(0, 1, 1, 0xFF663333)
  }
}

class HalfTree (val left :Boolean, y :Int) extends Prop {
  val foot = Coord.rect(2, 4)
  val start = if (left) Coord(0, y) else Coord(Level.width-2, y)
  val viz = if (left) Viz(2, 4).circleSF(0, 1/2f, 1, 0xFF663333)
            else      Viz(2, 4).circleSF(1, 1/2f, 1, 0xFF663333)
}
