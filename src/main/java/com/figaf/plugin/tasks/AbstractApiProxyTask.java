package com.figaf.plugin.tasks;

import com.figaf.plugin.client.ApiManagementClient;
import com.figaf.plugin.entities.ApiManagementConnectionProperties;
import com.figaf.plugin.entities.CloudPlatformType;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arsenii Istlentev
 */
@Setter
@ToString
public abstract class AbstractApiProxyTask extends DefaultTask {

    @Input
    protected String url;

    @Input
    protected String username;

    @Input
    protected String password;

    @Input
    protected CloudPlatformType platformType;

    @Input
    protected String sourceFilePath;

    @Input
    protected String apiProxyName;

    @Input
    protected Set<String> ignoreFilesList;

    protected ApiManagementConnectionProperties apiManagementConnectionProperties;

    protected File sourceFolder;

    protected ApiManagementClient apiManagementClient = new ApiManagementClient();

    @TaskAction
    public void taskAction() {
        try {
            defineParameters();
            doTaskAction();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected abstract void doTaskAction() throws Exception;

    private void defineParameters() {
        apiManagementConnectionProperties = new ApiManagementConnectionProperties(url, username, password, platformType);
        System.out.println("apiManagementConnectionProperties = " + apiManagementConnectionProperties);
        sourceFolder = new File(sourceFilePath);

        if (apiProxyName == null) {
            apiProxyName = sourceFolder.getName();
        }

        if (CollectionUtils.isEmpty(ignoreFilesList)) {
            ignoreFilesList = new HashSet<>();
        }
        ignoreFilesList.add("src/test");
        ignoreFilesList.add("build.gradle");
        ignoreFilesList.add("gradle.properties");
        ignoreFilesList.add("settings.gradle");

        System.out.println("apiProxyName = " + apiProxyName);
        System.out.println("ignoreFilesList = " + ignoreFilesList);
    }
}
