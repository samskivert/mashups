//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.util.Random

abstract class Prop extends Entity with Footed {
  override def depth = PropDepth
}

abstract class Tree extends Prop {
  def treeViz (w :Int, h :Int, cx :Float, cy :Float, r :Float) = {
    val browns = Random.shuffle(Browns)
    val rings = 3+Random.nextInt(math.max(w, h)*2)
    val dr = r / (rings+1)
    val viz = Viz(w, h).circleSF(cx, cy, r, 0xFF402105, browns(1))
    (viz /: (1 to rings)) { (v, n) =>
      val tr = r - dr*n - dr/2 + Random.nextFloat*dr
      v.circleF(cx, cy, tr, browns(n % browns.length))
    }
  }

  val Browns = Seq(0xFF7B3F00, 0xFF9F703A, 0xFF8B4500, 0xFF8B5A2B, 0xFF603311, 0xFF8B4513,
                   0xFF733D1A, 0xFF6B4226, 0xFF5C3317, 0xFF964514, 0xFF87421F, 0xFF9C661F)
}

class Tree2 (val start :Coord) extends Tree {
  val foot = Coord.square(2)
  def viz = treeViz(2, 2, 1/2f, 1/2f, 1/2f)
}

class CornerTree (val corner :Int) extends Tree {
  val foot = Coord.square(2)
  val start = corner match {
    case 0 => Coord(0, 0)
    case 1 => Coord(Level.width-2, 0)
    case 2 => Coord(Level.width-2, Level.height-2)
    case 3 => Coord(0, Level.height-2)
  }
  def viz = corner match {
    case 0 => treeViz(2, 2, 0, 0, 1)
    case 1 => treeViz(2, 2, 1, 0, 1)
    case 2 => treeViz(2, 2, 1, 1, 1)
    case 3 => treeViz(2, 2, 0, 1, 1)
  }
}

class LeftTree (y :Int) extends Tree {
  val foot  = Coord.rect(2, 4)
  val start = Coord(0, y)
  def viz   = treeViz(2, 4, 0, 1/2f, 1)
}

class RightTree (y :Int) extends Tree {
  val foot  = Coord.rect(2, 4)
  val start = Coord(Level.width-2, y)
  def viz   = treeViz(2, 4, 1, 1/2f, 1)
}
