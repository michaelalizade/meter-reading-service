package com.utilread.meterreading.service;

import com.utilread.meterreading.dto.request.CreateMeterRequest;
import com.utilread.meterreading.dto.response.MeterResponse;
import com.utilread.meterreading.mapper.MeterMapper;
import com.utilread.meterreading.model.MeterType;
import com.utilread.meterreading.model.User;
import com.utilread.meterreading.repository.MeterRepository;
import com.utilread.meterreading.repository.UserRepository;
import com.utilread.meterreading.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MeterServiceIntegrationTest {

    @Autowired
    private MeterService meterService;
    @Autowired
    private MeterRepository meterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MeterMapper meterMapper;

    private User user;

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder()
                .email("user@test.com")
                .fullName("Test User")
                .keycloakSub("sub123")
                .build());

        var principal = UserPrincipal.builder()
                .keycloakSub("sub123")
                .email("john@example.com")
                .fullName("John Doe")
                .build();
    }

    @Test
    void createMeter_shouldPersistMeter() {
        CreateMeterRequest request = new CreateMeterRequest();
        request.setMeterNumber("1001");
        request.setMeterType(MeterType.GAS);

        MeterResponse response = meterService.createMeter(request);

        assertThat(response).isNotNull();
        assertThat(response.getMeterNumber()).isEqualTo("1001");
        assertThat(meterRepository.findAll()).hasSize(1);
    }
}
