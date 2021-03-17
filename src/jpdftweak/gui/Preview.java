package jpdftweak.gui;

import com.jgoodies.forms.layout.CellConstraints;

import com.jgoodies.forms.layout.FormLayout;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import jpdftweak.tabs.input.treetable.TreeTableComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import javax.swing.border.BevelBorder;


public class Preview extends JPanel {
	
   // public static final int INCH = Toolkit.getDefaultToolkit(). getScreenResolution();
    public static int INCH =72;
    public static final int SIZE = 45;
    private int height;
    private static DefaultListModel listModel= new DefaultListModel();
    private static DefaultListModel model;
    private  JList<JLabel> list;
    private Dimension dim;
    private JScrollPane jsp;
    private final float mmPerPixel = (25.4f/ Toolkit.getDefaultToolkit().getScreenResolution());
    private float scaleValue;
    private int increment;
    private static int units;
    private JButton zoomOut;
    private JButton zoomIn;
    private JButton reset;
    private  JComboBox c ;
    private static int hght;
    private static int wdth;
    private static int orghght;
    private static int orgwdth;
    private static PDFPage newPage;
    private ImageIcon image;
    private static double prevzoom;
    private static double zoom;
    private static  boolean ispercent;
    private String[] values;
    private static int orgrwidth;
    private	static int orgheight;
   private static Rectangle rect;
   private static Image zoomImage;
   // private static JLabel label;
   
    
    public Preview()
    {
    	setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
    	
    }

