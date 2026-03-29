package com.jee.publicapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jee.publicapi.security.ActivityInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final DocumentStorageProperties storageProps;
    private final ActivityInterceptor activityInterceptor;

    public WebConfig(
            DocumentStorageProperties storageProps,
            ActivityInterceptor activityInterceptor) {

        this.storageProps = storageProps;
        this.activityInterceptor = activityInterceptor;
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
    
    /* ================= ACTIVITY INTERCEPTOR ================= */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(activityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register/**",
                        "/api/user/otp/**",
                        "/api/auth/user**",
                        "/files/**",
                        "/captcha/**"
                );
    }
}