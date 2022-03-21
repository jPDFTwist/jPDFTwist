package jpdftweak.tabs.input.treetable;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import jpdftweak.gui.Preview;
import jpdftweak.tabs.input.InputTabFileImporter;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.PageUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeTableComponent extends JPanel {
	private final JScrollPane scrollPane;
	private final FileTreeTableModel model;
	private JXTreeTable treeTable;
	private final JButton expandBtn;
	private final JButton upBtn;
	private final JButton downBtn;
	private final JButton deleteBtn;
	private final JButton orderBtn;
	private final JButton colorBtn;
	private final JButton exportList;
	private final JButton saveList;
	private final JButton openList;
	private boolean expandCollapse;
	private boolean ascendingOrder;
	TreeTableExpansionState expansionState;
	private Preview previewPanel;
	private final CellConstraints CC;
	final InputTabFileImporter inputTabFileImporter;

	public TreeTableComponent(final String[] headers, final Class[] classes, final InputTabFileImporter inputTabFileImporter, final Preview previewPanel) {
		this.CC = new CellConstraints();
		this.previewPanel = previewPanel;
		this.expandCollapse = false;
		this.ascendingOrder = true;
		if (headers.length != classes.length) {
			throw new IllegalArgumentException();
		}

		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
				ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
				ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
			},
				new RowSpec[] { RowSpec.decode("fill:pref:grow"), RowSpec.decode("fill:pref"), }));
		this.model = new FileTreeTableModel(headers, classes);
		this.initTreeTable();
		this.expansionState = new TreeTableExpansionState(this.treeTable);
		this.scrollPane = new JScrollPane(this.treeTable);
		this.expandBtn = new JButton("Expand/Collapse");
		this.upBtn = new JButton("Up");
		this.downBtn = new JButton("Down");
		this.deleteBtn = new JButton("Delete");
		this.orderBtn = new JButton("Alphabetical order");
		this.colorBtn = new JButton("Colors");
		this.exportList = new JButton("ExportList");
		this.saveList = new JButton("Save");
		this.openList = new JButton("Load");
		this.inputTabFileImporter = inputTabFileImporter;
		this.createUI();
	}

	public JXTreeTable getTreeTable() {
		return this.treeTable;
	}

	public void setTreeTable(final JXTreeTable treeTable) {
		this.treeTable = treeTable;
	}

	private void initTreeTable() {
		this.treeTable = new JXTreeTable(this.model);

		this.treeTable.getColumnModel().getColumn(1).setPreferredWidth(90);
		this.treeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		this.treeTable.getColumnModel().getColumn(9).setPreferredWidth(30);
		this.treeTable.getColumnModel().getColumn(10).setPreferredWidth(30);
		this.treeTable.getColumnModel().getColumn(9)
		.setCellRenderer(new ConditionalCheckBoxRenderer());
		this.treeTable.getColumnModel().getColumn(10)
		.setCellRenderer(new ConditionalCheckBoxRenderer());
		this.treeTable.setRootVisible(false);
		this.treeTable.setShowGrid(true);
		this.treeTable.setColumnControlVisible(true);
		this.treeTable.setSortable(true);
		this.treeTable.setSortOrder(0, SortOrder.DESCENDING);
		final TreeTableRenderer treeCellRenderer = new TreeTableRenderer();
		this.treeTable.setTreeCellRenderer(treeCellRenderer);

		this.treeTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent evt) {
				try {
					TreeTableComponent.this.treeTableMouseListenerAction(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		this.treeTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int keyCode = evt.getKeyCode();

				if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
					try {
						TreeTableComponent.this.treeTableKeyListenerAction(evt);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		final BorderHighlighter topHighlighter = new BorderHighlighter((arg0, arg1) -> true, BorderFactory.createMatteBorder(0, 0, 1, 1, Color.DARK_GRAY));
		final ColorHighlighter colorHighlighter = new ColorHighlighter((arg0, arg1) -> !(arg0 instanceof JTree), new Color(243, 242, 241), Color.BLACK);
		this.treeTable.addHighlighter(topHighlighter);
		this.treeTable.addHighlighter(colorHighlighter);
	}

	private void createUI() {
		this.scrollPane.setPreferredSize(new Dimension(750, 400));
		this.add(this.scrollPane, CC.xyw(1, 1, 9));
		this.add(this.expandBtn, CC.xy(1, 2));
		this.expandBtn.addActionListener(e -> TreeTableComponent.this.expandButtonListenerAction());
		this.add(this.upBtn, CC.xy(2, 2));
		this.upBtn.addActionListener(e -> TreeTableComponent.this.upButtonListenerAction());
		this.add(this.downBtn, CC.xy(3, 2));
		this.downBtn.addActionListener(e -> TreeTableComponent.this.downButtonListenerAction());
		this.add(this.deleteBtn, CC.xy(4, 2));
		this.deleteBtn.addActionListener(e -> {
			try {
				TreeTableComponent.this.deleteButtonListenerAction();
			} catch (Exception ex) {
				Logger.getLogger(TreeTableComponent.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		this.add(this.orderBtn, CC.xy(5, 2));
		this.orderBtn.addActionListener(e -> TreeTableComponent.this.sortButtonListenerAction());
		this.add(this.colorBtn, CC.xy(6, 2));
		this.colorBtn.addActionListener(e -> TreeTableComponent.this.colorButtonListenerAction());

		this.add(this.exportList, CC.xy(7, 2));
		this.exportList.addActionListener(e -> {
			TreeTableComponent.this.createPdf();

			JOptionPane.showMessageDialog(null, "File successfully saved at C:\\jProject\\Untitled\\ ");

		});

		this.add(this.saveList, CC.xy(8, 2));
		this.saveList.addActionListener(e -> {
			TreeTableComponent.this.createJSON();
		});

		this.add(this.openList, CC.xy(9, 2));
		this.openList.addActionListener(e -> TreeTableComponent.this.loadJSON());
	}

	private void treeTableMouseListenerAction(final MouseEvent evt) throws IOException {

		if (evt.getClickCount() != 2) {
			return;
		}
		final int row = this.treeTable.getSelectedRow();
		final TreePath path = this.treeTable.getPathForRow(row);

		if (path == null) {
			return;
		}
		final Node node = (Node) path.getLastPathComponent();
		if ((evt.getModifiers() & 0x10) != 0x10) {
			return;
		}
		if (node.getUserObject() instanceof FileUserObject) {
			try {
				File newFile = new File(node.getUserObject().getKey());
				if (newFile.exists()) {
					Desktop.getDesktop().open(newFile);
				}

			} catch (IOException ex) {
				Logger.getLogger(TreeTableComponent.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void treeTableKeyListenerAction(final KeyEvent evt) throws IOException {
		int row = -1;

		if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
			row = this.treeTable.getSelectedRow() + 1;
		}

		if (evt.getKeyCode() == KeyEvent.VK_UP) {
			row = this.treeTable.getSelectedRow() - 1;
		}
		final TreePath path = this.treeTable.getPathForRow(row);

		if (path == null) {
			return;
		}
		final Node node = (Node) path.getLastPathComponent();

		if (node.getUserObject() instanceof PageUserObject) {

			final FileUserObject userObject = (FileUserObject) node.getParent().getUserObject();
			String parent = userObject.getKey();

			PDFFile file = new PDFFile(ByteBuffer.wrap(loadFile(parent)));
			PDFPage page = file.getPage(Integer.parseInt(node.getUserObject().getKey()));
			int rwidth = (int) (page.getBBox().getWidth());
			int rheight = (int) (page.getBBox().getHeight());
			Rectangle rect = new Rectangle(0, 0, rwidth, rheight);
			Image img;

			if (rwidth / 72 == 1000 && rheight / 72 == 1000) {
				img = page.getImage(rwidth / 200, rheight / 200, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 > 500 && rheight / 72 > 500) {
				img = page.getImage(rwidth / 100, rheight / 100, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 > 200 && rheight / 72 > 200) {
				img = page.getImage(rwidth / 50, rheight / 50, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 > 90 && rheight / 72 > 90) {
				img = page.getImage(rwidth / 25, rheight / 25, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 > 50 && rheight / 72 > 50) {
				img = page.getImage(rwidth / 20, rheight / 20, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 > 20 && rheight / 72 > 20) {
				img = page.getImage(rwidth / 10, rheight / 10, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if ((rwidth / 72 > 15 && rwidth / 72 < 20) && (rheight / 72 > 15 && rheight / 72 < 20)) {
				img = page.getImage(rwidth / 4, rheight / 4, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 < 5 && rheight / 72 < 5) {
				img = page.getImage(rwidth, rheight, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			} else if (rwidth / 72 < 15 && rheight / 72 < 15) {
				img = page.getImage(rwidth / 2, rheight / 2, rect, null, true, true);
				this.previewPanel.preview(img, rwidth, rheight, page);
			}
		}

	}

	private void expandButtonListenerAction() {
		if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
			return;
		}
		if (this.expandCollapse) {
			this.treeTable.collapseAll();
		} else {
			this.treeTable.expandAll();
		}
		this.expandCollapse = !this.expandCollapse;
	}

	private void upButtonListenerAction() {
		this.expansionState.store();
		final ArrayList<TreePath> newPaths = new ArrayList<>();
		for (int selectedRowCount = this.treeTable.getSelectedRowCount(), i = 0; i < selectedRowCount; ++i) {
			final int row = this.treeTable.getSelectedRows()[0];
			if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
				return;
			}
			final TreePath path = this.treeTable.getPathForRow(row);
			final TreePath newPath = this.model.moveRow(path, -1);
			newPaths.add(newPath);
		}
		this.expansionState.restore();
		final Runnable setSelectionRunnable = () -> {
			final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
			tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
		};
		SwingUtilities.invokeLater(setSelectionRunnable);
	}

	private void downButtonListenerAction() {
		this.expansionState.store();
		final ArrayList<TreePath> newPaths = new ArrayList<>();
		for (int i = this.treeTable.getSelectedRowCount() - 1; i >= 0; --i) {
			final int row = this.treeTable.getSelectedRows()[i];
			if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
				return;
			}
			final TreePath path = this.treeTable.getPathForRow(row);
			final TreePath newPath = this.model.moveRow(path, 1);
			newPaths.add(newPath);
		}
		this.expansionState.restore();
		final Runnable setSelectionRunnable = () -> {
			final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
			tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
		};
		SwingUtilities.invokeLater(setSelectionRunnable);
	}

	private void deleteButtonListenerAction() {
		this.previewPanel.clearPreview();
		for (int i = this.treeTable.getSelectedRowCount() - 1; i >= 0
				&& this.treeTable.getSelectedRowCount() != 0; --i) {
			final int row = this.treeTable.getSelectedRows()[i];
			if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
				return;
			}
			final TreePath path = this.treeTable.getPathForRow(row);
			this.model.removeNodeFromParent((Node) path.getLastPathComponent(), true);
		}
	}

	private void sortButtonListenerAction() {
		this.expansionState.store();
		final Node parent = this.model.getRoot();
		parent.sortNode(0, this.ascendingOrder, true);
		this.ascendingOrder = !this.ascendingOrder;
		this.expansionState.restore();
	}

	private void colorButtonListenerAction() {
		final TreeTableColorPanel colors = new TreeTableColorPanel();
		colors.setVisible(true);
	}

	public FileTreeTableModel getModel() {
		return this.model;
	}

	public void clear() {
		this.model.clear();
	}

	public Node getSelected() {
		final int row = this.treeTable.getSelectedRow();
		final TreePath path = this.treeTable.getPathForRow(row);
		if (path == null) {

			return null;
		}

		return (Node) path.getLastPathComponent();
	}

	public void updateTreeTableUI() {
		SwingUtilities.invokeLater(() -> TreeTableComponent.this.treeTable.updateUI());
	}

	public static byte[] loadFile(String sourcePath) throws IOException {
		try (InputStream inputStream = new FileInputStream(sourcePath)) {
			return readFully(inputStream);
		}
	}

	public static byte[] readFully(InputStream stream) throws IOException {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int bytesRead;
		while ((bytesRead = stream.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	private void createPdf() {
		Document document = new Document();

		JTable table = treeTable;

		String filename;
		LocalDateTime now = LocalDateTime.now();
		String date = now.getDayOfMonth() + "_" + now.getMonth() + "_" + now.getYear();
		String time = now.getHour() + "_" + now.getMinute() + "_" + now.getSecond();
		String timestamp = date + "_" + time;
		try {
			PdfWriter writer;
			filename = "my_jtree_" + timestamp + ".pdf";
			writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\jProject\\Untitled\\" + filename));
			document.open();

			BufferedImage img1 = (BufferedImage) getImageFromComponent(table.getTableHeader());
			BufferedImage img2 = (BufferedImage) getImageFromComponent(table);
			BufferedImage joinedImg = joinBufferedImage(img1, img2);
			addImageToDocument(document, writer, joinedImg);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		document.close();

	}

	public java.awt.Image getImageFromComponent(JComponent component) {
		BufferedImage image = new BufferedImage(treeTable.getWidth(), component.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;

	}

	public void addImageToDocument(Document document, PdfWriter writer, java.awt.Image img)
			throws IOException, DocumentException {
		com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(writer, img, 1);

		PdfContentByte content = writer.getDirectContent();
		image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
		image.setAbsolutePosition(0, 0);

		float width = PageSize.A4.getWidth();
		float heightRatio = image.getHeight() * width / image.getWidth();
		int nPages = (int) (heightRatio / PageSize.A4.getHeight());
		float difference = heightRatio % PageSize.A4.getHeight();

		while (nPages >= 0) {
			document.newPage();
			content.addImage(image, width, 0, 0, heightRatio, 0, -((--nPages * PageSize.A4.getHeight()) + difference));
		}

	}

	public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {

		int heightTotal = 0;
		int maxWidth = 100;

		ArrayList<BufferedImage> images = new ArrayList<>();

		images.add(img1);
		images.add(img2);

		for (BufferedImage bufferedImage : images) {
			heightTotal += bufferedImage.getHeight();
			if (bufferedImage.getWidth() > maxWidth) {
				maxWidth = bufferedImage.getWidth();
			}

		}

		int heightCurr = 0;
		BufferedImage concatImage = new BufferedImage(maxWidth, heightTotal, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = concatImage.createGraphics();
		for (BufferedImage bufferedImage : images) {
			g2d.drawImage(bufferedImage, 0, heightCurr, null);
			heightCurr += bufferedImage.getHeight();
		}

		return concatImage;

	}

	private List<String> getInputElements(Node parent) {
		final List<String> inputElements = new ArrayList<>();

		Enumeration<?> e = parent.children();
		while (e.hasMoreElements()) {
			final Node child = (Node) e.nextElement();
			if (UserObjectType.isFile(child)) {
				inputElements.add(child.getUserObject().getKey());
			} else if (child.getUserObject().getType() == UserObjectType.FOLDER) {
				inputElements.addAll(getInputElements(child));
			}
		}

		return inputElements;
	}

	public void createJSON() {
		try {
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(TreeTableComponent.this);

			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			final TreeTableModel model = treeTable.getTreeTableModel();
			final JSONArray inputFiles = new JSONArray(getInputElements((Node) model.getRoot()));
			final JSONObject json = new JSONObject();
			json.put("input-files", inputFiles);

			LocalDateTime now = LocalDateTime.now();
			String timestamp = String.format("%s_%s_%s_%s_%s_%s",
				now.getDayOfMonth(), now.getMonth(), now.getYear(), now.getHour(), now.getMinute(), now.getSecond());
			String filename = "jpdftweak_input_" + timestamp + ".json";
			final String filepath = fc.getSelectedFile().getAbsolutePath() + File.separator + filename;
			FileWriter jsonFile = new FileWriter(filepath);
			jsonFile.write(json.toString(4));
			jsonFile.close();
			JOptionPane.showMessageDialog(null, "File successfully saved at " + filepath);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void loadJSON()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(TreeTableComponent.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
				JSONObject json = new JSONObject(content);
				JSONArray inputFiles = json.getJSONArray("input-files");
				String[] files = new String[inputFiles.length()];
				for(int i = 0; i < inputFiles.length(); i++){
					files[i] = inputFiles.getString(i);
				}
				inputTabFileImporter.importFilesToInputTab(files);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

	}

}