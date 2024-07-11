package com.swygbro.trip.backend.domain.user;

import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;

import java.time.LocalDate;

public class TestUserFactory {
    public static User createTestUser(String email,
                                      String nickname,
                                      String name,
                                      String phone,
                                      String location,
                                      Nationality nationality,
                                      String birthdate,
                                      Gender gender,
                                      String password) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .name(name)
                .phone(phone)
                .location(location)
                .nationality(nationality)
                .birthdate(java.time.LocalDate.parse(birthdate))
                .gender(gender)
                .password(password)
                .signUpType(SignUpType.Local)
                .build();
    }

    public static User createTestUser() {
        return createTestUser(
                "test@gmail.com",
                "test",
                "test",
                "01012345678",
                "Seoul",
                Nationality.KOR,
                "1990-01-01",
                Gender.Male,
                "password"
        );
    }

    public static CreateUserRequest createCreateUserRequest(String email,
                                                            String nickname,
                                                            String name,
                                                            String phone,
                                                            String location,
                                                            Nationality nationality,
                                                            LocalDate birthdate,
                                                            Gender gender,
                                                            String password,
                                                            String passwordCheck) {
        return new CreateUserRequest(
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
    }

    public static CreateUserRequest createCreateUserRequest() {
        return new CreateUserRequest(
                "testemail000001@email.com",
                "testnickname0001",
                "testname0001",
                "+01012345679",
                "Seoul",
                Nationality.KOR,
                LocalDate.of(2000, 1, 1),
                Gender.Male,
                "password01",
                "password01"
        );
    }

    public static CreateUserRequest createCreateUserRequestWithDifferentPassword() {
        return new CreateUserRequest(
                "testemail000001@email.com",
                "testnickname0001",
                "testname0001",
                "+01012345679",
                "Seoul",
                Nationality.KOR,
                LocalDate.of(2000, 1, 1),
                Gender.Male,
                "password01",
                "password02"
        );
    }
}
