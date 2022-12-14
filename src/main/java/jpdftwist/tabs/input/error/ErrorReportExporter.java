package jpdftwist.tabs.input.error;

import java.util.HashMap;

/**
 * @author Vasilis Naskos
 */
public interface ErrorReportExporter {

    void setData(HashMap<String, String> exceptionsMap);

    void export(String output);

}
