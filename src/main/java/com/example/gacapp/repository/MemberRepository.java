package com.example.gacapp.repository;

import com.example.gacapp.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Members, String> {

    @EntityGraph(attributePaths = {"leader"})
    Page<Members> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"leader"})
    Page<Members> findByLeaderId(String leaderId, Pageable pageable);

    @EntityGraph(attributePaths = {"leader"})
    Optional<Members> findWithLeaderById(String id);


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

    @Modifying
    @Transactional
    @Query("""
    UPDATE Members m
    SET m.leader = null
    WHERE m.leader.id = :leaderId
    """)
    void removeLeaderFromMembers(
            @Param("leaderId") String leaderId
    );
}
