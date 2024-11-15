package ch.admin.bit.jeap.truststoreplugin.truststore;

public enum TruststoreType {

    JKS("jks"),
    PKCS12("p12");

    private final String fileExtension;

    TruststoreType(String fileExtension ){
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

}
