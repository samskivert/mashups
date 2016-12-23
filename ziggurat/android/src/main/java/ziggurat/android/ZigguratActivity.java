package ziggurat.android;

import playn.android.GameActivity;

import ziggurat.core.Ziggurat;

public class ZigguratActivity extends GameActivity {

  @Override public void main () {
    new Ziggurat(platform());
  }
}
