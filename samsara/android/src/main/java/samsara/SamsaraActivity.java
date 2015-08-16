//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara;

import android.util.DisplayMetrics;

import playn.android.GameActivity;
import playn.core.PlayN;

public class SamsaraActivity extends GameActivity {

  @Override public void main () {
    PlayN.run(new Samsara());
  }

  @Override protected float scaleFactor () {
    DisplayMetrics dm = getResources().getDisplayMetrics();
    return (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM) ? 2 : 1;
  }

  @Override protected boolean usePortraitOrientation () {
    return true;
  }
}
