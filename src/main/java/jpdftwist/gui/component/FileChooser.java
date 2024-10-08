package jpdftwist.gui.component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * @author Vasilis Naskos
 */
public class FileChooser {

    private final JFileChooser fileChooser;
    private final Container parent;
    private static String lastDirectory;

    public FileChooser() {
        this(null);
    }

    public FileChooser(Container parent) {
        this.parent = parent;
        fileChooser = new JFileChooser(lastDirectory);
        setFileChooserProperties();
    }

    public void setLastDirectory(String lastDirectory) {
        FileChooser.lastDirectory = lastDirectory;
    }

    private void setFileChooserProperties() {
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter[] filters = {new FileNameExtensionFilter("JPEG Image(*.jpg, *.jpeg)", "JPG", "JPEG"),
            new FileNameExtensionFilter("JPEG2000 Image(*.jp2, *.j2k, *.jpf, *.jpx, *.jpm, *.mj2)", "JP2", "J2K", "JPF", "JPX", "JPM", "MJ2"),
            new FileNameExtensionFilter("PNG Images(*.png)", "PNG"),
            new FileNameExtensionFilter("BMP Images(*.bmp)", "BMP"),
            new FileNameExtensionFilter("TIFF Images(*.tiff, *.tif)", "TIFF", "TIF"),
            new FileNameExtensionFilter("GIF Images(*.gif)", "GIF"),
            new FileNameExtensionFilter("Photoshop Files(*.psd)", "PSD"),
            new FileNameExtensionFilter("TGA Images(*.tga)", "TGA"),
            new FileNameExtensionFilter("PNM Images(*.pnm)", "PNM"),
            new FileNameExtensionFilter("PPM Images(*.ppm)", "PPM"),
            new FileNameExtensionFilter("PGM Images(*.pgm)", "PGM"),
            new FileNameExtensionFilter("PBM Images(*.pbm)", "PBM"),
            new FileNameExtensionFilter("WBMP Images(*.wbmp)", "WBMP"),
            new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
            new FileNameExtensionFilter("All supported file types", "JPG", "JPEG", "JP2", "J2K", "JPF", "JPX",
                "JPM", "MJ2", "PNG", "GIF", "BMP", "TIFF", "TIF", "TGA", "PNM", "PPM", "PGM", "PBM", "WBMP",
                "PSD", "PDF")};

        for (FileNameExtensionFilter filter : filters) {
            fileChooser.setFileFilter(filter);
        }
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public File[] getSelectedFiles() {
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return null;

        lastDirectory = fileChooser.getSelectedFile().getPath();
        return fileChooser.getSelectedFiles();
    }

    public File getSelectedFile() {
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        lastDirectory = fileChooser.getSelectedFile().getAbsolutePath();
        return fileChooser.getSelectedFile();
    }

}
