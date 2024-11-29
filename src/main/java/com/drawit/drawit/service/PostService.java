package com.drawit.drawit.service;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.dto.PostDto;
import com.drawit.drawit.dto.request.RequestPostDto;
import com.drawit.drawit.entity.GameRound;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.Post;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.CommentRepository;
import com.drawit.drawit.repository.GameRoundRepository;
import com.drawit.drawit.repository.PostRepository;
import com.drawit.drawit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameRoundRepository gameRoundRepository;
    private final ObjectMapper objectMapper;
    @Transactional
    public PostDto writeNewPost(RequestPostDto requestPostDto) {
        // 1. 유저 확인
        User user = userRepository.findById(requestPostDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + requestPostDto.getUserId()));

        // 2. 게임 라운드 확인
        GameRound gameRound = gameRoundRepository.findById(requestPostDto.getGameRoundId())
                .orElseThrow(() -> new IllegalArgumentException("GameRound not found with ID: " + requestPostDto.getGameRoundId()));

        // 3. 게시글 생성
        Post post = Post.builder()
                .gameRound(gameRound)
                .user(user)
                .imageUrl(requestPostDto.getImageUrl())
                .visibility(requestPostDto.getVisibility())
                .createdAt(LocalDateTime.now())
                .build();

        // 4. 저장 및 반환
        return postToPostDto(postRepository.save(post));
    }

    public PostDto postToPostDto(Post post) {
        return objectMapper.convertValue(post, PostDto.class);
    }

}
