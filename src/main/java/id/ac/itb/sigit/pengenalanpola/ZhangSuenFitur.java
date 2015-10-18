package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 18/10/2015.
 */
public class ZhangSuenFitur {
    private int bulatan=0;
    private List<ZhangSuenUjung> ujung=new ArrayList<>();
    private  List<ZhangSuenSimpangan> simpangan=new ArrayList<>();

    public int getBulatan() {
        return bulatan;
    }
    public void setBulatan(int bulatan) {
        this.bulatan = bulatan;
    }

    public List<ZhangSuenUjung> getUjung() {
        return ujung;
    }

    public List<ZhangSuenSimpangan> getSimpangan() {
        return simpangan;
    }


//    private class Simpangan {
//        private Edge edge;
//        List<Edge> points=new ArrayList<>();
//
//        public Edge getEdge() {
//            return edge;
//        }
//        public void setEdge(Edge edge) {
//            this.edge = edge;
//        }
//
//        public List<Edge> getPoints() {
//            return points;
//        }
//    }
//
//
//    private class Ujung {
//        private Edge edge;
//
//        public Edge getEdge() {
//            return edge;
//        }
//        public void setEdge(Edge edge) {
//            this.edge = edge;
//        }
//    }
}
