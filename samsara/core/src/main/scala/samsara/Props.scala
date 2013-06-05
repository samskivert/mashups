//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

abstract class Prop extends Entity with Bodied

class Tree2 (val start :Coord) extends Prop {
  val foot = Coord.square(2)
  val viz = Viz(2, 2).circleSF(1/2f, 1/2f, 1/2f, 0xFFCC6600)
}
class Tree4 (val start :Coord) extends Prop {
  val foot = Coord.square(4)
  val viz = Viz(4, 4).circleSF(1/2f, 1/2f, 1/2f, 0xFF663333)
}
