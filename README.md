# Pengenalan pola

mengambil byte data dari image

## OpenCV

OpenCV for Windows x64 DLL is needed, dan sudah dimasukkan ke git juga biar gampang.

Copy `opencv\win_x64\opencv_java*.dll` DLL tersebut ke `C:\ProgramData\Oracle\Java\javapath`

_Hendy's internal note:_ The `org.opencv:opencv` artifact is published in `soluvas-public-thirdparty`.
You can re-publish (if you update the OpenCV version) to `soluvas-thirdparty` using:

```
mvn deploy:deploy-file -DrepositoryId=soluvas-public-thirdparty -Durl=http://nexus.bippo.co.id/nexus/content/repositories/soluvas-public-thirdparty/ -Dfile=opencv/opencv-2411.jar -Dpackaging=jar -DgroupId=org.opencv -DartifactId=opencv -Dversion=2.4.11
```