	public Preview(Dimension d,Image img,int rwidth,int rheight,PDFPage page) {
    	super(new BorderLayout());
        //super.setLayout((LayoutManager)(new FormLayout("f:p:g", "f:p:g")));
    	orgrwidth=rwidth;
       	orgheight=rheight;
       	//System.out.println("original width:"+orgrwidth);
     	//System.out.println("original height:"+orgheight);
         newPage=page;
 
    	prevzoom=0.0;
    	zoom=1.0;
    	ispercent=false;
    	clearPreview();
       
    	//System.out.println("image:"+img.toString());
    	//System.out.println("rwidth:"+rwidth);
    	//System.out.println("rheight:"+rheight);
    	
    	image=new ImageIcon(img);
    	 zoomOut=new JButton("-");
    	 zoomOut.setToolTipText("Zoom out");
    	 
    	 zoomIn=new JButton("+");
    	 zoomIn.setToolTipText("Zoom in");
    	 
    	 reset=new JButton("Fit to Window");
    	 reset.setToolTipText("Default preview");
    	 
    	 //values=zoomPercentages();
    	 // c = new JComboBox(zoomPercentages());
    	 // c=new JComboBox(new String[]{"10%","20%","25%","50%","75%","100%"});
    	  c=new JComboBox();
    	  c.addItem("1%");
    	  c.addItem("2%");
    	  c.addItem("3%");
    	  c.addItem("4%");
    	  c.addItem("5%");
    	  c.addItem("10%");
    	  c.addItem("20%");
    	  c.addItem("25%");
    	  c.addItem("50%");
    	  c.addItem("75%");
    	  c.addItem("100%");
    	  
    	  c.setEditable(true);
    	  c.setToolTipText("Zoom");
    	  
    	 wdth=image.getIconWidth();
    	 hght=image.getIconHeight();
    	 
    	 orgwdth=wdth;
    	 orghght=hght;
    	 
    	 if(orgwdth==orgrwidth)
  	   {
  		  // System.out.println("in 100%");
  		   String value="100%";
  		   for(int i=0;i<c.getModel().getSize();i++)
  		   {
  			   if(c.getItemAt(i).toString().equals(value))
  			   {
  				  // System.out.println("in combo if..");
  				   c.setSelectedIndex(i);
  				   break;
  			   }
  		   }
  		  // c.setSelectedIndex(2);
  		// c.setSelectedItem("25%");
  	   }
  	
    	 if(orgwdth*2==orgrwidth)
    	   {
    		  // System.out.println("in 50%");
    		   String value="50%";
    		   for(int i=0;i<c.getModel().getSize();i++)
    		   {
    			   if(c.getItemAt(i).toString().equals(value))
    			   {
    				  // System.out.println("in combo if..");
    				   c.setSelectedIndex(i);
    				   break;
    			   }
    		   }
    		  // c.setSelectedIndex(2);
    		// c.setSelectedItem("25%");
    	   }
    	 
    	 if(orgwdth*4==orgrwidth)
  	   {
  		   //System.out.println("in 25%");
  		   String value="25%";
  		   for(int i=0;i<c.getModel().getSize();i++)
  		   {
  			   if(c.getItemAt(i).toString().equals(value))
  			   {
  				  // System.out.println("in combo if..");
  				   c.setSelectedIndex(i);
  				   break;
  			   }
  		   }
  		  // c.setSelectedIndex(2);
  		// c.setSelectedItem("25%");
  	   }
    	 
    	 if (orgrwidth>1440)
  	   {
  		   //System.out.println("in 10%");
  		   String value="10%";
  		   for(int i=0;i<c.getModel().getSize();i++)
  		   {
  			   if(c.getItemAt(i).toString().equals(value))
  			   {
  				  // System.out.println("in combo if..");
  				   c.setSelectedIndex(i);
  				   break;
  			   }
  		   }
  		  // c.setSelectedIndex(2);
  		// c.setSelectedItem("25%");
  	   }
    	 
    	 if (orgrwidth>3600)
    	   {
    		  // System.out.println("in 5%");
    		   String value="5%";
    		   for(int i=0;i<c.getModel().getSize();i++)
    		   {
    			   if(c.getItemAt(i).toString().equals(value))
    			   {
    				  // System.out.println("in combo if..");
    				   c.setSelectedIndex(i);
    				   break;
    			   }
    		   }
    		  // c.setSelectedIndex(2);
    		// c.setSelectedItem("25%");
    	   }
    	 
    	 if (orgrwidth>6400)
    	   {
    		  // System.out.println("in 4%");
    		   String value="4%";
    		   for(int i=0;i<c.getModel().getSize();i++)
    		   {
    			   if(c.getItemAt(i).toString().equals(value))
    			   {
    				  // System.out.println("in combo if..");
    				   c.setSelectedIndex(i);
    				   break;
    			   }
    		   }
    		  // c.setSelectedIndex(2);
    		// c.setSelectedItem("25%");
    	   }
      	 
    	 if (orgrwidth>14400)
  	   {
  		   //System.out.println("in 2%");
  		   String value="2%";
  		   for(int i=0;i<c.getModel().getSize();i++)
  		   {
  			   if(c.getItemAt(i).toString().equals(value))
  			   {
  				  // System.out.println("in combo if..");
  				   c.setSelectedIndex(i);
  				   break;
  			   }
  		   }
  		  // c.setSelectedIndex(2);
  		// c.setSelectedItem("25%");
  	   }
    	 
    	 if (orgrwidth>36000)
    	   {
    		  // System.out.println("in 1%");
    		   String value="1%";
    		   for(int i=0;i<c.getModel().getSize();i++)
    		   {
    			   if(c.getItemAt(i).toString().equals(value))
    			   {
    				  // System.out.println("in combo if..");
    				   c.setSelectedIndex(i);
    				   break;
    			   }
    		   }
    		  // c.setSelectedIndex(2);
    		// c.setSelectedItem("25%");
    	   }
    	
      	
  	  
       
     
    	 
    	 dim = d;
        CellConstraints cc = new CellConstraints();
        height = d.height;
        
        //listModel = new DefaultListModel();
        JLabel label=new JLabel(image);
        
       // System.out.println("before zoom out");
        //listModel=new DefaultListModel();
        listModel.addElement(label);
        //System.out.println("list element:"+listModel.get(0));
      // System.out.println("size:"+this.listModel.getSize());
        
        list = new JList<>(listModel);
        
        myRenderer r = new myRenderer();
        list.setCellRenderer(r);
        list.setBackground(Color.gray);
        jsp = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      //  jsp.setLayout(new ScrollPaneLayout());
       
       createRuler(jsp,label,rwidth,rheight);
        
        
        //createRuler(jsp,(JLabel)listModel.getElementAt(0),rwidth,rheight);

        jsp.setBackground(Color.gray);
        jsp.getVerticalScrollBar().setUnitIncrement(8);
       // jsp.setMaximumSize(new Dimension((int)label.getPreferredSize().getWidth()/10,(int)label.getPreferredSize().getHeight()/10));
       jsp.setPreferredSize(new Dimension((int)label.getPreferredSize().getWidth()/10,(int)label.getPreferredSize().getHeight()/10));
        jsp.setViewportBorder(BorderFactory.createLineBorder(Color.black));
        add(jsp,BorderLayout.CENTER);
        JPanel btnpanel=new JPanel();
        btnpanel.setLayout(new FlowLayout());
        btnpanel.add(zoomIn);
        btnpanel.add(zoomOut);
        btnpanel.add(c);
        btnpanel.add(reset);
        add(btnpanel,BorderLayout.PAGE_END);

        
        this.zoomIn.addActionListener(new ActionListener() {
    		public void actionPerformed(final ActionEvent e) {
    			//Image newImage=new Image();
    			
    		//	System.out.println("Zoom in");
    			DefaultListModel model= new DefaultListModel();
    			model.addElement(listModel.get(0));
    			clearPreview();
    			
    			if(model.getSize()>0)
    			{
    				JLabel newlabel=(JLabel)model.get(0);
    				//System.out.println("newlabel:"+newlabel.toString());
    			
    				Icon icon=newlabel.getIcon();
    				
    				if (icon instanceof ImageIcon) {
    					// System.out.println("inside icon if..");
    					  ImageIcon imageIcon = (ImageIcon) icon;
    					  Image newimage = imageIcon.getImage();
    					  setIspercent(false);
    					  zoom(1.5,newimage,rwidth,rheight,d,newPage,isIspercent(),image);
    		    			 setZoom(1.5);
    					  
    					} 
    				
    				
    			}
    			//zoom(1.5,newimage,rwidth,rheight,d,page);
    			// setZoom(1.5);
    			// System.out.println("zoom:"+getZoom());
    	        
    				}
    		
    	});
        
      
		
        
       
        
        this.zoomOut.addActionListener(new ActionListener() {
    		public void actionPerformed(final ActionEvent e) {
    			
    			//System.out.println("Zoom out");
    			DefaultListModel model= new DefaultListModel();
    			model.addElement(listModel.get(0));
    			clearPreview();
    			
    			if(model.getSize()>0)
    			{
    				JLabel newlabel=(JLabel)model.get(0);
    				//System.out.println("newlabel:"+newlabel.toString());
    			
    				Icon icon=newlabel.getIcon();
    				
    				if (icon instanceof ImageIcon) {
    					//System.out.println("inside icon if..");
    					  ImageIcon imageIcon = (ImageIcon) icon;
    					  Image newimage = imageIcon.getImage();
    					  setIspercent(false);
    					  zoom(0.5,newimage,rwidth,rheight,d,newPage,isIspercent(),image);
    		    			 setZoom(0.5);
    					  
    					} 
    				
    				
    			}
    			// System.out.println("zoom:"+getZoom());

    			
		}
    		
    	});
        
        
        
        
        this.c.addActionListener(new ActionListener() {
    		public void actionPerformed(final ActionEvent e) {
    			//Image newImage=new Image();
    		
    			//System.out.println("Zoom %");
    			//DefaultListModel model= new DefaultListModel();
    			model=new DefaultListModel();
    			model.addElement(listModel.get(0));
    			clearPreview();
    			
    			if(model.getSize()>0)
    			{
    				JLabel newlabel=(JLabel)model.get(0);
    				//System.out.println("newlabel:"+newlabel.toString());
    			
    				Icon icon=newlabel.getIcon();
    				
    				if (icon instanceof ImageIcon) {
    					//System.out.println("inside icon if..");
    					  ImageIcon imageIcon = (ImageIcon) icon;
    					  Image newimage = imageIcon.getImage();
    					  String zoomvalue=(String)c.getSelectedItem();
    					  String trimmedString;
    					  int l=zoomvalue.indexOf("%");
    					  trimmedString=zoomvalue.substring(0, l);
    					  double value=0;
    					  value=Double.parseDouble(trimmedString);
    					  
    					 /** if(rwidth==72000)
    					  {
    						  value=value/2;
    					  }
    					  
    					 if(image.getIconWidth()*2==rwidth)
    					  {
    					     System.out.println("iconwidth==rwidth");
    						  value=value/2;
    					  }
    					  
    					 //else {
    					  value=value/10;
    					// }**/
    					  setIspercent(true);
    					  zoom(value,newimage,orgrwidth,orgheight,d,newPage,isIspercent(),image);
    					  //setZoom(value);
    					   
    					} 
    				
    				
    			}
    			
    			// System.out.println("zoom:"+getZoom());
    	        
    				}
    		
    	});
        
        
        
        this.reset.addActionListener(new ActionListener() {
    		public void actionPerformed(final ActionEvent e) {
    			//System.out.println("in fit to window");
    			
    			 if(orgwdth*2==orgrwidth)
    			   {
    	  		   c.setSelectedItem("50%");
    			   }
    	  	   if(orgwdth*4==orgrwidth)
    	  	   {
    	  		 c.setSelectedItem("25%");
    	  	   }
    	  	   if (orgrwidth>1440)
    	  	   {
    	  		 c.setSelectedItem("10%");     
    	  	   }
    	  	 if (orgrwidth>3600)
  	  	   {
  	  		 c.setSelectedItem("5%");     
  	  	   }
    	  	if (orgrwidth>6400)
   	  	   {
   	  		 c.setSelectedItem("4%");     
   	  	   }
    		
    		if (orgrwidth>14400)
    	  	   {
    	  		 c.setSelectedItem("2%");     
    	  	   }
    		
    		if (orgrwidth>36000)
 	  	   {
 	  		 c.setSelectedItem("1%");     
 	  	   }
 	  	
    	  	prevzoom=0.0;
    	    	zoom=1.0;

    		}
    		
    	});
        

        
   }
	
