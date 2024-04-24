package com.swygbro.trip.backend.domain.user.application;

import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.domain.user.excepiton.DuplicateUserAccountException;
import com.swygbro.trip.backend.domain.user.excepiton.PasswordNotMatchException;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private boolean isUniqueUser(CreateUserRequest dto) {
        return userRepository.existsByAccount(dto.getAccount()) &&
                userRepository.existsByNickname(dto.getNickname()) &&
                userRepository.existsByName(dto.getName()) &&
                userRepository.existsByPhone(dto.getPhone()) &&
                userRepository.existsByEmail(dto.getEmail());
    }

    public String createUser(CreateUserRequest dto) {
        if (isUniqueUser(dto)) {
            throw new DuplicateUserAccountException(dto.getAccount());
        }

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new PasswordNotMatchException();
        }

        User createdUser = userRepository.save(
                new User(dto, SignUpType.Local, passwordEncoder.encode(dto.getPassword()))
        );

        return createdUser.getAccount();
    }

    public UserProfileDto getUser(String account) {
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));
        return new UserProfileDto(user.getAccount(), user.getEmail());
    }

}
