package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.AttendanceRequest;
import com.example.gacapp.dto.request.BulkAttendanceRequest;
import com.example.gacapp.dto.response.AttendanceReportResponse;
import com.example.gacapp.dto.response.AttendanceResponse;
import com.example.gacapp.dto.response.MeetingAttendanceSummaryResponse;
import com.example.gacapp.exception.ResourceNotFoundException;
import com.example.gacapp.model.Attendance;
import com.example.gacapp.model.AttendanceStatus;
import com.example.gacapp.model.Members;
import com.example.gacapp.model.Meeting;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.AttendanceRepository;
import com.example.gacapp.repository.MemberRepository;
import com.example.gacapp.repository.MeetingRepository;
import com.example.gacapp.service.AttendanceService;
import com.example.gacapp.service.AuthService;
import com.example.gacapp.util.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final AttendanceMapper mapper;
    private final Clock clock;

    private static final String ATTENDANCE_NOT_FOUND =
            "Attendance not found: ";

    private static final String MEETING_NOT_FOUND =
            "Meeting not found: ";

    private static final String MEMBER_NOT_FOUND =
            "Member not found: ";


    // =========================================================
    // MARK ATTENDANCE
    // =========================================================

    @Override
    public AttendanceResponse markAttendance(
            AttendanceRequest request
    ) {

        User leader = authService.getLoggedInUser();

        validateLeader(leader);

        Meeting meeting = getMeeting(request.getMeetingId());

        validateMeetingOpen(meeting);

        Members member = getMember(request.getMemberId());

        validateMemberBelongsToLeader(member, leader);

        validateAttendanceStatus(request);

        validateAttendanceDoesNotExist(
                meeting.getId(),
                member.getId()
        );

        Attendance attendance = Attendance.builder()
                .meeting(meeting)
                .member(member)
                .leader(leader)
                .status(request.getStatus())
                .absenceReason(
                        request.getStatus() == AttendanceStatus.ABSENT
                                ? request.getAbsenceReason().trim()
                                : null
                )
                .markedAt(LocalDateTime.now(clock))
                .build();

        Attendance saved =
                attendanceRepository.save(attendance);

        log.info(
                "Attendance marked. leader={}, member={}, meeting={}, status={}",
                leader.getId(),
                member.getId(),
                meeting.getId(),
                request.getStatus()
        );

        return mapper.toDTO(saved);
    }


    // =========================================================
    // BULK ATTENDANCE
    // =========================================================

    @Override
    public Page<AttendanceResponse> markBulkAttendance(
            BulkAttendanceRequest request
    ) {

        User leader = authService.getLoggedInUser();

        validateLeader(leader);

        Meeting meeting = getMeeting(request.getMeetingId());

        validateMeetingOpen(meeting);

        if (request.getAttendance() == null ||
                request.getAttendance().isEmpty()) {

            throw new IllegalArgumentException(
                    "Attendance list cannot be empty"
            );
        }

        List<Attendance> attendanceRecords =
                request.getAttendance()
                        .stream()
                        .map(item -> {

                            if (item.getStatus() == null) {
                                throw new IllegalArgumentException(
                                        "Attendance status is required"
                                );
                            }

                            Members member =
                                    getMember(item.getMemberId());

                            validateMemberBelongsToLeader(
                                    member,
                                    leader
                            );

                            validateAttendanceStatus(item);

                            validateAttendanceDoesNotExist(
                                    meeting.getId(),
                                    member.getId()
                            );

                            return Attendance.builder()
                                    .meeting(meeting)
                                    .member(member)
                                    .leader(leader)
                                    .status(item.getStatus())
                                    .absenceReason(
                                            item.getStatus()
                                                    == AttendanceStatus.ABSENT
                                                    ? item.getAbsenceReason().trim()
                                                    : null
                                    )
                                    .markedAt(
                                            LocalDateTime.now(clock)
                                    )
                                    .build();

                        })
                        .toList();

        List<Attendance> saved =
                attendanceRepository.saveAll(
                        attendanceRecords
                );

        log.info(
                "Bulk attendance marked. leader={}, meeting={}, records={}",
                leader.getId(),
                meeting.getId(),
                saved.size()
        );

        List<AttendanceResponse> response =
                saved.stream()
                        .map(mapper::toDTO)
                        .toList();

        return new PageImpl<>(
                response,
                Pageable.unpaged(),
                response.size()
        );
    }


    // =========================================================
    // UPDATE ATTENDANCE
    // =========================================================

    @Override
    public AttendanceResponse updateAttendance(
            String attendanceId,
            AttendanceRequest request
    ) {

        User leader = authService.getLoggedInUser();

        validateLeader(leader);

        Attendance attendance =
                attendanceRepository.findByIdWithDetails(
                                attendanceId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        ATTENDANCE_NOT_FOUND + attendanceId
                                )
                        );

        validateAttendanceOwner(
                attendance,
                leader
        );

        validateMeetingOpen(
                attendance.getMeeting()
        );

        validateAttendanceStatus(request);

        attendance.setStatus(
                request.getStatus()
        );

        attendance.setAbsenceReason(
                request.getStatus() == AttendanceStatus.ABSENT
                        ? request.getAbsenceReason().trim()
                        : null
        );

        attendance.setMarkedAt(
                LocalDateTime.now(clock)
        );

        Attendance updated =
                attendanceRepository.save(attendance);

        return mapper.toDTO(updated);
    }


    // =========================================================
    // GET SINGLE ATTENDANCE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendance(
            String attendanceId
    ) {

        Attendance attendance =
                attendanceRepository.findByIdWithDetails(
                                attendanceId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        ATTENDANCE_NOT_FOUND + attendanceId
                                )
                        );

        User user = authService.getLoggedInUser();

        validateAttendanceOwner(
                attendance,
                user
        );

        return mapper.toDTO(attendance);
    }


    // =========================================================
    // GET MEETING ATTENDANCE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getMeetingAttendance(
            String meetingId,
            Pageable pageable
    ) {

        User user = authService.getLoggedInUser();

        validateLeaderOrAdmin(user);

        // Make sure the meeting actually exists.
        getMeeting(meetingId);

        if (user.getRole() == UserRole.ADMIN) {

            return attendanceRepository
                    .findByMeetingId(
                            meetingId,
                            pageable
                    )
                    .map(mapper::toDTO);
        }

        return attendanceRepository
                .findByMeetingIdAndLeaderId(
                        meetingId,
                        user.getId(),
                        pageable
                )
                .map(mapper::toDTO);
    }


    // =========================================================
    // MEMBER ATTENDANCE HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getMemberAttendance(
            String memberId,
            Pageable pageable
    ) {

        User user = authService.getLoggedInUser();

        validateLeaderOrAdmin(user);

        Members member = getMember(memberId);

        if (user.getRole() == UserRole.LEADER) {

            validateMemberBelongsToLeader(
                    member,
                    user
            );
        }

        return attendanceRepository
                .findByMemberId(
                        memberId,
                        pageable
                )
                .map(mapper::toDTO);
    }


    // =========================================================
    // LEADER ATTENDANCE RECORDS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getLeaderAttendance(
            Pageable pageable
    ) {

        User leader =
                authService.getLoggedInUser();

        validateLeader(leader);

        return attendanceRepository
                .findByLeaderId(
                        leader.getId(),
                        pageable
                )
                .map(mapper::toDTO);
    }


    // =========================================================
    // DELETE ATTENDANCE
    // =========================================================

    @Override
    public void deleteAttendance(
            String attendanceId
    ) {

        User leader =
                authService.getLoggedInUser();

        validateLeader(leader);

        Attendance attendance =
                attendanceRepository.findByIdWithDetails(
                                attendanceId
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        ATTENDANCE_NOT_FOUND + attendanceId
                                )
                        );

        validateAttendanceOwner(
                attendance,
                leader
        );

        validateMeetingOpen(
                attendance.getMeeting()
        );

        attendanceRepository.delete(attendance);

        log.info(
                "Attendance deleted. attendance={}, leader={}",
                attendanceId,
                leader.getId()
        );
    }


    // =========================================================
    // MEMBER ATTENDANCE REPORT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AttendanceReportResponse getMemberAttendanceReport(
            String memberId
    ) {

        User user =
                authService.getLoggedInUser();

        Members member =
                getMember(memberId);

        if (user.getRole() == UserRole.LEADER) {

            validateMemberBelongsToLeader(
                    member,
                    user
            );

        } else if (user.getRole() != UserRole.ADMIN) {

            throw new AccessDeniedException(
                    "You cannot view this attendance report"
            );
        }

        long total =
                attendanceRepository.countByMemberId(
                        memberId
                );

        long present =
                attendanceRepository.countByMemberIdAndStatus(
                        memberId,
                        AttendanceStatus.PRESENT
                );

        long absent =
                attendanceRepository.countByMemberIdAndStatus(
                        memberId,
                        AttendanceStatus.ABSENT
                );

        double percentage =
                calculatePercentage(
                        present,
                        total
                );

        return AttendanceReportResponse.builder()
                .memberId(member.getId())
                .memberName(
                        buildFullName(
                                member.getFirstName(),
                                member.getLastName()
                        )
                )
                .totalMeetings(total)
                .present(present)
                .absent(absent)
                .attendancePercentage(percentage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceReportResponse> getLeaderAttendanceReport(
            Pageable pageable
    ) {

        User leader = authService.getLoggedInUser();

        validateLeader(leader);

        Page<Members> members =
                memberRepository.findByLeaderId(
                        leader.getId(),
                        pageable
                );

        return members.map(member -> {

            long total =
                    attendanceRepository.countByMemberId(
                            member.getId()
                    );

            long present =
                    attendanceRepository.countByMemberIdAndStatus(
                            member.getId(),
                            AttendanceStatus.PRESENT
                    );

            long absent =
                    attendanceRepository.countByMemberIdAndStatus(
                            member.getId(),
                            AttendanceStatus.ABSENT
                    );

            double percentage =
                    total == 0
                            ? 0.0
                            : ((double) present / total) * 100.0;

            return AttendanceReportResponse.builder()
                    .memberId(member.getId())
                    .memberName(
                            member.getFirstName()
                                    + " "
                                    + member.getLastName()
                    )
                    .totalMeetings(total)
                    .present(present)
                    .absent(absent)
                    .attendancePercentage(percentage)
                    .build();
        });
    }


    // =========================================================
    // MEETING ATTENDANCE SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public MeetingAttendanceSummaryResponse getMeetingSummary(
            String meetingId
    ) {

        User user =
                authService.getLoggedInUser();

        validateLeaderOrAdmin(user);

        Meeting meeting =
                getMeeting(meetingId);

        long present =
                attendanceRepository.countByMeetingIdAndStatus(
                        meetingId,
                        AttendanceStatus.PRESENT
                );

        long absent =
                attendanceRepository.countByMeetingIdAndStatus(
                        meetingId,
                        AttendanceStatus.ABSENT
                );

        long totalMarked =
                present + absent;

        return MeetingAttendanceSummaryResponse.builder()
                .meetingId(meeting.getId())
                .totalMarked(totalMarked)
                .present(present)
                .absent(absent)
                .build();
    }


    // =========================================================
    // MONTHLY REPORT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceReportResponse> getMonthlyReport(
            int month,
            int year,
            Pageable pageable
    ) {

        User user =
                authService.getLoggedInUser();

        if (user.getRole() != UserRole.ADMIN) {

            throw new AccessDeniedException(
                    "Only administrators can view the monthly report"
            );
        }

        validateMonth(month);
        validateYear(year);

        Page<Members> members =
                memberRepository.findAll(pageable);

        return members.map(member -> {

            long total =
                    attendanceRepository
                            .countMonthlyAttendance(
                                    member.getId(),
                                    month,
                                    year
                            );

            long present =
                    attendanceRepository
                            .countMonthlyPresent(
                                    member.getId(),
                                    month,
                                    year
                            );

            long absent =
                    total - present;

            double percentage =
                    calculatePercentage(
                            present,
                            total
                    );

            return AttendanceReportResponse.builder()
                    .memberId(member.getId())
                    .memberName(
                            buildFullName(
                                    member.getFirstName(),
                                    member.getLastName()
                            )
                    )
                    .totalMeetings(total)
                    .present(present)
                    .absent(absent)
                    .attendancePercentage(percentage)
                    .build();
        });
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateLeader(User user) {

        if (user == null ||
                user.getRole() != UserRole.LEADER) {

            throw new AccessDeniedException(
                    "Only leaders can perform attendance operations"
            );
        }

        if (!user.isEnabled()) {

            throw new AccessDeniedException(
                    "Leader account is not approved"
            );
        }
    }


    private void validateLeaderOrAdmin(User user) {

        if (user == null ||
                (user.getRole() != UserRole.ADMIN &&
                        user.getRole() != UserRole.LEADER)) {

            throw new AccessDeniedException(
                    "Access denied"
            );
        }
    }


    private void validateMeetingOpen(
            Meeting meeting
    ) {

        if (meeting.isAttendanceClosed()) {

            throw new IllegalStateException(
                    "Attendance has already been closed"
            );
        }
    }


    private void validateMemberBelongsToLeader(
            Members member,
            User leader
    ) {

        if (member.getLeader() == null ||
                !member.getLeader()
                        .getId()
                        .equals(leader.getId())) {

            throw new AccessDeniedException(
                    "Member is not assigned to this leader"
            );
        }
    }


    private void validateAttendanceOwner(
            Attendance attendance,
            User user
    ) {

        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() != UserRole.LEADER) {

            throw new AccessDeniedException(
                    "Access denied"
            );
        }

        if (attendance.getLeader() == null ||
                !attendance.getLeader()
                        .getId()
                        .equals(user.getId())) {

            throw new AccessDeniedException(
                    "You cannot access this attendance record"
            );
        }
    }


    private void validateAttendanceStatus(
            AttendanceRequest request
    ) {

        if (request.getStatus() == null) {

            throw new IllegalArgumentException(
                    "Attendance status is required"
            );
        }

        if (request.getStatus() ==
                AttendanceStatus.ABSENT) {

            if (request.getAbsenceReason() == null ||
                    request.getAbsenceReason().isBlank()) {

                throw new IllegalArgumentException(
                        "Absence reason is required when member is absent"
                );
            }

            if (request.getAbsenceReason().length() > 500) {

                throw new IllegalArgumentException(
                        "Absence reason cannot exceed 500 characters"
                );
            }
        }
    }


    private void validateAttendanceDoesNotExist(
            String meetingId,
            String memberId
    ) {

        if (attendanceRepository
                .existsByMeetingIdAndMemberId(
                        meetingId,
                        memberId
                )) {

            throw new IllegalStateException(
                    "Attendance already marked for this member"
            );
        }
    }


    private Meeting getMeeting(
            String meetingId
    ) {

        return meetingRepository
                .findById(meetingId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                MEETING_NOT_FOUND + meetingId
                        )
                );
    }


    private Members getMember(
            String memberId
    ) {

        return memberRepository
                .findById(memberId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                MEMBER_NOT_FOUND + memberId
                        )
                );
    }


    private double calculatePercentage(
            long present,
            long total
    ) {

        if (total == 0) {
            return 0.0;
        }

        return Math.round(
                ((double) present / total) * 10000
        ) / 100.0;
    }


    private String buildFullName(
            String firstName,
            String lastName
    ) {

        if (lastName == null ||
                lastName.isBlank()) {

            return firstName;
        }

        return firstName + " " + lastName;
    }


    private void validateMonth(int month) {

        if (month < 1 || month > 12) {

            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }
    }


    private void validateYear(int year) {

        if (year < 2000 || year > 2100) {

            throw new IllegalArgumentException(
                    "Invalid year"
            );
        }
    }
}