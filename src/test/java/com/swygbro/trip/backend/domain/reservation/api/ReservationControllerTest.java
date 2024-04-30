package com.swygbro.trip.backend.domain.reservation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("[POST] /api/v1/reservation/client/save 예약 정보 저장 성공(200)")
    public void saveReservation() throws Exception {
        // given
        SaveReservationRequest request = SaveReservationRequest.builder()
                .guideId(1L)
                .productId(1)
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .build();
        String impUid = "imp_1234567890";

        given(reservationService.saveReservation(any())).willReturn(impUid);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/reservation/client/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));
        // then
        result.andExpect(status().isOk())
                .andExpect(content().string(impUid));
    }

    @ParameterizedTest
    @DisplayName("[POST] /api/v1/reservation/client/save 예약 정보 저장 실패(400)")
    @CsvSource({
            // [0] 인원 오류
            "1, 1, 2024-04-29 12:30:45, 0, 안녕하세요, 10000",
    })
    public void saveReservationFailInvalidInput(Long guideId, Integer productId, Timestamp reservatedAt, Integer personnel, String message, Integer price) throws Exception {
        // given
        SaveReservationRequest request = SaveReservationRequest.builder()
                .guideId(guideId)
                .productId(productId)
                .reservatedAt(reservatedAt)
                .personnel(personnel)
                .message(message)
                .price(price)
                .build();
        String impUid = "imp_1234567890";

        given(reservationService.saveReservation(any())).willReturn(impUid);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/reservation/client/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));
        // then
        log.info(result.toString());
        log.info("result status: {}", result.andReturn().getResponse().getStatus());

        result.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("[POST] /api/v1/reservation/client/payment 결제 정보 저장 실패(400)")
    @CsvSource({
            // [0] 인원 오류
            "20240430-merchant, 1, imp_1234567890, 1648344363, 1000, 0",
            // [1] 주문 번호 오류
            ", 1, imp_1234567890, 1648344363, 1000, 1",

    })
    public void savePaymentFailInvalidInput(String merchantUid, Long ProductId, String impUid, Long paidAt, Integer price, Integer personnel) throws Exception {
        // given
        SavePaymentRequest request = SavePaymentRequest.builder()
                .merchantUid(merchantUid)
                .productId(ProductId)
                .impUid(impUid)
                .paidAt(paidAt)
                .price(price)
                .personnel(personnel)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/reservation/client/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());

    }
}