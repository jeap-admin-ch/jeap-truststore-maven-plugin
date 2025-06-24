package ch.admin.bit.jeap.truststoreplugin.mojo;

import ch.admin.bit.jeap.truststoreplugin.filecertrepo.*;
import ch.admin.bit.jeap.truststoreplugin.gitcertrepo.GitCertRepo;
import ch.admin.bit.jeap.truststoreplugin.truststore.TrustStore;
import ch.admin.bit.jeap.truststoreplugin.truststore.TruststoreType;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

@Getter
@Mojo(name = "build-truststores", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class BuildTruststoresMojo extends AbstractMojo
{

    @Parameter
    private URL certificateRepositoryUrl;

    @Parameter(defaultValue = "master")
    private String certificateRepositoryBranch;

    @Parameter
    private File certificateRepositoryDir;

    @Parameter
    private List<File> outputDirs;

    @Parameter(defaultValue = "JKS")
    private TruststoreType trustStoreType;

    @Parameter(defaultValue = "truststore")
    private String trustStoreName;

    @Parameter(defaultValue = "changeit")
    private String trustStorePassword;

    @Parameter(defaultValue = "CERTIFICATES_REPO_GIT_TOKEN")
    private String tokenEnvironmentPropertyName;

    @Parameter
    private List<String> environments;

    @Parameter
    private List<String> includes;

    @Parameter
    private List<String> extensions;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    public void execute() throws MojoExecutionException {
        if (session.isOffline() && certificateRepositoryDir == null) {
            getLog().warn("Offline mode - not downloading certificates from repository. Alternatively configure 'certificateRepositoryDir'");
            return;
        }

        checkParameters();
        setDefaultsIfNotConfigured();
        File fileCertRepoDirectory = getFileCertRepoDirectory();
        FileCertRepo fileCertRepo = FileCertRepo.of(fileCertRepoDirectory.toPath(), includes, extensions);
        for (String environment : environments) {
            try {
                TrustStore trustStore = createTrustStore(fileCertRepo, environment);
                distributeTruststore(trustStore, environment);
            } catch (Exception e) {
                getLog().error("Creating truststore for environment '" + environment + "' failed.", e);
                throw new MojoExecutionException("Truststore building failed.", e);
            }
        }
    }

    private File getFileCertRepoDirectory() throws MojoExecutionException {
        if (certificateRepositoryDir != null) {
            return certificateRepositoryDir;
        }
        else {
            return cloneGitRepoToTempDir(certificateRepositoryUrl, certificateRepositoryBranch);
        }
    }

    private File cloneGitRepoToTempDir(URL gitRepoUrl, String branch) throws MojoExecutionException {
        try {
            File tempRepoBranchDir = Files.createTempDirectory(branch).toFile();
            FileUtils.forceDeleteOnExit(tempRepoBranchDir);
            GitCertRepo gitCertRepo = GitCertRepo.of(gitRepoUrl.toURI(), branch, tokenEnvironmentPropertyName);
            getLog().info("Cloning git repo " + gitRepoUrl + " at branch " + branch + " to directory " + tempRepoBranchDir.getAbsolutePath());
            gitCertRepo.cloneTo(tempRepoBranchDir);
            return tempRepoBranchDir;
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to clone git repository " + gitRepoUrl + " at branch " + branch + " to a temp dir.", e);
        }
    }

    private TrustStore createTrustStore(FileCertRepo fileCertRepo, String environment) {
        getLog().info("Creating truststore for environment '" + environment + "' from certificate file repository " + fileCertRepo.getRepoPath().toAbsolutePath());
        TrustStore trustStore = TrustStore.of(trustStoreType, trustStorePassword);
        fileCertRepo.streamCertFiles(environment).forEach(trustStore::addCertificate);
        getLog().info("Created truststore with size " + trustStore.size());
        return trustStore;
    }

    private void distributeTruststore(TrustStore trustStore, String environment) {
        outputDirs.forEach( outputDir -> {
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdirs();
            String filename = trustStoreName + "-" + environment + "." + trustStoreType.getFileExtension();
            File trustStoreFile = new File(outputDir, filename);
            trustStore.store(trustStoreFile);
            getLog().info("Created truststore " + trustStoreFile.getAbsolutePath());
        });
    }

    private void setDefaultsIfNotConfigured() {
        if ((environments == null) || environments.isEmpty()) {
            environments = List.of("dev", "ref", "abn", "prod");
        }
        if ((includes == null) || includes.isEmpty()) {
            includes = List.of("general");
        }
        if ((extensions == null) || extensions.isEmpty()) {
            extensions = List.of("cert", "cer", "crt", "pem");
        }
        if ((outputDirs == null) || outputDirs.isEmpty()) {
            outputDirs = List.of(new File(project.getBasedir(), "target/classes"));
        }
    }

    private void checkParameters() throws MojoExecutionException {
        if ((certificateRepositoryUrl == null) && (certificateRepositoryDir == null)) {
            throw new MojoExecutionException("One of the parameters 'certificateRepositoryUrl' and 'certificateRepositoryDir' must be configured.");
        }
        if ((certificateRepositoryUrl != null) && (certificateRepositoryDir != null)) {
            throw new MojoExecutionException("Parameters 'certificateRepositoryUrl' and 'certificateRepositoryDir' must not both be configured.");
        }
    }

}
