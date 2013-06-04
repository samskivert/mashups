//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import com.artemis.{Entity, World}

object Entities {

  def addPlayer (world :World, x :Int, y :Int, viz :Viz) = entity(world) { e =>
    e.addComponent(new Body(x, y, 1, 1, viz))
    e.addComponent(new Player())
  }

  def addProp (world :World, x :Int, y :Int, width :Int, height: Int, viz :Viz,
               footprint :Seq[Coord]) = entity(world) { e =>
    e.addComponent(new Body(x, y, width, height, viz))
    e.addComponent(new Footprint(footprint))
  }

  private def entity (world :World)(initter :(Entity => Unit)) = {
    val e = world.createEntity
    initter(e)
    e.addToWorld()
    e
  }
}
