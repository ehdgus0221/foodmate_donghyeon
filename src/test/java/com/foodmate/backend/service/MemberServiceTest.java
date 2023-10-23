package com.foodmate.backend.service;

import com.foodmate.backend.dto.MemberDto;
import com.foodmate.backend.entity.Food;
import com.foodmate.backend.entity.Member;
import com.foodmate.backend.entity.Preference;
import com.foodmate.backend.enums.Error;
import com.foodmate.backend.exception.FoodException;
import com.foodmate.backend.exception.MemberException;
import com.foodmate.backend.repository.FoodRepository;
import com.foodmate.backend.repository.LikesRepository;
import com.foodmate.backend.repository.MemberRepository;
import com.foodmate.backend.repository.PreferenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private LikesRepository likesRepository;

    @InjectMocks
    private MemberService memberService;

    private MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "file",
            "example.txt",
            "text/plain",
            "This is a test file".getBytes()
        );
    public static final Long memberId1 = 1L;
    public static final Long foodId1 = 1L;

    @Test
    @DisplayName("내 정보 가져오기 성공 - 기본 이미지/선호음식 없음")
    void success_getDefaultImageMemberInfo() {
        //given
        Authentication mockAuthentication = createAuthentication();
        Member mockMember = createMockDefaultImageMember(memberId1);

        List<Preference> mockPreference = new ArrayList<>();

        Long mockLikesCount = 32L;

        given(memberRepository.findByEmail(mockAuthentication.getName())).willReturn(Optional.of(mockMember));
        given(preferenceRepository.findAllByMember(mockMember)).willReturn(mockPreference);
        given(likesRepository.countAllByLiked(mockMember)).willReturn(mockLikesCount);

        //when
        MemberDto.Response response = memberService.getMemberInfo(mockAuthentication);

        //then
        assertAll(
                () -> assertEquals(mockMember.getId(), response.getMemberId()),
                () -> assertEquals(mockMember.getEmail(), response.getEmail()),
                () -> assertEquals(mockMember.getImage(), response.getImage()),
                () -> assertEquals(mockLikesCount, response.getLikes()),
                () -> assertEquals(mockMember.getNickname(), response.getNickname()),
                () -> assertNotNull(response.getFood()),
                () -> assertTrue(response.getFood().isEmpty())
        );

    }

    @Test
    @DisplayName("내 정보 가져오기 성공/선호음식 없음")
    void success_getMemberInfo() {
        //given
        Authentication mockAuthentication = createAuthentication();
        Member mockMember = createMockMember(memberId1);
        List<Preference> mockPreference = new ArrayList<>();

        Long mockLikesCount = 32L;

        given(memberRepository.findByEmail(mockAuthentication.getName())).willReturn(Optional.of(mockMember));
        given(preferenceRepository.findAllByMember(mockMember)).willReturn(mockPreference);
        given(likesRepository.countAllByLiked(mockMember)).willReturn(mockLikesCount);
        //when
        MemberDto.Response response = memberService.getMemberInfo(mockAuthentication);

        //then
        assertAll(
                () -> assertEquals(mockMember.getId(), response.getMemberId()),
                () -> assertEquals(mockMember.getEmail(), response.getEmail()),
                () -> assertEquals(mockMember.getImage(), response.getImage()),
                () -> assertEquals(mockLikesCount, response.getLikes()),
                () -> assertEquals(mockMember.getNickname(), response.getNickname()),
                () -> assertNotNull(response.getFood()),
                () -> assertTrue(response.getFood().isEmpty())
        );

    }

    @Test
    @DisplayName("내 정보 가져오기 성공 - 기본 이미지/선호음식 있음")
    void success_getMemberInfoWithPreference() {
        // given
        Authentication mockAuthentication = createAuthentication();
        Member mockMember = createMockMember(memberId1);

        Food mockFood = createFood(1L); // 유효한 음식 ID로 생성

        List<Preference> mockPreference = new ArrayList<>();
        mockPreference.add(new Preference(1L, mockMember, mockFood)); // 선호음식 추가
        List<String> mockFoods = new ArrayList<>();
        mockFoods.add(mockFood.getType()); // 선호음식 추가

        Long mockLikesCount = 32L;

        given(memberRepository.findByEmail(mockAuthentication.getName())).willReturn(Optional.of(mockMember));
        given(preferenceRepository.findAllByMember(mockMember)).willReturn(mockPreference);
        given(likesRepository.countAllByLiked(mockMember)).willReturn(mockLikesCount);
        given(foodRepository.findById(mockFood.getId())).willReturn(Optional.of(mockFood)); // 음식을 찾을 수 있도록 설정

        // when
        MemberDto.Response response = memberService.getMemberInfo(mockAuthentication);

        // then
        assertAll(
                () -> assertEquals(mockMember.getId(), response.getMemberId()),
                () -> assertEquals(mockMember.getEmail(), response.getEmail()),
                () -> assertEquals(mockMember.getImage(), response.getImage()),
                () -> assertEquals(mockLikesCount, response.getLikes()),
                () -> assertEquals(mockMember.getNickname(), response.getNickname()),
                () -> assertEquals(mockFoods, response.getFood())
        );
    }


    @Test
    @DisplayName("내 정보 가져오기 실패 - 유저 없음")
    void fail_getMemberInfoNotFoundMember() {
        // given
        Authentication mockAuthentication = createAuthentication();
        Member mockMember = null; // 사용자를 찾지 못했음을 나타내기 위해 null로 설정

        given(memberRepository.findByEmail(mockAuthentication.getName())).willReturn(Optional.ofNullable(mockMember)); // Optional.ofNullable 사용

        // when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.getMemberInfo(mockAuthentication));

        // then
        assertEquals(Error.USER_NOT_FOUND, exception.getError());
    }


    @Test
    @DisplayName("내 정보 가져오기 실패 - 해당 음식 없음")
    void fail_getMemberInfoNotFoundFood() {
        // given
        Authentication mockAuthentication = createAuthentication();
        Member mockMember = createMockMember(memberId1);

        List<Preference> mockPreferences = List.of(new Preference(1L, mockMember, new Food(20L, "dasdas", "dasdsa")));

        given(memberRepository.findByEmail(mockAuthentication.getName())).willReturn(Optional.of(mockMember));
        given(preferenceRepository.findAllByMember(mockMember)).willReturn(mockPreferences);


        // when
        FoodException exception = assertThrows(FoodException.class, () -> memberService.getMemberInfo(mockAuthentication));

        // then
        assertEquals(Error.FOOD_NOT_FOUND, exception.getError());
    }


    @Test
    @DisplayName("이메일 유효성 검사/이메일 사용 가능")
    void checkDuplicateEmailUse() {
        // given
        String email = "test@tes.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.ofNullable(null));

        // when
        boolean result = memberService.checkDuplicateEmail(email);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 유효성 검사/이메일 사용 불가")
    void checkDuplicateEmailUnUse() {
        // given
        String email = "dlaehdgus23@naver.com";
        Member mockMember = createMockMember(memberId1);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(mockMember));

        // when
        boolean result = memberService.checkDuplicateEmail(email);

        // then
        assertFalse(result);
    }


    @Test
    @DisplayName("이메일 유효성 검사/닉네임 사용 가능")
    void checkDuplicateNicknameUse() {
        // given
        String nickname = "testNickname";
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.ofNullable(null));

        // when
        boolean result = memberService.checkDuplicateNickname(nickname);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 유효성 검사/닉네임 사용 불가")
    void checkDuplicateNicknameUnUse() {
        // given
        String nickname = "동현";
        Member mockMember = createMockMember(memberId1);
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(mockMember));

        // when
        boolean result = memberService.checkDuplicateNickname(nickname);
        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("닉네임으로 유저 정보 가져오기")
    void success_getMemberInfoByNickname() {
        // given
        String nickname = "동현";
        Member mockMember = createMockMember(memberId1);

        Long mockLikesCount = 32L;

        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(mockMember));
        given(likesRepository.countAllByLiked(mockMember)).willReturn(mockLikesCount);

        // when
        MemberDto.Response response = memberService.getMemberInfoByNickname(nickname);

        // then
        assertAll(
                () -> assertEquals(mockMember.getId(), response.getMemberId()),
                () -> assertEquals(mockMember.getEmail(), response.getEmail()),
                () -> assertEquals(mockMember.getImage(), response.getImage()),
                () -> assertEquals(mockLikesCount, response.getLikes()),
                () -> assertEquals(mockMember.getNickname(), response.getNickname())
        );

    }

    @Test
    @DisplayName("닉네임으로 유저 정보 가져오기 실패 - 없는 유저")
    void fail_getMemberInfoByNicknameNotFound() {
        // given
        String nickname = "testet11asdsadsa";
        Member mockMember = createMockMember(memberId1);
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.empty());

        // when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.getMemberInfoByNickname(nickname));

        // then
        assertEquals(Error.USER_NOT_FOUND, exception.getError());

    }

    @Test
    @DisplayName("닉네임으로 유저 정보 가져오기 실패 - 회원 탈퇴 유저")
    void fail_getMemberInfoByNicknameDeleteUser() {
        // given
        String nickname = "deleteUser";
        Member mockMember = createMockDeleteMember(memberId1);
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(mockMember));

        // when
        MemberException exception = assertThrows(MemberException.class, () -> memberService.getMemberInfoByNickname(nickname));

        // then
        assertEquals(Error.DELETED_USER, exception.getError());

    }


    private Authentication createAuthentication() {

        String email = "dlaehdgus23@naver.com";
        String password = "ehdgus1234";

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(email, password,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        return mockAuthentication;
    }

    private Member createMockDefaultImageMember(Long memberId) {
        return Member.builder()
                .id(memberId)
                .email("dlaehdgus23@naver.com")
                .nickname("동현")
                .image(null)
                .password("ehdgus1234")
                .build();
    }

    private Member createMockMember(Long memberId) {
        return Member.builder()
                .id(memberId)
                .email("dlaehdgus23@naver.com")
                .nickname("동현")
                .image("asdfsadfsda")
                .password("ehdgus1234")
                .build();
    }

    private Member createMockDeleteMember(Long memberId) {
        return Member.builder()
                .id(memberId)
                .email("deleteUser@naver.com")
                .nickname("deleteUser")
                .image("asdfsadfsda")
                .password("deleteUser")
                .isDeleted(LocalDateTime.now())
                .build();
    }

    private Food createFood(Long foodId) {
        return Food.builder()
                .id(foodId)
                .image("dasdsa")
                .type("족발보쌈")
                .build();
    }


}