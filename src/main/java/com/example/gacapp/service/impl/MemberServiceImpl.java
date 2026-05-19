package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.exception.ResourceNotFoundException;
import com.example.gacapp.model.Members;
import com.example.gacapp.repository.MemberRepository;
import com.example.gacapp.service.MemberService;
import com.example.gacapp.util.FileStorageServiceImpl;
import com.example.gacapp.util.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final FileStorageServiceImpl fileStorageService;

    private static final String MEMBER_NOT_FOUND = "Member not found: ";
    private static final String MEMBERS = "members ";

    @Override
    public MembersResponse createMember(MembersRequest request, MultipartFile file) {

        Members member = MemberMapper.toEntity(request);

        // Save first to generate ID (important for folder structure)
        Members savedMember = repository.save(member);

        if (file != null && !file.isEmpty()) {

            String filename = fileStorageService.storeFile(
                    file,
                    MEMBERS,
                    savedMember.getId(),
                    null
            );

            String imageUrl = fileStorageService.getFileUrl(
                    MEMBERS,
                    savedMember.getId(),
                    filename
            );

            savedMember.setImageUrl(imageUrl);
        }

        return MemberMapper.toDTO(repository.save(savedMember));
    }

    @Override
    public MembersResponse getMemberById(String id) {

        Members member = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND + id));

        return MemberMapper.toDTO(member);
    }

    @Override
    public Page<MembersResponse> getAllMembers(Pageable pageable) {

        return repository.findAll(pageable)
                .map(MemberMapper::toDTO);
    }

    @Override
    public MembersResponse updateMember(String id, MembersRequest request, MultipartFile file) {

        Members existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND + id));

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setPhoneNumber(request.getPhoneNumber());
        existing.setEmail(request.getEmail());
        existing.setOccupation(request.getOccupation());
        existing.setResidenceAddress(request.getResidenceAddress());
        existing.setGender(request.getGender());
        existing.setMaritalStatus(request.getMaritalStatus());
        existing.setEmergencyNumber(request.getEmergencyNumber());
        existing.setDateOfBirth(request.getDateOfBirth());

        if (file != null && !file.isEmpty()) {

            String filename = fileStorageService.storeFile(
                    file,
                    MEMBERS,
                    existing.getId(),
                    extractFileName(existing.getImageUrl())
            );

            String imageUrl = fileStorageService.getFileUrl(
                    MEMBERS,
                    existing.getId(),
                    filename
            );

            existing.setImageUrl(imageUrl);
        }

        return MemberMapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteMember(String id) {

        Members member = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MEMBER_NOT_FOUND + id));

        repository.delete(member);
    }

    /**
     * Extract filename from stored URL for replacement cleanup
     */
    private String extractFileName(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;

        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}