package com.example.gacapp.service;

import com.example.gacapp.dto.response.MeetingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeetingService {

    Page<MeetingResponse> getUpcomingMeetings(Pageable pageable);

    Page<MeetingResponse> getPastMeetings(Pageable pageable);

    void closeAttendance(String meetingId);

}