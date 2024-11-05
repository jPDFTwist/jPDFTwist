package jpdftwist.utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Vasilis Naskos
 */
public class SupportedFileTypes {

    public static boolean isSupported(String fileType) {
        HashSet<String> supportedInputExtensions = new HashSet<>();

        supportedInputExtensions.addAll(Arrays.asList("jpg", "jpeg", "jp2", "j2k", "jpf", "jpx", "jpm", "mj2", "png",
            "bmp", "tiff", "tif", "iff", "dds", "gif", "svg", "hdr", "psd", "sgi", "tga", "pdf", "pam", "pbm", "pgm", 
            "pnm", "ppm", "pcx", "pct", "pict", "webp", "wmf"));

        return supportedInputExtensions.contains(fileType);
    }

    public static boolean isPDF(String filepath) {
        return SupportedFileTypes.getFileExtension(filepath).equals("pdf");
    }

    public static boolean isSupported(File file) {
        String extension = getFileExtension(file);

        return isSupported(extension);
    }

    public static String getFileExtension(String file) {

        int indexOfExtension = file.lastIndexOf('.') + 1;

        String extension = file.substring(indexOfExtension);

        return extension.toLowerCase();
    }

    public static String getFileExtension(File file) {
        String filePath = file.getAbsolutePath();

        return getFileExtension(filePath);
    }
}
