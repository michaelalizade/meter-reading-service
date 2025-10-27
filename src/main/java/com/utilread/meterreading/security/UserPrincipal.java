package com.utilread.meterreading.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {

    private String keycloakSub;
    private String email;
    private String fullName;
    private List<String> roles;
    private UUID userId;

}
