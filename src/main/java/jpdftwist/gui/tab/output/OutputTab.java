package jpdftwist.gui.tab.output;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import jpdftwist.core.PdfToImage;
import jpdftwist.core.tabparams.OutputParameters;
import jpdftwist.gui.component.FileChooser;

import jpdftwist.utils.ImageParser;
import jpdftwist.utils.JImageParser;

public class OutputTab extends JPanel {

    private final JTextField outputFile;
    private final JSlider qualitySlider;
    private final JCheckBox burst;
    private final JCheckBox multiPageTiffCheckBox;
    private JCheckBox transparent;
    private final JCheckBox uncompressedComboBox;
    private final JCheckBox pageMarksComboBox;
    private final JCheckBox tempfilesComboBox;
    private final JCheckBox optimizeSizeComboBox;
    private final JCheckBox fullyCompressedComboBox;
    private final JLabel colorLabel;
    private final JLabel compressionLabel;
    private final JLabel qualityLabel;
    private final JLabel warning;
    private final JComboBox<PdfToImage.ImageType> fileTypeComboBox;
    private final JComboBox<PdfToImage.ColorMode> colorMode;
    private final JComboBox<PdfToImage.TiffCompression> compressionType;
    private final JPanel splitOptionsPanel;
    private final JComboBox<String> dpiComboBox;
    private final JLabel dpiLabel;

