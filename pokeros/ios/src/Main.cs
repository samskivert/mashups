using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

using playn.ios;
using playn.core;
using pokeros.core;

namespace pokeros
{
  [Register ("AppDelegate")]
  public partial class AppDelegate : IOSApplicationDelegate {
    public override bool FinishedLaunching (UIApplication app, NSDictionary options) {
      var pconfig = new IOSPlatform.Config();
      pconfig.iPadLikePhone = true;
      var platform = IOSPlatform.register(app, pconfig);
      // prior to iOS 7 we need to say that we want to extend under the status bar
      if (!UIDevice.CurrentDevice.CheckSystemVersion(7,0)) {
        platform.rootViewController().WantsFullScreenLayout = true;
      }
      var iPad = (MonoTouch.UIKit.UIScreen.MainScreen.Bounds.Width >= 768);
      PlayN.run(new Pokeros(iPad ? 0.3f : 0.5f));
      return true;
    }
  }

  public class Application {
    static void Main (string[] args) {
      UIApplication.Main (args, null, "AppDelegate");
    }
  }
}
