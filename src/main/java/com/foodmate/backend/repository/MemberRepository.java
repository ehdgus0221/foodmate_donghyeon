package com.foodmate.backend.repository;

import com.foodmate.backend.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // RankingServiceImpl - 좋아요 랭킹
    List<Member> findTop10ByOrderByLikesDesc();

    // RankingServiceImpl - 모임왕 랭킹
    @Query("SELECT m.id, m.nickname, m.image, COUNT(fg.member) AS count " +
            "FROM Member m " +
            "JOIN FoodGroup fg ON m.id = fg.member.id " +
            "WHERE fg.groupDateTime < CURRENT_TIMESTAMP " +
            "AND fg.isDeleted IS NULL " +
            "GROUP BY m.id, m.nickname, m.image " +
            "ORDER BY COUNT(fg.member) DESC")
    List<Object[]> findTop10MemberWithCount(Pageable pageable);

}
