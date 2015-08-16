//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._
import playn.core.PlayN._

import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout
import tripleplay.util.TextStyle

object UI {

  val titleFont = graphics.createFont("Helvetica", Font.Style.BOLD, 48)
  val menuFont = graphics.createFont("Helvetica", Font.Style.BOLD, 24)
  def bodyFont (size :Float) = graphics.createFont("Times New Roman", Font.Style.BOLD, size)

  def sheet = SimpleStyles.newSheet

  def levelCfg = TextStyle.normal(bodyFont(256), 0xFF000000)
  def animCfg (color :Int, size :Float) = TextStyle.normal(bodyFont(size), color)
  def tipCfg = TextStyle.normal(bodyFont(32), 0xFFFFFFFF).withOutline(0xFF000000, 2f)

  /** Returns a shim configured with an [AxisLayout] stretch constraint. */
  def stretchShim :Shim = AxisLayout.stretch(shim(1, 1))

  /** Creates a shim with the specified dimensions. */
  def shim (width :Float, height :Float) = new Shim(width, height)
}
