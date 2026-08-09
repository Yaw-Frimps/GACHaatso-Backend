package com.example.gacapp.service;

import com.example.gacapp.model.MeetingSchedule;

public interface MeetingGenerationService {

    void generateFutureMeetings();

    void generateFutureMeetings(MeetingSchedule schedule);

}
