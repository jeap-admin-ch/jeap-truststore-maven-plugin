package ch.admin.bit.jeap.truststoreplugin.mojo;

import ch.admin.bit.jeap.truststoreplugin.truststore.TruststoreType;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoExtension;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@MojoTest
class BuildTruststoresMojoTest {

    @Inject
    private MavenSession session;

    @Test
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-check-params-missing-mandatory.xml")
    void test_WhenMissingParams_ThenThrowsException(BuildTruststoresMojo mojo) {
        assertThatThrownBy(mojo::execute).
            isInstanceOf(MojoExecutionException.class).
            hasMessage("One of the parameters 'certificateRepositoryUrl' and 'certificateRepositoryDir' must be configured.");
    }

    @Test
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-check-params-conflicting-certificate-locations.xml")
    void test_WhenBothCertificateLocationsProvided_ThenThrowsException(BuildTruststoresMojo mojo) {
        assertThatThrownBy(mojo::execute).
                isInstanceOf(MojoExecutionException.class).
                hasMessage("Parameters 'certificateRepositoryUrl' and 'certificateRepositoryDir' must not both be configured.");
    }

    @SneakyThrows
    @Test
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-check-params-all-provided.xml")
    void test_WhenAllParamsProvided_ThenConfigurationAsProvided(BuildTruststoresMojo mojo) {
        assertThat(mojo.getCertificateRepositoryUrl()).isEqualTo(new URL("https://testhost/scm/dst/test-certificates.git"));
        assertThat(mojo.getCertificateRepositoryBranch()).isEqualTo("feature-xy");
        assertThat(mojo.getEnvironments()).containsExactly("dev", "ref", "abn");
        assertThat(mojo.getIncludes()).containsExactly("general", "general/admin", "technology/kafka", "application/jme/provider");
        assertThat(mojo.getExtensions()).containsExactly("cert", "pem");
        assertThat(mojo.getTrustStoreType()).isEqualTo(TruststoreType.PKCS12);
        assertThat(mojo.getTrustStoreName()).isEqualTo("my-trust-store");
        assertThat(mojo.getTrustStorePassword()).isEqualTo("my-password");
        assertThat(mojo.getOutputDirs()).hasSize(2);
        assertThat(mojo.getOutputDirs().get(0)).isEqualTo(new File(mojo.getProject().getBasedir(), "target/classes"));
        assertThat(mojo.getOutputDirs().get(1)).isEqualTo(new File(mojo.getProject().getBasedir(), "some/other/path"));
    }

