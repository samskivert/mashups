//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._
import playn.scene.{Layer, ImageLayer}
import scala.collection.mutable.ArrayBuffer

case class Viz (width :Int, height :Int, stroke :Int, fill :Int) {
  import Viz._

  def create (plat :Platform, metrics :Metrics) :Layer = {
    val canvas = plat.graphics.createCanvas(metrics.size*width, metrics.size*height)
    canvas.setLineCap(Canvas.LineCap.ROUND).setStrokeColor(stroke).setFillColor(fill).
      translate(1, 1)
    _ops.foreach(_.apply(plat, canvas, canvas.width-2, canvas.height-2))
    new ImageLayer(canvas.image).setOrigin(canvas.width/2, canvas.height/2)
  }

  def op (op :(Platform, Canvas, Float, Float) => Unit) = {
    _ops += op
    this
  }

  def circleF (x :Float, y :Float, r :Float) = op {
    (plat, canvas, width, height) => {
      canvas.fillCircle(x*width, y*height, r*math.min(width, height)-1)
    }}
  def circleSF (x :Float, y :Float, r :Float) = op {
    (plat, canvas, width, height) => {
      val (cx, cy, cr) = (x*width, y*height, r*math.min(width, height))
      canvas.fillCircle(cx, cy, cr-1).setStrokeWidth(2).strokeCircle(cx, cy, cr-1).setStrokeWidth(1)
    }}

  def circleF (x :Float, y :Float, r :Float, fill :Int) = op {
    (plat, canvas, width, height) => {
      canvas.save().setFillColor(fill).
        fillCircle(x*width, y*height, r*math.min(width, height)-1).restore()
    }}

  def line (x1 :Float, y1 :Float, x2 :Float, y2 :Float, swidth :Float = 2) = op {
    (plat, canvas, width, height) => {
      canvas.setStrokeWidth(swidth).drawLine(x1*width, y1*height, x2*width, y2*height).
        setStrokeWidth(1)
    }}

  def roundRectSF (x :Float, y :Float, rwidth :Float, rheight :Float, corner :Float) = op {
    (plat, canvas, width, height) => {
      canvas.fillRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width).
        strokeRoundRect(x*width, y*width, rwidth*width, rheight*height, corner*width)
    }}

  def ellipseSF (x :Float, y :Float, ewidth :Float, eheight: Float) = op {
    (plat, canvas, width, height) => {
      val path = ellipsePath(canvas.createPath(), x*width, y*width, ewidth*width, eheight*height)
      canvas.fillPath(path).strokePath(path)
    }}

  def polyF (coords :(Float,Float)*) = op {
    (plat, canvas, width, height) => {
      canvas.fillPath(poly(canvas.createPath(), width, height, coords))
    }}
  def polySF (coords :(Float,Float)*) = op {
    (plat, canvas, width, height) => {
      val path = poly(canvas.createPath(), width, height, coords)
      canvas.fillPath(path).strokePath(path)
    }}

  def heartSF (bx :Float, by :Float, hw :Float, hh :Float) = op {
    (plat, canvas, width, height) => {
      val path = heart(canvas.createPath(), bx*width, by*height, hw*width, 2*hh*height/3)
      canvas.fillPath(path).strokePath(path)
    }}

  def textF (text :String, fill :Int) = op {
    (plat, canvas, width, height) => {
      val tl = plat.graphics.layoutText(text, new TextFormat().withFont(UI.bodyFont(height/3)))
      canvas.setFillColor(fill).fillText(tl, (width-tl.size.width)/2, (height-tl.size.height)/2)
    }}

  val _ops = ArrayBuffer[(Platform,Canvas,Float,Float) => Unit]()
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

  def heart (path :Path, bx :Float, by :Float, width :Float, height :Float) = {
    val (l, r, top) = (bx-width/2, bx+width/2, by-height)
    path.moveTo(bx, by)
    path.lineTo(r, top)
    path.bezierTo(r, top-height/2, bx, top-height/2, bx, top)
    path.bezierTo(bx, top-height/2, l, top-height/2, l, top)
    path.close()
  }
}
