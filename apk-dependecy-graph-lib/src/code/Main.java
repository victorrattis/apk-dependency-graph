package code;

import code.decode.ApkSmaliDecoderController;
import code.io.ArgumentReader;
import code.io.Arguments;
import code.io.Writer;
import code.util.FileUtils;
import code.analyze.SmaliAnalyzer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Arguments arguments = new ArgumentReader(args).read();
        if (arguments == null) {
            return;
        }

        // Delete the output directory for a better decoding result.
        if (FileUtils.deleteDir(arguments.getProjectPath())) {
            log("The output directory was deleted successfully!");
        }

        // Decode the APK file for smali code in the output directory.
        ApkSmaliDecoderController.decode(
            arguments.getApkFilePath(), arguments.getProjectPath());

        File resultFile = new File(arguments.getResultPath());
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments);
        if (analyzer.run()) {
            new Writer(resultFile).write(analyzer.getDependencies());
            log("Success! Now open index.html in your browser.");
        }
    }

    private static void log(final String text) {
        System.out.println(text);
    }
}
