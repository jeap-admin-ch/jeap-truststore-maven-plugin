package ch.admin.bit.jeap.truststoreplugin.truststore;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class TrustStore {

    private final KeyStore keyStore;
    private final char[] password;

    public static TrustStore of(TruststoreType type, String password) {
        return new TrustStore(type, password);
    }

    private TrustStore(TruststoreType type, String password) {
        this.password = password.toCharArray();
        keyStore = createTruststore(type);
    }

    public void addCertificate(File certFile) {
        X509Certificate certificate = loadX509Certificate(certFile);
        try {
            String alias = createAlias(certificate);
            keyStore.setCertificateEntry(alias, certificate);
        } catch (KeyStoreException e) {
            throw TrustStoreException.addingCertificateFailed(certificate, e);
        }
    }

    public void store(File targetFile) {
        try (FileOutputStream trustStoreOutputStream = new FileOutputStream(targetFile) ){
            keyStore.store(trustStoreOutputStream, password);
        } catch (Exception e) {
            throw TrustStoreException.storingFailed(targetFile, e);
        }
    }

    public int size() {
        try {
            return keyStore.size();
        } catch (KeyStoreException e) {
            throw TrustStoreException.sizeCheckFailed(e);
        }
    }

    private String createAlias(X509Certificate certificate) {
        X500Principal subject = certificate.getSubjectX500Principal();
        X500Principal issuer = certificate.getIssuerX500Principal();
        return subject + " by " +  issuer.getName() + " (" + certificate.getSerialNumber() + ")";
    }

    private KeyStore createTruststore(TruststoreType type) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type.name());
            keyStore.load(null, password);
            return keyStore;
        } catch (Exception e) {
            throw TrustStoreException.trustStoreCreationFailed(e);
        }
    }

    private X509Certificate loadX509Certificate(File certFile) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            try (FileInputStream certData = new FileInputStream(certFile)) {
                return (X509Certificate) certFactory.generateCertificate(certData);
            }
        }
        catch (Exception e) {
            throw TrustStoreException.certificateLoadFailed(certFile, e);
        }
    }

}
