package com.swygbro.trip.backend.domain.user.application;

import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.domain.user.excepiton.DuplicateUserAccountException;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String createUser(CreateUserRequest dto) {
        userRepository.findByAccount(dto.getAccount())
                .ifPresent(user -> {
                    throw new DuplicateUserAccountException(user.getAccount());
                });

        User createdUser = userRepository.save(new User(dto.getAccount(), dto.getEmail(), dto.getPassword()));
        return createdUser.getAccount();
    }

    public UserProfileDto getUser(String account) {
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));
        return new UserProfileDto(user.getAccount(), user.getEmail());
    }

}
