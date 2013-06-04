//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import org.junit.Test
import org.junit.Assert._

class CoordTest {

  @Test def testCoords {
    for (x <- 0 until Level.width; y <- 0 until Level.height) {
      val c = Coord(x, y)
      assertEquals(x, c.x)
      assertEquals(y, c.y)
    }
  }
}
