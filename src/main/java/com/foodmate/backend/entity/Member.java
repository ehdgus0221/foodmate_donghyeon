package com.foodmate.backend.entity;

import com.foodmate.backend.dto.MemberDto;
import com.foodmate.backend.enums.MemberLoginType;
import com.foodmate.backend.enums.MemberRole;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String password;

    private String image;

    private long likes;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    private MemberLoginType memberLoginType;

    private LocalDateTime registeredDate;

    private LocalDateTime updatedDate;

    private LocalDateTime isDeleted;

    private LocalDateTime banDate;

    private String emailAuthKey;

    private LocalDateTime emailAuthDate;

    private Boolean isEmailAuth;

    private String refreshToken;

    public static Member MemberDtoToMember(MemberDto.CreateMemberRequest request, String encPassword, String uuid) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encPassword)
                .memberLoginType(MemberLoginType.GENERAL)
                .memberRole(MemberRole.USER)
                .registeredDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .emailAuthKey(uuid)
                .isEmailAuth(false)
                .build();
    }
}