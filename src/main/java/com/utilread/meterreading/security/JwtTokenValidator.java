package com.utilread.meterreading.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.utilread.meterreading.config.KeycloakConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final KeycloakConfig keycloakConfig;
    private RSAPublicKey publicKey;

    public UserPrincipal validateToken(String token){
        try {
            if(token.startsWith("Bearer ")){
                token = token.substring(7);
            }

            DecodedJWT decodedJWT = JWT.decode(token);

            // TODO: in production, fetch and cache public key from Keycloak JWT endpoint

            String subject = decodedJWT.getSubject();
            String email = decodedJWT.getClaim("email").asString();
            String name = decodedJWT.getClaim("name").asString();

            List<String> roles = extractRoles(decodedJWT);

            String issuer = decodedJWT.getIssuer();
            if(!issuer.equals(keycloakConfig.getIssuerUri())){
                log.warn("Invalid issuer: expected {}, got {}", keycloakConfig.getIssuerUri(), issuer);
                throw new SecurityException("Invalid token issuer");
            }

            if(decodedJWT.getExpiresAt().before(new java.util.Date())){
                throw new SecurityException("Token is expired");
            }

            log.debug("Token is validated successfully for user: {}", email);

            return UserPrincipal.builder()
                    .keycloakSub(subject)
                    .email(email)
                    .fullName(name)
                    .roles(roles)
                    .build();
        }catch (Exception e){
            log.error("Token validation failed: {}", e.getMessage());
            throw new SecurityException("Invalid token: " + e.getMessage());
        }

    }

    private List<String> extractRoles(DecodedJWT jwt){
        try{

            Map<String, Object> realmAccess = jwt.getClaim("realm_access").asMap();
            if(realmAccess != null && realmAccess.containsKey("roles")){
                return (List<String>) realmAccess.get("roles");
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access").asMap();
            if(resourceAccess != null){
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(keycloakConfig.getClientId());
                if(clientAccess != null && clientAccess.containsKey("roles")){
                    return (List<String>) clientAccess.get("roles");
                }
            }

        }catch (Exception e){
            log.warn("Could not extract roles from token {}: " + e.getMessage());
        }
        return List.of();
    }

}
