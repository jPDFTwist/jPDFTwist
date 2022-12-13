package jpdftwist.tabs.input;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.IntegerList;
import jpdftwist.gui.Preview;
import jpdftwist.gui.components.treetable.Node;
import jpdftwist.gui.components.treetable.TreeTableComponent;
import jpdftwist.gui.components.treetable.TreeTableModel;
import jpdftwist.gui.components.treetable.TreeTableRowType;
import jpdftwist.gui.components.treetable.row.FolderTreeTableRow;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import java.awt.*;
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
	private final CellConstraints CC;
	private JTextField fileCountField;
	private final Preview previewPanel;
	private final TreeTableComponent inputFilesTable;
	private JButton selectFile;
	private JButton clear;
	private JButton generate;					  						  
	private InputOptionsPanel optionsPanel;
	private final TreeTableModel model;
	private boolean useTempFiles;

	private final GenerateInputItems generateFrame;

	private final InputTabFileImporter inputTabFileImporter;

	public static InputTabPanel getInputPanel() {
		return new InputTabPanel();
	}

	public InputTabPanel() {
		super(new FormLayout("f:p, f:p:g, f:p:g, f:p, f:p, f:p, f:p, f:p, f:p, f:p", "f:p, f:p, f:p:g"));

		this.CC = new CellConstraints();

		String[] columnHeaders = new String[]{"File", "ID", "PaperSize", "Orientation", "Color Depth", "Size", "Pages",
			"From Page", "To Page", "Include Odd", "Include Even", "Empty Before", "Bookmark Level"};

		Class[] itemClassType = new Class[]{String.class, String.class, String.class, String.class, String.class,
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

		this.previewPanel = new Preview();

		inputFilesTable = new TreeTableComponent(columnHeaders, itemClassType, inputTabFileImporter, previewPanel);
		inputFilesTable.getTreeTable().setBackground(new Color(230, 230, 250));
		model = inputFilesTable.getModel();

		generateUserInterface();
		updateFileCount();

		useTempFiles = false;

		GenerateInputItems.Listener l = new GenerateInputItems.Listener() {


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
		generateFrame = new GenerateInputItems(l);
	}

	int index;

	private int getIndex() {
		return index++;
	}

	private void generateUserInterface() {
		initializeFileRowComponents();
		positionFileRowComponents();

		generateOptionsPanel();

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputFilesTable, previewPanel);
		splitPane.setResizeWeight(0.9);
		this.add(splitPane, this.CC.xyw(1, 3, 9));
	}

	private void initializeFileRowComponents() {
		fileCountField = new JTextField();
		fileCountField.setEditable(false);

		selectFile = new JButton("Select...");
		selectFile.addActionListener(e -> importFilesActionPerformed());

		clear = new JButton("Clear");
		clear.addActionListener(clearButtonListener());

		generate = new JButton("Generate");
		generate.addActionListener(generateButtonListener());
	}

	private void importFilesActionPerformed() {
		inputTabFileImporter.importFilesToInputTab(null);
	}

	private ActionListener clearButtonListener() {
		return e -> clearActionPerformed();
	}

	private ActionListener generateButtonListener() {
		return e -> {
			generateFrame.setSelectedNode(inputFilesTable.getSelected());
			generateFrame.setVisible(true);
		};
	}

	private void clearActionPerformed() {
		this.previewPanel.clearPreview();
		this.model.clear();
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
		this.add(selectFile, CC.xy(4, 1));
		this.add(clear, CC.xy(5, 1));
		this.add(generate, CC.xy(6, 1));
	}

	private void generateOptionsPanel() {
		this.add(this.optionsPanel = new InputOptionsPanel(), this.CC.xyw(1, 2, 7));
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

		return Integer.parseInt(interleaveSizeValue);
	}

	public ModelReader getModelReader() {
		return new ModelReader() {
			public List<Node> getFolderNodes() {
				return null;
			}

			public List<Node> getFileNodes() {
				Node root = model.getRoot();
				return getFiles(root);
			}

			public List<Node> getFiles(Node root) {
				List<Node> files = new ArrayList<>();
				Enumeration<? extends MutableTreeTableNode> e = root.children();
				while (e.hasMoreElements()) {
					Node n = (Node) e.nextElement();
					if (TreeTableRowType.isFile(n)) {
						files.add(n);
					} else if (n.getUserObject() instanceof FolderTreeTableRow) {
						files.addAll(getFiles(n));
					}
				}
				return files;
			}
		};
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
