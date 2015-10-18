package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 18/10/2015.
 */
public class ZhangSuenSimpangan {
    private Edge edge;
    List<Edge> points=new ArrayList<>();

    public Edge getEdge() {
        return edge;
    }
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public List<Edge> getPoints() {
        return points;
    }
}
