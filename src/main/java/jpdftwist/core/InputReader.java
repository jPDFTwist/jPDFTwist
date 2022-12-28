package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import jpdftwist.core.input.VirtualBlankPage;
import jpdftwist.utils.JImageParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputReader {

    public PdfReader getPdfReader(PageRange pageRange, byte[] ownerPassword) throws IOException, DocumentException {
        if (pageRange.isVirtualFile()) {
            if (pageRange.isPDF()) {
                return getVirtualPdfReader(pageRange);
            } else if (pageRange.isImage()) {
                return getImagePdfReader(pageRange, pageRange.getVirtualFilePageCount());
            } else { // FIXME: Assumes the else is BLANK, too restrictive
                return getBlankReader(pageRange);
            }
        } else {
            if (pageRange.isPDF()) {
                return getPdfReader(pageRange.getName(), ownerPassword);
            } else if (pageRange.isImage()) {
                return getImagePdfReader(pageRange);
            } // FIXME: Else throw error
        }

        return null;
    }

    public PdfReader getTempPdfReader(OutputStream out, boolean useTempFiles, String tempFilePath) throws IOException {
        if (useTempFiles) {
            return new PdfReader(tempFilePath, null);
        }

        byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
        return new PdfReader(bytes);
    }

    private PdfReader getPdfReader(String filepath, byte[] ownerPassword) throws IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(filepath, false, true);
        return new PdfReader(raf, ownerPassword);
    }

    private PdfReader getBlankReader(PageRange pageRange) throws IOException {
        PDDocument document = new PDDocument();

        VirtualBlankPage pageTemplate = pageRange.getVirtualBlankPageTemplate();
        for (int i = 0; i < pageRange.getVirtualFilePageCount(); i++) {
            float width = (float) pageTemplate.getWidth();
            float height = (float) pageTemplate.getHeight();

            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            PDPageContentStream cos = new PDPageContentStream(document, page);
            cos.setNonStrokingColor(pageTemplate.getBackgroundColor());
            cos.addRect(0, 0, width, height);
            cos.fill();
            cos.close();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        out.close();

        return new PdfReader(out.toByteArray());
    }

    private PdfReader getVirtualPdfReader(PageRange pageRange) throws IOException {
        PDDocument document = PDDocument.load(new File(pageRange.getVirtualFileSrcFilePath()));
        PDDocument newDoc = new PDDocument();

        int numberOfPages = pageRange.getVirtualFilePageCount();
        int numberOfFilePages = document.getNumberOfPages();

        int repeat = numberOfPages / numberOfFilePages;

        for (int r = 0; r < repeat; r++) {
            for (int i = 0; i < numberOfFilePages; i++) {
                PDPage page = document.getDocumentCatalog().getPages().get(i);
                newDoc.addPage(page);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        newDoc.save(out);
        newDoc.close();
        document.close();
        out.close();

        return new PdfReader(out.toByteArray());
    }

    private PdfReader getImagePdfReader(PageRange pageRange) {
        return getImagePdfReader(pageRange, 1);
    }

    private PdfReader getImagePdfReader(PageRange pageRange, int repeat) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            String srcFile;

            if (pageRange.isVirtualFile()) {
                srcFile = pageRange.getVirtualFileSrcFilePath();
            } else {
                srcFile = pageRange.getName();
            }

            com.itextpdf.text.Image pdfImage = JImageParser.readItextImage(srcFile);

            if (pdfImage == null) {
                throw new IOException(
                    String.format("Image %s\n not supported or corrupted!", pageRange.getName()));
            }

            for (int i = 0; i < repeat; i++) {
                document.setPageSize(new Rectangle(pdfImage.getWidth(), pdfImage.getHeight()));
                document.setMargins(0, 0, 0, 0);
                document.newPage();
                document.add(pdfImage);
            }

            document.close();
            writer.close();

            PdfReader rdr = new PdfReader(baos.toByteArray());
            baos.close();

            return rdr;
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