    public OutputTab() {
        super(new FormLayout("f:p, f:p:g, f:p", "f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p:g" + ""));
        CellConstraints CC = new CellConstraints();
        warning = new JLabel("");
        
        this.add(new JLabel("Filename:"), CC.xy(1, 1));
        this.add(outputFile = new JTextField(), CC.xy(2, 1));
        outputFile.setBackground(new Color(255, 255, 255));
        
        JButton selectFile;
        this.add(selectFile = new JButton("..."), CC.xy(3, 1));
        selectFile.addActionListener(e -> {

            FileChooser fileChooser = new FileChooser();
            JFileChooser pdfChooser = fileChooser.getFileChooser();

            if (pdfChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String filename = pdfChooser.getSelectedFile().getAbsolutePath();
            if (!new File(filename).getName().contains(".")) 
            {
                filename += getFileExtensionFromOutputType();
            }
            filename = replaceFileExtension(filename);
            if (new File(filename).exists()) 
            {
                if (JOptionPane.showConfirmDialog(this, "Overwrite existing file?", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
                    return;
            }
            outputFile.setText(filename);
        });
        this.add(multiPageTiffCheckBox = new JCheckBox("Export as Tiff multipage image"), CC.xyw(1, 2, 3));
        multiPageTiffCheckBox.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) {
				findSharedLibrary();
				fileTypeComboBox.setEnabled(false);
				burst.setSelected(false);
				if(!multiPageTiffCheckBox.isSelected()){
					resetOptions(PdfToImage.ImageType.PDF);
					fileTypeComboBox.getModel().setSelectedItem(PdfToImage.ImageType.PDF);
				} 
				else
				{
					fileTypeComboBox.getModel().setSelectedItem(PdfToImage.ImageType.TIFF);
					resetOptions(PdfToImage.ImageType.TIFF);
				}
                String filename = outputFile.getText();
                filename = replaceFileExtension(filename);
                outputFile.setText(filename);
            }
        });
        this.add(burst = new JCheckBox("Split pages (use *  in file name as wildcard for page number)"), CC.xyw(1, 3, 3));
        
        burst.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setEnabled(burst.isSelected());
                splitOptionsPanel.setEnabled(burst.isSelected());

                warning.setIcon(null);
                warning.setToolTipText("");
                
                if (!burst.isSelected()) 
                {
                    onOutputTypeChanged(PdfToImage.ImageType.PDF);
                } 
                else 
                {
                    onOutputTypeChanged(fileTypeComboBox.getModel().getElementAt(fileTypeComboBox.getSelectedIndex()));

                    if (fileTypeComboBox.getSelectedIndex() != 0) 
                    {
                        findSharedLibrary();
                    }
                }
                String filename = outputFile.getText();
                filename = replaceFileExtension(filename);
                outputFile.setText(filename);
            }
        });

        this.add(new JLabel("Type:"), "1, 4, left, center");
        this.add(
            fileTypeComboBox = new JComboBox<>(new javax.swing.DefaultComboBoxModel<>(
                new PdfToImage.ImageType[]{
                  //Vector formats
                    PdfToImage.ImageType.PDF,
                  //PdfToImage.ImageType.SVG,
                  //PdfToImage.ImageType.EMF,
                  //PdfToImage.ImageType.WMF,
                  //Raster formats
                    PdfToImage.ImageType.BMP,
                    PdfToImage.ImageType.GIF,
                    PdfToImage.ImageType.IFF,
                    
                    PdfToImage.ImageType.JPG,
                    PdfToImage.ImageType.PNG,
                    
                    PdfToImage.ImageType.PAM,
                    PdfToImage.ImageType.PBM,
                    PdfToImage.ImageType.PNM,
                    PdfToImage.ImageType.PPM,
                    
                    PdfToImage.ImageType.PSD,
                    PdfToImage.ImageType.TGA,
                    
                    PdfToImage.ImageType.PICT,
                    PdfToImage.ImageType.TIFF,
                  //PdfToImage.ImageType.WEBP
                })),
            CC.xyw(2, 4, 2));
        fileTypeComboBox.setSelectedIndex(0);
        fileTypeComboBox.setMaximumRowCount(15);
        fileTypeComboBox.addItemListener(arg0 -> 
        {
            onOutputTypeChanged(fileTypeComboBox.getModel().getElementAt(fileTypeComboBox.getSelectedIndex()));

            if (fileTypeComboBox.getSelectedIndex() == 0) 
            {
                if (warning.getToolTipText().equals("<html>Images will be exported with selected Resolution")) {
                    warning.setIcon(null);
                    warning.setToolTipText("");
                }
            } else {
                findSharedLibrary();
            }
            String filename = outputFile.getText();
            filename = replaceFileExtension(filename);
            outputFile.setText(filename);

        });
        fileTypeComboBox.setEnabled(false);

        JPanel imagePanel;
        this.add(imagePanel = new JPanel(new FormLayout(
                new ColumnSpec[]{FormSpecs.PREF_COLSPEC, ColumnSpec.decode("pref:grow"),
                    ColumnSpec.decode("pref:grow"), ColumnSpec.decode("20px"), FormSpecs.PREF_COLSPEC,
                    FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("right:pref:grow"),},
                
                new RowSpec[]{RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"),
                    RowSpec.decode("24px"),})), CC.xyw(1, 5, 3));
        
        imagePanel.add(colorLabel = new JLabel("Color Mode:"), CC.xy(1, 1));
        imagePanel.add(colorMode = new JComboBox<>(), CC.xyw(2, 1, 2));
        
        colorMode.setMaximumRowCount(24);
        colorMode.addItemListener(arg0 -> 
        {
        if (multiPageTiffCheckBox.isSelected()) 
        {
        PdfToImage.ColorMode selectedColorMode = (PdfToImage.ColorMode) colorMode.getSelectedItem();
        
        //if (selectedColorMode != null) 
        //{
            switch (selectedColorMode) {
			case GRAY:{
				transparent.setEnabled(false);
				break;
			}
			case BNW:{
				transparent.setEnabled(false);
				break;
			}
			case BNWI:{
				transparent.setEnabled(false);
				break;
			}
			default:
				transparent.setEnabled(true);
				break;
			}
            //}
        }
        });
        imagePanel.add(compressionLabel = new JLabel("Compression:"), CC.xy(1, 2));
        imagePanel.add(compressionType = new JComboBox<>(), CC.xyw(2, 2, 2));
        
        compressionType.addItemListener(arg0 -> 
        {
            PdfToImage.TiffCompression selectedTiffCompression = (PdfToImage.TiffCompression) compressionType.getSelectedItem();
            
            //if (selectedTiffCompression != null) 
            //{
            	//setOptionsEnabled(colorMode.getSelectedIndex() == 0, true, false, true, true, false, false, false, false);
				switch (selectedTiffCompression) 
				{
				case JPEG: {
					setOptionsEnabled(true, true, true, true, colorMode.getSelectedIndex() == 0, false, false, false, false);
					break;
				}
				case ZLIB: {
					setOptionsEnabled(true,	true, true, true, colorMode.getSelectedIndex() == 0, false, false, false, false);
					break;
				}
				default:
					setOptionsEnabled(true,	true, false, true, colorMode.getSelectedIndex() == 0, false, false, false, false);
					break;
				}
            //}
        });
        
        imagePanel.add(qualityLabel = new JLabel("Quality:"), CC.xy(1, 3));
        qualityLabel.setToolTipText("JPEG quality (0-100%)");
        imagePanel.add(qualitySlider = new JSlider(), CC.xyw(2, 3, 2));
        qualitySlider.setValue(100);
        
        imagePanel.add(transparent = new JCheckBox("Save Transparency "), CC.xyw(1, 4, 2));
        transparent.setSelected(false);
        
        imagePanel.add(warning, CC.xy(7, 4));
        imagePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Burst Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        
        dpiLabel = new JLabel("Resolution:");
        dpiLabel.setToolTipText("Image resolution in DPI");
        imagePanel.add(dpiLabel, "5, 3, fill, default");
        
        dpiComboBox = new JComboBox<>();
        dpiComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"16", "36", "72", "100", "150", "200", "250", "280", "300", "360", "400", "600", "720", "800", "900", "1000", "1200", "1500", "1800", "2000", "2400", "2700", "3000", "3600"}));
        dpiComboBox.addItemListener(this::dpiComboBoxItemStateChanged);
        
        imagePanel.add(dpiComboBox, "7, 3, fill, default");
        dpiComboBox.setEnabled(false);
        
        this.add(new JSeparator(), CC.xyw(1, 6, 3));
        this.add(uncompressedComboBox = new JCheckBox("Save uncompressed"), CC.xyw(1, 7, 3));
        
        this.add(pageMarksComboBox = new JCheckBox("Remove PdfTk page marks"), CC.xyw(1, 8, 3));
        uncompressedComboBox.addActionListener(e -> pageMarksComboBox.setText((uncompressedComboBox.isSelected() ? "Add" : "Remove") + " PdfTk page marks"));
        
        tempfilesComboBox = new JCheckBox("Use temporary files for intermediary results (saves RAM)");
        this.add(tempfilesComboBox, CC.xyw(1, 9, 3));
        tempfilesComboBox.setEnabled(false);
        
        this.add(optimizeSizeComboBox = new JCheckBox("Optimize PDF size (will need a lot of RAM)"), CC.xyw(1, 10, 3));
        this.add(fullyCompressedComboBox = new JCheckBox("Use better compression (Acrobat 6.0+)"), CC.xyw(1, 11, 3));
        
        JLabel outputFilenameVariablesLabel = new JLabel("<html>You can use the following variables in the output filename:<br>"
            + "<tt>&lt;F></tt>: Input filename without extension<br>"
            + "<tt>&lt;FX></tt>: Input filename with extension<br>"
            + "<tt>&lt;P></tt>: Input file path without filename<br>"
            + "<tt>&lt;T></tt>: Create output dir tree<br>"
            + "<tt>&lt;#></tt>: Next free number (where file does not exist)<br>"
            + "<tt>&lt;*></tt>: Page number (for bursting pages)<br>"
            + "<tt>&lt;$></tt>: Test function");
        outputFilenameVariablesLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        this.add(outputFilenameVariablesLabel, "1, 13, 3, 1, left, top");
        splitOptionsPanel = new SplitOptionsPanel();
        
        add(splitOptionsPanel, "1, 12, 3, 1");
        onOutputTypeChanged(PdfToImage.ImageType.PDF);
    }

    public void setTempFileListener(ItemListener itemListener) {
        tempfilesComboBox.addItemListener(itemListener);
    }

    public boolean isTempFilesComboBoxSelected() {
        return tempfilesComboBox.isSelected();
    }

    private void findSharedLibrary() {
        try 
        {
            PdfToImage.setJavaLibraryPath();
        } 
        catch (NoSuchFieldException | IllegalAccessException ex) 
        {
        	Logger.getLogger(OutputTab.class.getName()).log(Level.SEVERE, "Ex062", ex);
            //ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
        String sharedLibraryName = PdfToImage.checkForLibraries();
        
        if (sharedLibraryName != null) {

            if (sharedLibraryName.equals("nojmupdf")) 
            {
                warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
                warning.setToolTipText("<html>This feature is not available<br>"
                    + "in compact version. If you are not<br>" + "using compact version verify that<br>"
                    + "lib/JmuPdf.jar is present and your<br>" + "download was not corrupted.");
                
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setSelectedItem(PdfToImage.ImageType.PDF);
                fileTypeComboBox.setSelectedIndex(0);

            } 
            else if (sharedLibraryName.contains(".")) 
            {
                warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
                warning.setToolTipText("<html>\"" + sharedLibraryName + "\" needs to be in <br>\""
                    + PdfToImage.getJarFolder() + "\"<br>" + "to export in image file type");
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setSelectedItem(PdfToImage.ImageType.PDF);
                fileTypeComboBox.setSelectedIndex(0);
            }

        } else {
            warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info.png")));
            warning.setToolTipText("<html>Images will be exported with selected Resolution");
        }
    }

    private String replaceFileExtension(String filepath) {
        // Prevent changing the extension to a filepath that does not have an extension
        if (filepath.lastIndexOf('.') == -1) {
            return filepath;
        }
        return filepath.substring(0, filepath.lastIndexOf('.')) + getFileExtensionFromOutputType();
    }

    private String getFileExtensionFromOutputType() 
    {
        if (burst.isSelected()) 
		{
            return "." + fileTypeComboBox.getModel().getElementAt(fileTypeComboBox.getSelectedIndex()).toString().toLowerCase();
        }
        if (multiPageTiffCheckBox.isSelected()) 
		{
            return ".tiff";
        }
        return ".pdf";
    }

    private void onOutputTypeChanged(PdfToImage.ImageType newOutputType) {
        resetColorModeModel(newOutputType);
        resetCompressionType(newOutputType);
        resetOptions(newOutputType);
    }

    private void resetColorModeModel(PdfToImage.ImageType outputType) 
    {
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> emptyModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]{});
        
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> grayAndRGBModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]
            {
            		PdfToImage.ColorMode.RGB,
            		PdfToImage.ColorMode.GRAY
            		});
        
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> bmpModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]
            {
            		PdfToImage.ColorMode.RGB, 
            		PdfToImage.ColorMode.GRAY, 
            		PdfToImage.ColorMode.BNW, 
            		PdfToImage.ColorMode.BNWI
            		});

