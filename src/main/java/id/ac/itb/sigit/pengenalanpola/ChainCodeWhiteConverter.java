package id.ac.itb.sigit.pengenalanpola;

import com.google.common.collect.ImmutableList;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ChainCode> chainCodes;

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
        chainCodes = new ArrayList<>();
        log.info("ukuran gambar {}{}", imgMat.size().height(), imgMat.size().width());
    }

    public List<ChainCode> getChainCode() {
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
                    List<String> chainCodeStrs = prosesChaincode(y, x, 3, imgMat, 1);
                    ChainCode chainCode = new ChainCode();

                    String kodeBelok = getKodeBelok(chainCodeStrs.stream().collect(Collectors.joining()));
                    chainCode.setChainCodeStr(chainCodeStrs.stream().collect(Collectors.joining()));
                    chainCode.setKodeBelok(kodeBelok);
                    chainCode.setX(x);
                    chainCode.setY(y);

                    if (chainCodeStrs.size() > 20) {
                        log.info("Chaincode object #{} at ({}, {}): {}", objectIdx, x, y, chainCodeStrs);
                        objectIdx++;
                        List<ChainCode> subChainCodes = subObject(imgMat);
                        if (subChainCodes.size() > 0) {
                            chainCode.getSubChainCode().addAll(subChainCodes);
                        }
                        chainCodes.add(chainCode);
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

        return chainCodes;
    }

    private List<ChainCode> subObject(opencv_core.Mat imgMat) {
        List<ChainCode> subChainCodes = new ArrayList<>();

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
                        List<String> chaincode2 = prosesChaincode(y, x, 3, imgMat, 0);

                        ChainCode subChainCode = new ChainCode();
                        subChainCode.setChainCodeStr(chaincode2.stream().collect(Collectors.joining()));
                        subChainCodes.add(subChainCode);

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

        return subChainCodes;
    }

    private List<String> prosesChaincode(int row, int col, int arah, opencv_core.Mat imgMat, int mode) {
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
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
                //
                //cek arah 8
                //
                List<String> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<String> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

            }
            //kondisi perjalanan arah 2
            else if (arah == 2) {
                //
                //cek arah 8 (samping kiri)
                //
                List<String> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<String> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<String> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

            } else if (arah == 3) {
                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<String> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<String> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<String> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }
            } else if (arah == 4) {
                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }


                //
                //cek arah 3
                //
                List<String> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<String> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<String> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<String> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
            } else if (arah == 5) {
                //
                //cek arah 3
                //
                List<String> arahE = objectarahE(row, col, imgMat, imgIdx, mode);
                if (!arahE.isEmpty()) {
                    return arahE;
                }

                //
                //cek arah 4
                //
                List<String> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<String> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<String> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }
            } else if (arah == 6) {
                //
                //cek arah 4
                //
                List<String> arahSE = objectarahSE(row, col, imgMat, imgIdx, mode);
                if (!arahSE.isEmpty()) {
                    return arahSE;
                }

                //
                //cek arah 5
                //
                List<String> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<String> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<String> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }
            } else if (arah == 7) {
                //
                //cek arah 5
                //
                List<String> arahS = objectarahS(row, col, imgMat, imgIdx, mode);
                if (!arahS.isEmpty()) {
                    return arahS;
                }

                //
                //cek arah 6
                //
                List<String> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<String> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
                if (!arahNE.isEmpty()) {
                    return arahNE;
                }
            } else //if(arah==8)
            {
                //
                //cek arah 6
                //
                List<String> arahSW = objectarahSW(row, col, imgMat, imgIdx, mode);
                if (!arahSW.isEmpty()) {
                    return arahSW;
                }

                //
                //cek arah 7
                //
                List<String> arahW = objectarahW(row, col, imgMat, imgIdx, mode);
                if (!arahW.isEmpty()) {
                    return arahW;
                }

                //
                //cek arah 8
                //
                List<String> arahNW = objectarahNW(row, col, imgMat, imgIdx, mode);
                if (!arahNW.isEmpty()) {
                    return arahNW;
                }

                //
                //cek arah 1 (depan)
                //
                List<String> arahN = objectarahN(row, col, imgMat, imgIdx, mode);
                if (!arahN.isEmpty()) {
                    return arahN;
                }

                //
                //cek arah 2
                //
                List<String> arahNE = objectarahNE(row, col, imgMat, imgIdx, mode);
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
    private List<String> objectarahN(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col;

        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray1 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray1 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("1").addAll(prosesChaincode(temprow, tempcol, 1, imgMat, mode)).build();
            }
        } else {
            if (gray1 < toleransi) {
                return ImmutableList.<String>builder().add("1").addAll(prosesChaincode(temprow, tempcol, 1, imgMat, mode)).build();
            }
        }
        return ImmutableList.of();
    }

    private List<String> objectarahNE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray2 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray2 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("2").addAll(prosesChaincode(temprow, tempcol, 2, imgMat, mode)).build();
            }
        } else {
            if (gray2 < toleransi) {
                return ImmutableList.<String>builder().add("2").addAll(prosesChaincode(temprow, tempcol, 2, imgMat, mode)).build();
            }
        }
        return ImmutableList.of();
    }

    private List<String> objectarahE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray3 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray3 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("3").addAll(prosesChaincode(temprow, tempcol, 3, imgMat, mode)).build();
            }
        } else {
            if (gray3 < toleransi) {
                return ImmutableList.<String>builder().add("3").addAll(prosesChaincode(temprow, tempcol, 3, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<String> objectarahSE(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col + 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray4 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray4 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("4").addAll(prosesChaincode(temprow, tempcol, 4, imgMat, mode)).build();
            }
        } else {
            if (gray4 < toleransi) {
                return ImmutableList.<String>builder().add("4").addAll(prosesChaincode(temprow, tempcol, 4, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<String> objectarahS(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();
        final int gray5 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray5 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("5").addAll(prosesChaincode(temprow, tempcol, 5, imgMat, mode)).build();
            }
        } else {
            if (gray5 < toleransi) {
                return ImmutableList.<String>builder().add("5").addAll(prosesChaincode(temprow, tempcol, 5, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<String> objectarahSW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row + 1;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray6 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray6 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("6").addAll(prosesChaincode(temprow, tempcol, 6, imgMat, mode)).build();
            }
        } else {
            if (gray6 < toleransi) {
                return ImmutableList.<String>builder().add("6").addAll(prosesChaincode(temprow, tempcol, 6, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<String> objectarahW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray7 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray7 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("7").addAll(prosesChaincode(temprow, tempcol, 7, imgMat, mode)).build();
            }
        } else {
            if (gray7 < toleransi) {
                return ImmutableList.<String>builder().add("7").addAll(prosesChaincode(temprow, tempcol, 7, imgMat, mode)).build();
            }
        }

        return ImmutableList.of();
    }

    private List<String> objectarahNW(int row, int col, opencv_core.Mat imgMat, ByteIndexer indexer, int mode) {
        int temprow, tempcol;
        temprow = row - 1;
        tempcol = col - 1;
        if(!cekRowAndCol(temprow,tempcol))
            return ImmutableList.of();

        final int gray8 = Byte.toUnsignedInt(indexer.get(temprow, tempcol));
        if (mode == 1) {
            if (gray8 > toleransiWhite) {
                areaObject(row, col);
                return ImmutableList.<String>builder().add("8").addAll(prosesChaincode(temprow, tempcol, 8, imgMat, mode)).build();
            }
        } else {
            if (gray8 < toleransi) {

                return ImmutableList.<String>builder().add("8").addAll(prosesChaincode(temprow, tempcol, 8, imgMat, mode)).build();
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

    private String getKodeBelok(String chainCode) {
        String kodeBelok = "";
        String temp = String.valueOf(chainCode.charAt(0));

        char[] tempChar = new char[2];
        tempChar[0] = chainCode.charAt(0);
        tempChar[1] = chainCode.charAt(0);
        kodeBelok += chainCode.charAt(0);
        boolean rep = false;

        for (int i = 0; i < chainCode.length() - 1; i++) {
            if (i == 105) {
                char f = chainCode.charAt(i);
            }
            char ff = chainCode.charAt(i);
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
