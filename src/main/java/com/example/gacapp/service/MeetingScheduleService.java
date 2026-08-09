package com.example.gacapp.service;

import com.example.gacapp.dto.request.MeetingScheduleRequest;
import com.example.gacapp.dto.response.MeetingScheduleResponse;

import java.util.List;

public interface MeetingScheduleService {

    MeetingScheduleResponse create(MeetingScheduleRequest request);

    MeetingScheduleResponse update(
            String id,
            MeetingScheduleRequest request
    );

    void deactivate(String id);

    List<MeetingScheduleResponse> getAll();

}