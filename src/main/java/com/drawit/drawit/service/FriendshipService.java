package com.drawit.drawit.service;

import com.drawit.drawit.dto.FriendDto;
import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.dto.response.ResponseFriendshipDto;
import com.drawit.drawit.dto.response.ResponseUserDto;
import com.drawit.drawit.entity.Friendship;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.FriendshipRepository;
import com.drawit.drawit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final FriendshipRepository friendshipRepository;

    /**
     * 친구 요청을 추가
     */
    @Transactional
    public FriendDto addFriend(Long senderId, String recipientNickname) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with ID: " + senderId));
        User recipient = userRepository.findByNickname(recipientNickname)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + recipientNickname));

        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(recipient);
        friendship.setStatus(Friendship.AcceptStatus.WAIT);
        friendship.setCreatedAt(LocalDateTime.now());

        return this.friendshipToFriendDto(friendshipRepository.save(friendship));
    }
    /**
     * 친구 요청에 대한 응답 처리
     */
    @Transactional
    public void respondToFriendRequest(Long requestId, Friendship.AcceptStatus status) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship request not found with ID: " + requestId));

        friendship.setStatus(status);
        friendshipRepository.save(friendship);
    }

    /**
     * 대기 중인 친구 요청 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseFriendshipDto> getPendingRequests(Long userId) {
        List<ResponseFriendshipDto> friendDtoList = new ArrayList<>();
        for (Friendship friendship : friendshipRepository.findAllByFriendIdAndStatus(userId, Friendship.AcceptStatus.WAIT)) {
            friendDtoList.add(ResponseFriendshipDto.builder()
                            .id(friendship.getId())
                            .status(friendship.getStatus())
                            .receiverId(friendship.getFriend().getId())
                            .senderId(friendship.getUser().getId())
                            .senderNickname(friendship.getUser().getNickname())
                    .build());
        }
        return friendDtoList;
    }


    public ResponseUserDto userToResponseUserDto(User user) {
        return objectMapper.convertValue(user, ResponseUserDto.class);
    }
    public FriendDto friendshipToFriendDto(Friendship friendship) {
        return objectMapper.convertValue(friendship, FriendDto.class);
    }
}
