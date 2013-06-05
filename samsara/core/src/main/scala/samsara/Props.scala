//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

/** A body with a foot print (basically a prop). */
trait Footed extends Bodied {
  val footprint :Seq[Coord]
}

abstract class Prop (val width :Int, val height :Int) extends Entity with Bodied with Footed {
  val footprint = Coord.region(0, 0, width, height)
}

class Tree2 (val start :Coord) extends Prop(2, 2) {
  val viz = Viz(2, 2).circleSF(1/2f, 1/2f, 1/2f, 0xFFCC6600)
}
class Tree4 (val start :Coord) extends Prop(4, 4) {
  val viz = Viz(4, 4).circleSF(1/2f, 1/2f, 1/2f, 0xFF663333)
}
