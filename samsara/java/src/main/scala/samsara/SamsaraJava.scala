//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN
import playn.java.JavaPlatform

object SamsaraJava {

  def main (args :Array[String]) {
    val config = new JavaPlatform.Config
    config.width = 640
    config.height = 960
    JavaPlatform.register(config)
    PlayN.run(new Samsara)
  }
}
