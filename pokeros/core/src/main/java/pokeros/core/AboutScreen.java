//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import react.UnitSlot;

import static playn.core.PlayN.openURL;

import tripleplay.ui.*;

public class AboutScreen extends UIScreen {

  public AboutScreen (Pokeros game) {
    super(game);
  }

  @Override protected String title () {
    return "About Pokeros";
  }

  @Override protected void createUI (Root root) {
    root.add(new Shim(5, 5),
             new Label("Copyright \u00A9 2013-2014\nMichael Bayne").addStyles(Style.TEXT_WRAP.on),
             UI.stretchShim(),
             new Label("Pokeros (pronounced \"poker-ohs\") was initially developed as a game " +
                       "design experiment. It turned out to be fun enough to merit polishing " +
                       "it up and making it more widely available.").addStyles(UI.textStyles),
             UI.stretchShim(),
             new Label("More information about Pokeros and the game design experiments is " +
                       "here:").addStyles(UI.textStyles),
             new Button("github.com/samskivert/mashups").onClick(
               new UnitSlot() { public void onEmit () {
                 openURL("https://github.com/samskivert/mashups/tree/master/pokeros/");
               }}),
             UI.stretchShim(),
             new Button("Back").onClick(popSlot()));
  }
}
