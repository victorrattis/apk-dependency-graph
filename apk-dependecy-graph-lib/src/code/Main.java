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
        // First, read all command line arguments that are important
        // for the dependency graph analysis process, according to
        // a specified pattern for that.
        Arguments arguments = new ArgumentReader(args).read();
        if (arguments == null) {
            return;
        }

        // Delete the output directory for a better decoding result.
        if (FileUtils.deleteDir(arguments.getProjectPath())) {
            System.out.println("The output directory was deleted!");
        }

        // Decode the APK file for smali code in the output directory.
        ApkSmaliDecoderController.decode(
            arguments.getApkFilePath(), arguments.getProjectPath());

        // After finishing the decoding process, it is possible to
        // analyze the smali codes to set the class dependency graph.
        File resultFile = new File(arguments.getResultPath());
        SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments);
        if (analyzer.run()) {
            // Save the made dependency to be saved in the resulting
            // path, which will be used on the UI app to make the
            // dependency graph.
            new Writer(resultFile).write(analyzer.getDependencies());
            System.out.println("Done!");
        }
    }
}
