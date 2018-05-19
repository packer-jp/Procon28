import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PolygonEX extends Polygon implements Cloneable {

    void addPoint(Point point) {
        addPoint(point.x, point.y);
    }

    void norm() {
        ArrayList<LineEX> lines = getLines();
        reset();
        for (int i = 0; i < lines.size(); i++) {
            int inext = i + 1 < lines.size() ? i + 1 : 0;
            if (!(lines.get(i).ptLineDist(lines.get(inext).getP2()) == 0)) {
                addPoint((Point) lines.get(i).getP2());
            }
        }
    }

    void reverse() {
        for (int i = 0; i < npoints / 2; i++) {
            int tmp = xpoints[i];
            xpoints[i] = xpoints[npoints - i - 1];
            xpoints[npoints - i - 1] = tmp;
            tmp = ypoints[i];
            ypoints[i] = ypoints[npoints - i - 1];
            ypoints[npoints - i - 1] = tmp;
        }
    }

    boolean perfectlyContains(Point2D p) {
        for (LineEX line : getLines()) {
            if (line.ptSegDist(p) == 0) {
                return false;
            }
        }
        return contains(p);
    }

    boolean contains(PolygonEX pattern) {
        for (int i = 0; i < npoints; i++) {
            if (pattern.perfectlyContains(new Point(xpoints[i], ypoints[i]))) {
                return false;
            }
        }
        for (LineEX patternLine : pattern.getVerticallyScaled()) {
            for (LineEX limitLine : getLines()) {
                if (patternLine.intersectsLine(limitLine)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String str = ":" + npoints;
        for (int i = 0; i < npoints; i++) {
            str += " " + xpoints[i] + " " + ypoints[i];
        }
        return str;
    }

    Direction getDirection(int index, boolean isForward) {
        int second;
        if (isForward) {
            second = index < npoints - 1 ? index + 1 : 0;
        } else {
            second = index > 0 ? index - 1 : npoints - 1;
        }
        return new Direction(ypoints[second] - ypoints[index], xpoints[second] - xpoints[index]);
    }

    VectorPolygon getVectorPolygon() {
        return new VectorPolygon(xpoints, ypoints, npoints);
    }

    ArrayList<LineEX> getLines() {
        ArrayList<LineEX> lines = new ArrayList<>();
        for (int i = 0; i < npoints; i++) {
            int inext = i + 1 < npoints ? i + 1 : 0;
            lines.add(new LineEX(new Point(xpoints[i], ypoints[i]), new Point(xpoints[inext], ypoints[inext])));
        }
        return lines;
    }

    ArrayList<LineEX> getVerticallyScaled() {
        ArrayList<LineEX> scaledLines = new ArrayList<>();
        ArrayList<Point2D> scaledPoints = new ArrayList<>();
        ArrayList<LineEX> lines = getLines();
        for (int i = 0; i < lines.size(); i++) {
            int iprevious = i - 1 >= 0 ? i - 1 : lines.size() - 1;

            LineEX line = lines.get(i);
            double magnification = Solver.OFFSET / Math.sqrt(line.a * line.a + line.b * line.b);
            LineEX tmp = new LineEX(0, 0, line.a * magnification, line.b * magnification);
            LineEX previousLine = new LineEX(tmp.getX2(), tmp.getY2(), tmp.getX2() + tmp.a, tmp.getY2() + tmp.b);

            line = lines.get(iprevious);
            magnification = Solver.OFFSET / Math.sqrt(line.a * line.a + line.b * line.b);
            tmp = new LineEX(0, 0, line.a * magnification, line.b * magnification);
            LineEX currentLine = new LineEX(tmp.getX2(), tmp.getY2(), tmp.getX2() + tmp.a, tmp.getY2() + tmp.b);

            Point2D delta = previousLine.getIntersectPoint((currentLine));
            if (Double.isNaN(delta.getX()) || Double.isNaN(delta.getY())) {
                System.out.println("a");
            }
            scaledPoints.add(new Point2D.Double(line.getX2() + delta.getX(), line.getY2() + delta.getY()));
        }

        for (int i = 0; i < npoints; i++) {
            int inext = i + 1 < npoints ? i + 1 : 0;
            scaledLines.add(new LineEX(scaledPoints.get(i), scaledPoints.get(inext)));
        }
        return scaledLines;
    }

    @Override
    public PolygonEX clone() throws CloneNotSupportedException {
        PolygonEX clone = null;
        try {
            clone = (PolygonEX) super.clone();
            clone.xpoints = this.xpoints.clone();
            clone.ypoints = this.ypoints.clone();
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }

    PolygonEX() {
        super();
    }

    PolygonEX(int[] xpoints, int[] ypoints, int npoints) {
        super(xpoints, ypoints, npoints);
    }
}