    @Test
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-check-params-only-mandatory-provided.xml")
    void test_WhenOnlyMandatoryParamsProvided_ThenDeclarativeDefaultsApply(BuildTruststoresMojo mojo) {
        assertThat(mojo.getCertificateRepositoryBranch()).isEqualTo("master");
        assertThat(mojo.getTrustStoreType()).isEqualTo(TruststoreType.JKS);
        assertThat(mojo.getTrustStoreName()).isEqualTo("truststore");
        assertThat(mojo.getTrustStorePassword()).isEqualTo("changeit");
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-cert-file-repo.xml")
    void test_ExecuteOnCertFileRepo(Boolean offline, BuildTruststoresMojo mojo, @TempDir File targetDir) {
        File resourcesTestCertRepoDir = MojoExtension.getTestFile("src/test/resources/test-certs/repo");
        FileUtils.copyDirectory(resourcesTestCertRepoDir, new File(targetDir, "cert-repo"));

        // Override basedir-dependent fields to use the temp directory
        MojoExtension.setVariableValueToObject(mojo, "certificateRepositoryDir", new File(targetDir, "cert-repo"));
        MojoExtension.setVariableValueToObject(mojo, "outputDirs", List.of(
                new File(targetDir, "target/classes"),
                new File(targetDir, "target/other/path")
        ));
        when(session.isOffline()).thenReturn(offline);

        final X509Certificate aminRoot = loadCertificate("src/test/resources/test-certs/repo/general/admin/admin-01.cert");
        final X509Certificate quovadisRoot = loadCertificate("src/test/resources/test-certs/repo/general/root.cert");
        final X509Certificate kafkaAbn = loadCertificate("src/test/resources/test-certs/repo/technology/kafka/abn/kafka-abn.cert");
        final X509Certificate kafkaDev = loadCertificate("src/test/resources/test-certs/repo/technology/kafka/dev/kafka-dev.cert");
        final X509Certificate kafkaRef = loadCertificate("src/test/resources/test-certs/repo/technology/kafka/ref/kafka-ref.cert");
        final X509Certificate jmeProviderDev = loadCertificate("src/test/resources/test-certs/repo/application/jme/provider/dev/dev.pem");
        final X509Certificate jmeProviderRef = loadCertificate("src/test/resources/test-certs/repo/application/jme/provider/ref/ref.pem");

        final String trustStoreName = "truststore";
        final String trustStoreExtension = "jks";

        final File outputDir1 = new File(targetDir, "target/classes");
        final File trustStoreDevOutDir1 = getTrustStore(outputDir1, "dev", trustStoreName, trustStoreExtension);
        final File trustStoreRefOutDir1 = getTrustStore(outputDir1, "ref", trustStoreName, trustStoreExtension);
        final File trustStoreAbnOutDir1 = getTrustStore(outputDir1, "abn", trustStoreName, trustStoreExtension);
        final File trustStoreProdOutDir1 = getTrustStore(outputDir1, "prod", trustStoreName, trustStoreExtension);
        assertPresence(false, trustStoreDevOutDir1, trustStoreRefOutDir1, trustStoreAbnOutDir1, trustStoreProdOutDir1);

        final File outputDir2 = new File(targetDir, "target/other/path");
        final File trustStoreDevOutDir2 = getTrustStore(outputDir2, "dev", trustStoreName, trustStoreExtension);
        final File trustStoreRefOutDir2 = getTrustStore(outputDir2, "ref", trustStoreName, trustStoreExtension);
        final File trustStoreAbnOutDir2 = getTrustStore(outputDir2, "abn", trustStoreName, trustStoreExtension);
        final File trustStoreProdOutDir2 = getTrustStore(outputDir2, "prod", trustStoreName, trustStoreExtension);
        assertPresence(false, trustStoreDevOutDir2, trustStoreRefOutDir2, trustStoreAbnOutDir2, trustStoreProdOutDir2);

        mojo.execute();

        assertPresence(true, trustStoreDevOutDir1, trustStoreRefOutDir1, trustStoreAbnOutDir1);
        assertPresence(false, trustStoreProdOutDir1);
        assertPresence(true, trustStoreDevOutDir2, trustStoreRefOutDir2, trustStoreAbnOutDir2);
        assertPresence(false, trustStoreProdOutDir2);

        assertThat(readCertsFromTruststore(trustStoreDevOutDir1, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
            containsOnly(aminRoot, quovadisRoot, kafkaDev, jmeProviderDev);
        assertThat(readCertsFromTruststore(trustStoreRefOutDir1, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
                containsOnly(aminRoot, quovadisRoot, kafkaRef, jmeProviderRef);
        assertThat(readCertsFromTruststore(trustStoreAbnOutDir1, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
                containsOnly(aminRoot, quovadisRoot, kafkaAbn);

        assertThat(readCertsFromTruststore(trustStoreDevOutDir2, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
                containsOnly(aminRoot, quovadisRoot, kafkaDev, jmeProviderDev);
        assertThat(readCertsFromTruststore(trustStoreRefOutDir2, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
                containsOnly(aminRoot, quovadisRoot, kafkaRef, jmeProviderRef);
        assertThat(readCertsFromTruststore(trustStoreAbnOutDir2, mojo.getTrustStoreType(), mojo.getTrustStorePassword())).
                containsOnly(aminRoot, quovadisRoot, kafkaAbn);
    }

    @SneakyThrows
    @Test
    @InjectMojo(goal = "build-truststores", pom = "src/test/resources/pom-cert-git-repo.xml")
    void test_ExecuteOnCertGitRepoOffline(BuildTruststoresMojo mojo, @TempDir File targetDir) {
        final String trustStoreName = "truststore";
        final String trustStoreExtension = "jks";

        final File outputDir = new File(targetDir, "target/classes");
        final File trustStoreDevOutDir = getTrustStore(outputDir, "dev", trustStoreName, trustStoreExtension);
        final File trustStoreRefOutDir = getTrustStore(outputDir, "ref", trustStoreName, trustStoreExtension);
        final File trustStoreAbnOutDir = getTrustStore(outputDir, "abn", trustStoreName, trustStoreExtension);
        final File trustStoreProdOutDir = getTrustStore(outputDir, "prod", trustStoreName, trustStoreExtension);
        assertPresence(false, trustStoreDevOutDir, trustStoreRefOutDir, trustStoreAbnOutDir, trustStoreProdOutDir);

        when(session.isOffline()).thenReturn(true);

        mojo.execute();

        assertPresence(false, trustStoreDevOutDir, trustStoreRefOutDir, trustStoreAbnOutDir, trustStoreProdOutDir);
    }

    private File getTrustStore(File outputDir, String environment, String trustStoreName, String trustStoreExtension) {
        return new File(outputDir, trustStoreName + "-" + environment + "." + trustStoreExtension);
    }

    private void assertPresence(boolean present, File... files) {
        for (File file : files) {
            assertThat(file.exists()).isEqualTo(present);
        }
    }

    @SneakyThrows
    private X509Certificate loadCertificate(String path) {
        File certFile = MojoExtension.getTestFile(path);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(certFile)) {
            return (X509Certificate) factory.generateCertificate(fis);
        }
    }

    @SneakyThrows
    List<X509Certificate> readCertsFromTruststore(File trustStoreFile, TruststoreType trustStoreType, String trustStorePassword) {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType.name());
        try (FileInputStream trustStoreData = new FileInputStream(trustStoreFile)){
            trustStore.load(trustStoreData, trustStorePassword.toCharArray());
        }
        return Collections.list(trustStore.aliases()).stream().
                map( alias -> extractCertificate(trustStore, alias)).
                collect(Collectors.toUnmodifiableList());
    }

    @SneakyThrows
    private X509Certificate extractCertificate(KeyStore store, String alias) {
        return (X509Certificate) store.getCertificate(alias);
    }

}
