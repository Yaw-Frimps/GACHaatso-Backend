package com.example.gacapp.repository;

import com.example.gacapp.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Members, String> {

    Page<Members> findByLeaderId(String leaderId, Pageable pageable);
}
