
package code;

import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;
import org.jf.dexlib2.analysis.InlineMethodResolver;

import java.io.File;
import java.io.IOException;

public class SmaliDecoder {
    private static final int MAXIMUM_NUMBER_OF_PROCESSORS = 6;

    private final File mApkFile;
    private final File mOutDir;
    private final String mDexFile;
    private final int mApi;

    public static void decode(
            File apkFile, File outDir, String dexName, int api) {
        new SmaliDecoder(apkFile, outDir, dexName, api).decode();
    }

    private SmaliDecoder(
            File apkFile, File outDir, String dexName, int api) {
        mApkFile = apkFile;
        mOutDir  = outDir;
        mDexFile = dexName;
        mApi     = api;
    }

    private void decode() {
        System.out.println("Decode");

        try {
            DexBackedDexFile dexFile = loadDexFile();
            Baksmali.disassembleDexFile(
                dexFile, 
                mOutDir, 
                getNumerOfAvailableProcessors(), 
                getSmaliOptions(dexFile));
        } catch (Exception ex) {
            // throw new IOException(ex);
            System.out.println(ex.getMessage());
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
        options.inlineResolver = null;

        if (dexFile instanceof DexBackedOdexFile) {
            options.inlineResolver =
                    InlineMethodResolver.createInlineMethodResolver(
                        ((DexBackedOdexFile)dexFile).getOdexVersion());
        }

        return options;
    }

    private DexBackedDexFile loadDexFile() throws IOException {
        DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(
            mApkFile, mDexFile, true, Opcodes.forApi(mApi));

        if (dexFile == null || dexFile.isOdexFile()) {
            throw new IOException(
                "Warning: You are disassembling an odex file without deodexing it.");
        }

        return dexFile;
    }
}
