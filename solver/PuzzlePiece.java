package solver;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class PuzzlePiece {

    int npoints;
    int npatterns;
    ArrayList<PolygonEX> patterns;

    PolygonEX getTranslated(Identifier identifier, int x, int y, boolean isForward) {
        PolygonEX currentPattern = null;
        try {
            currentPattern = patterns.get(identifier.pattern).clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PuzzlePiece.class.getName()).log(Level.SEVERE, null, ex);
        }
        int point = identifier.point;
        if (isForward) {
            point = point < npoints - 1 ? point + 1 : 0;
        }
        int deltaX = x - currentPattern.xpoints[point];
        int deltaY = y - currentPattern.ypoints[point];
        currentPattern.translate(deltaX, deltaY);
        return currentPattern;
    }

    PuzzlePiece(int[] xpoints, int[] ypoints, int npoints, boolean isUnlocked) {
        this.npoints = npoints;
        patterns = new ArrayList();
        ArrayList<VectorPolygon> vectorPatterns = new ArrayList();
        VectorPolygon basic = new VectorPolygon(xpoints, ypoints, npoints);
        vectorPatterns.add(basic);
        for (int i = 0; i < 3; i++) {
            vectorPatterns.add(vectorPatterns.get(i).getRotated90());
        }
        ArrayList<Direction> cosList = new ArrayList();
        ArrayList<Integer[]> vectorList = new ArrayList();
        for (int i = 0; i < npoints; i++) {
            int length = basic.xvectors[i] * basic.xvectors[i] + basic.yvectors[i] * basic.yvectors[i];
            for (int j = basic.xvectors[i], root = basic.yvectors[i];;) {
                int sign = root > 0 || root == 0 && j > 0 ? -1 : 1;
                j += sign;
                root = -sign * (int) Math.sqrt(length - j * j);
                if (j == -basic.yvectors[i] && root == basic.xvectors[i]) {
                    break;
                }
                if (root * root == length - j * j) {
                    Direction cos = new Direction(basic.xvectors[i] * j + basic.yvectors[i] * root, length);
                    int index = cosList.indexOf(cos);
                    if (index == -1) {
                        cosList.add(cos);
                        vectorList.add(new Integer[npoints * 2]);
                        vectorList.get(vectorList.size() - 1)[i * 2] = j;
                        vectorList.get(vectorList.size() - 1)[i * 2 + 1] = root;
                    } else {
                        vectorList.get(index)[i * 2] = j;
                        vectorList.get(index)[i * 2 + 1] = root;
                    }
                }
            }
        }
        check:
        for (int i = 0; i < vectorList.size(); i++) {
            int[] rotatedXvectors = new int[npoints];
            int[] rotatedYvectors = new int[npoints];
            for (int j = 0; j < npoints; j++) {
                if (vectorList.get(i)[j * 2] == null) {
                    continue check;
                } else {
                    rotatedXvectors[j] = vectorList.get(i)[j * 2];
                    rotatedYvectors[j] = vectorList.get(i)[j * 2 + 1];
                }
            }
            vectorPatterns.add(new VectorPolygon(rotatedXvectors, rotatedYvectors, npoints, true));
            for (int j = 0; j < 3; j++) {
                vectorPatterns.add(vectorPatterns.get(vectorPatterns.size() - 1).getRotated90());
            }
        }
        npatterns = vectorPatterns.size();
        if(isUnlocked){
            for (int i = 0; i < npatterns; i++) {
                vectorPatterns.add(vectorPatterns.get(i).getInverse());
            }
        }
        npatterns = vectorPatterns.size();
        for (int i = npatterns - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (vectorPatterns.get(i).matches(vectorPatterns.get(j))) {
                    vectorPatterns.remove(i);
                    break;
                }
            }
        }
        npatterns = vectorPatterns.size();
        for (int i = 0; i < npatterns; i++) {
            patterns.add(vectorPatterns.get(i).getPolygonEX());
        }
    }
}
