package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public abstract class Tab extends JPanel {

    public Tab(LayoutManager layout) {
        super(layout);
    }

    public abstract String getTabName();

    public void checkRun() throws IOException {
    }

    public abstract PDFTwist run(PDFTwist input, OutputEventListener outputEventListener) throws IOException, DocumentException;
}
