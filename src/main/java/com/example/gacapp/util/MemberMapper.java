package com.example.gacapp.util;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.model.Members;

public class MemberMapper {

    // Prevent instantiation
    private MemberMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Members toEntity(MembersRequest request) {
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

    public static MembersResponse toDTO(Members member) {
        return MembersResponse.builder()
                .id(member.getId()) // FIXED (was getId())
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
                .emergencyNumber(member.getEmergencyNumber())
                .imageUrl(member.getImageUrl())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}