package com.foodmate.backend.repository;

import com.foodmate.backend.entity.ChatMember;
import com.foodmate.backend.entity.ChatRoom;
import com.foodmate.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByMember(Member member);

    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    // 해당 채팅방에서 해당 멤버 삭제
    void deleteByMemberAndChatRoom(Member member, ChatRoom chatRoom);

}
