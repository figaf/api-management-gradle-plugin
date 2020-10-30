package com.figaf.plugin.tasks;

import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Arsenii Istlentev
 */
@Setter
public class UploadApiProxyTask extends AbstractApiProxyTask {

    @Override
    public void doTaskAction() throws Exception {
        System.out.println("uploadApiProxy");

        Path pathToDirectoryWithExcludedFiles = Files.createTempDirectory("api-management-plugin-upload-api-proxy-" + UUID.randomUUID().toString());
        File directoryWithExcludedFiles = pathToDirectoryWithExcludedFiles.toFile();
        try {
            List<Path> pathsToExclude = new ArrayList<>();
            for (String fileNameToExclude : ignoreFilesList) {
                pathsToExclude.add(Paths.get(sourceFilePath, fileNameToExclude));
            }

            FileUtils.copyDirectory(sourceFolder, directoryWithExcludedFiles, pathname -> {
                boolean accept = true;
                for (Path pathToExclude : pathsToExclude) {
                    if (pathname.toString().contains(pathToExclude.toString())) {
                        accept = false;
                        break;
                    }
                }
                return accept;
            });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipUtil.pack(directoryWithExcludedFiles, bos);
            bos.close();
            byte[] bundledModel = bos.toByteArray();

            apiProxyObjectClient.uploadApiProxy(requestContext, apiProxyName, bundledModel);
        } finally {
            FileUtils.deleteDirectory(directoryWithExcludedFiles);
        }
    }
}
