package jpdftweak.tabs.input.error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author Vasilis Naskos
 */
public class XmlExporter implements ErrorReportExporter {

    private HashMap<String, String> exceptionsMap;
    private Document doc;
    
    @Override
    public void setData(HashMap<String, String> exceptionsMap) {
        this.exceptionsMap = exceptionsMap;
    }
    
    @Override
    public void export(String output) {
        try {
            saveXML(output);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, "Error Report Failed", "Cannot create error report file.",
                    JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(XmlExporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            JOptionPane.showMessageDialog(
                    null, "Error Report Failed", "XML Structure fail.",
                    JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(XmlExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveXML(String output) throws IOException, ParserConfigurationException {
        initialize();

        String outputName = output;
        if(!output.endsWith(".xml"))
            outputName += ".xml";
        
        FileWriter fileWriter = new FileWriter(outputName);
        BufferedWriter out = new BufferedWriter(fileWriter);
        out.write(format());
        out.close();
    }
    
    private void initialize() throws ParserConfigurationException {
        initializeDocument();
        initializeRootElement();
    }
    
    private void initializeDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    }
    
    private void initializeRootElement() {
        Element rootElement = doc.createElement("exceptions");
        doc.appendChild(rootElement);
        populateRoot(rootElement);
    }
    
    private void populateRoot(Element rootElement) {
        for (String key : exceptionsMap.keySet()) {
            String value = exceptionsMap.get(key);
            Element exceptionItem = constructExceptionItem(key, value);
            rootElement.appendChild(exceptionItem);
        }
    }
    
    private Element constructExceptionItem(String key, String value) {
        Element exceptionItem = doc.createElement("exception");
        Element filePath = doc.createElement("filepath");
        Element stackTrace = doc.createElement("trace");
        
        Text filepathNode = doc.createTextNode(key);
        filePath.appendChild(filepathNode);
        exceptionItem.appendChild(filePath);
        
        Text stackTraceNode = doc.createTextNode(value);
        stackTrace.appendChild(stackTraceNode);
        exceptionItem.appendChild(stackTrace);
        
        return exceptionItem;
    }
    
    private String format() throws IOException {
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        //format.setIndent(4);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);

        return out.toString();
    }
    
}
