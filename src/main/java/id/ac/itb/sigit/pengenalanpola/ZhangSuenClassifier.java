package id.ac.itb.sigit.pengenalanpola;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sigit A on 10/18/2015.
 */
public class ZhangSuenClassifier {
    private static final Logger log = LoggerFactory.getLogger(ThinningAlgorithmContainer.class);
    private Feature feature;
    private boolean outputHuruf[];
    private boolean outputAngka[];
    private char[] indexHuruf = {
            'A',
            'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z'
    };

    public ZhangSuenClassifier() {
        outputHuruf = new boolean[26];
        outputAngka = new boolean[10];
    }

    public void setFeature(Feature f) {
        this.feature = f;
    }

    public void setOutputHurus() {
        outputHuruf[0] = classifyA();
        outputHuruf[1] = classifyB();
        outputHuruf[3] = classifyD();
        outputHuruf[4] = classifyE();
        outputHuruf[5] = classifyF();
        outputHuruf[7] = classifyH();
        outputHuruf[11] = classifyL();
        outputHuruf[15] = classifyP();
        outputHuruf[23] = classifyX();
        outputHuruf[24] = classifyY();
    }

    public boolean classifyA() {
        //kamus 1
        if (feature.getIntersection().size() == 2 && feature.getUjung().size() == 2 && feature.getBulatan() == 1) {
            if (feature.getIntersection().get(0).count == 3 && feature.getIntersection().get(1).count == 3) {
                return true;
            }
        }
        //kamus 2
        if (feature.getIntersection().size() == 3 && feature.getUjung().size() == 2 && feature.getBulatan() == 1) {
            if (feature.getIntersection().get(0).count == 3 && feature.getIntersection().get(1).count == 3 && feature.getIntersection().get(2).count == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean classifyB() {
        if (feature.getIntersection().size() == 2 && feature.getUjung().size() == 0 && feature.getBulatan() == 2) {
            return true;
        }
        return false;
    }

    public boolean classifyD() {
        if (feature.getIntersection().size() == 0 && feature.getUjung().size() == 0 && feature.getBulatan() == 1) {
            return true;
        }
        return false;
    }

    public boolean classifyE() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 3 && feature.getBulatan() == 0) {
            if (isBawahKiri(feature.getIntersection().get(0)) || isAtasKiri(feature.getIntersection().get(0)) && isAtasKanan(feature.getUjung().get(0)) && (isAtasKanan(feature.getUjung().get(1)) || isBawahKanan(feature.getUjung().get(1))) && isBawahKanan(feature.getUjung().get(2)))
                return true;
        }
        return false;
    }

    public boolean classifyF() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 3 && feature.getBulatan() == 0) {
            if (isAtasKanan(feature.getUjung().get(0)) && (isAtasKanan(feature.getUjung().get(1)) || isBawahKanan(feature.getUjung().get(1))) && isBawahKiri(feature.getUjung().get(2)))
                return true;
        }
        return false;
    }

    public boolean classifyH() {
        if (feature.getIntersection().size() == 2 && feature.getUjung().size() == 4 && feature.getBulatan() == 0) {
            int ataskiri = 0;
            int ataskanan = 0;
            int bawahkiri = 0;
            int bawahkanan = 0;
            int intersectKanan = 0;
            int intersectKiri = 0;
            for (int i = 0; i < feature.getUjung().size(); i++) {
                if (isAtasKiri(feature.getUjung().get(i))) {
                    ataskiri++;
                }
                if (isAtasKanan(feature.getUjung().get(i))) {
                    ataskanan++;
                }
                if (isBawahKanan(feature.getUjung().get(i))) {
                    bawahkanan++;
                }
                if (isBawahKiri(feature.getUjung().get(i))) {
                    bawahkiri++;
                }
            }
            for (int i = 0; i < feature.getIntersection().size(); i++) {
                if (isAtasKanan(feature.getIntersection().get(i)) || isBawahKanan(feature.getIntersection().get(i))) {
                    intersectKanan++;
                }
                if (isBawahKiri(feature.getIntersection().get(i)) || isAtasKiri(feature.getIntersection().get(i))) {
                    intersectKiri++;
                }
            }
            if (ataskanan == 1 && ataskiri == 1 && bawahkanan == 1 && bawahkiri == 1 && intersectKanan == 1 && intersectKiri == 1)
                return true;
        }
        return false;
    }

    public boolean classifyL() {
        if (feature.getIntersection().size() == 0 && feature.getUjung().size() == 2 && feature.getBulatan() == 0) {
            int ataskiri = 0;
            int bawahkanan = 0;
            for (int i = 0; i < feature.getUjung().size(); i++) {
                if (isAtasKiri(feature.getUjung().get(i))) ataskiri++;
                if (isBawahKanan(feature.getUjung().get(i))) bawahkanan++;
            }
            if (ataskiri == 1 && bawahkanan == 1) return true;
        }
        return false;
    }

    public boolean classifyP() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 1 && feature.getBulatan() == 1) {
            if (isBawahKiri(feature.getUjung().get(0)) && (isBawahKiri(feature.getIntersection().get(0)) || isAtasKiri(feature.getIntersection().get(0)))) {
                return true;
            }
        }
        return false;
    }

    public boolean classifyX() {
        if (feature.getIntersection().size() == 2 && feature.getUjung().size() == 4 && feature.getBulatan() == 0) {
            if (feature.getIntersection().get(0).count == 3 && feature.getIntersection().get(1).count == 3) return true;
        }
        return false;
    }

    public boolean classifyY() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 3 && feature.getBulatan() == 0) {
            int countKananAtas = 0;
            int countKiriAtas = 0;
            int countBawah = 0;
            for (int i = 0; i < feature.getUjung().size(); i++) {
                if (isAtasKanan(feature.getUjung().get(i))) {
                    countKananAtas++;
                }
                if (isAtasKiri(feature.getUjung().get(i))) {
                    countKiriAtas++;
                }
                if (isBawahKiri(feature.getUjung().get(i)) || isBawahKanan(feature.getUjung().get(i))) {
                    countBawah++;
                }
            }
            if (countBawah == 1 && countKananAtas == 1 && countKiriAtas == 1) {
                return true;
            }
        }
        return false;
    }

    public void setOutputAngka() {
        outputAngka[0] = classify0();
        outputAngka[1] = classify1();
        outputAngka[2] = classify2();
        outputAngka[3] = classify3();
        outputAngka[4] = classify4();
        outputAngka[5] = classify5();
        outputAngka[6] = classify6();
        outputAngka[7] = classify7();
        outputAngka[8] = classify8();
        outputAngka[9] = classify9();
    }

    public boolean classify0() {
        if (feature.getIntersection().size() == 0 && feature.getBulatan() == 1 && feature.getUjung().size() == 0) {
            return true;
        }
        return false;
    }

    public boolean classify1() {
        return false;
    }

    public boolean classify2() {
        return false;
    }

    public boolean classify3() {
        return false;
    }

    public boolean classify4() {
        return false;
    }

    public boolean classify5() {
        return false;
    }

    public boolean classify6() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 1 && feature.getBulatan() == 1)
            if (isAtasKanan(feature.getUjung().get(0)) && isBawahKiri(feature.getIntersection().get(0))) {
                return true;
            }
        return false;
    }

    public boolean classify7() {
        return false;
    }

    public boolean classify8() {
        return false;
    }

    public boolean classify9() {
        if (feature.getIntersection().size() == 1 && feature.getUjung().size() == 1 && feature.getBulatan() == 1) {
            if (isBawahKiri(feature.getUjung().get(0)) && (isBawahKanan(feature.getIntersection().get(0)) || isAtasKanan(feature.getIntersection().get(0)))) {
                return true;
            }
        }
        return false;
    }

    public String getOutputHuruf() {
        String result = "Huruf : ";
        for (int i = 0; i < outputHuruf.length; i++) {
            if (outputHuruf[i]) {
                result = result + indexHuruf[i] + " ";
            }
        }
        result = result + "\n";
        return result;
    }

    public String getOutputAngka() {
        String result = "Angka : ";
        for (int i = 0; i < outputAngka.length; i++) {
            if (outputAngka[i]) {
                result = result + i + " ";
            }
        }
        result = result + "\n";
        return result;
    }

    /*
     *Input specific point
     */
    public Koordinat generateAtasKanan() {
        Koordinat k = new Koordinat();
        k.x = feature.getMinX();
        k.y = feature.getMaxY();
        return k;
    }

    public Koordinat generateAtasKiri() {
        Koordinat k = new Koordinat();
        k.x = feature.getMinX();
        k.y = feature.getMinY();
        return k;
    }

    public Koordinat generateBawahKiri() {
        Koordinat k = new Koordinat();
        k.x = feature.getMaxX();
        k.y = feature.getMinY();
        return k;
    }

    public Koordinat generateBawahKanan() {
        Koordinat k = new Koordinat();
        k.x = feature.getMaxX();
        k.y = feature.getMaxY();
        return k;
    }

    public boolean isAtasKanan(Koordinat koordinat) {
        double dxAtasKanan = Math.abs(koordinat.x - generateAtasKanan().x);
        double dyAtasKanan = Math.abs(koordinat.y - generateAtasKanan().y);
        double dxAtasKiri = Math.abs(koordinat.x - generateAtasKiri().x);
        double dyAtasKiri = Math.abs(koordinat.y - generateAtasKiri().y);
        double dxBawahKiri = Math.abs(koordinat.x - generateBawahKiri().x);
        double dyBawahKiri = Math.abs(koordinat.y - generateBawahKiri().y);
        double dxBawahKanan = Math.abs(koordinat.x - generateBawahKanan().x);
        double dyBawahKanan = Math.abs(koordinat.y - generateBawahKanan().y);

        double dAtasKanan = Math.sqrt(dxAtasKanan * dxAtasKanan + dyAtasKanan * dyAtasKanan);
        double dAtasKiri = Math.sqrt(dxAtasKiri * dxAtasKiri + dyAtasKiri * dyAtasKiri);
        double dBawahKanan = Math.sqrt(dxBawahKanan * dxBawahKanan + dyBawahKanan * dyBawahKanan);
        double dBawahKiri = Math.sqrt(dxBawahKiri * dxBawahKiri + dyBawahKiri * dyBawahKiri);

        if (dAtasKanan <= dAtasKiri && dAtasKanan <= dBawahKanan && dAtasKanan <= dBawahKiri) return true;

        return false;
    }

    public boolean isAtasKiri(Koordinat koordinat) {
        double dxAtasKanan = Math.abs(koordinat.x - generateAtasKanan().x);
        double dyAtasKanan = Math.abs(koordinat.y - generateAtasKanan().y);
        double dxAtasKiri = Math.abs(koordinat.x - generateAtasKiri().x);
        double dyAtasKiri = Math.abs(koordinat.y - generateAtasKiri().y);
        double dxBawahKiri = Math.abs(koordinat.x - generateBawahKiri().x);
        double dyBawahKiri = Math.abs(koordinat.y - generateBawahKiri().y);
        double dxBawahKanan = Math.abs(koordinat.x - generateBawahKanan().x);
        double dyBawahKanan = Math.abs(koordinat.y - generateBawahKanan().y);

        double dAtasKanan = Math.sqrt(dxAtasKanan * dxAtasKanan + dyAtasKanan * dyAtasKanan);
        double dAtasKiri = Math.sqrt(dxAtasKiri * dxAtasKiri + dyAtasKiri * dyAtasKiri);
        double dBawahKanan = Math.sqrt(dxBawahKanan * dxBawahKanan + dyBawahKanan * dyBawahKanan);
        double dBawahKiri = Math.sqrt(dxBawahKiri * dxBawahKiri + dyBawahKiri * dyBawahKiri);

        if (dAtasKiri <= dAtasKanan && dAtasKiri <= dBawahKanan && dAtasKiri <= dBawahKiri) return true;
        return false;
    }

    public boolean isBawahKanan(Koordinat koordinat) {
        double dxAtasKanan = Math.abs(koordinat.x - generateAtasKanan().x);
        double dyAtasKanan = Math.abs(koordinat.y - generateAtasKanan().y);
        double dxAtasKiri = Math.abs(koordinat.x - generateAtasKiri().x);
        double dyAtasKiri = Math.abs(koordinat.y - generateAtasKiri().y);
        double dxBawahKiri = Math.abs(koordinat.x - generateBawahKiri().x);
        double dyBawahKiri = Math.abs(koordinat.y - generateBawahKiri().y);
        double dxBawahKanan = Math.abs(koordinat.x - generateBawahKanan().x);
        double dyBawahKanan = Math.abs(koordinat.y - generateBawahKanan().y);

        double dAtasKanan = Math.sqrt(dxAtasKanan * dxAtasKanan + dyAtasKanan * dyAtasKanan);
        double dAtasKiri = Math.sqrt(dxAtasKiri * dxAtasKiri + dyAtasKiri * dyAtasKiri);
        double dBawahKanan = Math.sqrt(dxBawahKanan * dxBawahKanan + dyBawahKanan * dyBawahKanan);
        double dBawahKiri = Math.sqrt(dxBawahKiri * dxBawahKiri + dyBawahKiri * dyBawahKiri);

        if (dBawahKanan <= dAtasKanan && dBawahKanan <= dAtasKiri && dBawahKanan <= dBawahKiri) return true;
        return false;
    }

    public boolean isBawahKiri(Koordinat koordinat) {
        double dxAtasKanan = Math.abs(koordinat.x - generateAtasKanan().x);
        double dyAtasKanan = Math.abs(koordinat.y - generateAtasKanan().y);
        double dxAtasKiri = Math.abs(koordinat.x - generateAtasKiri().x);
        double dyAtasKiri = Math.abs(koordinat.y - generateAtasKiri().y);
        double dxBawahKiri = Math.abs(koordinat.x - generateBawahKiri().x);
        double dyBawahKiri = Math.abs(koordinat.y - generateBawahKiri().y);
        double dxBawahKanan = Math.abs(koordinat.x - generateBawahKanan().x);
        double dyBawahKanan = Math.abs(koordinat.y - generateBawahKanan().y);

        double dAtasKanan = Math.sqrt(dxAtasKanan * dxAtasKanan + dyAtasKanan * dyAtasKanan);
        double dAtasKiri = Math.sqrt(dxAtasKiri * dxAtasKiri + dyAtasKiri * dyAtasKiri);
        double dBawahKanan = Math.sqrt(dxBawahKanan * dxBawahKanan + dyBawahKanan * dyBawahKanan);
        double dBawahKiri = Math.sqrt(dxBawahKiri * dxBawahKiri + dyBawahKiri * dyBawahKiri);
        log.info("atasKanan = {} atasKiri = {} bawahKanan = {} bawahKiri = {}", dAtasKanan, dAtasKiri, dBawahKanan, dBawahKiri);
        if (dBawahKiri <= dAtasKanan && dBawahKiri <= dAtasKiri && dBawahKiri <= dBawahKanan) return true;
        return false;
    }
}
