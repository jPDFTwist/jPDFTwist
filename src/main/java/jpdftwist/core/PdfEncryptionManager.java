package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.interfaces.PdfEncryptionSettings;

public class PdfEncryptionManager {

    private int encryptionMode = -1;
    private int encryptionPermissions = -1;
    private byte[] userPassword = null;
    private byte[] ownerPassword = null;

    public void setEncryptionSettings(PdfEncryptionSettings w) throws DocumentException {
        if (encryptionMode != -1) {
            w.setEncryption(userPassword, ownerPassword, encryptionPermissions, encryptionMode);
        }
    }

    public void setEncryptionMode(final int encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public void setEncryptionPermissions(final int encryptionPermissions) {
        this.encryptionPermissions = encryptionPermissions;
    }

    public void setUserPassword(final byte[] userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * @deprecated The owner password should not be leaked outside the scope of this manager
     */
    public byte[] getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(final byte[] ownerPassword) {
        this.ownerPassword = ownerPassword;
    }
}
