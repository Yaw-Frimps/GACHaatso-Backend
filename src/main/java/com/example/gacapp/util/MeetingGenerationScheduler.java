package com.example.gacapp.util;


import com.example.gacapp.service.MeetingGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class MeetingGenerationScheduler {


    private final MeetingGenerationService meetingGenerationService;



    /**
     * Runs every day at 2:00 AM
     *
     * Ensures future meetings always exist
     */
    @Scheduled(
            cron = "0 0 2 * * *",
            zone = "Africa/Accra"
    )
    public void generateMeetings(){

        log.info("Starting automatic meeting generation");

        meetingGenerationService.generateFutureMeetings();

        log.info("Automatic meeting generation completed");

    }

}
