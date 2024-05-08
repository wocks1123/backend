package com.swygbro.trip.backend.infra.discordbot;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "discord-bot", url = "${discord.webhook-url}")
public interface DiscordApiClient {
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    void sendMessage(@RequestBody DiscordMessage discordMessage);
}
