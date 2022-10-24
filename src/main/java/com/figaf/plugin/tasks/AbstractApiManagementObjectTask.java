package com.figaf.plugin.tasks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.figaf.integration.apimgmt.client.ApiProxyObjectClient;
import com.figaf.integration.apimgmt.client.KeyMapEntriesClient;
import com.figaf.integration.common.entity.*;
import com.figaf.plugin.enumeration.ApiManagementObjectType;
import com.figaf.integration.common.factory.HttpClientsFactory;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.*;

/**
 * @author Arsenii Istlentev
 */
@Setter
@ToString
public abstract class AbstractApiManagementObjectTask extends DefaultTask {

    protected final static ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Input
    protected String url;

    @Input
    protected String username;

    @Input
    protected String password;

    @Input
    protected CloudPlatformType platformType;

    @Input
    protected String oauthTokenUrl;

    @Input
    protected AuthenticationType authenticationType;

    @Input
    protected String publicApiUrl;

    @Input
    protected String publicApiClientId;

    @Input
    protected String publicApiClientSecret;

    @Input
    protected String sourceFilePath;

    @Input
    protected String apiManagementObjectName;

    @Input
    protected Set<String> ignoreFilesList;

    @Input
    protected ApiManagementObjectType apiManagementObjectType;

    @Input
    protected HttpClientsFactory httpClientsFactory;

    protected ConnectionProperties apiManagementConnectionProperties;

    protected RequestContext requestContext;

    protected File sourceFolder;

    protected ApiProxyObjectClient apiProxyObjectClient;

    protected KeyMapEntriesClient keyMapEntriesClient;

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
        if (AuthenticationType.OAUTH.equals(authenticationType) && StringUtils.isNotEmpty(publicApiUrl)) {
            apiManagementConnectionProperties = new ConnectionProperties(publicApiUrl, username, password);
        } else {
            apiManagementConnectionProperties = new ConnectionProperties(url, username, password);
        }
        System.out.println("apiManagementConnectionProperties = " + apiManagementConnectionProperties);
        System.out.println("httpClientsFactory = " + httpClientsFactory);
        System.out.println("oauthTokenUrl = " + oauthTokenUrl);
        System.out.println("authenticationType = " + authenticationType);
        System.out.println("publicApiClientId = " + publicApiClientId);

        apiProxyObjectClient = new ApiProxyObjectClient(httpClientsFactory);
        keyMapEntriesClient = new KeyMapEntriesClient(httpClientsFactory);

        requestContext = new RequestContext();
        requestContext.setCloudPlatformType(platformType);
        requestContext.setConnectionProperties(apiManagementConnectionProperties);
        requestContext.setPlatform(Platform.API_MANAGEMENT);
        requestContext.setOauthUrl(oauthTokenUrl);
        requestContext.setAuthenticationType(authenticationType);
        requestContext.setClientId(publicApiClientId);
        requestContext.setClientSecret(publicApiClientSecret);
        requestContext.setRestTemplateWrapperKey("");

        sourceFolder = new File(sourceFilePath);

        if (apiManagementObjectName == null) {
            apiManagementObjectName = sourceFolder.getName();
        }

        if (CollectionUtils.isEmpty(ignoreFilesList)) {
            ignoreFilesList = new HashSet<>();
        }
        ignoreFilesList.add("src/test");
        ignoreFilesList.add("build.gradle");
        ignoreFilesList.add("gradle.properties");
        ignoreFilesList.add("settings.gradle");

        System.out.println("apiManagementObjectName = " + apiManagementObjectName);
        System.out.println("ignoreFilesList = " + ignoreFilesList);
    }
}
