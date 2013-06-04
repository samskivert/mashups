//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara;

import playn.android.GameActivity;
import playn.core.PlayN;

public class SamsaraActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new Samsara());
  }
}
