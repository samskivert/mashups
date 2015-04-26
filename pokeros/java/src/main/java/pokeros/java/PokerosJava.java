//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.java;

import playn.java.LWJGLPlatform;

import pokeros.core.Pokeros;

public class PokerosJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    if (args.length == 0 || !configSize(config, args[0])) {
      config.width = 640/2;
      config.height = 960/2;
      // config.scaleFactor = 2;
    }
    // config.emulateTouch = true;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new Pokeros(plat, 0.5f);
    plat.start();
  }

  protected static boolean configSize (LWJGLPlatform.Config config, String device) {
    if ("ipad".equals(device)) {
      config.width = 768;
      config.height = 1024;
      // config.scaleFactor = 2;
    } else if ("i5".equals(device)) {
      config.width = 640;
      config.height = 1136;
      // config.scaleFactor = 2;
    } else if ("droid".equals(device)) {
      // config.width = (int)(480/1.5f);
      // config.height = (int)(800/1.5f);
      config.width = 480;
      config.height = 800;
      // config.scaleFactor = 1.5f;
    } else if ("n7".equals(device)) {
      config.width = 1200;
      config.height = 1920;
      // config.scaleFactor = 2;
    } else if ("n10".equals(device)) {
      config.width = 1600;
      config.height = 2560;
      // config.scaleFactor = 2;
    } else return false;
    return true;
  }
}
