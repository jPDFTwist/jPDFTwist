package jpdftweak.tabs.input.error;

import java.util.HashMap;

/**
 *
 * @author Vasilis Naskos
 */
public interface ErrorReportExporter {
    
    public void setData(HashMap<String, String> exceptionsMap);
    public void export(String output);
    
}
