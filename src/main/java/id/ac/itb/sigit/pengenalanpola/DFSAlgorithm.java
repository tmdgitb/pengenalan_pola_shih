package id.ac.itb.sigit.pengenalanpola;


import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Sigit A on 10/16/2015.
 */
public class DFSAlgorithm {
    private static final Logger log = LoggerFactory.getLogger(DFSAlgorithm.class);
    private Mat image;
    private ArrayList<Feature> features;
    private boolean[][] mark;
    private byte[][] imageCpy;

    public DFSAlgorithm(Mat image) {
        features = new ArrayList<Feature>();
        this.image = image.clone();
        this.mark = new boolean[image.rows()][image.cols()];
        this.imageCpy = new byte[image.rows()][image.cols()];
        ByteIndexer binIdx=image.createIndexer();
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                byte[] data = new byte[1];
                binIdx.get(0, j, data);
                if (data[0] == -1) {
                    imageCpy[i][j] = 1;

                } else {
                    imageCpy[i][j] = 0;
                }
                mark[i][j] = false;
                System.out.print(imageCpy[i][j]+",");
            }
            System.out.println();
        }
    }

    public void processImage() {
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                if ((i > 0 && i < image.rows() - 1) && (j > 0 && j < image.cols() - 1)) {
                    if (isForeground(i, j) && !mark[i][j]) {
                        Feature f = new Feature();
                        f.setMaxX(i);
                        f.setMaxY(j);
                        f.setMinX(i);
                        f.setMinY(j);
                        DFSIteration(i, j, i, j, f);
                        features.add(f);
                    }
                }
            }
        }
    }

    private void DFSIteration(int i, int j, int tempi, int tempj, Feature f) {
        //log.info("i={} j={}",i,j);
        int intsect = cekIntersection(i, j);
        if (isForeground(i, j)) mark[i][j] = true;
        //==============Cari Pertigaan atau ++ ==============
        if (intsect > 2) {
            Koordinat k = new Koordinat();
            k.x = i;
            k.y = j;
            k.count = intsect;
            f.setIntersection(k);
        }
        //==============Cari ujung===========================
        if (intsect == 1) {
            Koordinat k = new Koordinat();
            k.x = i;
            k.y = j;
            f.setUjung(k);
        }
        //===============Cari Bulatan========================
        if (cekBulatan(i, j, tempi, tempj)) {
            f.setBulatan();
        }
        tempi = i;
        tempj = j;
        //===============Get Min Max X Y=====================
        if (i < f.getMinX()) {
            f.setMinX(i);
        }
        if (i > f.getMaxX()) {
            f.setMaxX(i);
        }
        if (j < f.getMinY()) {
            f.setMinY(j);
        }
        if (j > f.getMaxY()) {
            f.setMaxY(j);
        }
        //===================================================

        if ((i > 0 && i < image.rows() - 1) && (j > 0 && j < image.cols() - 1)) {
            if (isForeground(i - 1, j) && !mark[i - 1][j]) {
                DFSIteration(i - 1, j, tempi, tempj, f);
            }
            if (isForeground(i - 1, j + 1) && !mark[i - 1][j + 1]) {
                DFSIteration(i - 1, j + 1, tempi, tempj, f);
            }
            if (isForeground(i, j + 1) && !mark[i][j + 1]) {
                DFSIteration(i, j + 1, tempi, tempj, f);
            }
            if (isForeground(i + 1, j + 1) && !mark[i + 1][j + 1]) {
                DFSIteration(i + 1, j + 1, tempi, tempj, f);
            }
            if (isForeground(i + 1, j) && !mark[i + 1][j]) {
                DFSIteration(i + 1, j, tempi, tempj, f);
            }
            if (isForeground(i + 1, j - 1) && !mark[i + 1][j - 1]) {
                DFSIteration(i + 1, j - 1, tempi, tempj, f);
            }
            if (isForeground(i, j - 1) && !mark[i][j - 1]) {
                DFSIteration(i, j - 1, tempi, tempj, f);
            }
            if (isForeground(i - 1, j - 1) && !mark[i - 1][j - 1]) {
                DFSIteration(i - 1, j - 1, tempi, tempj, f);
            }
        }
    }

    private boolean isForeground(int i, int j) {
        return imageCpy[i][j] == 1;
    }

    private boolean cekBulatan(int i, int j, int tempi, int tempj) {
        if ((i > 0 && i < image.rows() - 1) && (j > 0 && j < image.cols() - 1)) {
            int tetangga[] = new int[8];
            tetangga[0] = imageCpy[i - 1][j];
            tetangga[1] = imageCpy[i - 1][j + 1];
            tetangga[2] = imageCpy[i][j + 1];
            tetangga[3] = imageCpy[i + 1][j + 1];
            tetangga[4] = imageCpy[i + 1][j];
            tetangga[5] = imageCpy[i + 1][j - 1];
            tetangga[6] = imageCpy[i][j - 1];
            tetangga[7] = imageCpy[i - 1][j - 1];

            //if (i != 0 || j != 0 || i != image.rows() - 1 || j != image.cols() - 1) {

            for (int x = 0; x < 8; x++) {
                if (x == 7) {
                    if (tetangga[7] == 1 && tetangga[0] == 1) {
                        return false;
                    }
                } else {
                    if (tetangga[x] == 1 && tetangga[x + 1] == 1) {
                        return false;
                    }
                }
            }


            boolean p[] = new boolean[8];
            int markindex = 0;
            p[0] = mark[i - 1][j];
            p[1] = mark[i - 1][j + 1];
            p[2] = mark[i][j + 1];
            p[3] = mark[i + 1][j + 1];
            p[4] = mark[i + 1][j];
            p[5] = mark[i + 1][j - 1];
            p[6] = mark[i][j - 1];
            p[7] = mark[i - 1][j - 1];
            //cari index sebelumnya
            if (tempi == i - 1 && tempj == j) {
                markindex = 0;
            } else if (tempi == i - 1 && tempj == j + 1) {
                markindex = 1;
            } else if (tempi == i && tempj == j + 1) {
                markindex = 2;
            } else if (tempi == i + 1 && tempj == j + 1) {
                markindex = 3;
            } else if (tempi == i + 1 && tempj == j) {
                markindex = 4;
            } else if (tempi == i + 1 && tempj == j - 1) {
                markindex = 5;
            } else if (tempi == i && tempj == j - 1) {
                markindex = 6;
            } else if (tempi == i - 1 && tempj == j - 1) {
                markindex = 7;
            }

            if (markindex == 0) {
                if (p[1] || p[2] || p[3] || p[4] || p[5] || p[6] || p[7]) return true;
            } else if (markindex == 1) {
                if (p[0] || p[2] || p[3] || p[4] || p[5] || p[6] || p[7]) return true;
            } else if (markindex == 2) {
                if (p[0] || p[1] || p[3] || p[4] || p[5] || p[6] || p[7]) return true;
            } else if (markindex == 3) {
                if (p[0] || p[1] || p[2] || p[4] || p[5] || p[6] || p[7]) return true;
            } else if (markindex == 4) {
                if (p[0] || p[1] || p[2] || p[3] || p[5] || p[6] || p[7]) return true;
            } else if (markindex == 5) {
                if (p[0] || p[1] || p[2] || p[3] || p[4] || p[6] || p[7]) return true;
            } else if (markindex == 6) {
                if (p[0] || p[1] || p[2] || p[3] || p[4] || p[5] || p[7]) return true;
            } else if (markindex == 7) {
                if (p[0] || p[1] || p[2] || p[3] || p[4] || p[5] || p[6]) return true;
            }
        }
        return false;
    }

    private int cekIntersection(int i, int j) {
        int intersection = 0;
        if ((i > 0 && i < image.rows() - 1) && (j > 0 && j < image.cols() - 1)){
            int p[] = new int[8];
            p[0] = imageCpy[i - 1][j];
            p[1] = imageCpy[i - 1][j + 1];
            p[2] = imageCpy[i][j + 1];
            p[3] = imageCpy[i + 1][j + 1];
            p[4] = imageCpy[i + 1][j];
            p[5] = imageCpy[i + 1][j - 1];
            p[6] = imageCpy[i][j - 1];
            p[7] = imageCpy[i - 1][j - 1];
            for (int x = 0; x < 8; x++) {
                if (x == 7) {
                    if (p[7] == 0 && p[0] == 1) {
                        intersection++;
                    }
                } else {
                    if (p[x] == 0 && p[x + 1] == 1) {
                        intersection++;
                    }
                }
            }
        }
        return intersection;
    }

    public String printInfo(){
        String result="===========================================\n";
        for (int i=0; i<features.size();i++){
            result = result + features.get(i).printMinMaxProperties();
            result = result + features.get(i).printIntersection();
            result = result + features.get(i).printUjung();
            result = result + features.get(i).printBulatan();
            ZhangSuenClassifier zc = new ZhangSuenClassifier();
            zc.setFeature(features.get(i));
            zc.setOutputAngka();
            zc.setOutputHurus();
            result = result + zc.getOutputHuruf();
            result = result + zc.getOutputAngka()+"\n";
        }
        result = result + "===========================================\n";
        //log.info("result = {}",result);
        return result;
    }
}


