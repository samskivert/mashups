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
    image.canvas.setLineCap(Canvas.LineCap.ROUND)
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

  def line (x1 :Float, y1 :Float, x2 :Float, y2 :Float,
            stroke :Int = 0xFF000000, swidth :Float = 2) = {
    _ops += ((canvas :Canvas, width :Float, height :Float) => {
      canvas.setStrokeColor(stroke).setStrokeWidth(swidth).
        drawLine(x1*width, y1*height, x2*width, y2*height)
    })
    this
  }

  def roundRectSF (x :Float, y :Float, rwidth :Float, rheight :Float, corner :Float,
                   stroke :Int = 0xFF000000, fill :Int = 0xFFFFFFFF) = {
    _ops += ((canvas :Canvas, width :Float, height :Float) => {
      canvas.setFillColor(fill).
        fillRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width).
        setStrokeColor(stroke).
        strokeRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width)
    })
    this
  }

  val _ops = ArrayBuffer[(Canvas,Float,Float) => Unit]()
}
