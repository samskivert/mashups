package ziggurat.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import ziggurat.core.Ziggurat;

public class ZigguratActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new Ziggurat());
  }
}
