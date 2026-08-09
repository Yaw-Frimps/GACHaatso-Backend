package com.example.gacapp.repository;


import com.example.gacapp.model.MeetingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface MeetingScheduleRepository
        extends JpaRepository<MeetingSchedule, String> {

    List<MeetingSchedule> findByActiveTrue();

    List<MeetingSchedule> findByDayOfWeekAndActiveTrue(
            DayOfWeek dayOfWeek
    );
    

}
