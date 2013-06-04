//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Layer
import playn.core.PlayN._

abstract class Viz {

  def create (metrics :Metrics) :Layer
}

object Viz {

  def circle (size :Int, fill :Int = 0xFFFFFFFF, stroke :Int = 0xFF000000) = new Viz {
    def create (metrics :Metrics) = {
      val diam = metrics.size*size
      val image = graphics.createImage(diam, diam)
      val r = diam/2
      image.canvas.setFillColor(fill).fillCircle(r, r, r-3).
        setStrokeColor(stroke).setStrokeWidth(2).strokeCircle(r, r, r-2)
      graphics.createImageLayer(image).setOrigin(image.width/2, image.height/2)
    }
  }
}
