package code.decode;

import java.io.IOException;

public class ApkSmaliDecoderController {
    private static final int DEFAULT_ANDROID_VERSION = 28;

    public static void decode(
            final String apkFilePath, final String outDirPath) {
        try {
            new SmaliDecoder(
                apkFilePath, outDirPath, DEFAULT_ANDROID_VERSION).decode();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}