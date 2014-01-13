//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import pokeros.core.Pokeros;

public class PokerosJava {

  public static void main (String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    if (args.length == 0 || !configSize(config, args[0])) {
      config.width = 640/2;
      config.height = 960/2;
      config.scaleFactor = 2;
    }
    config.emulateTouch = true;
    JavaPlatform.register(config);
    PlayN.run(new Pokeros(0.5f));
  }

  protected static boolean configSize (JavaPlatform.Config config, String device) {
    if ("ipad".equals(device)) {
      config.width = 768/2;
      config.height = 1024/2;
      config.scaleFactor = 2;
    } else if ("i5".equals(device)) {
      config.width = 640/2;
      config.height = 1136/2;
      config.scaleFactor = 2;
    } else if ("droid".equals(device)) {
      config.width = (int)(480/1.5f);
      config.height = (int)(800/1.5f);
      config.scaleFactor = 1.5f;
    } else if ("n7".equals(device)) {
      config.width = 1200/2;
      config.height = 1920/2;
      config.scaleFactor = 2;
    } else if ("n10".equals(device)) {
      config.width = 1600/2;
      config.height = 2560/2;
      config.scaleFactor = 2;
    } else return false;
    return true;
  }
}
