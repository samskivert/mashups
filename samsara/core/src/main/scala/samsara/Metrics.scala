//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import pythagoras.f.MathUtil

class Metrics (width :Float, height :Float) {

  val size :Float = Math.min(MathUtil.ifloor(width / Level.width),
                             MathUtil.ifloor(height / Level.height))
}
