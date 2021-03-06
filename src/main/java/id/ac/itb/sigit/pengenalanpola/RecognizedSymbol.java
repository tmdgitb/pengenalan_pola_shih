package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;

/**
 * Created by Sigit on 17/10/2015.
 */
public class RecognizedSymbol implements Serializable {
    private String name;
    private Geometry geometry;
    private double confidence;
    private String resampledDfcce;
    private String trainingDfcce;

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

    public String getResampledDfcce() {
        return resampledDfcce;
    }

    public void setResampledDfcce(String resampledDfcce) {
        this.resampledDfcce = resampledDfcce;
    }

    public String getTrainingDfcce() {
        return trainingDfcce;
    }

    public void setTrainingDfcce(String trainingDfcce) {
        this.trainingDfcce = trainingDfcce;
    }
}
