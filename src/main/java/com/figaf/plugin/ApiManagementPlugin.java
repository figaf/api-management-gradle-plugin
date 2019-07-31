package com.figaf.plugin;

import com.figaf.plugin.tasks.UploadApiProxy;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author Arsenii Istlentev
 */
public class ApiManagementPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.getTasks().register("uploadApiProxy", UploadApiProxy.class, uploadApiProxy -> {
        });

    }

}