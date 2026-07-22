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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Clock clock;

    @Override
    @CacheEvict(value = {"pendingApprovals", "approvedUsers"}, allEntries = true)
    public ApprovalStatusResponse approveUser(String userId) {
        log.info("Approving leader with ID: {}", userId);

        User user = getUserRequiringApprovalOrThrow(userId);

        if (user.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new ApprovalRejectionException(user.getRole() + " has already been approved");
        }

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setEnabled(true);
        user.setApprovedAt(LocalDateTime.now(clock));

        userRepository.save(user);

        try {
            emailService.sendUserApprovalEmail(user.getEmail(), user.getFirstName(), user.getRole().name());
        } catch (MessagingException e) {
            log.error("Failed to send approval email to {}: {}", user.getEmail(), e.getMessage(), e);
            // optionally continue silently; approval is already done
        }

        return mapToResponse(user);
    }

    @Override
    @CacheEvict(value = {"pendingApprovals", "approvedUsers"}, allEntries = true)
    public ApprovalStatusResponse rejectUser(String userId) {
        log.info("Rejecting leader with ID: {}", userId);

        User user = getUserRequiringApprovalOrThrow(userId);

        if (user.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new ApprovalRejectionException(user.getRole() + " has already been rejected");
        }

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setEnabled(false);

        userRepository.save(user);

        try {
            emailService.sendUserRejectionEmail(user.getEmail(), user.getFirstName(), user.getRole().name());
        } catch (MessagingException e) {
            log.error("Failed to send rejection email to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return mapToResponse(user);
    }

    @Override
    @Cacheable(value = "pendingApprovals", key = "#pageable.pageNumber + '-' + #pageable.pageSize+ '-' + #pageable.sort")
    public Page<ApprovalStatusResponse> getPendingUsers(Pageable pageable) {


        return userRepository
                .findByRoleInAndApprovalStatus(
                        List.of(UserRole.LEADER, UserRole.PASTOR),
                        ApprovalStatus.PENDING,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Cacheable(value = "approvedUsers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getApprovedUsers(Pageable pageable) {


        return userRepository.findByRoleInAndApprovalStatus(
                List.of(UserRole.LEADER, UserRole.PASTOR),
                ApprovalStatus.APPROVED,
                pageable
        ).map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = {"pendingApprovals", "approvedUsers"}, allEntries = true)
    public void deleteUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() != UserRole.LEADER &&
                user.getRole() != UserRole.PASTOR) {

            throw new ApprovalRejectionException(
                    "Only Leader and Pastor accounts can be deleted");
        }

        user.setDeleted(true);
        user.setEnabled(false);
        user.setApprovalStatus(ApprovalStatus.REJECTED);

        userRepository.save(user);
    }


    private User getUserRequiringApprovalOrThrow(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() != UserRole.LEADER &&
                user.getRole() != UserRole.PASTOR) {

            throw new ApprovalRejectionException(
                    "Only leaders and pastors require approval");
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