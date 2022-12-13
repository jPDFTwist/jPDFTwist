package jpdftwist.tabs.input.treetable.node.factory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.text.pdf.PdfReader;

import jpdftwist.core.IntegerList;
import jpdftwist.tabs.input.treetable.UserObjectValue;
import jpdftwist.tabs.input.treetable.node.Node;
import jpdftwist.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftwist.tabs.input.treetable.node.userobject.RealFileUserObject;
import jpdftwist.utils.PdfParser;

/**
 *
 * @author Vasilis Naskos
 */
public class RealPdfNodeFactory extends FileNodeFactory {

	private String filepath;
	private PdfReader reader;
	private boolean autoRestrictionsOverwrite;
	private boolean autoRestrictionsNew;

	public RealPdfNodeFactory() {
		autoRestrictionsNew = false;
		autoRestrictionsOverwrite = false;
	}

	
	public Node getFileNode(String filepath) {
		this.filepath = filepath;
		try {
			this.reader = PdfParser.open(filepath, optimize, autoRestrictionsOverwrite, autoRestrictionsNew);
		} catch (IOException ex) {
			Logger.getLogger(RealPdfNodeFactory.class.getName()).log(Level.SEVERE, null, ex);
			// TODO throw Exception
		}

		RealFileUserObject pdfUO = createRealPdfUserObject();

		Node file = new Node(pdfUO);
		insertPages(file);

		//        reader.close();

		return file;
	}

	private RealFileUserObject createRealPdfUserObject() {
		// System.out.println("filepath:"+this.filepath);
		long fileSize = new File(filepath).length();

		RealFileUserObject pdfUO = new RealFileUserObject(filepath, FileUserObject.SubType.PDF);

		pdfUO.setValueAt(fileSize, UserObjectValue.SIZE);
		pdfUO.setValueAt(reader.getNumberOfPages(), UserObjectValue.PAGES);
		pdfUO.setValueAt(1, UserObjectValue.FROM);
		pdfUO.setValueAt(reader.getNumberOfPages(), UserObjectValue.TO);
		pdfUO.setValueAt(true, UserObjectValue.EVEN);
		pdfUO.setValueAt(true, UserObjectValue.ODD);
		pdfUO.setValueAt(new IntegerList("0"), UserObjectValue.EMPTY_BEFORE);
		pdfUO.setValueAt(0, UserObjectValue.BOOKMARK_LEVEL);

		return pdfUO;
	}

	private void insertPages(Node file) {
		int pageCount = reader.getNumberOfPages();
		updateListenersPageCount(pageCount);

		PageNodeFactory pageNodeFactory = NodeFactory.getPageNodeFactory();

		for (int i = 1; i <= pageCount; i++) {
			updateListenersNextPage(i);

			pageNodeFactory.setSize(reader.getPageSizeWithRotation(i));
			Node page = pageNodeFactory.getPageNode(i);

			file.insert(page, i - 1);
		}
	}

	public void setAutoRestrictionsNew(boolean autoRestrictionsNew) {
		this.autoRestrictionsNew = autoRestrictionsNew;
	}

	public void setAutoRestrictionsOverwrite(boolean autoRestrictionsOverwrite) {
		this.autoRestrictionsOverwrite = autoRestrictionsOverwrite;
	}

}
