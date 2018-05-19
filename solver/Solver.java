import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

public class Solver {

    int npieces = 0;
    int nhints = 0;
    static final double OFFSET = -0.0001;
    ArrayList<PuzzlePiece> pieces = new ArrayList();
    HashMap<Direction, ArrayList<Identifier>> directionMap = new HashMap();

    public static void main(String[] args) {
        new Solver().compute();
    }

    void compute() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("[^-\\d]+");
        /*
        boolean[] isUnlockeds = new boolean[50];
        for (int i = 0; i < 50; i++) {
            isUnlockeds[i] = scanner.nextInt() == 0 ? true : false;
        }
        */
        int nQRs = scanner.nextInt();
        for (int i = 0; i < nQRs; i++) {
            int ncurrentPieces = scanner.nextInt();
            npieces += ncurrentPieces;
            for (int j = 0; j < ncurrentPieces; j++) {
                int[] xpoints, ypoints;
                int npoints;
                npoints = scanner.nextInt();
                xpoints = new int[npoints];
                ypoints = new int[npoints];
                for (int k = 0; k < npoints; k++) {
                    xpoints[k] = scanner.nextInt();
                    ypoints[k] = scanner.nextInt();
                }
                pieces.add(new PuzzlePiece(xpoints, ypoints, npoints, true));
            }
        }

        for (int i = 0; i < npieces; i++) {
            PuzzlePiece currentPiece = pieces.get(i);
            for (int j = 0; j < currentPiece.npatterns; j++) {
                PolygonEX currentPattern = currentPiece.patterns.get(j);
                for (int k = 0; k < currentPattern.npoints; k++) {
                    Direction currentDirection = currentPattern.getDirection(k, true);
                    if (!directionMap.containsKey(currentDirection)) {
                        directionMap.put(currentDirection, new ArrayList());
                    }
                    directionMap.get(currentDirection).add(new Identifier(i, j, k));
                }
            }
        }
        ArrayList<PolygonEX> frame = new ArrayList<>();
        while (true) {
            int[] xpoints, ypoints;
            int npoints;
            npoints = scanner.nextInt();
            if (npoints == 810) {
                break;
            }
            xpoints = new int[npoints];
            ypoints = new int[npoints];
            for (int i = 0; i < npoints; i++) {
                xpoints[i] = scanner.nextInt();
                ypoints[i] = scanner.nextInt();
            }
            PolygonEX currentLimit = new PolygonEX(xpoints, ypoints, npoints);
            currentLimit.reverse();
            frame.add(currentLimit);
        }
        Node start = new Node(frame, new PolygonEX[npieces]);
        
        nQRs = scanner.nextInt();
        for (int i = 0; i < nQRs; i++) {
            int ncurrentHints = scanner.nextInt();
            nhints += ncurrentHints;
            for (int j = 0; j < ncurrentHints; j++) {
                int[] xpoints, ypoints;
                int npoints;
                npoints = scanner.nextInt();
                xpoints = new int[npoints];
                ypoints = new int[npoints];
                for (int k = 0; k < npoints; k++) {
                    xpoints[k] = scanner.nextInt();
                    ypoints[k] = scanner.nextInt();
                }
                int ipiece = 0;
                PolygonEX hint = new PolygonEX(xpoints, ypoints, npoints);
                VectorPolygon vectorHint = hint.getVectorPolygon();
                check:
                for (int k = 0; k < npieces; k++) {
                    if (start.patterns[k] != null) {
                        continue;
                    }
                    for (PolygonEX pattern : pieces.get(k).patterns) {
                        if (pattern.getVectorPolygon().matches(vectorHint)) {
                            ipiece = k;
                            break check;
                        }
                    }
                }
                start = new Node(start, hint, ipiece);
                start.init();
            }
        }

        Node solution = getSolution(start);

        System.out.println(solution);
    }

    Node getSolution(Node node) {
        if (node.npieces == npieces) {
            return node;
        }
        int minSize = 114514;
        ArrayList<Node> branch = null;
        for (PolygonEX currentLimit : node.frame) {
            for (int i = 0; i < currentLimit.npoints; i++) {
                if (currentLimit.getDirection(i, false).getOpposite().getClockAngle(currentLimit.getDirection(i, true)) > Math.PI) {
                    continue;
                }
                int x = currentLimit.xpoints[i];
                int y = currentLimit.ypoints[i];
                ArrayList<Node> forwardNodeList = getFittablePatterns(node, x, y, true, directionMap.get(currentLimit.getDirection(i, true).getOpposite()));
                ArrayList<Node> oppositeNodeList = getFittablePatterns(node, x, y, false, directionMap.get(currentLimit.getDirection(i, false)));

                if (forwardNodeList.isEmpty() || oppositeNodeList.isEmpty()) {
                    return null;
                }
                if (currentLimit.getDirection(i, false).getOpposite().getClockAngle(currentLimit.getDirection(i, true)) < Math.PI) {
                    if (forwardNodeList.size() < minSize) {
                        minSize = forwardNodeList.size();
                        branch = forwardNodeList;
                    }
                    if (oppositeNodeList.size() < minSize) {
                        minSize = oppositeNodeList.size();
                        branch = oppositeNodeList;
                    }
                } else {
                    forwardNodeList.addAll(oppositeNodeList);
                    if (forwardNodeList.size() <= minSize) {
                        minSize = forwardNodeList.size();
                        branch = forwardNodeList;
                    }
                }
            }
        }
        for (Node currentNode : branch) {
            currentNode.init();
            Node solution = getSolution(currentNode);
            if (solution != null) {
                return solution;
            }
        }
        return null;
    }

    ArrayList<Node> getFittablePatterns(Node node, int x, int y, boolean isForward, ArrayList<Identifier> identifierList) {
        ArrayList<Node> nodeList = new ArrayList();
        for (Identifier currentIdentifier : identifierList) {
            if (node.patterns[currentIdentifier.piece] != null) {
                continue;
            }
            PolygonEX currentPattern = pieces.get(currentIdentifier.piece).getTranslated(currentIdentifier, x, y, isForward);
            if (node.frame.stream().allMatch(t -> t.contains(currentPattern))) {
                nodeList.add(new Node(node, currentPattern, currentIdentifier.piece));
            }
        }
        return nodeList;
    }
}
