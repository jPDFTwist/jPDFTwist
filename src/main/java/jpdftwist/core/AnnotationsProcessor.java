package jpdftwist.core;

import jpdftwist.utils.SupportedFileTypes;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationsProcessor {

    private ArrayList<List<PDAnnotation>> pdAnnotations = new ArrayList<>();
    private final ArrayList<PDDocument> pdDocuments = new ArrayList<>();
    private boolean preserveHyperlinks = false;

    public void repositionAnnotations(int page, float a, float b, float c, float d, float e, float f) {
        if (page > pdAnnotations.size()) {
            return;
        }

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

    public void preserveHyperlinks(List<PageRange> pageRanges) {
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

    public void preserveHyperlinks(String outputFile) throws IOException {
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

    public boolean isPreserveHyperlinks() {
        return preserveHyperlinks;
    }

    public void setAnnotations(final ArrayList<List<PDAnnotation>> pdAnnotations) {
        this.pdAnnotations = pdAnnotations;
    }

    public List<PDAnnotation> getAnnotations(int index) {
        return pdAnnotations.get(index);
    }
}
