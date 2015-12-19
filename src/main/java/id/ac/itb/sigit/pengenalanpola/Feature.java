package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;

/**
 * Created by Sigit A on 10/16/2015.
 */
public class Feature {
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private ArrayList<Koordinat> intersection;
    private ArrayList<Koordinat> ujung;
    private int bulatan;

    public Feature() {
        intersection = new ArrayList<>();
        ujung = new ArrayList<>();
        bulatan = 0;
        maxX = 0;
        maxY = 0;
        minX = 0;
        minY = 0;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public ArrayList<Koordinat> getUjung() {
        return ujung;
    }

    public void setUjung(Koordinat ujung) {
        this.ujung.add(ujung);
    }

    public int getBulatan() {
        return bulatan;
    }

    public void setBulatan() {
        this.bulatan++;
    }

    public ArrayList<Koordinat> getIntersection() {
        return this.intersection;
    }

    public void setIntersection(Koordinat intersect) {
        this.intersection.add(intersect);
    }

    public String printIntersection() {
        String result = "Intersection : ";
        for (int i = 0; i < intersection.size(); i++) {
            result = result + "(" + intersection.get(i).x + "," + intersection.get(i).y + ")" + intersection.get(i).count + " ";
        }
        result = result + "\n";
        return result;
    }

    public String printUjung() {
        String result = "Ujung : ";
        for (int i = 0; i < ujung.size(); i++) {
            result = result + "(" + ujung.get(i).x + "," + ujung.get(i).y + ") ";
        }
        result = result + "\n";
        return result;
    }

    public String printMinMaxProperties() {
        String result = "Properties : ";
        result = result + "Min X = "+getMinX()+", Max X = "+getMaxX()+", Min Y = "+getMinY()+", Max Y = "+getMaxY()+"\n";
        return result;
    }

    public String printBulatan(){
        String result = "Jumlah Lingkaran : ";
        result = result + bulatan +"\n";
        return result;
    }
}
