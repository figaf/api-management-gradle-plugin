package com.figaf.plugin.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arsenii Istlentev
 */
@NoArgsConstructor
@Getter
@ToString(of = {"protocol", "host", "port", "username"})
public class ApiManagementConnectionProperties {

    private String url;
    private String protocol;
    private String host;
    private Integer port;
    private String username;
    private String password;

    public ApiManagementConnectionProperties(String url, String username, String password) {
        Pattern pattern = Pattern.compile("(https?):\\/\\/([^:]+):*(\\d*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            this.protocol = matcher.group(1);
            this.host = matcher.group(2);
            String portString = matcher.group(3);
            if (NumberUtils.isParsable(portString)) {
                this.port = NumberUtils.toInt(portString);
            }
        }
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
