package code.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtils {
    public static List<String> filterByContainsName(
            final String zipFilePath, final String filterName)
            throws IOException {
        List<String> files = new ArrayList<>();
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            String fileName = ((ZipEntry) zipEntries.nextElement()).getName();
            if (fileName.contains(filterName)) {
                files.add(fileName);
            }
        }
        return files;
    }
}
