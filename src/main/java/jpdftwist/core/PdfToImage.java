package jpdftwist.core;

import com.jmupdf.Document;
import com.jmupdf.Page;
import com.jmupdf.PageRenderer;
import com.jmupdf.pdf.PdfDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * PdfToImage Class uses JmuPdf and ImageIO libraries to produce image files
 * from PDF pages. The image file types that can be produced are: "JPG", "PNG",
 * "GIF", "PAM", "PNM", "BMP", "TIFF" singlepage, "TIFF" multipage. JmuPdf
 * requires a shared library located in "java.class.path" to properly convert
 * PDF pages to the images. Java class path is set programmatically, in the path
 * that jpdftwist.jar is using a "trick", since there is no proper way to do it
 * in java. Those shared libraries doesn't support yet MacOS systems, so this
 * feature is available only for Windows and Linux systems.
 */
public class PdfToImage {

    private final int colorMode;
    private final int compressionType;
    private final int quality;
    private final int transparent;
    private final ImageType imageType;
    boolean burstImages;

    public enum ColorMode {
        RGB(PageRenderer.IMAGE_TYPE_RGB, "RGB"),
        GRAY(PageRenderer.IMAGE_TYPE_GRAY, "Grayscale"),
        BNW(PageRenderer.IMAGE_TYPE_BINARY, "Black and White"),
        BNWI(PageRenderer.IMAGE_TYPE_BINARY_DITHER, "Black and White Indexed");

        private final int code;
        private final String name;

        ColorMode(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String toString() {
            return name;
        }
    }

    public enum TiffCompression {
        NONE(PdfDocument.TIF_COMPRESSION_NONE),
        LZW(PdfDocument.TIF_COMPRESSION_LZW),
        JPEG(PdfDocument.TIF_COMPRESSION_JPEG),
        ZLIB(PdfDocument.TIF_COMPRESSION_ZLIB),
        PACKBITS(PdfDocument.TIF_COMPRESSION_PACKBITS),
        DEFLATE(PdfDocument.TIF_COMPRESSION_DEFLATE),
        RLE(PdfDocument.TIF_COMPRESSION_CCITT_RLE);

        private final int code;

