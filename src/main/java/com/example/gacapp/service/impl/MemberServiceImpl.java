package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.request.UpdateMemberRequest;
import com.example.gacapp.dto.response.CloudinaryResponse;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.exception.ResourceNotFoundException;
import com.example.gacapp.model.ApprovalStatus;
import com.example.gacapp.model.Members;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.MemberRepository;
import com.example.gacapp.service.AuthService;
import com.example.gacapp.service.CloudinaryService;
import com.example.gacapp.service.MemberService;
import com.example.gacapp.model.ImageFolder;
import com.example.gacapp.util.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {


    private final MemberRepository repository;
    private final CloudinaryService cloudinaryService;
    private final MemberMapper memberMapper;
    private final AuthService authService;



    private static final String MEMBER_NOT_FOUND =
            "Member not found: ";



    // ================= CREATE =================


    @Override
    @CacheEvict(value = "members_all", allEntries = true)
    @Transactional
    public MembersResponse createMember(
            MembersRequest request,
            MultipartFile file
    ) {


        User user = authService.getLoggedInUser();

        validateLeaderAccess(user);


        log.info(
                "Creating new member with email: {}",
                request.getEmail()
        );


        Members member =
                memberMapper.toEntity(request);



        Members savedMember =
                repository.save(member);



        if(file != null && !file.isEmpty()) {


            CloudinaryResponse response =
                    cloudinaryService.upload(
                            file,
                            ImageFolder.MEMBERS,
                            savedMember.getId()
                    );



            savedMember.setProfileImageUrl(
                    response.getImageUrl()
            );


            savedMember.setProfileImagePublicId(
                    response.getPublicId()
            );


            repository.save(savedMember);

        }



        return memberMapper.toDTO(savedMember);

    }




    // ================= GET BY ID =================


    @Override
    @Cacheable(value = "members", key = "#id")
    public MembersResponse getMemberById(String id) {


        Members member =
                repository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MEMBER_NOT_FOUND + id
                                )
                        );



        User user =
                authService.getLoggedInUser();



        validateMemberAccess(
                user,
                member,
                "access"
        );


        return memberMapper.toDTO(member);

    }





    // ================= GET ALL =================


    @Override
    @Cacheable(
            value = "members_all",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<MembersResponse> getAllMembers(Pageable pageable) {


        User user =
                authService.getLoggedInUser();



        validateLeaderAccess(user);



        Page<Members> members;



        if(user.getRole() == UserRole.ADMIN){

            members =
                    repository.findAll(pageable);

        }else{

            members =
                    repository.findByLeaderId(
                            user.getId(),
                            pageable
                    );

        }



        return members.map(memberMapper::toDTO);

    }





    // ================= UPDATE =================


    @Override
    @CacheEvict(
            value = {"members_all", "members"},
            allEntries = true
    )
    @Transactional
    public MembersResponse updateMember(
            String id,
            UpdateMemberRequest request,
            MultipartFile file
    ) {


        Members existing =
                repository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MEMBER_NOT_FOUND + id
                                )
                        );



        User user =
                authService.getLoggedInUser();



        validateMemberAccess(
                user,
                existing,
                "update"
        );




        if(request.getFirstName() != null)
            existing.setFirstName(request.getFirstName());


        if(request.getLastName() != null)
            existing.setLastName(request.getLastName());


        if(request.getPhoneNumber() != null)
            existing.setPhoneNumber(request.getPhoneNumber());


        if(request.getEmail() != null)
            existing.setEmail(request.getEmail());


        if(request.getOccupation() != null)
            existing.setOccupation(request.getOccupation());


        if(request.getResidenceAddress() != null)
            existing.setResidenceAddress(request.getResidenceAddress());


        if(request.getGender() != null)
            existing.setGender(request.getGender());


        if(request.getMaritalStatus() != null)
            existing.setMaritalStatus(request.getMaritalStatus());


        if(request.getEmergencyNumber() != null)
            existing.setEmergencyNumber(request.getEmergencyNumber());


        if(request.getDateOfBirth() != null)
            existing.setDateOfBirth(request.getDateOfBirth());




        // IMAGE UPDATE

        if(file != null && !file.isEmpty()) {



            if(existing.getProfileImagePublicId() != null){

                cloudinaryService.delete(
                        existing.getProfileImagePublicId()
                );

            }




            CloudinaryResponse response =
                    cloudinaryService.upload(
                            file,
                            ImageFolder.MEMBERS,
                            existing.getId()
                    );



            existing.setProfileImageUrl(
                    response.getImageUrl()
            );


            existing.setProfileImagePublicId(
                    response.getPublicId()
            );

        }





        Members updated =
                repository.save(existing);



        return memberMapper.toDTO(updated);

    }






    // ================= DELETE =================


    @Override
    @CacheEvict(
            value = {"members_all", "members"},
            allEntries = true
    )
    @Transactional
    public void deleteMember(String id) {


        log.warn(
                "Deleting member with id={}",
                id
        );



        Members member =
                repository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MEMBER_NOT_FOUND + id
                                )
                        );



        User user =
                authService.getLoggedInUser();



        validateMemberAccess(
                user,
                member,
                "delete"
        );




        if(member.getProfileImagePublicId() != null){

            cloudinaryService.delete(
                    member.getProfileImagePublicId()
            );

        }




        repository.delete(member);



        log.info(
                "Member deleted successfully id={}",
                id
        );

    }






    // ================= SECURITY HELPERS =================



    private void validateLeaderAccess(User user) {


        if(user.getRole() == UserRole.LEADER &&
                user.getApprovalStatus() != ApprovalStatus.APPROVED){


            throw new AccessDeniedException(
                    "Leader not approved yet"
            );

        }

    }





    private void validateMemberAccess(
            User user,
            Members member,
            String action
    ) {


        validateLeaderAccess(user);



        if(user.getRole() == UserRole.ADMIN){

            return;

        }




        if(member == null ||
                member.getLeader() == null ||
                !member.getLeader()
                        .getId()
                        .equals(user.getId())){


            throw new AccessDeniedException(
                    "You cannot " + action + " this member"
            );

        }

    }

}