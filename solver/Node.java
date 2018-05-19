package solver;

import java.awt.Point;
import java.util.ArrayList;

class Node {

    int npieces = 0;
    int ipatterns;
    PolygonEX currentPattern;
    PolygonEX[] patterns;
    ArrayList<PolygonEX> frame = new ArrayList<>();

    void init() {
        patterns[ipatterns] = currentPattern;
        ArrayList<LineEX> patternLines = currentPattern.getLines();
        ArrayList<LineEX> limitLines = new ArrayList<>();
        frame.forEach(after -> limitLines.addAll(after.getLines()));
        frame.clear();
        ArrayList<LineEX> tmp = (ArrayList<LineEX>) limitLines.clone();
        for (int i = 0; i < limitLines.size(); i++) {
            LineEX line = limitLines.get(i);
            for (int j = 0; j < patternLines.size(); j++) {
                Point point = new Point((Point) patternLines.get(j).getP1());
                if (line.ptSegDist(point) == 0 && !line.getP1().equals(point) && !line.getP2().equals(point)) {
                    limitLines.add(new LineEX((Point) line.getP1(), point));
                    limitLines.add(new LineEX(point, (Point) line.getP2()));
                    limitLines.remove(i);
                    i--;
                    break;
                }
            }
        }
        for (int i = 0; i < patternLines.size(); i++) {
            LineEX line = patternLines.get(i);
            for (int j = 0; j < tmp.size(); j++) {
                Point point = new Point((Point) tmp.get(j).getP1());
                if (line.ptSegDist(point) == 0 && !line.getP1().equals(point) && !line.getP2().equals(point)) {
                    patternLines.add(new LineEX((Point) line.getP1(), point));
                    patternLines.add(new LineEX(point, (Point) line.getP2()));
                    patternLines.remove(i);
                    i--;
                    break;
                }
            }
        }
        for (int i = 0; i < limitLines.size(); i++) {
            LineEX lline = limitLines.get(i);
            for (int j = 0; j < patternLines.size(); j++) {
                LineEX pline = patternLines.get(j);
                if (lline.overlaps(pline)) {
                    limitLines.remove(i);
                    patternLines.remove(j);
                    i--;
                    j--;
                    break;
                }
            }
        }
        
        limitLines.addAll(patternLines);

        while (!limitLines.isEmpty()) {
            PolygonEX polygon = new PolygonEX();
            LineEX firstLine = limitLines.get(0);
            LineEX previousLine = firstLine;
            LineEX currentLine = null;
            do {
                Point previousPoint = (Point) previousLine.getP2();
                limitLines.remove(previousLine);
                double minAngle = 7;
                for (LineEX line : limitLines) {
                    if (line.getP1().equals(previousPoint)) {
                        double angle = previousLine.getDirection().getClockAngle(line.getDirection());
                        if (angle < minAngle) {
                            currentLine = line;
                            minAngle = angle;
                        }
                    }
                }
                if (previousLine.equals(firstLine)) {
                    limitLines.add(previousLine);
                }
                previousLine = currentLine;
                polygon.addPoint(previousPoint);
            } while (!previousLine.equals(firstLine));
            limitLines.remove(previousLine);
            polygon.norm();
            frame.add(polygon);
        }
    }

    @Override
    public String toString() {
        String str = "1 ";
        str += npieces;
        for (PolygonEX pattern : patterns) {
            if (pattern == null) {
                continue;
            }
            str += pattern;
        }
        return str;
    }

    Node(ArrayList<PolygonEX> frame, PolygonEX[] patterns) {
        this.frame = frame;
        this.patterns = patterns;
    }

    Node(Node node, PolygonEX pattern, int ipatterns) {
        frame = (ArrayList<PolygonEX>) node.frame.clone();
        patterns = node.patterns.clone();
        npieces = node.npieces + 1;
        currentPattern = pattern;
        this.ipatterns = ipatterns;
    }
}
