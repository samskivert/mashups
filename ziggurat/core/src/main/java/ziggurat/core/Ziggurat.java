package ziggurat.core;

import static playn.core.PlayN.*;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;

public class Ziggurat extends Game.Default {

  public Ziggurat() {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init() {
    // create and add background image layer
    Image bgImage = assets().getImage("images/bg.png");
    ImageLayer bgLayer = graphics().createImageLayer(bgImage);
    graphics().rootLayer().add(bgLayer);
  }

  @Override
  public void update(int delta) {
  }

  @Override
  public void paint(float alpha) {
    // the background automatically paints itself, so no need to do anything here!
  }
}
