package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 16/10/2015.
 */
public class ZhangSuenService {

    private ZhangSuen zhangSuen;
    private static final Logger log = LoggerFactory.getLogger(ChainCodeService.class);
    private opencv_core.Mat origMat;
    private opencv_core.Mat grayOrigMat;
    private opencv_core.Mat zhangSuenMat;
    private boolean flag[][];
    private List<ZhangSuenFitur> zhangSuenFiturList;

    public opencv_core.Mat loadInput(File imageFile, int mode) {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        grayOrigMat = opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        if (mode == 1) {
            final InverseImageConverter inverseImage = new InverseImageConverter(grayOrigMat);
            grayOrigMat = inverseImage.getInverseImage();
        }
        zhangSuen = new ZhangSuen();
        zhangSuenMat = zhangSuen.process(grayOrigMat);
        getListZhangSuenFitur();
        return origMat;
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes, int mode) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
        grayOrigMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        if (mode == 1) {
            final InverseImageConverter inverseImage = new InverseImageConverter(grayOrigMat);
            grayOrigMat = inverseImage.getInverseImage();
        }
        zhangSuen = new ZhangSuen();
        zhangSuenMat = zhangSuen.process(grayOrigMat);
        getListZhangSuenFitur();
        return origMat;
    }

    public opencv_core.Mat getOrigMat() {
        return origMat;
    }

    public opencv_core.Mat getZhainSuenMat() {
        return zhangSuenMat;
    }

    //get objek pertama
    public ZhangSuenFitur getZhangSuenFitur() {
        if (zhangSuenFiturList == null) {
            return new ZhangSuenFitur();
        }
        return zhangSuenFiturList.get(0);
    }

    private List<ZhangSuenFitur> getListZhangSuenFitur() {
        flag = new boolean[zhangSuenMat.rows()][zhangSuenMat.cols()];
        zhangSuenFiturList = new ArrayList<>();
        final ByteIndexer imgIdx = zhangSuenMat.createIndexer();

        // imgIdx.put(0,1,(byte)1);


        for (int y = 0; y < zhangSuenMat.rows(); y++) {
            for (int x = 0; x < zhangSuenMat.cols(); x++) {
                int pxl = Byte.toUnsignedInt(imgIdx.get(y, x));
                if (pxl > 250 && !flag[y][x]) {
                    ZhangSuenFitur zhangSuenFitur = new ZhangSuenFitur();
                    zhangSuenFitur = prosesZhangSuenFitur(x, y, imgIdx, zhangSuenFitur);
                    zhangSuenFiturList.add(zhangSuenFitur);
                    log.info("zhangSuenFitur object #{} at ({}, {}): {}", zhangSuenFitur, x, y, zhangSuenFitur.getSimpangan());
                }
            }
        }

        return zhangSuenFiturList;
    }

    private ZhangSuenFitur prosesZhangSuenFitur(int x, int y, ByteIndexer idxImg, ZhangSuenFitur zhangSuenFitur) {
        if (flag[y][x]) {
            //buletan
            zhangSuenFitur.setBulatan(zhangSuenFitur.getBulatan() + 1);
            return zhangSuenFitur;
        }

        // flag[col][row]=true;
        flag[y][x] = true;

        if ((y - 1) < 0 || (x - 1) < 0 ||
                (y + 1) >= idxImg.rows() || (x + 1) >= idxImg.cols()) {
            return zhangSuenFitur;
        }

        List<Edge> dataTetangga = new ArrayList<>();

        Edge edge1 = new Edge(x - 1, y - 1);
        edge1.setvalue(idxImg.get(y - 1, x - 1));
        dataTetangga.add(edge1);

        Edge edge2 = new Edge(x, y - 1);
        edge2.setvalue(idxImg.get(y - 1, x));
        dataTetangga.add(edge2);

        Edge edge3 = new Edge(x + 1, y - 1);
        edge3.setvalue(idxImg.get(y - 1, x + 1));
        dataTetangga.add(edge3);

        Edge edge4 = new Edge(x + 1, y);
        edge4.setvalue(idxImg.get(y, x + 1));
        dataTetangga.add(edge4);

        Edge edge5 = new Edge(x + 1, y + 1);
        edge5.setvalue(idxImg.get(y + 1, x + 1));
        dataTetangga.add(edge5);

        Edge edge6 = new Edge(x, y + 1);
        edge6.setvalue(idxImg.get(y + 1, x));
        dataTetangga.add(edge6);

        Edge edge7 = new Edge(x - 1, y + 1);
        edge7.setvalue(idxImg.get(y + 1, x - 1));
        dataTetangga.add(edge7);

        Edge edge8 = new Edge(x - 1, y);
        edge8.setvalue(idxImg.get(y, x - 1));
        dataTetangga.add(edge8);

        List<Edge> nextStep = new ArrayList<>();

        int temppotition = -1;

        boolean ujung = false;
        boolean cabang = true;

        for (int i = 0; i < dataTetangga.size(); i++) {
            if (dataTetangga.get(i).getvalue() != 0) {
                if (temppotition < 0) {
                    ujung = true;
                    nextStep.add(dataTetangga.get(i));
                    temppotition = i;
                } else {
                    ujung = false;
                    nextStep.add(dataTetangga.get(i));
                    int cek = i - temppotition;
                    if (cek < 2) {
                        cabang = false;
                    }
                    temppotition = i;
                }

                if (i == dataTetangga.size() - 1 && dataTetangga.get(0).getvalue() != 0) {
                    cabang = false;
                }
            }
        }
        if (nextStep.size() == 0) {
            //cuma 1 pixel;
            return zhangSuenFitur;
        } else if (ujung && nextStep.size() < 2) {
            //ujung
            ZhangSuenUjung zhangSuenUjung = new ZhangSuenUjung();
            zhangSuenUjung.setEdge(nextStep.get(0));
            zhangSuenFitur.getUjung().add(zhangSuenUjung);
            zhangSuenFitur = prosesZhangSuenFitur(nextStep.get(0).getX(), nextStep.get(0).getY(), idxImg, zhangSuenFitur);
            return zhangSuenFitur;
        } else if (cabang && nextStep.size() > 2) {
            //cabang

            ZhangSuenSimpangan zhangSuenSimpangan = new ZhangSuenSimpangan();
            zhangSuenSimpangan.setEdge(new Edge(y, x));
            zhangSuenSimpangan.getPoints().addAll(nextStep);
            zhangSuenFitur.getSimpangan().add(zhangSuenSimpangan);
            for (int i = 0; i < nextStep.size(); i++) {
                if (!flag[nextStep.get(i).getY()][nextStep.get(i).getX()]) {
                    zhangSuenFitur = prosesZhangSuenFitur(nextStep.get(i).getX(), nextStep.get(i).getY(), idxImg, zhangSuenFitur);
                }
            }
            return zhangSuenFitur;
        } else {
            for (int i = 0; i < nextStep.size(); i++) {
                if (!flag[nextStep.get(i).getY()][nextStep.get(i).getX()]) {
                    zhangSuenFitur = prosesZhangSuenFitur(nextStep.get(i).getX(), nextStep.get(i).getY(), idxImg, zhangSuenFitur);
                }
            }
            return zhangSuenFitur;
        }
    }
}
