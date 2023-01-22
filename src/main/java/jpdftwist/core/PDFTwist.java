package jpdftwist.core;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfTransition;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.interfaces.PdfEncryptionSettings;
import com.itextpdf.text.pdf.internal.PdfViewerPreferencesImp;
import jpdftwist.core.ShuffleRule.PageBase;
import jpdftwist.core.tabparams.RotateParameters;
import jpdftwist.core.tabparams.ScaleParameters;
import jpdftwist.core.watermark.WatermarkProcessor;
import jpdftwist.core.watermark.WatermarkStyle;
import jpdftwist.utils.SupportedFileTypes;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFTwist {

    private static final PdfName[] INFO_NAMES = {PdfName.TITLE, PdfName.SUBJECT, PdfName.KEYWORDS, PdfName.AUTHOR,
        PdfName.CREATOR, PdfName.PRODUCER, PdfName.CREATIONDATE, PdfName.MODDATE};

    public static final int[] permissionBits = {4, 8, 16, 32, 256, 512, 1024, 2048};
    public static final String[] permissionTexts = {"Printing", "ModifyContents", "Copy", "ModifyAnnotations",
        "FillIn", "ScreenReaders", "Assembly", "HQPrinting"};

    public static final String[] TRANSITION_NAMES = new String[]{"None", "Out Vertical Split", "Out Horizontal Split",
        "In Vertical Split", "In Horizontal Split", "Vertical Blinds", "Vertical Blinds", "Inward Box",
        "Outward Box", "Left-Right Wipe", "Right-Left Wipe", "Bottom-Top Wipe", "Top-Bottom Wipe", "Dissolve",
        "Left-Right Glitter", "Top-Bottom Glitter", "Diagonal Glitter",};

    public static String[] getKnownInfoNames() {
        String[] result = new String[INFO_NAMES.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = INFO_NAMES[i].toString().substring(1);
        }
        return result;
    }

    private static final String PDFTK_PAGE_MARKER = "pdftk_PageNum";

    private PdfReader currentReader;
    private int encryptionMode = -1, encryptionPermissions = -1;
    private byte[] userPassword = null;
    private byte[] ownerPassword = null;
    private int[][] transitionValues;
    private Map<PdfName, PdfObject> optionalViewerPreferences;
    private int simpleViewerPreferences;
    private List<File> attachments = null;
    private PrivateKey key = null;
    private Certificate[] certChain = null;
    private int certificationLevel = 0;
    private boolean sigVisible = false;
    private final String inputFilePath;
    private final String inputFileName;
    private final String inputFileFullName;
    private boolean preserveHyperlinks;
    private File tempfile1 = null, tempfile2 = null;
    private PdfToImage pdfImages;
    private final boolean mergeByDir;
    private final boolean useTempFiles;
    private String rootFolder;
    private ArrayList<List<PDAnnotation>> pdAnnotations = new ArrayList<>();
    private ArrayList<PDDocument> pdDocuments;
    private final List<PageRange> pageRanges;
    private int maxLength;
    private int interleaveSize;
    private final OutputEventListener outputEventListener;
    private boolean isCanceled = false;

    public PDFTwist(List<PageRange> pageRanges, boolean useTempFiles, boolean mergeByDir, int interleaveSize, OutputEventListener outputEventListener) throws IOException {
        this.useTempFiles = useTempFiles;
        this.outputEventListener = outputEventListener;
        this.pageRanges = pageRanges;
        this.mergeByDir = mergeByDir;

        this.inputFilePath = pageRanges.get(0).getParentName();
        this.inputFileFullName = pageRanges.get(0).getFilename();

        if (useTempFiles) {
            tryToCreateTempOutputFiles();
        }

        OutputStream baos = null;

        try {
            baos = createTempOutputStream();
            Document document = new Document();
            PdfCopy copy = null;
            try {
                copy = new PdfCopy(document, baos);
            } catch (DocumentException ex) {
                Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            }
            document.open();
            if (interleaveSize == 0) {
                int pagesBefore = 0;

                for (PageRange pageRange : pageRanges) {
                    InputReader inputReader = new InputReader();
                    currentReader = inputReader.getPdfReader(pageRange, ownerPassword);

                    int[] pages = pageRange.getPages(pagesBefore);
                    for (int page : pages) {
                        if (page == -1) {
                            copy.addPage(currentReader.getPageSizeWithRotation(1), 0);
                        } else {
                            copy.addPage(copy.getImportedPage(currentReader, page));
                        }
                    }
                    copy.freeReader(currentReader);
                    currentReader.close();

                    pagesBefore += pages.length;
                }
            } else {
                this.interleaveSize = interleaveSize;
                interleave(copy);
            }

            pdDocuments = new ArrayList<>();
            document.close();
            currentReader = getTempPdfReader(baos);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                baos.close();
            } catch (IOException ex) {
                Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int pos = inputFileFullName.lastIndexOf('.');
        if (pos == -1) {
            inputFileName = inputFileFullName;
        } else {
            inputFileName = inputFileFullName.substring(0, pos);
        }

        keepFileParents();
    }

    public void cancel() {
        this.isCanceled = true;
    }

    private void tryToCreateTempOutputFiles() {
        try {
            createTempOutputFiles();
        } catch (IOException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create 2 temporary files not sure why it must be 2
     */
    private void createTempOutputFiles() throws IOException {
        tempfile1 = File.createTempFile("~jpdftwist", ".tmp").getAbsoluteFile();
        tempfile2 = File.createTempFile("~jpdftwist", ".tmp").getAbsoluteFile();
        tempfile1.deleteOnExit();
        tempfile2.deleteOnExit();
    }

    private OutputStream createTempOutputStream() throws IOException {
        if (useTempFiles) {
            File swap = tempfile1;
            tempfile1 = tempfile2;
            tempfile2 = swap;
            if (!tempfile1.delete()) {
                throw new IOException("Cannot delete " + tempfile1);
            }
            return Files.newOutputStream(tempfile1.toPath());
        } else {
            return new ByteArrayOutputStream();
        }
    }

    public PdfReader getTempPdfReader(OutputStream out) throws IOException {
        return new InputReader().getTempPdfReader(out, useTempFiles, tempfile1);
    }

    public static PdfReader getTempPdfReader(OutputStream out, boolean useTempFiles, File tempFile) throws IOException {
        return new InputReader().getTempPdfReader(out, useTempFiles, tempFile);
    }

    private void interleave(PdfCopy copy) throws IOException, BadPdfFormatException {
        int[][] pagesPerRange = new int[pageRanges.size()][];
        maxLength = 0;
        for (int i = 0; i < pagesPerRange.length; i++) {
            PageRange range = pageRanges.get(i);
            pagesPerRange[i] = range.getPages(0);
            if (pagesPerRange[i].length > maxLength) {
                maxLength = pagesPerRange[i].length;
            }
        }

        int blockCount = (maxLength + interleaveSize - 1) / interleaveSize;
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < pageRanges.size(); j++) {
                int[] pages = pagesPerRange[j];
                for (int k = 0; k < interleaveSize; k++) {
                    int pageIndex = i * interleaveSize + k;
                    int pageNum = pageIndex < pages.length ? pages[pageIndex] : -1;
                    if (pageNum == -1) {
                        copy.addPage(currentReader.getPageSizeWithRotation(1), 0);
                    } else {
                        copy.addPage(copy.getImportedPage(currentReader, pageNum));
                    }
                }
            }
        }
    }

    private void keepFileParents() {
        TreeSet<String> set = new TreeSet<>();

        pageRanges.stream()
            .map(PageRange::getParentName)
            .forEach(set::add);

        rootFolder = (new File(set.first())).getParent() + File.separator;
    }

    public void setPdfImages(PdfToImage pdfImages) {
        this.pdfImages = pdfImages;
    }

    public static void copyXMPMetadata(PdfReader reader, PdfWriter writer) throws IOException {
        PdfObject xmpObject = PdfReader.getPdfObject(reader.getCatalog().get(PdfName.METADATA));
        if (xmpObject != null && xmpObject.isStream()) {
            byte[] xmpMetadata = PdfReader.getStreamBytesRaw((PRStream) xmpObject);
            writer.setXmpMetadata(xmpMetadata);
        }
    }

    public void cleanup() {
        if (tempfile1 != null) {
            tempfile1.delete();
        }
        if (tempfile2 != null) {
            tempfile2.delete();
        }
    }

    /**
     * Some stuff that is unconditionally done by pdftk. Maybe it helps.
     */
    private void cargoCult() {
        currentReader.consolidateNamedDestinations();
        currentReader.removeUnusedObjects();
        currentReader.shuffleSubsetNames();
    }

    public void updateInfoDictionary(Map<String, String> newInfo) {
        PdfDictionary trailer = currentReader.getTrailer();
        if (trailer != null && trailer.isDictionary()) {
            PdfObject info = PdfReader.getPdfObject(trailer.get(PdfName.INFO));
            if (info != null && info.isDictionary()) {
                PdfDictionary infoDic = (PdfDictionary) info;
                for (Map.Entry<String, String> entry : newInfo.entrySet()) {
                    if (entry.getValue().length() == 0) {
                        infoDic.remove(new PdfName(entry.getKey()));
                    } else {
                        infoDic.put(new PdfName(entry.getKey()),
                            new PdfString(entry.getValue(), PdfObject.TEXT_UNICODE));
                    }
                }
            }
        }
        // remove XMP metadata
        currentReader.getCatalog().remove(PdfName.METADATA);
    }

    public static void copyInformation(PdfReader source, PdfReader destination) {
        PdfDictionary srcTrailer = source.getTrailer();
        PdfDictionary dstTrailer = destination.getTrailer();
        if (srcTrailer != null && srcTrailer.isDictionary() && dstTrailer != null && dstTrailer.isDictionary()) {
            PdfObject srcInfo = PdfReader.getPdfObject(srcTrailer.get(PdfName.INFO));
            PdfObject dstInfo = PdfReader.getPdfObject(dstTrailer.get(PdfName.INFO));
            if (srcInfo != null && srcInfo.isDictionary() && dstInfo != null && dstInfo.isDictionary()) {
                PdfDictionary srcInfoDic = (PdfDictionary) srcInfo;
                PdfDictionary dstInfoDic = (PdfDictionary) dstInfo;
                for (PdfName key : srcInfoDic.getKeys()) {
                    PdfObject value = srcInfoDic.get(key);
                    dstInfoDic.put(key, value);
                }
            }
        }
        source.close();
    }

    public void setEncryption(int mode, int permissions, byte[] ownerPassword, byte[] userPassword) throws IOException {
        this.encryptionMode = mode;
        this.encryptionPermissions = permissions;
        this.userPassword = userPassword;
        this.ownerPassword = ownerPassword;
        if (ownerPassword.length == 0) {
            throw new IOException("Owner password may not be empty");
        }
    }

    private void setEncryptionSettings(PdfEncryptionSettings w) throws DocumentException {
        if (encryptionMode != -1) {
            w.setEncryption(userPassword, ownerPassword, encryptionPermissions, encryptionMode);
        }
    }

    public void writeOutput(String outputFile, boolean multipageTiff, boolean burst, boolean uncompressed,
                            boolean sizeOptimize, boolean fullyCompressed)
        throws IOException, DocumentException {
        if (!outputFile.contains(File.separator)) {
            File temp = new File(outputFile);
            outputFile = temp.getAbsolutePath();
        }
        if (sizeOptimize) {
            Document document = new Document(currentReader.getPageSizeWithRotation(1));
            OutputStream baos = createTempOutputStream();
            PdfSmartCopy copy = new PdfSmartCopy(document, baos);
            document.open();
            PdfImportedPage page;
            outputEventListener.setPageCount(currentReader.getNumberOfPages());
            if (isCanceled) {
                return;
            }
            for (int i = 0; i < currentReader.getNumberOfPages(); i++) {
                outputEventListener.updatePagesProgress();
                if (isCanceled) {
                    return;
                }
                page = copy.getImportedPage(currentReader, i + 1);
                copy.addPage(page);
            }
            PRAcroForm form = currentReader.getAcroForm();
            if (form != null) {
                copy.copyAcroForm(currentReader);
            }
            copy.setOutlines(SimpleBookmark.getBookmark(currentReader));
            PdfViewerPreferencesImp.getViewerPreferences(currentReader.getCatalog())
                .addToCatalog(copy.getExtraCatalog());
            copyXMPMetadata(currentReader, copy);
            PdfPageLabelFormat[] formats = PdfPageLabels.getPageLabelFormats(currentReader);
            if (formats != null) {
                PdfPageLabels lbls = new PdfPageLabels();
                for (PdfPageLabelFormat format : formats) {
                    lbls.addPageLabel(format);
                }
                copy.setPageLabels(lbls);
            }
            document.close();
            copyInformation(currentReader, currentReader = getTempPdfReader(baos));
        }

        String outpath = inputFilePath;
        outpath = outpath.replace(rootFolder, "");
        if (outpath.contains(":") && System.getProperty("os.name").toLowerCase().contains("win")) {
            outpath = outpath.replace(":", "");
            outpath = File.separator + outpath;
        }

        outputFile = outputFile.replace("<T>", outpath);

        if (mergeByDir) {
            outpath = outpath.replace(File.separatorChar, '_');
            outputFile = outputFile.replace("<F>", outpath);
            outputFile = outputFile.replace("<FX>", outpath + ".pdf");
        } else {
            outputFile = outputFile.replace("<F>", inputFileName);
            outputFile = outputFile.replace("<FX>", inputFileFullName);
        }

        outputFile = outputFile.replace("<P>", inputFilePath);
        if (outputFile.contains("<#>")) {
            for (int i = 1; ; i++) {
                String f = outputFile.replace("<#>", "" + i);
                if (!new File(f).exists()) {
                    outputFile = f;
                    break;
                }
            }
        }
        cargoCult();
        try {
            if (uncompressed && pdfImages == null) {
                Document.compress = false;
            }
            int total = currentReader.getNumberOfPages();
            outputEventListener.setPageCount(total);
            if (multipageTiff) {
                if (outputFile.indexOf('*') != -1) {
                    throw new IOException("TIFF multipage filename should not contain *");
                }
                Document document = new Document(currentReader.getPageSizeWithRotation(1));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfCopy copy = new PdfCopy(document, baos);
                document.open();
                PdfImportedPage page;
                for (int pagenum = 1; pagenum <= currentReader.getNumberOfPages(); pagenum++) {
                    outputEventListener.updatePagesProgress();
                    if (isCanceled) {
                        return;
                    }
                    page = copy.getImportedPage(currentReader, pagenum);
                    copy.addPage(page);
                }
                PRAcroForm form = currentReader.getAcroForm();
                if (form != null) {
                    copy.copyAcroForm(currentReader);
                }
                document.close();
                pdfImages.convertToMultiTiff(baos.toByteArray(), outputFile);
            } else if (burst) {
                if (outputFile.indexOf('*') == -1) {
                    throw new IOException("Output filename does not contain *");
                }
                String prefix = outputFile.substring(0, outputFile.indexOf('*'));
                String suffix = outputFile.substring(outputFile.indexOf('*') + 1);
                String[] pageLabels = PdfPageLabels.getPageLabels(currentReader);
                PdfCopy copy;
                ByteArrayOutputStream baos = null;
                for (int pagenum = 1; pagenum <= currentReader.getNumberOfPages(); pagenum++) {
                    outputEventListener.updatePagesProgress();
                    if (isCanceled) {
                        return;
                    }
                    Document document = new Document(currentReader.getPageSizeWithRotation(1));
                    String pageNumber = "" + pagenum;
                    if (pageLabels != null && pagenum <= pageLabels.length) {
                        pageNumber = pageLabels[pagenum - 1];
                    }
                    File outFile = new File(prefix + pageNumber + suffix);
                    if (!outFile.getParentFile().isDirectory()) {
                        outFile.getParentFile().mkdirs();
                    }
                    if (pdfImages.shouldExecute()) {
                        baos = new ByteArrayOutputStream();
                        copy = new PdfCopy(document, baos);
                    } else {
                        copy = new PdfCopy(document, Files.newOutputStream(outFile.toPath()));
                        setEncryptionSettings(copy);
                        if (fullyCompressed) {
                            copy.setFullCompression();
                        }
                    }
                    document.open();
                    PdfImportedPage page;
                    page = copy.getImportedPage(currentReader, pagenum);
                    copy.addPage(page);
                    PRAcroForm form = currentReader.getAcroForm();
                    if (form != null) {
                        copy.copyAcroForm(currentReader);
                    }
                    document.close();
                    if (pdfImages.shouldExecute()) {
                        pdfImages.convertToImage(baos.toByteArray(), prefix + pageNumber + suffix);
                    }
                }
            } else {
                PdfStamper stamper;
                if (key != null) {
                    new File(outputFile).getParentFile().mkdirs();
                    stamper = PdfStamper.createSignature(currentReader, Files.newOutputStream(Paths.get(outputFile)), '\0', null,
                        true);
                    PdfSignatureAppearance sap = stamper.getSignatureAppearance();
                    sap.setCrypto(key, certChain, null, PdfSignatureAppearance.WINCER_SIGNED);
                    sap.setCertificationLevel(certificationLevel);
                    if (sigVisible) {
                        sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
                    }
                } else {
                    new File(outputFile).getParentFile().mkdirs();
                    stamper = new PdfStamper(currentReader, Files.newOutputStream(Paths.get(outputFile)));
                }
                setEncryptionSettings(stamper);
                if (fullyCompressed) {
                    stamper.setFullCompression();
                }
                for (int i = 1; i <= total; i++) {
                    outputEventListener.updatePagesProgress();
                    if (isCanceled) {
                        return;
                    }

                    currentReader.setPageContent(i, currentReader.getPageContent(i));
                }
                if (transitionValues != null) {
                    for (int i = 0; i < total; i++) {
                        PdfTransition t = transitionValues[i][0] == 0 ? null
                            : new PdfTransition(transitionValues[i][0], transitionValues[i][1]);
                        stamper.setTransition(t, i + 1);
                        stamper.setDuration(transitionValues[i][2], i + 1);
                    }
                }
                if (optionalViewerPreferences != null) {
                    stamper.setViewerPreferences(simpleViewerPreferences);
                    for (Map.Entry<PdfName, PdfObject> e : optionalViewerPreferences.entrySet()) {
                        stamper.addViewerPreference(e.getKey(), e.getValue());
                    }
                }
                if (attachments != null) {
                    for (File f : attachments) {
                        stamper.addFileAttachment(f.getName(), null, f.getAbsolutePath(), f.getName());
                    }
                }
                stamper.close();
            }
        } finally {
            Document.compress = true;
        }
        currentReader.close();
        currentReader = null;
        if (tempfile1 != null && !tempfile1.delete()) {
            throw new IOException("Cannot delete " + tempfile1);
        }
        if (tempfile2 != null && !tempfile2.delete()) {
            throw new IOException("Cannot delete " + tempfile2);
        }
        tempfile1 = tempfile2 = null;

        if (preserveHyperlinks) {
            InputStream in = Files.newInputStream(Paths.get(outputFile));
            PDFParser parser = new PDFParser((RandomAccessRead) in);
            parser.parse();
            PDDocument document = parser.getPDDocument();
            List<PDPage> allPages = (List<PDPage>) document.getDocumentCatalog().getPages();
            for (int i = 0; i < allPages.size(); i++) {
                PDPage page = allPages.get(i);
                page.setAnnotations(pdAnnotations.get(i));
            }
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                document.close();
                OutputStream outStream = Files.newOutputStream(Paths.get(outputFile));
                out.writeTo(outStream);
                out.close();
                outStream.close();
            } catch (IOException ex) {
                Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!pdDocuments.isEmpty()) {
                for (PDDocument pdDocument : pdDocuments) {
                    pdDocument.close();
                }
            }
        }
    }

    public void cropPages(PageBox cropTo) throws IOException, DocumentException {
        OutputStream baos = createTempOutputStream();
        CropProcessor cropProcessor = new CropProcessor();
        currentReader = cropProcessor.apply(outputEventListener, currentReader, baos, cropTo, preserveHyperlinks, pdAnnotations, useTempFiles, tempfile1);
    }

    public void rotatePages(RotateParameters param) {
        outputEventListener.setAction("Rotating");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            int rotation = currentReader.getPageRotation(i);
            Rectangle r = currentReader.getPageSizeWithRotation(i);
            int count;
            if (r.getWidth() > r.getHeight()) { // landscape
                if (param.isLandscape()) {
                    if (r.getWidth() >= param.getLandscapeLowerLimit()
                        && r.getWidth() <= param.getLandscapeUpperLimit()) {
                        count = param.getLandscapeCount();
                    } else {
                        count = 0;
                    }
                } else {
                    count = param.getLandscapeCount();
                }
            } else {
                if (param.isPortrait()) {
                    if (r.getHeight() >= param.getPortraitLowerLimit()
                        && r.getHeight() <= param.getPortraitUpperLimit()) {
                        count = param.getPortraitCount();
                    } else {
                        count = 0;
                    }
                } else {
                    count = param.getPortraitCount();
                }
            }
            rotation = (rotation + 90 * count) % 360;
            PdfDictionary dic = currentReader.getPageN(i);
            dic.put(PdfName.ROTATE, new PdfNumber(rotation));
        }
    }

    public void removeRotation() throws DocumentException, IOException {
        outputEventListener.setAction("Removing Rotation");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        boolean needed = false;
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            if (currentReader.getPageRotation(i) != 0) {
                needed = true;
            }
        }
        if (!needed) {
            return;
        }
        OutputStream baos = createTempOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        PdfContentByte cb = null;
        PdfImportedPage page;
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            Rectangle currentSize = currentReader.getPageSizeWithRotation(i);
            currentSize = new Rectangle(currentSize.getWidth(), currentSize.getHeight()); // strip rotation
            document.setPageSize(currentSize);
            if (cb == null) {
                document.open();
                cb = writer.getDirectContent();
            } else {
                document.newPage();
            }
            int rotation = currentReader.getPageRotation(i);
            page = writer.getImportedPage(currentReader, i);
            float a, b, c, d, e, f;
            if (rotation == 0) {
                a = 1;
                b = 0;
                c = 0;
                d = 1;
                e = 0;
                f = 0;
            } else if (rotation == 90) {
                a = 0;
                b = -1;
                c = 1;
                d = 0;
                e = 0;
                f = currentSize.getHeight();
            } else if (rotation == 180) {
                a = -1;
                b = 0;
                c = 0;
                d = -1;
                e = currentSize.getWidth();
                f = currentSize.getHeight();
            } else if (rotation == 270) {
                a = 0;
                b = 1;
                c = -1;
                d = 0;
                e = currentSize.getWidth();
                f = 0;
            } else {
                throw new IOException("Unparsable rotation value: " + rotation);
            }
            cb.addTemplate(page, a, b, c, d, e, f);
            if (preserveHyperlinks)
                repositionAnnotations(pdAnnotations, i, a, b, c, d, e, f);
        }
        copyXMPMetadata(currentReader, writer);
        document.close();
        copyInformation(currentReader, currentReader = getTempPdfReader(baos));
    }

    public void scalePages(ScaleParameters param) throws DocumentException, IOException {
        OutputStream baos = createTempOutputStream();
        ScaleProcessor scaleProcessor = new ScaleProcessor();
        currentReader = scaleProcessor.apply(outputEventListener, currentReader, baos, param, preserveHyperlinks, pdAnnotations, useTempFiles, tempfile1);
    }

    public void shufflePages(int passLength, int blockSize, ShuffleRule[] shuffleRules) throws DocumentException, IOException {
        outputEventListener.setAction("Shuffling");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        removeRotation();
        OutputStream baos = createTempOutputStream();
        Rectangle size = currentReader.getPageSize(1);
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            if (currentReader.getPageSize(i).getWidth() != size.getWidth()
                || currentReader.getPageSize(i).getHeight() != size.getHeight()) {
                throw new IOException(
                    "Pages must have equals sizes to be shuffled. Use the Scale option on the PageSize tab first.");
            }
            if (currentReader.getPageRotation(i) != 0) {
                throw new RuntimeException();
            }
        }
        Document document = new Document(size, 0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate page;
        int pl = Math.abs(passLength);
        int cnt = currentReader.getNumberOfPages();
        int passes = blockSize == 0 ? 1 : (cnt + blockSize - 1) / blockSize;
        int[] destinationPageNumbers;
        destinationPageNumbers = new int[cnt + 1];
        int ddPage = 0;
        for (int pass = 0; pass < passes; pass++) {
            int passcnt = pass == passes - 1 ? cnt - pass * blockSize : blockSize;
            int refcnt = ((passcnt + (pl - 1)) / pl) * pl;
            for (int i = 0; i < passcnt; i += pl) {
                int idx = i;
                int reverseIdx = refcnt - idx - pl;
                if (passLength < 0) {
                    idx = i / 2;
                    reverseIdx = refcnt - idx - pl;
                }
                idx += pass * blockSize;
                reverseIdx += pass * blockSize;
                for (ShuffleRule sr : shuffleRules) {
                    if (sr.isNewPageBefore()) {
                        ddPage++;
                    }
                    int pg = sr.getPageNumber();
                    if (sr.getPageBase() == PageBase.BEGINNING) {
                        pg += idx;
                    } else if (sr.getPageBase() == PageBase.END) {
                        pg += reverseIdx;
                    }
                    if (pg < 1) {
                        throw new IOException("Invalid page number. Check your n-up rules.");
                    }
                    if (pg <= cnt) {
                        destinationPageNumbers[pg] = ddPage;
                    }
                }
            }
        }

        ArrayList<List<PDAnnotation>> tmp = new ArrayList<>();

        for (int pass = 0; pass < passes; pass++) {
            int passcnt = pass == passes - 1 ? cnt - pass * blockSize : blockSize;
            int refcnt = ((passcnt + (pl - 1)) / pl) * pl;

            for (int i = 0; i < passcnt; i += pl) {
                int idx = i;
                int reverseIdx = refcnt - idx - pl;
                if (passLength < 0) {
                    idx = i / 2;
                    reverseIdx = refcnt - idx - pl;
                }
                idx += pass * blockSize;
                reverseIdx += pass * blockSize;
                for (ShuffleRule sr : shuffleRules) {
                    if (sr.isNewPageBefore()) {
                        document.newPage();
                    }
                    float s = (float) sr.getScale();
                    float offsetx = (float) sr.getOffsetX();
                    float offsety = (float) sr.getOffsetY();
                    if (sr.isOffsetXPercent()) {
                        offsetx = offsetx * size.getWidth() / 100;
                    }
                    if (sr.isOffsetXPercent()) {
                        offsety = offsety * size.getHeight() / 100;
                    }
                    float a, b, c, d, e, f;
                    switch (sr.getRotate()) {
                        case 'N':
                            a = s;
                            b = 0;
                            c = 0;
                            d = s;
                            e = offsetx * s;
                            f = offsety * s;
                            break;
                        case 'R':
                            a = 0;
                            b = -s;
                            c = s;
                            d = 0;
                            e = offsety * s;
                            f = -offsetx * s;
                            break;
                        case 'U':
                            a = -s;
                            b = 0;
                            c = 0;
                            d = -s;
                            e = -offsetx * s;
                            f = -offsety * s;
                            break;
                        case 'L':
                            a = 0;
                            b = s;
                            c = -s;
                            d = 0;
                            e = -offsety * s;
                            f = offsetx * s;
                            break;
                        default:
                            throw new RuntimeException("" + sr.getRotate());
                    }
                    int pg = sr.getPageNumber();
                    if (sr.getPageBase() == PageBase.BEGINNING) {
                        pg += idx;
                    } else if (sr.getPageBase() == PageBase.END) {
                        pg += reverseIdx;
                    }
                    if (pg < 1) {
                        throw new IOException("Invalid page number. Check your n-up rules.");
                    }
                    if (pg <= cnt) {
                        page = writer.getImportedPage(currentReader, pg);
                        cb.addTemplate(page, a, b, c, d, e, f);
                        if (preserveHyperlinks)
                            repositionAnnotations(pdAnnotations, pg, a, b, c, d, e, f);

                        if (sr.getFrameWidth() > 0) {
                            cb.setLineWidth((float) sr.getFrameWidth());
                            cb.rectangle(e, f, a * size.getWidth() + c * size.getHeight(),
                                b * size.getWidth() + d * size.getHeight());
                            cb.stroke();
                        }
                    } else {
                        writer.setPageEmpty(false);
                    }

                    if (preserveHyperlinks) {
                        if (pg < destinationPageNumbers.length) {
                            if (destinationPageNumbers[pg] - 1 < tmp.size()) {
                                tmp.get(destinationPageNumbers[pg] - 1).addAll(pdAnnotations.get(pg - 1));
                            } else {
                                tmp.add(destinationPageNumbers[pg] - 1, pdAnnotations.get(pg - 1));
                            }
                        }
                    }
                }
            }
        }

        pdAnnotations = tmp;

        copyXMPMetadata(currentReader, writer);
        document.close();
        copyInformation(currentReader, currentReader = getTempPdfReader(baos));
    }

    public void addPageMarks() {
        int pageCount = currentReader.getNumberOfPages();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = currentReader.getPageN(i);
            if (p != null && p.isDictionary()) {
                p.put(new PdfName(PDFTK_PAGE_MARKER), new PdfNumber(i));
            }
        }
    }

    public void removePageMarks() {
        int pageCount = currentReader.getNumberOfPages();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = currentReader.getPageN(i);
            if (p != null && p.isDictionary()) {
                p.remove(new PdfName(PDFTK_PAGE_MARKER));
            }
        }
    }

    public void updateBookmarks(PdfBookmark[] bm) throws DocumentException, IOException {
        OutputStream baos = createTempOutputStream();
        PdfStamper stamper = new PdfStamper(currentReader, baos);
        stamper.setOutlines(PdfBookmark.makeBookmarks(bm));
        stamper.close();
        currentReader = getTempPdfReader(baos);
    }

    public void addWatermark(String wmFile, String wmText, int wmSize, float wmOpacity, Color wmColor, int pnPosition,
                             boolean pnFlipEven, int pnSize, float pnHOff, float pnVOff, String mask)
        throws DocumentException, IOException {
        OutputStream baos = createTempOutputStream();
        int pagecount = currentReader.getNumberOfPages();
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(wmOpacity);
        PdfStamper stamper = new PdfStamper(currentReader, baos);
        BaseFont bf = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
        float txtwidth = 0;
        PdfImportedPage wmTemplate = null;
        String[] pageLabels = null;
        PdfPageLabelFormat[] pageLabelFormats = null;
        if (wmText != null) {
            txtwidth = bf.getWidthPoint(wmText, wmSize);
        }
        if (wmFile != null) {
            wmTemplate = stamper.getImportedPage(jpdftwist.utils.PdfParser.open(wmFile, true), 1);
        }
        if (mask != null && mask.length() > 0) {
            pageLabels = PdfPageLabels.getPageLabels(currentReader);
            if (pageLabels == null) {
                pageLabels = new String[pagecount];
                for (int i = 1; i <= pagecount; i++) {
                    pageLabels[i - 1] = "" + i;
                }
            }
            pageLabelFormats = PdfPageLabels.getPageLabelFormats(currentReader);
            if (pageLabelFormats == null || pageLabelFormats.length == 0) {
                pageLabelFormats = new PdfPageLabelFormat[]{
                    new PdfPageLabelFormat(1, PdfPageLabels.DECIMAL_ARABIC_NUMERALS, "", 1)};
            }
        }
        for (int i = 1; i <= pagecount; i++) {
            if (wmTemplate != null) {
                PdfContentByte underContent = stamper.getUnderContent(i);
                underContent.addTemplate(wmTemplate, 0, 0);
                /*
                 * if (preserveHyperlinks) { List links = currentReader.getLinks(i); PdfWriter w
                 * = underContent.getPdfWriter(); for (int j = 0; j < links.size(); j++) {
                 * PdfAnnotation.PdfImportedLink link = (PdfAnnotation.PdfImportedLink)
                 * links.get(j); if (link.isInternal()) { continue; // preserving internal links
                 * would be pointless here } w.addAnnotation(link.createAnnotation(w)); } }
                 */
            }
            PdfContentByte overContent = stamper.getOverContent(i);
            Rectangle size = currentReader.getPageSizeWithRotation(i);
            if (wmText != null) {
                float angle = (float) Math.atan(size.getHeight() / size.getWidth());
                float m1 = (float) Math.cos(angle);
                float m2 = (float) -Math.sin(angle);
                float m3 = (float) Math.sin(angle);
                float m4 = (float) Math.cos(angle);
                float xoff = (float) (-Math.cos(angle) * txtwidth / 2 - Math.sin(angle) * wmSize / 2);
                float yoff = (float) (Math.sin(angle) * txtwidth / 2 - Math.cos(angle) * wmSize / 2);
                overContent.saveState();
                overContent.setGState(gs1);
                overContent.beginText();
                if (wmColor != null) {
                    overContent.setColorFill(new BaseColor(wmColor));
                }
                overContent.setFontAndSize(bf, wmSize);
                overContent.setTextMatrix(m1, m2, m3, m4, xoff + size.getWidth() / 2, yoff + size.getHeight() / 2);
                overContent.showText(wmText);
                overContent.endText();
                overContent.restoreState();
            }
            if (pnPosition != -1) {
                overContent.beginText();
                overContent.setFontAndSize(bf, pnSize);
                int pnXPosition = pnPosition % 3;
                if (pnFlipEven && i % 2 == 0) {
                    pnXPosition = 2 - pnXPosition;
                }
                float xx = pnHOff * ((pnXPosition == 2) ? -1 : 1) + size.getWidth() * pnXPosition / 2.0f;
                float yy = pnVOff * ((pnPosition / 3 == 2) ? -1 : 1) + size.getHeight() * (pnPosition / 3f) / 2.0f;
                String number = "" + i;
                if (mask != null && mask.length() > 0) {
                    int pagenumber = i;
                    for (PdfPageLabelFormat format : pageLabelFormats) {
                        if (format.physicalPage <= i) {
                            pagenumber = i - format.physicalPage + format.logicalPage;
                        }
                    }
                    String pagenumbertext = pageLabels[i - 1];
                    try {
                        number = String.format(mask, i, pagecount, pagenumber, pagenumbertext);
                    } catch (IllegalFormatException ex) {
                        throw new IOException(ex.toString());
                    }
                }
                if ((pnXPosition != 1 && pnHOff * 2 < bf.getWidthPoint(number, pnSize))
                    || (pnPosition / 3 == 0 && pnVOff < bf.getDescentPoint(number, pnSize))
                    || (pnPosition / 3 == 2 && pnVOff < bf.getAscentPoint(number, pnSize))) {
                    throw new IOException("Page number " + number + " is not within page bounding box");
                }
                overContent.showTextAligned(PdfContentByte.ALIGN_CENTER, number, xx, yy, 0);
                overContent.endText();
            }
        }
        stamper.close();
        currentReader = getTempPdfReader(baos);
    }

    public void addWatermark(WatermarkStyle style) throws DocumentException, IOException {
        final OutputStream baos = createTempOutputStream();

        WatermarkProcessor watermarkProcessor = new WatermarkProcessor();
        watermarkProcessor.apply(baos, currentReader, style, pageRanges, maxLength, interleaveSize);

        currentReader = getTempPdfReader(baos);
    }

    public int getPageCount() {
        return currentReader.getNumberOfPages();
    }

    public void setTransition(int page, int type, int tduration, int pduration) {
        if (transitionValues == null) {
            transitionValues = new int[getPageCount()][3];
            for (int i = 0; i < transitionValues.length; i++) {
                transitionValues[i][2] = -1;
            }
        }
        transitionValues[page - 1][0] = type;
        transitionValues[page - 1][1] = tduration;
        transitionValues[page - 1][2] = pduration;
    }

    public void setViewerPreferences(int simplePrefs, Map<PdfName, PdfObject> optionalPrefs) {
        this.optionalViewerPreferences = optionalPrefs;
        this.simpleViewerPreferences = simplePrefs;
    }

    public void addFile(File f) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(f);
    }

    public void setSignature(File keystoreFile, String alias, char[] password, int certificationLevel, boolean visible)
        throws IOException {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Files.newInputStream(keystoreFile.toPath()), password);
            key = (PrivateKey) ks.getKey(alias, password);
            if (key == null) {
                throw new IOException("No private key found with alias " + alias);
            }
            certChain = ks.getCertificateChain(alias);
            this.certificationLevel = certificationLevel;
            this.sigVisible = visible;
        } catch (GeneralSecurityException ex) {
            throw new IOException(ex.toString(), ex);
        }
    }

    public void setPageNumbers(PdfPageLabelFormat[] labelFormats)
        throws DocumentException, IOException {
        PdfPageLabels lbls = new PdfPageLabels();
        for (PdfPageLabelFormat format : labelFormats) {
            lbls.addPageLabel(format);
        }
        Document document = new Document(currentReader.getPageSizeWithRotation(1));
        OutputStream baos = createTempOutputStream();
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        PdfImportedPage page;
        outputEventListener.setAction("Adding page numbers");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        for (int i = 0; i < currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            page = copy.getImportedPage(currentReader, i + 1);
            copy.addPage(page);
        }
        PRAcroForm form = currentReader.getAcroForm();
        if (form != null) {
            copy.copyAcroForm(currentReader);
        }
        copy.setPageLabels(lbls);
        copyXMPMetadata(currentReader, copy);
        document.close();
        copyInformation(currentReader, currentReader = getTempPdfReader(baos));
    }

    public void preserveHyperlinks() {
        preserveHyperlinks = true;
        for (PageRange range : pageRanges) {
            try {
                String filepath = range.getName();
                if (!SupportedFileTypes.getFileExtension(filepath).equals("pdf")) {
                    pdAnnotations.add(null);
                    continue;
                }
                InputStream in = Files.newInputStream(new File(filepath).toPath());
                PDFParser parser = new PDFParser((RandomAccessRead) in);
                parser.parse();
                PDDocument pdDocument = parser.getPDDocument();
                pdDocuments.add(pdDocument);
                List<PDPage> allPages = (List<PDPage>) pdDocument.getDocumentCatalog().getPages();
                for (PDPage pdPage : allPages) {
                    List<PDAnnotation> annotations = pdPage.getAnnotations();
                    pdAnnotations.add(annotations);
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void repositionAnnotations(ArrayList<List<PDAnnotation>> pdAnnotations, int page, float a, float b, float c, float d, float e, float f) {
        if (page > pdAnnotations.size())
            return;

        List<PDAnnotation> pageAnnotations = pdAnnotations.get(page - 1);
        if (pageAnnotations == null) {
            return;
        }
        for (PDAnnotation annot : pageAnnotations) {
            PDRectangle rect = annot.getRectangle();

            if (rect == null)
                continue;

            float llx = rect.getLowerLeftX();
            float lly = rect.getLowerLeftY();
            float urx = rect.getUpperRightX();
            float ury = rect.getUpperRightY();

            float x = llx * a + lly * c + e;
            float y = llx * b + lly * d + f;
            llx = x;
            lly = y;
            x = urx * a + ury * c + e;
            y = urx * b + ury * d + f;
            urx = x;
            ury = y;

            rect.setLowerLeftX(llx);
            rect.setLowerLeftY(lly);
            rect.setUpperRightX(urx);
            rect.setUpperRightY(ury);

            annot.setRectangle(rect);
        }
    }
}