import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

    int npieces;
    int xmax, ymax;
    int xmin, ymin;
    ArrayList<Polygon> pieces;

    @Override
    public void paint(Graphics g) {
        int xoffset = 20, yoffset = 60;
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;
        double xmag = (double) (width - 2 * xoffset) / (xmax - xmin), ymag = (double) (height - xoffset - yoffset) / (ymax - ymin);
        double magnification = xmag > ymag ? ymag : xmag;
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.translate(xoffset, yoffset);
        g.translate((int) (-xmin * magnification), (int) (-ymin * magnification));
        g.setColor(Color.darkGray);
        for (int i = 0; i < npieces; i++) {
            g.fillPolygon(getScaledPolygon(pieces.get(i), magnification));
        }

        g.translate((int) (xmin * magnification), (int) (ymin * magnification));
        g.setColor(Color.gray);
        for (int i = 0; i <= xmax - xmin; i++) {
            g.drawLine((int) (i * magnification), 0, (int) (i * magnification), (int) ((ymax - ymin) * magnification));
        }
        for (int i = 0; i <= ymax - ymin; i++) {
            g.drawLine(0, (int) (i * magnification), (int) ((xmax - xmin) * magnification), (int) (i * magnification));
        }
        
        g.translate((int) (-xmin * magnification), (int) (-ymin * magnification));
        g.setColor(Color.white);
        for (int i = 0; i < npieces; i++) {
            g.drawPolygon(getScaledPolygon(pieces.get(i), magnification));
        }
    }

    Polygon getScaledPolygon(Polygon polygon, double magnification) {
        int[] xpoints = new int[polygon.npoints];
        int[] ypoints = new int[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            xpoints[i] = (int) (polygon.xpoints[i] * magnification);
            ypoints[i] = (int) (polygon.ypoints[i] * magnification);
        }
        return new Polygon(xpoints, ypoints, polygon.npoints);
    }

    MainFrame(int xmax, int ymax, int xmin, int ymin, ArrayList<Polygon> pieces) {
        this.xmax = xmax;
        this.ymax = ymax;
        this.xmin = xmin;
        this.ymin = ymin;
        this.pieces = pieces;
        this.npieces = pieces.size();
        setBounds(200, 100, 1280, 900);
        setVisible(true);
    }
}
