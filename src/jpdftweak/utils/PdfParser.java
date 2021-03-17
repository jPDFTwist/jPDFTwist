package jpdftweak.utils;

import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpdftweak.tabs.input.password.PasswordObject;
import jpdftweak.tabs.input.password.PdfUnlockDialog;

/**
 *
 * @author Vasilis Naskos
 */
public class PdfParser {
    
    public static PdfReader open(String filepath) {
        try {
            return open(filepath, false);
        } catch (IOException ex) {
            Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static PdfReader open(String filepath, boolean optimize)
            throws IOException {
        return open(filepath, optimize, false, false);
    }
    
    public static PdfReader open(String filepath, boolean optimize,
            boolean autoRestrictionsOverwrite,
            boolean autoRestrictionsNew) throws IOException {
        try {
            return open(filepath, "", optimize);
        } catch(Exception ex) {
            return openWithPass(filepath, optimize, autoRestrictionsOverwrite, autoRestrictionsNew);
        }
    }
    
    public static PdfReader openWithPass(String filepath, boolean optimize,
            boolean autoRestrictionsOverwrite,
            boolean autoRestrictionsNew) throws IOException {
        PasswordObject unlockInfo = PdfUnlockDialog.askForPassword(null, filepath, autoRestrictionsOverwrite, autoRestrictionsNew);

        if (unlockInfo.getUnlockedFilePath() != null) {
            filepath = unlockInfo.getUnlockedFilePath();
            return open(filepath, "", optimize);
        }

        String password = unlockInfo.getPasswordAsString();
        
        return open(filepath, password, true);
    }
    
    private static PdfReader open(String filepath, String ownerPassword, boolean optimize)
            throws IOException {
        try {
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(filepath, false, true);
            PdfReader reader = open(new PdfReader(raf, ownerPassword.getBytes("ISO-8859-1")), optimize);
            raf.close();
            return reader;
        } catch (ExceptionConverter ex) {
            while (ex.getException() instanceof ExceptionConverter) {
                ex = (ExceptionConverter) ex.getCause();
            }
            if (ex.getException() instanceof InvalidPdfException) {
                try {
                    // The PdfReader constructor that takes a file name does more thorough checking and reparing, 
                    // but it will need more RAM. Therefore, if the first one fails, try that one now.
//                    open(new PdfReader(filepath, ownerPassword.getBytes("ISO-8859-1")));
                    PdfReader reader = open(new PdfReader(filepath, new byte[]{}), optimize);
                   System.out.println("in invalid pdf");
                    return reader;
                } catch (ExceptionConverter ex2) {
                    throw ex;
                }
            } else {
                throw ex;
            }
        } catch (NullPointerException ex) {
            throw new IOException(ex);
        }
    }
    
    private static PdfReader open(PdfReader reader, boolean optimize)
            throws BadPasswordException {
        PdfReader rdr = reader;
        
        if (!rdr.isOpenedWithFullPermissions()) {
            throw new BadPasswordException("PdfReader not opened with owner password");
        }
        rdr.consolidateNamedDestinations();
        if (optimize) {
            rdr.removeUnusedObjects();
            rdr.shuffleSubsetNames();
        }
        
        return rdr;
    }
    
}
