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
      IOSPlatform.register(app, pconfig);
      PlayN.run(new Pokeros());
      return true;
    }
  }

  public class Application {
    static void Main (string[] args) {
      UIApplication.Main (args, null, "AppDelegate");
    }
  }
}
