package com.figaf.plugin;

import com.figaf.integration.common.factory.HttpClientsFactory;
import lombok.Getter;
import lombok.ToString;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

/**
 * @author Arsenii Istlentev
 */
@Getter
@ToString
public class ApiManagementPluginExtension {

    private final Property<String> url;

    private final Property<String> username;

    private final Property<String> password;

    private final Property<String> platformType;

    private final Property<String> oauthTokenUrl;

    private final Property<String> authenticationType;

    private final Property<String> publicApiUrl;

    private final Property<String> publicApiClientId;

    private final Property<String> publicApiClientSecret;

    private final Property<String> sourceFilePath;

    private final Property<String> apiProxyName;

    private final SetProperty<String> ignoreFilesList;

    private final Property<String> apiManagementObjectType;

    private final Property<HttpClientsFactory> httpClientsFactory;

    public ApiManagementPluginExtension(Project project) {
        this.url = project.getObjects().property(String.class);
        this.username = project.getObjects().property(String.class);
        this.password = project.getObjects().property(String.class);
        this.platformType = project.getObjects().property(String.class);
        this.oauthTokenUrl = project.getObjects().property(String.class);
        this.authenticationType = project.getObjects().property(String.class);
        this.publicApiUrl = project.getObjects().property(String.class);
        this.publicApiClientId = project.getObjects().property(String.class);
        this.publicApiClientSecret = project.getObjects().property(String.class);
        this.sourceFilePath = project.getObjects().property(String.class);
        this.apiProxyName = project.getObjects().property(String.class);
        this.ignoreFilesList = project.getObjects().setProperty(String.class);
        this.apiManagementObjectType = project.getObjects().property(String.class);
        this.httpClientsFactory = project.getObjects().property(HttpClientsFactory.class);
    }
}
