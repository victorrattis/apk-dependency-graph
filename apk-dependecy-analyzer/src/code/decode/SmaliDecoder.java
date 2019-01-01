package code.decode;

import code.util.ZipFileUtils;

import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;
import org.jf.dexlib2.analysis.InlineMethodResolver;

import java.io.File;
import java.io.IOException;
import java.util.List;

final public class SmaliDecoder {
    private static final int MAXIMUM_NUMBER_OF_PROCESSORS = 6;

    private static final String WARNING_DISASSEMBLING_ODEX_FILE =
        "Warning: You are disassembling an odex file without deodexing it.";

    private final String mApkFilePath;
    private final String mOutDirPath;
    private final int mApiVersion;

    SmaliDecoder(String apkFilePath, String outDirPath, int api) {
        mApkFilePath = apkFilePath;
        mOutDirPath  = outDirPath;
        mApiVersion  = api;
    }

    void decode() throws IOException {
        File apkFile = new File(mApkFilePath);
        if (!apkFile.exists()) {
            throw new IOException("Apk file not found!");
        }
        File outDir = new File(mOutDirPath);

        // Read all dex files in the APK file and so decode each one.
        log("Decoding " + mApkFilePath);
        for (String dexFileName : getDexFiles(mApkFilePath)) {
            log("Smali Decoding: " + dexFileName);
            decodeDexFile(apkFile, dexFileName, mApiVersion, outDir);
        }
    }

    private void decodeDexFile(
            File apkFile, String dexFileName, int apiVersion, File outDir)
            throws IOException {
        try {
            DexBackedDexFile dexFile =
                loadDexFile(apkFile, dexFileName, apiVersion);

            Baksmali.disassembleDexFile(
                dexFile,
                outDir,
                getNumerOfAvailableProcessors(),
                getSmaliOptions(dexFile));
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private int getNumerOfAvailableProcessors() {
        int jobs = Runtime.getRuntime().availableProcessors();
        return jobs > MAXIMUM_NUMBER_OF_PROCESSORS ?
                MAXIMUM_NUMBER_OF_PROCESSORS : jobs;
    }

    private BaksmaliOptions getSmaliOptions(final DexBackedDexFile dexFile) {
        final BaksmaliOptions options = new BaksmaliOptions();

        options.deodex = false;
        options.implicitReferences = false;
        options.parameterRegisters = true;
        options.localsDirective = true;
        options.sequentialLabels = true;
        options.debugInfo = false;
        options.codeOffsets = false;
        options.accessorComments = false;
        options.registerInfo = 0;

        if (dexFile instanceof DexBackedOdexFile) {
            options.inlineResolver =
                    InlineMethodResolver.createInlineMethodResolver(
                        ((DexBackedOdexFile)dexFile).getOdexVersion());
        } else {
            options.inlineResolver = null;
        }

        return options;
    }

    private DexBackedDexFile loadDexFile(
            File apkFile, String dexFilePath, int apiVersion)
            throws IOException {
        DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(
            apkFile, dexFilePath, true, Opcodes.forApi(apiVersion));

        if (dexFile == null || dexFile.isOdexFile()) {
            throw new IOException(WARNING_DISASSEMBLING_ODEX_FILE);
        }

        return dexFile;
    }

    private List<String> getDexFiles(String apkFilePath) throws IOException {
        return ZipFileUtils.filterByContainsName(apkFilePath, "classes");
    }

    private void log(final String text) {
        System.out.println(text);
    }
}