//        if (outputType.equals(PdfToImage.ImageType.PAM) ||
//            outputType.equals(PdfToImage.ImageType.PNM) ||
//            outputType.equals(PdfToImage.ImageType.PPM) ||
//            outputType.equals(PdfToImage.ImageType.GIF) ||
//            outputType.equals(PdfToImage.ImageType.JPG) ||
//            outputType.equals(PdfToImage.ImageType.PNG)) 
//        {
//            colorMode.setModel(grayAndRGBModel);
//        } 
//        else if (
          if (
        		 outputType.equals(PdfToImage.ImageType.BMP) ||
                 outputType.equals(PdfToImage.ImageType.GIF) ||
                 outputType.equals(PdfToImage.ImageType.IFF) ||
                 
                 outputType.equals(PdfToImage.ImageType.JPG) ||
                 outputType.equals(PdfToImage.ImageType.PNG) ||
                 
        	     outputType.equals(PdfToImage.ImageType.PAM) ||
        	     outputType.equals(PdfToImage.ImageType.PBM) ||
                 outputType.equals(PdfToImage.ImageType.PNM) ||
                 outputType.equals(PdfToImage.ImageType.PPM) ||

        		 outputType.equals(PdfToImage.ImageType.PSD) ||
        		 outputType.equals(PdfToImage.ImageType.TGA) ||
        		 
        	     outputType.equals(PdfToImage.ImageType.PICT)||
        	     outputType.equals(PdfToImage.ImageType.TIFF))
        {
            colorMode.setModel(bmpModel);
        } 
        else 
        {
            colorMode.setModel(emptyModel);
        }
    }

    private void resetCompressionType(PdfToImage.ImageType outputType) 
    {
        javax.swing.DefaultComboBoxModel<PdfToImage.TiffCompression> emptyCompressionModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]{});
        
        javax.swing.DefaultComboBoxModel<PdfToImage.TiffCompression> tiffCompressionModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]{
            PdfToImage.TiffCompression.NONE,
            PdfToImage.TiffCompression.LZW,
            PdfToImage.TiffCompression.JPEG,
            PdfToImage.TiffCompression.ZLIB,
            PdfToImage.TiffCompression.PACKBITS,
            PdfToImage.TiffCompression.DEFLATE,
            PdfToImage.TiffCompression.RLE
        });
        
