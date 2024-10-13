package jpdftwist.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempFileManager {

    private boolean useTempFiles;
    private File tempFile1 = null;
    private File tempFile2 = null;

    public TempFileManager(final boolean useTempFiles) {
        this.useTempFiles = useTempFiles;

        if (useTempFiles) {
            tryToCreateTempOutputFiles();
        }
    }

    private void tryToCreateTempOutputFiles() {
        try {
            createTempOutputFiles();
        } catch (IOException ex) {
            Logger.getLogger(TempFileManager.class.getName()).log(Level.SEVERE, "Ex010", ex);
        }
    }

    /**
     * Create 2 temporary files not sure why it must be 2
     */
    private void createTempOutputFiles() throws IOException {
        tempFile1 = File.createTempFile("~jpdftwist", ".tmp").getAbsoluteFile();
        tempFile2 = File.createTempFile("~jpdftwist", ".tmp").getAbsoluteFile();
        tempFile1.deleteOnExit();
        tempFile2.deleteOnExit();
    }

    public OutputStream createTempOutputStream() throws IOException {
        if (!useTempFiles) {
            return new ByteArrayOutputStream();
        }

        File swap = tempFile1;
        tempFile1 = tempFile2;
        tempFile2 = swap;
        if (!tempFile1.delete()) {
            Logger.getLogger(TempFileManager.class.getName()).log(Level.SEVERE, "Ex125");
            throw new IOException("Cannot delete " + tempFile1);
        }
        return Files.newOutputStream(tempFile1.toPath());
    }

    public File getTempFile() {
        return tempFile1;
    }

    public void cleanup() {
        if (tempFile1 != null && !tempFile1.delete()) {
            Logger.getLogger(TempFileManager.class.getName()).log(Level.WARNING, "Cannot delete " + tempFile1, "Ex099");
        }
        tempFile1 = null;

        if (tempFile2 != null && !tempFile2.delete()) {
            Logger.getLogger(TempFileManager.class.getName()).log(Level.WARNING, "Cannot delete " + tempFile2, "Ex100");
        }
        tempFile2 = null;
    }
}
