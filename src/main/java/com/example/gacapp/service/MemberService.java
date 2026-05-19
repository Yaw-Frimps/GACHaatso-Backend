package com.example.gacapp.service;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.response.MembersResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService {

    MembersResponse createMember(MembersRequest request, MultipartFile file);

    MembersResponse getMemberById(String id);

    Page<MembersResponse> getAllMembers(Pageable pageable);

    MembersResponse updateMember(String id, MembersRequest request, MultipartFile file);

    void deleteMember(String id);
}
