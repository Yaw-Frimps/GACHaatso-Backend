package com.example.gacapp.repository;

import com.example.gacapp.model.Attendance;
import com.example.gacapp.model.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, String> {

    Optional<Attendance> findByMeetingIdAndMemberId(
            String meetingId,
            String memberId
    );

    boolean existsByMeetingIdAndMemberId(
            String meetingId,
            String memberId
    );

    long countByMeetingIdAndStatus(
            String meetingId,
            AttendanceStatus status
    );

    long countByMemberIdAndStatus(
            String memberId,
            AttendanceStatus status
    );

    long countByMemberId(
            String memberId
    );

    Page<Attendance> findByMeetingId(
            String meetingId,
            Pageable pageable
    );

    Page<Attendance> findByMemberId(
            String memberId,
            Pageable pageable
    );

    Page<Attendance> findByMeetingIdAndLeaderId(
            String meetingId,
            String leaderId,
            Pageable pageable
    );

    Page<Attendance> findByLeaderId(
            String leaderId,
            Pageable pageable
    );

    @Query("""
    select a from Attendance a
    join fetch a.member
    join fetch a.leader
    join fetch a.meeting
    where a.id=:id
    """)
    Optional<Attendance> findByIdWithDetails(
            String id
    );

    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.member.id = :memberId
        AND MONTH(a.meeting.meetingDate)=:month
        AND YEAR(a.meeting.meetingDate)=:year
    """)
    long countMonthlyAttendance(
            String memberId,
            int month,
            int year
    );



    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.member.id = :memberId
        AND a.status='PRESENT'
        AND MONTH(a.meeting.meetingDate)=:month
        AND YEAR(a.meeting.meetingDate)=:year
    """)
    long countMonthlyPresent(
            String memberId,
            int month,
            int year
    );
}