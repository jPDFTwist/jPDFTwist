package jpdftwist.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

import ij.IJ;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.plugins.BF;

/**
 *
 * @author Vasilis Naskos
 */
public class JImageParser {

	public static ImageObject tryToReadImage(File file) {
		return tryToReadImage(file.getAbsolutePath());
	}

	public static ImageObject tryToReadImage(String filepath) {
		try {
			return readImage(filepath);
		} catch (BadElementException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static ImageObject readImage(String filepath) throws BadElementException, IOException {

		java.awt.Image awtImage = readAwtImage(filepath);

		if (awtImage == null) {
			return null;
		}

		int physicalWidthDpi = 72;
		int physicalHeightDpi = 72;

		try {
			ImageInfo imageInfo = Sanselan.getImageInfo(new File(filepath));
			physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
			physicalHeightDpi = imageInfo.getPhysicalHeightDpi();
		} catch (ImageReadException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
		}

		ImageObject imgObj = new ImageObject(Image.getInstance(awtImage, null));
		try {
			imgObj.setPhysicalWidthDpi(physicalWidthDpi);
			imgObj.setPhysicalHeightDpi(physicalHeightDpi);
			imgObj.setWidth(awtImage.getWidth(null));
			imgObj.setHeight(awtImage.getHeight(null));
			imgObj.setDepth(getBitDepth(awtImage));
		} catch (Exception ex) {
//			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
		}

		awtImage.flush();

		return imgObj;

	}

	public static Image readItextImage(String filepath) {
		try {
			java.awt.Image awtImage = readAwtImage(filepath);
			return Image.getInstance(awtImage, null);
		} catch (BadElementException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static java.awt.Image readAwtImage(String filepath) {
		java.awt.Image awtImage = readImageIJ(filepath);
		if (awtImage != null) {
			return awtImage;
		}

		awtImage = readImageBF(filepath);
		if (awtImage != null) {
			return awtImage;
		}

		awtImage = readImageMultiTiff(filepath);
		if (awtImage != null) {
			return awtImage;
		}

		awtImage = readImageJPEGReader(filepath);
		if (awtImage != null) {
			return awtImage;
		}

		awtImage = readImageImageIO(filepath);
		return awtImage;
	}

	/*
	 * public static java.awt.Image readPSD(InputStream is) throws
	 * ImageReadException, IOException { java.awt.Image awtImage =
	 * Sanselan.getBufferedImage(is); if (awtImage != null) { return awtImage; }
	 * awtImage = Sanselan.getBufferedImage(is); return awtImage; }
	 */

	private static java.awt.Image readImageIJ(String filepath) {
		try {
			ImagePlus imp = IJ.openImage(filepath);
			return imp.getImage();
		} catch (Exception ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static java.awt.Image readImageBF(String filepath) {
		try {
			java.awt.Image awtImage = null;
			ImagePlus[] imps = BF.openImagePlus(filepath);

			for (ImagePlus imp : imps) {
				awtImage = imp.getImage();
			}

			return awtImage;
		} catch (FormatException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static java.awt.Image readImageMultiTiff(String filepath) {
		try {
			RenderedImage[] r = readMultiPageTiff(filepath);
			return convertRenderedImage(r[0]);
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static java.awt.Image readImageJPEGReader(String filepath) {
		try {
			JpegReader jpeg = new JpegReader();
			return jpeg.readImage(new File(filepath));
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (ImageReadException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static java.awt.Image readImageImageIO(String filepath) {
		try {
			return tryWithImageIO(filepath);
		} catch (IOException ex) {
			Logger.getLogger(ImageParser.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static java.awt.Image tryWithImageIO(String filepath) throws IOException {
		ImageInputStream iis = null;
		try {
			java.awt.Image awtImage = null;

			iis = new FileImageInputStream(new File(filepath));
			for (Iterator<ImageReader> i = ImageIO.getImageReaders(iis); awtImage == null && i.hasNext();) {
				ImageReader r = i.next();
				r.setInput(iis);
				awtImage = r.read(0);
			}

			return awtImage;
		} finally {
			if (iis != null) {
				iis.close();
			}
		}
	}

	private static RenderedImage[] readMultiPageTiff(String filename) throws IOException {
		if (!(filename.toLowerCase().endsWith(".tiff") || filename.toLowerCase().endsWith(".tif"))) {
			return null;
		}

		File file = new File(filename);
		SeekableStream ss = new FileSeekableStream(file);
		ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
		int numPages = decoder.getNumPages();
		RenderedImage images[] = new RenderedImage[numPages];

		for (int i = 0; i < numPages; i++) {
			images[i] = decoder.decodeAsRenderedImage(i);
		}

		return images;
	}

	public static BufferedImage convertRenderedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable properties = new Hashtable();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (String key : keys) {
				properties.put(key, img.getProperty(key));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(java.awt.Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public static int getBitDepth(java.awt.Image img) throws IOException {
		ImageInputStream in = ImageIO.createImageInputStream(img);
		if (in == null) {
			throw new IOException("Can't create ImageInputStream!");
		}

		try {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);

			ImageReader reader;
			if (!readers.hasNext()) {
				throw new IOException("Can't read image format!");
			} else {
				reader = readers.next();
			}
			reader.setInput(in, true, true);
			int bitDepth = reader.getImageTypes(0).next().getColorModel().getPixelSize();
			reader.dispose();
			return bitDepth;
		} finally {
			in.close();
		}
	}

	public static class ImageObject {

		private final Image image;
		private int physicalWidthDpi;
		private int physicalHeightDpi;
		private int depth;
		private int width;
		private int height;

		public ImageObject(Image image) {
			this.image = image;
		}

		public Image getImage() {
			return image;
		}

		public int getPhysicalWidthDpi() {
			return physicalWidthDpi;
		}

		public void setPhysicalWidthDpi(int physicalWidthDpi) {
			this.physicalWidthDpi = physicalWidthDpi;
		}

		public int getPhysicalHeightDpi() {
			return physicalHeightDpi;
		}

		public void setPhysicalHeightDpi(int physicalHeightDpi) {
			this.physicalHeightDpi = physicalHeightDpi;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

}
