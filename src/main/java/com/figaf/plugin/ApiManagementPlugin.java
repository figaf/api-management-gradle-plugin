package com.figaf.plugin;

import com.figaf.integration.common.entity.CloudPlatformType;
import com.figaf.plugin.enumeration.ApiManagementObjectType;
import com.figaf.plugin.tasks.AbstractApiManagementObjectTask;
import com.figaf.plugin.tasks.DownloadApiManagementObjectTask;
import com.figaf.plugin.tasks.UploadApiManagementObjectTask;
import com.figaf.integration.common.factory.HttpClientsFactory;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.SetProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arsenii Istlentev
 */
public class ApiManagementPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        ApiManagementPluginExtension extension = project.getExtensions().create("apiManagementPlugin", ApiManagementPluginExtension.class, project);

        project.getTasks().register(
            "uploadApiManagementObject",
            UploadApiManagementObjectTask.class, uploadApiManagementObject -> applyExtension(uploadApiManagementObject, extension)
        );

        project.getTasks().register(
            "downloadApiManagementObject",
            DownloadApiManagementObjectTask.class, downloadApiManagementObject -> applyExtension(downloadApiManagementObject, extension)
        );

    }

    private void applyExtension(AbstractApiManagementObjectTask abstractApiManagementObjectTask, ApiManagementPluginExtension extension) {
        try {
            abstractApiManagementObjectTask.setGroup("api-mgmt-plugin");
            abstractApiManagementObjectTask.setUrl(extension.getUrl().getOrNull());
            abstractApiManagementObjectTask.setUsername(extension.getUsername().getOrNull());
            abstractApiManagementObjectTask.setPassword(extension.getPassword().getOrNull());
            String platformTypeString = extension.getPlatformType().getOrNull();
            if (platformTypeString != null) {
                abstractApiManagementObjectTask.setPlatformType(CloudPlatformType.valueOf(platformTypeString));
            } else {
                abstractApiManagementObjectTask.setPlatformType(CloudPlatformType.NEO);
            }
            abstractApiManagementObjectTask.setApiManagementObjectName(extension.getApiProxyName().getOrNull());
            abstractApiManagementObjectTask.setSourceFilePath(extension.getSourceFilePath().getOrNull());
            SetProperty<String> ignoreFilesListProperty = extension.getIgnoreFilesList();
            Set<String> ignoreFilesList = new HashSet<>();
            if (ignoreFilesListProperty != null && ignoreFilesListProperty.isPresent()) {
                ignoreFilesList.addAll(ignoreFilesListProperty.get());
            }
            abstractApiManagementObjectTask.setIgnoreFilesList(ignoreFilesList);
            abstractApiManagementObjectTask.setApiManagementObjectType(
                ApiManagementObjectType.valueOf(extension.getApiManagementObjectType().getOrNull())
            );
            abstractApiManagementObjectTask.setHttpClientsFactory(
                extension.getHttpClientsFactory().getOrElse(new HttpClientsFactory())
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}