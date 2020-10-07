package com.figaf.plugin;

import com.figaf.plugin.entities.CloudPlatformType;
import com.figaf.plugin.tasks.AbstractApiProxyTask;
import com.figaf.plugin.tasks.DownloadApiProxyTask;
import com.figaf.plugin.tasks.UploadApiProxyTask;
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

        project.getTasks().register("uploadApiProxy", UploadApiProxyTask.class, uploadApiProxy -> applyExtension(uploadApiProxy, extension));

        project.getTasks().register("downloadApiProxy", DownloadApiProxyTask.class, downloadApiProxy -> applyExtension(downloadApiProxy, extension));

    }

    private void applyExtension(AbstractApiProxyTask abstractApiProxyTask, ApiManagementPluginExtension extension) {
        try {
            abstractApiProxyTask.setUrl(extension.getUrl().getOrNull());
            abstractApiProxyTask.setUsername(extension.getUsername().getOrNull());
            abstractApiProxyTask.setPassword(extension.getPassword().getOrNull());
            String platformTypeString = extension.getPlatformType().getOrNull();
            if (platformTypeString != null) {
                abstractApiProxyTask.setPlatformType(CloudPlatformType.valueOf(platformTypeString));
            } else {
                abstractApiProxyTask.setPlatformType(CloudPlatformType.NEO);
            }
            abstractApiProxyTask.setApiProxyName(extension.getApiProxyName().getOrNull());
            abstractApiProxyTask.setSourceFilePath(extension.getSourceFilePath().getOrNull());
            SetProperty<String> ignoreFilesListProperty = extension.getIgnoreFilesList();
            Set<String> ignoreFilesList = new HashSet<>();
            if (ignoreFilesListProperty != null && ignoreFilesListProperty.isPresent()) {
                ignoreFilesList.addAll(ignoreFilesListProperty.get());
            }
            abstractApiProxyTask.setIgnoreFilesList(ignoreFilesList);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}