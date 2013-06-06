//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import scala.collection.mutable.ArrayBuffer

case class Viz (width :Int, height :Int) {
  import Viz._

  def create (metrics :Metrics) :Layer = {
    val image = graphics.createImage(metrics.size*width, metrics.size*height)
    image.canvas.setLineCap(Canvas.LineCap.ROUND).translate(1, 1)
    _ops.foreach(_.apply(image.canvas, image.width-2, image.height-2))
    graphics.createImageLayer(image).setOrigin(image.width/2, image.height/2)
  }

  def op (op :(Canvas, Float, Float) => Unit) = {
    _ops += op
    this
  }

  def circleSF (x :Float, y :Float, r :Float, stroke :Int = 0xFF000000, fill :Int = 0xFFFFFFFF) = {
    circleF(x, y, r, fill)
    circleS(x, y, r, stroke)
  }

  def circleF (x :Float, y :Float, r :Float, fill :Int) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setFillColor(fill).fillCircle(x*width, y*height, r*math.min(width, height)-1)
    }}

  def circleS (x :Float, y :Float, r :Float, stroke :Int) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setStrokeColor(stroke).setStrokeWidth(2).
        strokeCircle(x*width, y*height, r*math.min(width, height)-1).
        setStrokeWidth(1)
    }}

  def line (x1 :Float, y1 :Float, x2 :Float, y2 :Float, stroke :Int, swidth :Float = 2) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setStrokeColor(stroke).setStrokeWidth(swidth).
        drawLine(x1*width, y1*height, x2*width, y2*height).
        setStrokeWidth(1)
    }}

  def roundRectSF (x :Float, y :Float, rwidth :Float, rheight :Float, corner :Float,
                   stroke :Int = 0xFF000000, fill :Int = 0xFFFFFFFF) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setFillColor(fill).
        fillRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width).
        setStrokeColor(stroke).
        strokeRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width)
    }}

  def ellipseF (x :Float, y :Float, ewidth :Float, eheight: Float, fill :Int) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      val path = ellipsePath(canvas.createPath(), x*width, y*width, ewidth*width, eheight*height)
      canvas.setFillColor(fill).fillPath(path)
    }}

  def ellipseS (x :Float, y :Float, ewidth :Float, eheight: Float, stroke :Int) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      val path = ellipsePath(canvas.createPath(), x*width, y*width, ewidth*width, eheight*height)
      canvas.setStrokeColor(stroke).strokePath(path)
    }}

  def ellipseSF (x :Float, y :Float, ewidth :Float, eheight: Float, stroke :Int, fill :Int) = {
    ellipseF(x, y, ewidth, eheight, fill)
    ellipseS(x, y, ewidth, eheight, stroke)
  }

  def polyS (stroke :Int, coords :(Float,Float)*) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setStrokeColor(stroke).strokePath(poly(canvas.createPath(), width, height, coords))
    }}

  def polyF (fill :Int, coords :(Float,Float)*) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      canvas.setFillColor(fill).fillPath(poly(canvas.createPath(), width, height, coords))
    }}

  def polySF (stroke :Int, fill :Int, coords :(Float,Float)*) = {
    polyF(fill, coords :_*)
    polyS(stroke, coords :_*)
  }

  def textF (text :String, fill :Int) = op {
    (canvas :Canvas, width :Float, height :Float) => {
      val tl = graphics.layoutText(text, new TextFormat().withFont(UI.bodyFont(height/3)))
      canvas.setFillColor(fill).fillText(tl, (width-tl.width)/2, (height-tl.height)/2)
    }}

  val _ops = ArrayBuffer[(Canvas,Float,Float) => Unit]()
}

object Viz {

  def poly (path :Path, sx :Float, sy: Float, coords :Seq[(Float,Float)]) = {
    path.moveTo(coords.head._1*sx, coords.head._2*sy)
    (path /: coords.drop(1))((p, c) => path.lineTo(c._1*sx, c._2*sy))
    path.close()
  }

  def ellipsePath (path :Path, x :Float, y :Float, w :Float, h :Float) = {
    val kappa = .5522848f
    val ox = (w / 2) * kappa // control point offset horizontal
    val oy = (h / 2) * kappa // control point offset vertical
    val xe = x + w           // x-end
    val ye = y + h           // y-end
    val xm = x + w / 2       // x-middle
    val ym = y + h / 2       // y-middle
    path.moveTo(x, ym)
    path.bezierTo(x, ym - oy, xm - ox, y, xm, y)
    path.bezierTo(xm + ox, y, xe, ym - oy, xe, ym)
    path.bezierTo(xe, ym + oy, xm + ox, ye, xm, ye)
    path.bezierTo(xm - ox, ye, x, ym + oy, x, ym)
    path.close()
  }
}
