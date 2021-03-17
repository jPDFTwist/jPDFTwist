package jpdftweak;

import jpdftweak.gui.MainForm;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.JTextArea;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static final String VERSION = "1.1 Stage XIV";

	public static void main(final String[] args) {
			
		System.out.println("");
		
		final long heapSize = Runtime.getRuntime().totalMemory();
		System.out.println("Heap Size     =  " + heapSize / 1024L / 1024L + " MB");

		
//		final long freememory = Runtime.getRuntime().freeMemory();
//		System.out.println("Free Memory   =  " + freememory / 1024L / 1024L + " MB");
				
//		final long usedmemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		System.out.println("Used Memory   =  " + usedmemory / 1024L / 1024L + " MB");

		
		final long maxBytes = Runtime.getRuntime().maxMemory();
		System.out.println("Max Memory    =  " + maxBytes / 1024L / 1024L + " MB");
		
		
		System.out.println("");
		System.out.println("");
		
		final String missingLib = findMissingLibName();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {

		}

		if (missingLib != null) {
			JOptionPane.showMessageDialog(null, "The required file lib/" + missingLib
					+ ".jar could not be loaded.\nVerify that the file is present and your download was not corrupted.",
					"JPDFTweak", 0);
			return;
		}
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				e.printStackTrace();
				final JDialog exceptionDialog = new JDialog((Frame) null,
						"An unexpected error occurred while running jPDF Tweak.");
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				pw.write(
						"Please send this error report to <schierlm@users.sourceforge.net>.\nTry to provide specific information about when this error occured.\n\njPDF Tweak version: 1.1 Stage XIV\nJava version: "
								+ System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")\n"
								+ "Operating System: " + System.getProperty("os.name") + " ("
								+ System.getProperty("os.version") + ", " + System.getProperty("os.arch") + ")\n\n");
				e.printStackTrace(new PrintWriter(sw));
				final JTextArea jta = new JTextArea(sw.toString(), 20, 80);
				jta.setBackground(new Color(255, 187, 187));
				jta.setEditable(false);
				exceptionDialog.getContentPane().add(new JScrollPane(jta), "Center");
				exceptionDialog.pack();
				exceptionDialog.setLocationRelativeTo(null);
				exceptionDialog.setVisible(true);
			}
		});
		new MainForm();
	}

	private static String findMissingLibName() {
		String result = null;
		try {
			result = "itext";
			Class.forName("com.itextpdf.text.DocumentException");
			result = "forms";
			Class.forName("com.jgoodies.forms.layout.FormLayout");
			result = "bcprov";
			Class.forName("org.bouncycastle.asn1.ASN1OctetString");
			result = "bcmail";
			Class.forName("org.bouncycastle.cms.CMSEnvelopedData");
			result = "bctsp";
			Class.forName("org.bouncycastle.tsp.TSPException");
			result = null;
		}
		catch (Throwable t) {
		}
		return result;
		
		
	}
}