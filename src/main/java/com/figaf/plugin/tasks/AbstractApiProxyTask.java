package com.figaf.plugin.tasks;

import com.figaf.integration.apimgmt.client.ApiProxyObjectClient;
import com.figaf.integration.common.entity.CloudPlatformType;
import com.figaf.integration.common.entity.ConnectionProperties;
import com.figaf.integration.common.entity.Platform;
import com.figaf.integration.common.entity.RequestContext;
import com.figaf.integration.common.factory.HttpClientsFactory;
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

    private final static String SSO_URL = "https://accounts.sap.com/saml2/idp/sso";

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

    protected ConnectionProperties apiManagementConnectionProperties;

    protected RequestContext requestContext;

    protected File sourceFolder;

    protected ApiProxyObjectClient apiProxyObjectClient = new ApiProxyObjectClient(SSO_URL, new HttpClientsFactory());

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
        apiManagementConnectionProperties = new ConnectionProperties(url, username, password);
        System.out.println("apiManagementConnectionProperties = " + apiManagementConnectionProperties);

        requestContext = new RequestContext();
        requestContext.setCloudPlatformType(platformType);
        requestContext.setConnectionProperties(apiManagementConnectionProperties);
        requestContext.setPlatform(Platform.API_MANAGEMENT);
        requestContext.setRestTemplateWrapperKey("");

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
