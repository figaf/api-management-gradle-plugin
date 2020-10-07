package com.figaf.plugin;

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

    private final Property<String> sourceFilePath;

    private final Property<String> apiProxyName;

    private final SetProperty<String> ignoreFilesList;

    public ApiManagementPluginExtension(Project project) {
        this.url = project.getObjects().property(String.class);
        this.username = project.getObjects().property(String.class);
        this.password = project.getObjects().property(String.class);
        this.platformType = project.getObjects().property(String.class);
        this.sourceFilePath = project.getObjects().property(String.class);
        this.apiProxyName = project.getObjects().property(String.class);
        this.ignoreFilesList = project.getObjects().setProperty(String.class);
    }
}
