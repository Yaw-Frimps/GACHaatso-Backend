package com.example.gacapp.util;


import com.example.gacapp.dto.response.AttendanceResponse;
import com.example.gacapp.model.Attendance;
import org.springframework.stereotype.Component;


@Component
public class AttendanceMapper {


    public AttendanceResponse toDTO(
            Attendance attendance
    ) {

        if (attendance == null) {
            return null;
        }


        return AttendanceResponse.builder()

                .id(attendance.getId())


                .meetingId(
                        attendance.getMeeting() != null
                                ? attendance.getMeeting().getId()
                                : null
                )


                .memberId(
                        attendance.getMember() != null
                                ? attendance.getMember().getId()
                                : null
                )


                .memberName(
                        attendance.getMember() != null
                                ?
                                attendance.getMember()
                                        .getFirstName()
                                + " "
                                +
                                        attendance.getMember()
                                                .getLastName()

                                :
                                null
                )


                .leaderId(
                        attendance.getLeader() != null
                                ?
                                attendance.getLeader().getId()
                                :
                                null
                )


                .leaderName(
                        attendance.getLeader() != null
                                ?
                                attendance.getLeader()
                                        .getFirstName()
                                + " "
                                +
                                        attendance.getLeader()
                                                .getLastName()

                                :
                                null
                )


                .status(
                        attendance.getStatus()
                )


                .absenceReason(
                        attendance.getAbsenceReason()
                )


                .markedAt(
                        attendance.getMarkedAt()
                )


                .createdAt(
                        attendance.getCreatedAt()
                )


                .updatedAt(
                        attendance.getUpdatedAt()
                )


                .build();

    }

}