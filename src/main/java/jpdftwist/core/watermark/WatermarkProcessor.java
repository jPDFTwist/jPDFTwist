package jpdftwist.core.watermark;

import com.frequal.romannumerals.Converter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.stage.FileChooser;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageRange;
import jpdftwist.core.PdfReaderManager;
import jpdftwist.core.TempFileManager;
import jpdftwist.core.UnitTranslator;
import jpdftwist.core.input.PageDimensions;
import jpdftwist.gui.tab.watermark.WatermarkOptionsPanel;
import jpdftwist.utils.JImageParser;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.joda.time.DateTime;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

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
                Rectangle Mediasize = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
//                underContent.addTemplate(wmTemplate, 0, 0);
                underContent.addTemplate(wmTemplate, 
                		Mediasize.getWidth() / 2 - (wmTemplate.getWidth() / 2),
                		Mediasize.getHeight() / 2 - (wmTemplate.getHeight() / 2));
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
            	
				String filename = style.getVariableTextFile();
				if (br == null) {
					br = new BufferedReader(new FileReader(filename));
				}
				if (filename.toLowerCase().contains(".txt")) 
				{
					String sCurrentLine;
					if ((sCurrentLine = br.readLine()) != null) {
						text = sCurrentLine;
					} else {
						text = "";
						break; }
                }
				if (filename.toLowerCase().contains(".csv")) 
				{
					String sCurrentLine;
					if ((sCurrentLine = br.readLine()) != null) {
							String[] values = sCurrentLine.split(",");
							text = values[0];
							
//							for (int k = 0; k < values.length; k++) {		//Column number
//								text = values[k];
//							}
					} else {
						text = "";
						break; }
                }
            case IMAGE:
            	
			case VECTOR:
				if (style.getType() == WatermarkStyle.WatermarkType.VECTOR) {
					try {
						// Get Page
						Rectangle SizeV = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
//						int DocumentPageCount = pdfReaderManager.getCurrentReader().getNumberOfPages();	
						float MediaWidth = SizeV.getWidth();
						float MediaHeight = SizeV.getHeight();

//						PdfReader StampDocument = jpdftwist.utils.PdfParser.open(style.getVectorPath());
//						int StampPageCount = StampDocument.getNumberOfPages();
//						StampDocument.close();
										
//	                    JOptionPane.showMessageDialog(null, "Document Page Count  =  " + DocumentPageCount + "   |   Stamp Page Count  =  " + StampPageCount + "", "Info", JOptionPane.INFORMATION_MESSAGE);
						
//						if (!style.getVectorPath().isEmpty()) {							

							// Get Vector Stamp
							PdfImportedPage WMTemplateV = stamper.getImportedPage(jpdftwist.utils.PdfParser.open(style.getVectorPath(), false), i);

							double boxWidth; // Input in box by user
							double boxHeight; // Input in box by user
							float scaledWidthfactor;
							float scaledHeightfactor;
							double horOffset;
							double verOffset;
							double StampWidth;
							double StampHeight;

							switch (style.getUnits()) {
							case INCHES:
								boxWidth = (UnitTranslator.POINT_POSTSCRIPT * style.getWidth());
								boxHeight = (UnitTranslator.POINT_POSTSCRIPT * style.getHeight());
								horOffset = (UnitTranslator.POINT_POSTSCRIPT * style.getHorizontalPosition());
								verOffset = (UnitTranslator.POINT_POSTSCRIPT * ((MediaHeight / 72) - style.getVerticalPosition()));
								StampWidth = (WMTemplateV.getWidth());
								StampHeight = (WMTemplateV.getHeight());
								break;
							case MM:
								boxWidth = (UnitTranslator.millisToPoints(style.getWidth()));
								boxHeight = (UnitTranslator.millisToPoints(style.getHeight()));
								horOffset = (UnitTranslator.millisToPoints(style.getHorizontalPosition()));
								verOffset = (UnitTranslator.millisToPoints(((MediaHeight / 2.8346438836889) - style.getVerticalPosition())));
								StampWidth = (WMTemplateV.getWidth());
								StampHeight = (WMTemplateV.getHeight());
								break;
							default:
								boxWidth = (style.getWidth());
								boxHeight = (style.getHeight());
								horOffset = (style.getHorizontalPosition());
								verOffset = (MediaHeight - style.getVerticalPosition());
								StampWidth = (WMTemplateV.getWidth());
								StampHeight = (WMTemplateV.getHeight());
								break;
							}
//                  		JOptionPane.showMessageDialog(null, "H = " + Double.toString(horV) + " pts  |  V = " + Double.toString(verV) + " pts", "Info", JOptionPane.INFORMATION_MESSAGE);

							if (boxWidth == 0 && boxHeight == 0) {
								scaledWidthfactor = 1;
								scaledHeightfactor = 1;
							} else if (boxWidth == 0) {
								scaledWidthfactor = 1;
								scaledHeightfactor = (float) boxHeight / (float) StampHeight;
							} else if (boxHeight == 0) {
								scaledWidthfactor = (float) boxWidth / (float) StampWidth;
								scaledHeightfactor = 1;
							} else {
								scaledWidthfactor = (float) boxWidth / (float) StampWidth;
								scaledHeightfactor = (float) boxHeight / (float) StampHeight;
							}

							if (style.getHorizontalReference() == 0) {
								horOffset = horOffset + 36; // To compensate for an unexplained 0.5" horizontal shift
							} else if (style.getHorizontalReference() == 1) {
								horOffset = (horOffset) + ((MediaWidth / 2) - ((StampWidth * scaledWidthfactor) / 2));
							} else if (style.getHorizontalReference() == 2) {
								horOffset = (horOffset + (MediaWidth - (StampWidth * scaledWidthfactor))) - 36;
							}

							if (style.getVerticalReference() == 0) {
								verOffset = (verOffset - (StampHeight * scaledHeightfactor)) - 36; // To compensate for an unexplained 0.5" vertical shift
							} else if (style.getVerticalReference() == 1) {
								verOffset = (verOffset - (StampHeight * scaledHeightfactor)) - ((MediaHeight / 2) - ((StampHeight * scaledHeightfactor) / 2));
							} else if (style.getVerticalReference() == 2) {
								verOffset = ((verOffset - (StampHeight * scaledHeightfactor)) - ((MediaHeight) - ((StampHeight * scaledHeightfactor)))) + 36;
							}

//                    		JOptionPane.showMessageDialog(null, "BoxWidth = " + Float.toString((float)boxWidth) + " pts  |  BoxHeight = " + Float.toString((float)boxHeight) + " pts", "Info", JOptionPane.INFORMATION_MESSAGE);
//                    		JOptionPane.showMessageDialog(null, "StampWidth = " + Float.toString((float)stampWidth) + " pts  |  StampHeight = " + Float.toString((float)stampHeight) + " pts", "Info", JOptionPane.INFORMATION_MESSAGE);
//                    		JOptionPane.showMessageDialog(null, "ScaledWidthfactor = " + Float.toString(scaledWidthfactor) + "  |  ScaledHeightfactor = " + Float.toString(scaledHeightfactor) + " ", "Info", JOptionPane.INFORMATION_MESSAGE);

							PdfContentByte OverContentV = stamper.getOverContent(i);

							double angle = ((style.getAngle() * Math.PI) / 180);
							float alpha = (100 - style.getOpacity()) / 100.0F;
							PdfGState stateV = new PdfGState();
							stateV.setFillOpacity(alpha);
							OverContentV.setGState(stateV);

//                    		AffineTransform transformR = AffineTransform.getRotateInstance(angle);
//                    		OverContentV.addTemplate(Template2, transformR);

							OverContentV.addTemplate(WMTemplateV, scaledWidthfactor, 0, 0, scaledHeightfactor, (float) horOffset, (float) verOffset);
//						}
					} catch (Exception ex) {
						Logger.getLogger(WatermarkProcessor.class.getName()).log(Level.SEVERE, "Ex156", ex);
					}
				}
            	
            case CROPMARKS_inch:
            	if (style.getType() == WatermarkStyle.WatermarkType.CROPMARKS_inch) {
            		
                    PdfContentByte CB20 = stamper.getOverContent(i);
                    Rectangle sizeCM = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
                    
                    float width = sizeCM.getWidth();
                    float height = sizeCM.getHeight();
                    
					CB20.moveTo(width - 11, 18);
					CB20.lineTo(width, 18);
					CB20.moveTo(width - 18, height - 11);
					CB20.lineTo(width - 18, height);
					
					CB20.moveTo(18, 11);
					CB20.lineTo(18, 0);
					CB20.moveTo(11, height - 18);
					CB20.lineTo(0, height - 18);
					
					CB20.moveTo(18, height);
					CB20.lineTo(18, height - 11);
					CB20.moveTo(width, height - 18);
					CB20.lineTo(width - 11, height - 18);
					
					CB20.moveTo(width - 18, 0);
					CB20.lineTo(width - 18, 11);
					CB20.moveTo(0, 18);
					CB20.lineTo(11, 18);
                    
                    CB20.setLineWidth(style.getFontSize() / 32);
                    CB20.setColorStroke(new BaseColor(style.getFontColor()));
                    CB20.stroke();
                    
                    //  Calibration Marks Top
                    CB20.circle((0 + (width / 2) - (11 * 3)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (11 * 2)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (11)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(255, 0, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11 * 2)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(0, 0, 255);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11 * 3)), height - 3 - (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 0, 0, 255);
                    CB20.fill();
                    
                    //  Calibration Marks Bottom
                    CB20.circle((0 + (width / 2) - (11 * 3)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (11 * 2)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (11)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(255, 0, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11 * 2)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setRGBColorFill(0, 0, 255);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (11 * 3)), 0 + 3 + (11 / 2), (11 / 2));
                    CB20.setCMYKColorFill(0, 0, 0, 255);
                    CB20.fill();
            	}
            	
            case CROPMARKS_mm:
            	if (style.getType() == WatermarkStyle.WatermarkType.CROPMARKS_mm) {
            		
                    PdfContentByte CB20 = stamper.getOverContent(i);
                    Rectangle sizeCM = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
                    
                    float width = sizeCM.getWidth();
                    float height = sizeCM.getHeight();
                    
					CB20.moveTo(width - 21, 28);
					CB20.lineTo(width, 28);
					CB20.moveTo(width - 28, height - 21);
					CB20.lineTo(width - 28, height);
					
					CB20.moveTo(28, 21);
					CB20.lineTo(28, 0);
					CB20.moveTo(21, height - 28);
					CB20.lineTo(0, height - 28);
					
					CB20.moveTo(28, height);
					CB20.lineTo(28, height - 21);
					CB20.moveTo(width, height - 28);
					CB20.lineTo(width - 21, height - 28);
					
					CB20.moveTo(width - 28, 0);
					CB20.lineTo(width - 28, 21);
					CB20.moveTo(0, 28);
					CB20.lineTo(21, 28);
                    
                    CB20.setLineWidth(style.getFontSize() / 32);
                    CB20.setColorStroke(new BaseColor(style.getFontColor()));
                    CB20.stroke();
                    
                    //  Calibration Marks Top
                    CB20.circle((0 + (width / 2) - (21 * 3)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (21 * 2)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (21)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(255, 0, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21 * 2)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(0, 0, 255);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21 * 3)), height - 3 - (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 0, 0, 255);
                    CB20.fill();
                    
                    //  Calibration Marks Bottom
                    CB20.circle((0 + (width / 2) - (21 * 3)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (21 * 2)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(255, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) - (21)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(0, 255, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(255, 0, 0, 0);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21 * 2)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setRGBColorFill(0, 0, 255);
                    CB20.fill();
                    CB20.circle((0 + (width / 2) + (21 * 3)), 0 + 3 + (21 / 2), (21 / 2));
                    CB20.setCMYKColorFill(0, 0, 0, 255);
                    CB20.fill();
            	}
                
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

                    text = text.replace("{img_depth}", pageRange.getImageColorDepth());
                    
                    text = text.replace("{img_width_points}", Double.toString(width));
                    text = text.replace("{img_height_points}", Double.toString(height));
                    
                    text = text.replace("{img_width_inch}", Double.toString(width / UnitTranslator.POINT_POSTSCRIPT));
                    text = text.replace("{img_height_inch}", Double.toString(height / UnitTranslator.POINT_POSTSCRIPT));
                    
                    text = text.replace("{img_width_mm}", Double.toString(width / 2.8346438836889));
                    text = text.replace("{img_height_mm}", Double.toString(height / 2.8346438836889));
                    
                    text = text.replace("{img_width_dpi}", Integer.toString(72)); // TODO
                    text = text.replace("{img_height_dpi}", Integer.toString(72));
                } else {
                    text = text.replace("{img_[a-z]*}", "");
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
                text = text.replace("{file_size}", pageRange.getFileSize());
                text = text.replace("%c", Integer.toString(pageCount));
                
                text = text.replace("{page_width_points}", Double.toString(size.getWidth()));
                text = text.replace("{page_height_points}", Double.toString(size.getHeight()));
                
                text = text.replace("{page_width_inch}", Double.toString(size.getWidth() / 72));
                text = text.replace("{page_height_inch}", Double.toString(size.getHeight() / 72));
                
                text = text.replace("{page_width_mm}", Double.toString(size.getWidth() / 2.8346438836889));
                text = text.replace("{page_height_mm}", Double.toString(size.getHeight() / 2.8346438836889));
                
                text = text.replace("%n", Integer.toString(i));
                text = text.replace("%N", Integer.toString(logical));
                text = text.replace("{last_modified}", lastModified);

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

				// Get Raster Stamp
//				if (!style.getImagePath().isEmpty()) {
					
            		if (style.getImagePath().toLowerCase().endsWith(".pdf")) {
						PDDocument document = PDDocument.load(new File(style.getImagePath()));
						PDPageTree list = document.getPages();

						PDPage page = list.get(style.getPdfPage() - 1);
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

					// target = new BufferedImage(source.getWidth(null), source.getHeight(null),
					// java.awt.Transparency.TRANSLUCENT);

    				target = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = target.createGraphics();

					float alpha = (100 - style.getOpacity()) / 100.0F;

					int mode = AlphaComposite.SRC_OVER;
					AlphaComposite AC = AlphaComposite.getInstance(mode, alpha);

					// g.setColor(new Color(255,255,255,0));
					// g.setXORMode(new Color(255,255,255,0));
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

				if (style.getHorizontalReference() == 0) {
					hor = (int) (0 - hor) + 36; // To compensate for an unexplained 0.5" horizontal shift
				} else if (style.getHorizontalReference() == 1) {
					hor = (int) (hor + (size.getWidth() / 2 - boxWidth / 2));
				} else if (style.getHorizontalReference() == 2) {
					hor = (int) ((size.getWidth() - boxWidth) - hor) - 36;
				}

				if (style.getVerticalReference() == 0) {
					ver = (int) (0 - ver) + 36; // To compensate for an unexplained 0.5" vertical shift
				} else if (style.getVerticalReference() == 1) {
					ver = (int) (ver + size.getHeight() / 2 - boxHeight / 2);
				} else if (style.getVerticalReference() == 2) {
					ver = (int) ((size.getHeight() - boxHeight) - ver) - 36;
				}

				g2d.rotate(angle, hor + (boxWidth / 2f), ver + (boxHeight / 2f));

				Color bgColor = style.getBackgroundColor();
				bgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), opacityValue);

				Color foregroundColor = style.getFontColor();
				foregroundColor = new Color(foregroundColor.getRed(), foregroundColor.getGreen(),
						foregroundColor.getBlue(), opacityValue);

				java.awt.Rectangle bounds = new java.awt.Rectangle(hor, ver, boxWidth, boxHeight);

				if (style.isBackground()) {
					g2d.setColor(bgColor);
					g2d.fill(bounds);
				}

				if (style.getType() == WatermarkStyle.WatermarkType.IMAGE) {
					g2d.drawImage(target, hor, ver, boxWidth, boxHeight, null);
				} else {
					TextRenderer.drawString(g2d, text, style.getFont(), foregroundColor, bounds, alignment,
							style.isUnderline(), style.isStrikethrough(), true);
				}
				g2d.dispose();
//			}
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
