package com.utilread.meterreading.service;

import com.utilread.meterreading.dto.request.CreateMeterRequest;
import com.utilread.meterreading.exception.DuplicateResourceException;
import com.utilread.meterreading.mapper.MeterMapper;
import com.utilread.meterreading.model.MeterType;
import com.utilread.meterreading.model.User;
import com.utilread.meterreading.repository.MeterRepository;
import com.utilread.meterreading.security.SecurityContext;
import com.utilread.meterreading.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class MeterServiceTest {

    @Mock
    private MeterRepository meterRepository;

    @Mock
    private UserService userService;

    @Mock
    private MeterMapper meterMapper;

    @InjectMocks
    private MeterService meterService;

    private UserPrincipal principal;
    private User user;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        principal = UserPrincipal.builder()
                .keycloakSub("sub123")
                .email("john@example.com")
                .fullName("John Doe")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .email("john@example.com")
                .fullName("John Doe")
                .keycloakSub("sub123")
                .build();

        mockStatic(SecurityContext.class).when(SecurityContext::getCurrentUser).thenReturn(principal);
    }

    @Test
    void createMeter_shouldThrowDuplicateException_whenMeterExists(){
        CreateMeterRequest request = new CreateMeterRequest();
        request.setMeterNumber("12345");
        request.setMeterType(MeterType.WATER);

        when(userService.getOrCreateUser(principal)).thenReturn(user);
        when(meterRepository.existsByUserIdAndMeterNumber(user.getId(), request.getMeterNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> meterService.createMeter(request));
    }
    // TODO: mockstatic isn't desirable. Why and what problems could arise.

}
