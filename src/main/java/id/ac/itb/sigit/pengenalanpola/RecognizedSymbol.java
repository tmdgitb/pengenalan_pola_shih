package id.ac.itb.sigit.pengenalanpola;

/**
 * Created by Sigit on 17/10/2015.
 */
public class RecognizedSymbol {
    private String name;
    private Geometry geometry;
    private double confidence;

    public RecognizedSymbol(String name, Geometry geometry, double confidence) {
        this.name = name;
        this.geometry = geometry;
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
