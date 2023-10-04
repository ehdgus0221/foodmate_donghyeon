package com.foodmate.backend.entity;

import com.foodmate.backend.enums.MemberLoginType;
import com.foodmate.backend.enums.MemberRole;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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


    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

}