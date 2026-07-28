package com.example.gacapp.service;

import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    ApprovalStatusResponse approveUser (String userId);
    ApprovalStatusResponse rejectUser (String userId);

    Page<ApprovalStatusResponse> getPendingUsers(Pageable pageable);

    Page<ApprovalStatusResponse> getApprovedUsers(Pageable pageable);

    void deleteUser(String userId);

    MembersResponse assignMemberToLeader(String memberId, String leaderId);

    Page<MembersResponse> getLeaderMembers(
            String leaderId,
            Pageable pageable
    );


    Page<MembersResponse> getUnassignedMembers(
            Pageable pageable
    );


    Page<UserResponse> getAvailableLeaders(
            Pageable pageable
    );
}
