package com.foodmate.backend.service;

import com.foodmate.backend.dto.CommentDto;
import com.foodmate.backend.dto.GroupDto;
import com.foodmate.backend.dto.ReplyDto;
import org.springframework.security.core.Authentication;

public interface GroupService {

    String addGroup(Authentication authentication, GroupDto.Request request);

    GroupDto.DetailResponse getGroupDetail(Long groupId);

    String updateGroup(Long groupId, Authentication authentication, GroupDto.Request request);

    String deleteGroup(Long groupId, Authentication authentication);

    String enrollInGroup(Long groupId, Authentication authentication);

    String addComment(Long groupId, Authentication authentication, CommentDto.Request request);

    String addReply(Long groupId, Long commentId, Authentication authentication, ReplyDto.Request request);

    String updateComment(Long groupId, Long commentId, Authentication authentication, CommentDto.Request request);

    String updateReply(Long groupId, Long commentId, Long replyId, Authentication authentication, ReplyDto.Request request);

}
