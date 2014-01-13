package pokeros.android;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import playn.android.AndroidAssets;
import playn.android.GameActivity;
import playn.core.Font;
import playn.core.PlayN;

import pokeros.core.Pokeros;

public class PokerosActivity extends GameActivity {

  @Override
  public void main(){
    // register our fonts
    platform().graphics().registerFont("fonts/copperplate.ttf", "Copperplate", Font.Style.PLAIN);
    platform().graphics().registerFont("fonts/copperplate.ttf", "Copperplate", Font.Style.BOLD);

    // use a lower-memory format for JPGs (which tend to be large and memory sucking)
    platform().assets().setBitmapOptionsAdjuster(new AndroidAssets.BitmapOptionsAdjuster() {
      @Override public void adjustOptions (String path, AndroidAssets.BitmapOptions options) {
        // use a 16-bit per pixel format for JPGs; looks decent, saves memory
        if (path.endsWith(".jpg")) options.inPreferredConfig = Bitmap.Config.RGB_565;
      }
    });

    PlayN.run(new Pokeros(0.5f));
  }

  @Override protected float scaleFactor () {
    DisplayMetrics dm = getResources().getDisplayMetrics();
    return (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM) ? 2 : 1;
  }

  @Override protected boolean usePortraitOrientation () { return true; }

  @Override protected String logIdent () { return "pokeros"; }

  @Override protected int makeWindowFlags () { // we want the status bar to remain visible
    return super.makeWindowFlags() & ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
  }
}
