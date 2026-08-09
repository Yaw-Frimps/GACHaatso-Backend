package com.example.gacapp.service.impl;


import com.example.gacapp.model.Meeting;
import com.example.gacapp.model.MeetingSchedule;
import com.example.gacapp.repository.MeetingRepository;
import com.example.gacapp.repository.MeetingScheduleRepository;
import com.example.gacapp.service.MeetingGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.time.LocalDate;
import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingGenerationServiceImpl
        implements MeetingGenerationService {



    private final MeetingScheduleRepository scheduleRepository;

    private final MeetingRepository meetingRepository;

    private final Clock clock;



    @Value("${meeting.generation.days}")
    private int generationDays;



    @Override
    @Transactional
    public void generateFutureMeetings(){


        List<MeetingSchedule> schedules =
                scheduleRepository.findByActiveTrue();



        schedules.forEach(
                this::generateFutureMeetings
        );


        log.info(
                "Meeting generation completed"
        );

    }




    @Override
    @Transactional
    public void generateFutureMeetings(
            MeetingSchedule schedule
    ){


        if(!schedule.isActive()){

            log.info(
                    "Skipping inactive schedule {}",
                    schedule.getName()
            );

            return;
        }



        LocalDate today =
                LocalDate.now(clock);



        LocalDate endDate =
                today.plusDays(
                        generationDays
                );



        LocalDate date =
                today;



        while(!date.isAfter(endDate)){


            if(date.getDayOfWeek()
                    == schedule.getDayOfWeek()){


                createMeetingIfNotExists(
                        schedule,
                        date
                );

            }


            date =
                    date.plusDays(1);

        }

    }





    private void createMeetingIfNotExists(
            MeetingSchedule schedule,
            LocalDate date
    ){


        boolean exists =
                meetingRepository
                        .existsByScheduleIdAndMeetingDate(
                                schedule.getId(),
                                date
                        );



        if(exists){

            return;

        }



        Meeting meeting =
                Meeting.builder()
                        .schedule(schedule)
                        .meetingDate(date)
                        .meetingTime(
                                schedule.getMeetingTime()
                        )
                        .attendanceClosed(false)
                        .build();



        meetingRepository.save(meeting);



        log.info(
                "Generated meeting {} for {}",
                date,
                schedule.getName()
        );


    }

}