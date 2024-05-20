package com.swygbro.trip.backend.domain.user.domain;

import com.swygbro.trip.backend.domain.user.dto.UserDetailDto;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;


public interface UserDao {
    Optional<UserProfileDto> getUserProfile(Long id);

    Page<UserDetailDto> findUsersByFilter(Pageable pageable,
                                          String email,
                                          String nickname,
                                          String name,
                                          String phone,
                                          String location,
                                          Nationality nationality,
                                          LocalDate birthdate,
                                          Gender gender,
                                          SignUpType signUpType);
}
