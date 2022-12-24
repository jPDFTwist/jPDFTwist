package jpdftwist.core;

import jpdftwist.core.input.FileInputElement;
import jpdftwist.core.input.PageDimensions;
import jpdftwist.core.input.VirtualBlankPage;

import java.util.Arrays;

public class PageRange {

    private final FileInputElement fileInputElement;

    public PageRange(FileInputElement fileInputElement) {
        this.fileInputElement = fileInputElement;
    }

    public int[] getPages(int pagesBefore) {
        IntegerList emptyBefore = fileInputElement.getEmptyBefore();
        Integer from = fileInputElement.getFrom();
        Integer to = fileInputElement.getTo();
        Boolean includeEven = fileInputElement.getIncludeEven();
        Boolean includeOdd = fileInputElement.getIncludeOdd();

        int emptyPagesBefore = emptyBefore.getValue()[pagesBefore % emptyBefore.getValue().length];
        int[] pages = new int[emptyPagesBefore + Math.abs(to - from) + 1];
        Arrays.fill(pages, 0, emptyPagesBefore, -1);
        int length = emptyPagesBefore;
        for (int i = from; ; i += from > to ? -1 : 1) {
            if ((i % 2 == 0 && includeEven) || (i % 2 == 1 && includeOdd)) {
                if (fileInputElement.getPageIndices().size() >= i)
                    pages[length++] = fileInputElement.getPageIndices().get(i - 1) + 1;
            }
            if (i == to) {
                break;
            }
        }
        if (length != pages.length) {
            int[] newPages = new int[length];
            System.arraycopy(pages, 0, newPages, 0, length);
            return newPages;
        }
        return pages;
    }

    public String getFilename() {
        return fileInputElement.getFileName();
    }

    public String getName() {
        return fileInputElement.getFilePath();
    }

    public String getParentName() {
        return fileInputElement.getParentFilePath();
    }

    public boolean isPDF() {
        return fileInputElement.isPDF();
    }

    public boolean isImage() {
        return fileInputElement.isImage();
    }

    public boolean isVirtualFile() {
        return fileInputElement.isVirtual();
    }

    public VirtualBlankPage getVirtualBlankPageTemplate() {
        return fileInputElement.getVirtualBlankPage();
    }

    public PageDimensions getImageSize() {
        return fileInputElement.getImageSize();
    }

    public Integer getVirtualFilePageCount() {
        return fileInputElement.getVirtualFilePageCount();
    }

    public String getVirtualFileSrcFilePath() {
        return fileInputElement.getVirtualFileSrcFilePath();
    }

    public String getFileSize() {
        return fileInputElement.getFileSize();
    }

    public String getImageColorDepth() {
        return fileInputElement.getColorDepth();
    }
}
