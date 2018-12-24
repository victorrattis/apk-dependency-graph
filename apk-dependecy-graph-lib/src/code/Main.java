package code;


import java.io.File;

import code.io.ArgumentReader;
import code.io.Arguments;
import code.io.Writer;
import java.io.IOException;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.List;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		Arguments arguments = new ArgumentReader(args).read();
		if (arguments == null) {
			return;
		}

		final String ApkFilePath = arguments.getApkFilePath();
		final String outputFilePath = arguments.getProjectPath();

		// clear output dir;
		File outputFile = new File(outputFilePath);
		if (outputFile.exists()) {
			boolean result = deleteDirectory(outputFile);
			log("Was output dir deleted: " + result);
		}

		List<String> classeFiles = getClassesFiles(ApkFilePath);
		for (String fileName : classeFiles) {
			log("Smali Decoding: " + fileName);
			SmaliDecoder.decode(
					new File(ApkFilePath),
					new File(outputFilePath),
					fileName,
					28);
		}

		File resultFile = new File(arguments.getResultPath());
		SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments);
		if (analyzer.run()) {
			new Writer(resultFile).write(analyzer.getDependencies());
			log("Success! Now open index.html in your browser.");
		}
	}

	private static boolean deleteDirectory(final File dir) {
		File[] allContents = dir.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return dir.delete();
	}

	private static void log(final String text) {
		System.out.println(text);
	}

	public static List<String> getClassesFiles(final String apkFilePath) {
		List<String> files = new ArrayList<>();
		try {
			ZipFile zipFile = new ZipFile(apkFilePath);
			Enumeration zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				String fileName = ((ZipEntry) zipEntries.nextElement()).getName();
				if (fileName.contains("classes")) {
					files.add(fileName);
				}
			}
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}
		return files;
	}
}
