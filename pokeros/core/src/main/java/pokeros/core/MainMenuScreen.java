//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.Game;

import react.UnitSlot;

import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class MainMenuScreen extends UIScreen {

  public MainMenuScreen (Pokeros game) {
    super(game);
  }

  @Override protected void createUI (Root root) {
    root.add(new Shim(1, 10),
             new Label("Pokeros").addStyles(
               UI.titleStyles.add(Style.FONT.is(UI.defaultFont.derive(68f)))),
             UI.stretchShim(),
             new Button("Play").addStyles(UI.bigButtonStyles).onClick(
               new UnitSlot() { public void onEmit () {
                 _game.screens.push(new GameScreen(_game));
               }}),
             new Button("Rules").addStyles(UI.medButtonStyles).onClick(
               new UnitSlot() { public void onEmit () {
                 _game.screens.push(new RulesScreen(_game));
               }}),
             new Button("About").addStyles(UI.medButtonStyles).onClick(
               new UnitSlot() { public void onEmit () {
                 _game.screens.push(new AboutScreen(_game));
               }}),
             UI.stretchShim(),
             new Group(AxisLayout.horizontal()).add(
               new Label("Wins:"), new Label(_game.history.wins), new Shim(10, 1),
               new Label("Losses:"), new Label(_game.history.losses), new Shim(20, 1),
               new Button("More...").onClick(
                 new UnitSlot() { public void onEmit () {
                   _game.screens.push(new HistoryScreen(_game));
                 }})));
  }
}
