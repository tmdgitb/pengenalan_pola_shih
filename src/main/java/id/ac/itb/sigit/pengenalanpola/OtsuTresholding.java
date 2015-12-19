package id.ac.itb.sigit.pengenalanpola;

/**
 * Created by Sigit A on 10/29/2015.
 */
public class OtsuTresholding {
    private Histogram2 hist;
    private int thresholdValue;

    public void setHist(Histogram2 hist){
        this.hist = hist;
    }

    public void findThreshold(){
        double tMean = 0;
        double variance = 0;
        double maxVariance = 0;
        double firstCM = 0;
        double zerothCM = 0;

        for (int k = 0; k<256 ; k++){
            tMean = (double)k*(double)hist.lv[3][k]/(double)hist.totalPix;
        }

        for (int k = 0; k<256 ; k++){
            zerothCM += (double)hist.lv[3][k] / (double)hist.totalPix;
            firstCM += (double)k *(double) hist.lv[3][k] / (double)hist.totalPix;
            variance = (tMean * zerothCM - firstCM);
            variance *= variance;
            variance /= zerothCM * (1 - zerothCM);
            if (maxVariance < variance){
                maxVariance = variance;
                thresholdValue = k;
            }
        }
    }

    public int getThresholdValue(){
        return this.thresholdValue;
    }

    public void findThreshold2() {
        double sum = 0;
        for (int i = 1; i < 256; ++i)
            sum += i * hist.lv[3][i];
        double sumB = 0;
        double wB = 0;
        double wF = 0;
        double mB;
        double mF;
        double max = 0.0;
        double between = 0.0;
        double threshold1 = 0.0;
        double threshold2 = 0.0;
        for (int i = 0; i < 256; ++i) {
            wB += hist.lv[3][i];
            if (wB == 0)
                continue;
            wF = hist.totalPix - wB;
            if (wF == 0)
                break;
            sumB += i * hist.lv[3][i];
            mB = sumB / wB;
            mF = (sum - sumB) / wF;
            between = wB * wF * (mB - mF) * (mB - mF);
            if ( between >= max ) {
                threshold1 = i;
                if ( between > max ) {
                    threshold2 = i;
                }
                max = between;
            }
        }
        thresholdValue = (int)(( threshold1 + threshold2 ) / 2.0);
    }

    public void findThreshold3(){
        int total = hist.totalPix;
        float sum = 0;
        for(int i=0; i<256; i++) sum += i * hist.lv[3][i];
        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;

        for(int i=0 ; i<256 ; i++) {
            wB += hist.lv[3][i];
            if(wB == 0) continue;
            wF = total - wB;

            if(wF == 0) break;

            sumB += (float) (i * hist.lv[3][i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if(varBetween > varMax) {
                varMax = varBetween;
                thresholdValue = i;
            }
        }
    }
}
