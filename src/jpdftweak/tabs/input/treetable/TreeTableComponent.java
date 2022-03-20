package jpdftweak.tabs.input.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jpdftweak.core.PageRange;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.treetable.TreeTableModel;

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
import jpdftweak.tabs.input.preview.PreviewHandler;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.PageUserObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class TreeTableComponent extends JPanel implements PreviewHandler {
	private final JScrollPane scrollPane;
	private FileTreeTableModel model;
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
	private PreviewHandler previewHandler;
	// private Component previewPanel;
	private JPanel previewPanel;
	private final CellConstraints CC;
	private double zoom = 1.0; // zoom factor
	private DefaultListModel listModel;
	private ImageIcon image;
	private JList<JLabel> list;
	private Preview preview;
	// private int headercheck=0;
	// boolean isNewPage;
	// private PdfPTable pdfTable;

	public TreeTableComponent(final String[] headers, final Class[] classes) {
		this.CC = new CellConstraints();
		this.expandCollapse = false;
		this.ascendingOrder = true;
		if (headers.length != classes.length) {
			throw new IllegalArgumentException();
		}

		setLayout((LayoutManager) new FormLayout(
				new ColumnSpec[] { ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"), ColumnSpec.decode("pref:grow"),
						ColumnSpec.decode("pref:grow"), },
				new RowSpec[] { RowSpec.decode("fill:pref:grow"), RowSpec.decode("fill:pref"), }));
		this.model = new FileTreeTableModel(headers, classes);
		this.initTreeTable();
		this.expansionState = new TreeTableExpansionState(this.treeTable);
		this.scrollPane = new JScrollPane((Component) this.treeTable);
		this.expandBtn = new JButton("Expand/Collapse");
		this.upBtn = new JButton("Up");
		this.downBtn = new JButton("Down");
		this.deleteBtn = new JButton("Delete");
		this.orderBtn = new JButton("Alphabetical order");
		this.colorBtn = new JButton("Colors");
		this.exportList = new JButton("ExportList");
		this.saveList = new JButton("Save");
		this.openList = new JButton("Load CSV");
		this.createUI();
	}

	public JXTreeTable getTreeTable() {
		return this.treeTable;
	}

	public void setTreeTable(final JXTreeTable treeTable) {
		this.treeTable = treeTable;
	}

	private void initTreeTable() {
		this.treeTable = new JXTreeTable((TreeTableModel) this.model);

		this.treeTable.getColumnModel().getColumn(1).setPreferredWidth(90);
		this.treeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		this.treeTable.getColumnModel().getColumn(9).setPreferredWidth(30);
		this.treeTable.getColumnModel().getColumn(10).setPreferredWidth(30);
		this.treeTable.getColumnModel().getColumn(9)
		.setCellRenderer((TableCellRenderer) new ConditionalCheckBoxRenderer());
		this.treeTable.getColumnModel().getColumn(10)
		.setCellRenderer((TableCellRenderer) new ConditionalCheckBoxRenderer());
		this.treeTable.setRootVisible(false);
		this.treeTable.setShowGrid(true);
		this.treeTable.setColumnControlVisible(true);
		this.treeTable.setSortable(true);
		this.treeTable.setSortOrder(0, SortOrder.DESCENDING);
		final TreeTableRenderer treeCellRenderer = new TreeTableRenderer();
		this.treeTable.setTreeCellRenderer((TreeCellRenderer) treeCellRenderer);

		this.treeTable.addMouseListener((MouseListener) new MouseAdapter() {
			
			public void mouseClicked(final MouseEvent evt) {
				try {
					TreeTableComponent.this.treeTableMouseListenerAction(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		this.treeTable.addKeyListener((KeyListener) new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int keyCode = evt.getKeyCode();

				if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {

					try {
						TreeTableComponent.this.treeTableKeyListenerAction(evt);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});

		final BorderHighlighter topHighlighter = new BorderHighlighter((HighlightPredicate) new HighlightPredicate() {
			public boolean isHighlighted(final Component arg0, final ComponentAdapter arg1) {
				return true;
			}
		}, (Border) BorderFactory.createMatteBorder(0, 0, 1, 1, Color.DARK_GRAY));
		final ColorHighlighter colorHighlighter = new ColorHighlighter((HighlightPredicate) new HighlightPredicate() {
			public boolean isHighlighted(final Component arg0, final ComponentAdapter arg1) {
				return !(arg0 instanceof JTree);
			}
		}, new Color(243, 242, 241), Color.BLACK);
		this.treeTable.addHighlighter((Highlighter) topHighlighter);
		this.treeTable.addHighlighter((Highlighter) colorHighlighter);
	}

	private void createUI() {
		// final CellConstraints CC = new CellConstraints();
		this.scrollPane.setPreferredSize(new Dimension(750, 400));
		this.add(this.scrollPane, CC.xyw(1, 1, 9));
		this.add(this.expandBtn, CC.xy(1, 2));
		this.expandBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.expandButtonListenerAction();
			}
		});
		this.add(this.upBtn, CC.xy(2, 2));
		this.upBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.upButtonListenerAction();
			}
		});
		this.add(this.downBtn, CC.xy(3, 2));
		this.downBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.downButtonListenerAction();
			}
		});
		this.add(this.deleteBtn, CC.xy(4, 2));
		this.deleteBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				try {
					TreeTableComponent.this.deleteButtonListenerAction();
				} catch (Exception ex) {
					Logger.getLogger(TreeTableComponent.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		this.add(this.orderBtn, CC.xy(5, 2));
		this.orderBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.sortButtonListenerAction();
			}
		});
		this.add(this.colorBtn, CC.xy(6, 2));
		this.colorBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.colorButtonListenerAction();
			}
		});

		this.add(this.exportList, CC.xy(7, 2));
		this.exportList.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.createPdf();

				JOptionPane.showMessageDialog(null, "File successfully saved at C:\\jProject\\Untitled\\ ");

			}
		});

		this.add(this.saveList, CC.xy(8, 2));
		this.saveList.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.createCSV();
				JOptionPane.showMessageDialog(null, "File successfully saved at C:\\jProject\\Untitled\\ ");

			}
		});

		this.add(this.openList, CC.xy(9, 2));
		this.openList.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent e) {
				TreeTableComponent.this.loadCSV();

			}
		});

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

			final FileUserObject userObject = (FileUserObject) node.getUserObject();
			// System.out.println("path:"+userObject.getKey());

			try {
				File newFile = new File(userObject.getKey());
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
				// Rectangle rect = new Rectangle(0, 0, rwidth/200, rheight/200);
				img = page.getImage(rwidth / 200, rheight / 200, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 > 500 && rheight / 72 > 500) {
				img = page.getImage(rwidth / 100, rheight / 100, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 > 200 && rheight / 72 > 200) {
				img = page.getImage(rwidth / 50, rheight / 50, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 > 90 && rheight / 72 > 90) {

				img = page.getImage(rwidth / 25, rheight / 25, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 > 50 && rheight / 72 > 50) {

				img = page.getImage(rwidth / 20, rheight / 20, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 > 20 && rheight / 72 > 20) {

				img = page.getImage(rwidth / 10, rheight / 10, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if ((rwidth / 72 > 15 && rwidth / 72 < 20) && (rheight / 72 > 15 && rheight / 72 < 20)) {

				img = page.getImage(rwidth / 4, rheight / 4, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 < 5 && rheight / 72 < 5) {

				img = page.getImage(rwidth, rheight, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			else if (rwidth / 72 < 15 && rheight / 72 < 15) {

				img = page.getImage(rwidth / 2, rheight / 2, rect, null, true, true);
				previewPanel = new Preview(new Dimension(400, 500), img, rwidth, rheight, page);

			}

			this.previewPanel.repaint();
			// this.add(this.previewPanel,CC.xyw(8,1,7));
			this.add(this.previewPanel, CC.xyw(10, 1, 18));

			// this.previewHandler.runPreview(node);
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
		final ArrayList<TreePath> newPaths = new ArrayList<TreePath>();
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
		final Runnable setSelectionRunnable = new Runnable() {
			
			public void run() {
				final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
				tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
			}
		};
		SwingUtilities.invokeLater(setSelectionRunnable);
	}

	private void downButtonListenerAction() {
		this.expansionState.store();
		final ArrayList<TreePath> newPaths = new ArrayList<TreePath>();
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
		final Runnable setSelectionRunnable = new Runnable() {
			
			public void run() {
				final TreeSelectionModel tsm = TreeTableComponent.this.treeTable.getTreeSelectionModel();
				tsm.setSelectionPaths(newPaths.toArray(new TreePath[0]));
			}
		};
		SwingUtilities.invokeLater(setSelectionRunnable);
	}

	private void deleteButtonListenerAction() {
		for (int i = this.treeTable.getSelectedRowCount() - 1; i >= 0
				&& this.treeTable.getSelectedRowCount() != 0; --i) {
			final int row = this.treeTable.getSelectedRows()[i];
			if (this.treeTable.getCellEditor() != null && !this.treeTable.getCellEditor().stopCellEditing()) {
				return;
			}
			final TreePath path = this.treeTable.getPathForRow(row);
			this.model.removeNodeFromParent((Node) path.getLastPathComponent(), true);
			preview = new Preview();
			this.preview.clearPreview();
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

	public void setPreviewHandler(final PreviewHandler previewHandler) {
		this.previewHandler = previewHandler;
	}

	public Node getSelected() {
		final int row = this.treeTable.getSelectedRow();
		final TreePath path = this.treeTable.getPathForRow(row);
		if (path == null) {

			return null;
		}
		final Node node = (Node) path.getLastPathComponent();

		return node;
	}

	public void updateTreeTableUI() {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				TreeTableComponent.this.treeTable.updateUI();
			}
		});
	}

	
	public void runPreview(Node p0) {
		// TODO Auto-generated method stub

	}

	public static byte[] loadFile(String sourcePath) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(sourcePath);
			return readFully(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
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
		// Document document=new Document(PageSize.A4,50.0f,50.0f,10.0f,50.0f);
		Document document = new Document();

		JTable table = (JTable) treeTable;

		int width = table.getWidth();
		int height = table.getHeight();

		String filename = null;
		LocalDateTime now = LocalDateTime.now();
		String date = Integer.toString(now.getDayOfMonth()) + "_" + now.getMonth() + "_"
				+ Integer.toString(now.getYear());
		String time = Integer.toString(now.getHour()) + "_" + Integer.toString(now.getMinute()) + "_"
				+ Integer.toString(now.getSecond());
		String timestamp = date + "_" + time;
		try {

			File dir = new File("C:/jProject/Untitled");
			if (dir.exists() == false) {
				boolean result = dir.mkdirs();
				// System.out.println("created:"+ result);
			}

			PdfWriter writer;
			filename = "my_jtree_" + timestamp + ".pdf";
			writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\jProject\\Untitled\\" + filename));
			document.open();

			/**
			 * PdfContentByte cb=writer.getDirectContent(); PdfTemplate
			 * tp=cb.createTemplate((float)width,(float)height);
			 *
			 * Graphics2D g2; if(shapes)
			 * g2=tp.createGraphicsShapes((float)width,(float)height); else
			 * g2=tp.createGraphicsShapes((float)width,(float)height);
			 *
			 * table.print(g2);
			 *
			 * g2.dispose(); com.itextpdf.text.Image
			 * image=com.itextpdf.text.Image.getInstance((PdfTemplate)tp);
			 * image.scalePercent(43.0f); document.add((com.itextpdf.text.Element)image);
			 **/

			/**
			 * java.awt.Image img1=getImageFromComponent(table.getTableHeader());
			 * addImageToDocument(document, writer, img1);
			 *
			 * getRowsImage(table,document, writer);
			 **/

			// java.awt.Image img2=getImageFromComponent(table);
			// addImageToDocument(document, writer, img2);

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
		// System.out.println("width:"+component.getWidth());
		BufferedImage image = new BufferedImage(treeTable.getWidth(), component.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;

	}

	public void addImageToDocument(Document document, PdfWriter writer, java.awt.Image img)
			throws IOException, DocumentException {
		com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(writer, img, 1);

		/**
		 * com.itextpdf.text.Rectangle rect=new
		 * com.itextpdf.text.Rectangle(image.getScaledWidth(), image.getScaledHeight());
		 * document.setPageSize(rect); document.newPage(); //image.scalePercent(43.0f);
		 * image.setAbsolutePosition(0, 0);
		 * document.add((com.itextpdf.text.Element)image);
		 **/

		PdfContentByte content = writer.getDirectContent();
		image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
		image.setAbsolutePosition(0, 0);

		float width = PageSize.A4.getWidth();
		float heightRatio = image.getHeight() * width / image.getWidth();
		int nPages = (int) (heightRatio / PageSize.A4.getHeight());
		float difference = heightRatio % PageSize.A4.getHeight();
		// System.out.println("difference:"+difference);

		while (nPages >= 0) {
			// difference=difference+8.0f;
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

	public void createCSV() {
		try {
			final TreeTableModel model = treeTable.getTreeTableModel();
			final JSONArray inputFiles = new JSONArray(getInputElements((Node) model.getRoot()));
			final JSONObject json = new JSONObject();
			json.put("input-files", inputFiles);

			LocalDateTime now = LocalDateTime.now();
			String timestamp = String.format("%s_%s_%s_%s_%s_%s",
				now.getDayOfMonth(), now.getMonth(), now.getYear(), now.getHour(), now.getMinute(), now.getSecond());
			String filename = "Jtree_details" + timestamp + ".json";
			FileWriter jsonFile = new FileWriter("C:\\jProject\\Untitled\\" + filename);
			jsonFile.write(json.toString(4));
			jsonFile.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void loadCSV()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(TreeTableComponent.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
				JSONObject json = new JSONObject(content);
				JSONArray inputFiles = json.getJSONArray("input-files");
				for (int i=0;i<inputFiles.length();i++){
					System.out.println(inputFiles.getString(i));
					// TODO: load the json files into the table model
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

	}

}
