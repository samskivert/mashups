//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import playn.core.*;
import static pythagoras.f.FloatMath.*;
import tripleplay.util.Randoms;

public class Spiral1 extends Generator {

  public abstract class Shape {
    public void init (Canvas canvas) {}
    public abstract void render (Canvas canvas, float x, float y, float a, float nx, float ny);
  }

  public Shape LINE = new Shape() {
    public void init (Canvas canvas) {
      canvas.setStrokeWidth(2);
    }
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.drawLine(x, y, nx, ny);
    }
    public String toString () { return "line"; }
  };
  public Shape DOT = new Shape() {
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.fillCircle(x, y, 2);
    }
    public String toString () { return "dot"; }
  };
  public Shape SQ = new Shape() {
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.save();
      canvas.translate(x, y);
      canvas.rotate(a);
      canvas.strokeRect(0, -3, Math.abs(nx-x), 6);
      canvas.restore();
    }
    public String toString () { return "square"; }
  };
  public Shape TRI = new Shape() {
    private Path path;
    public void init (Canvas canvas) {
      path = canvas.createPath();
      path.moveTo(3, 0);
      path.lineTo(6, 6);
      path.lineTo(0, 6);
      path.close();
    }
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.save();
      canvas.translate(x, y);
      canvas.rotate(a);
      canvas.fillPath(path);
      canvas.restore();
    }
    public String toString () { return "tri"; }
  };
  public Shape SEG = new Shape() {
    public void init (Canvas canvas) {
      canvas.setStrokeWidth(2);
    }
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.drawLine(x, y, x+(nx-x)/2, y+(ny-y)/2);
    }
    public String toString () { return "seg"; }
  };
  public Shape CRS = new Shape() {
    public void init (Canvas canvas) {
      canvas.setStrokeWidth(2);
    }
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.save();
      canvas.translate(x, y);
      canvas.rotate(a);
      canvas.drawLine(-3, 0, 3, 0);
      canvas.drawLine(0, -3, 0, 3);
      canvas.restore();
    }
    public String toString () { return "cross"; }
  };
  public Shape EX = new Shape() {
    public void render (Canvas canvas, float x, float y, float a, float nx, float ny) {
      canvas.fillCircle(x, y, 2);
    }
    public String toString () { return "x"; }
  };

  // slots:
  // 0 - primary color
  // 1 - secondary color
  //                             R     O     Y     G     B     I     V
  public final float[] ANGLE = { PI/2, PI/3, PI/4, PI/5, PI/6, PI/7, PI/8 }; // 2
  public final int[]   LIFE  = { 4,    6,    8,    10,   12,   14,   16   }; // 3
  public final int[] XDENSE  = { 2,    3,    4,    2,    2,    3,    4    }; // 4
  public final int[] YDENSE  = { 2,    2,    2,    3,    4,    3,    4    }; // 4
  public final Shape[] SHAPE = { LINE, DOT,  SQ,   TRI,  SEG,  CRS,  EX   }; // 5
  // 6 - ???

  public void generate (Randoms rando, Gene[] genes, Canvas target, Info info) {
    float width = target.width(), height = target.height();

    // genes[5] = Gene.ORANGE;

    int pcol = genes[0].primary, scol = genes[1].primary;
    float angle = ANGLE[genes[2].ordinal()];
    int lifespan = LIFE[genes[3].ordinal()]*4;
    int xdense = XDENSE[genes[4].ordinal()], ydense = YDENSE[genes[4].ordinal()];
    Shape shape = SHAPE[genes[5].ordinal()];
    float gridw = width/xdense, gridh = height/ydense;

    info.addColor(genes[0], "P Col", pcol);
    info.addColor(genes[1], "S Col", scol);
    info.add(genes[2], "Angle", (int)(360 * angle / (PI*2)));
    info.add(genes[3], "Life", lifespan);
    info.add(genes[4], "Density", xdense + "/" + ydense);
    info.add(genes[5], "Shape", shape);

    int pens = xdense*ydense;
    float[] xs = new float[pens], ys = new float[pens], as = new float[pens];
    int[] ages = new int[pens];

    // assign starting age and position to each pen
    for (int pp = 0; pp < pens; pp++) {
      int gx = pp % xdense, gy = pp / xdense;
      xs[pp] = gridw * gx + rando.getFloat(gridw);
      ys[pp] = gridh * gy + rando.getFloat(gridh);
      as[pp] = rando.getFloat(2*PI);
      ages[pp] = lifespan;
    }

    // target.setCompositeOperation(Canvas.Composite.MULTIPLY);
    shape.init(target);

    int gens = 100, births = pens;
    info.add("Generations", gens);

    for (int gg = 0; gg < gens; gg++) {
      for (int pp = 0; pp < pens; pp++) {
        int age = ages[pp];
        float x = xs[pp], y = ys[pp], a = as[pp];
        float dist = (angle*5/PI)*(lifespan-age);
        float nx = x + dist*cos(a), ny = y + dist*sin(a);

        float blend = gg / (float)gens;
        int color = (int)((blend*pcol) + (1-blend)*scol);
        target.setStrokeColor(color).setFillColor(color);

        shape.render(target, x, y, a, nx, ny);
        if (age > 1) {
          xs[pp] = nx;
          ys[pp] = ny;
          as[pp] += angle;
          ages[pp] = age-1;
        } else {
          int gx = pp % xdense, gy = pp / xdense;
          xs[pp] = gridw * gx + rando.getFloat(gridw);
          ys[pp] = gridh * gy + rando.getFloat(gridh);
          as[pp] = rando.getFloat(2*PI);
          ages[pp] = lifespan;
          births += 1;
        }
      }
    }

    info.add("Births", births);
  }
}
