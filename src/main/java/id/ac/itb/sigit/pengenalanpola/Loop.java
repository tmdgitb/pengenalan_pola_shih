package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;

/**
 * Created by Sigit on 15/10/2015.
 */
public class Loop implements Serializable {

    private int x;
    private int y;

    public Loop(int x, int y) {
        this.x = x;
        this.y = y;
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
