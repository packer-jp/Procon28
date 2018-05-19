package solver;

import java.awt.Point;

class Direction {

    int numer;
    int denom;

    private int gcd(int n1, int n2) {
        return n2 == 0 ? n1 : gcd(n2, n1 % n2);
    }

    double getClockAngle(Direction leave) {
        double arriveAngle = Math.atan2(-numer, -denom);
        double leaveAngle = Math.atan2(leave.numer, leave.denom);
        double clockAngle = leaveAngle - arriveAngle;
        if (clockAngle > Math.PI) {
            clockAngle -= Math.PI * 2;
        } else if (clockAngle < -Math.PI) {
            clockAngle += Math.PI * 2;
        }
        if (clockAngle < 0) {
            clockAngle += Math.PI * 2;
        }
        return clockAngle;
    }

    Direction getOpposite() {
        return new Direction(-numer, -denom);
    }

    @Override
    public int hashCode() {
        return (numer + 65) * 203 + (denom + 101);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            Direction var = (Direction) obj;
            return numer == var.numer && denom == var.denom;
        }
        return false;
    }

    Direction(int numer, int denom) {
        int gcd = gcd(Math.abs(numer), Math.abs(denom));
        this.numer = numer / gcd;
        this.denom = denom / gcd;
    }

    Direction(Point start, Point stop) {
        this(stop.y - start.y, stop.x - start.x);
    }
}
