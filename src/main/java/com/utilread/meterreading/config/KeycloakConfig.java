package com.utilread.meterreading.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

    private String url;
    private String realm;
    private String clientId;

    public String getIssuerUri() {
        return url + "/realms/" + realm;
    }

    public String getJwksUri() {
        return getIssuerUri() + "/protocol/openid-connect/certs";
    }

    public String getTokenUri() {
        return getIssuerUri() + "/protocol/openid-connect/token";
    }
}