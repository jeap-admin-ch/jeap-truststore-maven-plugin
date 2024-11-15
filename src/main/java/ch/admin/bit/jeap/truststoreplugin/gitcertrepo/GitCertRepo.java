package ch.admin.bit.jeap.truststoreplugin.gitcertrepo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.net.URI;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GitCertRepo {

    public static GitCertRepo of(URI uri, String branch) {
        return new GitCertRepo(uri, branch);
    }

    private final URI uri;
    private final String branch;

    public void cloneTo(File targetDirectory ) {
        try {
            Git.cloneRepository()
                .setURI(uri.toString())
                .setBranch(branch)
                .setDirectory(targetDirectory)
                .call();
        } catch (Exception e) {
            throw GitCertRepoException.cloningFailed(uri, branch, e);
        }
    }

}
