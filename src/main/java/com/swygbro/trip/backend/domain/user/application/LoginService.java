package com.swygbro.trip.backend.domain.user.application;

import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.LoginRequest;
import com.swygbro.trip.backend.domain.user.excepiton.LoginFailException;
import com.swygbro.trip.backend.global.jwt.TokenService;
import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenDto login(LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(LoginFailException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new LoginFailException();
        }

        return tokenService.generateToken(user.getEmail());
    }
}
