
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
    private final File mApkFile;
    private final File mOutDir;
    private final String mDexFile;
    private final boolean mBakDeb;
    private final int mApi;

    public static void decode(File apkFile, File outDir, String dexName, boolean bakdeb, int api) {
        new SmaliDecoder(apkFile, outDir, dexName, bakdeb, api).decode();
    }

    private SmaliDecoder(File apkFile, File outDir, String dexName, boolean bakdeb, int api) {
        mApkFile = apkFile;
        mOutDir  = outDir;
        mDexFile = dexName;
        mBakDeb  = bakdeb;
        mApi     = api;
    }

    private void decode() {
        System.out.println("Decode");

        try {
            final BaksmaliOptions options = new BaksmaliOptions();

            // options
            options.deodex = false;
            options.implicitReferences = false;
            options.parameterRegisters = true;
            options.localsDirective = true;
            options.sequentialLabels = true;
            options.debugInfo = mBakDeb;
            options.codeOffsets = false;
            options.accessorComments = false;
            options.registerInfo = 0;
            options.inlineResolver = null;

            // set jobs automatically
            int jobs = Runtime.getRuntime().availableProcessors();
            if (jobs > 6) {
                jobs = 6;
            }

            // create the dex
            DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(mApkFile, mDexFile, true, Opcodes.forApi(mApi));

            if (dexFile.isOdexFile()) {
                throw new Exception("Warning: You are disassembling an odex file without deodexing it.");
            }

            if (dexFile instanceof DexBackedOdexFile) {
                options.inlineResolver =
                        InlineMethodResolver.createInlineMethodResolver(((DexBackedOdexFile)dexFile).getOdexVersion());
            }

            Baksmali.disassembleDexFile(dexFile, mOutDir, jobs, options);
        } catch (Exception ex) {
            // throw new IOException(ex);
            System.out.println(ex.getMessage());
        }
    }
}
