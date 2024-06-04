package com.swygbro.trip.backend.infra.discordbot;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordMessageProvider {

    private final DiscordApiClient discordApiClient;

    public void sendMessage(String message) {
        try {
            // discordApiClient.sendMessage(new DiscordMessage(message));
        } catch (FeignException e) {
            throw new RuntimeException("디스코드 메시지 전송 실패");
        }
    }
}
