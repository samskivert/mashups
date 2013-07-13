package pokeros.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import pokeros.core.Pokeros;

public class PokerosActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new Pokeros());
  }
}
