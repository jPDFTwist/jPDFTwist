package jpdftwist.tabs;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PdfToImage;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.tabs.input.FileChooser;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;


public class OutputTab extends Tab {
	
	private JTextField outputFile;
	private JSlider qualitySlider;
	private JCheckBox burst, multipageTiff, transparent, uncompressed, pageMarks, tempfiles, optimizeSize, fullyCompressed;
	private JLabel colorLabel, compressionLabel, qualityLabel, warning;
	private JComboBox fileType, colorMode, compressionType;
	private JPanel imagePanel;
	private final MainWindow mainWindow;
	private String currentExtension = ".pdf";
	private JPanel panel;
	private JRadioButton rdbtnSplitByPage;
	private JRadioButton RadioButton_1;
	private JLabel label_24;
	private JLabel label_25;
	private JRadioButton RadioButton_2;
	private JRadioButton RadioButton_3;
	private JRadioButton rdbtnSplitByChunk;
	private JRadioButton rdbtnSplitBySize;
	private JRadioButton RadioButton_6;
	private JRadioButton RadioButton_7;
	private JLabel label_7;
	private JLabel label_10;
	private JLabel label_12;
	private JComboBox textField_DPI;
	private JLabel label_DPI;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JComboBox textField_8;
	
	
	public OutputTab (MainWindow mf) {
		super(new FormLayout("f:p, f:p:g, f:p", "f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p:g" + ""));
		this.mainWindow = mf;
		CellConstraints CC = new CellConstraints();
		this.add(new JLabel("Filename:"), CC.xy(1, 1));
		this.add(outputFile = new JTextField(), CC.xy(2, 1));
		outputFile.setBackground(new Color(255, 255, 255));
		JButton selectFile;
		this.add(selectFile = new JButton("..."), CC.xy(3, 1));
		selectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				FileChooser fileChooser = new FileChooser();
				JFileChooser pdfChooser = fileChooser.getFileChooser();
				
				if (pdfChooser.showSaveDialog(mainWindow) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				String filename = pdfChooser.getSelectedFile().getAbsolutePath();
				if (!new File(filename).getName().contains(".")) {
					filename += currentExtension;
				}
				filename = setCorrectExtension(filename);
				if (new File(filename).exists()) {
					if (JOptionPane.showConfirmDialog(mainWindow, "Overwrite existing file?", "Confirm Overwrite",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
						return;
				}
				outputFile.setText(filename);
			}
		});
		this.add(multipageTiff = new JCheckBox("Export as Tiff multipage image"), CC.xyw(1, 2, 3));
		multipageTiff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findSharedLibrary();
				fileType.setEnabled(false);
				burst.setSelected(false);
				if (!multipageTiff.isSelected()) {
					whichToEnable(0);
				} else {
					whichToEnable(100);
				}
				String filename = outputFile.getText();
				filename = setCorrectExtension(filename);
				outputFile.setText(filename);
			}
		});
		this.add(burst = new JCheckBox("Split pages (use *  in file name as wildcard for page number)"),
				CC.xyw(1, 3, 3));
		burst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				multipageTiff.setSelected(false);
				fileType.setEnabled(burst.isSelected());

				RadioButton_1.setEnabled(burst.isSelected());
				RadioButton_2.setEnabled(burst.isSelected());
				RadioButton_3.setEnabled(burst.isSelected());
				rdbtnSplitByChunk.setEnabled(burst.isSelected());
				rdbtnSplitByPage.setEnabled(burst.isSelected());
				RadioButton_6.setEnabled(burst.isSelected());
				RadioButton_7.setEnabled(burst.isSelected());
				rdbtnSplitBySize.setEnabled(burst.isSelected());
				//RadioButton_9.setEnabled(burst.isSelected());
				//RadioButton_10.setEnabled(burst.isSelected());

				textField_3.setEnabled(burst.isSelected());
				textField_4.setEnabled(burst.isSelected());
				textField_5.setEnabled(burst.isSelected());
				textField_6.setEnabled(burst.isSelected());
				textField_7.setEnabled(burst.isSelected());
				textField_8.setEnabled(burst.isSelected());
				//textField_9.setEnabled(burst.isSelected());
				//textField_10.setEnabled(burst.isSelected());

				warning.setIcon(null);
				warning.setToolTipText("");
				if (!burst.isSelected()) {

					whichToEnable(0);
					setOptionsEnabled(true,	false,	false,	false,	true,	true,	true,	true,	true,	true);
				} else {
					whichToEnable(fileType.getSelectedIndex());
					
					if (fileType.getSelectedIndex() != 0) {
						findSharedLibrary();
					}
				}
				String filename = outputFile.getText();
				filename = setCorrectExtension(filename);
				outputFile.setText(filename);
			}
		});

		this.add(new JLabel("Type:"), "1, 4, left, center");
		this.add(
				fileType = new JComboBox(new javax.swing.DefaultComboBoxModel(
						new PdfToImage.ImageType[] { 
//								Vector formats
								PdfToImage.ImageType.PDF, 
								PdfToImage.ImageType.PSD,
								PdfToImage.ImageType.SVG,
								PdfToImage.ImageType.EMF,
								PdfToImage.ImageType.WMF,
								
//								Raster formats								
								PdfToImage.ImageType.JPG, 
								PdfToImage.ImageType.JP2,								
								PdfToImage.ImageType.PNG,
								PdfToImage.ImageType.PAM, 
								PdfToImage.ImageType.PNM, 
								PdfToImage.ImageType.BMP,
								PdfToImage.ImageType.GIF,
								PdfToImage.ImageType.PCX,
								PdfToImage.ImageType.TGA, 
								PdfToImage.ImageType.TIFF
								})),
				CC.xyw(2, 4, 2));
		fileType.setSelectedIndex(0);
		fileType.setMaximumRowCount(15);
		fileType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				whichToEnable(fileType.getSelectedIndex());

				if (fileType.getSelectedIndex() == 0) {
					setOptionsEnabled(true,	false,	false,	false,	true,	true,	true,	true,	true,	true);
									
					if (warning.getToolTipText().equals("<html>Images will be exported with selected Resolution")) {
						warning.setIcon(null);
						warning.setToolTipText("");
					}
				} else {
					findSharedLibrary();
				}
				String filename = outputFile.getText();
				filename = setCorrectExtension(filename);
				outputFile.setText(filename);

			}
		});
		fileType.setEnabled(false);

		this.add(imagePanel = new JPanel(new FormLayout(
				new ColumnSpec[] { FormSpecs.PREF_COLSPEC, ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("20px"), FormSpecs.PREF_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("right:pref:grow"), },
				new RowSpec[] { RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"),
						RowSpec.decode("24px"), })),
				CC.xyw(1, 5, 3));
		imagePanel.add(colorLabel = new JLabel("Color Mode:"), CC.xy(1, 1));
		imagePanel.add(
				colorMode = new JComboBox(
						new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
								{ 
										
								})),
				
				CC.xyw(2, 1, 2));
		colorMode.setMaximumRowCount(24);
		colorMode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {

				if (fileType.getSelectedIndex() == 14 || multipageTiff.isSelected()) {

					PdfToImage.ColorMode selectedColorMode = (PdfToImage.ColorMode) colorMode.getSelectedItem();
					switch (selectedColorMode) {
					case GRAY: {
						transparent.setEnabled(false);
						break;
					}
					case BNW: {
						transparent.setEnabled(false);
						break;
					}
					case BNWI: {
						transparent.setEnabled(false);
						break;
					}
					default:
						transparent.setEnabled(true);
						break;
					}
				}
			}
		});
		imagePanel.add(compressionLabel = new JLabel("Compression:"), CC.xy(1, 2));
		imagePanel.add(compressionType = new JComboBox(new javax.swing.DefaultComboBoxModel(
				new PdfToImage.TiffCompression[] 
						{ 
								
						})),
				
				CC.xyw(2, 2, 2));
		compressionType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				PdfToImage.TiffCompression selectedTiffCompression = (PdfToImage.TiffCompression) compressionType.getSelectedItem();
				switch (selectedTiffCompression) {
				case JPEG: {
					setOptionsEnabled(colorMode.getSelectedIndex() == 0,	true,	true,	true,	true,	true,	false,	true,	false,	false);
					break;
				}
				case ZLIB: {
					setOptionsEnabled(colorMode.getSelectedIndex() == 0,	true,	true,	true,	true,	true,	false,	true,	false,	false);
					break;
				}
				default:
					setOptionsEnabled(colorMode.getSelectedIndex() == 0,	true,	true,	true,	true,	true,	false,	true,	false,	false);
					break;
				}
			}
		});
		imagePanel.add(qualityLabel = new JLabel("Quality:"), CC.xy(1, 3));
		qualityLabel.setToolTipText("JPEG quality (0-100%)");
		imagePanel.add(qualitySlider = new JSlider(), CC.xyw(2, 3, 2));
		qualitySlider.setValue(90);
		imagePanel.add(transparent = new JCheckBox("Save Transparency "), CC.xyw(1, 4, 2));
		transparent.setSelected(true);
		imagePanel.add(warning = new JLabel(""), CC.xy(7, 4));
		imagePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Burst Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		imagePanel.add(getLabel_DPI(), "5, 3, fill, default");
		imagePanel.add(getTextField_DPI(), "7, 3, fill, default");
		this.add(new JSeparator(), CC.xyw(1, 6, 3));
		this.add(uncompressed = new JCheckBox("Save uncompressed"), CC.xyw(1, 7, 3));
		this.add(pageMarks = new JCheckBox("Remove PdfTk page marks"), CC.xyw(1, 8, 3));
		uncompressed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageMarks.setText((uncompressed.isSelected() ? "Add" : "Remove") + " PdfTk page marks");
			}
		});
		tempfiles = new JCheckBox("Use temporary files for intermediary results (saves RAM)");
		tempfiles.addItemListener(new ItemListener() {

			
			public void itemStateChanged(ItemEvent e) {
				mainWindow.getInputTab().setUseTempFiles(tempfiles.isSelected());
			}
		});
		this.add(tempfiles, CC.xyw(1, 9, 3));
		this.add(optimizeSize = new JCheckBox("Optimize PDF size (will need a lot of RAM)"), CC.xyw(1, 10, 3));
		this.add(fullyCompressed = new JCheckBox("Use better compression (Acrobat 6.0+)"), CC.xyw(1, 11, 3));
		JLabel label_1 = new JLabel("<html>You can use the following variables in the output filename:<br>"
				+ "<tt>&lt;F></tt>: Input filename without extension<br>"
				+ "<tt>&lt;FX></tt>: Input filename with extension<br>"
				+ "<tt>&lt;P></tt>: Input file path without filename<br>"
				+ "<tt>&lt;T></tt>: Create output dir tree<br>"
				+ "<tt>&lt;#></tt>: Next free number (where file does not exist)<br>"
				+ "<tt>&lt;*></tt>: Page number (for bursting pages)<br>"
				+ "<tt>&lt;$></tt>: Test function");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.add(label_1, "1, 13, 3, 1, left, top");
		add(getPanel_1(), "1, 12, 3, 1");
		setOptionsEnabled(true,	false,	false,	false,	true,	true,	true,	true,	true,	true);
	}

	private void findSharedLibrary() {
		try {
			PdfToImage.setJavaLibraryPath();
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
		}
		String sharedLibraryName = PdfToImage.checkForLibraries();
		if (sharedLibraryName != null) {
			
			if (sharedLibraryName.equals("nojmupdf")) {
				warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
				warning.setToolTipText("<html>This feature is not available<br>"
						+ "in compact version. If you are not<br>" + "using compact version verify that<br>"
						+ "lib/JmuPdf.jar is present and your<br>" + "download was not corrupted.");
				multipageTiff.setSelected(false);
				fileType.setSelectedItem(PdfToImage.ImageType.PDF);
				fileType.setSelectedIndex(0);
			
			} else if (sharedLibraryName.contains(".")) {
				warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
				warning.setToolTipText("<html>\"" + sharedLibraryName + "\" needs to be in <br>\""
						+ PdfToImage.getJarFolder() + "\"<br>" + "to export in image file type");
				multipageTiff.setSelected(false);
				fileType.setSelectedItem(PdfToImage.ImageType.PDF);
				fileType.setSelectedIndex(0);
			}
		
		} else {
			warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info.png")));
			warning.setToolTipText("<html>Images will be exported with selected Resolution");
		}
	}

	private String setCorrectExtension(String filename) {
		boolean changeExtension = filename.endsWith(currentExtension);
		if (changeExtension) {
			filename = filename.substring(0, filename.length() - currentExtension.length());
		}
		if (burst.isSelected() || multipageTiff.isSelected()) {
			if (fileType.getSelectedIndex() == 14 || multipageTiff.isSelected()) {

				currentExtension = ".tiff";
			} else {
				currentExtension = "." + fileType.getSelectedItem().toString().toLowerCase();
			}
		} else {
			currentExtension = ".pdf";
		}
		if (changeExtension) {
			filename += currentExtension;
		}
		return filename;
	}

	private void whichToEnable(int option) {

//		Selecting Color Modes

		
		//		PDF format
		if (option == 0 )
		{
			javax.swing.DefaultComboBoxModel MODE0 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE0);
			}
			
		//			PSD format		
		} else if (option == 1){
			javax.swing.DefaultComboBoxModel MODE1 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE1);
			}
			
		//			SVG format		
		} else if (option == 2){
			javax.swing.DefaultComboBoxModel MODE2 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE2);
			}

		//			EMF format		
		} else if (option == 3){
			javax.swing.DefaultComboBoxModel MODE3 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE3);
			}
			
		//			WMF format
		} else if (option == 4){
			javax.swing.DefaultComboBoxModel MODE4 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE4);
			}
			
		//			JPG format	
		} else if (option == 5){
			javax.swing.DefaultComboBoxModel MODE5 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE5);
			}
			
		//			JP2 format			
		} else if (option == 6){
			javax.swing.DefaultComboBoxModel MODE6 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE6);
			}
			
		//			PNG format		
		} else if (option == 7){
			javax.swing.DefaultComboBoxModel MODE7 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE7);
			}
			
		//			PAM format	
		} else if (option == 8){
			javax.swing.DefaultComboBoxModel MODE8 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ PdfToImage.ColorMode.GRAY, 
							PdfToImage.ColorMode.RGB });
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE8);
			}
			
		//			PNM format		
		} else if (option == 9){
			javax.swing.DefaultComboBoxModel MODE9 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ PdfToImage.ColorMode.GRAY, 
							PdfToImage.ColorMode.RGB });
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE9);
			}
			
		//			BMP format	
		} else if (option == 10){
			javax.swing.DefaultComboBoxModel MODE10 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ PdfToImage.ColorMode.BNW, 
						PdfToImage.ColorMode.BNWI,
							PdfToImage.ColorMode.GRAY, 
							PdfToImage.ColorMode.RGB });
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE10);
			}
			
		//			GIF format	
		} else if (option == 11){
			javax.swing.DefaultComboBoxModel MODE11 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ PdfToImage.ColorMode.GRAY, 
							PdfToImage.ColorMode.RGB });
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE11);
			}
			
		//			PCX format	
		} else if (option == 12){
			javax.swing.DefaultComboBoxModel MODE12 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE12);
			}
			
		//			TGA format		
		} else if (option == 13){
			javax.swing.DefaultComboBoxModel MODE13 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{ 
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE13);
			}
			
		//			TIFF format	
		} else if (option == 14){
			javax.swing.DefaultComboBoxModel MODE14 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
					{
							
					});
			if(colorMode.getModel().getSize() != 64){
				colorMode.setModel(MODE14);
			}
			
			//			multiTIFF format	
			} else if (option == 100){
				javax.swing.DefaultComboBoxModel MODE100 = new javax.swing.DefaultComboBoxModel(new PdfToImage.ColorMode[] 
						{
								
						});
				if(colorMode.getModel().getSize() != 64){
					colorMode.setModel(MODE100);
				}
			
		}

		
