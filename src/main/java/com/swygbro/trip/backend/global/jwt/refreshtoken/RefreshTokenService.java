package com.swygbro.trip.backend.global.jwt.refreshtoken;

import com.swygbro.trip.backend.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String email) {
        String token = tokenService.createRefreshToken(email);
        refreshTokenRepository.save(new RefreshToken(email, token));
        return token;
    }
    
}
