package com.jee.publicapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.upload")
public class DocumentStorageProperties {

    private String documentBasePath;
    private String correctionBasePath;
    private String userFolderPattern;

    public String getDocumentBasePath() {
        return documentBasePath;
    }

    public void setDocumentBasePath(String documentBasePath) {
        this.documentBasePath = documentBasePath;
    }

    public String getCorrectionBasePath() {
        return correctionBasePath;
    }

    public void setCorrectionBasePath(String correctionBasePath) {
        this.correctionBasePath = correctionBasePath;
    }

    public String getUserFolderPattern() {
        return userFolderPattern;
    }

    public void setUserFolderPattern(String userFolderPattern) {
        this.userFolderPattern = userFolderPattern;
    }
}
