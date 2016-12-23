package ziggurat.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import ziggurat.core.Ziggurat;

public class ZigguratHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("ziggurat/");
    new Ziggurat(plat);
    plat.start();
  }
}
