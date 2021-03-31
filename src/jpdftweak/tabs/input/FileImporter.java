package jpdftweak.tabs.input;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import jpdftweak.core.FilenameUtils;
import jpdftweak.tabs.input.error.ErrorHandler;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.factory.FileNodeFactory;
import jpdftweak.tabs.input.treetable.node.factory.NodeFactory;
import jpdftweak.tabs.input.treetable.node.factory.RealPdfNodeFactory;
import jpdftweak.tabs.input.treetable.node.factory.event.PageEventListener;
import jpdftweak.tabs.input.treetable.node.userobject.FileUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;
import jpdftweak.utils.SupportedFileTypes;

/**
 *
 * @author Vasilis Naskos
 */
public class FileImporter implements Runnable {

	private JFrame parentFrame;

	private final InputProgressDialog importDialog;
	private final ModelHandler modelHandler;
	private final ErrorHandler errorHandler;

	private boolean useTempFiles, optimizePDF, autoRestrictionsOverwrite, autoRestrioctionsNew;

	private ArrayList<File[]> files;
	private ProcessCancelledListener cancelListener;

	private boolean cancel;

	public FileImporter(ModelHandler modelHandler) {
		this.cancel = false;
		this.useTempFiles = false;
		this.optimizePDF = false;
		this.autoRestrioctionsNew = true;
		this.autoRestrictionsOverwrite = true;

		this.errorHandler = new ErrorHandler();
		this.importDialog = new InputProgressDialog();
		this.cancelListener = new ProcessCancelledListener() {

			
			public void cancelled() {
				cancel = true;
			}
		};
		importDialog.setCancelledListener(cancelListener);

		this.modelHandler = modelHandler;
	}

	public void setParentFrame(JFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

	public void setOptimizePDF(boolean optimizePDF) {
		this.optimizePDF = optimizePDF;
	}

	public void setAutoRestrictionsOverwrite(boolean autoRestrictionsOverwrite) {
		this.autoRestrictionsOverwrite = autoRestrictionsOverwrite;
	}

	public void setAutoRestrioctionsNew(boolean autoRestrioctionsNew) {
		this.autoRestrioctionsNew = autoRestrioctionsNew;
	}

	public void setUseTempFiles(boolean useTempFiles) {
		this.useTempFiles = useTempFiles;
	}

	public FileImporter(ModelHandler handler, File... f) {
		this(handler);
		files = new ArrayList<File[]>();
		files.add(f);
	}

	public FileImporter(ModelHandler handler, ArrayList<File[]> files) {
		this(handler);
		this.files = files;
	}

	
	public void run() {
		if (files != null && !files.isEmpty()) {

			showProgressDialog();

			setProgressBarLimits();

			for (File[] fileArray : files) {
				if (!importDialog.isVisible()) {
					break;
				}
				importDirectory(fileArray);
			}

			importDialog.closeDialogWithDelay();
			errorHandler.showErrors();

			modelHandler.updateTableUI();
			return;
		}

		FileChooser fileChooser = new FileChooser();
		File[] selectedFiles = fileChooser.getSelectedFiles();
		if (selectedFiles == null)
			return;

		showProgressDialog();

		DirectoryScanner scanner = new DirectoryScanner(selectedFiles);
		files = scanner.getFiles();

		setProgressBarLimits();

		Thread t = new Thread(new Runnable() {

			
			public void run() {
				for (File[] directory : files) {
					if (cancel) {
						break;
					}
					importDirectory(directory);
				}

				importDialog.closeDialogWithDelay();
				errorHandler.showErrors();
				modelHandler.updateTableUI();
				System.gc();
			}
		});

		t.start();
	}

	private void setProgressBarLimits() {
		int foldersCount = files.size();
		int[] filesInFolders = new int[foldersCount];
		int totalFiles = 0;

		for (int i = 0; i < foldersCount; i++) {
			filesInFolders[i] = files.get(i).length;
			totalFiles += filesInFolders[i];
		}

		importDialog.setFileCount(totalFiles);
		importDialog.setFoldersCount(foldersCount);
		importDialog.setFilesInFolderCount(filesInFolders);
	}

	private void showProgressDialog() {
		importDialog.setVisible(true);
	}

	private void importDirectory(File[] directory) {
		for (File file : directory) {
			if (cancel) {
				break;
			}
			importDialog.updateCurrentFile(FilenameUtils.normalize(file.getPath()));
			importFile(file);
			importDialog.updateProgress();
		}
	}

	private void importFile(File file) {
		// System.out.println("in import file:"+file);
		try {
			FileUserObject.SubType subType = SupportedFileTypes.isPDF(file.getAbsolutePath())
					? FileUserObject.SubType.PDF
							: FileUserObject.SubType.IMAGE;

			FileNodeFactory fileNodeFactory = NodeFactory.getFileNodeFactory(UserObjectType.REAL_FILE, subType);
			if (fileNodeFactory instanceof RealPdfNodeFactory) {
				((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsNew(autoRestrioctionsNew);
				((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsOverwrite(autoRestrictionsOverwrite);
			}
			fileNodeFactory.setOptimize(optimizePDF);
			fileNodeFactory.addPageEventListener(new PageEventListener() {
				
				public void pageCountChanged(int pages) {
					importDialog.setPageCount(pages);
				}

				
				public void nextPage(int page) {
					importDialog.updatePageProgress();
				}
			});
			Node node = fileNodeFactory.getFileNode(file.getAbsolutePath());
			if (node != null) {
				modelHandler.insertFileNode(node);
			}
		} catch (Exception ex) {
			Logger.getLogger(FileImporter.class.getName()).log(Level.SEVERE, null, ex);
			String exceptionTrace = getExceptionTrace(ex);
			errorHandler.reportError(file.getPath(), exceptionTrace);
		}
	}

	private String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}

	public void setReadPageSize(boolean readPageSizeSelected) {
		// TODO Auto-generated method stub

	}

}
