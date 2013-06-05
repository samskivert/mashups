//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import scala.collection.mutable.ArrayBuffer

case class Viz (width :Int, height :Int) {

  def create (metrics :Metrics) :Layer = {
    val image = graphics.createImage(metrics.size*width, metrics.size*height)
    _ops.foreach(_.apply(image.canvas, image.width, image.height))
    graphics.createImageLayer(image).setOrigin(image.width/2, image.height/2)
  }

  def circleSF (x :Float, y :Float, r :Float, stroke :Int = 0xFF000000, fill :Int = 0xFFFFFFFF) = {
    circleF(x, y, r, fill)
    circleS(x, y, r, stroke)
  }

  def circleF (x :Float, y :Float, r :Float, fill :Int) = {
    _ops += ((canvas :Canvas, width :Float, height :Float) => {
      canvas.setFillColor(fill).fillCircle(x*width, y*height, r*math.min(width, height)-3)
    })
    this
  }

  def circleS (x :Float, y :Float, r :Float, stroke :Int) = {
    _ops += ((canvas :Canvas, width :Float, height :Float) => {
      canvas.setStrokeColor(stroke).setStrokeWidth(2).
        strokeCircle(x*width, y*height, r*math.min(width, height)-2)
    })
    this
  }

  val _ops = ArrayBuffer[(Canvas,Float,Float) => Unit]()
}
