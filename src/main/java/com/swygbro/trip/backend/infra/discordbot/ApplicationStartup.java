package com.swygbro.trip.backend.infra.discordbot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartup {

    private final DiscordMessageProvider discordMessageProvider;

    @Value("${version}")
    private String version;

    @Profile("deploy")
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        discordMessageProvider.sendMessage(generateStartupMessage());
    }

    @Profile("!deploy")
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyLocal() {
        log.info(generateStartupMessage());
    }

    private String generateStartupMessage() {
        return """
                ```
                                    ,--.    ,--.  ,--.
                ,--,--,--. ,--,--.,-'  '-.,-'  '-.|  ,---. ,---. ,--.   ,--.
                |        |' ,-.  |'-.  .-''-.  .-'|  .-.  | .-. :|  |.'.|  |
                |  |  |  |\\ '-'  |  |  |    |  |  |  | |  \\   --.|   .'.   |
                `--`--`--' `--`--'  `--'    `--'  `--' `--'`----''--'   '--'
                version    : v%s
                started at : %s
                ```
                """.formatted(version, ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}
