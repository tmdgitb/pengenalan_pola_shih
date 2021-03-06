# Pengenalan pola

Mengambil byte data dari image.

## Cara Menjalankan Aplikasi yang Sudah Dibuild

1. Buka Command Prompt di folder aplikasi
2. Jalankan perintah:

        pengenalanpola_shih

## DaemonApp

Gunakan browser untuk membuka http://localhost:8080/

## OBSOLETE: OpenCV

**OBSOLETE: No longer required because we use bytedeco's javacpp-presets opencv!**

### Windows 64-bit

OpenCV for Windows x64 DLL is needed, dan sudah dimasukkan ke git juga biar gampang.

Copy `opencv\win_x64\opencv_java*.dll` DLL tersebut ke `C:\ProgramData\Oracle\Java\javapath`

_Hendy's internal note:_ The `org.opencv:opencv` artifact is published in `soluvas-public-thirdparty`.
You can re-publish (if you update the OpenCV version) to `soluvas-thirdparty` using:

```
mvn deploy:deploy-file -DrepositoryId=soluvas-public-thirdparty -Durl=http://nexus.bippo.co.id/nexus/content/repositories/soluvas-public-thirdparty/ -Dfile=opencv/opencv-2411.jar -Dpackaging=jar -DgroupId=org.opencv -DartifactId=opencv -Dversion=2.4.11
```

### Ubuntu 14.04 / Linux Mint 17

1. Install `libopencv2.4-jni` (works on Power too):

        sudo aptitude install libopencv2.4-java libopencv2.4-jni

2. Symlink `libopencv_java248.so`.
    For `x64`, while you can put it in `/usr/java/packages/lib/amd64` it's still easier and portable to just use `/usr/lib`.
    For `ppc64el`, `opencv_java248` will be looked from
    `/opt/ibm/java-ppc64le-80/jre/lib/ppc64le/compressedrefs:/opt/ibm/java-ppc64le-80/jre/lib/ppc64le:/usr/lib`

        sudo ln -sv /usr/lib/jni/libopencv_java248.so /usr/lib

## IntelliJ Run/Console Encoding Troubleshooting

If you get weird characters instead of proper Unicode chaincode "↓↘↓↓↘↓↘↓↘↓↓↘↓↓",
set Settings → File Encoding → Project Encoding → IDE Encoding, select UTF-8.

## ChainCodeApp

Before Freeman (FCCE):

    Processing image file 'AA.jpg' ...
    Image mat: rows=96 cols=149 depth=0 type=16
    ukuran gambar 96149
    Chaincode object #0 at (34, 21): 33333333335455454545545545454554554545545455455454545545545477777777771811811811887777777777777777777766555656556577777777711212112112121121211211211212112112121121211211212
    Chaincode subobject : 54554554545545545577777777777711121211211212112
    Chaincode object #1 at (103, 21): 33333333335454554545545545454554554545545455454554554545545477777777771181811811887777777777777777777766556556556577777777711212112112121121211211212112112121121121211211212
    Chaincode subobject : 54554554545545545577777777777711121211211211212
    size chaincode 2
    Chaincode char #1 = 33333333335455454545545545454554554545545455455454545545545477777777771811811811887777777777777777777766555656556577777777711212112112121121211211211212112112121121211211212

After Freeman (FCCE):

    Processing image file 'AA.jpg' ...
    Image mat: rows=96 cols=149 depth=0 type=16
    ukuran gambar 96149
    Chaincode object #0 at (34, 21): →→→→→→→→→→↓↘↓↓↘↓↘↓↘↓↓↘↓↓↘↓↘↓↘↓↓↘↓↓↘↓↘↓↓↘↓↘↓↓↘↓↓↘↓↘↓↘↓↓↘↓↓↘↓↘←←←←←←←←←←↑↖↑↑↖↑↑↖↑↑↖↖←←←←←←←←←←←←←←←←←←←←↙↙↓↓↓↙↓↙↓↓↙↓←←←←←←←←←↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↗↑↑↗↑↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗
    Chaincode subobject : ↓↘↓↓↘↓↓↘↓↘↓↓↘↓↓↘↓↓←←←←←←←←←←←←↑↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗
    Chaincode object #1 at (103, 21): →→→→→→→→→→↓↘↓↘↓↓↘↓↘↓↓↘↓↓↘↓↘↓↘↓↓↘↓↓↘↓↘↓↓↘↓↘↓↓↘↓↘↓↓↘↓↓↘↓↘↓↓↘↓↘←←←←←←←←←←↑↑↖↑↖↑↑↖↑↑↖↖←←←←←←←←←←←←←←←←←←←←↙↙↓↓↙↓↓↙↓↓↙↓←←←←←←←←←↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗↑↑↗↑↑↗↑↗
    Chaincode subobject : ↓↘↓↓↘↓↓↘↓↘↓↓↘↓↓↘↓↓←←←←←←←←←←←←↑↑↑↗↑↗↑↑↗↑↑↗↑↑↗↑↗
    size chaincode 2
    Chaincode char #1 = 00000000006766767676676676767667667676676766766767676676676744444444442322322322334444444444444444444455666565665644444444422121221221212212122122122121221221212212122122121
