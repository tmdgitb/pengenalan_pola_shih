package id.ac.itb.sigit.pengenalanpola;

import com.google.common.collect.ImmutableList;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 03/10/2015.
 */
public class ChainCodeWhiteConverter {
    private static final Logger log = LoggerFactory.getLogger(ChainCodeWhiteConverter.class);
    private boolean flag[][];
    private int toleransiWhite = 230, toleransi = 150;
    private boolean searchObject = true, searchSubObject = false;
    private int minHor = 0, maxHor = 0, minVer = 0, maxVer = 0;
    private opencv_core.Mat imgMat;
    public List<Geometry> geometries;

    /**
     * @param data MUST BE GRAYSCALE
     */
    public ChainCodeWhiteConverter(opencv_core.Mat data) {
        this(data, "");
    }

    /**
     * @param data MUST BE GRAYSCALE
     * @param msg
     */
    public ChainCodeWhiteConverter(opencv_core.Mat data, String msg) {
        imgMat = data;
        geometries = new ArrayList<>();
        log.info("ukuran gambar {}{}", imgMat.size().height(), imgMat.size().width());
    }

    public List<Geometry> getChainCode() {
        flag = new boolean[imgMat.rows()][imgMat.cols()];
        int objectIdx = 0;

        final ByteIndexer imgIdx = imgMat.createIndexer();

        for (int y = 0; y < imgMat.rows(); y++) {
            for (int x = 0; x < imgMat.cols(); x++) {
                int grayScale = Byte.toUnsignedInt(imgIdx.get(y, x));

                if (grayScale > toleransiWhite && searchObject && !flag[y][x]) {
                    minVer = y;
                    maxVer = y;
                    minHor = x;
                    maxHor = x;
                    final AbsChainCode absChainCode = new AbsChainCode(prosesChaincode(y, x, 3, imgMat, 1));
                    Geometry geometry = new Geometry();

                    String kodeBelok = calculateKodeBelok(absChainCode.getFcce());
                    geometry.setAbsChainCode(absChainCode);
                    geometry.setKodeBelok(kodeBelok);
                    geometry.setX(x);
                    geometry.setY(y);

                    if (absChainCode.getDirs().size() > 20) {
                        log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, absChainCode);
                        objectIdx++;
                        List<Geometry> subGeometries = subObject(imgMat);
                        if (subGeometries.size() > 0) {
                            geometry.getSubGeometry().addAll(subGeometries);
                        }
                        geometries.add(geometry);
                    }
                    searchObject = false;
                }

                if (grayScale > toleransiWhite && flag[y][x]) {
                    if((x+1) >= imgMat.cols())
                    {
                        searchObject = true;
                    }
                    else {
                        int grayScale1 = Byte.toUnsignedInt(imgIdx.get(y, x + 1));

                        if (grayScale1 < toleransiWhite) {
                            searchObject = true;
                        } else {
                            searchObject = false;
                        }
                    }
                }
            }
        }

