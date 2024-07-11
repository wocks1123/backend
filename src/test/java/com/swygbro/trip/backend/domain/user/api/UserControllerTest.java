package com.swygbro.trip.backend.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swygbro.trip.backend.domain.user.TestUserFactory;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserInfoDto;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @DisplayName("[POST] /api/v1/auth/signup 회원가입 성공(200)")
    public void createUserSuccess() throws Exception {
        // given
        CreateUserRequest request = TestUserFactory.createCreateUserRequest();
        given(userService.createUser(any())).willReturn(UserInfoDto.builder()
                .id(1L)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .name(request.getName())
                .birthdate(request.getBirthdate())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .build());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.birthdate").value(request.getBirthdate().toString()))
                .andExpect(jsonPath("$.gender").value(request.getGender().toString()))
                .andExpect(jsonPath("$.nationality").value(request.getNationality().toString()));
    }

    @ParameterizedTest
    @DisplayName("[POST] /api/v1/auth/signup 회원가입 실패(400) - 입력양식 오류")
    @CsvSource({
            // 정상
            //"user01@email.com, nickname0001, name0001, 00000000000, KOR, 1990-01-01, Male, password01!, password01!",
            //  이메일 양식 오류
            "user0email.com, nickname0001, name0001, 00000000000, Seoul, KOR, 1990-01-01, Male, password01!, password01!",
            //  닉네임 양식 오류
            "user01@email.com, nickname_length_over1341351454145145, name0001, 00000000000, Seoul, KOR, 1990-01-01, Male, password01!, password01!",
            //  이름 양식 오류
            "user01@email.com, nickname0001, name_length_over1341351454145145, 00000000000, Seoul, KOR, 1990-01-01, Male, password01!, password01!",
            //  전화번호 양식 오류
            "user01@email.com, nickname0001, name0001, 000-0000-0000, Seoul, KOR, 1990-01-01, Male, password01!, password01!",
            // 비밀번호 양식 오류
            "user01@email.com, nickname0001, name0001, 00000000000, Seoul, KOR, 1990-01-01, Male, password, password01!",
    })
    public void createUserFailInvalidInput(String email,
                                           String nickname,
                                           String name,
                                           String phone,
                                           String location,
                                           Nationality nationality,
                                           LocalDate birthdate,
                                           Gender gender,
                                           String password,
                                           String passwordCheck) throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
                email,
                nickname,
                name,
                phone,
                location,
                nationality,
                birthdate,
                gender,
                password,
                passwordCheck
        );
        given(userService.createUser(any())).willReturn(new UserInfoDto());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest());
    }

}
