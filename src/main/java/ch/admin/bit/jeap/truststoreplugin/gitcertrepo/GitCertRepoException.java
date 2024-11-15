package ch.admin.bit.jeap.truststoreplugin.gitcertrepo;

import java.net.URI;

public class GitCertRepoException extends RuntimeException {

    GitCertRepoException(String message, Exception e) {
        super(message, e);
    }

    static GitCertRepoException cloningFailed(URI repoUri, String branch, Exception e) {
        return new GitCertRepoException("Unable to clone the git repository " + repoUri + " at branch " + branch, e);
    }

}
