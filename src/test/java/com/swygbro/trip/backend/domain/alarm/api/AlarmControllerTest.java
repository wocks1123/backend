package com.swygbro.trip.backend.domain.alarm.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("example@email.com")
@ActiveProfiles("test")
public class AlarmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("[ALARM][GET] /api/v1/alarm/subscribe 알람 구독 성공 (200)")
    @Test
    void subscribe() throws Exception {
        // given

        // when, then
        mockMvc.perform(
                        get("/api/v1/alarm/subscribe")
                                .with(csrf()))
                .andExpect(status().isOk());
    }
}