//		Selecting Compression Types	
		
		
		//		PDF format
		if (option == 0 )
		{
			javax.swing.DefaultComboBoxModel MODE0 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE0);
			}
			
		//			PSD format		
		} else if (option == 1){
			javax.swing.DefaultComboBoxModel MODE1 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE1);
			}
			
		//			SVG format		
		} else if (option == 2){
			javax.swing.DefaultComboBoxModel MODE2 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE2);
			}

		//			EMF format		
		} else if (option == 3){
			javax.swing.DefaultComboBoxModel MODE3 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE3);
			}
			
		//			WMF format
		} else if (option == 4){
			javax.swing.DefaultComboBoxModel MODE4 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE4);
			}
			
		//			JPG format	
		} else if (option == 5){
			javax.swing.DefaultComboBoxModel MODE5 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE5);
			}
			
		//			JP2 format			
		} else if (option == 6){
			javax.swing.DefaultComboBoxModel MODE6 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE6);
			}
			
		//			PNG format		
		} else if (option == 7){
			javax.swing.DefaultComboBoxModel MODE7 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					
					// *** Replace with PNG Compression ***					
					{ PdfToImage.TiffCompression.NONE
							});		
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE7);
			}
			
		//			PAM format	
		} else if (option == 8){
			javax.swing.DefaultComboBoxModel MODE8 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
					
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE8);
			}
			
		//			PNM format		
		} else if (option == 9){
			javax.swing.DefaultComboBoxModel MODE9 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
					
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE9);
			}
			
		//			BMP format	
		} else if (option == 10){
			javax.swing.DefaultComboBoxModel MODE10 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
					
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE10);
			}
			
		//			GIF format	
		} else if (option == 11){
			javax.swing.DefaultComboBoxModel MODE11 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
					
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE11);
			}
			
		//			PCX format	
		} else if (option == 12){
			javax.swing.DefaultComboBoxModel MODE12 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ 
							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE12);
			}
			
		//			TGA format		
		} else if (option == 13){
			javax.swing.DefaultComboBoxModel MODE13 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					
					// *** Replace with TGA Compression ***						
					{ PdfToImage.TiffCompression.NONE
							});			
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE13);
			}
			
		//			TIFF format	
		} else if (option == 14){
			javax.swing.DefaultComboBoxModel MODE14 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
					{ PdfToImage.TiffCompression.NONE, 
							PdfToImage.TiffCompression.LZW,
							PdfToImage.TiffCompression.JPEG, 
							PdfToImage.TiffCompression.ZLIB,
							PdfToImage.TiffCompression.PACKBITS, 
							PdfToImage.TiffCompression.DEFLATE, 
							PdfToImage.TiffCompression.RLE							
					});
			if(compressionType.getModel().getSize() != 64){
				compressionType.setModel(MODE14);
			}
			
			//			multiTIFF format	
			} else if (option == 100){
				javax.swing.DefaultComboBoxModel MODE100 = new javax.swing.DefaultComboBoxModel(new PdfToImage.TiffCompression[] 
						{ PdfToImage.TiffCompression.NONE, 
								PdfToImage.TiffCompression.LZW,
								PdfToImage.TiffCompression.JPEG, 
								PdfToImage.TiffCompression.ZLIB,
								PdfToImage.TiffCompression.PACKBITS, 
								PdfToImage.TiffCompression.DEFLATE, 
								PdfToImage.TiffCompression.RLE							
						});
				if(compressionType.getModel().getSize() != 64){
					compressionType.setModel(MODE100);
				}
			
		}

	
		switch (option) {

//		PDF format
		case 0: {
			setOptionsEnabled(true,	false,	false,	false,	true,	true,	true,	true,	true,	true);
			break;
		}
//		PSD format		
		case 1: {
			setOptionsEnabled(true,	false,	false,	false,	true,	false,	false,	true,	false,	false);
			break;
		}
//		SVG format			
		case 2: {
			setOptionsEnabled(true,	false,	false,	false,	true,	false,	false,	true,	false,	false);
			break;
		}
//		EMF format			
		case 3: {
			setOptionsEnabled(false,	false,	false,	false,	false,	false,	false,	true,	false,	false);
			break;
		}

//		WMF format			
		case 4: {
			setOptionsEnabled(false,	false,	false,	false,	false,	false,	false,	true,	false,	false);
			break;
		}

//		JPG format			
		case 5: {
			setOptionsEnabled(true,	false,	true,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		JP2 format			
		case 6: {
			setOptionsEnabled(true,	false,	true,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		PNG format			
		case 7: {
			setOptionsEnabled(true,	true,	true,	true,	true,	false,	false,	true,	false,	false);
			break;
		}

//		PAM format			
		case 8: {
			setOptionsEnabled(true,	false,	false,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		PNM format			
		case 9: {
			setOptionsEnabled(true,	false,	false,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		BMP format			
		case 10: {
			setOptionsEnabled(true,	false,	false,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		GIF format			
		case 11: {
			setOptionsEnabled(true,	false,	false,	true,	true,	false,	false,	true,	false,	false);
			break;
		}

//		PCX format			
		case 12: {
			setOptionsEnabled(true,	false,	false,	true,	false,	false,	false,	true,	false,	false);
			break;
		}

//		TGA format			
		case 13: {
			setOptionsEnabled(true,	true,	true,	true,	true,	false,	false,	true,	false,	false);
			break;
		}

//		TIFF format			
		case 14: {
			setOptionsEnabled(true,	true,	true,	true,	true,	false,	false,	true,	false,	false);
			break;
		}
//		multipageTiff enabled		
		case 100: {
			setOptionsEnabled(true,	true,	true,	true,	true,	false,	false,	true,	false,	false);
			break;
		}
		
		default:
			setOptionsEnabled(true,	false,	false,	false,	true,	true,	true,	true,	true,	true);
			break;
		}
			}


	private void setOptionsEnabled(boolean color, boolean compression, boolean quality, boolean dpi, boolean transparency, 
			boolean pdfOptions_1, boolean pdfOptions_2, boolean pdfOptions_3, boolean pdfOptions_4, boolean pdfOptions_5) {
		
		colorLabel.setEnabled(color);
		colorMode.setEnabled(color);
		
		textField_DPI.setEnabled(dpi);
		label_DPI.setEnabled(dpi);
		
		compressionLabel.setEnabled(compression);		
		compressionType.setEnabled(compression);
		
		qualityLabel.setEnabled(quality);
		qualitySlider.setEnabled(quality);
		
		transparent.setEnabled(transparency);
		
		uncompressed.setEnabled(pdfOptions_1);
		pageMarks.setEnabled(pdfOptions_2);
		tempfiles.setEnabled(pdfOptions_3);
		optimizeSize.setEnabled(pdfOptions_4);
		fullyCompressed.setEnabled(pdfOptions_5);

	}

	private boolean matchTransparency(boolean transparency) {
		if (transparent.isEnabled()) {
			return transparency;
		} else {
			return false;
		}
	}

	
	public String getTabName() {
		return "Output";
	}

	
	public void checkRun() throws IOException {
		if (outputFile.getText().length() == 0)
			throw new IOException("No output file selected");
		String outputFileName = outputFile.getText();
		if (mainWindow.getInputTab().getBatchLength() > 1) {
			if (!outputFileName.contains("<F>") && !outputFileName.contains("<FX>") && !outputFileName.contains("<P>")
					&& !outputFileName.contains("<#>")) {
				throw new IOException("Variables in output file name required for batch mode");
			}
		}
		mainWindow.getInputTab().setUseTempFiles(tempfiles.isSelected());
	}

	
	public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
		outDialog.updateJPDFTwistProgress(getTabName());
		outDialog.setAction("Producing output file(s)");
		if (pageMarks.isSelected()) {
			if (uncompressed.isSelected()) {
				pdfTwist.addPageMarks();
			} else {
				pdfTwist.removePageMarks();
			}
		}
		boolean matchedTransparency = matchTransparency(transparent.isSelected());

		boolean burstImages = (fileType.getSelectedIndex() != 0 && !multipageTiff.isSelected());
		PdfToImage.ImageType type = (PdfToImage.ImageType) fileType.getSelectedItem();
		if (multipageTiff.isSelected()) {
			type = PdfToImage.ImageType.TIFF;
		}
		pdfTwist.setPdfImages(new PdfToImage(burstImages, (PdfToImage.ColorMode) colorMode.getSelectedItem(), type,
				(PdfToImage.TiffCompression) compressionType.getSelectedItem(), qualitySlider.getValue(),
				matchedTransparency));
		pdfTwist.writeOutput(outputFile.getText(), multipageTiff.isSelected(), burst.isSelected(),
				uncompressed.isSelected(), optimizeSize.isSelected(), fullyCompressed.isSelected(), outDialog);
		return null;
	}

	private JPanel getPanel_1() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Split Options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			panel.setLayout(new FormLayout(
					new ColumnSpec[] { ColumnSpec.decode("max(160px;pref)"), ColumnSpec.decode("max(200px;pref):grow"),
							ColumnSpec.decode("15px"), ColumnSpec.decode("max(160px;pref)"),
							ColumnSpec.decode("max(200px;pref):grow"), ColumnSpec.decode("15px"), },
					new RowSpec[] { RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"),
							RowSpec.decode("23px"), RowSpec.decode("23px"), }));
			panel.add(getRadioButton_1(), "1, 1, left, center");
			panel.add(getRdbtnNewRadioButton_1_1_1(), "4, 1, left, center");
			panel.add(getTextField_6(), "5, 1, fill, default");
			panel.add(getRdbtnNewRadioButton_2_1_1(), "4, 2, left, center");
			panel.add(getTextField_4_1(), "5, 2, fill, default");
			panel.add(getTextField_3(), "2, 3, fill, default");
			panel.add(getRdbtnSplitBySize_1(), "4, 3, left, center");
			panel.add(getRdbtnNewRadioButton_1_2(), "1, 2, left, center");
			panel.add(getRdbtnNewRadioButton_2_2(), "1, 3, left, center");
			panel.add(getTextField_5_1(), "5, 3, fill, default");
			panel.add(getRdbtnNewRadioButton_3_2(), "1, 4, left, center");
			panel.add(getTextField_1(), "2, 4, fill, default");
			panel.add(getRdbtnSplitByPage(), "1, 5, left, center");
			panel.add(getTextField_1_1(), "2, 5, fill, default");

		}
		return panel;
	}

	private JRadioButton getRdbtnSplitByPage() {
		if (rdbtnSplitByPage == null) {
			rdbtnSplitByPage = new JRadioButton("Split by page text");
			rdbtnSplitByPage.setToolTipText("Split 'after' the page containing a specifc text");
			rdbtnSplitByPage.setEnabled(false);
		}
		return rdbtnSplitByPage;
	}

	private JRadioButton getRadioButton_1() {
		if (RadioButton_1 == null) {
			RadioButton_1 = new JRadioButton("Split by odd pages");
			RadioButton_1.setToolTipText("Split after each odd page");
			RadioButton_1.setEnabled(false);
		}
		return RadioButton_1;
	}

	private JLabel getLabel_24() {
		if (label_24 == null) {
			label_24 = new JLabel("");
		}
		return label_24;
	}

	private JLabel getLabel_25() {
		if (label_25 == null) {
			label_25 = new JLabel("");
		}
		return label_25;
	}

	private JRadioButton getRdbtnNewRadioButton_1_2() {
		if (RadioButton_2 == null) {
			RadioButton_2 = new JRadioButton("Split by even pages");
			RadioButton_2.setToolTipText("Split after each even page");
			RadioButton_2.setEnabled(false);
		}
		return RadioButton_2;
	}

	private JRadioButton getRdbtnNewRadioButton_2_2() {
		if (RadioButton_3 == null) {
			RadioButton_3 = new JRadioButton("Split by specific pages");
			RadioButton_3.setToolTipText("Split after specific pages (ex: 4-6, 9, 14)");
			RadioButton_3.setEnabled(false);
		}
		return RadioButton_3;
	}

	private JRadioButton getRdbtnNewRadioButton_3_2() {
		if (rdbtnSplitByChunk == null) {
			rdbtnSplitByChunk = new JRadioButton("Split by chunk of  'n' pages");
			rdbtnSplitByChunk.setSelected(true);
			rdbtnSplitByChunk.setToolTipText("Split after a chunk of pages (ex: 100)");
			rdbtnSplitByChunk.setEnabled(false);
		}
		return rdbtnSplitByChunk;
	}

	private JRadioButton getRdbtnSplitBySize_1() {
		if (rdbtnSplitBySize == null) {
			rdbtnSplitBySize = new JRadioButton("Split by size");
			rdbtnSplitBySize.setToolTipText("Split 'after' a specific size");
			rdbtnSplitBySize.setEnabled(false);
		}
		return rdbtnSplitBySize;
	}

	private JRadioButton getRdbtnNewRadioButton_1_1_1() {
		if (RadioButton_6 == null) {
			RadioButton_6 = new JRadioButton("Split by bookmark level");
			RadioButton_6.setToolTipText("Split 'before' the page linked to a bookmark level");
			RadioButton_6.setEnabled(false);
		}
		return RadioButton_6;
	}

	private JRadioButton getRdbtnNewRadioButton_2_1_1() {
		if (RadioButton_7 == null) {
			RadioButton_7 = new JRadioButton("Split by bookmark text");
			RadioButton_7.setToolTipText("Split 'before' the bookmark containing a specifc text");
			RadioButton_7.setEnabled(false);
		}
		return RadioButton_7;
	}

	private JLabel getLabel_7() {
		if (label_7 == null) {
			label_7 = new JLabel("");
		}
		return label_7;
	}

	private JLabel getLabel_10() {
		if (label_10 == null) {
			label_10 = new JLabel("");
		}
		return label_10;
	}

	private JLabel getLabel_12() {
		if (label_12 == null) {
			label_12 = new JLabel("");
		}
		return label_12;
	}

	private JComboBox getTextField_DPI() {
		if (textField_DPI == null) {
			textField_DPI = new JComboBox();
			textField_DPI.setModel(new DefaultComboBoxModel(new String[] {"16", "36", "72", "100", "120", "150", "180", "200", "240", "270", "300", "350", "400", "600", "800", "900", "1000", "1200", "1500", "1800", "2000", "2400", "2700", "3000", "Custom"}));
			textField_DPI.setSelectedIndex(10);
			textField_DPI.setToolTipText("");
		}
		return textField_DPI;
	}

	private JLabel getLabel_DPI() {
		if (label_DPI == null) {
			label_DPI = new JLabel("Resolution:");
			label_DPI.setToolTipText("Image resolution in DPI");
		}
		return label_DPI;
	}

	private JTextField getTextField_3() {
		if (textField_3 == null) {
			textField_3 = new JTextField("");
			textField_3.setEnabled(false);
		}
		return textField_3;
	}

	private JTextField getTextField_1() {
		if (textField_4 == null) {
			textField_4 = new JTextField("1");
			textField_4.setEnabled(false);
		}
		return textField_4;
	}

	private JTextField getTextField_1_1() {
		if (textField_5 == null) {
			textField_5 = new JTextField("");
			textField_5.setToolTipText("");
			textField_5.setEnabled(false);
		}
		return textField_5;
	}

	private JTextField getTextField_6() {
		if (textField_6 == null) {
			textField_6 = new JTextField("");
			textField_6.setEnabled(false);
		}
		return textField_6;
	}

	private JTextField getTextField_4_1() {
		if (textField_7 == null) {
			textField_7 = new JTextField("");
			textField_7.setEnabled(false);
		}
		return textField_7;
	}

	private JComboBox getTextField_5_1() {
		if (textField_8 == null) {
			textField_8 = new JComboBox();
			textField_8.setModel(new DefaultComboBoxModel(new String[] {"(MB) > MegaBytes", "(KB)  > KiloBytes", "(B)    > Bytes"}));
			textField_8.setSelectedIndex(1);
			textField_8.setEnabled(false);
		}
		return textField_8;
	}
}