//        if (outputType.equals(PdfToImage.ImageType.PNG))
//        {
//            compressionType.setModel(zipCompressionModel);  
//        } 
        
        if (outputType.equals(PdfToImage.ImageType.TIFF))
        {
            compressionType.setModel(tiffCompressionModel); 
        } 
        else 
        {
            compressionType.setModel(emptyCompressionModel);
        }
    }

    private void resetOptions(PdfToImage.ImageType outputType) 
    {
		switch (outputType) 
		{
		case PDF:
			setOptionsEnabled(false, false, false, false, false, true, true, true, true);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case BMP:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case GIF:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case IFF:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;

		case JPG:
			setOptionsEnabled(true, false, true, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case PNG:
			setOptionsEnabled(true, false, false, true, true, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;	
		
		case PAM:
			setOptionsEnabled(true, false, false, true, true, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case PBM:
			setOptionsEnabled(false, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
		
		case PNM:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case PPM:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case PSD:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;	
			
		case TGA:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		case PICT:
			setOptionsEnabled(true, false, false, true, false, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;

		case TIFF:
			setOptionsEnabled(true, true, false, true, true, false, false, false, false);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
			
		default:
			setOptionsEnabled(false, false, false, false, false, true, true, true, true);
			//dpiComboBox.setEnabled(false);
			dpiComboBox.setSelectedIndex(10);
			break;
		}
    }

    private void setOptionsEnabled(boolean color, boolean compression, boolean quality, boolean dpi, boolean transparency,
                                   boolean uncompressed, boolean pageMarks, boolean optimizeSize, boolean fullyCompressed) {
        colorLabel.setEnabled(color);
        colorMode.setEnabled(color);

        //dpiComboBox.setEnabled(dpi);
        dpiLabel.setEnabled(dpi);

        compressionLabel.setEnabled(compression);
        compressionType.setEnabled(compression);

        qualityLabel.setEnabled(quality);
        qualitySlider.setEnabled(quality);

        transparent.setEnabled(transparency);

        uncompressedComboBox.setEnabled(uncompressed);
        pageMarksComboBox.setEnabled(pageMarks);
        optimizeSizeComboBox.setEnabled(optimizeSize);
        fullyCompressedComboBox.setEnabled(fullyCompressed);
    }

    private boolean matchTransparency(boolean transparency) 
    {
        if (transparent.isEnabled()) 
        {
            return transparency;
        } 
        else 
        {
            return false;
        }
    }

    public String getTabName() {
        return "Output";
    }
    
    public void checkRun() throws IOException {
    }
    
    public void dpiComboBoxItemStateChanged(java.awt.event.ItemEvent evt) 
    {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
//    		JOptionPane.showMessageDialog(null, dpiComboBox.getItemAt(dpiComboBox.getSelectedIndex()).toString(), " Note", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public String DPIvalue() {
//    	String DPI = dpiComboBox.getModel().getElementAt(dpiComboBox.getSelectedIndex()).toString();
    	String DPI = dpiComboBox.getItemAt(dpiComboBox.getSelectedIndex()).toString();
    	return DPI;
    }

    public OutputParameters getParameters() 
    {
        boolean matchedTransparency = matchTransparency(transparent.isSelected());
        boolean burstImages = (fileTypeComboBox.getSelectedIndex() != 0 && !multiPageTiffCheckBox.isSelected());
        
        PdfToImage.ImageType type = (PdfToImage.ImageType) fileTypeComboBox.getSelectedItem();
        if (multiPageTiffCheckBox.isSelected()) 
        {
            type = PdfToImage.ImageType.TIFF;
        }
        PdfToImage.ColorMode selectedColorMode = colorMode.getModel().getElementAt(colorMode.getSelectedIndex());
        PdfToImage.TiffCompression selectedTiffCompression = compressionType.getModel().getElementAt(compressionType.getSelectedIndex());

        return new OutputParameters(
            outputFile.getText(),
            multiPageTiffCheckBox.isSelected(),
            burst.isSelected(),
            uncompressedComboBox.isSelected(),
            optimizeSizeComboBox.isSelected(),
            fullyCompressedComboBox.isSelected(),
            pageMarksComboBox.isSelected(),
            new PdfToImage(burstImages, selectedColorMode, type, selectedTiffCompression, qualitySlider.getValue(), matchedTransparency));
    }
}
