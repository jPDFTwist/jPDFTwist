package jpdftwist.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachmentsManager {

    private List<File> attachments = new ArrayList<>();

    public void addFile(File f) {
        attachments.add(f);
    }

    public List<File> getAttachments() {
        return attachments;
    }
}
