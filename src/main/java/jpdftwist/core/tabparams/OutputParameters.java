package jpdftwist.core.tabparams;

import jpdftwist.core.PdfToImage;

public class OutputParameters {

    private final String outputFilepath;
    private final boolean multiPageTiff;
    private final boolean burst;
    private final boolean uncompressed;
    private final boolean optimizeSize;
    private final boolean fullyCompressed;
    private final boolean pageMarks;
    private final PdfToImage pdfToImage;

    public OutputParameters(String outputFilepath, boolean multiPageTiff, boolean burst, boolean uncompressed, boolean optimizeSize, boolean fullyCompressed, boolean pageMarks, PdfToImage pdfToImage) {
        this.outputFilepath = outputFilepath;
        this.multiPageTiff = multiPageTiff;
        this.burst = burst;
        this.uncompressed = uncompressed;
        this.optimizeSize = optimizeSize;
        this.fullyCompressed = fullyCompressed;
        this.pageMarks = pageMarks;
        this.pdfToImage = pdfToImage;
    }

    public String getOutputFilepath() {
        return outputFilepath;
    }

    public boolean isMultiPageTiff() {
        return multiPageTiff;
    }

    public boolean isBurst() {
        return burst;
    }

    public boolean isUncompressed() {
        return uncompressed;
    }

    public boolean isOptimizeSize() {
        return optimizeSize;
    }

    public boolean isFullyCompressed() {
        return fullyCompressed;
    }

    public boolean isPageMarks() {
        return pageMarks;
    }

    public PdfToImage getPdfToImage() {
        return pdfToImage;
    }
}