        return geometries;
    }

    private List<Geometry> subObject(opencv_core.Mat imgMat) {
        List<Geometry> subGeometries = new ArrayList<>();

        final ByteIndexer imgIdx = imgMat.createIndexer();
        try {
            for (int y = minVer; y < maxVer; y++) {
                for (int x = minHor; x < maxHor; x++) {
                    final int grayScale = Byte.toUnsignedInt(imgIdx.get(y, x));
                    final int nextGrayScale = Byte.toUnsignedInt(imgIdx.get(y, x + 1));
                    if (grayScale > toleransiWhite && flag[y][x]) {
                        if (nextGrayScale > toleransiWhite) {
                            searchSubObject = true;
                        } else {
                            searchSubObject = false;
                        }
                    }

                    if (grayScale < toleransi && searchSubObject && !flag[y][x]) {
                        final AbsChainCode chaincode2 = new AbsChainCode(prosesChaincode(y, x, 3, imgMat, 0));

                        final Geometry subGeometry = new Geometry();
                        subGeometry.setAbsChainCode(chaincode2);
                        subGeometries.add(subGeometry);

                        //charDef.getSubChainCode().add(chaincode2);
                        log.info("Chaincode subobject : {}", chaincode2);
                        searchSubObject = false;
                    }

                    if (grayScale < toleransi && flag[y][x]) {
                        if (nextGrayScale > toleransi) {
                            searchSubObject = true;
                        } else {
                            searchSubObject = false;
                        }
                    }
                }
            }
        } finally {
            imgIdx.release();
        }

        return subGeometries;
    }

    private List<AbsDirection> prosesChaincode(int row, int col, int arah, opencv_core.Mat imgMat, int mode) {
        if (flag[row][col]) {
            return ImmutableList.of();
        }
        flag[row][col] = true;

        final ByteIndexer imgIdx = imgMat.createIndexer();
        try {

            //kondisi perjalanan arah 1
            if (arah == 1) {
                //
                //cek arah 7 (samping kiri)
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
                //
                //cek arah 8
                //
                List<AbsDirection> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<AbsDirection> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

            }
            //kondisi perjalanan arah 2
            else if (arah == 2) {
                //
                //cek arah 8 (samping kiri)
                //
                List<AbsDirection> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<AbsDirection> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<AbsDirection> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

            } else if (arah == 3) {
                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<AbsDirection> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<AbsDirection> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<AbsDirection> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }
            } else if (arah == 4) {
                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<AbsDirection> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<AbsDirection> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<AbsDirection> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<AbsDirection> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
            } else if (arah == 5) {
                //
                //cek arah 3
                //
                List<AbsDirection> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<AbsDirection> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<AbsDirection> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<AbsDirection> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
            } else if (arah == 6) {
                //
                //cek arah 4
                //
                List<AbsDirection> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<AbsDirection> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<AbsDirection> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<AbsDirection> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }
            } else if (arah == 7) {
                //
                //cek arah 5
                //
                List<AbsDirection> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<AbsDirection> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<AbsDirection> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }
            } else //if(arah==8)
            {
                //
                //cek arah 6
                //
                List<AbsDirection> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<AbsDirection> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<AbsDirection> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<AbsDirection> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<AbsDirection> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }
            }
            return ImmutableList.of();
        } finally {
            imgIdx.release();
        }
    }


    private int grayScale(byte[] imagByte) {
        return Byte.toUnsignedInt(imagByte[0]);
    }

    /**
     * {@link AbsDirection#N}
     * @param row
     * @param col
     * @param imgMat
     * @param indexer
     * @param mode
     * @return
     */
    private List<AbsDirection> objectarahN(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col;

        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray1 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray1 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.N).addAll(prosesChaincode(temprow, tempcol, 1, imgMat, mode)).build();
            }
        } else {
            if (gray1 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.N).addAll(prosesChaincode(temprow, tempcol, 1, imgMat, mode)).build();
            }
        }
        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahNE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray2 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray2 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.NE)
                        .addAll(prosesChaincode(temprow, tempcol, 2, imgMat, mode)).build();
            }
        } else {
            if (gray2 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.NE)
                        .addAll(prosesChaincode(temprow, tempcol, 2, imgMat, mode)).build();
            }
        }
        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray3 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray3 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.E)
                        .addAll(prosesChaincode(temprow, tempcol, 3, imgMat, mode)).build();
            }
        } else {
            if (gray3 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.E)
                        .addAll(prosesChaincode(temprow, tempcol, 3, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahSE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray4 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray4 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.SE)
                        .addAll(prosesChaincode(temprow, tempcol, 4, imgMat, mode)).build();
            }
        } else {
            if (gray4 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.SE)
                        .addAll(prosesChaincode(temprow, tempcol, 4, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahS(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();
        final int gray5 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray5 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.S)
                        .addAll(prosesChaincode(temprow, tempcol, 5, imgMat, mode)).build();
            }
        } else {
            if (gray5 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.S)
                        .addAll(prosesChaincode(temprow, tempcol, 5, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahSW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray6 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray6 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.SW)
                        .addAll(prosesChaincode(temprow, tempcol, 6, imgMat, mode)).build();
            }
        } else {
            if (gray6 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.SW)
                        .addAll(prosesChaincode(temprow, tempcol, 6, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray7 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray7 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.W)
                        .addAll(prosesChaincode(temprow, tempcol, 7, imgMat, mode)).build();
            }
        } else {
            if (gray7 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.W)
                        .addAll(prosesChaincode(temprow, tempcol, 7, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<AbsDirection> objectarahNW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray8 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray8 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.NW)
                        .addAll(prosesChaincode(temprow, tempcol, 8, imgMat, mode)).build();
            }
        } else {
            if (gray8 < toleransi) {
                return ImmutableList.<AbsDirection>builder().add(AbsDirection.NW)
                        .addAll(prosesChaincode(temprow, tempcol, 8, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private void areaObject(int row, int col) {
        if (minHor > col) {
            minHor = col;
        } else if (maxHor < col) {
            maxHor = col;
        }

        if (minVer > row) {
            minVer = row;
        } else if (maxVer < row) {
            maxVer = row;
        }
    }

    private String calculateKodeBelok(String chainCode) {
        String kodeBelok = "";
        String temp = String.valueOf(chainCode.charAt(0));

        char[] tempChar = new char[2];
        tempChar[0] = chainCode.charAt(0);
        tempChar[1] = chainCode.charAt(0);
        kodeBelok += chainCode.charAt(0);
        boolean rep = false;

        for (int i = 0; i < chainCode.length() - 1; i++) {
            final char ff = chainCode.charAt(i);
            if (tempChar[0] != chainCode.charAt(i)) {
                if (tempChar[1] != chainCode.charAt(i)) {
                    tempChar[0] = tempChar[1];
                    tempChar[1] = chainCode.charAt(i);
                    kodeBelok += chainCode.charAt(i);
                } else {
                    tempChar[0] = tempChar[1];
                    tempChar[1] = chainCode.charAt(i);
                }

            } else {
                if (tempChar[1] == chainCode.charAt(i + 1) && tempChar[0] != tempChar[1]) {
                    i++;
                } else if (tempChar[1] == chainCode.charAt(i + 1)) {

                } else {
                    tempChar[0] = tempChar[1];
                    tempChar[1] = chainCode.charAt(i + 1);
                    kodeBelok += chainCode.charAt(i + 1);
                    i++;
                }
            }
        }
        return kodeBelok;
    }

    private  boolean cekRowAndCol(int row,int col)
    {
        if(row <0 || col <0 || row >= imgMat.rows() || col >= imgMat.cols())
            return false;
        return true;
    }

}
