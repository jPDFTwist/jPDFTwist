package jpdftwist.core;

import java.io.File;
import java.util.List;

public class OutputFilePathManager {

    private final List<PageRange> pageRanges;
    private final String inputFilePath;
    private final String inputFileName;
    private final String inputFileFullName;
    private final String rootFolder;
    private final boolean mergeByDir;

    public OutputFilePathManager(final List<PageRange> pageRanges, final boolean mergeByDir, String rootDir) {
        this.pageRanges = pageRanges;
        this.mergeByDir = mergeByDir;

        this.inputFilePath = pageRanges.get(0).getParentName();
        this.inputFileFullName = pageRanges.get(0).getFilename();
        int pos = inputFileFullName.lastIndexOf('.');
        if (pos == -1) {
            inputFileName = inputFileFullName;
        } else {
            inputFileName = inputFileFullName.substring(0, pos);
        }

        rootFolder = rootDir;
    }

    public String expandOutputPath(final String rawOutputFile) {
        String outputFile = rawOutputFile;

        if (!outputFile.contains(File.separator)) {
            File temp = new File(outputFile);
            outputFile = temp.getAbsolutePath();
        }

        String outpath = inputFilePath;
        outpath = outpath.replace(rootFolder, "");
        if (outpath.contains(":") && System.getProperty("os.name").toLowerCase().contains("win")) {
            outpath = outpath.replace(":", "");
            outpath = File.separator + outpath;
        }

        outputFile = outputFile.replace("<T>", outpath);

        if (mergeByDir) {
            outpath = outpath.replace(File.separatorChar, '_');
            outputFile = outputFile.replace("<F>", outpath);
            outputFile = outputFile.replace("<FX>", outpath + ".pdf");
        } else {
            outputFile = outputFile.replace("<F>", inputFileName);
            outputFile = outputFile.replace("<FX>", inputFileFullName);
        }

        outputFile = outputFile.replace("<P>", inputFilePath);
        if (outputFile.contains("<#>")) {
            for (int i = 1; ; i++) {
                String f = outputFile.replace("<#>", "" + i);
                if (!new File(f).exists()) {
                    outputFile = f;
                    break;
                }
            }
        }

        return outputFile;
    }
}
