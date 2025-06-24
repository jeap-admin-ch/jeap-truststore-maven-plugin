package ch.admin.bit.jeap.truststoreplugin.truststore;

import java.io.File;
import java.security.cert.X509Certificate;

public class TrustStoreException extends RuntimeException {

    TrustStoreException(String message, Exception e) {
        super(message, e);
    }

    static TrustStoreException trustStoreCreationFailed(Exception e) {
        return new TrustStoreException("Unable to create truststore.", e);
    }

    static TrustStoreException certificateLoadFailed(File certFile, Exception e) {
        return new TrustStoreException("Unable to load certificate from file " + certFile.getAbsolutePath(), e);
    }

    static TrustStoreException addingCertificateFailed(X509Certificate certificate, Exception e) {
        String certificateDescription = certificate.getSubjectX500Principal() + " issued by " + certificate.getIssuerX500Principal();
        return new TrustStoreException("Unable to add certificate for " + certificateDescription + " to truststore.", e);
    }

    static TrustStoreException storingFailed(File trustStoreFile, Exception e) {
        return new TrustStoreException("Unable to store truststore in file " + trustStoreFile.getAbsolutePath(), e);
    }

    static TrustStoreException sizeCheckFailed(Exception e) {
        return new TrustStoreException("Unable to get the size of the truststore", e);
    }

}
