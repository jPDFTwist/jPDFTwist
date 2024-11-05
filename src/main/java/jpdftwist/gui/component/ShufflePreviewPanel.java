package jpdftwist.gui.component;

import jpdftwist.core.PageDimension;
import jpdftwist.core.PdfReaderManager;
import jpdftwist.core.ShuffleRule;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import com.itextpdf.text.Rectangle;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ShufflePreviewPanel extends JPanel 
{
	public PdfReaderManager pdfReaderManager;
    public ShufflePreviewPanel() {
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }
    
    private float pwidth = 1, pheight = 1, pwidthImp = 1, pheightImp = 1;
    private ShuffleRule[] rules = new ShuffleRule[0];

    public void setConfig(ShuffleRule[] rules) {
        this.rules = rules;
        repaint();
    }

    public void setPageFormat(PageDimension dimension) {
        pwidth = dimension.getWidth();
        pheight = dimension.getHeight();

//        try {
//        Rectangle sizeSH = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(1);
//        pwidthImp = sizeSH.getWidth();
//        pheightImp = sizeSH.getHeight();
//        } catch (Exception ex) {}
        
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Dialog", Font.PLAIN, 40));
        Graphics2D gg = (Graphics2D) g;
        
        int pw = getWidth() - 171;				//create space RIGHT
        int ph = (int) (pw * pheight / pwidth);
        int y = -ph + 80;						//create space DOWN

        for (ShuffleRule sr : rules) {
            if (sr.isNewPageBefore()) {
                y += ph + 5;
                g.setColor(Color.WHITE);
                g.fillRect(84, y - 1, pw + 2, ph + 2);
                g.setColor(Color.BLACK);
                g.drawRect(84, y - 1, pw + 2, ph + 2);
            }
            AffineTransform oldTransform = gg.getTransform();
            g.translate(85, y + ph);			//translate ORIGIN
            
            // Begin transform
            gg.rotate(Math.toRadians(sr.getRotateAngle()));
            double ox = sr.getOffsetX(), oy = sr.getOffsetY();
            
            if (sr.isOffsetXPercent())
                ox = (ox * pw / 100);
            else
                ox = (ox * pw / pwidth);
            
            if (sr.isOffsetYPercent())
                oy = (oy * ph / 100);
            else
                oy = (oy * ph / pheight);
            
            gg.scale(sr.getScale(), sr.getScale());
            gg.translate(ox, -oy);
            
            // End transform
            g.setColor(Color.CYAN);
            g.fillRect(0, -ph, pw, ph);
            g.setColor(Color.BLUE);
            g.drawRect(0, -ph, pw, ph);
            g.drawString(sr.getPageString(), 25, -ph + 55);
            gg.setTransform(oldTransform);
            
            // Cropmarks Coordinates Test
//            gg.drawLine((int)pwidth-11,18,(int)pwidth,18);
//            gg.drawLine((int)pwidth-18,(int)pheight-11,(int)pwidth-18,(int)pheight);
//            gg.drawLine(18,11,18,0);
//            gg.drawLine(11,(int)pheight-18,0,(int)pheight-18);
//            gg.drawLine(18,(int)pheight,18,(int)pheight-11);
//            gg.drawLine((int)pwidth,(int)pheight-18,(int)pwidth-11,(int)pheight-18);
//            gg.drawLine((int)pwidth-18,0,(int)pwidth-18,11);
//            gg.drawLine(0,18,11,18);
        }
    }
}
