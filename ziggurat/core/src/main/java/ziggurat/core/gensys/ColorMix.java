//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import playn.core.Canvas;
import static pythagoras.f.FloatMath.*;
import tripleplay.util.Colors;
import tripleplay.util.Randoms;

public class ColorMix extends Generator {

  public interface F {
    float apply (float value, float period, float max);
  }

  final F sinF = new F() {
    public float apply (float value, float period, float max) {
      return sin(value/period); }
    public String toString () { return "sin"; }
  };
  final F cosF = new F() {
    public float apply (float value, float period, float max) {
      return cos(value/period); }
    public String toString () { return "cos"; }
  };
  final F tanF = new F() {
    public float apply (float value, float period, float max) {
      return clamp(tan(value/period), -1, 1);
    }
    public String toString () { return "tan"; }
  };
  final F atanF = new F() {
    public float apply (float value, float period, float max) {
      return clamp(atan(value/max), -1, 1);
    }
    public String toString () { return "atan"; }
  };
  final F sawF = new F() {
    public float apply (float value, float period, float max) {
      float v = (value/period)%2;
      return v > 1 ? 2-v : v;
    }
    public String toString () { return "saw"; }
  };
  final F sqF = new F() {
    public float apply (float value, float period, float max) {
      float v = value/max; return v*v;
    }
    public String toString () { return "sq"; }
  };

  // slots:
  // 0 - primary color
  // 1 - secondary color
  //                             R     O     Y     G     B     I     V
  // 2 - x func
  public F[] xfuns = {           sinF, cosF, tanF, sawF, sqF,  atanF, sinF };
  // 3 - y func
  public F[] yfuns = {           sinF, cosF, tanF, sawF, sqF,  atanF, sinF };
  // 4 - period
  public int[] periods = {       2,    3,    4,    5,    6,    7,    8    };
  // 5 - ???
  // public float[] as = {          25,   33,   65,   70,   130,  170,  190  };
  // public float[][] pss = {       {3,2}, {2,4}, {2,5}, {5,2}, {2,6}, {7,2}, {2,7} };
  // 6 - ???

  public void generate (Randoms rando, Gene[] genes, Canvas target, Info info) {
    float widthf = target.width(), heightf = target.height();
    int width = (int)widthf, height = (int)heightf;

    // genes[3] = Gene.INDIGO;

    int pcol = genes[0].primary;
    int scol = genes[1].secondary;
    F xf = xfuns[genes[2].ordinal()];
    F yf = yfuns[genes[3].ordinal()];
    int period = periods[genes[4].ordinal()];

    info.addColor(genes[0], "P Col", pcol);
    info.addColor(genes[1], "S Col", scol);
    info.add(genes[2], "fx", xf);
    info.add(genes[3], "fy", yf);
    info.add(genes[4], "period", period);

    float xp = widthf/period, yp = heightf/period;

    for (int yy = 0; yy < height; yy += 2) {
      for (int xx = 0; xx < width; xx += 2) {
        float x = xf.apply(xx, xp, widthf);
        float y = yf.apply(yy, yp, heightf);
        // float v = Math.abs((x+y)/2);
        float v = Math.abs(x*y);
        int color = Colors.blend(pcol, scol, v);
        target.setFillColor(color).fillRect(xx, yy, 2, 2);
      }
    }

    // target.save();
    // target.setStrokeColor(0x33FFFFFF & pcol);
    // target.setStrokeWidth(2);
    // target.translate(widthf/2, heightf/2);
    // float a = as[genes[5].ordinal()];
    // info.add(genes[5], "a", a);
    // float b = 100;
    // float m = (a - b), n = (a/b)-1;
    // float ox = 0, oy = 0;
    // float scale = 1.75f;
    // for (int tt = 0; tt < 500; tt++) {
    //   float nx = (m*cos(tt) + b*cos(tt*n))*scale;
    //   float ny = (m*sin(tt) - b*sin(tt*n))*scale;
    //   if (tt > 0) target.drawLine(ox, oy, nx, ny);
    //   ox = nx;
    //   oy = ny;
    // }
    // target.restore();

    target.save();
    target.setStrokeColor(0x33FFFFFF & pcol);
    target.setStrokeWidth(2);
    target.translate(widthf/2, heightf/2);
    int a = genes[5].ordinal()+1, b = (a+genes[6].ordinal())%8+1, c = a, d = b;
    info.add(genes[5], "a", a);
    info.add(genes[6], "b", b);
    float ox = 0, oy = 0;
    float scale = widthf/4;
    for (int tt = 0; tt < 500; tt++) {
      float nx = (cos(a*tt) - pow(cos(b*tt), 3))*scale;
      float ny = (sin(c*tt) - pow(sin(d*tt), 3))*scale;
      if (tt > 0) target.drawLine(ox, oy, nx, ny);
      ox = nx;
      oy = ny;
    }
    target.restore();
  }
}
