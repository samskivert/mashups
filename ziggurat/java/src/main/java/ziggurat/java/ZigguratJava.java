package ziggurat.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import ziggurat.core.Ziggurat;

public class ZigguratJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new Ziggurat());
  }
}
