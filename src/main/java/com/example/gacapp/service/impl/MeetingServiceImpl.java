package com.example.gacapp.service.impl;




import com.example.gacapp.dto.response.MeetingResponse;
import com.example.gacapp.exception.ResourceNotFoundException;
import com.example.gacapp.model.Meeting;
import com.example.gacapp.repository.MeetingRepository;
import com.example.gacapp.service.MeetingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MeetingServiceImpl implements MeetingService {



    private final MeetingRepository meetingRepository;

    private final Clock clock;



    private static final String MEETING_NOT_FOUND =
            "Meeting not found: ";




    //=================================
    // UPCOMING MEETINGS
    //=================================

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingResponse> getUpcomingMeetings(
            Pageable pageable
    ) {


        LocalDate today =
                LocalDate.now(clock);



        return meetingRepository
                .findByMeetingDateGreaterThanEqual(
                        today,
                        pageable
                )
                .map(this::map);

    }





    //=================================
    // MEETING HISTORY
    //=================================

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingResponse> getPastMeetings(
            Pageable pageable
    ) {


        LocalDate today =
                LocalDate.now(clock);



        return meetingRepository
                .findByMeetingDateLessThan(
                        today,
                        pageable
                )
                .map(this::map);

    }





    //=================================
    // CLOSE ATTENDANCE
    //=================================

    @Override
    public void closeAttendance(
            String meetingId
    ) {


        Meeting meeting =
                meetingRepository
                        .findById(meetingId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                MEETING_NOT_FOUND + meetingId
                                        )
                        );



        if (meeting.isAttendanceClosed()) {


            throw new IllegalStateException(
                    "Attendance has already been closed"
            );

        }



        meeting.setAttendanceClosed(true);



        meetingRepository.save(meeting);



        log.info(
                "Attendance closed successfully for meeting {}",
                meetingId
        );

    }





    //=================================
    // ENTITY -> DTO
    //=================================

    private MeetingResponse map(
            Meeting meeting
    ) {


        return MeetingResponse.builder()

                .id(
                        meeting.getId()
                )

                .scheduleName(
                        meeting.getSchedule()
                                .getName()
                )

                .meetingDate(
                        meeting.getMeetingDate()
                )

                .meetingTime(
                        meeting.getMeetingTime()
                )

                .attendanceClosed(
                        meeting.isAttendanceClosed()
                )

                .createdAt(
                        meeting.getCreatedAt()
                )

                .build();

    }

}
