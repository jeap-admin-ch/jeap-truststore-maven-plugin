package ch.admin.bit.jeap.truststoreplugin.gitcertrepo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.net.URI;

/**
 * Utility class for cloning a Git repository with support for both pipeline and local environments.
 *
 * <p>This class attempts to clone a Git repository by:
 * <ul>
 *   <li>Using the token provided as environment variable (Default `CERTIFICATES_REPO_GIT_TOKEN`) as a personal
 *       access token (PAT) for authentication when it is provided. This is typically used in CI/CD pipelines to
 *       access private GitHub repositories.</li>
 *   <li>Falling back to the system-installed Git client when the token is not available. In this case,
 *       cloning relies on the credentials already configured in the local environment (e.g., SSH keys,
 *       credential helpers, or Git configuration).</li>
 * </ul>
 *
 * <p>This dual-mode approach ensures that the cloning process works seamlessly both in automated
 * environments (such as pipelines) and in local development setups.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GitCertRepo {

    private final Log log;
    private final URI uri;
    private final String branch;
    private final String tokenEnvironmentPropertyName;

    public static GitCertRepo of(Log log, URI uri, String branch, String tokenEnvironmentPropertyName) {
        return new GitCertRepo(log, uri, branch, tokenEnvironmentPropertyName);
    }

    @SuppressWarnings({"java:S2142", "java:S4036"})
    public void cloneTo(File targetDirectory) {
        try {
            final String token = System.getenv(tokenEnvironmentPropertyName);

            if (token != null && !token.isEmpty()) {
                log.info("Found a " + tokenEnvironmentPropertyName + " in environment: using jgit with this token to clone the repository");
                Git.cloneRepository()
                        .setURI(uri.toString())
                        .setBranch(branch)
                        .setDirectory(targetDirectory)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider("no-username-when-using-token", token))
                        .call();
            } else {
                log.info("No " + tokenEnvironmentPropertyName + " found in environment: using git client with system credentials to clone the repository");
                ProcessBuilder pb = new ProcessBuilder("git", "clone", "--branch", branch, "--single-branch", uri.toString(), targetDirectory.getAbsolutePath());
                pb.directory(targetDirectory);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("Failed to clone repository. Exit code: " + exitCode);
                    throw new IllegalStateException("Failed to clone repository. Exit code: " + exitCode);
                }
            }
            log.info("Cloned branch '" + branch + "' successfully.");
        } catch (Exception e) {
            throw GitCertRepoException.cloningFailed(uri, branch, e);
        }
    }

}
