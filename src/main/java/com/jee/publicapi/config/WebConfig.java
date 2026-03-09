package com.jee.publicapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final DocumentStorageProperties storageProps;

    public WebConfig(DocumentStorageProperties storageProps) {
        this.storageProps = storageProps;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String documentPath = storageProps.getDocumentBasePath().replace("\\", "/");
        String correctionPath = storageProps.getCorrectionBasePath().replace("\\", "/");

        if (!documentPath.endsWith("/")) documentPath += "/";
        if (!correctionPath.endsWith("/")) correctionPath += "/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations(
                        "file:///" + documentPath,
                        "file:///" + correctionPath
                )
                .setCachePeriod(0);
    }
}