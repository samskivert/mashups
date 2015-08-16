//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.robovm;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

import playn.robovm.RoboPlatform;
import pokeros.core.Pokeros;

public class PokerosRoboVM extends UIApplicationDelegateAdapter {

  @Override
  public boolean didFinishLaunching (UIApplication app, UIApplicationLaunchOptions launchOpts) {
    // create a full-screen window
    CGRect bounds = UIScreen.getMainScreen().getBounds();
    UIWindow window = new UIWindow(bounds);

    // configure and create the PlayN platform
    RoboPlatform.Config config = new RoboPlatform.Config();
    config.orients = UIInterfaceOrientationMask.All;
    config.iPadLikePhone = true;
    RoboPlatform plat = RoboPlatform.create(window, config);

    // TODO
    // // prior to iOS 7 we need to say that we want to extend under the status bar
    // if (!UIDevice.getCurrentDevice().checkSystemVersion(7, 0)) {
    //   window.getRootViewController().setWantsFullScreenLayout(true);
    // }

    // create and initialize our game
    boolean iPad = (bounds.getWidth() >= 768);
    new Pokeros(plat, iPad ? 0.3f : 0.5f);

    // make our main window visible (this starts the platform)
    window.makeKeyAndVisible();
    addStrongRef(window);
    return true;
  }

  public static void main (String[] args) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(args, null, PokerosRoboVM.class);
    pool.close();
  }
}
