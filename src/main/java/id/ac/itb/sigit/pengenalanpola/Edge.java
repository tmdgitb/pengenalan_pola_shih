package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;

/**
 * Created by Sigit on 15/10/2015.
 */
public class Edge implements Serializable {

    private int x;
    private int y;
    private  byte value;

    public Edge(int x, int y) {
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

    public byte getvalue() {
        return value;
    }

    public void setvalue(byte status) {
        this.value = status;
    }
}
