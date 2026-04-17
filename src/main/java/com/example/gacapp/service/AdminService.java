package com.example.gacapp.service;

import com.example.gacapp.dto.response.ApprovalStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    ApprovalStatusResponse approveLeader (String userId);
    ApprovalStatusResponse rejectLeader (String userId);

    Page<ApprovalStatusResponse> getPendingLeaders(Pageable pageable);

    void deleteLeader(String userId);
}