        TiffCompression(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum ImageType {
        PDF, PSD, SVG, EMF, WMF,
        JPG, JP2, PNG, PAM, PNM, TGA, TIFF,
        BMP, GIF, PCX
    }

    public PdfToImage(boolean burstImages, ColorMode colorMode, ImageType imageType, TiffCompression compressionType, int quality, boolean transparent) {
        this.colorMode = colorMode == null ? -1 : colorMode.getCode();
        this.compressionType = compressionType == null ? -1 : compressionType.getCode();
        this.burstImages = burstImages;
        this.imageType = imageType;
        this.transparent = matchTransparency(transparent);
        this.quality = quality;
    }

    private int matchTransparency(boolean transparent) {
        if (transparent) {
            return PdfDocument.IMAGE_SAVE_ALPHA;
        } else {
            return PdfDocument.IMAGE_SAVE_NO_ALPHA;
        }
    }

    public boolean shouldExecute() {
        return this.burstImages;
    }

    /**
     * Gets a PDF document in byte array form and converts the first page to an
     * image file type, user had chosen before. JmuPdf does not support "GIF" and
     * "BMP" file types but has the ability to convert a PDF document to
     * BufferedImage. This Buffered image is converted to "GIF" or "BMP" using
     * ImageIO.
     *
     * @param docByte    PDF document in Byte array
     * @param outputPath The path that image file must be created
     * @throws IOException
     */
    public void convertToImage(byte[] docByte, String outputPath) throws IOException {
        PdfDocument document;
        try {
            document = new PdfDocument(docByte);
            Page page = document.getPage(1);
            String outputFilePath = outputPath.substring(0, outputPath.lastIndexOf(".") + 1)
                + imageType.toString().toLowerCase();
            if (imageType == ImageType.JPG
                || imageType == ImageType.JP2
                || imageType == ImageType.PNG
                || imageType == ImageType.PAM
                || imageType == ImageType.PNM
                || imageType == ImageType.TGA) {
                exportUsingJmuPdf(document, outputFilePath);

            } else if (imageType == ImageType.TIFF) {
                exportUsingImageIO(renderPage(page), outputFilePath);

            } else if (imageType == ImageType.BMP
                || imageType == ImageType.GIF
                || imageType == ImageType.PCX) {
                exportUsingImageIO(renderPage(page), outputFilePath);

            } else if (imageType == ImageType.PSD) {
                exportUsingImageIO(renderPage(page), outputFilePath);

            } else if (imageType == ImageType.SVG) {
                exportUsingImageIO(renderPage(page), outputFilePath);

            } else if (imageType == ImageType.EMF
                || imageType == ImageType.WMF) {
                exportUsingImageIO(renderPage(page), outputFilePath);
            }
            document.dispose();
        } catch (Exception e) {
            throw new IOException(e.toString(), e);
        }
    }

    /**
     * Gets a PDF PdfDocument in byte array form and converts it into a multipage
     * "TIFF" image file.
     *
     * @param docByte    PDF PdfDocument in Byte array
     * @param outputPath The path that "TIFF" file must be created
     * @throws IOException
     */
    public void convertToMultiTiff(byte[] docByte, String outputPath) throws IOException {
        PdfDocument document;
        try {
            document = new PdfDocument(docByte);
            String outputFilePath = outputPath.substring(0, outputPath.lastIndexOf(".") + 1) + "tiff";
            for (int i = 0; i < document.getPageCount(); i++) {
                document.saveAsTif((i + 1), outputFilePath, 1.0f, transparent, colorMode, compressionType,
                    Document.TIF_DATA_APPEND, quality);
            }
            document.dispose();
        } catch (Exception e) {
            throw new IOException(e.toString(), e);
        }
    }

    private void exportUsingJmuPdf(PdfDocument document, String outputFilePath) {
        switch (imageType) {
            case JPG:
                document.saveAsJPeg(1, outputFilePath, 1.0f, colorMode, quality);
                break;
            case PNG:
                document.saveAsPng(1, outputFilePath, 1.0f, transparent, colorMode);
                break;
            case PAM:
                document.saveAsPam(1, outputFilePath, 1.0f, transparent, colorMode);
                break;
            case PNM:
                document.saveAsPnm(1, outputFilePath, 1f, colorMode);
                break;
            case TIFF:
                document.saveAsTif(1, outputFilePath, 1.0f, transparent, colorMode, compressionType, Document.TIF_DATA_APPEND, quality);
                break;
            default:
                break;
        }
    }

    private void exportUsingImageIO(BufferedImage bufferedImage, String outputFilePath) throws IOException {
        ImageIO.write(bufferedImage, imageType.toString(), new File(outputFilePath));
    }

    private BufferedImage renderPage(Page page) {
        PageRenderer render = new PageRenderer(page, 1.0f, Page.PAGE_ROTATE_AUTO, colorMode);
        render.render(true);
        BufferedImage currentImage = render.getImage();
        render.dispose();
        return currentImage;
    }

    /**
     * A "trick" method to get not the path that the jpdftwist.jar is running but
     * the path that jpdftwist.jar exists.
     *
     * @return
     */
    public static String getJarFolder() {
        String s = System.getProperty("java.class.path");
        String separateSymbol = File.pathSeparator;
        if (s.contains(separateSymbol)) { // jpdftwist.jar was executed from a Java IDE
            String[] one = s.split(separateSymbol);
            for (String a : one) {
                if (a.contains("lib")) {
                    return a.substring(0, a.lastIndexOf("lib") + 3);
                }
            }
        } else {
            s = new File(s).getAbsoluteFile().getParentFile().getPath();
        }
        return s;

    }

    /**
     * Uses a "trick" to change the "java.class.path" programmatically. Sets the new
     * Class Path and then makes the previous path null to make Java update to the
     * new.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setJavaLibraryPath() throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.library.path", getJarFolder());
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    public static boolean is64bit() {
        String val = System.getProperty("sun.arch.data.model");
        return val.equals("64");
    }

    /**
     * According to System type and architecture search for appropriate shared
     * library.
     *
     * @return
     */
    public static String checkForLibraries() {
        try {
            Class.forName("com.jmupdf.util.Util");
        } catch (Throwable t) {
            return "nojmupdf";
        }
        String sharedLibraryName;
        if (is64bit()) {
            if (File.pathSeparatorChar == ';') { // Windows Systems
                sharedLibraryName = "jmupdf64.dll";
            } else {
                sharedLibraryName = "libjmupdf64.so";
            }
        } else {
            if (File.pathSeparatorChar == ';') {
                sharedLibraryName = "jmupdf32.dll";
            } else {
                sharedLibraryName = "libjmupdf32.so";
            }
        }
        File f = new File(getJarFolder(), sharedLibraryName);
        if (f.exists()) {
            return null;
        } else {
            return sharedLibraryName;
        }
    }

}
