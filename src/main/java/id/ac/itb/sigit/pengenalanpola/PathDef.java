package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 15/10/2015.
 */
public class PathDef implements Serializable {
    private List<Loop> loops = new ArrayList<>();
    private List<Intersection> intersections = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public List<Loop> getLoops() {
        return loops;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
