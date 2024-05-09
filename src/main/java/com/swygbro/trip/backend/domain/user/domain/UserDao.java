package com.swygbro.trip.backend.domain.user.domain;

import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;

import java.util.Optional;


public interface UserDao {
    Optional<UserProfileDto> getUserProfile(Long id);
}
