import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LineEX extends Line2D.Double {

    double a, b, c;

    void init() {
        a = y2 - y1;
        b = x1 - x2;
        c = -a * x1 - b * y1;
    }
    
    boolean overlaps(LineEX target) {
        return getP1().equals(target.getP2()) && getP2().equals(target.getP1());
    }

    @Override
    public Point2D getP1() {
        return new Point((int) x1, (int) y1);
    }

    @Override
    public Point2D getP2() {
        return new Point((int) x2, (int) y2);
    }

    Point2D getIntersectPoint(LineEX target) {
        double denom = a * target.b - target.a * b;
        return new Point2D.Double((b * target.c - target.b * c) / denom, (target.a * c - a * target.c) / denom);
    }

    Direction getDirection() {
        return new Direction((Point) getP1(), (Point) getP2());
    }

    LineEX(Point2D p1, Point2D p2) {
        super(p1, p2);
        init();
    }

    LineEX(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
        init();
    }
}
