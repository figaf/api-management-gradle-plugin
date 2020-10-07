package com.figaf.plugin.client;

import com.figaf.plugin.client.wrapper.ApiManagementCommonClientWrapper;
import com.figaf.plugin.entities.ApiManagementConnectionProperties;
import com.figaf.plugin.entities.RestTemplateWrapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

/**
 * @author Arsenii Istlentev
 */
public class ApiManagementClient extends ApiManagementCommonClientWrapper {

    private static final String API_PROXIES_TRANSPORT_WITH_NAME = "/apiportal/api/1.0/Transport.svc/APIProxies?name=%s";
    private static final String API_PROXIES_TRANSPORT = "/apiportal/api/1.0/Transport.svc/APIProxies";

    public byte[] downloadApiProxy(ApiManagementConnectionProperties apiManagementConnectionProperties, String apiProxyName) {
        return executeGet(
                apiManagementConnectionProperties,
                String.format(API_PROXIES_TRANSPORT_WITH_NAME, apiProxyName),
                resolvedBody -> resolvedBody,
                byte[].class
        );
    }

    public void uploadApiProxy(ApiManagementConnectionProperties apiManagementConnectionProperties, byte[] bundledApiProxy) {
        RestTemplateWrapper restTemplateWrapper = getRestTemplateWrapper(apiManagementConnectionProperties);
        String token = retrieveToken(apiManagementConnectionProperties, restTemplateWrapper.getRestTemplate(), "/apiportal/api/1.0/Management.svc/APIProxies");

        String url = buildUrl(apiManagementConnectionProperties, API_PROXIES_TRANSPORT);

        uploadApiProxy(bundledApiProxy, url, restTemplateWrapper.getRestTemplate(), token);
    }

    private void uploadApiProxy(byte[] bundledApiProxy, String url, RestTemplate restTemplate, String token) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-CSRF-Token", token);
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        org.springframework.http.HttpEntity<byte[]> requestEntity = new HttpEntity<>(Base64.getEncoder().encode(bundledApiProxy), httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException("Couldn't execute api proxy uploading:\n" +
                    responseEntity.getBody()
            );
        }

    }
}
