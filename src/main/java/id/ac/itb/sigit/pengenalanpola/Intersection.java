package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;

/**
 * Created by Sigit on 15/10/2015.
 */
public class Intersection implements Serializable {
    private int x;
    private int y;
    private int pathCount;

    public Intersection(int x, int y, int pathCount) {
        this.x = x;
        this.y = y;
        this.pathCount = pathCount;
    }

    /**
     * Number of paths in this intersection;
     * @return
     */
    public int getPathCount() {
        return pathCount;
    }

    public void setPathCount(int pathCount) {
        this.pathCount = pathCount;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
