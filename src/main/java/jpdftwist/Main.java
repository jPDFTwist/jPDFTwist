package jpdftwist;

import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;
import sun.management.ManagementFactoryHelper;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	public static final String VERSION = "1.1 Stage XVI";

	public static void main(final String[] args) {
        System.out.println("");

        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
        		java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        	//long physicalMemorySize = os.getTotalPhysicalMemorySize() / 1048576;
        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        	
            MemoryMXBean memBean = ManagementFactoryHelper.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memBean.getHeapMemoryUsage();
        	

        long committedHeap = heapMemoryUsage.getCommitted();    
        //final long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("Heap Size     =  " + committedHeap + " bytes");

        long maxHeap = heapMemoryUsage.getMax();
        //final long heapMaxSize = heapMemoryUsage.getMax();
        System.out.println("Heap Max      =  " + maxHeap + " bytes");
        
        System.out.println("Physical Max  =  " + physicalMemorySize + " bytes");												
        System.out.println("");
        System.out.println("");

        final String missingLib = findMissingLibName();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception Ex1) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception Ex2) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Ex083", Ex2);
            }
        }

	if(missingLib!=null)

	{
		JOptionPane.showMessageDialog(null, "The required file lib/" + missingLib
				+ ".jar could not be loaded.\nVerify that the file is present and your download was not corrupted.",
				"JPDFTwist", JOptionPane.ERROR_MESSAGE);
		return;
	}Thread.setDefaultUncaughtExceptionHandler((t,e)->
	{
		e.printStackTrace();
		final JDialog exceptionDialog = new JDialog((Frame) null,
				"An unexpected error occurred while running JPDFTwist.");
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		pw.write(
				"Please open a github issue at https://github.com/xlance-github/jPDFTwist\nTry to provide specific information about when this error occurred.\n\nJPDFTwist version: 1.1 Stage XIV\nJava version: "
						+ System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")\n"
						+ "Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.version")
						+ ", " + System.getProperty("os.arch") + ")\n\n");
		e.printStackTrace(new PrintWriter(sw));
		final JTextArea jta = new JTextArea(sw.toString(), 20, 80);
		jta.setBackground(new Color(255, 187, 187));
		jta.setEditable(false);
		exceptionDialog.getContentPane().add(new JScrollPane(jta), "Center");
		exceptionDialog.pack();
		exceptionDialog.setLocationRelativeTo(null);
		exceptionDialog.setVisible(true);
	});new MainWindow();
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
        } catch (Throwable t) {
        }
        return result;

    }
}