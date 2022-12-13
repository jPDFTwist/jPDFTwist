/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jpdftwist.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasilis
 */
public class Utils {

	public final static String getMd5(String datafile) {
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(datafile);
			byte[] dataBytes = new byte[1024];

			int nread;

			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}

			byte[] mdbytes = md.digest();

			// convert the byte to hex format
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return null;
	}

	public static String readableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

}
