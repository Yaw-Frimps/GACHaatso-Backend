package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.MeetingScheduleRequest;
import com.example.gacapp.dto.response.MeetingScheduleResponse;
import com.example.gacapp.exception.ResourceNotFoundException;
import com.example.gacapp.model.MeetingSchedule;
import com.example.gacapp.repository.MeetingScheduleRepository;
import com.example.gacapp.service.MeetingGenerationService;
import com.example.gacapp.service.MeetingScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MeetingScheduleServiceImpl
        implements MeetingScheduleService {


    private final MeetingScheduleRepository repository;

    private final MeetingGenerationService meetingGenerationService;



    private static final String SCHEDULE_NOT_FOUND =
            "Meeting schedule not found: ";



    // ================= CREATE =================


    @Override
    @Transactional
    public MeetingScheduleResponse create(
            MeetingScheduleRequest request
    ) {


        MeetingSchedule schedule =
                MeetingSchedule.builder()
                        .name(request.getName())
                        .dayOfWeek(request.getDayOfWeek())
                        .meetingTime(request.getMeetingTime())
                        .active(true)
                        .build();



        MeetingSchedule saved =
                repository.save(schedule);



        /*
         * Immediately generate the next 90 days
         * of meetings for this schedule
         */
        meetingGenerationService
                .generateFutureMeetings(saved);



        return map(saved);

    }





    // ================= UPDATE =================


    @Override
    @Transactional
    public MeetingScheduleResponse update(
            String id,
            MeetingScheduleRequest request
    ) {


        MeetingSchedule schedule =
                repository.findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        SCHEDULE_NOT_FOUND + id
                                )
                        );



        schedule.setName(
                request.getName()
        );


        schedule.setDayOfWeek(
                request.getDayOfWeek()
        );


        schedule.setMeetingTime(
                request.getMeetingTime()
        );



        MeetingSchedule updated =
                repository.save(schedule);



        /*
         * Generate missing future meetings
         * based on the updated schedule.
         *
         * Existing meetings are protected
         * because generation checks duplicates.
         */
        meetingGenerationService
                .generateFutureMeetings(updated);



        return map(updated);

    }





    // ================= DEACTIVATE =================


    @Override
    @Transactional
    public void deactivate(
            String id
    ) {


        MeetingSchedule schedule =
                repository.findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        SCHEDULE_NOT_FOUND + id
                                )
                        );



        schedule.setActive(false);


        repository.save(schedule);

    }





    // ================= GET ALL =================


    @Override
    @Transactional(readOnly = true)
    public List<MeetingScheduleResponse> getAll() {


        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();

    }





    // ================= MAPPER =================


    private MeetingScheduleResponse map(
            MeetingSchedule schedule
    ){


        return MeetingScheduleResponse.builder()

                .id(schedule.getId())

                .name(schedule.getName())

                .dayOfWeek(
                        schedule.getDayOfWeek()
                )

                .meetingTime(
                        schedule.getMeetingTime()
                )

                .active(
                        schedule.isActive()
                )

                .createdAt(
                        schedule.getCreatedAt()
                )

                .updatedAt(
                        schedule.getUpdatedAt()
                )

                .build();

    }

}