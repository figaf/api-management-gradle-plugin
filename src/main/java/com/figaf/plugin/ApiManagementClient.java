package com.figaf.plugin;

import com.figaf.plugin.entities.ApiManagementConnectionProperties;
import okhttp3.HttpUrl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Arsenii Istlentev
 */
public class ApiManagementClient {

    public void uploadApiProxy(ApiManagementConnectionProperties connectionProperties, byte[] bundledApiProxy) {
        try {

            HttpUrl.Builder uriBuilder = new HttpUrl.Builder()
                    .scheme(connectionProperties.getProtocol())
                    .host(connectionProperties.getHost())
                    .encodedPath("/apiportal/api/1.0/Transport.svc/APIProxies");
            if (connectionProperties.getPort() != null) {
                uriBuilder.port(connectionProperties.getPort());
            }
            String uri = uriBuilder.build().toString();

            HttpClient client = HttpClients.custom().build();

            String apiProxyApiCsrfToken = getCsrfToken(connectionProperties, client);

            HttpPost uploadApiProxyRequest = new HttpPost(uri);
            uploadApiProxyRequest.setHeader(createBasicAuthHeader(connectionProperties));
            uploadApiProxyRequest.setHeader("X-CSRF-Token", apiProxyApiCsrfToken);

            HttpResponse uploadApiProxyResponse = null;
            try {


                HttpEntity entity = new ByteArrayEntity(Base64.getEncoder().encode(bundledApiProxy), ContentType.APPLICATION_OCTET_STREAM);
                uploadApiProxyRequest.setEntity(entity);

                uploadApiProxyResponse = client.execute(uploadApiProxyRequest);

                switch (uploadApiProxyResponse.getStatusLine().getStatusCode()) {
                    case 200: {
                        return;
                    }
                    default: {
                        throw new RuntimeException("Couldn't execute api proxy uploading:\n" +
                                IOUtils.toString(uploadApiProxyResponse.getEntity().getContent(), "UTF-8")
                        );
                    }
                }

            } finally {
                HttpClientUtils.closeQuietly(uploadApiProxyResponse);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while while uploading an api proxy: " + ex.getMessage(), ex);
        }
    }

    public byte[] downloadApiProxy(ApiManagementConnectionProperties connectionProperties, String apiProxyName) {
        try {
            HttpUrl.Builder uriBuilder = new HttpUrl.Builder()
                    .scheme(connectionProperties.getProtocol())
                    .host(connectionProperties.getHost())
                    .encodedPath("/apiportal/api/1.0/Transport.svc/APIProxies")
                    .addQueryParameter("name", apiProxyName);
            if (connectionProperties.getPort() != null) {
                uriBuilder.port(connectionProperties.getPort());
            }
            String uri = uriBuilder.build().toString();

            HttpClient client = HttpClients.custom().build();
            HttpGet downloadApiProxyRequest = new HttpGet(uri);
            downloadApiProxyRequest.setHeader(createBasicAuthHeader(connectionProperties));

            HttpResponse downloadApiProxyResponse = null;
            try {
                downloadApiProxyResponse = client.execute(downloadApiProxyRequest);

                switch (downloadApiProxyResponse.getStatusLine().getStatusCode()) {
                    case 200: {
                        return IOUtils.toByteArray(downloadApiProxyResponse.getEntity().getContent());
                    }
                    default: {
                        throw new RuntimeException("Couldn't execute api proxy downloading:\n" +
                                IOUtils.toString(downloadApiProxyResponse.getEntity().getContent(), "UTF-8")
                        );
                    }
                }

            } finally {
                HttpClientUtils.closeQuietly(downloadApiProxyResponse);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while while downloading an api proxy: " + ex.getMessage(), ex);
        }
    }

    private String getCsrfToken(ApiManagementConnectionProperties connectionProperties, HttpClient httpClient) {

        HttpResponse headResponse = null;
        try {

            HttpUrl.Builder uriBuilder = new HttpUrl.Builder()
                    .scheme(connectionProperties.getProtocol())
                    .host(connectionProperties.getHost())
                    .encodedPath("/apiportal/api/1.0/Transport.svc/APIProxies");
            if (connectionProperties.getPort() != null) {
                uriBuilder.port(connectionProperties.getPort());
            }
            String uri = uriBuilder.build().toString();

            HttpGet getRequest = new HttpGet(uri);
            getRequest.setHeader("X-CSRF-Token", "Fetch");
            getRequest.setHeader(createBasicAuthHeader(connectionProperties));

            headResponse = httpClient.execute(getRequest);

            if (headResponse == null) {
                throw new RuntimeException(String.format("Couldn't fetch CSRF token for user %s: response is null.", connectionProperties.getUsername()));
            }

            if (ArrayUtils.isEmpty(headResponse.getHeaders("X-CSRF-Token"))) {
                throw new RuntimeException(String.format(
                        "Couldn't fetch CSRF token for user: Code: %d, Message: %s",
                        headResponse.getStatusLine().getStatusCode(),
                        IOUtils.toString(headResponse.getEntity().getContent(), "UTF-8"))
                );
            }

            return headResponse.getFirstHeader("X-CSRF-Token").getValue();


        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while fetching API Proxy transport CSRF token: " + ex.getMessage(), ex);
        } finally {
            HttpClientUtils.closeQuietly(headResponse);
        }

    }

    private Header createBasicAuthHeader(ApiManagementConnectionProperties connectionProperties) {
        return new BasicHeader(
                "Authorization",
                String.format(
                        "Basic %s",
                        org.apache.commons.codec.binary.Base64.encodeBase64String(
                                (connectionProperties.getUsername() + ":" + connectionProperties.getPassword()).getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }
}
