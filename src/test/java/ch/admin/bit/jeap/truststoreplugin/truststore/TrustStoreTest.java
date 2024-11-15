package ch.admin.bit.jeap.truststoreplugin.truststore;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class TrustStoreTest {

    @SneakyThrows
    @Test
    void testCreateAddStore() {
        final String trustStorePassword = "changeit";
        final Path testCertDirPath = Paths.get("src","test", "resources", "test-certs");
        final File adminRootCerFile = testCertDirPath.resolve("root-01.cert").toFile();
        final File kafkaDevCertFile = testCertDirPath.resolve("kafka-dev.cert").toFile();
        final File trustStoreFile = createTempFile("truststore", "jks");

        TrustStore trustStore = TrustStore.of(TruststoreType.JKS, trustStorePassword);
        trustStore.addCertificate(adminRootCerFile);
        trustStore.addCertificate(kafkaDevCertFile);
        trustStore.store(trustStoreFile);

        KeyStore keyStore = loadKeyStore(trustStoreFile, trustStorePassword);
        assertThat(Collections.list(keyStore.aliases())).hasSize(2);
        assertCertificatePresent(keyStore, adminRootCerFile);
        assertCertificatePresent(keyStore, kafkaDevCertFile);
    }

    @SneakyThrows
    private KeyStore loadKeyStore(File keyStoreFile, String keyStorePassword) {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreData = new FileInputStream(keyStoreFile)){
            keyStore.load(keyStoreData, keyStorePassword.toCharArray());
        }
        return keyStore;
    }


    @SneakyThrows
    private void assertCertificatePresent(KeyStore keyStore, File certFile) {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert;
        try (FileInputStream fis = new FileInputStream (certFile)) {
            cert = (X509Certificate) factory.generateCertificate(fis);
        }
        String alias = cert.getSubjectX500Principal() + " by " + cert.getIssuerX500Principal().getName() + " (" + cert.getSerialNumber() + ")";
        Certificate certFromKeystore = keyStore.getCertificate(alias);
        assertThat(certFromKeystore).isEqualTo(cert);
    }

    @SneakyThrows
    private File createTempFile(String name, String extension) {
        File file = File.createTempFile(name, extension);
        file.deleteOnExit();
        return file;
    }

}
