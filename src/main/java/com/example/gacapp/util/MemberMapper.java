package com.example.gacapp.util;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.model.Members;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MemberMapper {


    private final LeaderMapper leaderMapper;



    public Members toEntity(MembersRequest request) {

        if(request == null)
            return null;


        return Members.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .maritalStatus(request.getMaritalStatus())
                .residenceAddress(request.getResidenceAddress())
                .occupation(request.getOccupation())
                .emergencyNumber(request.getEmergencyNumber())
                .build();
    }




    public MembersResponse toDTO(Members member){

        if(member == null)
            return null;



        return MembersResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .age(member.getAge())
                .gender(member.getGender())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .maritalStatus(member.getMaritalStatus())
                .residenceAddress(member.getResidenceAddress())
                .occupation(member.getOccupation())

                // SAFE LEADER MAPPING
                .leader(
                        leaderMapper.toDTO(member.getLeader())
                )

                .emergencyNumber(member.getEmergencyNumber())
                .profileImageUrl(member.getProfileImageUrl())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}