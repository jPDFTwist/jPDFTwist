package jpdftwist.core.watermark;

import com.frequal.romannumerals.Converter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfStamper;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageRange;
import jpdftwist.core.PdfReaderManager;
import jpdftwist.core.TempFileManager;
import jpdftwist.core.UnitTranslator;
import jpdftwist.core.input.PageDimensions;
import jpdftwist.utils.JImageParser;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.joda.time.DateTime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatermarkProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;

    private int bateIndex;
    private int bateRepeat;
    private BufferedReader br;

    public WatermarkProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
    }

    public void apply(String wmFile, String wmText, int wmSize, float wmOpacity, Color wmColor, int pnPosition,
                           boolean pnFlipEven, int pnSize, float pnHOff, float pnVOff, String mask,
                           File tempFile) throws DocumentException, IOException {
        try {
        OutputStream baos = tempFileManager.createTempOutputStream();
        int pageCount = pdfReaderManager.getPageCount();
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(wmOpacity);
        PdfStamper stamper = new PdfStamper(pdfReaderManager.getCurrentReader(), baos);
        BaseFont bf = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
        float txtwidth = 0;
        PdfImportedPage wmTemplate = null;
        String[] pageLabels = null;
        PdfPageLabels.PdfPageLabelFormat[] pageLabelFormats = null;
        if (wmText != null) {
            txtwidth = bf.getWidthPoint(wmText, wmSize);
        }
        if (wmFile != null) {
            wmTemplate = stamper.getImportedPage(jpdftwist.utils.PdfParser.open(wmFile, true), 1);
        }
        if (mask != null && mask.length() > 0) {
            pageLabels = PdfPageLabels.getPageLabels(pdfReaderManager.getCurrentReader());
            if (pageLabels == null) {
                pageLabels = new String[pageCount];
                for (int i = 1; i <= pageCount; i++) {
                    pageLabels[i - 1] = "" + i;
                }
            }
            pageLabelFormats = PdfPageLabels.getPageLabelFormats(pdfReaderManager.getCurrentReader());
            if (pageLabelFormats == null || pageLabelFormats.length == 0) {
                pageLabelFormats = new PdfPageLabels.PdfPageLabelFormat[]{
                    new PdfPageLabels.PdfPageLabelFormat(1, PdfPageLabels.DECIMAL_ARABIC_NUMERALS, "", 1)};
            }
        }
        for (int i = 1; i <= pageCount; i++) {
            if (wmTemplate != null) {
                PdfContentByte underContent = stamper.getUnderContent(i);
                underContent.addTemplate(wmTemplate, 0, 0);
            }
            PdfContentByte overContent = stamper.getOverContent(i);
            Rectangle size = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
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
                    for (PdfPageLabels.PdfPageLabelFormat format : pageLabelFormats) {
                        if (format.physicalPage <= i) {
                            pagenumber = i - format.physicalPage + format.logicalPage;
                        }
                    }
                    String pagenumbertext = pageLabels[i - 1];
                    try {
                        number = String.format(mask, i, pageCount, pagenumber, pagenumbertext);
                    } catch (IllegalFormatException ex) {
                        Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex057", ex);
                        throw new IOException();
                    }
                }
                if ((pnXPosition != 1 && pnHOff * 2 < bf.getWidthPoint(number, pnSize))
                    || (pnPosition / 3 == 0 && pnVOff < bf.getDescentPoint(number, pnSize))
                    || (pnPosition / 3 == 2 && pnVOff < bf.getAscentPoint(number, pnSize))) {
                    Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex090");
                    throw new IOException("Page number " + number + " is not within page bounding box");
                }
                overContent.showTextAligned(PdfContentByte.ALIGN_CENTER, number, xx, yy, 0);
                overContent.endText();
            }
        }
        stamper.close();
        pdfReaderManager.setCurrentReader(PDFTwist.getTempPdfReader(baos, tempFile));
        } catch (Exception ex) {
            Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex153", ex);
        }
    }

    public void apply(WatermarkStyle style, List<PageRange> pageRanges, int maxLength, int interleaveSize, File tempFile) throws DocumentException, IOException {
        try {
        OutputStream baos = tempFileManager.createTempOutputStream();
        int pageCount = pdfReaderManager.getPageCount();

        PdfStamper stamper = new PdfStamper(pdfReaderManager.getCurrentReader(), baos);

        int offset = 0;
        int i = 0;
        int logical = style.getLogicalPage() - 1;
        bateIndex = style.getBatesStartWith();
        bateRepeat = 1;
        List<Integer> batesList = style.getBatesPagesList();

        if (interleaveSize != 0) {
            int blockCount = (maxLength + interleaveSize - 1) / interleaveSize;
            for (int o = 0; o < blockCount; o++) {
                for (int j = 1; j <= pageRanges.size(); j++) {
                    PageRange pageRange = pageRanges.get(j - 1);
                    for (int k = 1; k <= interleaveSize; k++) {
                        int lim = (o * interleaveSize) + k;
                        i++;
                        if (i < style.getStartPage()) {
                            continue;
                        }
                        logical++;
                        if (lim > pageRange.getPages(0).length) {
                            continue;
                        }
                        Rectangle size = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
                        doWatermark(i, logical, style, pageCount, pageRange, stamper, batesList, size);
                    }
                }
            }
        } else {
            for (PageRange pageRange : pageRanges) {
                for (int j = 1; j <= pageRange.getPages(0).length; j++) {
                    i = offset + j;
                    if (i < style.getStartPage()) {
                        continue;
                    }
                    logical++;
                    Rectangle size = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
                    doWatermark(i, logical, style, pageCount, pageRange, stamper, batesList, size);
                }
                offset += pageRange.getPages(0).length;
            }
        }
        if (br != null) {
            br.close();
        }
        stamper.close();
        pdfReaderManager.setCurrentReader(PDFTwist.getTempPdfReader(baos, tempFile));
        } catch (Exception ex) {
            Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex154", ex);
        }
    }

    private void doWatermark(int i, int logical, WatermarkStyle style, int pageCount, PageRange pageRange,
                             PdfStamper stamper, List<Integer> batesList, Rectangle size) throws IOException {
        try {
        PdfContentByte overContent = stamper.getOverContent(i);

        String text = null;

        switch (style.getType()) {
            case NUMBERS:
                text = Integer.toString(logical);
                break;
            case LATIN_CAPITAL:
                Converter conv = new Converter(true);
                text = conv.toRomanNumerals(logical);
                break;
            case LATIN_LOWERCASE:
                Converter conv2 = new Converter(true);
                text = conv2.toRomanNumerals(logical).toLowerCase();
                break;
            case CAPITAL_LETTERS:
                text = toAlphabetic(logical - 1).toUpperCase();
                break;
            case LOWERCASE_LETTERS:
                text = toAlphabetic(logical - 1).toLowerCase();
                break;
            case BATES_NUMBERING:
                if ((style.getBatesApplyTo() == 1 && i % 2 == 0) || (style.getBatesApplyTo() == 2 && i % 2 != 0)
                    || (style.getBatesApplyTo() == 3 && !(pageRange.isPDF()))
                    || (style.getBatesApplyTo() == 4 && !(pageRange.isPDF()))
                    || (style.getBatesApplyTo() == 5 && !batesList.contains(i))
                    || (style.getBatesApplyTo() == 6 && batesList.contains(i))) {
                    text = "";
                    break;
                }

                String zeroPadding = "%d";
                if (style.getBatesZeroPadding() > 0) {
                    zeroPadding = "%0" + style.getBatesZeroPadding() + "d";
                }
                String bate = String.format(zeroPadding, bateIndex);

                text = style.getBatesPrefix() + bate + style.getBatesSuffix();
                if (style.getBatesRepeatFor() <= bateRepeat) {
                    bateIndex += style.getBatesStep();
                    bateRepeat = 1;
                } else {
                    bateRepeat++;
                }
                break;
            case VARIABLE_TEXT:
                if (br == null) {
                    String filename = style.getVariableTextFile();
                    br = new BufferedReader(new FileReader(filename));
                }
                String sCurrentLine;
                if ((sCurrentLine = br.readLine()) != null) {
                    text = sCurrentLine;
                } else {
                    text = "";
                    break;
                }
            case IMAGE:

            case REPEATED_TEXT:
                if (text == null) {
                    text = style.getRepeatedText();
                }
                DateTime dt = new DateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                if (pageRange.isImage()) {
                    PageDimensions page = pageRange.getImageSize();
                    double width = page.getWidth();
                    double height = page.getHeight();

                    text = text.replace("\\{img_depth\\}", pageRange.getImageColorDepth());
                    text = text.replace("\\{img_width\\}", Double.toString(width));
                    text = text.replace("\\{img_height\\}", Double.toString(height));
                    text = text.replace("\\{img_width_inch\\}", Double.toString(width / UnitTranslator.POINT_POSTSCRIPT));
                    text = text.replace("\\{img_height_inch\\}", Double.toString(height / UnitTranslator.POINT_POSTSCRIPT));
                    text = text.replace("\\{img_width_dpi\\}", Integer.toString(72)); // TODO
                    text = text.replace("\\{img_height_dpi\\}", Integer.toString(72));
                } else {
                    text = text.replace("\\{img_[a-z]*\\}", "");
                }

                String parent = "";
                String lastModified = "";

                if (pageRange.isVirtualFile()) {
                    parent = pageRange.getParentName() + File.separator;
                    lastModified = sdf.format(new Date());
                } else {
                    parent = new File(pageRange.getName()).getParent() + File.separator;
                    lastModified = sdf.format(new File(pageRange.getName()).lastModified());
                }

                text = text.replace("%h", Integer.toString(dt.getHourOfDay()));
                text = text.replace("%m", String.format("%02d", dt.getMinuteOfHour()));
                text = text.replace("%s", String.format("%02d", dt.getSecondOfMinute()));
                text = text.replace("%D", String.format("%02d", dt.getDayOfMonth()));
                text = text.replace("%M", String.format("%02d", dt.getMonthOfYear()));
                text = text.replace("%Y", String.format("%04d", dt.getYear()));
                text = text.replace("%f", pageRange.getFilename().substring(0, pageRange.getFilename().lastIndexOf('.')));
                text = text.replace("%F", pageRange.getFilename());
                text = text.replace("%p", parent);
                text = text.replace("\\{file_size\\}", pageRange.getFileSize());
                text = text.replace("%c", Integer.toString(pageCount));
                text = text.replace("\\{page_width\\}", Float.toString(size.getWidth()));
                text = text.replace("\\{page_height\\}", Float.toString(size.getHeight()));
                text = text.replace("\\{page_width_inch\\}", Float.toString(size.getWidth() / 72));
                text = text.replace("\\{page_height_inch\\}", Float.toString(size.getHeight() / 72));
                text = text.replace("%n", Integer.toString(i));
                text = text.replace("%N", Integer.toString(logical));
                text = text.replace("\\{last_modified\\}", lastModified);

                break;
            default:
                text = "";
        }

        text = style.getPrefix() + text;

        float angle = (float) ((style.getAngle() * Math.PI) / 180);
        int opacityValue = (255 * (100 - style.getOpacity())) / 100;
        int hor;
        int ver;
        int boxWidth;
        int boxHeight;
        TextRenderer.TextAlignment alignment = null;

        switch (style.getUnits()) {
            case INCHES:
                boxWidth = (int) (UnitTranslator.POINT_POSTSCRIPT * style.getWidth());
                boxHeight = (int) (UnitTranslator.POINT_POSTSCRIPT * style.getHeight());
                hor = (int) (UnitTranslator.POINT_POSTSCRIPT * style.getHorizontalPosition());
                ver = (int) (UnitTranslator.POINT_POSTSCRIPT * style.getVerticalPosition());
                break;
            case MM:
                boxWidth = (int) UnitTranslator.millisToPoints(style.getWidth());
                boxHeight = (int) UnitTranslator.millisToPoints(style.getHeight());
                hor = (int) UnitTranslator.millisToPoints(style.getHorizontalPosition());
                ver = (int) UnitTranslator.millisToPoints(style.getVerticalPosition());
                break;
            default:
                boxWidth = (int) style.getWidth();
                boxHeight = (int) style.getHeight();
                hor = (int) style.getHorizontalPosition();
                ver = (int) style.getVerticalPosition();
                break;
        }

        BufferedImage target = null;

        switch (style.getHorizontalAlign()) {
            case LEFT:
                switch (style.getVerticalAlign()) {
                    case TOP:
                        alignment = TextRenderer.TextAlignment.TOP_LEFT;
                        break;
                    case MIDDLE:
                        alignment = TextRenderer.TextAlignment.MIDDLE_LEFT;
                        break;
                    default:
                        alignment = TextRenderer.TextAlignment.BOTTOM_LEFT;
                        break;
                }
                break;
            case CENTER:
                switch (style.getVerticalAlign()) {
                    case TOP:
                        alignment = TextRenderer.TextAlignment.TOP;
                        break;
                    case MIDDLE:
                        alignment = TextRenderer.TextAlignment.MIDDLE;
                        break;
                    default:
                        alignment = TextRenderer.TextAlignment.BOTTOM;
                        break;
                }
                break;
            case RIGHT:
                switch (style.getVerticalAlign()) {
                    case TOP:
                        alignment = TextRenderer.TextAlignment.TOP_RIGHT;
                        break;
                    case MIDDLE:
                        alignment = TextRenderer.TextAlignment.MIDDLE_RIGHT;
                        break;
                    default:
                        alignment = TextRenderer.TextAlignment.BOTTOM_RIGHT;
                        break;
                }
                break;
            default:
                switch (style.getVerticalAlign()) {
                    case TOP:
                        alignment = TextRenderer.TextAlignment.TOP_JUSTIFY;
                        break;
                    case MIDDLE:
                        alignment = TextRenderer.TextAlignment.JUSTIFY;
                        break;
                    default:
                        alignment = TextRenderer.TextAlignment.BOTTOM_JUSTIFY;
                        break;
                }
                break;
        }

        Graphics2D g2d = overContent.createGraphics(size.getWidth(), size.getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (style.getType() == WatermarkStyle.WatermarkType.IMAGE) {
            BufferedImage source = null;

            if (style.getImagePath().toLowerCase().endsWith(".pdf")) {
                PDDocument document = PDDocument.load(new File(style.getImagePath()));
                PDPageTree list = document.getPages();

                PDPage page = list.get(style.getPdfPage());
                PDResources pdResources = page.getResources();
                for (COSName cosName : pdResources.getXObjectNames()) // loop for all resources
                {
                    PDXObject pdxObject = pdResources.getXObject(cosName);
                    if (pdxObject instanceof PDImageXObject) { // check that the resource is image
                        source = ((PDImageXObject) pdxObject).getImage();
                    }
                    document.close();
                }
            } else {
                source = (BufferedImage) JImageParser.readAwtImage(style.getImagePath());
            }

            //target = new BufferedImage(source.getWidth(null), source.getHeight(null), java.awt.Transparency.TRANSLUCENT);

            target = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = target.createGraphics();

            float alpha = (100 - style.getOpacity()) / 100.0F;

            int mode = AlphaComposite.SRC_OVER;
            AlphaComposite AC = AlphaComposite.getInstance(mode, alpha);

            //g.setColor(new Color(255,255,255,0));
            //g.setXORMode(new Color(255,255,255,0));
            g.setBackground(new Color(255, 255, 255, 0));
            g.setComposite(AC);
            g.drawImage(source, null, 0, 0);
            g.dispose();

            int w, h;

            if (boxWidth == 0 && boxHeight == 0) {
                w = source.getWidth();
                h = source.getHeight();
            } else if (boxHeight == 0) {
                w = boxWidth;
                float ratio = (source.getHeight() / (source.getWidth() * 1F));
                h = (int) (ratio * boxWidth);
            } else if (boxWidth == 0) {
                float ratio = (source.getWidth() / (source.getHeight() * 1F));
                w = (int) (ratio * boxHeight);
                h = boxHeight;
            } else {
                w = boxWidth;
                h = boxHeight;
            }

            boxWidth = w;
            boxHeight = h;
        }

        if (boxWidth == 0 && style.getType() != WatermarkStyle.WatermarkType.IMAGE) {
            g2d.setFont(style.getFont());
            String[] lines = text.split("\n");
            int maxTextWidth = 0;
            for (String line : lines) {
                int textWidth = g2d.getFontMetrics().stringWidth(line) + 10;
                if (textWidth > maxTextWidth) {
                    maxTextWidth = textWidth;
                }
            }
            boxWidth = maxTextWidth;
        }

        if (boxHeight == 0 && style.getType() != WatermarkStyle.WatermarkType.IMAGE) {
            int textHeight = style.getFont().getSize();
            int textDescent = g2d.getFontMetrics().getDescent();
            String[] lines = text.split("\n");
            boxHeight = (textHeight + textDescent) * lines.length;
        }

        if (style.getHorizontalReference() == 1) {
            hor = (int) (hor + (size.getWidth() / 2 - boxWidth / 2));
        } else if (style.getHorizontalReference() == 2) {
            hor = (int) (size.getWidth() - boxWidth) - hor;
        }

        if (style.getVerticalReference() == 1) {
            ver = (int) (ver + size.getHeight() / 2 - boxHeight / 2);
        } else if (style.getVerticalReference() == 2) {
            ver = (int) (size.getHeight() - boxHeight) - ver;
        }

        g2d.rotate(angle, hor + (boxWidth / 2f), ver + (boxHeight / 2f));

        Color bgColor = style.getBackgroundColor();
        bgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), opacityValue);

        Color foregroundColor = style.getFontColor();
        foregroundColor = new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(),
            opacityValue);

        java.awt.Rectangle bounds = new java.awt.Rectangle(hor, ver, boxWidth, boxHeight);

        if (style.isBackground()) {
            g2d.setColor(bgColor);
            g2d.fill(bounds);
        }

        if (style.getType() == WatermarkStyle.WatermarkType.IMAGE) {
            g2d.drawImage(target, hor, ver, boxWidth, boxHeight, null);
        } else {
            TextRenderer.drawString(g2d, text, style.getFont(), foregroundColor, bounds, alignment, style.isUnderline(),
                style.isStrikethrough(), true);
        }

        g2d.dispose();
        } catch (Exception ex) {
            Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex108", ex);
        }
    }

    private String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ((int) 'A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }
}
