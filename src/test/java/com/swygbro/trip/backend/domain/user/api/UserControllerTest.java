package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @DisplayName("[POST] /api/v1/users 회원가입 성공(200)")
    public void createUserSuccess() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
                "testemail000001@email.com",
                "testnickname0001",
                "testname0001",
                "00000000000",
                Nationality.KOR,
                "1990-01-01",
                Gender.Male,
                "password01!",
                "password01!"
        );
        given(userService.createUser(any())).willReturn(1L);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson()));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @ParameterizedTest
    @DisplayName("[POST] /api/v1/users 회원가입 실패(400) - 입력양식 오류")
    @CsvSource({
            // 정상
            //"user01@email.com, nickname0001, name0001, 00000000000, KOR, 1990-01-01, Male, password01!, password01!",
            //  이메일 양식 오류
            "user0email.com, nickname0001, name0001, 00000000000, KOR, 1990-01-01, Male, password01!, password01!",
            //  닉네임 양식 오류
            "user01@email.com, nickname_length_over1341351454145145, name0001, 00000000000, KOR, 1990-01-01, Male, password01!, password01!",
            //  이름 양식 오류
            "user01@email.com, nickname0001, name_length_over1341351454145145, 00000000000, KOR, 1990-01-01, Male, password01!, password01!",
            //  전화번호 양식 오류
            "user01@email.com, nickname0001, name0001, 000-0000-0000, KOR, 1990-01-01, Male, password01!, password01!",
            // 비밀번호 양식 오류
            "user01@email.com, nickname0001, name0001, 00000000000, KOR, 1990-01-01, Male, password, password01!",
    })
    public void createUserFailInvalidInput(String email, String nickname, String name, String phone, Nationality nationality, String birthdate, Gender gender, String password, String passwordCheck) throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
                email,
                nickname,
                name,
                phone,
                nationality,
                birthdate,
                gender,
                password,
                passwordCheck
        );
        given(userService.createUser(any())).willReturn(1L);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson()));

        // then
        result.andExpect(status().isBadRequest());
    }

}
