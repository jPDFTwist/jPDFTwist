package jpdftwist.tabs.input;

import jpdftwist.utils.SupportedFileTypes;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Vasilis Naskos
 */
public class DirectoryScanner {

    private final ArrayList<File> rootDir = new ArrayList<>();

    public ArrayList<File[]> getFiles(File[] selectedFiles) {
        ArrayList<File[]> files = new ArrayList<>();

        for (File file : selectedFiles)
            files.addAll(getChildren(file));

        files.add(rootDir.toArray(new File[]{}));

        return files;
    }

    private ArrayList<File[]> getChildren(File parentFile) {
        ArrayList<File[]> files = new ArrayList<>();

        if (isDirectory(parentFile))
            files.addAll(scanDirectory(parentFile));
        else if (isSupportedFile(parentFile))
            rootDir.add(parentFile);

        return files;
    }

    private boolean isDirectory(File file) {
        return file.isDirectory();
    }

    private boolean isSupportedFile(File file) {
        boolean isFile = file.isFile();
        boolean isSupported = SupportedFileTypes.isSupported(file);

        return isFile && isSupported;
    }

    private ArrayList<File[]> scanDirectory(File parentFile) {
        ArrayList<File[]> directoryFiles = new ArrayList<>();

        File[] currentDirFiles = getFilesFromDirectory(parentFile);
        if (currentDirFiles.length != 0)
            directoryFiles.add(currentDirFiles);

        File[] subDirectories = getDirectoriesFromDirectory(parentFile);
        for (File subDir : subDirectories)
            directoryFiles.addAll(scanDirectory(subDir));

        return directoryFiles;
    }

    private File[] getFilesFromDirectory(File directory) {
        return directory.listFiles((dir, name) -> {
            String extension = SupportedFileTypes.getFileExtension(name);
            return SupportedFileTypes.isSupported(extension);
        });
    }

    private File[] getDirectoriesFromDirectory(File directory) {
        return directory.listFiles((dir, name) -> {
            String filePath = dir.getPath() + File.separator + name;
            File checkFile = new File(filePath);
            return checkFile.isDirectory();
        });
    }

}
