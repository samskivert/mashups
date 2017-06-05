//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.java.JavaPlatform
import playn.java.LWJGLPlatform

object SamsaraJava {

  def main (args :Array[String]) {
    val config = new JavaPlatform.Config
    // config.scaleFactor = 2
    config.width = 375
    config.height = 667
    val plat = new LWJGLPlatform(config)
    new Samsara(plat)
    plat.start()
  }
}
