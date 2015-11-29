package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;

/**
 * Created by Sigit A on 11/15/2015.
 */
public class ColorMap {
    private String grup;
    private ArrayList<ColorPixel> map;
    private ColorPixel representative;
    private double radious;

    public ColorMap() {
        map = new ArrayList<>();
        representative = new ColorPixel();
    }

    public void setRadious(int r) {
        radious = r;
    }

    public double getRadious() {
        return radious;
    }

    public void addMap(ColorPixel clr) {
        map.add(clr);
    }

    public ArrayList<ColorPixel> getMap(){
        return map;
    }

    public void removeMap(ColorPixel clr) {
        map.remove(clr);
    }

    public void setGrup(String name) {
        grup = name;
    }

    public void setRepresentative(ColorPixel p) {
        representative = p;
    }

    public String getGrup() {
        return grup;
    }

    public ColorPixel getRepresentative() {
        return representative;
    }

    public double matchPixel(ColorPixel input) {
        double min = 4096;
        for (int i = 0; i < map.size(); i++) {
            ColorPixel recent = map.get(i);
            double temp = Math.sqrt(Math.pow(input.r - recent.r, 2) + Math.pow(input.g - recent.g, 2) + Math.pow(input.b - recent.b, 2));
            if (temp < min && temp<radious) {
                min = temp;
            }
        }
        return min;
    }
}
