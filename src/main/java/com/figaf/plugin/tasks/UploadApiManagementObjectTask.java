package com.figaf.plugin.tasks;

import com.figaf.integration.apimgmt.entity.KeyMapEntryMetaData;
import com.figaf.integration.apimgmt.entity.KeyMapEntryValue;
import com.figaf.integration.common.exception.ClientIntegrationException;
import com.figaf.plugin.enumeration.ApiManagementObjectType;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Arsenii Istlentev
 */
@Setter
public class UploadApiManagementObjectTask extends AbstractApiManagementObjectTask {

    @Override
    public void doTaskAction() throws Exception {
        System.out.println("uploadApiManagementObject");

        if (ApiManagementObjectType.API_PROXY.equals(apiManagementObjectType)) {
            uploadApiProxy();
        } else if (ApiManagementObjectType.KEY_VALUE_MAP.equals(apiManagementObjectType)) {
            uploadKeyValueMap();
        } else {
            throw new IllegalArgumentException("Unexpected api management object type: " + apiManagementObjectType);
        }
    }

    private void uploadApiProxy() throws Exception {
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

            apiProxyObjectClient.uploadApiProxy(requestContext, apiManagementObjectName, bundledModel);
        } finally {
            FileUtils.deleteDirectory(directoryWithExcludedFiles);
        }

    }

    private void uploadKeyValueMap() throws Exception {

        Path pathToFile = Paths.get(sourceFilePath, String.format("%s.json", apiManagementObjectName));
        Map<String, String> localKeyToValueMaps = jsonMapper.readValue(pathToFile.toFile(), Map.class);

        KeyMapEntryMetaData keyMapEntryMetaData = new KeyMapEntryMetaData();
        keyMapEntryMetaData.setName(apiManagementObjectName);
        keyMapEntryMetaData.setEncrypted(false);
        keyMapEntryMetaData.setScope("ENV");

        keyMapEntryMetaData.setKeyMapEntryValues(new ArrayList<>());
        KeyMapEntryValue keyMapEntryValue;

        for (Map.Entry<String, String> localKeyToValueMap : localKeyToValueMaps.entrySet()) {
            keyMapEntryValue = new KeyMapEntryValue(
                apiManagementObjectName,
                localKeyToValueMap.getKey(),
                localKeyToValueMap.getValue()
            );
            keyMapEntryMetaData.getKeyMapEntryValues().add(keyMapEntryValue);
        }

        keyMapEntriesClient.createOrUpdateKeyMapEntry(keyMapEntryMetaData, requestContext);

    }
}
