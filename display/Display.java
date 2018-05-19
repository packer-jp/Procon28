package display;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Display {

    public static void main(String[] args) {
        new Display().compute();
    }

    void compute() {
        String position = "";
        try {
            File file = new File("../datas/position2.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) {
                position += (br.readLine() + " ");
            }
            br.close();
        } catch (FileNotFoundException fe) {
        } catch (IOException ie) {
        }
        Scanner scanner = new Scanner(position);
        scanner.useDelimiter("[^-\\d]+");
        int[] xpoints, ypoints;
        int npoints;
        while (true) {
            int xmax = -114514;
            int ymax = -114514;
            int xmin = 114514;
            int ymin = 114514;
            ArrayList<Polygon> pieces = new ArrayList<>();
            int nQRs = scanner.nextInt();
            for (int i = 0; i < nQRs; i++) {
                int npieces = scanner.nextInt();
                for (int j = 0; j < npieces; j++) {
                    npoints = scanner.nextInt();
                    xpoints = new int[npoints];
                    ypoints = new int[npoints];
                    for (int k = 0; k < npoints; k++) {
                        xpoints[k] = scanner.nextInt();
                        ypoints[k] = scanner.nextInt();
                        xmax = xmax > xpoints[k] ? xmax : xpoints[k];
                        ymax = ymax > ypoints[k] ? ymax : ypoints[k];
                        xmin = xmin < xpoints[k] ? xmin : xpoints[k];
                        ymin = ymin < ypoints[k] ? ymin : ypoints[k];
                    }
                    pieces.add(new Polygon(xpoints, ypoints, npoints));
                }
            }
            new MainFrame(xmax, ymax, xmin, ymin, pieces);
        }
    }
}
