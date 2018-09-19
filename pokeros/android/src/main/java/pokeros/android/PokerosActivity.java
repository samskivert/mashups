package pokeros.android;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import playn.android.AndroidAssets;
import playn.android.GameActivity;
import playn.core.Font;

import pokeros.core.Pokeros;

public class PokerosActivity extends GameActivity {

  public Pokeros game;

  @Override public void main () {
    // register our fonts
    Typeface copperplate = platform().assets().getTypeface("fonts/copperplate.ttf");
    platform().graphics().registerFont(copperplate, "Copperplate", Font.Style.PLAIN);
    platform().graphics().registerFont(copperplate, "Copperplate", Font.Style.BOLD);

    game = new Pokeros(platform(), 0.5f);
  }

  @Override protected float scaleFactor () {
    return getResources().getDisplayMetrics().density * 0.8f;
  }

  @Override protected String logIdent () { return "pokeros"; }

  @Override protected int makeWindowFlags () { // we want the status bar to remain visible
    return super.makeWindowFlags() & ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
  }
}
