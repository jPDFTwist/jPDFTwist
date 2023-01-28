package jpdftwist.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class SignatureManager {

    private PrivateKey key = null;
    private Certificate[] certChain = null;
    private int certificationLevel = 0;
    private boolean sigVisible = false;

    public void setSignature(File keystoreFile, String alias, char[] password, int certificationLevel, boolean visible)
        throws IOException {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Files.newInputStream(keystoreFile.toPath()), password);
            key = (PrivateKey) ks.getKey(alias, password);
            if (key == null) {
                throw new IOException("No private key found with alias " + alias);
            }
            certChain = ks.getCertificateChain(alias);
            this.certificationLevel = certificationLevel;
            this.sigVisible = visible;
        } catch (GeneralSecurityException ex) {
            throw new IOException(ex.toString(), ex);
        }
    }

    public PrivateKey getKey() {
        return key;
    }

    public Certificate[] getCertChain() {
        return certChain;
    }

    public int getCertificationLevel() {
        return certificationLevel;
    }

    public boolean isSigVisible() {
        return sigVisible;
    }
}
