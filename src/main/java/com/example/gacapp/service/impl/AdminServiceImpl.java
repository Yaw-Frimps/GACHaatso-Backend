package com.example.gacapp.service.impl;

import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.dto.response.UserResponse;
import com.example.gacapp.exception.ApprovalRejectionException;
import com.example.gacapp.exception.MemberNotFoundException;
import com.example.gacapp.exception.UserNotFoundException;
import com.example.gacapp.model.ApprovalStatus;
import com.example.gacapp.model.Members;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.MemberRepository;
import com.example.gacapp.repository.UserRepository;
import com.example.gacapp.service.AdminService;
import com.example.gacapp.util.MemberMapper;
import com.example.gacapp.util.UserMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final MemberRepository membersRepository;
    private final MemberMapper memberMapper;
    private final UserMapper userMapper;
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
    @Transactional
    @CacheEvict(
            value = {"pendingApprovals", "approvedUsers"},
            allEntries = true
    )
    public void deleteUser(String userId) {


        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found"
                                )
                        );



        if(user.getRole() != UserRole.LEADER &&
                user.getRole() != UserRole.PASTOR){


            throw new ApprovalRejectionException(
                    "Only Leader and Pastor accounts can be deleted"
            );

        }



    /*
       Remove leader assignment first
    */
        if(user.getRole() == UserRole.LEADER){

            membersRepository.removeLeaderFromMembers(
                    user.getId()
            );

        }



        user.setDeleted(true);
        user.setEnabled(false);
        user.setApprovalStatus(
                ApprovalStatus.REJECTED
        );


        userRepository.save(user);

    }

    @Override
    @Transactional
    public MembersResponse assignMemberToLeader(String memberId, String leaderId) {

        Members member = membersRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new UserNotFoundException("Leader not found"));

        if (leader.getRole() != UserRole.LEADER) {
            throw new IllegalArgumentException("Selected user is not a leader.");
        }

        if (leader.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Leader has not been approved.");
        }

        if (!leader.isEnabled()) {
            throw new IllegalArgumentException("Leader account is disabled.");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User currentAdmin = userRepository.findByEmail(
                authentication.getName()
        ).orElseThrow(() ->
                new UserNotFoundException("Admin not found")
        );

        member.setLeader(leader);
        member.setAssignedBy(currentAdmin);
        member.setAssignedAt(LocalDateTime.now(clock));

        membersRepository.save(member);
        return memberMapper.toDTO(member);
    }

    @Override
    public Page<MembersResponse> getLeaderMembers(
            String leaderId,
            Pageable pageable
    ) {


        return membersRepository
                .findByLeaderId(
                        leaderId,
                        pageable
                )
                .map(memberMapper::toDTO);

    }

    @Override
    public Page<MembersResponse> getUnassignedMembers(
            Pageable pageable
    ) {

        return membersRepository
                .findByLeaderIsNull(pageable)
                .map(memberMapper::toDTO);

    }

    @Override
    public Page<UserResponse> getAvailableLeaders(
            Pageable pageable
    ) {

        return userRepository
                .findByRoleAndApprovalStatusAndEnabledTrue(
                        UserRole.LEADER,
                        ApprovalStatus.APPROVED,
                        pageable
                )
                .map(userMapper::toDTO);

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