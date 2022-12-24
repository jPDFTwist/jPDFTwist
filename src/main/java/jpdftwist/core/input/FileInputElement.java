package jpdftwist.core.input;

import jpdftwist.core.IntegerList;

import java.io.File;
import java.util.List;

public class FileInputElement {

    private final String filePath;
    private final String parentFilePath;
    private final boolean virtual;
    private final FileInputElementType type;
    private final IntegerList emptyBefore;
    private final Integer from;
    private final Integer to;
    private final Boolean includeEven;
    private final Boolean includeOdd;
    private Integer pageCount;
    private String srcFilePath; // Only for virtual
    private String fileSize; // Only for real
    private String colorDepth; // Only for image
    private PageDimensions imageSize; // Only for image
    private VirtualBlankPage virtualBlankPage;
    private final List<Integer> pageIndices;

    public FileInputElement(String filePath, String parentFilePath, boolean virtual, FileInputElementType type, IntegerList emptyBefore, Integer from, Integer to, Boolean includeEven, Boolean includeOdd, List<Integer> pageIndices) {
        this.filePath = filePath;
        this.parentFilePath = parentFilePath;
        this.virtual = virtual;
        this.type = type;
        this.emptyBefore = emptyBefore;
        this.from = from;
        this.to = to;
        this.includeEven = includeEven;
        this.includeOdd = includeOdd;
        this.pageIndices = pageIndices;
    }

    public String getFileName() {
        int beginIndex = filePath.lastIndexOf(File.separatorChar);

        return filePath.substring(beginIndex + 1);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getParentFilePath() {
        return parentFilePath;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public boolean isPDF() {
        return this.type == FileInputElementType.PDF;
    }

    public boolean isImage() {
        return this.type == FileInputElementType.IMAGE;
    }

    public boolean isBlank() {
        return this.type == FileInputElementType.BLANK;
    }

    public IntegerList getEmptyBefore() {
        return emptyBefore;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public Boolean getIncludeEven() {
        return includeEven;
    }

    public Boolean getIncludeOdd() {
        return includeOdd;
    }

    public Integer getVirtualFilePageCount() {
        if (!isVirtual()) {
            return null; // FIXME: Throw instead of returning
        }

        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getVirtualFileSrcFilePath() {
        if (!isVirtual()) {
            return null; // FIXME: Throw instead of returning
        }

        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getFileSize() {
        if (isVirtual()) {
            return "NaN"; // FIXME: Throw instead of returning
        }

        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getColorDepth() {
        if (!isImage()) {
            return "NaN"; // FIXME: Throw instead of returning
        }
        return colorDepth;
    }

    public void setColorDepth(String colorDepth) {
        this.colorDepth = colorDepth;
    }

    public PageDimensions getImageSize() {
        if (!isImage()) {
            return null; // FIXME: Throw instead of returning
        }

        return imageSize;
    }

    public void setImageSize(PageDimensions imageSize) {
        this.imageSize = imageSize;
    }

    public VirtualBlankPage getVirtualBlankPage() {
        if (isVirtual() && isBlank()) {
            return virtualBlankPage;
        }
        
        return null; //FIXME: Throw instead of returning
    }

    public void setVirtualBlankPage(VirtualBlankPage virtualBlankPage) {
        this.virtualBlankPage = virtualBlankPage;
    }

    public List<Integer> getPageIndices() {
        return pageIndices;
    }
}
