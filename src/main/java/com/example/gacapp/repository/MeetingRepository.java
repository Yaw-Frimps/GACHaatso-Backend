    package com.example.gacapp.repository;

    import com.example.gacapp.model.Meeting;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Optional;

    public interface MeetingRepository
            extends JpaRepository<Meeting, String> {

        Optional<Meeting> findByScheduleIdAndMeetingDate(
                String scheduleId,
                LocalDate meetingDate
        );

        List<Meeting> findByMeetingDateBetween(
                LocalDate startDate,
                LocalDate endDate
        );

        Page<Meeting> findByMeetingDateGreaterThanEqual(
                LocalDate date,
                Pageable pageable
        );


        Page<Meeting> findByMeetingDateLessThan(
                LocalDate date,
                Pageable pageable
        );

        boolean existsByScheduleIdAndMeetingDate(
                String scheduleId,
                LocalDate meetingDate
        );

    }
