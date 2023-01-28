package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;
import jpdftwist.core.tabparams.RotateParameters;
import jpdftwist.core.tabparams.ScaleParameters;
import jpdftwist.core.watermark.WatermarkProcessor;
import jpdftwist.core.watermark.WatermarkStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class PDFTwist {

    private final TempFileManager tempFileManager;
    private final PdfEncryptionManager pdfEncryptionManager;
    private final PdfReaderManager pdfReaderManager;
    private final SignatureManager signatureManager;
    private final AttachmentsManager attachmentsManager;
    private final TransitionManager transitionManager;
    private final ViewerPreferencesManager viewerPreferencesManager;
    private final InputOrderManager inputOrderManager;
    private final PageMarksProcessor pageMarksProcessor;
    private final OptimizeSizeProcessor optimizeSizeProcessor;
    private final OutputMultiPageTiffProcessor outputMultiPageTiffProcessor;
    private final BurstFilesProcessor burstFilesProcessor;
    private final OutputPdfProcessor outputPdfProcessor;
    private final AnnotationsProcessor annotationsProcessor;

    private final String inputFilePath;
    private final String inputFileName;
    private final String inputFileFullName;
    private PdfToImage pdfImages;
    private final boolean mergeByDir;
    private String rootFolder;
    private final List<PageRange> pageRanges;
    private final int interleaveSize;
    private final OutputEventListener outputEventListener;

    public PDFTwist(List<PageRange> pageRanges, boolean useTempFiles, boolean mergeByDir, int interleaveSize, OutputEventListener outputEventListener) throws IOException {
        this.outputEventListener = outputEventListener;
        this.pageRanges = pageRanges;
        this.mergeByDir = mergeByDir;
        this.interleaveSize = interleaveSize;

        this.tempFileManager = new TempFileManager(useTempFiles);
        this.inputOrderManager = new InputOrderManager();
        this.pdfEncryptionManager = new PdfEncryptionManager();
        this.pdfReaderManager = new PdfReaderManager(tempFileManager, inputOrderManager, pdfEncryptionManager);
        this.signatureManager = new SignatureManager();
        this.attachmentsManager = new AttachmentsManager();
        this.transitionManager = new TransitionManager(pdfReaderManager);
        this.viewerPreferencesManager = new ViewerPreferencesManager();

        this.pageMarksProcessor = new PageMarksProcessor(pdfReaderManager);
        this.optimizeSizeProcessor = new OptimizeSizeProcessor(tempFileManager, pdfReaderManager);
        this.outputMultiPageTiffProcessor = new OutputMultiPageTiffProcessor(pdfReaderManager);
        this.burstFilesProcessor = new BurstFilesProcessor(pdfReaderManager, pdfEncryptionManager);
        this.outputPdfProcessor = new OutputPdfProcessor(pdfReaderManager, pdfEncryptionManager, signatureManager, attachmentsManager, transitionManager, viewerPreferencesManager);
        this.annotationsProcessor = new AnnotationsProcessor();

        this.inputFilePath = pageRanges.get(0).getParentName();
        this.inputFileFullName = pageRanges.get(0).getFilename();
        int pos = inputFileFullName.lastIndexOf('.');
        if (pos == -1) {
            inputFileName = inputFileFullName;
        } else {
            inputFileName = inputFileFullName.substring(0, pos);
        }

        pdfReaderManager.initializeReader(pageRanges, interleaveSize);

        keepFileParents();
    }

    public void cancel() {
        this.optimizeSizeProcessor.cancel();
        this.outputMultiPageTiffProcessor.cancel();
        this.burstFilesProcessor.cancel();
        this.outputPdfProcessor.cancel();
    }

    public static PdfReader getTempPdfReader(OutputStream out, File tempFile) throws IOException {
        return new InputReader().getTempPdfReader(out, tempFile);
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

    public void updateInfoDictionary(Map<String, String> newInfo) {
        PdfDictionary trailer = pdfReaderManager.getCurrentReader().getTrailer();
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
        pdfReaderManager.getCurrentReader().getCatalog().remove(PdfName.METADATA);
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
        if (ownerPassword.length == 0) {
            throw new IOException("Owner password may not be empty");
        }

        pdfEncryptionManager.setEncryptionMode(mode);
        pdfEncryptionManager.setEncryptionPermissions(permissions);
        pdfEncryptionManager.setUserPassword(userPassword);
        pdfEncryptionManager.setOwnerPassword(ownerPassword);
    }

    public void writeOutput(String rawOutputFile, boolean multiPageTiff, boolean burst, boolean uncompressed, boolean sizeOptimize, boolean fullyCompressed)
        throws IOException, DocumentException {

        final String outputFile = expandOutputPath(rawOutputFile);

        try {
            if (sizeOptimize) {
                optimizeSize();
            }

            pdfReaderManager.cargoCult();

            if (uncompressed && pdfImages == null) {
                Document.compress = false;
            }
            int pageCount = pdfReaderManager.getPageCount();
            outputEventListener.setPageCount(pageCount);
            if (multiPageTiff) {
                outputMultiPageTiff(outputFile);
            } else if (burst) {
                burstFiles(outputFile, fullyCompressed);
            } else {
                outputPdf(outputFile, fullyCompressed, pageCount);
            }
        } catch (CancelOperationException ignored) {
            return;
        } finally {
            Document.compress = true;
        }

        cleanupOpenResources();

        if (annotationsProcessor.isPreserveHyperlinks()) {
            annotationsProcessor.preserveHyperlinks(outputFile);
        }
    }

    public void cropPages(PageBox cropTo) throws IOException, DocumentException {
        CropProcessor cropProcessor = new CropProcessor(tempFileManager, pdfReaderManager, annotationsProcessor);
        cropProcessor.apply(outputEventListener, cropTo, tempFileManager.getTempFile());
    }

    public void rotatePages(RotateParameters param) {
        RotateProcessor rotateProcessor = new RotateProcessor(pdfReaderManager);
        rotateProcessor.apply(outputEventListener, param);
    }

    public void removeRotation() throws DocumentException, IOException {
        RemoveRotationProcessor removeRotationProcessor = new RemoveRotationProcessor(tempFileManager, pdfReaderManager, annotationsProcessor);
        removeRotationProcessor.apply(outputEventListener, tempFileManager.getTempFile());
    }

    public void scalePages(ScaleParameters param) throws DocumentException, IOException {
        ScaleProcessor scaleProcessor = new ScaleProcessor(tempFileManager, pdfReaderManager, annotationsProcessor);
        scaleProcessor.apply(outputEventListener, param, tempFileManager.getTempFile());
    }

    public void shufflePages(int passLength, int blockSize, ShuffleRule[] shuffleRules) throws DocumentException, IOException {
        ShufflePagesProcessor shufflePagesProcessor = new ShufflePagesProcessor(tempFileManager, pdfReaderManager, annotationsProcessor);
        shufflePagesProcessor.apply(outputEventListener, passLength, blockSize, shuffleRules, tempFileManager.getTempFile());
    }

    public void addPageMarks() {
        pageMarksProcessor.addPageMarks();
    }

    public void removePageMarks() {
        pageMarksProcessor.removePageMarks();
    }

    public void updateBookmarks(PdfBookmark[] bm) throws DocumentException, IOException {
        BookmarksProcessor bookmarksProcessor = new BookmarksProcessor(tempFileManager, pdfReaderManager);
        bookmarksProcessor.updateBookmarks(bm, tempFileManager.getTempFile());
    }

    public void addWatermark(String wmFile, String wmText, int wmSize, float wmOpacity, Color wmColor, int pnPosition,
                             boolean pnFlipEven, int pnSize, float pnHOff, float pnVOff, String mask)
        throws DocumentException, IOException {
        WatermarkProcessor watermarkProcessor = new WatermarkProcessor(tempFileManager, pdfReaderManager);
        watermarkProcessor.apply(wmFile, wmText, wmSize, wmOpacity, wmColor, pnPosition, pnFlipEven, pnSize, pnHOff, pnVOff, mask, tempFileManager.getTempFile());
    }

    public void addWatermark(WatermarkStyle style) throws DocumentException, IOException {
        WatermarkProcessor watermarkProcessor = new WatermarkProcessor(tempFileManager, pdfReaderManager);
        int maxLength = inputOrderManager.calculateMaxLength(pageRanges).getMaxLength();
        watermarkProcessor.apply(style, pageRanges, maxLength, interleaveSize, tempFileManager.getTempFile());
    }

    public int getPageCount() {
        return pdfReaderManager.getPageCount();
    }

    public void setTransition(int page, int type, int tduration, int pduration) {
        transitionManager.setTransition(page, type, tduration, pduration);
    }

    public void setViewerPreferences(int simplePrefs, Map<PdfName, PdfObject> optionalPrefs) {
        this.viewerPreferencesManager.setViewerPreferences(simplePrefs, optionalPrefs);
    }

    public void addFile(File f) {
        attachmentsManager.addFile(f);
    }

    public void setSignature(File keystoreFile, String alias, char[] password, int certificationLevel, boolean visible)
        throws IOException {
        signatureManager.setSignature(keystoreFile, alias, password, certificationLevel, visible);
    }

    public void setPageNumbers(PdfPageLabelFormat[] labelFormats) throws DocumentException, IOException {
        PageNumberProcessor pageNumberProcessor = new PageNumberProcessor(tempFileManager, pdfReaderManager);
        pageNumberProcessor.addPageNumbers(outputEventListener, labelFormats, tempFileManager.getTempFile());
    }

    public void preserveHyperlinks() {
        annotationsProcessor.preserveHyperlinks(pageRanges);
    }

    private String expandOutputPath(final String rawOutputFile) {
        String outputFile = rawOutputFile;

        if (!outputFile.contains(File.separator)) {
            File temp = new File(outputFile);
            outputFile = temp.getAbsolutePath();
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

        return outputFile;
    }

    private void optimizeSize() throws IOException, DocumentException {
        optimizeSizeProcessor.optimizeSize(outputEventListener);
    }

    private void outputMultiPageTiff(String outputFile) throws IOException, DocumentException {
        outputMultiPageTiffProcessor.output(outputEventListener, outputFile, pdfImages);
    }

    private void burstFiles(String outputFile, boolean fullyCompressed) throws IOException, DocumentException {
        burstFilesProcessor.burst(outputEventListener, outputFile, fullyCompressed, pdfImages);
    }

    private void outputPdf(String outputFile, boolean fullyCompressed, int total) throws IOException, DocumentException {
        outputPdfProcessor.output(outputEventListener, outputFile, fullyCompressed, total);
    }

    public void cleanupOpenResources() {
        pdfReaderManager.cleanup();
        tempFileManager.cleanup();
    }
}