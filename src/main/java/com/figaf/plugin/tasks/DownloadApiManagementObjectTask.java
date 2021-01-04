package com.figaf.plugin.tasks;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arsenii Istlentev
 * @author Sergey Klochkov
 */
public class DownloadApiManagementObjectTask extends AbstractApiManagementObjectTask {

    @Override
    protected void doTaskAction() throws Exception {
        System.out.println("downloadApiManagementObject");
        switch (apiManagementObjectType) {
            case API_PROXY:
                downloadApiProxy();
                break;
            case KEY_VALUE_MAP:
                downloadKeyValueMap();
                break;
            default:
                throw new IllegalArgumentException("Unexpected api management object type: " + apiManagementObjectType);
        }
    }

    private void downloadApiProxy() throws Exception {
        Path pathToApiProxyZipArchive = Files.createTempFile(apiManagementObjectName, ".zip");
        File apiProxyZipArchiveFile = pathToApiProxyZipArchive.toFile();
        try {
            List<Path> pathsToInclude = new ArrayList<>();
            for (String fileNameToExclude : ignoreFilesList) {
                pathsToInclude.add(Paths.get(sourceFilePath, fileNameToExclude));
            }

            List<Path> sourceFolderPaths = Files.walk(sourceFolder.toPath()).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (Path path : sourceFolderPaths) {
                boolean needToDelete = true;
                for (Path pathToInclude : pathsToInclude) {
                    if (path.toString().contains(pathToInclude.toString())) {
                        needToDelete = false;
                        break;
                    }
                }
                if (needToDelete) {
                    if (!Files.isDirectory(path) || path.toFile().list() != null && path.toFile().list().length == 0 && !path.equals(sourceFolder.toPath())) {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            byte[] bundledModel = apiProxyObjectClient.downloadApiProxy(requestContext, apiManagementObjectName);
            FileUtils.writeByteArrayToFile(apiProxyZipArchiveFile, bundledModel);
            ZipUtil.unpack(apiProxyZipArchiveFile, sourceFolder);
        } finally {
            Files.deleteIfExists(pathToApiProxyZipArchive);
        }
    }

    private void downloadKeyValueMap() throws Exception {
        Map<String, String> keyToValueMap = keyMapEntriesClient.getKeyToValueMap(
            apiManagementObjectName,
            requestContext
        );

        if (keyToValueMap == null) {
            throw new IllegalArgumentException(String.format("Couldn't download key map entry %s, because it's not exist", apiManagementObjectName));
        }

        byte[] bundledModel = jsonMapper.writeValueAsBytes(keyToValueMap);

        Path pathToFile = Paths.get(sourceFilePath, String.format("%s.json", apiManagementObjectName));

        File file;
        if (Files.exists(pathToFile)) {
            file = pathToFile.toFile();
        } else {
            file = Files.createFile(pathToFile).toFile();
        }

        FileUtils.writeByteArrayToFile(file, bundledModel);
    }
}
