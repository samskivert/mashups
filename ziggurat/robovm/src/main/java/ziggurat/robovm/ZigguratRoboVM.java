package ziggurat.robovm;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

import playn.robovm.RoboPlatform;
import ziggurat.core.Ziggurat;

public class ZigguratRoboVM extends UIApplicationDelegateAdapter {

  @Override
  public boolean didFinishLaunching (UIApplication app, UIApplicationLaunchOptions launchOpts) {
    // create a full-screen window
    CGRect bounds = UIScreen.getMainScreen().getBounds();
    UIWindow window = new UIWindow(bounds);

    // configure and create the PlayN platform
    RoboPlatform.Config config = new RoboPlatform.Config();
    config.orients = UIInterfaceOrientationMask.All;
    RoboPlatform plat = RoboPlatform.create(window, config);

    // create and initialize our game
    new Ziggurat(plat);

    // make our main window visible (this starts the platform)
    window.makeKeyAndVisible();
    addStrongRef(window);
    return true;
  }

  public static void main (String[] args) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(args, null, ZigguratRoboVM.class);
    pool.close();
  }
}
