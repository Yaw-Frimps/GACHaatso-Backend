package com.example.gacapp.repository;

import com.example.gacapp.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Members, String> {

    Page<Members> findByLeaderId(String leaderId, Pageable pageable);


    Page<Members> findByLeaderIsNull(
            Pageable pageable
    );


    boolean existsByLeaderId(String leaderId);

    @Query("""
        SELECT m FROM Members m
        WHERE MONTH(m.dateOfBirth)=:month
        ORDER BY DAY(m.dateOfBirth)
    """)
    List<Members> findBirthdaysByMonth(
            int month
    );



    @Query("""
        SELECT m FROM Members m
        WHERE MONTH(m.dateOfBirth)=MONTH(CURRENT_DATE)
        AND DAY(m.dateOfBirth)=DAY(CURRENT_DATE)
    """)
    List<Members> findTodaysBirthdays();
}
