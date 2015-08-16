package samsara;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.apple.foundation.Foundation;

import playn.robovm.RoboPlatform;

public class SamsaraRoboVM extends UIApplicationDelegateAdapter {

  @Override
  public boolean didFinishLaunching (UIApplication app, UIApplicationLaunchOptions launchOpts) {
    try {
      Foundation.log("Hello world!");
      // create a full-screen window
      CGRect bounds = UIScreen.getMainScreen().getBounds();
      UIWindow window = new UIWindow(bounds);

      // configure and register the PlayN platform; start our game
      Foundation.log("Robo init!");
      RoboPlatform.Config config = new RoboPlatform.Config();
      config.orients = UIInterfaceOrientationMask.All;
      RoboPlatform pf = RoboPlatform.register(window, config);
      Foundation.log("Samsara init!");
      pf.run(new Samsara());

      // make our main window visible
      Foundation.log("Window init!");
      window.makeKeyAndVisible();
      addStrongRef(window);
    } catch (Throwable t) {
      Foundation.log("Crash!");
      Foundation.log(t.toString());
    }
    return true;
  }

  public static void main (String[] args) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(args, null, SamsaraRoboVM.class);
    pool.close();
  }
}
