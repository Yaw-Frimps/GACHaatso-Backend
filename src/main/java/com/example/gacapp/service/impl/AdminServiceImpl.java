package com.example.gacapp.service.impl;

import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.exception.ApprovalRejectionException;
import com.example.gacapp.exception.UserNotFoundException;
import com.example.gacapp.model.ApprovalStatus;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.UserRepository;
import com.example.gacapp.service.AdminService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @CacheEvict(value = "pendingLeaders", allEntries = true)
    public ApprovalStatusResponse approveLeader(String userId) {
        log.info("Approving leader with ID: {}", userId);

        User user = getLeaderOrThrow(userId);

        if (user.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new ApprovalRejectionException("Leader has already been approved");
        }

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setEnabled(true);
        user.setApprovedAt(LocalDateTime.now());

        userRepository.save(user);

        try {
            emailService.sendLeaderApprovalEmail(user.getEmail(), user.getFirstName());
        } catch (MessagingException e) {
            log.error("Failed to send approval email to {}: {}", user.getEmail(), e.getMessage(), e);
            // optionally continue silently; approval is already done
        }

        return mapToResponse(user);
    }

    @Override
    @CacheEvict(value = "pendingLeaders", allEntries = true)
    public ApprovalStatusResponse rejectLeader(String userId) {
        log.info("Rejecting leader with ID: {}", userId);

        User user = getLeaderOrThrow(userId);

        if (user.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new ApprovalRejectionException("Leader has already been rejected");
        }

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setEnabled(false);

        userRepository.save(user);

        try {
            emailService.sendLeaderRejectionEmail(user.getEmail(), user.getFirstName());
        } catch (MessagingException e) {
            log.error("Failed to send rejection email to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return mapToResponse(user);
    }

    @Override
    @Cacheable(value = "pendingLeaders", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getPendingLeaders(Pageable pageable) {


        return userRepository
                .findByRoleAndApprovalStatus(
                        UserRole.LEADER,
                        ApprovalStatus.PENDING,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Cacheable(value = "approvedLeaders", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getApprovedLeaders(Pageable pageable) {


        return userRepository
                .findByRoleAndApprovalStatus(
                        UserRole.LEADER,
                        ApprovalStatus.APPROVED,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = {"pendingLeaders", "approvedLeaders"}, allEntries = true)
    public void deleteLeader(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() != UserRole.LEADER) {
            throw new ApprovalRejectionException("Only Leader accounts can be deleted");
        }

        user.setDeleted(true);
        user.setEnabled(false);
        user.setApprovalStatus(ApprovalStatus.REJECTED);

        userRepository.save(user);
    }


    private User getLeaderOrThrow(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != UserRole.LEADER) {
            throw new ApprovalRejectionException("Only leaders require approval");
        }

        return user;
    }

    private ApprovalStatusResponse mapToResponse(User user) {
        return ApprovalStatusResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .approvalStatus(user.getApprovalStatus().name())
                .approvedAt(user.getApprovedAt())
                .build();
    }
}