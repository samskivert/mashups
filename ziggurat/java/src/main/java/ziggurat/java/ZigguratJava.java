package ziggurat.java;

import playn.java.LWJGLPlatform;

import ziggurat.core.Ziggurat;

public class ZigguratJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    // use config to customize the Java platform, if needed
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new Ziggurat(plat);
    plat.start();
  }
}