	 private void createRuler(JScrollPane scrollPane,JLabel label,int rwidth,int rheight) {
		   
			
		// System.out.println("in create ruler");
		 
		 		JLabel[] corners = new JLabel[4];
		       for (int i = 0; i < 4; i++) {
		           corners[i] = new JLabel();
		           corners[i].setBackground(Color.lightGray);
		           corners[i].setOpaque(true);
		       }
		       JLabel rowheader = new JLabel() {
		           public void paintComponent(Graphics g) {
		               super.paintComponent(g);
		               Rectangle rect = g.getClipBounds();
		               g.setFont(new Font("SansSerif", Font.PLAIN, 10));
		               
		               g.setColor(Color.black);

		              /** for (int i = 10 - (rect.y % 10); i < rect.height; i += 10)
		               	
		               {
		                   g.drawLine(0, rect.y + (int) (i / mmPerPixel), 3, rect.y + (int) (i / mmPerPixel));
		                   g.drawString("" + (rect.y + i), 6, rect.y + (int) (i / mmPerPixel) + 3);
		                   
		                   
		               }**/
		               units = INCH;
		              // System.out.println("units in create ruler:"+units);
		              // System.out.println("rwidth:"+rwidth);
		              // System.out.println("rheight:"+rheight);
		           	  
		              // System.out.println("icon width:"+wdth);

		               increment = units / 2;
		               int tickLength = 0;
		               String text = null;
		               int start=0;
		               int end=0;
		               scrollRectToVisible(rect);
		               start = (rect.y / increment) * increment;
		              // System.out.println("start in create ruler:"+start);
		               end = (((rect.y + rect.height) / increment) + 1) * increment;
		              // System.out.println("end in create ruler:"+end);
		               if (start == 0) {
		            	  // System.out.println("in start=0 of create ruler");
		               	text = Integer.toString(0) ;
		                   
		               	tickLength = 10;
		               	g.drawLine(SIZE-1, 0, SIZE-tickLength-1, 0);
		                   
		               	g.drawString(text, 9, 10);

		               	text = null;
		                   
		               	start = increment;
		               }
		               
		               
		              
		              
		              for (int i = start; i < end; i += increment) {
		           	   if (i % units == 0)  {
		                      
		           		    tickLength = 10;
		           		    
		           		  // if((int)label.getPreferredSize().getHeight()*2==rheight)
		           		   // System.out.println("org height in create ruler:"+orgheight);
		           			  
		           		 if(hght==orgheight)
		           		   {
		           			  // System.out.println("in multiples of 2..");
		           			   //System.out.println("text:"+i/units*2);
		           		       text = Integer.toString(i/units*1);
		           		   // System.out.println("text:"+text);
		           		   }
		           		    
		           		    if(hght*2==orgheight)
		           		   {
		           			  // System.out.println("in multiples of 2..");
		           			   //System.out.println("text:"+i/units*2);
		           		       text = Integer.toString(i/units*2);
		           		   // System.out.println("text:"+text);
		           		   }
		           		
		           		    
		           		//else if(hght*4==rheight || (hght>=288 && hght<=342))  
		           			else if(hght*4==orgheight )
		           		   {
		           			   //System.out.println("in multiples of 2..");
		           			   //System.out.println("text:"+i/units*2);
		           		       text = Integer.toString(i/units*4);
		           		       
		           		   // System.out.println("text:"+text);
		           		   }
		           			   
		           			else if(orgrwidth==72000 && orgheight==72000)
			           		  {
			           			text = Integer.toString(i/units*200); 
			           		  }
			           		
		           			  
		           			   
		           			else if(orgheight>36000)
			           		   {
			           		//	   System.out.println("in multiples of 100..");
			           			   text = Integer.toString(i/units*100);
			          
			           			   
			           		   }
			          
		           			else if(orgheight>14400)
			           		   {
			           	//		   System.out.println("in multiples of 50..");
			           			   text = Integer.toString(i/units*50);
			          
			           			   
			           		   }
			          
		           		    
		           			else if(orgheight>6400)
			           		   {
			           	//		   System.out.println("in multiples of 30..");
			           			   text = Integer.toString(i/units*25);
			          
			           			   
			           		   }
			          
		           		//else if(rheight>3600 || (hght>=144 && hght<=504 ) )
		           			else if(orgheight>3600)
		           		   {
		           			  // System.out.println("in multiples of 20..");
		           			   text = Integer.toString(i/units*20);
		          
		           			   
		           		   }
		          
		           		  // else if(rheight>1440 || (hght>=144 && hght<=504 ) ) 
		           			 else if(orgheight>1440 )
		           		   {
		           		//	   System.out.println("in multiples of 10..");
		           			   text = Integer.toString(i/units*10);
		          
		           			   
		           		   }
		           		
		           		
		           		 
		           		 
		           		  
		           		             
		           	   } 
		           	   
		           		else {
		           		                
		           		 tickLength = 7;
		           		                
		           	     text = null;
		           		      }
		           	   
		                  if (tickLength != 0) {
		               	   g.drawLine(SIZE-1, i, SIZE-tickLength-1, i);
		               	   if(text!=null)
		               	   {
		               	//	   System.out.println("text in if:"+text);
		               	   g.drawString(text, 9, i+3);
		               	   }
		               	   
		                  }
		                  
		           	   
		              }
		               	
		             
		           }

		          public Dimension getPreferredSize() {
		              // return new Dimension(SIZE,(int) dim.getHeight());
		       	   
		           	return new Dimension(SIZE,(int)label.getPreferredSize().getHeight());
		           	//return new Dimension(SIZE,(int)label.getHeight());
		           }
		       };
		       
		       rowheader.setBackground(Color.lightGray);
		       rowheader.setOpaque(true);
		      

		       JLabel columnheader = new JLabel() {
		           public void paintComponent(Graphics g) {
		               super.paintComponent(g);
		               units = INCH;
		               increment = units / 2;
		               Rectangle r = g.getClipBounds();
		               //Graphics2D g2 = (Graphics2D) g;
		               //AffineTransform rigthRot = AffineTransform.getQuadrantRotateInstance(1);
		              // g2.setFont(g2.getFont().deriveFont(rigthRot));
		              
		               int tickLength = 0;
		               
		               String text = null;
		               int start=0;
		               int end=0;
		               
		               start = (r.x / increment) * increment;
		               
		               end = (((r.x + r.width) / increment) + 1) * increment;
		               
		               if (start == 0) {
		               	text = Integer.toString(0) ;
		                   
		               	tickLength = 10;
		               	g.drawLine(0, SIZE-1, 0, SIZE-tickLength-1);
		                   
		               	g.drawString(text, 2, 21);
		               	text = null;
		                   
		               	start = increment;
		               }

		               
		                   
		              /** for (int i = 10 - (r.x % 10); i < r.width; i += 10) {
		                   g.drawLine(r.x + (int) (i / mmPerPixel), 0, r.x + (int) (i / mmPerPixel), 3);
		                   g2.drawString("" + (r.x + i), r.x + (int) (i / mmPerPixel) - 4, 6);
		               }**/
		              for (int i = start; i < end; i += increment) {
		           	   if (i % units == 0)  {
		                      
		           		    tickLength = 10;
		           		    
		           		    
		           		  //if(label.getPreferredSize().getWidth()*2==rwidth)  
		           		  if(wdth==orgrwidth)
		           		   {
		           		   text = Integer.toString(i/units*1);
		           		   }
		           		
		           		    
		           		    if(wdth*2==orgrwidth)
		           		   {
		           		   text = Integer.toString(i/units*2);
		           		   }
		           		
		           		//else if(wdth*4==rwidth || (wdth>=288 && wdth<=342)) 
		           			else if(wdth*4==orgrwidth)
		           		   {
		           			   //System.out.println("in multiples of 2..");
		           			   //System.out.println("text:"+i/units*2);
		           		       text = Integer.toString(i/units*4);
		           		   // System.out.println("text:"+text);
		           		   }
		           			  
		           		 else if(orgrwidth==72000 && orgheight==72000)
		           		  {
		           			text = Integer.toString(i/units*200); 
		           		  }
		           		
		           			 
		           			  
		           			else if(orgrwidth>36000)
			           		   {
			           		//	   System.out.println("in multiples of 100..");
			           			   text = Integer.toString(i/units*100);
			          
			           			   
			           		   }
			          
		           			else if(orgrwidth>14400)
			           		   {
			           	//		   System.out.println("in multiples of 50..");
			           			   text = Integer.toString(i/units*50);
			          
			           			   
			           		   }
			          
		           			else if(orgrwidth>6400)
			           		   {
			           		//	   System.out.println("in multiples of 25..");
			           			   text = Integer.toString(i/units*25);
			          
			           			   
			           		   }
			          
		           		
		           			else if(orgrwidth>3600)
			           		   {
			           			  // System.out.println("in multiples of 20..");
			           			   text = Integer.toString(i/units*20);
			          
			           			   
			           		   }
			          
			          
		           		    
		           		// else if (rwidth>1440 || (wdth>=144 && wdth<=504)) 
		           			 else if (orgrwidth>1440)
		           		 {
		           			   text = Integer.toString(i/units*10);
		           		   }
		           		 
		           		   
		           		 
		           		
		           		   
		           		            } 
		           		else {
		           		                
		           		 tickLength = 7;
		           		                
		           	     text = null;
		           		            }
		                  if (tickLength != 0) {
		               	   g.drawLine(i, SIZE-1, i, SIZE-tickLength-1);
		               	   
		               	   if (text != null)
		               	                          
		               	  g.drawString(text+"", i-3, 21);
		               	    }
		                  
		           	   
		              }
		               	
		             
		          
		           }

		           public Dimension getPreferredSize() {
		              // return new Dimension((int) dim.getWidth() SIZE);
		           	return new Dimension((int)label.getPreferredSize().getWidth(), SIZE);
		           	//return new Dimension((int)label.getWidth(), SIZE);
		           }
		       };
		       columnheader.setBackground(Color.lightGray);
		       columnheader.setOpaque(true);
		       columnheader.setAutoscrolls(true);
		       
		       scrollPane.setRowHeaderView(rowheader);
		       scrollPane.setColumnHeaderView(columnheader);
		       scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, corners[0]);
		       scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corners[1]);
		       scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corners[2]);
		       scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corners[3]);
		   }

	 
	 
    public void clearPreview() {
    	
    	if(listModel!=null)
    	{
    	if(!listModel.isEmpty())
    	{
    		//System.out.println("in clear preview if..");
        listModel.clear();
        
    	}
    	}
    }

    
    public class myRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(((JLabel) value).getIcon());
            label.setText(null);
            return label;
        }
    }
    
   
   private void zoom(double zoomFactor,Image img,int rwidth,int rheight,Dimension d,PDFPage page,boolean isPercent,ImageIcon image){
   
	   
	   
	  /**if (isPercent==true && rwidth==72000)
			  
		  { System.out.println("in value/200");
			  wdth=(int)((rwidth/200)*zoomFactor);
			  hght=(int)((rheight/200)*zoomFactor);
		  
		  }
		
	    else if(isPercent==true && image.getIconWidth()*2!=rwidth)
	    {
		  System.out.println("in value/10");
		  wdth=(int)((rwidth/10)*zoomFactor);
		  hght=(int)((rheight/10)*zoomFactor);
	    }
	  
	  else if(isPercent==true && image.getIconWidth()*2==rwidth)
	  { System.out.println("in value/2");
		  wdth=(int)((rwidth/2)*zoomFactor);
		  hght=(int)((rheight/2)*zoomFactor);
	 }**/
	   if(isPercent==true)
	   {
		   wdth=(int)((rwidth*zoomFactor)/100);
			  hght=(int)((rheight*zoomFactor)/100);
			  //System.out.println("width in ispercent:"+wdth);
			 // System.out.println("Height in ispercent:"+hght);
	   }
	  
	 else {
		 
	   wdth=(int) (wdth*zoomFactor);
		hght=(int) (hght*zoomFactor);
	  }
		 
		
		JLabel label=new JLabel(image);
		//System.out.println("label width:"+label.getPreferredSize().getWidth());
		//System.out.println("label Height:"+label.getPreferredSize().getHeight());
		//System.out.println("icon width:"+wdth);
		//System.out.println("icon height:"+hght);
		//label.setPreferredSize(new Dimension(wdth,hght));;
		//System.out.println("new label width:"+label.getPreferredSize().getWidth());
		//System.out.println("new label Height:"+label.getPreferredSize().getHeight());
		
		
		  rect = new Rectangle(0, 0,rwidth, rheight);			
         zoomImage=page.getImage(wdth, hght, rect, null,true,true);
		
		//Image scaled=img.getScaledInstance(wdth, hght, Image.SCALE_AREA_AVERAGING);
		
		Image scaled=zoomImage.getScaledInstance(wdth, hght, Image.SCALE_SMOOTH);
		//Image scaled=img.getScaledInstance(wdth, hght, Image.SCALE_SMOOTH);
		//ImageIcon scaledIcon=new ImageIcon(scaled);
	   BufferedImage resizedImage = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
	   
         
     Graphics2D g = resizedImage.createGraphics();
   
     
     // g.drawString("JAVA", 10, 10);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
       g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
       g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
       g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);  
       g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
      
       
    //   g.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
        //ImageIcon imgicon=new ImageIcon(resizedImage);
       
     g.drawImage(zoomImage, 0, 0, zoomImage.getWidth(null) , zoomImage.getHeight(null) , null);
    // g.drawImage(scaled, 0, 0, scaled.getWidth(null) , scaled.getHeight(null) , null);
      // g.drawImage(img, 0, 0, resizedImage.getWidth() , resizedImage.getHeight() , null);
      

       repaint();
       g.dispose();
       
     /**  RenderedImage rimage=(RenderedImage)resizedImage;
       try {
			ImageIO.write(rimage, "png", new File("C:\\output.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
     
       
       
      // BufferedImage better = rescaled(resizedImage, AffineTransformOp.TYPE_BICUBIC, 1.5);
       
        JLabel label1 = new JLabel(new ImageIcon(zoomImage));
    //  JLabel label1 = new JLabel(new ImageIcon(resizedImage));

      // JLabel label1 = new JLabel();
     //label1.setPreferredSize(new Dimension(zoomImage.getWidth(null),zoomImage.getHeight(null)));
     //label1.setIcon(new ImageIcon(zoomImage));
      // JLabel label1 = new JLabel(scaledIcon);
     // JLabel label1 = new JLabel(new ImageIcon(getClass().getResource("C:\\output.png")));
      /** Font font = label1.getFont();
       FontMetrics fm = label1.getFontMetrics(font);
       int width = label1.getWidth();
       int height = label1.getHeight();
       int textWidth = fm.stringWidth(label1.getText());
       int textHeight = fm.getHeight();

       int size = font.getSize();
       while (textHeight < height && textWidth < width) {
           size += 2;
           font = font.deriveFont(font.getStyle(), size);
           fm = label1.getFontMetrics(font);
           textWidth = fm.stringWidth(label1.getIcon().toString());
           textHeight = fm.getHeight();
       }

       label1.setFont(font);**/
           
          listModel.addElement(label1);
           /**list = new JList<>(listModel);
           myRenderer r = new myRenderer();
           list.setCellRenderer(r);
           list.setBackground(Color.gray);
          jsp = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
           		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);**/
         // updateRuler(jsp,label1,wdth,hght,zoomFactor);
          updateRuler(jsp,label1,rwidth,rheight,zoomFactor,isPercent);
       /** jsp.setBackground(Color.gray);
          jsp.getVerticalScrollBar().setUnitIncrement(20);
          jsp.getHorizontalScrollBar().setUnitIncrement(20);
          jsp.setPreferredSize(d);
          jsp.setViewportBorder(BorderFactory.createLineBorder(Color.black));
          add(jsp,BorderLayout.CENTER);**/
		

	   
   }
   
   
   
   
   
   private void updateRuler(JScrollPane scrollPane,JLabel labelnew,int rwidth,int rheight,double zoomfactor,boolean ispercent) {
   	//System.out.println("in update ruler");
   	
   
   	//System.out.println("label width:"+labelnew.getPreferredSize().getWidth());
       JLabel[] corners = new JLabel[4];
       for (int i = 0; i < 4; i++) {
           corners[i] = new JLabel();
           corners[i].setBackground(Color.lightGray);
           corners[i].setOpaque(true);
       }
       JLabel rowheader = new JLabel() {
           public void paintComponent(Graphics g) {
               super.paintComponent(g);
               Rectangle rect = g.getClipBounds();
               
               g.setFont(new Font("SansSerif", Font.PLAIN, 10));
               
               g.setColor(Color.black);

              /** for (int i = 10 - (rect.y % 10); i < rect.height; i += 10)
               	
               {
                   g.drawLine(0, rect.y + (int) (i / mmPerPixel), 3, rect.y + (int) (i / mmPerPixel));
                   g.drawString("" + (rect.y + i), 6, rect.y + (int) (i / mmPerPixel) + 3);
                   
                   
               }**/
          if(isIspercent()==true)
          {
        	  
        	  if (orgheight<360)
          	   {
          		   units=(int) ((int)(INCH*zoomfactor)/100);   
          	   }
        	  
        	  if(orghght*2==orgheight)
    		   {
       		   units=(int) ((int)(INCH*zoomfactor)/50);
       		  
    		   }
       	   if(orghght*4==orgheight)
       	   {
       		   units=(int) ((int)(INCH*zoomfactor)/25);
       	   }
       	   if (orgheight>1440 )
       	   {
       		   units=(int) ((int)(INCH*zoomfactor)/10);   
       	   }
       	  
       	   if (orgheight>3600 )
       	   {
       		   units=(int) ((int)(INCH*zoomfactor)/5);   
       	   }
        	if (orgrwidth>6400)
 	       {
 		   units=(int) ((int)(INCH*zoomfactor)/4);   
 	       }
        	if (orgrwidth>14400)
  	       {
  		   units=(int) ((int)(INCH*zoomfactor)/2);   
  	       }
        	if (orgrwidth>36000)
   	       {
   		   units=(int) ((int)(INCH*zoomfactor)/1);   
   	       }
          }
          else {  
        	 // System.out.println("ispercent false..");
               units=(int) ((int)INCH*getZoom());
          }
               
              // units =INCH;
               
               
             /**  if (label.getPreferredSize().getWidth()==517 || label.getPreferredSize().getWidth()==172|| label.getPreferredSize().getWidth()==432 ||label.getPreferredSize().getWidth()==144)
               {
            	   units=(int) ((int)INCH*zoomfactor); 
               }
               else if(rheight==2592 || rheight==900) {
            	   if(zoomfactor==1.5)
            	  { 
            	  units=(int) ((int)INCH*1.5*1.5);
            	  }
            	  
            	   if(zoomfactor==0.5)
            	  { 
            	  units=(int) ((int)INCH*1.5*0.5);
            	  }
              
               }**/
            	   
              /** else 
               {
            	   units= (int) ((int)units*zoomfactor);
               }**/
              // System.out.println("units in updateruler:"+units);
               increment = units / 2;
               int tickLength = 0;
               String text = null;
               int start=0;
               int end=0;
               int temp=0;
               scrollRectToVisible(rect);
               start = (rect.y / increment) * increment;
              // System.out.println("start:"+start);
               end = (((rect.y + rect.height) / increment) + 1) * increment;
              // System.out.println("end:"+end);
               if (start == 0) {
               	text = Integer.toString(0) ;
                   
               	tickLength = 10;
               	g.drawLine(SIZE-1, 0, SIZE-tickLength-1, 0);
                   
               	g.drawString(text, 9, 10);

               	text = null;
                   
               	start = increment;
               }
               
               
              
              
              for (int i = start; i < end; i += increment) {
            	 // System.out.println("i:"+i);
            	 // System.out.println("units:"+units);
            	  if(units % 2!=0)
            	  {
            		  units=units-1;
            	  }
           	   if (i % units == 0)  {
                    //  System.out.println("in ticklength 10");
           		    tickLength = 10;
           		    //System.out.println("rheight in update ruler:"+rheight);
           		// System.out.println("rwidth in update ruler:"+rwidth);
           		 
           		 if(ispercent==true)
           		 {
           			 temp=(int) ((int)(labelnew.getPreferredSize().getWidth()*2)/zoomfactor);
           		 }
           		 else
           		 {
           		     temp=(int) ((int)(labelnew.getPreferredSize().getWidth()*2)/getZoom());
           		 }
           		    
           		// System.out.println("temp in rowheader:"+temp);
           		    
           		/**    
           		 if(rwidth==72000 && rheight==72000)
           		    {
           		    	text = Integer.toString(i/units*200);
           		    }
           		   else if(rwidth>1080 && rwidth<1440)
           		   {
           			text = Integer.toString(i/units*4); 
           		   }
           		   else if(temp==rwidth || (rwidth-temp) % 2==0 && rwidth<=1440 || (rwidth-temp) % 2==0 && rwidth-temp<=30 || (rwidth-temp) % 2==1 && rwidth-temp<=30 || rwidth-temp==1)
           		  // if(temp==rheight || temp==1349 || temp==1348)  
           			    
           		   {
           		     text = Integer.toString(i/units*2);
           		   }
           		    
           		   else {
           			  
           			   text = Integer.toString(i/units*10);
           		   }**/
           		 
           		if(orghght==orgheight)
     		   {
     			 //  System.out.println("in multiples of 1...");
     			   //System.out.println("text:"+i/units*2);
     		       text = Integer.toString(i/units*1);
     		   // System.out.println("text:"+text);
     		   }
     	
           		 
           	if(orghght*2==orgheight)
        		   {
        			  // System.out.println("in multiples of 2..");
        			   //System.out.println("text:"+i/units*2);
        		       text = Integer.toString(i/units*2);
        		  //  System.out.println("text:"+text);
        		   }
        		
        		    
        		//else if(orghght*4==rheight || (orghght>=288 && orghght<=342 )) 
        			else if(orghght*4==orgheight)
        		   {
        			   //System.out.println("in multiples of 2..");
        			   //System.out.println("text:"+i/units*2);
        		       text = Integer.toString(i/units*4);
        		       
        		   // System.out.println("text:"+text);
        		   }
           	
        			 else if(orgrwidth==72000 && orgheight==72000)
           		  {
           			text = Integer.toString(i/units*200); 
           		  }
           	
           	
        			else if(orgheight>36000 )
          		   {
          			  // System.out.println("in multiples of 100..");
          			   text = Integer.toString(i/units*100);
         
          			   
          		   }
  		
        			else if(orgheight>14400 )
         		   {
         			 //  System.out.println("in multiples of 50..");
         			   text = Integer.toString(i/units*50);
        
         			   
         		   }
 		
        		    
        			else if(orgheight>6400 )
            		   {
            			  // System.out.println("in multiples of 25..");
            			   text = Integer.toString(i/units*25);
           
            			   
            		   }
    		
           	
        			  else if(orgheight>3600 )
           		   {
           			   //System.out.println("in multiples of 20..");
           			   text = Integer.toString(i/units*20);
          
           			   
           		   }
   		
        		 //  else if(rheight>1440 || (orghght>=144 && orghght<=504 ) )
        			   else if(orgheight>1440 )
        		   {
        			  // System.out.println("in multiples of 10..");
        			   text = Integer.toString(i/units*10);
       
        			   
        		   }
		  
        		// else if(rwidth==72000 && rheight==72000)
        			
        		  
        		             
        	   
           		            } 
           	   
           		else {
           		                
           		 tickLength = 7;
           		                
           	     text = null;
           		            }
                  if (tickLength != 0) {
               	   g.drawLine(SIZE-1, (int)(i), SIZE-tickLength-1, (int)(i));
               	   if(text!=null)
               	   {
               	   g.drawString(text, 9, (int)((i)+3));
               	   }
               	   
                  }
                  
           	   
              }
               	
             
           }

           public Dimension getPreferredSize() {
              // return new Dimension(SIZE,(int) dim.getHeight());
           	return new Dimension(SIZE,(int)labelnew.getPreferredSize().getHeight() );
           }
       };
       rowheader.setBackground(Color.lightGray);
       rowheader.setOpaque(true);
      

       JLabel columnheader = new JLabel() {
           public void paintComponent(Graphics g) {
               super.paintComponent(g);
               
               if(isIspercent()==true)
               {
            	   if (orgrwidth<360)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/100);   
            	   }
            	   if(orgwdth*2==orgrwidth)
         		   {
            		   units=(int) ((int)(INCH*zoomfactor)/50);
         		   }
            	   if(orgwdth*4==orgrwidth)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/25);
            	   }
            	   if (orgrwidth>1440)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/10);   
            	   }
            	   if (orgrwidth>3600)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/5);   
            	   }
            	   if (orgrwidth>6400)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/4);   
            	   }
            	   if (orgrwidth>14400)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/2);   
            	   }
            	   if (orgrwidth>14400)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/2);   
            	   }
            	   if (orgrwidth>36000)
            	   {
            		   units=(int) ((int)(INCH*zoomfactor)/1);   
            	   }
                 
               }
               else {    
                    units=(int) ((int)INCH*getZoom());
               } 
              /** if ( label.getPreferredSize().getWidth()==517 || label.getPreferredSize().getWidth()==172|| label.getPreferredSize().getWidth()==432||label.getPreferredSize().getWidth()==144)
               {
            	   units=(int) ((int)INCH*zoomfactor); 
               }
               
               else if(rheight==2592 || rheight==900) {
            	   if(zoomfactor==1.5)
            	  { 
            	  units=(int) ((int)INCH*1.5*1.5);
            	  }
            	  
            	   if(zoomfactor==0.5)
            	  { 
            	  units=(int) ((int)INCH*1.5*0.5);
            	  }
              
               }**/
            	
               /**else 
               {
            	   units= (int) ((int)units*zoomfactor);
               }**/
               
               increment = units / 2;
               Rectangle r = g.getClipBounds();
               //Graphics2D g2 = (Graphics2D) g;
               //AffineTransform rigthRot = AffineTransform.getQuadrantRotateInstance(1);
              // g2.setFont(g2.getFont().deriveFont(rigthRot));
              
               int tickLength = 0;
               
               String text = null;
               int start=0;
               int end=0;
               int temp=0;
               
               start = (r.x / increment) * increment;
               
               end = (((r.x + r.width) / increment) + 1) * increment;
               
               if (start == 0) {
               	text = Integer.toString(0) ;
                   
               	tickLength = 10;
               	g.drawLine(0, SIZE-1, 0, SIZE-tickLength-1);
                   
               	g.drawString(text, 2, 21);
               	text = null;
                   
               	start = increment;
               }

               
                   
              /** for (int i = 10 - (r.x % 10); i < r.width; i += 10) {
                   g.drawLine(r.x + (int) (i / mmPerPixel), 0, r.x + (int) (i / mmPerPixel), 3);
                   g2.drawString("" + (r.x + i), r.x + (int) (i / mmPerPixel) - 4, 6);
               }**/
              for (int i = start; i < end; i += increment) {
            	  
            	  if(units % 2!=0)
            	  {
            		  units=units-1;
            	  }
           	   if (i % units == 0)  {
                      
           		    tickLength = 10;
           		    if(ispercent==true)
           		    {
           		    	 temp=(int) ((int)(labelnew.getPreferredSize().getWidth()*2)/zoomfactor);
           		    	//percentarray[0]=false;
           		    }
           		    else
           		    {
           		       temp=(int) ((int)(labelnew.getPreferredSize().getWidth()*2)/getZoom());
           		    }
           		  
           		// System.out.println("temp in columnheader:"+temp);
           		 
           		/**if(rwidth==72000 && rheight==72000)
       		    {
       		    	text = Integer.toString(i/units*200);
       		    }
       		    
           		else if(temp==rwidth ||(rwidth-temp) % 2==0 && rwidth<=1440 || (rwidth-temp) % 2==0 && rwidth-temp<=30 || (rwidth-temp) % 2==1 && rwidth-temp<=30 || rwidth-temp==1)
           		 //if(temp==rwidth||temp==864 || temp==1348)  
           		   {
           		   text = Integer.toString(i/units*2);
           		   }
           		   else {
           			   text = Integer.toString(i/units*10);
           		   }**/
           		if(orgwdth==orgrwidth)
      		   {
      		   text = Integer.toString(i/units);
      		   }
      		           		 
           		 if(orgwdth*2==orgrwidth)
         		   {
         		   text = Integer.toString(i/units*2);
         		   }
         		
         		//else if(orgwdth*4==rwidth || (orgwdth>=288 && orgwdth<=342))  
         			else if(orgwdth*4==orgrwidth) 
         		   {
         			   //System.out.println("in multiples of 2..");
         			   //System.out.println("text:"+i/units*2);
         		       text = Integer.toString(i/units*4);
         		   // System.out.println("text:"+text);
         		   }
         		
         			 else if(orgrwidth==72000 && orgheight==72000)
            		  {
            			text = Integer.toString(i/units*200); 
            		  }
            		
           		 
         			 else if (orgrwidth>36000)
             		 {
             			   text = Integer.toString(i/units*100);
             		   }
           		 
         		    
         			 else if (orgrwidth>14400)
             		 {
             			   text = Integer.toString(i/units*50);
             		   }
         		    
         			 else if (orgrwidth>6400)
             		 {
             			   text = Integer.toString(i/units*25);
             		   }
         		    
         			 else if (orgrwidth>3600)
             		 {
             			   text = Integer.toString(i/units*20);
             		   }
         		    
         		 //else if (rwidth>1440 || (orgwdth>=144 && orgwdth<=504)) 
         			 else if (orgrwidth>1440)
         		 {
         			   text = Integer.toString(i/units*10);
         		   }
         		 
         		   
         		 
         		
         		   
         		
           		          
           	   } 
           		else {
           		                
           		 tickLength = 7;
           		                
           	     text = null;
           		            }
                  if (tickLength != 0) {
               	   g.drawLine((int)(i), SIZE-1,(int)(i), SIZE-tickLength-1);
               	   
               	   if (text != null)
               	                          
               	  g.drawString(text+"", (int)((i)-3), 21);
               	    }
                  
           	   
              }
               	
             
          
           }

           public Dimension getPreferredSize() {
              // return new Dimension((int) dim.getWidth() SIZE);
           	return new Dimension((int)labelnew.getPreferredSize().getWidth(), SIZE);
           }
       };
       columnheader.setBackground(Color.lightGray);
       columnheader.setOpaque(true);
       columnheader.setAutoscrolls(true);
       
       scrollPane.setRowHeaderView(rowheader);
       scrollPane.setColumnHeaderView(columnheader);
       scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, corners[0]);
       scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corners[1]);
       scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corners[2]);
       scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corners[3]);
       
      
   }

   
   public  double getZoom() {
		return zoom;
	}







	public void setZoom(double d) {
		prevzoom=zoom;
		zoom =prevzoom*d;
	}
	
	
	 public static boolean isIspercent() {
			return ispercent;
		}

		public static void setIspercent(boolean ispercent) {
			Preview.ispercent = ispercent;
		}


	
	private String[] zoomPercentages(){
		String[] values=new String[91];
		for(int i=10;i<=100;i++)
		{   
			values[i-10]=i+"%";
			
		}
		return values;
	}
   
   
   
   }
