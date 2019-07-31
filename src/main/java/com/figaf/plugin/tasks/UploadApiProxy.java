package com.figaf.plugin.tasks;

import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Arsenii Istlentev
 */
@Setter
public class UploadApiProxy extends DefaultTask {

    @TaskAction
    public void doAction() {
        System.out.println("Hello world");
    }
}
