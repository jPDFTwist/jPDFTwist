package jpdftweak.tabs.input;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftweak.core.IntegerList;
import jpdftweak.tabs.input.preview.PreviewHandler;
import jpdftweak.tabs.input.treetable.FileTreeTableModel;
import jpdftweak.tabs.input.treetable.TreeTableComponent;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FolderUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;
import jpdftweak.utils.ConstantClass1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class InputTabPanel extends JPanel {

	private final JFrame parentFrame;
	private final CellConstraints CC ;
	private JTextField fileCountField;
	private final TreeTableComponent inputFilesTable;
	private JButton selectfile;
	private JButton clear;
	private JButton generate;					  						  
	private InputOptionsPanel optionsPanel;
	private FileTreeTableModel model;
	private Integer id;
	private boolean flg;					
	ConstantClass1 C;			  
	private boolean useTempFiles;
	private final String[] columnHeaders;
											
	private final Class[] itemClassType ;

	private final GenerateInputItems generateFrame;
	private final GenerateInputItems.Listener L;

	private InputTabFileImporter inputTabFileImporter;

	public static InputTabPanel getInputPanel() {
		return new InputTabPanel();
	}

	public InputTabPanel() {
		super(new FormLayout("f:p, f:p:g, f:p:g, f:p, f:p, f:p, f:p, f:p, f:p, f:p", "f:p, f:p, f:p:g"));

		this.CC = new CellConstraints();
		this.flg = true;
		this.C = new ConstantClass1();
		
		this.columnHeaders = new String[]{"File", "ID", "PaperSize", "Orientation", "Color Depth", "Size", "Pages",
				"From Page", "To Page", "Include Odd", "Include Even", "Empty Before", "Bookmark Level"};
		
		this.itemClassType = new Class[]{String.class, String.class, String.class, String.class, String.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class, Boolean.class,
				IntegerList.class, Integer.class};								  								  
		this.parentFrame = (JFrame) this.getParent();

		inputTabFileImporter = files -> {
			final ModelHandler modelHandler = new ModelHandler() {

				public void insertFileNode(Node node) {
					Node parent = model.createParents(node.getUserObject().getKey());
					model.insertNodeInto(node, parent, parent.getChildCount());

					updateFileCount();
				}

				public void updateTableUI() {
					inputFilesTable.updateTreeTableUI();
				}
			};

			final FileImporter importer;
			if (files == null) {
				importer = new FileImporter(modelHandler);
			} else {
				List<File[]> filesList = new ArrayList<>();
				filesList.add(Arrays.stream(files).map(File::new).toArray(File[]::new));
				importer = new FileImporter(modelHandler, filesList);
			}
			importer.setParentFrame(parentFrame);
			importer.setUseTempFiles(useTempFiles);
			importer.setOptimizePDF(optionsPanel.isOptimizePDFSelected());
			importer.setAutoRestrictionsOverwrite(optionsPanel.isAutoRestrictionsOverwriteSelected());
			importer.setAutoRestrioctionsNew(optionsPanel.isAutoRestrictionsNewSelected());

			final Thread importFiles = new Thread(importer);
			importFiles.start();
		};

		inputFilesTable = new TreeTableComponent(columnHeaders, itemClassType, inputTabFileImporter);
		inputFilesTable.getTreeTable().setBackground(new Color(230, 230, 250));
		model = inputFilesTable.getModel();

		generateUserInterface();
		updateFileCount();

		useTempFiles = false;

		L = new GenerateInputItems.Listener() {


			public void importFileArray(final int[] places, ArrayList<File[]> files) {
				InputTabPanel.this.index = 0;

				ModelHandler modelHandler = new ModelHandler() {


					public void insertFileNode(Node node) {
						Node parent = model.createParents(node.getUserObject().getKey());
						int i = getIndex();
						if (places[i] == -1) {
							model.insertNodeInto(node, parent, parent.getChildCount());
						} else {
							model.insertNodeInto(node, parent, places[i]);
						}

						updateFileCount();
					}

					
					public void updateTableUI() {
						inputFilesTable.updateTreeTableUI();
					}
				};

				final FileImporter importer = new FileImporter(modelHandler, files);
				final Thread importFiles = new Thread(importer);
				importFiles.start();
			}

			
			public void importFile(String name, int index) {
				importFiles(index, new File(name));
			}

			
			public void importFile(String name) {
				importFiles(new File(name));
			}

			
			public void importNode(Node node, int index) {
				Node parent = model.createParents(node.getUserObject().getKey());
				model.insertNodeInto(node, parent, index);
			}

			
			public void insertNodeInto(Node node, Node parent, int index) {
				model.insertNodeInto(node, parent, index);
			}
		};
		generateFrame = new GenerateInputItems(L);
	}

	int index;

	private int getIndex() {
		return index++;
	}

	private void generateUserInterface() {
		initializeFileRowComponents();
		positionFileRowComponents();

		generateOptionsPanel();
		generateInputFilesTable();
	}

	private void initializeFileRowComponents() {
		fileCountField = new JTextField();
		fileCountField.setEditable(false);

		selectfile = new JButton("Select...");
		selectfile.addActionListener(e -> importFilesActionPerformed());

		clear = new JButton("Clear");
		clear.addActionListener(clearButtonListener());

		generate = new JButton("Generate");
		generate.addActionListener(generateButtonListener());
	}

	private void importFilesActionPerformed() {
		inputTabFileImporter.importFilesToInputTab(null);
	}

	private ActionListener clearButtonListener() {
		ActionListener clearButtonListener = new ActionListener() {

			
			public void actionPerformed(ActionEvent e) {
				clearActionPerformed();
			}
		};

		return clearButtonListener;
	}

	private ActionListener generateButtonListener() {
		ActionListener generateButtonListener = new ActionListener() {

			
			public void actionPerformed(ActionEvent e) {
				generateFrame.setSelectedNode(inputFilesTable.getSelected());
				generateFrame.setVisible(true);
			}
		};

		return generateButtonListener;
	}

	private void clearActionPerformed() {
		// TODO mf.setInputFile(null);
		this.model.clear();
		final ConstantClass1 C = new ConstantClass1();
		C.setId(-1);
		C.setFid(0);
		C.setPreId(-1);
		this.updateFileCount();
	}

	private void updateFileCount() {
		String fn;

		if (model.isEmpty())
			fn = "(No file selected)";
		else
			fn = "(" + model.getFileCount(model.getRoot()) + " files selected)";

		fileCountField.setText(fn);
	}

	private void positionFileRowComponents() {
		this.add(new JLabel("Filename"), CC.xy(1, 1));
		this.add(fileCountField, CC.xyw(2, 1, 2));
		this.add(selectfile, CC.xy(4, 1));
		this.add(clear, CC.xy(5, 1));
		this.add(generate, CC.xy(6, 1));
	}

	private void generateOptionsPanel() {
		this.add((Component) (this.optionsPanel = new InputOptionsPanel()), this.CC.xyw(1, 2, 7));
	}

	private void generateInputFilesTable() {
		positionInputFilesTable();
	}

	private void positionInputFilesTable() {
		this.add((Component) this.inputFilesTable, this.CC.xyw(1, 3, 9));
	}

	public void setUseTempFiles(boolean useTempFiles) {
		this.useTempFiles = useTempFiles;
	}

	public boolean isModelEmpty() {
		return model.isEmpty();
	}

	public Node getRootNode() {
		return model.getRoot();
	}

	public boolean isMergeByDirSelected() {
		return optionsPanel.isMergeByDirSelected();
	}

	public boolean isBatchSelected() {
		return optionsPanel.isBatchSelected();
	}

	public boolean isInterleaveSelected() {
		return optionsPanel.isInterleaveSelected();
	}

	public int getInterleaveSize() {
		String interleaveSizeValue = optionsPanel.getInterleaveSize();
		int interleaveSize = Integer.parseInt(interleaveSizeValue);

		return interleaveSize;
	}

	public ModelReader getModelReader() {
		// TODO
		ModelReader reader = new ModelReader() {

			
			public List<Node> getFolderNodes() {
				// Node root = model.getRoot();
				// List<Node> folders = model.listFolders(root);
				// return folders;
				return null;
			}

			
			public List<Node> getFileNodes() {
				Node root = model.getRoot();
				return getFiles(root);
			}

			public List<Node> getFiles(Node root) {
				List<Node> files = new ArrayList<Node>();
				Enumeration e = root.children();
				while (e.hasMoreElements()) {
					Node n = (Node) e.nextElement();
					if (UserObjectType.isFile(n)) {
						files.add(n);
					} else if (n.getUserObject() instanceof FolderUserObject) {
						files.addAll(getFiles(n));
					}
				}
				return files;
			}
		};

		return reader;
	}

	// TODO
	public int getBatchLength() {
		if (isBatchSelected()) {
			return model.getFileCount(model.getRoot());
		} else if (isMergeByDirSelected()) {
			return model.getFolderCount(model.getRoot());
		} else {
			return 1;
		}
	}

	public void setPreviewHandler(PreviewHandler previewHandler) {
		inputFilesTable.setPreviewHandler(previewHandler);
	}

	public void importFiles(File f) {
		importFiles(-1, f);
	}

	public void importFiles(final int index, File f) {
		ModelHandler modelHandler = new ModelHandler() {

			
			public void insertFileNode(Node node) {
				Node parent = model.createParents(node.getUserObject().getKey());
				if (index == -1) {
					model.insertNodeInto(node, parent, parent.getChildCount());
				} else {
					model.insertNodeInto(node, parent, index);
				}

				updateFileCount();
			}

			
			public void updateTableUI() {
				inputFilesTable.updateTreeTableUI();
			}
		};

		final FileImporter importer = new FileImporter(modelHandler, f);
		final Thread importFiles = new Thread(importer);
		importFiles.start();
	}
}
