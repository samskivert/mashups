//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import pokeros.core.Pokeros;

public class PokerosJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    config.width = 640;
    config.height = 960;
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new Pokeros());
  }
}