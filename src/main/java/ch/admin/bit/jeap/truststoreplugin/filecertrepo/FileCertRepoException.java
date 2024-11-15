package ch.admin.bit.jeap.truststoreplugin.filecertrepo;

public class FileCertRepoException extends RuntimeException {

    FileCertRepoException(String message, Exception e) {
        super(message, e);
    }

    static FileCertRepoException fileAccessFailed(Exception e) {
        return new FileCertRepoException("Unable to access file.", e);
    }

}
