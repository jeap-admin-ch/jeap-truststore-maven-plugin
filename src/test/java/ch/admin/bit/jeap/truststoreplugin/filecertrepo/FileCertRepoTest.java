package ch.admin.bit.jeap.truststoreplugin.filecertrepo;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FileCertRepoTest {

    @Test
    void testTrailingSlashesAccepted() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("general/", "technology/kafka/", "application/jme/provider/"), List.of("cert", "pem"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("dev");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("root.cert", "kafka-dev.cert", "dev.pem");
    }

    @Test
    void testDoesOnlyIncludeGivenEnvironmentSubdirectory() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("technology/kafka"), List.of("cert"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("dev");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("kafka-dev.cert");
    }

    @Test
    void testDoesNotIncludeSubdirectoryCertificatesOtherThanFromEnvironment() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("general"), List.of("cert"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("dev");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("root.cert");
    }

    @Test
    void testDoesOnlyIncludeGivenExtensions() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("general/admin", "technology/kafka"), List.of("cert"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("abn");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("admin-01.cert", "kafka-abn.cert");
    }

    @Test
    void testFindMoreThanOneCertificateInOneDirectory() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("general/swissgov"), List.of("cert"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("abn");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("gov-01.cert", "gov-02.CERT");
    }

    @Test
    void testPracticalUseCase() {
        FileCertRepo fileCertRepo = FileCertRepo.of(getTestCertFileRepoPath(),
                List.of("general", "general/admin", "technology/kafka", "application/jme/provider"), List.of("cert", "pem"));

        Stream<File> certFiles = fileCertRepo.streamCertFiles("dev");

        assertThat(certFiles.map(File::getName).collect(Collectors.toUnmodifiableList())).
                containsOnly("root.cert",
                             "admin-01.cert",
                             "kafka-dev.cert",
                            "dev.pem");
    }

    private Path getTestCertFileRepoPath() {
        return Paths.get("src","test", "resources", "test-certs", "repo");
    }

}

